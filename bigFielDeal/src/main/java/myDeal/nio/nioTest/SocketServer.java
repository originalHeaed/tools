package myDeal.nio.nioTest;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class SocketServer {

    /**
     * 多路复用器
     */
    private Selector selector;

    /**
     * 监听器
     */
    private ServerSocketChannel socketChannel;

    private int port;

    public SocketServer() throws IOException {
        try {
            port = 1234;
            selector = Selector.open();
            socketChannel = ServerSocketChannel.open();
            socketChannel.bind(new InetSocketAddress(port));
            socketChannel.configureBlocking(false);
            socketChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (Exception e) {
            System.out.println("初始化端口监听失败");
            System.out.println(e);
        }
    }

    /**
     * 启用一个线程来监听处理来自客户端的事件
     */
    public void startListen() throws IOException {
        /* 启用一个线程来轮询 selector */
        new Thread(() -> {
            try {
                while (selector.isOpen()) {
                    if (selector.select() > 0) {
                        /* 处理所有已经准备好的 channel */
                        selector.selectedKeys().stream().forEach(ele -> {
                            try {
                                if (ele.isAcceptable()) {
                                    dealAccept(ele);
                                } else if (ele.isReadable()) {
                                    dealRead(ele);
                                } else if (ele.isWritable()) {
                                    dealWrite(ele);
                                }
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        });
                        selector.selectedKeys().clear(); // 将所有事件从 selectedContain 中移除
                    }

                }
            } catch (Exception e) {
                throw new RuntimeException("多路复用处理失败", e);
            }
        }).start();
    }

    /**
     * 处理接受客户端请求的事件
     * @param selectionKey
     */
    private void dealAccept(SelectionKey selectionKey) throws IOException {
        System.out.println("客户端发来请求，建立连接，将连接加入到 Select 中监听");
        SocketChannel channel = ((ServerSocketChannel) selectionKey.channel()).accept();
        //  A channel must be placed into non-blocking mode before being registered with a selector, otherwise will throw IllegalBlockingModeException
        channel.configureBlocking(false);
        channel.register(selector, SelectionKey.OP_READ); // 接收到客户端请求后，建立一个等待读取事件的 通道
    }

    /**
     * 处理接受客户端请求的事件
     * @param ele
     */
    private void dealRead(SelectionKey ele) throws IOException {
        System.out.println(ele + "：读取客户端请求数据");
        SocketChannel channel = (SocketChannel) ele.channel();
        /* 处理该 socket 已经客户收到来自客户端的消息 */
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        channel.read(byteBuffer);
        byteBuffer.flip();
        byte[] bytes = new byte[byteBuffer.limit()];
        byteBuffer.get(bytes);
        System.out.println(ele + "：收到消息：" + new String(bytes));
        /* 将数据写回客户端 */
        ele.interestOps(SelectionKey.OP_WRITE);
        ele.attach("来，给你响应");
    }

    private void dealWrite(SelectionKey ele) throws IOException {
        String attachment = (String) ele.attachment();
        System.out.println(ele + "：返回客户端响应");
        System.out.println(ele + "：返回内容：" + attachment);
        SocketChannel channel = (SocketChannel) ele.channel();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.put(attachment.getBytes());
        byteBuffer.flip();
        channel.write(byteBuffer);
        /* 写完后一定要注意注销写事件，不然会一直写 */
        ele.interestOps(SelectionKey.OP_READ);
        /* 关闭该 channel */
        channel.close();
        ele.cancel(); // 使用  SelectionKey 不会将服务器的资源释放，仍然需要使用对应的 close 方法
    }

    /**
     * 释放资源
     */
    public void close() {
        try {
            if (selector != null) selector.close();
            if (socketChannel != null) socketChannel.close();
        } catch (Exception e) {
            System.out.println("关闭资源失败");
        }
    }
}
