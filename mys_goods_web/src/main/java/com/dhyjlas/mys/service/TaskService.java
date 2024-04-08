package com.dhyjlas.mys.service;

import com.alibaba.fastjson.JSON;
import com.dhyjlas.mys.entity.ExchangeInfo;
import com.dhyjlas.mys.entity.UserInfo;
import com.dhyjlas.mys.util.ThreadPoolUtils;
import com.dhyjlas.mys.util.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * <p>File: TaskService.java </p>
 * <p>Title: </p>
 * <p>Description: </p>
 *
 * @author yjl.yu/dhyjlas@163.com
 * @version 1.0
 */
@Slf4j
@Service
public class TaskService {
    private final MysService mysService;

    private final FileService fileService;

    public TaskService(MysService mysService, FileService fileService) {
        this.mysService = mysService;
        this.fileService = fileService;
    }

    /**
     * 等待任务执行
     *
     * @param exchangeInfo 兑换信息
     * @param userInfo     用户信息
     * @param offset       时间偏移量
     * @throws InterruptedException InterruptedException
     */
    public void run(ExchangeInfo exchangeInfo, UserInfo userInfo, long offset) throws InterruptedException {
        log.info("即将进行兑换：{}", exchangeInfo.getGoods_name());

        try {
            //检查兑换文件是否还在
            exchangeInfo = fileService.findExchangeInfo(exchangeInfo);
        } catch (Exception e) {
            log.error("兑换信息不存在，任务已终止");
            return;
        }

        //检查Cookie是否有效，无效则及时更新
        boolean isExpire = mysService.checkCookie(userInfo.getCookie());
        if (isExpire) {
            log.info("更新Cookie信息：{}", JSON.toJSONString(userInfo));
            mysService.updateCookie(userInfo);
        }

        int thread = exchangeInfo.getThread() > 0 ? exchangeInfo.getThread() : 1;
        int retry = exchangeInfo.getRetry() > 0 ? exchangeInfo.getRetry() : 1;
        if (thread >= 3) {
            offset -= ((thread - 1) / 2 * 50);
        }
        while (true) {
            if (TimeUtil.getCurrentMillisTime() - offset > exchangeInfo.getExchange_time() * 1000) {
                for (int i = 0; i < thread; i++) {
                    int finalI = i;
                    ExchangeInfo finalExchangeInfo = exchangeInfo;
                    ThreadPoolUtils.INSTANCE.execute(() -> {
                        for (int j = 0; j < retry; j++) {
                            try {
                                log.info("正在进行兑换：{}", finalExchangeInfo.getGoods_name());
                                String orderSn = mysService.exchangeGoods(userInfo, finalExchangeInfo);
                                if (StringUtils.isNotBlank(orderSn)) {
                                    finalExchangeInfo.setStatus("兑换成功");
                                    finalExchangeInfo.setOrderSn(orderSn);
                                    fileService.writeExchangeInfo(finalExchangeInfo);
                                    break;
                                }
                            } catch (Exception e) {
                                log.error("线程{}：第{}次尝试兑换失败，原因{}", finalI + 1, j + 1, e.getMessage());
                            }
                        }
                    });
                    Thread.sleep(50);
                }
                break;
            }
        }
        Thread.sleep(3000);
        if (StringUtils.isBlank(exchangeInfo.getOrderSn())) {
            exchangeInfo.setStatus("兑换失败");
            fileService.writeExchangeInfo(exchangeInfo);
        }
    }
}
