package myDeal.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Set;

/**
 * Selector 测试
 */
public class SelectorTest {
    public static void main(String[] args) throws IOException {
        /* 创建 Selector 的方法 */
        Selector selector = Selector.open(); // 系统提供的默认 selector

        /* 将 Channel 注册到 Selector 中 */
        SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", Integer.parseInt("1234")));
        socketChannel.register(selector, SelectionKey.OP_READ, SelectionKey.OP_WRITE); // 将 channel 注册到 selector 中并指定需要监听的事件

        /* 获取处于就绪的 Channel 并处理 */
        int select = selector.select();
        while (select > 0) {
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            /* 处理已经就绪的 channel */
            for (SelectionKey ele : selectionKeys) {
                SelectableChannel channel = ele.channel();
//                .....
            }
        }
        /* 关闭 selector */
        selector.close();
    }
}
