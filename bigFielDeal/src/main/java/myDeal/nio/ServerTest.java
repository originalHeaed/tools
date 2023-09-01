package myDeal.nio;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerTest {
    public static void main(String[] args) throws IOException {
        byte[] bytes = new byte["你好啊".getBytes().length];
        ServerSocket serverSocket = new ServerSocket(1234);
        Socket accept = serverSocket.accept();
        InputStream inputStream = accept.getInputStream();
        OutputStream outputStream = accept.getOutputStream();
        inputStream.read(bytes);
        System.out.println("收到消息：" + new String(bytes));
        outputStream.write("啊哈哈".getBytes());
        inputStream.close();
        outputStream.close();
        accept.close();
    }
}
