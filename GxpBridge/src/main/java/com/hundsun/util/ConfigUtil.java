package com.hundsun.util;

import com.hundsun.myconst.MyConst;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

/*********************************************************
 * 文件名称：ConfigUtil.java
 * 软件版权: 恒生电子股份有限公司
 * 模块名称：com.hundsun.Util
 * 功能说明：配置类 - 存放、校验、获取配置文件信息的类
 * 开发人员 @author：wanggh31690
 * 开发时间 @date：2022/8/20 13:46
 * 修改记录：程序版本  修改日期  修改人员  修改单号  修改说明
 *********************************************************/
public class ConfigUtil {

    /**
     * 日志
     */
    private static Logger log = LoggerFactory.getLogger(ConfigUtil.class);

    /**
     * 配置文件非空的配置 key
     */
    private static final String[] importKeys = {"listenPort",
            MyConst.REQUEST_URL,
            MyConst.IS_SUP_BIDIRECTIONAL,
            MyConst.CA_PATH,
            MyConst.CA_TYPE,
            MyConst.CA_PASSWORD,
            MyConst.JSON_ENCODING
    };

    /**
     * 配置文件中 cert.type 支持的类型
     */
    private static final Set<String> supportCertType = new HashSet<String>(){
        {
            add("JKS");
            add("PKCS12");
        }
    };

    /**
     * 存放配置文件中的 key-value
     */
    private static Properties configTable;
    /**
     * 配置文件的路径
     */
    private static String url;

    /**
     * 初始化配置文件内容
     */
    private static void initProperties() {
        log.debug("开始加载配置文件...");
        url = System.getProperty(MyConst.CONFI_PATH_NAME);
        if (CommonUtil.isEmpty(url)) {
            log.error("配置文件为空，启动时请使用 -DFilePath 指定配置文件路径");
            throw new RuntimeException("配置文件为空，启动时请使用 -DFilePath 指定配置文件路径");
        }
        try (InputStream inputstream = new java.io.FileInputStream(url);
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputstream))
        ){
            configTable = new Properties();
            configTable.load(bufferedReader);
        }catch(Exception e){
            log.error("加载配置文件失败，文件路径{}", url);
            log.error("失败原因：", CommonUtil.getMessage(e));
            throw new RuntimeException("加载配置文件失败，文件路径：" + url, e);
        }
    }

    /**
     * 获取加载的配置文件对象
     * @return
     */
    private static Properties getConfig() {
        if (configTable == null) {
            synchronized (ConfigUtil.class) {
                if (configTable == null) {
                    initProperties();
                }
            }
        }
        return configTable;
    }

    /**
     * 根据 key 在配置文件中获取值
     * @param key
     * @return
     */
    public static String getConfigVal(String key) {
        if (configTable == null) {
            log.warn("尚未加载配置文件至内存中");
            return null;
        }
        if (CommonUtil.isEmpty(key)) return null;
        return configTable.getProperty(key);
    }

    /**
     * 根据 key 在配置文件中获取值
     * @param key
     * @return
     */
    public static String getConfigValOrDef(String key, String defVal) {
        if (configTable == null) {
            log.warn("尚未加载配置文件至内存中");
            return defVal;
        }
        if (CommonUtil.isEmpty(key)) return defVal;
        return configTable.getProperty(key, defVal);
    }

    /**
     * 将配置文件加载入内存中
     * 校验配置文件中必填参数进行校验
     * 如果 keys 中某个 key 不存在于配置文件中，则抛出异常信息
     */
    public static void LoadAndCheckConfigFile() {
        getConfig();
        log.debug("开始校验配置文件...");
        if (CommonUtil.isEmpty(importKeys)) {
            log.debug("无校验项，校验成功");
            return;
        }
        /* 1. 校验非空参数 */
        for (String key : importKeys) {
            if (!configTable.containsKey(key)) {
                log.error("键为 {} 的键值对不存在于路径 {} 的配置文件中，请添加后在启动", key, url);
                throw new RuntimeException("键为" + key + "的键值对不存在于路径为 " + url +" 的配置文件中，请添加后在启动");
            }
        }
        /* 2. 双向认证时客户端证书信息校验 */
        if (CommonUtil.strConvertBoolean(getConfigVal(MyConst.IS_SUP_BIDIRECTIONAL))) {
            if (CommonUtil.isEmpty(getConfigVal(MyConst.CLIENT_CERT_PATH))) {
                log.error("配置文件中：{} 为 true 时 {} 不能为空", MyConst.IS_SUP_BIDIRECTIONAL, MyConst.CLIENT_CERT_PATH);
                throw new RuntimeException("配置文件中：isBidirectional 为 true 时 client.cert.path 不能为空");
            }
            if (CommonUtil.isEmpty(getConfigVal(MyConst.CLIENT_CERT_TYPE))) {
                log.error("配置文件中：{} 为 true 时 {} 不能为空", MyConst.IS_SUP_BIDIRECTIONAL, MyConst.CLIENT_CERT_TYPE);
                throw new RuntimeException("配置文件中：isBidirectional 为 true 时 client.cert.type 不能为空");
            }
            if (CommonUtil.isEmpty(getConfigVal(MyConst.CLIENT_CERT_PASSWORD))) {
                log.error("配置文件中：{} 为 true 时 {} 不能为空", MyConst.IS_SUP_BIDIRECTIONAL, MyConst.CLIENT_CERT_PASSWORD);
                throw new RuntimeException("配置文件中：isBidirectional 为 true 时 client.cert.password 不能为空");
            }
        }
        /* 3. 两个证书类型校验 */
        if (!CommonUtil.isEmpty(getConfigVal(MyConst.CA_TYPE)) && !supportCertType.contains(getConfigVal(MyConst.CA_TYPE))) {
            log.error("{}不支持当前类型{}", MyConst.CA_TYPE, getConfigVal(MyConst.CA_TYPE));
            throw new RuntimeException("不允许填入不支持的证书类型");
        }
        if (!CommonUtil.isEmpty(getConfigVal(MyConst.CLIENT_CERT_TYPE)) && !supportCertType.contains(getConfigVal(MyConst.CLIENT_CERT_TYPE))) {
            log.error("{}不支持当前类型{}", MyConst.CLIENT_CERT_TYPE, getConfigVal(MyConst.CLIENT_CERT_TYPE));
            throw new RuntimeException("不允许填入不支持的证书类型");
        }
    }
}
