package myDeal.readFile;

/**
 * 文件处理工具类
 */
public class FileDealUtil {
    /**
     * 在多线程处理下最大同时处理的线程数
     */
    private int segement = 10;

    /**
     * 文件路径
     */
    private String FilePath;

    public static FileDealUtil getInstance(String filePath) {
        return new FileDealUtil();
    }
}
