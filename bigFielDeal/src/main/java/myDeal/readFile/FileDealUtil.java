package myDeal.readFile;

import java.time.Instant;
import java.util.List;
import java.util.function.Consumer;

/**
 * 文件处理工具类
 */
public class FileDealUtil {

    /**
     * 默认的线程并发数
     */
    private static int DEF_SEGEMENT = 10;

    /**
     * 每次读取的文件行数
     */
    private static int DEF_LINE_NUM = 50;

    /**
     * 线程工具类
     */
    private ThreadUtil threadUtil;


    /**
     * 文件工具类
     */
    private FileReadUtil fileReadUtil;

    private FileDealUtil(String filePath, int segement, int dealLineNum) {
        try {
            fileReadUtil = FileReadUtil.getInstance(filePath, dealLineNum);
        } catch (Exception e) {
            throw new RuntimeException("创建文件读取对象失败", e);
        }
        threadUtil = ThreadUtil.getThreadUtil(segement);
    }

    private FileDealUtil(String filePath) {
        this(filePath, DEF_SEGEMENT, DEF_LINE_NUM);
    }

    /**
     * 创建文件处理对象，不指定最大线程数
     */
    public static FileDealUtil getInstance(String filePath) {
        return new FileDealUtil(filePath);
    }

    /**
     * 创建文件处理对象，指定最大线程数
     */
    public static FileDealUtil getInstance(String filePath, int segement) {
        return new FileDealUtil(filePath, segement, DEF_LINE_NUM);
    }

    /**
     * 传入每行数据的处理规则，然后开始多线程处理文件内容
     */
    public void dealFile(Consumer<List<String>> consumer) {
        /* 获取执行前的时间戳 */
        Instant begin = Instant.now();
        /* 读文件并将内容加入到线程池中执行，如果线程池满了会阻塞等待 */
        try {
            List<String> list;
            while ((list = fileReadUtil.readData()).size() > 0) {
                List<String> tasklist = list;
                if (tasklist.size() > 0) threadUtil.addTask(() -> consumer.accept(tasklist));
            }
        } catch (Exception e) {
            throw new RuntimeException("处理文件失败", e);
        } finally {
            threadUtil.close();
            fileReadUtil.close();
        }
        /* 获取执行之后的时间戳 */
        Instant end = Instant.now();
        int i = end.getNano() - begin.getNano();
        System.out.println("耗费时间（纳秒）：" + i);
    }
}
