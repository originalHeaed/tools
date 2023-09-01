package myDeal.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * ServerSocketChannel 的使用与测试
 */
public class ServerSocketChannelTest {
    public static void main(String[] args) throws IOException {
        /* 建立 ServerSocketChannel */
        ServerSocketChannel open = ServerSocketChannel.open();
        open.bind(new InetSocketAddress(1234)); // 绑定 1234 端口
        /* 阻塞知道有 socket 请求到本机的 1234 端口 */
        SocketChannel server1 = open.accept();
        /* 使用 SocketChannel 读取数据 */
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.clear();
        server1.read(byteBuffer);
        byteBuffer.flip();
        byte[] res = new byte[byteBuffer.limit()];
        int index = 0;
        while (byteBuffer.hasRemaining()) res[index++] = byteBuffer.get();
        System.out.println("收到消息：" + new String(res));
        /* 使用 SocketChannel 写数据 */
        byteBuffer.clear();
        byteBuffer.put("啊哈哈".getBytes());
        byteBuffer.flip();
        server1.write(byteBuffer);
        System.out.println("响应结束");
        /* 释放资源 */
        server1.finishConnect();
        open.close();
    }
}
