package myDeal.readFile;

import java.util.concurrent.*;
import java.util.function.Consumer;

/**
 * 线程处理类，用来创建一个无核心线程的线程池
 * 定义了一个只包含指定核心线程的线程，当所有核心线程都在执行任务时，会阻塞后续生产者任务的提交，直到有任意一个核心线程执行完成任务
 */
public class ThreadUtil {
    /**
     * 线程池的最大线程数
     */
    private int segement;

    private ThreadPoolExecutor threadPoolExecutor;

    /**
     * 执行的主线程
     */
    private Thread mainThread;

    private ThreadUtil(int segement) {
        this.segement = segement;
        mainThread = Thread.currentThread();
        /* 当线程池拒绝时将该任务加入该线程池的阻塞队列，这时候由于 put 方法不同于 offer 方法会导致主线程阻塞，直到 put 成功 */
        threadPoolExecutor = new ThreadPoolExecutor(this.segement, this.segement, 60, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1, true), (r, executor) -> {
            try {
                if (!executor.isShutdown()) {
                    System.out.println("线程池中所有核心线程已经处于工作状态，将等待下一个核心线程处理任务完成才继续处理，阻塞的线程为：" + Thread.currentThread());
                    executor.getQueue().put(r);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
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

    /**
     * 判断线程池是否已经满了
     * @return
     */
    public boolean isFull() {
        /* 判断激活的线 */
        return threadPoolExecutor.getActiveCount() == this.segement;
    }
}
