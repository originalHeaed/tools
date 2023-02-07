package com.hundsun.exception;

/**
 * 自定义异常类
 */
public class MyRuntimeException extends RuntimeException {
    private static final long serialVersionUID = 7658018707769320413L;
    private String errorCode;
    private String errorMessage;

    public String getErrorCode() {
        return this.errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public MyRuntimeException(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public MyRuntimeException(String errorCode, String errorMessage, Throwable throwable) {
        super(throwable);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public MyRuntimeException(Throwable throwable) {
        super(throwable);
    }

    public String getMessage() {
        StringBuilder builder = new StringBuilder();
        if (this.errorCode == null) {
            if (this.errorMessage == null) {
                return super.getMessage();
            }
            builder.append(this.errorMessage);
        } else if (this.errorMessage != null) {
            builder.append(this.errorMessage);
        } else {
            builder.append(this.errorCode);
        }
        return builder.toString();
    }
}
