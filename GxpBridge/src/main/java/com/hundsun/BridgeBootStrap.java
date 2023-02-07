package com.hundsun;

import com.hundsun.socket.MainSocket;
import com.hundsun.util.ConfigUtil;
import gxpbridge.pub.BridgeDriver;
import gxpbridge.pub.VariableOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.Properties;

/*********************************************************
 * 文件名称：BridgeMain.java
 * 软件版权: 恒生电子股份有限公司
 * 模块名称：com.hundsun
 * 功能说明：启动类
 * 开发人员 @author：wanggh31690
 * 开发时间 @date：2022/8/20 9:57
 * 修改记录：程序版本  修改日期  修改人员  修改单号  修改说明
 *********************************************************/
public class BridgeBootStrap {
    /**
     * 日志对象
     */
    private static Logger log = LoggerFactory.getLogger(BridgeBootStrap.class);

    public static void main(String[] args) {
        log.debug("开始启动 Java 桥接器");
        /* 加载并校验配置文件 */
        ConfigUtil.LoadAndCheckConfigFile();
        /* 启动 socket 监听 gxp 消息 */
        MainSocket mainSocket = new MainSocket();
        mainSocket.start();
    }
}
