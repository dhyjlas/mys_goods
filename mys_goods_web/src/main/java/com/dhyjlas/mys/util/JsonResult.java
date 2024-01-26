package com.dhyjlas.mys.util;

import com.dhyjlas.mys.consts.MsgEnum;
import lombok.Data;

/**
 * <p>File: JsonResult.java </p>
 * <p>Title: </p>
 * <p>Description: </p>
 *
 * @author yjl.yu/dhyjlas@163.com
 * @version 1.0
 */
@Data
public class JsonResult {
    private Integer code;
    private String msg;
    private Object data;
    private final long timestamps = System.currentTimeMillis();

    public JsonResult(Integer status) {
        this.code = status;
    }

    public JsonResult(Integer status, String msg) {
        this.code = status;
        this.msg = msg;
    }

    public JsonResult(MsgEnum msgEnum) {
        this.code = msgEnum.getCode();
        this.msg = msgEnum.getMsg();
    }

    public static JsonResult success() {
        return new JsonResult(MsgEnum.SUCCESS);
    }

    public static JsonResult failure() {
        return new JsonResult(MsgEnum.SYSTEM_ERROR);
    }

    public static JsonResult success(String msg) {
        return new JsonResult(MsgEnum.SUCCESS.getCode(), msg);
    }

    public static JsonResult failure(String msg) {
        return new JsonResult(MsgEnum.SYSTEM_ERROR.getCode(), msg);
    }

    public static JsonResult failure(Integer code, String msg) {
        return new JsonResult(code, msg);
    }

    public static JsonResult success(MsgEnum msgEnum) {
        return new JsonResult(msgEnum);
    }

    public static JsonResult failure(MsgEnum msgEnum) {
        return new JsonResult(msgEnum);
    }

    public static JsonResult failure(MsgEnum msgEnum, String msg) {
        return new JsonResult(msgEnum.getCode(), msg);
    }

    public JsonResult data(Object data) {
        this.data = data;
        return this;
    }
}
