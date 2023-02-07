package com.hundsun.socket;

import com.hundsun.dto.ResponseEntity;
import com.hundsun.exception.MyRuntimeException;
import com.hundsun.myconst.MyConst;
import com.hundsun.myconst.MyErrorEnum;
import com.hundsun.util.CommonUtil;
import com.hundsun.util.ConfigUtil;
import com.hundsun.util.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/*********************************************************
 * 文件名称：DealSocket.java
 * 软件版权: 恒生电子股份有限公司
 * 模块名称：com.hundsun.socket
 * 功能说明：处理每个客户端请求
 * 开发人员 @author：wanggh31690
 * 开发时间 @date：2022/8/23 15:50
 * 修改记录：程序版本  修改日期  修改人员  修改单号  修改说明
 *********************************************************/
public class DealSocket extends Thread {

    /**
     * 日志对象
     */
    private static Logger log = LoggerFactory.getLogger(DealSocket.class);

    /**
     * 处理本次消息的 socket 对象
     */
    private Socket socket = null;

    private InputStreamReader reader;

    private BufferedReader bufferedReader;

    private OutputStreamWriter writer;

    private BufferedWriter bufferedWriter;

    public DealSocket(Socket socket) throws IOException {
        if(socket == null){
            log.error("构造时 socket 不能为空");
            throw new RuntimeException("构造时 socket 不能为空");
        }
        this.socket = socket;
        try {
            reader = new InputStreamReader(this.socket.getInputStream(), ConfigUtil.getConfigVal(MyConst.JSON_ENCODING));
            bufferedReader = new BufferedReader(reader);
            writer = new OutputStreamWriter(socket.getOutputStream(), ConfigUtil.getConfigVal(MyConst.JSON_ENCODING));
            bufferedWriter = new BufferedWriter(writer);
        } catch (Exception e) {
            log.error("使用 sokcet 输入输出流构建字符输入输出流失败");
            throw e;
        }
    }

    /**
     * 线程执行的方法
     */
    public void run() {
        Thread thread = Thread.currentThread();
        log.info("当前线程名称为：" + thread.getName() + "," + "socke 为" + socket);
        try {
            while (socket!=null && socket.isConnected() && (!socket.isClosed())) {
                log.debug("阻塞 socket 准备读取数据...");
                blockAndReadFirst();
                log.debug("收到消息，开始读数据");
                String reqJson = readJson();
                log.debug("读入 socket 发送过来的数据成功，数据内容为：{}", reqJson);
                log.debug("开始发送 https");
                ResponseEntity response = sendHttpsAndGetRes(reqJson);
                log.info("将数据:{}返回给 socket 客户端", response.getRespMsg());
                writeJson(response.getRespMsg());
            }
            log.info("与 socket 连接断开", socket);
        } catch (Exception e) {
            log.error("处理 gxp socket请求异常，socket 异常断开");
            log.error(CommonUtil.getMessage(e));
        } finally {
            try {
                bufferedReader.close();
                bufferedWriter.close();
                reader.close();
                writer.close();
            } catch (Exception e) {
                log.error("关闭流失败");
                log.error(CommonUtil.getMessage(e));
            }
            if (socket != null && socket.isConnected() && !socket.isClosed()) {
                try {
                    socket.getInputStream().close();
                    socket.getOutputStream().close();
                    socket.close();
                } catch (Exception e) {
                    log.error("关闭 socket 失败");
                    log.error(CommonUtil.getMessage(e));
                }
            }
            log.error("关闭 socket 成功");
        }
    }

    /**
     * 阻塞等待 socket 传递第一个字符
     * 如果第一个字符非 { 抛出异常，断开连接（传递 json 数据）
     */
    private void blockAndReadFirst() throws IOException, InterruptedException {
        try {
            while (!bufferedReader.ready()) {
                Thread.sleep(10);
            }
            int first = bufferedReader.read();
            if ('{' != first) {
                log.error("gxp socket 传递数据中第一个字符非 {");
                throw new RuntimeException("gxp socket 传递数据中第一个字符非 {");
            }
        } catch (Exception e) {
            log.error("阻塞并读取 gxp socket 传递的第一个字符失败第一个字符失败");
            throw e;
        }
    }

    /**
     * 从 inputStream 读入 JSON（编码 UTF-8）
     * @return
     */
    private String readJson() {
        try {
            int count = 1;
            /* 读取发送的 url */
            StringBuilder sb = new StringBuilder();
            sb.append('{');
            do {
                int c = bufferedReader.read(); // 读入一个字符
                if ('{' == c) count++;
                else if ('}' == c) count--;
                sb.append((char) c);
            } while (count != 0);
            return sb.toString();
        } catch (UnsupportedEncodingException unsupportedEncodingException) {
            log.error("不支持{}编码方式", ConfigUtil.getConfigVal(MyConst.JSON_ENCODING));
            throw new RuntimeException("无法根据配置中编码类型获取 gxp 发送的数据", unsupportedEncodingException);
        } catch (Exception e) {
            log.error("读入数据失败");
            throw new RuntimeException("从指定端口读入数据失败", e);
        }
    }

    /**
     * 发送 HTTPS 请求，并获取请求结果
     */
    private ResponseEntity sendHttpsAndGetRes(String reqJson) {
        ResponseEntity response = new ResponseEntity();
        try {
            String requestUrl = ConfigUtil.getConfigVal(MyConst.REQUEST_URL);
            String resp = HttpUtil.httpsPost(requestUrl, reqJson);
            response.setRespCode(MyConst.SUCCESS_CODE);
            response.setRespMsg(resp);
        } catch (MyRuntimeException my) { // 抛出了自定义异常
            log.error("发送 HTTPS 失败，错误信息:{}", my.getErrorMessage());
            response.setRespCode(my.getErrorCode());
            response.setRespMsg(my.getErrorMessage());
        } catch (Exception e) {
            log.error("发送 HTTPS 失败，未知错误");
            log.error(CommonUtil.getMessage(e));
            response.setRespCode(MyConst.UNKNOWN_ERR_CODE);
            response.setRespMsg("{发送 HTTPS 失败（详细信息见桥接器日志）}");
        }
        return response;
    }

    /**
     * 返回给 json
     * 编码 UTF-8
     * @param respJson
     */
    private void writeJson(String respJson) {
        if (socket != null && socket.isConnected() && !socket.isClosed()) {
            try {
                bufferedWriter.write(respJson);
                bufferedWriter.flush();
            } catch (Exception e) {
                log.error("将数据{}返回给 gxp 失败", respJson);
                e.printStackTrace();
                throw new RuntimeException("将数据返回给 gxp 失败");
            }
        } else {
            log.error("客户端 socket 已经关闭，无法将数据{}写回客户端", respJson);
            throw new RuntimeException("客户端 socket 已经关闭，无法将数据写回客户端");
        }
    }
}
