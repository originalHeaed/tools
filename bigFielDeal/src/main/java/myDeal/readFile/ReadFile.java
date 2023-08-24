package myDeal.readFile;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.List;

/**
 * 正常文件的读取
 */
public class ReadFile {
    /**
     * 使用 guava 读取文件
     * @throws IOException
     */
    public void readByGuava() throws IOException {
        String path = "D:\\test.txt";
        List<String> list = Files.readLines(new File(path), Charsets.UTF_8);
    }

    /**
     * 通过 Apache Common IO 读取文件
     */
    public void readByApacheIO() throws IOException {
        String path = "D:\\test.txt";
        /* 一次性读取所有的内容 */
        FileUtils.readLines(new File(path), Charsets.UTF_8);
        /* 按行读取内容 */
        LineIterator lineIterator = FileUtils.lineIterator(new File(path), String.valueOf(Charsets.UTF_8));
        lineIterator.nextLine();
    }

    /**
     * 通过 BufferReader 读取文件（按行）
     */
    public void readByJAVA() {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader("D:\\test.txt"))) {
            String s = bufferedReader.readLine();
        } catch (Exception e) {

        }
    }

    public static void main(String[] args) throws IOException {
        FileChannel channel = FileChannel.open(Paths.get("D:\\test4.txt"));
        byte[] base = new byte["3".getBytes(StandardCharsets.UTF_8).length];
        ByteBuffer byteBuffer = ByteBuffer.allocate("3".getBytes(StandardCharsets.UTF_8).length * 3);
        channel.read(byteBuffer);
        byteBuffer.flip();
        byteBuffer.get(base);
        System.out.println(new String(base, StandardCharsets.UTF_8));
        channel.read(byteBuffer);
        byteBuffer.get(base);
        System.out.println(new String(base, StandardCharsets.UTF_8));
        channel.close();
        byteBuffer.clear();
    }
}
