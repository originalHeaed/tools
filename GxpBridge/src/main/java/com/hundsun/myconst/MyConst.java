package com.hundsun.myconst;

import java.nio.charset.StandardCharsets;

/*********************************************************
 * 文件名称：MyConst.java
 * 软件版权: 恒生电子股份有限公司
 * 模块名称：com.hundsun.myconst
 * 功能说明：使用的常量
 * 开发人员 @author：wanggh31690
 * 开发时间 @date：2022/8/20 15:56
 * 修改记录：程序版本  修改日期  修改人员  修改单号  修改说明
 *********************************************************/
public interface MyConst {

    //============================= 配置文件中 key 的统一命名处 ===============

    String REQUEST_URL = "requestUrl";

    /**
     * 使用 SSL 或者 TSL
     */
    String PROTOCOL = "protocol";

    /**
     * json 字符编码
     */
    String JSON_ENCODING = "json.encoding";

    /**
     * 是否支持双向认证
     */
    String IS_SUP_BIDIRECTIONAL = "isBidirectional";

    /**
     * 配置文件中服务端信任证书类型
     */
    String CA_TYPE = "server.cert.type";

    /**
     * 配置文件中服务端信任证书路径
     */
    String CA_PATH = "server.cert.path";

    /**
     * 配置文件中服务端信任证书打开密码
     */
    String CA_PASSWORD = "server.cert.password";

    /**
     * 配置文件中客户端证书类型
     */
    String CLIENT_CERT_TYPE = "client.cert.type";

    /**
     * 配置文件中客户端证书路径
     */
    String CLIENT_CERT_PATH = "client.cert.path";

    /**
     * 配置文件中客户端证书打开密码
     */
    String CLIENT_CERT_PASSWORD = "client.cert.password";

    /**
     * 发送 http 时默认读超时时间（单位 s)
     */
    String HTTP_READ_TIMEOUT = "http.read.timeout";

    /**
     * 发送 http 时默认读写时时间（单位 s)
     */
    String HTTP_WRITE_TIMEOUT = "http.write.timeout";

    /**
     * 发送 http 时连接超时时间（单位 s）
     */
    String HTTP_CONNECT_TIMEOUT = "http.connect.timeout";

    /**
     * http 连接池中最大连接数
     */
    String POOL_MAX_SIZE = "http.max.connections";

    /**
     * 池中 http 最大存活时间（单位 min）
     */
    String MAX_KEEP_DURATION = "http.keep.duration";

    /**
     * 发送 HTTPS 请求头中 X-DBS-ORG_ID 值（不允许为空）
     */
    String X_DBS_ORGID = "x.dbs.orgId";

    /**
     * 发送 HTTPS 请求头中 X-DBS-PROFILE 值（不允许为空）
     */
    String X_DBS_PROFILE = "x.dbs.profile";

    /**
     * 发送 HTTPS 请求头中 X-DBS-CHANNEL_ID 值
     */
    String X_DBS_CHANNELID = "x.dbs.channelId";

    //====================== 其他常量 ================
    /**
     * 程序启动时 -D 后接受入参的名字
     */
    String CONFI_PATH_NAME = "FilePath";

    /**
     * 成功的返回码
     */
    String SUCCESS_CODE = "000";

    /**
     * 未知错误
     */
    String UNKNOWN_ERR_CODE = "999";
}
