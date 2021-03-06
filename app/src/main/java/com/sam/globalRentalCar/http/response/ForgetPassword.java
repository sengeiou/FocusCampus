package com.sam.globalRentalCar.http.response;

/**
 * 重置密码
 */
public class ForgetPassword {

    /**
     * success : true
     * data : 设置成功
     * msg : success
     * code : 200
     * traceId : null
     */

    private Boolean success;
    private String data;
    private String msg;
    private String code;
    private Object traceId;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Object getTraceId() {
        return traceId;
    }

    public void setTraceId(Object traceId) {
        this.traceId = traceId;
    }
}
