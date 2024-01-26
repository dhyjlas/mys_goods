package com.dhyjlas.mys.controller;

import com.dhyjlas.mys.consts.MsgEnum;
import com.dhyjlas.mys.entity.ExchangeInfo;
import com.dhyjlas.mys.entity.GoodsInfo;
import com.dhyjlas.mys.exception.BusinessException;
import com.dhyjlas.mys.schedule.ExchangeSchedule;
import com.dhyjlas.mys.service.FileService;
import com.dhyjlas.mys.service.MysService;
import com.dhyjlas.mys.util.JsonResult;
import com.dhyjlas.mys.util.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>File: ExchangeController.java </p>
 * <p>Title: </p>
 * <p>Description: </p>
 *
 * @author yjl.yu/dhyjlas@163.com
 * @version 1.0
 */
@Slf4j
@RestController
public class ExchangeController {
    private final FileService fileService;

    private final MysService mysService;

    @Autowired
    private ExchangeSchedule exchangeSchedule;

    public ExchangeController(FileService fileService, MysService mysService) {
        this.fileService = fileService;
        this.mysService = mysService;
    }

    /**
     * 获取兑换文件列表
     *
     * @return JsonResult
     */
    @RequestMapping("/exchange/list")
    public JsonResult listExchange() {
        List<ExchangeInfo> exchangeInfoList = fileService.listExchangeInfo();
        return JsonResult.success().data(exchangeInfoList);
    }

    /**
     * 删除兑换文件
     *
     * @param exchangeInfo 兑换文件信息
     * @return JsonResult
     */
    @RequestMapping("/exchange/delete")
    public JsonResult deleteExchange(@RequestBody ExchangeInfo exchangeInfo) {
        if (exchangeInfo.getId() == null || StringUtils.isBlank(exchangeInfo.getMys_uid())) {
            throw new BusinessException(MsgEnum.INVALID);
        }

        if (fileService.deleteExchangeInfo(exchangeInfo)) {
            return JsonResult.success();
        }
        return JsonResult.failure(MsgEnum.ERROR_DELETE);
    }

    /**
     * 新增兑换
     *
     * @param exchangeInfo 兑换文件信息
     * @return JsonResult
     */
    @RequestMapping("/exchange/add")
    public JsonResult addExchange(@RequestBody ExchangeInfo exchangeInfo) {
        if (StringUtils.isBlank(exchangeInfo.getMys_uid())) {
            throw new BusinessException(MsgEnum.INVALID_MYS_UID);
        }

        if (StringUtils.isAnyBlank(exchangeInfo.getGoods_id())) {
            throw new BusinessException(MsgEnum.INVALID_GOODS);
        }
        GoodsInfo goodsInfo = mysService.getGoodsDetail(exchangeInfo.getGoods_id());
        if (goodsInfo.getNext_time() * 1000 <= TimeUtil.getCurrentMillisTime()) {
            throw new BusinessException(MsgEnum.ERROR_EXCHANGE_TIME);
        }
        exchangeInfo.setGoods_name(goodsInfo.getGoods_name());
        exchangeInfo.setGame_biz(goodsInfo.getGame_biz());
        exchangeInfo.setType(goodsInfo.getType());
        exchangeInfo.setExchange_num(1);
        exchangeInfo.setExchange_time(goodsInfo.getNext_time());

        //虚拟商品需要角色ID和区服
        if (!StringUtils.equals(exchangeInfo.getGame_biz(), "bbs") && exchangeInfo.getType() == 2) {
            if (StringUtils.isAnyBlank(exchangeInfo.getGame_uid(), exchangeInfo.getRegion())) {
                throw new BusinessException(MsgEnum.INVALID_GAME_UID);
            }
        }
        //实物商品需要配送地址
        if (exchangeInfo.getType() == 1 || exchangeInfo.getType() == 4) {
            if (StringUtils.isBlank(exchangeInfo.getAddress_id())) {
                throw new BusinessException(MsgEnum.INVALID_ADDRESS);
            }
        }

        boolean f = fileService.writeExchangeInfo(exchangeInfo);
        if (f) {
            exchangeSchedule.performTask();
            return JsonResult.success();
        }
        return JsonResult.failure();
    }
}
