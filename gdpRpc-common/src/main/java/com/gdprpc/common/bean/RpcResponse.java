package com.gdprpc.common.bean;

/**
 * @author 我是金角大王 on 2017-10-25.
 */
public class RpcResponse {
    private  String id;
    private Exception exception;
    private Object result;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
