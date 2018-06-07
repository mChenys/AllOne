package blog.csdn.net.mchenys.common.download;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程管理器
 * Created by mChenys on 2015/11/28.
 */
public class ThreadManager {

    private static ThreadPool mThreadPool;

    /**
     * 获取线程池
     *
     * @return
     */
    public static ThreadPool getThreadPool() {
        if (null == mThreadPool) {
            synchronized (ThreadManager.class) {
                if (null == mThreadPool) {
                    // cpu个数
                    int cpuNum = Runtime.getRuntime().availableProcessors();
                    //线程个数
                    int count = cpuNum * 2 + 1;
                    mThreadPool = new ThreadPool(count, count, 0);
                }
            }
        }
        return mThreadPool;
    }

    public static class ThreadPool {
        int corePoolSize;// 核心线程数
        int maximumPoolSize;// 最大线程数
        long keepAliveTime;// 保持活跃时间(休息时间)
        private ThreadPoolExecutor executor;

        /**
         * 构造方法初始化
         *
         * @param corePoolSize
         * @param maximumPoolSize
         * @param keepAliveTime
         */
        private ThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime) {
            this.corePoolSize = corePoolSize;
            this.maximumPoolSize = maximumPoolSize;
            this.keepAliveTime = keepAliveTime;
        }
        private static final ThreadFactory sThreadFactory = new ThreadFactory() {
            private final AtomicInteger mCount = new AtomicInteger(1);

            public Thread newThread(Runnable r) {
                return new Thread(r, "ThreadManager #" + mCount.getAndIncrement());
            }
        };
        /**
         * 执行线程任务
         *
         * @param r
         */
        public void execute(Runnable r) {
            //参1:核心线程数;参2:最大线程数;参3:保持活跃时间(休息时间);参4:活跃时间单位;参5:线程队列;参6:线程工厂;参7:异常处理策略
            if (null == executor) {
                executor = new ThreadPoolExecutor(corePoolSize,
                        maximumPoolSize,
                        keepAliveTime,
                        TimeUnit.SECONDS,
                        new LinkedBlockingQueue<Runnable>(),
                        sThreadFactory/*Executors.defaultThreadFactory()*/,
                        new ThreadPoolExecutor.AbortPolicy());
            }
            // 将当前Runnable对象放在线程池中执行
            executor.execute(r);
        }

        /**
         * 从线程池的任务队列中移除一个任务
         * 如果当前任务已经是运行状态了,那么就表示不在任务队列中了,也就移除失败.
         */
        public void cancel(Runnable r) {
            if (null != executor && null != r) {
                executor.getQueue().remove(r);
            }
        }

        /**
         * 是否关闭了线程池
         * @return
         */
        public boolean isShutdown(){
            return executor.isShutdown();
        }

        /**
         * 关闭线程池
         */
        public void shutdown() {
            executor.shutdown();
        }
    }
}
