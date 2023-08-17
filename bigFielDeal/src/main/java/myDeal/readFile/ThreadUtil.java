package myDeal.readFile;

import java.util.concurrent.*;

/**
 * 线程处理类，用来创建一个无核心线程的线程池
 */
public class ThreadUtil {
    /**
     * 线程池的最大线程数
     */
    private int segement;

    private ThreadPoolExecutor threadPoolExecutor;

    private ThreadUtil(int segement) {
        this.segement = segement;
//        threadPoolExecutor = Executors.newCachedThreadPool(this.segement);
    }

    public static ThreadUtil getThreadUtil(int segement) {
        return new ThreadUtil(segement);
    }

    /**
     * 向线程池中加入任务
     * @param runnable
     */
    public void addTask(Runnable runnable) {
        threadPoolExecutor.execute(runnable);
    }

    /**
     * 关闭线程池
     */
    public void close() {
        threadPoolExecutor.shutdown();
    }
}
