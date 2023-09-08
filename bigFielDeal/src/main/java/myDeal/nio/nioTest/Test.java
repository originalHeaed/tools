package myDeal.nio.nioTest;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Test {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", 1234);
        OutputStream outputStream = socket.getOutputStream();
        outputStream.write("滴滴滴哒".getBytes());
        outputStream.flush();

        InputStream inputStream = socket.getInputStream();
        byte[] bytes = new byte["来，给你响应".getBytes().length];
        inputStream.read(bytes);
        System.out.println("收到服务端响应：" + new String(bytes));
        socket.close();
    }
}
