package myDeal.writeFile;

import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;

public class MyTest {

    /**
     * 需要写入的数据行数
     */
    private static int time = 100000000;

    /**
     * 使用 bufferWrite 每隔批的记录数
     */
    private static int bufferSize = 500000;

    /**
     * 使用内层映射每次写入的记录数
     */
    private static int mappedBufferSize = 50000000;

    /**
     * 三种写入方式：
     * （1）使用传统的 FileOutputStream
     * （2）使用带缓冲区的 BufferWriter（和缓冲区大小有关，缓冲区越大，效率会相对来说更高）
     * （3）使用 JAVA NIO 的内存映射（写入总数据量不变的情况下，每次映射的内存空间越大写入的次数越少，效率越高）
     * 在纯写入的情况下，写入的数据量为 0.5g，三种方法的耗时如下：
     * （1）2s
     * （2）3s
     * （3）1s
     * 在纯写入的情况下，写入的数据量为 5.5g，三种方法的耗时如下：
     * （1）19s
     * （2）88s
     * （3）24s
     *
     * 不知道是啥问题使用 MappedByteBuffer 的效率始终没办法比上 BufferWriter
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        BufferWriteUtil bufferWriteUtil = BufferWriteUtil.getBufferWriteUtil("D:\\myTarget1.txt");
        /* 使用 BufferWriter 向文件中写入数据 */
        Instant instant = Instant.now();
        List<String> list = new LinkedList<>();
        for (int i = 1; i <= time; i++) {
            list.add("tetetetetet\n");
            if (i % bufferSize == 0 || i == time) {
                bufferWriteUtil.write(list);
                list = new LinkedList<>();
            }
        }
        bufferWriteUtil.close();
        Instant instant1 = Instant.now();
        System.out.println("使用 BufferWriter 写入数据时间：" + Duration.between(instant, instant1).getSeconds());
        /* 使用 JAVA NIO 内存映射写数据 */
        MappedBufferWriteUtil mappedBufferWriteUtil = MappedBufferWriteUtil.GetMappedBufferWrite("D:\\myTarget2.txt");
        instant = Instant.now();
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= time; i++) {
            sb.append("tetetetetet\n");
            if (i % mappedBufferSize == 0 || i == time) {
                mappedBufferWriteUtil.write(sb.toString());
                sb = new StringBuilder();
            }
        }
        instant1 = Instant.now();
        System.out.println("使用 JAVA NIO 内存映射写入数据时间：" + Duration.between(instant, instant1).getSeconds());
        /* 使用传统 IO 方式（无缓冲区，无内存映射） */
        instant = Instant.now();
        try (
                FileWriter fileOutputStream = new FileWriter("D:\\myTarget3.txt");
        ) {
            for (int i = 0; i < time; i++) {
                fileOutputStream.write("tetetetetet\n");
            }
        } catch (Exception e) {
            System.out.println("使用传统方式写数据失败");
        }
        instant1 = Instant.now();
        System.out.println("使用传统方式写入数据时间：" + Duration.between(instant, instant1).getSeconds());
    }
}
