package com.hundsun.dto;

/*********************************************************
 * 文件名称：ResponseEntity.java
 * 软件版权: 恒生电子股份有限公司
 * 模块名称：com.hundsun.dto
 * 功能说明：构建返回 gxp 对象
 * 开发人员 @author：wanggh31690
 * 开发时间 @date：2022/8/23 10:50
 * 修改记录：程序版本  修改日期  修改人员  修改单号  修改说明
 *********************************************************/
public class ResponseEntity {
    /**
     * 返回码
     */
    private String respCode;

    /**
     * 返回信息
     */
    private String respMsg;

    public String getRespCode() {
        return respCode;
    }

    public void setRespCode(String respCode) {
        this.respCode = respCode;
    }

    public String getRespMsg() {
        return respMsg;
    }

    public void setRespMsg(String respMsg) {
        this.respMsg = respMsg;
    }

    @Override
    public String toString() {
        return "ResponseEntity{" +
                "respCode='" + respCode + '\'' +
                ", respMsg='" + respMsg + '\'' +
                '}';
    }
}
