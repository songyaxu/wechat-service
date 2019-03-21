package com.usforever.wechatservice.common;

import lombok.Data;

import java.io.Serializable;

/**
* @author: yaxuSong
* @className: Result
* @description: 账户结果对象
* @createTime: 2018/7/27 上午11:21
**/
@Data
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;
    
    public static final int   SUCCESS_CODE     = 0;

    private int               code;
    private String            error;
    private T                 data;

    private Result() {

    }

    public boolean isSuccess() {
        return code == Result.SUCCESS_CODE;
    }

    public static <T> Result<T> success() {
        return Result.success(null);
    }

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<T>();
        result.setCode(Result.SUCCESS_CODE);
        result.setData(data);
        return result;
    }

    public static <T> Result<T> fail(int code, String error) {
        Result<T> result = new Result<T>();
        result.setCode(code);
        result.setError(error);
        return result;
    }
    
    public static <T> Result<T> fail(){
        Result<T> result = new Result<T>();
        result.setCode(9999);
        result.setError("处理异常");
        return  result;
    }
}
