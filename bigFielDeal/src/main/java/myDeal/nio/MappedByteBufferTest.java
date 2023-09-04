package myDeal.nio;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * 测试 MappedByteBuffer
 */
public class MappedByteBufferTest {
    public static void main(String[] args) throws IOException {
        /* 1. MappedByteBuffer 的创建 */
        /* StandardOpenOption: JAVA NIO 文件通道建立的方式（通道可以只支持读、只支持写、支持读写、支持文件不存在时自动创建等） */
        FileChannel open = FileChannel.open(Paths.get("D:\\test.txt"), StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE);
        // 创建一个大小为 1024 字节的内存缓冲区，用来直接映射文件，映射的起始点为文件的开头
        MappedByteBuffer map = open.map(FileChannel.MapMode.READ_WRITE, 0, 1024);
        // A mapping, once established, is not dependent upon the file channel that was used to create it. Closing the channel, in particular, has no effect upon the validity of the mapping
        open.close();
        /* 2. 写文件 */
        map.put("zzz".getBytes());
        map.flip();
        map.force(); // 将直接内层中的内容刷盘
        /* 3. 读文件 */
        byte[] b = new byte["zzz".getBytes().length];
        map.clear();
        map.get(b);
        System.out.println("读取内容：" + new String(b));
    }
}
