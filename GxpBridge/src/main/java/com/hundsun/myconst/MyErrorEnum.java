package com.hundsun.myconst;

public enum MyErrorEnum implements IEnum {

    ER101("101", "{连接至指定请求失败；可能错误原因：请求地址有误、网络问题等（详细可见桥接器日志）}"),
    ER102("102", "{与指定请求路径进行通信失败，socket 连接断开；可能错误原因：可能错误原因：客户端数字证书在服务端校验失败、网络问题等（详细可见桥接器日志）}"),
    ER103("103", "{发送 json 请求失败}"),
    ER104("104", "{银行返回码不符合要求，不在[200,300]内，连接断开；}"),
    ER105("105", "{认证客户端数字整数失败}"),
    ER106("106", "{认证服务端数字证书失败，服务端证书不正确或不可信}"),
    ER107("107", "{与银行端连接超时！}"),
    ER108("108", "{无法根据key，在配置文件中找到对应请求 url！}"),
    ER110("110", "{银行证书认证失败}"),
    ER111("111", "{构建 httpsclient 对象失败，无法发送 https 请求}"),
    ER112("112", "{构建 httpclient 对象失败，无法发送 http 请求}"),
    ER113("113", "{构建 SSLFACTORY 对象失败, 可能原因：提供的客户端证书或服务端CA证书有问题（详细可见桥接器日志）}"),
    ER114("114", "{获取证书信息失败，失败原因：可能是路径有误、证书与证书类型不匹配、证书密码与证书不匹配}"),
    ER115("115", "{信任的证书不属于的 X509数字证书，构建服务端信任证书失败}"),
    ER116("116", "{构建 SSLFACTORY 对象失败（详细可见桥接器日志）}"),
    ;


    /**
     * 错误码
     */
    private final String code;
    /**
     * 错误描述
     */
    private final String desc;

    MyErrorEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDescription() {
        return desc;
    }

    @Override
    public String getLocalCode() {
        return this.code;
    }

    @Override
    public String getLocalDescription() {
        return this.desc;
    }
}
