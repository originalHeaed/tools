package com.hundsun.util;

import com.hundsun.dto.MyTrustManager;
import com.hundsun.exception.MyRuntimeException;
import com.hundsun.myconst.MyConst;
import com.hundsun.myconst.MyErrorEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.SecureRandom;

/*********************************************************
 * 文件名称：Util.java
 * 软件版权: 恒生电子股份有限公司
 * 模块名称：com.hundsun
 * 功能说明：SSLSocket 工具类
 * 开发人员 @author：wanggh31690
 * 开发时间 @date：2022/8/19 17:39
 * 修改记录：程序版本  修改日期  修改人员  修改单号  修改说明
 *********************************************************/
public class SSLSocketUtils {
    /**
     * 日志
     */
    private static Logger log =  LoggerFactory.getLogger(SSLSocketUtils.class);

    /**
     * SSL 工厂
     */
    private static SSLSocketFactory sslFactory;

    /**
     * 客户端的证书数组
     */
    private static KeyManager[] clientManager;

    /**
     * 信任证书数组
     */
    private static TrustManager[] trustManagers;

    /**
     *
     * @return
     * @throws Exception
     */
    public static SSLSocketFactory getSslFactory() {
        if (sslFactory == null) {
            synchronized (SSLSocketUtils.class) {
                if (sslFactory == null) {
                    log.debug("开始初始化 SSL Socket Factory...");
                    try {
                        SSLContext sslContext = SSLContext.getInstance(ConfigUtil.getConfigVal(MyConst.PROTOCOL));
                        /* 加载客户端证书，提供给服务端 */
                        clientManager = getClientCert();
                        /* 创建信任证书对象 */
                        trustManagers = new TrustManager[]{ MyTrustManager.getMyTrustManager() };
                        /* 初始化 sslContext */
                        sslContext.init(clientManager, trustManagers, new SecureRandom());
                        sslFactory = sslContext.getSocketFactory();
                        log.debug("成功初始化 SSL Socket Factory");
                    } catch (MyRuntimeException myRuntimeException) {
                        throw myRuntimeException;
                    } catch (Exception e) {
                        log.error("构建 SSLFACTORY 对象失败");
                        log.error(CommonUtil.getMessage(e));
                        throw new MyRuntimeException(MyErrorEnum.ER113.getCode(), MyErrorEnum.ER113.getDescription());
                    }
                }
            }
        }
        return sslFactory;
    }

    /**
     * 获取客户端证书
     * 用于双向验证中发送给服务端进行校验
     */
    private static KeyManager[] getClientCert() throws Exception {
        /**
         * 不支持双向认证时，
         */
        if (!CommonUtil.strConvertBoolean(ConfigUtil.getConfigVal(MyConst.IS_SUP_BIDIRECTIONAL))) {
            return null;
        }
        log.debug("开始加载客户端数字证书 —— 用于发送给银行进行双向认证...");
        /* 获取客户端证书 keystore 对象 */
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        String clientpath = ConfigUtil.getConfigVal(MyConst.CLIENT_CERT_PATH);
        String clientType = ConfigUtil.getConfigVal(MyConst.CLIENT_CERT_TYPE);
        String clientPassword = ConfigUtil.getConfigVal(MyConst.CLIENT_CERT_PASSWORD);
        KeyStore keyStore = getJKSOrP12KeyStore(clientType, clientpath, clientPassword);
        kmf.init(keyStore, clientPassword.toCharArray());
        return kmf.getKeyManagers();
    }

    /**
     * 从获取 .jks 或则 .p12 的 keyStore
     * @param caType
     * @param filePath
     * @return
     */
    public static KeyStore getJKSOrP12KeyStore(String caType, String filePath, String password) {
        KeyStore ks = null;
        try (
            FileInputStream caInputStream = new FileInputStream(filePath);
        ){
            ks = KeyStore.getInstance(caType);
            ks.load(caInputStream, password.toCharArray());
        } catch (Exception e) {
            log.error("获取类型为 {}、路径为 {} 的证书信息失败", caType, filePath);
            log.error(CommonUtil.getMessage(e));
            throw new MyRuntimeException(MyErrorEnum.ER114.getCode(),
                    MyErrorEnum.ER114.getDescription() + "证书类型为：" + caType + ", 证书路径为：" + filePath);
        }
        return ks;
    }
}
