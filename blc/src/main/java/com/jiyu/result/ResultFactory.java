package com.jiyu.result;

public class ResultFactory {

    //200
    public static Result buildSuccessResult(Object data) {
        return new Result(ResultCode.SUCCESS.code, "成功", data);
    }

    //400 自己传错误的message
    public static Result buildFailResult(String message) {
        return new Result(ResultCode.FAIL.code, message, null);
    }


}
