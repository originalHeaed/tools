package com.hundsun.util;

import com.hundsun.dto.MyTrustManager;
import com.hundsun.exception.MyRuntimeException;
import com.hundsun.myconst.MyConst;
import com.hundsun.myconst.MyErrorEnum;
import okhttp3.ConnectionPool;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;

/*********************************************************
 * 文件名称：HttpUtil.java
 * 软件版权: 恒生电子股份有限公司
 * 模块名称：com.hundsun.Util
 * 功能说明：http 工具集合
 * 开发人员 @author：wanggh31690
 * 开发时间 @date：2022/8/20 16:51
 * 修改记录：程序版本  修改日期  修改人员  修改单号  修改说明
 *********************************************************/
public class HttpUtil {
    /**
     * 日志对象
     */
    private static Logger log = LoggerFactory.getLogger(HttpUtil.class);

    /**
     * 用于发送 https 的 okhttp
     */
    private static OkHttpClient httpsClient;

    /**
     * 用于发送 http 的 okhttp
     */
    private static OkHttpClient httpClient;

    public static String httpsPost(String requestUrl, String json) {
        try {
            log.debug("发送 https 请求开始：url为:{}，数据内容为：{}", requestUrl, json);
            /* MIME类型 */
            MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
            /* 构建请求 */
            Request request = new Request.Builder()
                    .addHeader("X-DBS-ORG_ID", ConfigUtil.getConfigVal(MyConst.X_DBS_ORGID))
                    .addHeader("X-DBS-PROFILE", ConfigUtil.getConfigVal(MyConst.X_DBS_PROFILE))
                    .addHeader("X-DBS-CHANNEL_ID", ConfigUtil.getConfigValOrDef(MyConst.X_DBS_CHANNELID, ""))
                    .url(requestUrl)
                    .post(RequestBody.create(json, mediaType))
                    .build();
            log.info("请求头部内容为：{}", request);
            /* 发送请求并获取返回值 */
            Response response = getHttpsClient().newCall(request).execute();
            if (!response.isSuccessful()) {
                log.error("返回码不符合预期，返回码为{}", response.code());
                throw new MyRuntimeException(MyErrorEnum.ER104.getCode(), MyErrorEnum.ER104.getDescription());
            }
            String result = response.body().string();
            log.debug("发送 https 结束：返回数据内容：{}", result);
            return result;
        } catch (MyRuntimeException myRuntimeException) {
            throw myRuntimeException;
        } catch (SocketTimeoutException socketTimeoutException) {
            log.error("与银行连接超时");
            log.error(CommonUtil.getMessage(socketTimeoutException));
            throw new MyRuntimeException(MyErrorEnum.ER107.getCode(), MyErrorEnum.ER107.getDescription());
        } catch (ConnectException connectException) {
            log.error("与请求地址进行进行连接{}失败", requestUrl);
            log.error(CommonUtil.getMessage(connectException));
            throw new MyRuntimeException(MyErrorEnum.ER101.getCode(), MyErrorEnum.ER101.getDescription());
        } catch (SocketException socketException) {
            log.error("与指定请求路径{}进行通信失败，连接断开", requestUrl);
            log.error(CommonUtil.getMessage(socketException));
            throw new MyRuntimeException(MyErrorEnum.ER102.getCode(), MyErrorEnum.ER102.getDescription());
        } catch (Exception e) {
            log.error("向url为{}发送 json 请求失败", requestUrl);
            log.error(CommonUtil.getMessage(e));
            throw new MyRuntimeException(MyErrorEnum.ER103.getCode(), MyErrorEnum.ER103.getDescription());
        }
    }

    /**
     * 发送 http 的 post 请求
     * 用于发送 HTTP（防止后续需求变更，未进行测试）
     * @param requestUrl
     * @param json
     * @return
     */
    public static String httpPost(String requestUrl, String json) {
        try {
            /* MIME类型 */
            MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
            /* 构建请求 */
            Request request = new Request.Builder()
                    .url(requestUrl)
                    .post(RequestBody.create(json, mediaType))
                    .build();
            /* 发送请求并获取返回值 */
            Response response = getHttpsClient().newCall(request).execute();
            if (!response.isSuccessful()) {
                log.error("返回码不符合预期，返回码为{}", response.code());
                throw new RuntimeException("返回码不符合预期");
            }
            return response.body().string();
        } catch (MyRuntimeException myRuntimeException) {
            throw myRuntimeException;
        } catch (Exception e) {
            log.error("向url为{}发送 json 请求失败", requestUrl);
            log.error(CommonUtil.getMessage(e));
            throw new MyRuntimeException(MyErrorEnum.ER103.getCode(), MyErrorEnum.ER103.getDescription());
        }
    }

    /**
     * 初始化发送 http 对象
     * @return
     */
    private static OkHttpClient getHttpClient() {
        if (httpClient == null) {
            synchronized (HttpUtil.class) {
                if (httpClient == null) {
                    initHttpClient();
                }
            }
        }
        return httpClient;
    }

    /**
     * 初始化发送 https 对象
     * @return
     */
    private static OkHttpClient getHttpsClient() {
        if (httpsClient == null) {
            synchronized (HttpUtil.class) {
                if (httpsClient == null) {
                    initHttpsClient();
                }
            }
        }
        return httpsClient;
    }

    /**
     * 初始化HTTP客户端
     */
    private static void initHttpClient() {
        log.debug("开始初始化 http 发送对象");
        // 进行数据初始化
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        try {
            builder
                    // 设置读取超时时间
                    .readTimeout(Long.valueOf(ConfigUtil.getConfigValOrDef(MyConst.HTTP_READ_TIMEOUT, "10")),
                            TimeUnit.SECONDS)
                    // 设置写的超时时间
                    .writeTimeout(Long.valueOf(ConfigUtil.getConfigValOrDef(MyConst.HTTP_WRITE_TIMEOUT, "10")),
                            TimeUnit.SECONDS)
                    // 设置连接超时时间
                    .connectTimeout(Long.valueOf(ConfigUtil.getConfigValOrDef(MyConst.HTTP_CONNECT_TIMEOUT, "10")),
                            TimeUnit.SECONDS)
                    // 使用连接池
                    .connectionPool(pool());
        } catch (MyRuntimeException myRuntimeException) {
            throw myRuntimeException;
        } catch (Exception e) {
            log.error("构建 httpclient 对象失败，无法发送 http 请求");
            log.error(CommonUtil.getMessage(e));
            throw new MyRuntimeException(MyErrorEnum.ER112.getCode(), MyErrorEnum.ER112.getDescription());
        }
        log.debug("成功初始化 http 发送对象");
        httpClient = builder.build();
    }

    /**
     * 初始化 httpsclient 对象
     */
    private static void initHttpsClient() {
        log.debug("开始初始化 https 发送对象");
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        try {
        // 进行数据初始化
        builder
                // 设置读取超时时间
                .readTimeout(Long.valueOf(ConfigUtil.getConfigValOrDef(MyConst.HTTP_READ_TIMEOUT, "10")),
                        TimeUnit.SECONDS)
                // 设置写的超时时间
                .writeTimeout(Long.valueOf(ConfigUtil.getConfigValOrDef(MyConst.HTTP_WRITE_TIMEOUT, "10")),
                        TimeUnit.SECONDS)
                // 设置连接超时时间
                .connectTimeout(Long.valueOf(ConfigUtil.getConfigValOrDef(MyConst.HTTP_CONNECT_TIMEOUT, "10")),
                        TimeUnit.SECONDS)
                // 校验服务端证书时是否需要对比证书中 hostname 与 url 中的hostname（必填）
                .hostnameVerifier((hostname, session) -> true)
                // 设置 SSL 证书相关信息
                .sslSocketFactory(SSLSocketUtils.getSslFactory(), MyTrustManager.getMyTrustManager().getMyX509TrustManager());
        } catch (MyRuntimeException myRuntimeException) {
            throw myRuntimeException;
        } catch (Exception e) {
            log.error("构建 httpsclient 对象失败，无法发送 https 请求");
            log.error(CommonUtil.getMessage(e));
            throw new MyRuntimeException(MyErrorEnum.ER111.getCode(), MyErrorEnum.ER111.getDescription());
        }
        log.debug("成功初始化 https 发送对象");
        httpsClient = builder.build();
    }

    /**
     * 复用 http/https 的连接池
     * @return
     */
    private static ConnectionPool pool() {
        return new ConnectionPool(Integer.parseInt(ConfigUtil.getConfigValOrDef(MyConst.POOL_MAX_SIZE, "20")),
                Integer.parseInt(ConfigUtil.getConfigValOrDef(MyConst.MAX_KEEP_DURATION, "5")),
                TimeUnit.MINUTES);
    }
}
