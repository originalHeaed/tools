package myDeal.readFile;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.time.Instant;
import java.time.temporal.ChronoField;
import java.util.concurrent.atomic.AtomicInteger;

public class MyTestDealBigFile {

    public static final int BATCH_NUM = 1;

    public static void main(String[] args) throws FileNotFoundException {
        /* 多线程处理文件 */
        Instant begin = Instant.now();
        FileDealUtil instance = FileDealUtil.getInstanceLineNum("D:\\test.txt", BATCH_NUM);
        AtomicInteger total = new AtomicInteger();
        instance.dealFile(val -> {
            //try {
//                Thread.sleep(1000); // 假设每次处理 50000 行记录耗时 1 sec
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
            total.getAndIncrement();
        });
        /* 获取执行之后的时间戳 */
        Instant end = Instant.now();
        int i = begin.get(ChronoField.MILLI_OF_SECOND) - end.get(ChronoField.MILLI_OF_SECOND);
        System.out.println("多线程累计处理：" + total.get() + "批记录，总耗费时间（豪秒）：" + i);

        /* 单线程 */
        Instant begin2 = Instant.now();
        FileDealUtil instance2 = FileDealUtil.getInstance("D:\\test.txt", 1, BATCH_NUM);
        AtomicInteger total2 = new AtomicInteger();
        instance2.dealFile(val -> {
//            try {
//                Thread.sleep(1000); // 假设每次处理 50000 行记录耗时 1 sec
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
            total2.getAndIncrement();
        });
        /* 获取执行之后的时间戳 */
        Instant end2 = Instant.now();
        int i2 = begin2.get(ChronoField.MILLI_OF_SECOND) - end2.get(ChronoField.MILLI_OF_SECOND);
        System.out.println("单线程累计处理：" + total2.get() + "批记录，总耗费时间（豪秒）：" + i2);

        /* 常规处理 */
        Instant begin3 = Instant.now();
        int total3 = 0;
        try (BufferedReader fileReader = new BufferedReader(new FileReader("D:\\test.txt"))) {
            fileReader.readLine();
            /* 开始逻辑处理 */
            total3++;
        } catch (Exception e) {
            throw new RuntimeException("常规处理异常", e);
        }
        Instant end3 = Instant.now();
        int i3 = begin3.get(ChronoField.MILLI_OF_SECOND) - end3.get(ChronoField.MILLI_OF_SECOND);
        System.out.println("常规处理：" + total3 + "批记录，总耗费时间（豪秒）：" + i3);

    }
}
