package com.dhyjlas.mys.controller;

import com.dhyjlas.mys.consts.MsgEnum;
import com.dhyjlas.mys.entity.GoodsInfo;
import com.dhyjlas.mys.entity.UserInfo;
import com.dhyjlas.mys.entity.body.GoodsBody;
import com.dhyjlas.mys.exception.BusinessException;
import com.dhyjlas.mys.service.FileService;
import com.dhyjlas.mys.service.MysService;
import com.dhyjlas.mys.util.JsonResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>File: GoodsController.java </p>
 * <p>Title: </p>
 * <p>Description: </p>
 *
 * @author yjl.yu/dhyjlas@163.com
 * @version 1.0
 */
@Slf4j
@RestController
public class GoodsController {
    private final MysService mysService;

    private final FileService fileService;

    public GoodsController(MysService mysService, FileService fileService) {
        this.mysService = mysService;
        this.fileService = fileService;
    }

    /**
     * 获取商品列表
     *
     * @param goodsBody 请求参数
     * gema可取参数 '全部商品':'', '崩坏3':'bh3', '原神':'hk4e', '崩坏：星穹铁道':'hkrpg', '崩坏学园2':'bh2', '未定事件簿':'nxx', '米游社':'bbs'
     * @return JsonResult
     */
    @RequestMapping("/goods/list")
    public JsonResult listGoods(@RequestBody GoodsBody goodsBody){
        if(StringUtils.isBlank(goodsBody.getUid())){
            throw new BusinessException(MsgEnum.INVALID_UID);
        }
        UserInfo userInfo = fileService.findUserInfo(goodsBody.getUid());
        List<GoodsInfo> goodsInfoList = mysService.getGoodsList(userInfo, goodsBody.getGame() == null ? "": goodsBody.getGame());
        return JsonResult.success().data(goodsInfoList);
    }
}
