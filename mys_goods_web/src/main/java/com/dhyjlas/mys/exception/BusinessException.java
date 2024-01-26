package com.dhyjlas.mys.exception;


import com.dhyjlas.mys.consts.MsgEnum;

/**
 * <p>File: BussinessException.java </p>
 * <p>Title: </p>
 * <p>Description: </p>
 *
 * @author yjl.yu/dhyjlas@163.com
 * @version 1.0
 */
public class BusinessException extends RuntimeException {
    private int code = 500;

    public BusinessException(int code, String message){
        this(message);
        this.code = code;
    }

    public BusinessException(MsgEnum msgEnum){
        this(msgEnum.getCode(), msgEnum.getMsg());
    }

    public BusinessException(MsgEnum msgEnum, String message){
        this(msgEnum.getCode(), message);
    }

    public BusinessException(String message) {
        super(message, new Throwable(message), true, false);
    }

    public BusinessException(Throwable cause) {
        super(null, cause, true, false);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause, true, false);
    }

    public int getCode() {
        return code;
    }
}