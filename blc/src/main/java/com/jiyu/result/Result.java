package com.jiyu.result;

import lombok.Data;

@Data
public class Result {
    //构造response，code用于前端判断，message可以在前端显示错误信息，视情况传data
    private int code;
    private String message;
    private Object result;

    public Result(int code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.result = data;
    }

}