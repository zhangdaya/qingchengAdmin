package com.qingcheng.entity;

import java.io.Serializable;

/**
 * 返回前端的消息封装
 * （增加，删除，修改）前端没有返回值给后端，通常情况应该给前端一个明确的返回值，成功还是失败；如果失败了应该返回失败的错误信息.
 * 所以应该在pojo定义一个Result类。
 */
public class Result implements Serializable {

    /**
     *   返回的业务码  0：成功执行  1：发生错误
     */
    private int code;

    /**
     *   信息
     */
    private String message;

    public Result(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public Result() {
        this.code=0;
        this.message = "执行成功";
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
