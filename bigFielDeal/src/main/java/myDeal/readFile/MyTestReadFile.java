package myDeal.readFile;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class MyTestReadFile {
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

    public static void main(String[] args) {
        Instant now = Instant.now();
        long i = 0;
        while (i < 3000000000l) {
            i++;
        }
        Instant now2 = Instant.now();
        System.out.println(Duration.between(now, now2).getSeconds());
    }
}
