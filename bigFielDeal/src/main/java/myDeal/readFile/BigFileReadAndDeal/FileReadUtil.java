package myDeal.readFile.BigFileReadAndDeal;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
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

    private static final Charset DEF_CHARSETS = StandardCharsets.UTF_8;

    /**
     * 每次 readDate 最大读取行数
     */
    private int MAX;

    /**
     * 文件路径
     */
    private String path;

    /**
     * 读取文件的字符编码
     */
    private Charset charsets;

    /**
     * 每个字符对应的字节数
     */
    private int baseLength;

    /**
     * 使用 BIO 对文件进行处理的流对象
     */
    private BufferedReader bufferedReader;


    /**
     * 使用 NIO 对文件进行处理的对象
     */
    private FileChannel channel;

    private ByteBuffer byteBuffer;

    private FileReadUtil(String path) throws FileNotFoundException {
        this(path, DEF_MAX);
    }

    private FileReadUtil(String path, int max) {
        this(path, max, DEF_CHARSETS);
    }

    private FileReadUtil(String path, Charset charsets) {
        this(path, DEF_MAX, charsets);
    }


    private FileReadUtil(String path, int max, Charset charsets) {
        MAX = max;
        this.path = path;
        this.charsets = charsets;
        this.baseLength = "t".getBytes(charsets).length;
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
     * 通过 BIO 从文件中获取内容
     */
    public List<String> readDataByNormal() throws IOException {
        if (bufferedReader == null) bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(path), charsets));
        List<String> list = new LinkedList<>();
        String line = "";
        while (list.size() < MAX && (line = bufferedReader.readLine()) != null) {
            list.add(line);
        }
        return list;
    }

    /**
     * 通过 NIO 从文件中获取内容
     * 暂时不使用 NIO 的方式读取文件，使用 NIO 的方式主要优势在需要处理多个文件，
     * 提高多个文件同时读取的能力，不适合大文件读取，因为如果存在多个大文件采用 NIO 的方式读取
     * 容易导致 OOM
     */
    public List<String> readDataByNIO() throws IOException {
        if (channel != null) {
            channel = FileChannel.open(Paths.get(path));
            byteBuffer = ByteBuffer.allocate(1024 * baseLength); // 读入 1024 个字符
            channel.read(byteBuffer);
        }
        List<String> res = new LinkedList<>();
        while (res.size() < MAX && byteBuffer.hasRemaining()) {

        }
        return res;
    }

    /**
     * 关闭文件流
     */
    public void close() {
        try {
            if (bufferedReader != null) bufferedReader.close();
            if (channel != null) channel.close();
            if (byteBuffer != null) byteBuffer.clear();
        } catch (Exception e) {
        }
    }
}
