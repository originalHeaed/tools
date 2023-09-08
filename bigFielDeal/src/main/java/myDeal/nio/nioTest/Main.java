package myDeal.nio.nioTest;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        SocketServer socketServer = new SocketServer();
        socketServer.startListen();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("程序结束，释放资源");
            socketServer.close();
        }));
    }
}
