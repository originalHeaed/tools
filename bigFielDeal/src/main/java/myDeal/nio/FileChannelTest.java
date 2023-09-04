package myDeal.nio;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * FileChannel 的使用与测试
 */
public class FileChannelTest {
    public static void main(String[] args) throws IOException, InterruptedException {
        /* 1. FileChannel 创建 */
        /* 通过 FileChannel 创建一个可以读写的 channel，如果文件不存在会创建一个新的文件 */
        FileChannel open = FileChannel.open(Paths.get("D:\\test.txt"), StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE);
        /* 通过 FileInputStream 创建一个 FileChannel（只能读，不能写） */
        FileInputStream fileInputStream = new FileInputStream("D:\\test.txt");
        FileChannel channel = fileInputStream.getChannel();
        /* 通过 FileOutputStream 创建一个 FileChannel（不能读，只能写） */
        FileOutputStream fileOutputStream = new FileOutputStream("D:\\test.txt");
        FileChannel channel1 = fileOutputStream.getChannel();
        /* 通过 RandomAccessFile 创建一个可以读写的 FileChannel */
        RandomAccessFile randomAccessFile = new RandomAccessFile("D:\\test.txt", "rw");

        /* 2. 将数据写入 ByteBuffer 中，然后通过 channel 写入到文件中 */
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.put("你好啊".getBytes());
        byteBuffer.flip();
        open.write(byteBuffer);
        open.force(false); // 将文件改动强制写入底层（防止数据丢失）
        /* 3. 将数据从 channel 中读取到 ByteBuffer 中 */
        byteBuffer.clear();
        open.read(byteBuffer, 0);
        byteBuffer.flip();
        byte[] readRes = new byte[byteBuffer.limit()];
        int i = 0;
        while (byteBuffer.hasRemaining()) readRes[i++] = byteBuffer.get();
        System.out.println("读取完成：" + new String(readRes));
        /* 4. 关闭通道 */
        open.close(); // 关闭通道
    }
}
