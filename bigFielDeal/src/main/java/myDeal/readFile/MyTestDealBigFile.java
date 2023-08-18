package myDeal.readFile;

import java.io.BufferedReader;
import java.io.FileReader;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

public class MyTestDealBigFile {

    public static final int BATCH_NUM = 5000000;


    /**
     * 使用普通的文件读取方式读取文件
     * 对比使用线程池按行处理文件和正常按行处理文件的差别
     * 使用线程池按行处理文件：
     *  内存占用高（可控）：会读取多批次的数据到线程中执行，但是可以通过控制每批读取行数以及线程数来现在内层使用情况，防止 oom
     *  处理效率高很多：每行记录等待资源时间占比越高，使用线程池的效率越高，如果每行记录的处理不需要等待资源则效率还不如正常模式
     *      （因为除去等待资源之外都需要一直使用 CPU，通过并发获取的效率提高不大）
     * 使用正常按行处理文件：
     *  内层占用低：每次只读取一行（当然也可以批量读取，但总体上比使用线程池的低）
     *  处理效率低：串行处理，如果每行记录处理过程中等待资源时间占比高，则会导致 CPU 利用率低，因此 CPU 利用率低，效率低
     *
     *  自测1：
     *  条件：5 亿行数据，每 50 w 耗费 1 s 的情况下（Thread.sleep(1000)）
     *  线程池：核心线程数为 10，总耗时 116s 左右
     *  普通方法：总耗时 1000s 左右
     *
     *  自测2：
     *  条件：5 亿行数据，每 500w 耗费指定时间的 CPU 空转（while 循环），相当于没有任何外部 IO，所有操作都需要 CPU 支持
     *  线程池：核心线程数为 10，总耗时 359s
     *  普通方法：总耗时 122s
     *
     *  总结：内存占用基本都没啥大问题，每行记录等待资源时间占高建议使用线程池，占比低建议使用正常模式（减少线程之间的切换，反而还提高了效率）
     */
    public static void dealBigFileByNormal() {
        /* 多线程处理文件 */
        Instant begin = Instant.now();
        FileDealUtil instance = FileDealUtil.getInstanceLineNum("D:\\test.txt", BATCH_NUM);
        AtomicInteger total = new AtomicInteger(0);
        instance.dealFileByNormal(val -> {
            total.getAndIncrement();
//            try {
//                Thread.sleep(10); // 假设每次处理 50000 行记录耗时 1 sec
//            } catch (InterruptedException e) {
//                System.out.println("线程睡眠异常");
//                throw new RuntimeException(e);
//            }
            long i = 0;
            while (i < 3000000000l) i++;
        });
        /* 获取执行之后的时间戳 */
        Instant end = Instant.now();
        long i = Duration.between(begin, end).getSeconds();
        System.out.println("多线程累计处理：" + total.get() + "批记录，总耗费时间（秒）：" + i);

        /* 常规处理 */
        Instant begin3 = Instant.now();
        int total3 = 0;
        try (BufferedReader fileReader = new BufferedReader(new FileReader("D:\\test.txt"))) {
            while (fileReader.readLine() != null) {
                /* 开始逻辑处理 */
                total3++;
                if ((total3 % 5000000) == 0) {
                    long i2 = 0;
                    while (i2 < 3000000000l) i2++;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("常规处理异常", e);
        }
        Instant end3 = Instant.now();
        long i3 = Duration.between(begin3, end3).getSeconds();
        System.out.println("常规处理：" + total3 + "批记录，总耗费时间（秒）：" + i3);
    }

    public static void main(String[] args) {
        dealBigFileByNormal();
    }
}
