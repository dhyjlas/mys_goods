package com.dhyjlas.mys.schedule;

import com.alibaba.fastjson2.JSON;
import com.dhyjlas.mys.entity.ExchangeInfo;
import com.dhyjlas.mys.entity.UserInfo;
import com.dhyjlas.mys.service.FileService;
import com.dhyjlas.mys.service.MysService;
import com.dhyjlas.mys.service.TaskService;
import com.dhyjlas.mys.util.LocalCache;
import com.dhyjlas.mys.util.ScheduledPoolUtils;
import com.dhyjlas.mys.util.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>File: ExchangeSchedule.java </p>
 * <p>Title: </p>
 * <p>Description: </p>
 *
 * @author yjl.yu/dhyjlas@163.com
 * @version 1.0
 */
@Slf4j
@Component
public class ExchangeSchedule {
    private final TaskService taskService;

    private final FileService fileService;

    private final MysService mysService;

    public ExchangeSchedule(TaskService taskService, FileService fileService, MysService mysService) {
        this.taskService = taskService;
        this.fileService = fileService;
        this.mysService = mysService;
    }

    /**
     * 定时任务，每隔一段时间检查是否有即将执行的任务
     */
    @Scheduled(fixedRate = 1000 * 60 * 60)  // 每隔1小时执行一次
    public void performTask() {
        log.info("检查是否存在即将执行的任务...");
        List<ExchangeInfo> exchangeInfoList = fileService.listExchangeInfo();
        long time = TimeUtil.getCurrentMillisTime();
        for (ExchangeInfo exchangeInfo : exchangeInfoList) {
            try {
                if (exchangeInfo.getExchange_time() * 1000 < time) {
                    //任务已过期
                    continue;
                }
                if (exchangeInfo.getExchange_time() * 1000 - time > 1000 * 60 * 90) {
                    //离任务执行时间大于一个半小时
                    continue;
                }
                if (null != LocalCache.TASK.getIfPresent(exchangeInfo.getId())) {
                    //任务已经在等待执行了
                    continue;
                }

                //开始准备执行兑换任务
                UserInfo userInfo = fileService.findUserInfo(exchangeInfo.getMys_uid());
                if (userInfo == null || StringUtils.isBlank(userInfo.getCookie())) {
                    log.error("用户信息丢失->{}", JSON.toJSONString(exchangeInfo));
                    //用户信息缺失
                    continue;
                }

                //准备启动延时任务
                long delay = exchangeInfo.getExchange_time() - time / 1000 - 10;
                delay = delay < 0 ? 0 : delay;
                log.info("新的兑换任务已创建，将于{}秒后启动 exchangeInfo->{}", delay, JSON.toJSONString(exchangeInfo));
                ScheduledPoolUtils.INSTANCE.schedule(() -> {
                    try {
                        taskService.run(exchangeInfo, userInfo, 0);
                    } catch (Exception e) {
                        log.error("启动兑换线程失败，请手动检查原因 exchangeInfo->{} userInfo->{}", JSON.toJSONString(exchangeInfo), JSON.toJSONString(userInfo));
                        e.printStackTrace();
                    }
                }, delay, TimeUnit.SECONDS);
                LocalCache.TASK.put(exchangeInfo.getId(), exchangeInfo);
            } catch (Exception e) {
                log.error("检查任务->{}", JSON.toJSONString(exchangeInfo));
                log.error("检查任务出错", e);
            }
        }
    }
}

