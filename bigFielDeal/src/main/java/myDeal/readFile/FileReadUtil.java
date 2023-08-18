package myDeal.readFile;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

/**
 * 从文件中读取数据的集合类
 */
public class FileReadUtil {
    /**
     * 每次读取最大行数
     */
    private static final int DEF_MAX = 50;

    /**
     * 每次 readDate 最大读取行数
     */
    private int MAX;

    private FileReader reader;
    private BufferedReader bufferedReader;

    private FileReadUtil(String path) throws FileNotFoundException {
        this(path, DEF_MAX);
    }

    private FileReadUtil(String path, int max) throws FileNotFoundException {
        reader = new FileReader(path);
        bufferedReader = new BufferedReader(reader, 1);
        MAX = max;
    }

    /**
     * 创建一个文件读取对象，每次最多从文件中读取 50 条记录
     */
    public static FileReadUtil getInstance(String path) throws FileNotFoundException {
        return new FileReadUtil(path);
    }

    /**
     * 创建一个文件读取对象，每次最多从文件中读取 max 条记录
     */
    public static FileReadUtil getInstance(String path, int max) throws FileNotFoundException {
        return new FileReadUtil(path, max);
    }

    /**
     * 从文件中读取指定条数的记录
     */
    public List<String> readData() throws IOException {
        List<String> list = new LinkedList<>();
        String line = "";
        while (list.size() < MAX && (line = bufferedReader.readLine()) != null) {
            list.add(line);
        }
        return list;
    }

    /**
     * 关闭文件流
     */
    public void close() {
        try {
            if (reader != null) reader.close();
            if (bufferedReader != null) bufferedReader.close();
        } catch (Exception e) {
        }
    }
}
