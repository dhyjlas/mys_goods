package com.dhyjlas.mys.util;

import java.util.concurrent.*;

/**
 * <p>File: ReflectUtils.java </p>
 * <p>Title: </p>
 * <p>Description: </p>
 *
 * @author yjl.yu/dhyjlas@163.com
 * @version 1.0
 */
public enum ThreadPoolUtils {
    /**
     * 线程池单例
     */
    INSTANCE(50,
            50,
            5,
            TimeUnit.SECONDS,
            new LinkedBlockingDeque<>(1000),
            Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.AbortPolicy());

    private final ThreadPoolExecutor threadPool;

    ThreadPoolUtils(int corePoolSize,
                    int maximumPoolSize,
                    long keepAliveTime,
                    TimeUnit unit,
                    BlockingQueue<Runnable> workQueue,
                    ThreadFactory threadFactory,
                    RejectedExecutionHandler handler) {
        threadPool = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
    }

    /**
     * 执行线程操作
     */
    public <T> Future<T> submit(Callable<T> callable) {
        return threadPool.submit(callable);
    }

    /**
     * 执行线程操作
     */
    public void execute(Runnable command) {
        threadPool.execute(command);
    }

    /**
     * 获取线程池对象
     */
    public ThreadPoolExecutor getPool() {
        return threadPool;
    }
}
