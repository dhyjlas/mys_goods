package com.dhyjlas.mys.util;

import java.util.concurrent.*;

/**
 * <p>File: ScheduledPoolUtils.java </p>
 * <p>Title: </p>
 * <p>Description: </p>
 *
 * @author yjl.yu/dhyjlas@163.com
 * @version 1.0
 */
public enum ScheduledPoolUtils {
    /**
     * 带定时的线程池
     */
    INSTANCE(50,
            Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.AbortPolicy());

    private final ScheduledExecutorService scheduledExecutor;

    ScheduledPoolUtils(int corePoolSize,
                       ThreadFactory threadFactory,
                       RejectedExecutionHandler handler) {
        scheduledExecutor = new ScheduledThreadPoolExecutor(corePoolSize, threadFactory, handler);
    }

    /**
     * 执行线程
     *
     * @param command Runnable
     * @param delay 延迟时间
     * @param unit 时间单位
     * @return
     */
    public ScheduledFuture<?> schedule(Runnable command,
                                       long delay, TimeUnit unit) {
        return scheduledExecutor.schedule(command, delay, unit);
    }

    /**
     * 获取线程池对象
     */
    public ScheduledExecutorService getPool() {
        return scheduledExecutor;
    }
}
