package com.hundsun.socket;

import com.hundsun.util.CommonUtil;
import com.hundsun.util.ConfigUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.ServerSocket;
import java.net.Socket;

/*********************************************************
 * 文件名称：MainSocket.java
 * 软件版权: 恒生电子股份有限公司
 * 模块名称：com.hundsun.socket
 * 功能说明：监听并获取来自 gxp 信息，将获取信息通过 https 发送给银行，
 *          并将银行返回信息通过 socket 发送给 gxp
 * 开发人员 @author：wanggh31690
 * 开发时间 @date：2022/8/23 15:09
 * 修改记录：程序版本  修改日期  修改人员  修改单号  修改说明
 *********************************************************/
public class MainSocket extends Thread{

    /**
     * 是否监听并接受 gxp 请求
     */
    public static boolean isListening = true;

    /**
     * 日志对象
     */
    private static Logger log = LoggerFactory.getLogger(MainSocket.class);


    /**
     * 线程执行的方法
     */
    public void run(){
        ServerSocket serverSocket = null;
        int port = Integer.parseInt(ConfigUtil.getConfigVal("listenPort"));
        try {
            serverSocket = new ServerSocket(port);	//侦听交易所端口
            /* 死循环，接受来自指定端口的信息 */
            while (isListening) {
                try {
                    Socket socket = serverSocket.accept();
                    if(socket!=null && socket.isConnected() && (!socket.isClosed())) {
                        log.debug("建立socket连接-----------" + socket.getRemoteSocketAddress());
                        DealSocket dealSocket = new DealSocket(socket);
                        dealSocket.start();
                    }
                } catch (Exception e) {
                    log.error("接受并处理来端口{}的信息失败");
                    log.error(CommonUtil.getMessage(e));
                }
            }
        } catch (Exception e) {
            log.error("启动端口{} 监听来自gxp信息的 socket 失败", port);
            log.error(CommonUtil.getMessage(e));
            throw new RuntimeException();
        }
    }
}
