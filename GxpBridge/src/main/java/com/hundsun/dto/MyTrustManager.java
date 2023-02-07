package com.hundsun.dto;

import com.hundsun.util.CommonUtil;
import com.hundsun.util.ConfigUtil;
import com.hundsun.util.SSLSocketUtils;
import com.hundsun.exception.MyRuntimeException;
import com.hundsun.myconst.MyConst;
import com.hundsun.myconst.MyErrorEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/*********************************************************
 * 文件名称：TrustManager.java
 * 软件版权: 恒生电子股份有限公司
 * 模块名称：com.hundsun.http
 * 功能说明：信任的服务器证书类
 * 开发人员 @author：wanggh31690
 * 开发时间 @date：2022/8/20 15:41
 * 修改记录：程序版本  修改日期  修改人员  修改单号  修改说明
 *********************************************************/
public class MyTrustManager implements X509TrustManager {
    /**
     * 日志对象
     */
    private static Logger log = LoggerFactory.getLogger(MyTrustManager.class);

    private static MyTrustManager myTrustManager;

    private X509TrustManager MyX509TrustManager;

    /**
     * 获取 MyTrustManager 类实例
     * @return
     * @throws Exception
     */
    public static MyTrustManager getMyTrustManager() throws Exception {
        if (myTrustManager == null) {
            synchronized (MyTrustManager.class) {
                if (myTrustManager == null) myTrustManager = new MyTrustManager();
            }
        }
        return myTrustManager;
    }

    /**
     * 构造方法私有化
     * @throws Exception
     */
    private MyTrustManager() {
        try {
            log.debug("开始加载服务端 CA 证书 —— 用于校验服务端数字证书...");
            TrustManagerFactory managerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            KeyStore keyStore = null;
            String caType = ConfigUtil.getConfigVal(MyConst.CA_TYPE);
            if ("JKS".equals(caType) || "P12".equals(caType)) {
                String caPath = ConfigUtil.getConfigVal(MyConst.CA_PATH);
                String caPassword = ConfigUtil.getConfigVal(MyConst.CA_PASSWORD);
                keyStore = SSLSocketUtils.getJKSOrP12KeyStore(caType, caPath, caPassword);
            }
            managerFactory.init(keyStore);
            javax.net.ssl.TrustManager trustManagers[] = managerFactory.getTrustManagers();
            for (int i = 0; i < trustManagers.length; i++) {
                if (trustManagers[i] instanceof X509TrustManager) {
                    MyX509TrustManager = (X509TrustManager) trustManagers[i];
                    return;
                }
            }
            log.error("信任的证书不属于的 X509数字证书，构建服务端信任证书失败");
            throw new MyRuntimeException(MyErrorEnum.ER115.getCode(), MyErrorEnum.ER115.getDescription());
        } catch (MyRuntimeException myRuntimeException) {
            throw myRuntimeException;
        } catch (Exception e) {
            log.error("构建 MyTrustManager 对象失败");
            throw new MyRuntimeException(MyErrorEnum.ER116.getCode(), MyErrorEnum.ER116.getDescription());
        }
    }

    /**
     * 校验客户端证书
     */
    @Override
    public void checkClientTrusted(X509Certificate[] x509Certificates, String s) {
        try {
            MyX509TrustManager.checkClientTrusted(x509Certificates, s);
        } catch (Exception e) {
            // 校验失败时的处理
            log.error("校验客户端数字证书失败");
            log.error(CommonUtil.getMessage(e));
            throw new MyRuntimeException(MyErrorEnum.ER105.getCode(), MyErrorEnum.ER105.getDescription());
        }
    }

    /**
     * 校验服务端证书
     */
    @Override
    public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
        try {
            MyX509TrustManager.checkServerTrusted(x509Certificates, s);
        } catch (Exception e) {
            // 校验失败时的处理
            log.error("校验服务端数字证书失败，服务端证书不正确或不可信");
            log.error(CommonUtil.getMessage(e));
            throw new MyRuntimeException(MyErrorEnum.ER106.getCode(), MyErrorEnum.ER106.getDescription());
        }
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return MyX509TrustManager.getAcceptedIssuers();
    }

    /**
     * 返回 X509TrustManager
     * @return
     */
    public X509TrustManager getMyX509TrustManager() {
        return this.MyX509TrustManager;
    }
}
