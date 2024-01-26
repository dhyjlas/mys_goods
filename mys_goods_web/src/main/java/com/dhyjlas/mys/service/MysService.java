package com.dhyjlas.mys.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dhyjlas.mys.consts.MsgEnum;
import com.dhyjlas.mys.entity.*;
import com.dhyjlas.mys.exception.BusinessException;
import com.dhyjlas.mys.util.HttpUtil;
import com.dhyjlas.mys.util.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * <p>File: MhyService.java </p>
 * <p>Title: 米游社接口相关</p>
 * <p>Description: </p>
 *
 * @author yjl.yu/dhyjlas@163.com
 * @version 1.0
 */
@Slf4j
@Service
public class MysService {
    @Value("#{${web.header}}")
    private Map<String, String> defaultHeader;

    @Value("${web.url}")
    private String webUrl;

    @Value("${mi.url}")
    private String miUrl;

    @Value("${bbs.url}")
    private String bbsUrl;

    /**
     * 获取ticket和mys_uid
     *
     * @param mobile        手机号
     * @param mobileCaptcha 验证码
     * @return JSONObject
     */
    public JSONObject getTicketByMobile(String mobile, String mobileCaptcha) {
        String url = webUrl + "/Api/login_by_mobilecaptcha";
        Map<String, String> params = new HashMap<>(3);
        params.put("mobile", mobile);
        params.put("mobile_captcha", mobileCaptcha);
        params.put("source", "user.mihoyo.com");

        log.info("获取ticket和mys_uid url->{} param->{}", url, JSON.toJSONString(params));
        HttpUtil.Result result = HttpUtil.post(url, params);
        log.info("获取ticket和mys_uid result->{}", result);

        if (result.getCode() != 200) {
            log.error("获取ticket和mys_uid 返回错误 url->{} param->{} response->{}", url, JSON.toJSONString(params), result);
            throw new BusinessException(MsgEnum.ERROR_CAPTCHA);
        }
        if (result.getJson().getJSONObject("data").getIntValue("status") == -201) {
            log.error("获取ticket和mys_uid 验证码错误 url->{} param->{} response->{}", url, JSON.toJSONString(params), result);
            throw new BusinessException(MsgEnum.ERROR_CAPTCHA);
        }

        JSONObject cookies = result.getCookies();
        if (cookies.get("login_ticket") == null) {
            throw new BusinessException(MsgEnum.INVALID_LOGIN_TICKET);
        }
        String mysUid;
        if (cookies.get("login_uid") == null) {
            mysUid = result.getJson().getJSONObject("data").getJSONObject("account_info").getString("account_id");
        } else {
            mysUid = result.getCookies().getString("login_uid");
        }
        if (mysUid == null) {
            throw new BusinessException(MsgEnum.INVALID_LOGIN_UID);
        }
        JSONObject re = new JSONObject(2);
        re.put("cookie", cookies);
        re.put("mysUid", mysUid);
        return re;
    }

    /**
     * 获取stoken
     *
     * @param loginTicket login_ticket
     * @param mysUid      mys_uid
     * @return String
     */
    public String getStokenByTicket(String loginTicket, String mysUid) {
        String url = miUrl + "/auth/api/getMultiTokenByLoginTicket?login_ticket=" + loginTicket + "&token_types=3&uid=" + mysUid;

        log.info("获取stoken url->{}", url);
        HttpUtil.Result result = HttpUtil.get(url);
        log.info("获取stoken result->{}", result);

        if (result.getJson().getIntValue("retcode") != 0) {
            log.error("获取stoken 返回错误 url->{} response->{}", url, result);
            throw new BusinessException(MsgEnum.ERROR_REQUEST);
        }

        return result.getJson().getJSONObject("data").getJSONArray("list").getJSONObject(0).getString("token");
    }


    /**
     * 获取cookie_token
     *
     * @param mobile        手机号
     * @param mobileCaptcha 验证码
     * @return JSONObject
     */
    public JSONObject getCookieTokenByMobile(String mobile, String mobileCaptcha) {
        String url = miUrl + "/account/auth/api/webLoginByMobile";
        Map<String, Object> params = new HashMap<>(5);
        params.put("is_bh2", false);
        params.put("mobile", mobile);
        params.put("captcha", mobileCaptcha);
        params.put("action_type", "login");
        params.put("token_type", 6);

        log.info("获取cookie_token url->{} param->{}", url, JSON.toJSONString(params));
        HttpUtil.Result result = HttpUtil.post(url, null, JSON.toJSONString(params), "utf-8");
        log.info("获取cookie_token result->{}", result);

        if (result.getCode() != 200) {
            log.error("获取cookie_token 返回错误 url->{} param->{} response->{}", url, JSON.toJSONString(params), result);
            throw new BusinessException(MsgEnum.ERROR_CAPTCHA);
        }
        if (result.getJson().getJSONObject("data").getIntValue("retcode") == -201) {
            log.error("获取cookie_token 验证码错误 url->{} param->{} response->{}", url, JSON.toJSONString(params), result);
            throw new BusinessException(MsgEnum.ERROR_CAPTCHA);
        }

        JSONObject cookies = result.getCookies();
        if (cookies.get("cookie_token") == null) {
            throw new BusinessException(MsgEnum.INVALID_COOKIE_TOKEN);
        }
        return cookies;
    }

    /**
     * 检查cookie是否过期
     *
     * @param cookie cookie
     * @return Boolean
     */
    public boolean checkCookie(String cookie) {
        String url = miUrl + "/account/address/list";
        Map<String, String> headers = new HashMap<>(1);
        headers.put("Cookie", cookie);

        log.info("检查cookie是否过期 url->{} headers->{}", url, JSON.toJSONString(headers));
        HttpUtil.Result result = HttpUtil.get(url, headers);
        log.info("检查cookie是否过期 result->{}", result);

        if (result.getCode() != 200) {
            log.error("检查cookie是否过期 错误 url->{} headers->{} result->{}", url, JSON.toJSONString(headers), result);
            throw new BusinessException(MsgEnum.ERROR_REQUEST);
        }

        if (result.getJson().getIntValue("retcode") != 0) {
            log.error("检查cookie是否过期 验证码已过期");
            return true;
        }
        log.info("cookie未过期");
        return false;
    }

    /**
     * 更新cookie
     *
     * @param userInfo 用户信息
     */
    public void updateCookie(UserInfo userInfo) {
        if (userInfo == null || StringUtils.isAnyBlank(userInfo.getCookie())) {
            throw new BusinessException(MsgEnum.INVALID);
        }

        Map<String, String> map = this.parseCookie(userInfo.getCookie());
        String cookie = map.get("cookie_token");
        String stoken = map.get("stoken");
        if (StringUtils.isAnyBlank(cookie, stoken)) {
            throw new BusinessException(MsgEnum.INVALID);
        }

        String url = miUrl + "/auth/api/getCookieAccountInfoBySToken?uid=" + userInfo.getMys_uid() + "&stoken=" + stoken;

        log.info("更新cookie url->{}", url);
        HttpUtil.Result result = HttpUtil.get(url);
        log.info("更新cookie result->{}", result);

        if (result.getCode() != 200) {
            log.error("更新cookie 错误 url->{} result->{}", url, result);
            throw new BusinessException(MsgEnum.ERROR_REQUEST);
        }
        JSONObject data = result.getJson().getJSONObject("data");
        if (data == null) {
            log.error("更新cookie 错误 url->{} result->{}", url, result);
            throw new BusinessException(MsgEnum.ERROR_UPDATE_COOKIE, result.getJson().getString("message"));
        }

        Map<String, String> cookieMap = this.parseCookie(userInfo.getCookie());
        cookieMap.put("cookie", data.getString("cookie_token"));

        userInfo.setCookie(this.buildCookie(cookieMap));
    }

    /**
     * 获取昵称
     *
     * @param userInfo 用户信息
     * @return LinkedHashMap<String, Integer>
     */
    public String getNickname(UserInfo userInfo) {
        if (userInfo == null || StringUtils.isAnyBlank(userInfo.getCookie(), userInfo.getMys_uid())) {
            throw new BusinessException(MsgEnum.INVALID);
        }

        Map<String, String> headers = new HashMap<>(1);
        headers.put("Cookie", userInfo.getCookie());
        String url = bbsUrl + "/user/api/getUserFullInfo?uid=" + userInfo.getMys_uid();

        log.info("获取昵称 url->{} header->{}", url, headers);
        HttpUtil.Result result = HttpUtil.get(url, headers);
        log.info("获取昵称 result->{}", result);

        if (result.getCode() != 200) {
            log.error("获取昵称 错误 url->{} header->{} result->{}", url, JSON.toJSONString(headers), result);
            throw new BusinessException(MsgEnum.ERROR_REQUEST);
        }
        if (result.getJson().getIntValue("retcode") != 0) {
            log.error("获取昵称 错误 url->{} header->{} result->{}", url, JSON.toJSONString(headers), result);
            throw new BusinessException(MsgEnum.ERROR_CHANNEL_LEVEL, result.getJson().getString("message"));
        }
        //同时更新昵称
        return result.getJson().getJSONObject("data").getJSONObject("user_info").getString("nickname");
    }

    /**
     * 获取查询所需ticket
     *
     * @param userInfo 用户信息
     * @return String
     */
    public String getActionTicket(UserInfo userInfo) {
        if (userInfo == null || StringUtils.isAnyBlank(userInfo.getMys_uid(), userInfo.getCookie())) {
            throw new BusinessException(MsgEnum.INVALID);
        }

        Map<String, String> map = this.parseCookie(userInfo.getCookie());
        String stoken = map.get("stoken");
        String url = miUrl + "/auth/api/getActionTicketBySToken?action_type=game_role&stoken=" + stoken + "&uid=" + userInfo.getMys_uid();

        log.info("获取查询所需ticket url->{}", url);
        HttpUtil.Result result = HttpUtil.get(url);
        log.info("获取查询所需ticket result->{}", result);

        if (result.getCode() != 200) {
            log.error("获取查询所需ticket 错误 url->{} result->{}", url, result);
            throw new BusinessException(MsgEnum.ERROR_REQUEST);
        }

        JSONObject data = result.getJson().getJSONObject("data");
        if (data == null) {
            log.error("获取查询所需ticket 错误 url->{} result->{}", url, result);
            throw new BusinessException(MsgEnum.ERROR_ACTION_TICKET, result.getJson().getString("message"));
        }

        return data.getString("ticket");
    }

    /**
     * 获取绑定角色
     *
     * @param userInfo 用户信息
     * @return UserInfo
     */
    public List<GameInfo> getGameRoles(UserInfo userInfo) {
        String actionTicket = this.getActionTicket(userInfo);
        if (StringUtils.isBlank(actionTicket)) {
            throw new BusinessException(MsgEnum.ERROR_ACTION_TICKET);
        }
        Map<String, String> headers = new HashMap<>(1);
        headers.put("cookie", userInfo.getCookie());
        String url = miUrl + "/binding/api/getUserGameRoles?point_sn=myb&action_ticket=" + actionTicket;

        log.info("获取绑定角色 url->{} header->{}", url, JSON.toJSONString(headers));
        HttpUtil.Result result = HttpUtil.get(url, headers);
        log.info("获取绑定角色 result->{}", result);

        if (result.getCode() != 200) {
            log.error("获取绑定角色 返回错误 url->{} header->{} response->{}", url, JSON.toJSONString(headers), result);
            throw new BusinessException(MsgEnum.ERROR_REQUEST);
        }

        JSONObject data = result.getJson().getJSONObject("data");
        if (data == null) {
            log.error("获取绑定角色 返回错误 url->{} header->{} response->{}", url, JSON.toJSONString(headers), result);
            throw new BusinessException(MsgEnum.ERROR_GAME_ROLES, result.getJson().getString("message"));
        }

        return JSON.parseArray(JSON.toJSONString(data.get("list")), GameInfo.class);
    }

    /**
     * 获取收货地址
     *
     * @param userInfo 用户信息
     * @return List<AddressInfo>
     */
    public List<AddressInfo> getAddressList(UserInfo userInfo) {
        if (userInfo == null || StringUtils.isAnyBlank(userInfo.getCookie())) {
            throw new BusinessException(MsgEnum.INVALID);
        }

        Map<String, String> headers = new HashMap<>(1);
        headers.put("cookie", userInfo.getCookie());

        String url = miUrl + "/account/address/list";

        log.info("获取收货地址 url->{} header->{}", url, JSON.toJSONString(headers));
        HttpUtil.Result result = HttpUtil.get(url, headers);
        log.info("获取收货地址 result->{}", result);

        if (result.getCode() != 200) {
            log.error("获取收货地址 返回错误 url->{} header->{} response->{}", url, JSON.toJSONString(headers), result);
            throw new BusinessException(MsgEnum.ERROR_REQUEST);
        }

        JSONObject data = result.getJson().getJSONObject("data");
        if (data == null) {
            log.error("获取收货地址 返回错误 url->{} header->{} response->{}", url, JSON.toJSONString(headers), result);
            throw new BusinessException(MsgEnum.ERROR_ADDRESS_LIST, result.getJson().getString("message"));
        }

        return JSON.parseArray(JSON.toJSONString(data.get("list")), AddressInfo.class);
    }

    /**
     * 获取商品列表
     *
     * @param userInfo 用户信息
     * @param game     分区
     * @return List<GoodsInfo>
     */
    public List<GoodsInfo> getGoodsList(UserInfo userInfo, String game) {
        if (userInfo == null || StringUtils.isAnyBlank(userInfo.getCookie())) {
            throw new BusinessException(MsgEnum.INVALID);
        }

        Map<String, String> headers = new HashMap<>(1);
        headers.put("cookie", userInfo.getCookie());

        List<GoodsInfo> goodsInfoList = new ArrayList<>();
        int page = 1;

        while (true) {
            String url = miUrl + "/mall/v1/web/goods/list?app_id=1&point_sn=myb&page_size=20&page=" + page + "&game=" + game;

            log.info("获取商品列表 url->{} header->{}", url, JSON.toJSONString(headers));
            HttpUtil.Result result = HttpUtil.get(url, headers);
            log.info("获取商品列表 result->{}", result);

            if (result.getCode() != 200) {
                log.error("获取商品列表 返回错误 url->{} header->{} response->{}", url, JSON.toJSONString(headers), result);
                throw new BusinessException(MsgEnum.ERROR_REQUEST);
            }

            JSONObject data = result.getJson().getJSONObject("data");
            if (data == null) {
                log.error("获取商品列表 错误 url->{} header->{} result->{}", url, JSON.toJSONString(headers), result);
                throw new BusinessException(MsgEnum.ERROR_GOODS_LIST, result.getJson().getString("message"));
            }

            goodsInfoList.addAll(JSON.parseArray(JSON.toJSONString(data.get("list")), GoodsInfo.class));
            int total = data.getIntValue("total");
            if (total > page * 20) {
                page++;
            } else {
                break;
            }
        }

        return goodsInfoList;
    }

    /**
     * 按需要获取商品详情
     *
     * @param goodsId 商品ID
     * @return GoodsInfo
     */
    public GoodsInfo getGoodsDetail(String goodsId) {
        if (StringUtils.isBlank(goodsId)) {
            throw new BusinessException(MsgEnum.INVALID);
        }

        String url = miUrl + "/mall/v1/web/goods/detail?app_id=1&point_sn=myb&goods_id=" + goodsId;

        log.info("按需要获取商品详情 url->{}", url);
        HttpUtil.Result result = HttpUtil.get(url);
        log.info("按需要获取商品详情 result->{}", result);

        if (result.getCode() != 200) {
            log.error("按需要获取商品详情 返回错误 url->{} response->{}", url, result);
            throw new BusinessException(MsgEnum.ERROR_REQUEST);
        }

        JSONObject data = result.getJson().getJSONObject("data");
        if (data == null) {
            log.error("按需要获取商品详情 错误 url->{} result->{}", url, result);
            throw new BusinessException(MsgEnum.ERROR_GOODS_DETAIL, result.getJson().getString("message"));
        }

        return JSON.parseObject(JSON.toJSONString(data), GoodsInfo.class);
    }

    /**
     * 获取米游币数量
     *
     * @param userInfo 用户信息
     * @return int
     */
    public int getPoint(UserInfo userInfo) {
        if (userInfo == null || StringUtils.isAnyBlank(userInfo.getCookie())) {
            throw new BusinessException(MsgEnum.INVALID);
        }

        Map<String, String> headers = new HashMap<>(1);
        headers.put("cookie", userInfo.getCookie());

        String url = bbsUrl + "/apihub/sapi/getUserMissionsState";

        log.info("获取米游币数量 url->{} header->{}", url, JSON.toJSONString(headers));
        HttpUtil.Result result = HttpUtil.get(url, headers);
        log.info("获取米游币数量 result->{}", result);

        if (result.getCode() != 200) {
            log.error("获取米游币数量 返回错误 url->{} header->{} response->{}", url, JSON.toJSONString(headers), result);
            throw new BusinessException(MsgEnum.ERROR_REQUEST);
        }

        JSONObject data = result.getJson().getJSONObject("data");
        if (data == null) {
            log.error("获取米游币数量 错误 url->{} header->{} result->{}", url, JSON.toJSONString(headers), result);
            throw new BusinessException(MsgEnum.ERROR_GET_POINT, result.getJson().getString("message"));
        }

        return data.getIntValue("total_points");
    }

    /**
     * 兑换商品
     *
     * @param userInfo     用户信息
     * @param exchangeInfo 兑换信息
     * @return String
     */
    public String exchangeGoods(UserInfo userInfo, ExchangeInfo exchangeInfo) {
        if (userInfo == null || StringUtils.isAnyBlank(userInfo.getCookie())) {
            throw new BusinessException(MsgEnum.INVALID);
        }
        if (exchangeInfo == null || StringUtils.isAnyBlank(exchangeInfo.getGoods_id(), exchangeInfo.getMys_uid(), exchangeInfo.getGame_biz())) {
            throw new BusinessException(MsgEnum.INVALID);
        }

        String url = miUrl + "/mall/v1/web/goods/exchange";

        Map<String, String> headers = new HashMap<>(1);
        headers.put("cookie", userInfo.getCookie());

        Map<String, Object> params = new HashMap<>(8);
        params.put("app_id", 1);
        params.put("point_sn", "myb");
        params.put("goods_id", exchangeInfo.getGoods_id());
        params.put("exchange_num", 1);
        params.put("uid", exchangeInfo.getGame_uid());
        params.put("game_biz", exchangeInfo.getGame_biz());
        if (StringUtils.isNotBlank(exchangeInfo.getRegion())) {
            params.put("region", exchangeInfo.getRegion());
        }
        if (StringUtils.isNotBlank(exchangeInfo.getAddress_id())) {
            params.put("address_id", exchangeInfo.getAddress_id());
        }
        if (StringUtils.isNotBlank(exchangeInfo.getMys_uid())) {
            params.put("uid", exchangeInfo.getMys_uid());
        }

        log.info("兑换商品 url->{} param->{}", url, JSON.toJSONString(params));
        HttpUtil.Result result = HttpUtil.post(url, headers, JSON.toJSONString(params), "utf-8");
        log.info("兑换商品 result->{}", result);

        if (result.getCode() != 200) {
            log.error("兑换商品 错误 url->{} header->{} params->{} response->{}", url, JSON.toJSONString(headers), JSON.toJSONString(params), result);
            throw new BusinessException(MsgEnum.ERROR_REQUEST);
        }

        JSONObject data = result.getJson().getJSONObject("data");
        if (data == null) {
            log.error("兑换商品 错误 url->{} header->{} params->{} response->{}", url, JSON.toJSONString(headers), JSON.toJSONString(params), result);
            throw new BusinessException(MsgEnum.ERROR_EXCHANGE_GOODS.getCode(), result.getJson().getString("message"));
        }

        return data.getString("order_sn");
    }

    /**
     * 检查手机号是否注册
     *
     * @param mobile 手机号
     * @return boolean
     */
    public boolean isMobileRegistrable(String mobile){
        if(StringUtils.isBlank(mobile)){
            throw new BusinessException(MsgEnum.INVALID);
        }
        String url = webUrl + "/Api/is_mobile_registrable?mobile=" + mobile + "&t=" + TimeUtil.getCurrentMillisTime();

        log.info("检查手机号是否注册 url->{}", url);
        HttpUtil.Result result = HttpUtil.get(url);
        log.info("检查手机号是否注册 result->{}", result);

        if (result.getCode() != 200) {
            log.error("检查手机号是否注册 错误 url->{} response->{}", url, result);
            throw new BusinessException(MsgEnum.ERROR_REQUEST);
        }

        JSONObject data = result.getJson().getJSONObject("data");
        if (data == null || data.get("is_registable") == null) {
            log.error("检查手机号是否注册 错误 url->{} response->{}", url, result);
            throw new BusinessException(MsgEnum.ERROR_IS_REGISTRABLE);
        }

        return data.getIntValue("is_registable") == 1;
    }

    /**
     * 获取mmt_key
     * 会触发风控，暂时无法使用
     *
     * @return String
     */
    public String getMmtKey() {
        long t = TimeUtil.getCurrentMillisTime();
        String url = webUrl + "/Api/create_mmt?scene_type=1&now=" + t + "&reason=user.mihoyo.com#/login/captcha&action_type=login_by_mobile_captcha&t=" + t;

        log.info("获取mmt_key url->{}", url);
        HttpUtil.Result result = HttpUtil.get(url, defaultHeader);
        log.info("获取mmt_key result->{}", result);

        if (result.getCode() != 200) {
            log.error("获取mmt_key 错误 url->{} response->{}", url, result);
            throw new BusinessException(MsgEnum.ERROR_REQUEST);
        }

        JSONObject data = result.getJson().getJSONObject("data");
        if (data == null) {
            log.error("获取mmt_key 错误 url->{} response->{}", url, result);
            throw new BusinessException(MsgEnum.ERROR_MMT_KEY);
        }

        return data.getJSONObject("mmt_data").getString("mmt_key");
    }

    /**
     * 发送验证码
     *
     * @param mobile 手机
     * @param mmtKey mmt_key
     * @return boolean
     */
    public boolean sendMobileCaptcha(String mobile, String mmtKey){
        if(StringUtils.isAnyBlank(mobile, mmtKey)){
            throw new BusinessException(MsgEnum.INVALID);
        }

        String url = webUrl + "/Api/create_mobile_captcha";
        Map<String, String> params = new HashMap<>(3);
        params.put("action_type", "login");
        params.put("mmt_key", mmtKey);
        params.put("mobile", mobile);
        params.put("t", String.valueOf(TimeUtil.getCurrentMillisTime()));

        log.info("发送验证码 url->{} param->{}", url, JSON.toJSONString(params));
        HttpUtil.Result result = HttpUtil.post(url, defaultHeader, params, "utf-8");
        log.info("发送验证码 result->{}", result);

        if (result.getCode() != 200) {
            log.error("发送验证码 返回错误 url->{} param->{} response->{}", url, JSON.toJSONString(params), result);
            throw new BusinessException(MsgEnum.ERROR_REQUEST);
        }

        JSONObject data = result.getJson().getJSONObject("data");
        if (data == null || data.getIntValue("status") != 1) {
            log.error("发送验证码 返回错误 url->{} params->{} response->{}", url, JSON.toJSONString(params), result);
            throw new BusinessException(MsgEnum.ERROR_SEND_CAPTCHA);
        }

        return true;
    }

    /**
     * 解析cookie
     *
     * @param cookie cookie
     * @return Map<String, String>
     */
    public Map<String, String> parseCookie(String cookie) {
        String[] items = cookie.split(";");
        Map<String, String> map = new HashMap<>();
        for (String item : items) {
            int index = item.indexOf('=');
            if (index >= 0) {
                map.put(item.substring(0, index).trim(), item.substring(index + 1).trim());
            }
        }

        return map;
    }

    /**
     * 组装cookie
     *
     * @param map cookie
     * @return String
     */
    public String buildCookie(Map<String, String> map) {
        StringBuilder builder = new StringBuilder();
        for(Map.Entry<String, String> entry : map.entrySet()){
            builder.append(entry.getKey()).append("=").append(entry.getValue()).append(";");
        }
        return builder.toString();
    }
}
