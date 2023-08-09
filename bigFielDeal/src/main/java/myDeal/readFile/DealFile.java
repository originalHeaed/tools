package myDeal.readFile;

/**
 * 文件处理
 */
@FunctionalInterface
public interface DealFile {
    /**
     * 按行处理文件
     * @param line
     */
    void dealFile(String line);
}
