package myDeal.writeFile;

import sun.nio.ch.DirectBuffer;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

/**
 * 使用 JAVA NIO 直接映射写文件
 * 优点：文件从磁盘 -> 系统内层（普通 IO 是磁盘 -> 内核缓存 -> 系统内存），提高了文件的效率
 * 缺点：使用复杂，回收复杂
 */
public class MappedBufferWriteUtil {
    /**
     * 内存映射对象
     */
    private MappedByteBuffer mappedByteBuffer;

    /**
     * 默认映射大小 1G（不能超过 int 的最大值）（单位是字节）
     */
    private final long DEF_SIZE = 1073741824;

    /**
     * 已经写入的字节数
     */
    private long used;

    /**
     * 写入文件的编码
     */
    private Charset charset;

    /**
     * 文件路径
     */
    private String path;

    private MappedBufferWriteUtil(String path, Charset charset) throws IOException {
        used = 0;
        this.charset = charset;
        this.path = path;
    }

    /**
     * 获取一个与指定文件关联的内层映射写对象
     * @param path 文件路径
     * @return
     */
    public static MappedBufferWriteUtil GetMappedBufferWrite(String path) throws IOException {
        return new MappedBufferWriteUtil(path, StandardCharsets.UTF_8);
    }

    /**
     * 获取一个与指定文件关联的内层映射写对象
     * @param path 文件路径
     * @return
     */
    public static MappedBufferWriteUtil GetMappedBufferWrite(String path, String charSet) throws IOException {
        return new MappedBufferWriteUtil(path, Charset.forName(charSet));
    }

    /**
     * 向文件中写入数据（每次写都会创建一个内存映射区域，因此建议每次 content 的长度尽可能大，减少调用 write 的次数）
     * @param content
     */
    public void write(String content) {
        /* 将数据写入到直接存中（不会增加堆的内存使用率） */
        byte[] bytes = content.getBytes(charset);
        try {
            FileChannel open = new RandomAccessFile(path, "rw").getChannel();
//            FileChannel open = FileChannel.open(Paths.get(path), StandardOpenOption.WRITE, StandardOpenOption.READ);
            mappedByteBuffer = open.map(FileChannel.MapMode.READ_WRITE, used, bytes.length);
            open.close(); // 关闭通道对 mapped 无影响
        } catch (Exception e) {
            System.out.println(e);
            System.out.println("写入文件失败");
        }
        mappedByteBuffer.put(bytes); // 将数据写入直接内存
        used += bytes.length;
        this.close(); // 关闭
    }

    /**
     * 关闭资源
     * 使用 MappedByteBuffer 的缺点是将数据缓存在直接内存中，会消耗系统内层，且回收不稳定
     */
    public void close() {
        mappedByteBuffer.force();
        /* 释放磁盘文件与内存的映射 */
        ((DirectBuffer)mappedByteBuffer).cleaner().clear();
    }
}
