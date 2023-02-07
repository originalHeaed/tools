package com.hundsun;

import gxpbridge.pub.BridgeDriver;
import gxpbridge.pub.VariableOperation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/*********************************************************
 * 文件名称：TestGxpBridge.java
 * 软件版权: 恒生电子股份有限公司
 * 模块名称：com.hundsun
 * 功能说明：
 * 开发人员 @author：wanggh31690
 * 开发时间 @date：2022/8/25 10:35
 * 修改记录：程序版本  修改日期  修改人员  修改单号  修改说明
 *********************************************************/
public class BridgeJava implements BridgeDriver {
    // private static final String RESULT_RECV = "19017";    表示接收失败
    // private static final String RESULT_UNKNOWN = "19000"; 表示未知错误
    private static final String RESULT_CONN = "19015";   // 表示连接失败
    private static final String RESULT_SENT = "19016";    // 表示发送失败
    private static final String RESULT_SUCC = "0";         // 表示成功

    private static final String IP_NAME = "bridgeIp"; // gxp 中配置项 ip 的 key
    private static final String PORT_NAME = "bridgePort"; // gxp 中配置项 port 的 key



    /**
     * 用于与桥接器通信的 socket
     */
    private Socket socket;

    private InputStreamReader reader;

    private BufferedReader bufferedReader;

    private OutputStreamWriter writer;

    private BufferedWriter bufferedWriter;

    /**
     * 桥接器 ip
     */
    private String bridgeIp;

    /**
     * 桥接器 port
     */
    private String bridgePort;

    /**
     * 初始化
     */
    @Override
    public boolean init(Properties properties) {
        System.out.println("调用 init 函数，开始初始化");
        try {
            /* 获取桥接器所在的 ip 与监听的 port */
            bridgeIp = properties.getProperty(IP_NAME);
            bridgePort = properties.getProperty(PORT_NAME);
            if (bridgeIp == null || bridgeIp == "") {
                System.out.println("桥接器 IP 不允许为空");
                throw new RuntimeException("桥接器 IP 不允许为空");
            }
            if (bridgePort == null || bridgePort == "") {
                System.out.println("桥接器监听的 port 不允许为空");
                throw new RuntimeException("桥接器监听的 port 不允许为空");
            }
            return this.initSocket();
        } catch (Exception e) {
            System.out.println("建立与桥接器连接的 socket 客户端失败");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * gxp 请求到 java 转发器之后，转发器将会调用该方法
     */
    @Override
    public PerformResult performCall(VariableOperation variableOperation, byte[] bytes) {
        String respCode = RESULT_SUCC;
        String res;
        System.out.println("收到 performCall 请求，开始发送至桥接器");
        try {
            /* 判断客户端 socket 是否正常 */
            if (socket == null || !socket.isConnected() || socket.isClosed()) {
                System.out.println("客户端 socket 对象未初始化或者断开连接，重新建立 socket 客户端");
                if (!initSocket()) {
                    respCode = RESULT_CONN;
                    res = "{建立与桥接器连接的 socket 客户端失败}";
                    PerformResult performResult = new PerformResult(Integer.parseInt(respCode), res.getBytes(StandardCharsets.UTF_8));
                    return performResult;
                }
            }
            /* 将数据发送至桥接器（{key}{content}） */
            String sb = new String(bytes, StandardCharsets.UTF_8);
            System.out.println("发送至服务端 socket 数据为：" + sb);
            socket.getOutputStream().write(sb.getBytes(StandardCharsets.UTF_8));
            socket.getOutputStream().flush();
            /*  读入桥接器返回的数据 */
            res = blockAndReadRes();
            System.out.println("收到返回的信息：" + res);
        } catch (Exception e) {
            e.printStackTrace();
            respCode = RESULT_SENT;
            res = "{与桥接器通信失败，断开与服务端连接的 socket}";
            if (socket != null && socket.isConnected() && !socket.isClosed()) {
                try {
                    bufferedWriter.close();
                    writer.close();
                    bufferedReader.close();
                    reader.close();
                    socket.close();
                } catch (IOException ex) {
                    System.out.println("关闭流或客户端 socket 失败");
                    ex.printStackTrace();
                }
            }
        }
        PerformResult performResult = new PerformResult(Integer.parseInt(respCode), res.getBytes(StandardCharsets.UTF_8));
        return performResult;
    }


    @Override
    public boolean fini() {
        System.out.println("开始关闭与桥接器连接的 socket 客户端");
        if (socket != null && socket.isConnected() && !socket.isClosed()) {
            try {
                bufferedWriter.close();
                writer.close();
                bufferedReader.close();
                reader.close();
                socket.close();
            } catch (IOException e) {
                System.out.println("fini 方法中关闭与桥接器连接的 socket 客户端失败");
                e.printStackTrace();
                return false;
            }
        } else {
            System.out.println("与桥接器连接的 socket 客户端已关闭，无需再次关闭");
        }
        return true;
    }


    /**
     * 初始化 socket
     */
    private boolean initSocket() {
        try {
            System.out.println("建立与 ip：" + bridgeIp + ":" + bridgePort + "连接的 socket 客户端");
            socket = new Socket(bridgeIp, Integer.parseInt(bridgePort));
            socket.setSoTimeout(30000); // 超时时间 30s
            reader = new InputStreamReader(this.socket.getInputStream(), StandardCharsets.UTF_8);
            bufferedReader = new BufferedReader(reader);
            writer = new OutputStreamWriter(this.socket.getOutputStream(), StandardCharsets.UTF_8);
            bufferedWriter = new BufferedWriter(writer);
            return true;
        } catch (Exception e) {
            System.out.println("建立与桥接器连接的 socket 客户端失败");
            e.printStackTrace();
            try {
                if (bufferedWriter != null) {
                    bufferedWriter.close();
                }
                if (writer != null) {
                    writer.close();
                }
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
                if (reader != null) {
                    reader.close();
                }
                if (socket != null && socket.isConnected() && !socket.isClosed()) {
                    socket.close();
                }
            } catch (Exception exception) {
                System.out.println("关闭流或 socket 失败");
                e.printStackTrace();
            }
            return false;
        }
    }

    /**
     * 阻塞读取第一个字符
     * @throws IOException
     * @throws InterruptedException
     */
    private String blockAndReadRes() {
        try {
            /* 阻塞等待数据 */
            while (!bufferedReader.ready()) {
                Thread.sleep(10);
            }
            int first = bufferedReader.read();
            if ('{' != first) {
                System.out.println("银行返回的 json 数据中第一个字符非 {");
                throw new RuntimeException("gxp socket 传递数据中第一个字符非 {");
            }
            /* 按照字符为单位读取数据 */
            int count = 1;
            StringBuilder sb = new StringBuilder();
            sb.append('{');
            do {
                int c = bufferedReader.read(); // 读入一个字符
                if ('{' == c) count++;
                else if ('}' == c) count--;
                sb.append((char) c);
            } while (count != 0);
            return sb.toString();
        } catch (Exception e) {
            System.out.println("阻塞并读取银行返回的数据失败");
            e.printStackTrace();
            return "{阻塞并读取银行返回的数据失败}";
        }
    }
    //
    // public static void main(String[] args) {
    //     for (int i = 0; i < 4; i++) {
    //         Thread thread = new Thread(new Runnable() {
    //             @Override
    //             public void run() {
    //                 System.out.println(Thread.currentThread());
    //                 Properties properties = new Properties();
    //                 properties.put("bridgeIp", "127.0.0.1");
    //                 properties.put("bridgePort", "8080");
    //                 BridgeJava bridgeJava = new BridgeJava();
    //                 bridgeJava.init(properties);
    //                 for (int i = 0; i < 5; i++) {
    //                     PerformResult performResult = bridgeJava.performCall(new VariableOperation() {
    //                         @Override
    //                         public String getStringValue(String s) throws Exception {
    //                             return null;
    //                         }
    //                         @Override
    //                         public String getStringValue(int i) {
    //                             return null;
    //                         }
    //                         @Override
    //                         public String getStringValue(String s, String s1) throws Exception {
    //                             return null;
    //                         }
    //                         @Override
    //                         public byte[] getValue(String s) throws Exception {
    //                             return new byte[0];
    //                         }
    //                     }, "{\"test\":wang}".getBytes(StandardCharsets.UTF_8));
    //                     try {
    //                         Thread.sleep(1000);
    //                     } catch (InterruptedException e) {
    //                         e.printStackTrace();
    //                     }
    //                 }
    //                 bridgeJava.fini();
    //             }
    //         });
    //         thread.start();
    //     }
    // }
}
