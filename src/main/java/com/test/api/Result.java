package com.test.api;

import lombok.Data;

import java.io.Serializable;

/**
 * @author peng
 */
@Data
public class Result implements Serializable {
    private String code;
    private String msg;
    private Object data;

    public static Result success(Object data) {
        Result result = new Result();
        result.setCode("200");
        result.setMsg("操作成功");
        result.setData(data);
        return result;
    }

    public static Result success(String code, String msg) {
        Result result = new Result();
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }

    public static Result success(String code, String msg, Object data) {
        Result result = new Result();
        result.setCode(code);
        result.setMsg(msg);
        result.setData(data);
        return result;
    }

    public static Result fail(String code, String msg) {
        Result result = new Result();
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }

    public static Result fail(String code, String msg, Object data) {
        Result result = new Result();
        result.setCode(code);
        result.setMsg(msg);
        result.setData(data);
        return result;
    }
}

