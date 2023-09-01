package myDeal.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * SocketChannel 的使用与测试
 */
public class SocketChannelTest {
    public static void main(String[] args) throws IOException {
        /* 1. 创建一个 SocketChannel 对象 */
        SocketChannel socketChannel = SocketChannel.open();
        /* 2. 与远程建立连接 */
        socketChannel.connect(new InetSocketAddress("localhost", 1234));
        /* 3. 写入数据 */
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.clear();
        byteBuffer.put("你好啊".getBytes());
        byteBuffer.flip();
        socketChannel.write(byteBuffer);
        /* 4. 读数据 */
        byteBuffer.clear();
        socketChannel.read(byteBuffer);
        byteBuffer.flip();
        byte[] res = new byte[byteBuffer.limit()];
        int i = 0;
        while (byteBuffer.hasRemaining()) res[i++] = byteBuffer.get();
        System.out.println("收到回信：" + new String(res));
        /* 5. 关闭 */
        socketChannel.shutdownInput();
        socketChannel.shutdownOutput();
        socketChannel.finishConnect();
    }
}
