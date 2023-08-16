package myDeal.readFile;

import java.time.Instant;
import java.util.function.Consumer;

/**
 * 文件处理工具类
 */
public class FileDealUtil {

    /**
     * 默认的线程并发数
     */
    private static int DEF_SEGEMENT = 10;

    private ThreadUtil threadUtil;


    /**
     * 文件路径
     */
    private FileReadUtil fileReadUtil;

    private FileDealUtil(String filePath, int segement) {
        try {
            fileReadUtil = FileReadUtil.getInstance(filePath);
        } catch (Exception e) {
            throw new RuntimeException("创建文件读取对象失败", e);
        }
        threadUtil = ThreadUtil.getThreadUtil(segement);
    }

    private FileDealUtil(String filePath) {
        this(filePath, DEF_SEGEMENT);
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
        return new FileDealUtil(filePath, segement);
    }

    /**
     * 传入每行数据的处理规则，然后开始多线程处理文件内容
     */
    public void dealFile(Consumer<String> consumer) {
        /* 获取执行前的时间戳 */
        Instant begin = Instant.now();
        /* 开始读取文件内容 */
        /* 获取执行之后的时间戳 */
        Instant end = Instant.now();
        int i = end.getNano() - begin.getNano();
        System.out.println("耗费时间（纳秒）：" + i);
    }
}
