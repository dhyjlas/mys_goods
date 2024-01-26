package com.dhyjlas.mys.controller;

import com.alibaba.fastjson.JSONObject;
import com.dhyjlas.mys.consts.MsgEnum;
import com.dhyjlas.mys.entity.UserInfo;
import com.dhyjlas.mys.entity.body.MobileCaptcha;
import com.dhyjlas.mys.entity.body.UserBody;
import com.dhyjlas.mys.exception.BusinessException;
import com.dhyjlas.mys.service.FileService;
import com.dhyjlas.mys.service.MysService;
import com.dhyjlas.mys.util.JsonResult;
import com.dhyjlas.mys.util.LocalCache;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * <p>File: UserController.java </p>
 * <p>Title: </p>
 * <p>Description: </p>
 *
 * @author yjl.yu/dhyjlas@163.com
 * @version 1.0
 */
@Slf4j
@RestController
public class UserController {
    private final FileService fileService;

    private final MysService mysService;

    public UserController(FileService fileService, MysService mysService) {
        this.fileService = fileService;
        this.mysService = mysService;
    }

    /**
     * 获取用户列表
     *
     * @return JsonResult
     */
    @RequestMapping("/user/list")
    public JsonResult list() {
        List<UserInfo> userInfoList = fileService.listUserInfo();
        return JsonResult.success().data(userInfoList);
    }

    /**
     * 获取一个用户
     *
     * @return JsonResult
     */
    @RequestMapping("/user")
    public JsonResult list(@RequestBody UserBody userBody) {
        if (StringUtils.isBlank(userBody.getUid())) {
            throw new BusinessException(MsgEnum.INVALID_UID);
        }
        UserInfo userInfo = fileService.findUserInfo(userBody.getUid());
        return JsonResult.success().data(userInfo);
    }

    /**
     * 新增用户
     *
     * @param mobileCaptcha 手机和验证码
     * @return JsonResult
     */
    @RequestMapping("/user/add/1")
    public JsonResult addUserInfoByMobile(@RequestBody MobileCaptcha mobileCaptcha) {
        if (StringUtils.isBlank(mobileCaptcha.getMobile())) {
            throw new BusinessException(MsgEnum.INVALID_MOBILE);
        }
        if (StringUtils.isBlank(mobileCaptcha.getCaptcha())) {
            throw new BusinessException(MsgEnum.INVALID_CAPTCHA);
        }
        JSONObject jsonObject = mysService.getTicketByMobile(mobileCaptcha.getMobile(), mobileCaptcha.getCaptcha());
        JSONObject cookie = jsonObject.getJSONObject("cookie");
        String mysUid = jsonObject.getString("mysUid");

        String ticket = cookie.getString("login_ticket");
        if (StringUtils.isBlank(ticket)) {
            throw new BusinessException(MsgEnum.ERROR_GET_TICKET);
        }

        String stoken = mysService.getStokenByTicket(ticket, mysUid);
        if (StringUtils.isBlank(stoken)) {
            throw new BusinessException(MsgEnum.ERROR_GET_STOKEN);
        }

        LocalCache.TICKET.put(mobileCaptcha.getMobile(), ticket);
        LocalCache.STOKEN.put(mobileCaptcha.getMobile(), stoken);
        LocalCache.UID.put(mobileCaptcha.getMobile(), mysUid);
        return JsonResult.success();
    }

    /**
     * 新增用户
     *
     * @param mobileCaptcha 手机和验证码
     * @return JsonResult
     */
    @RequestMapping("/user/add/2")
    public JsonResult addCookieByMobile(@RequestBody MobileCaptcha mobileCaptcha) {
        if (StringUtils.isBlank(mobileCaptcha.getMobile())) {
            throw new BusinessException(MsgEnum.INVALID_MOBILE);
        }
        if (StringUtils.isBlank(mobileCaptcha.getCaptcha())) {
            throw new BusinessException(MsgEnum.INVALID_CAPTCHA);
        }
        JSONObject jsonObject = mysService.getCookieTokenByMobile(mobileCaptcha.getMobile(), mobileCaptcha.getCaptcha());
        jsonObject.put("login_ticket", LocalCache.TICKET.getIfPresent(mobileCaptcha.getMobile()));
        jsonObject.put("stoken", LocalCache.STOKEN.getIfPresent(mobileCaptcha.getMobile()));
        jsonObject.put("stuid", jsonObject.get("account_id"));

        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
            builder.append(entry.getKey()).append("=").append(entry.getValue()).append(";");
        }
        String cookie = builder.toString();

        UserInfo userInfo = new UserInfo();
        userInfo.setMys_uid((String) LocalCache.UID.getIfPresent(mobileCaptcha.getMobile()));
        userInfo.setCookie(cookie);

        this.updateInfo(userInfo);

        fileService.writeUserInfo(userInfo);

        LocalCache.TICKET.invalidate(mobileCaptcha.getMobile());
        LocalCache.STOKEN.invalidate(mobileCaptcha.getMobile());
        LocalCache.UID.invalidate(mobileCaptcha.getMobile());

        return JsonResult.success().data(userInfo);
    }

    /**
     * 更新用户信息
     *
     * @param userBody uid数据
     * @return JsonResult
     */
    @RequestMapping("/user/update")
    public JsonResult updateUser(@RequestBody UserBody userBody) {
        if (StringUtils.isBlank(userBody.getUid())) {
            throw new BusinessException(MsgEnum.INVALID_UID);
        }
        UserInfo userInfo = fileService.findUserInfo(userBody.getUid());
        boolean isExpire = mysService.checkCookie(userInfo.getCookie());
        if (isExpire) {
            mysService.updateCookie(userInfo);
            isExpire = mysService.checkCookie(userInfo.getCookie());
            if (isExpire) {
                throw new BusinessException(MsgEnum.ERROR_COOKIE_LOGOUT);
            }
        }
        this.updateInfo(userInfo);

        fileService.writeUserInfo(userInfo);

        return JsonResult.success();
    }

    /**
     * 更新用户信息
     *
     * @param userBody uid数据
     * @return JsonResult
     */
    @RequestMapping("/user/delete")
    public JsonResult deleteUser(@RequestBody UserBody userBody) {
        if (StringUtils.isBlank(userBody.getUid())) {
            throw new BusinessException(MsgEnum.INVALID_UID);
        }
        UserInfo userInfo = fileService.findUserInfo(userBody.getUid());
        fileService.deleteUserInfo(userInfo);

        return JsonResult.success();
    }

    private void updateInfo(UserInfo userInfo) {
        try {
            userInfo.setPoint(mysService.getPoint(userInfo));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            userInfo.setAddress_list(mysService.getAddressList(userInfo));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            userInfo.setGame_list(mysService.getGameRoles(userInfo));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            userInfo.setNickname(mysService.getNickname(userInfo));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
