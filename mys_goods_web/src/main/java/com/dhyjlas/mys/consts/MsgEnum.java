package com.dhyjlas.mys.consts;

import lombok.Getter;
import lombok.Setter;

/**
 * <p>File: ErrorCode.java </p>
 * <p>Title: </p>
 * <p>Description: </p>
 *
 * @author yjl.yu/dhyjlas@163.com
 * @version 1.0
 */
public enum MsgEnum {
    //成功
    SUCCESS(100, "成功"),
    SUCCESS_LOGIN(100, "登录成功"),
    SUCCESS_LOGOUT(100, "退出成功"),
    SUCCESS_SAVE(100, "保存成功"),
    SUCCESS_UPDATE(100, "更新成功"),
    SUCCESS_DELETE(100, "删除成功"),

    //2、3开头，内部接口错误
    ERROR_USERNAME_PASSWORD(201, "请输入正确的用户名和密码"),
    ERROR_LOGIN(202, "登录异常，请稍后重试"),
    ERROR_SAVE(203, "保存失败"),
    ERROR_UPDATE(204, "更新失败"),
    ERROR_DELETE(205, "删除失败"),
    ERROR_QUERY(206, "查询失败"),
    INVALID(207, "参数缺失"),
    ERROR_REQUEST(208, "请求出错"),
    ERROR_CAPTCHA(209, "验证码错误"),
    INVALID_LOGIN_TICKET(210, "缺少'login_ticket'字段"),
    INVALID_LOGIN_UID(211, "缺少'uid'字段"),
    INVALID_COOKIE_TOKEN(212, "缺少'cookie_token'字段"),
    ERROR_UPDATE_COOKIE(213, "cookie获取失败"),
    ERROR_CHANNEL_LEVEL(214, "获取频道等级失败"),
    ERROR_ACTION_TICKET(215, "获取查询所需ticket失败"),
    ERROR_GAME_ROLES(216, "获取绑定角色失败"),
    ERROR_ADDRESS_LIST(217, "获取收货地址失败"),
    ERROR_GOODS_LIST(218, "获取商品列表失败"),
    ERROR_GOODS_DETAIL(219, "获取商品详情失败"),
    ERROR_GET_POINT(220, "获取米游币数量失败"),
    ERROR_EXCHANGE_GOODS(221, "兑换商品失败"),
    ERROR_MSDIR(222, "目标路径无法创建"),
    ERROR_MMT_KEY(223, "获取mmt_key失败"),
    ERROR_SEND_CAPTCHA(224, "发送验证码失败"),
    ERROR_IS_REGISTRABLE(225, "检查手机号是否注册失败"),
    ERROR_GET_TICKET(226, "获取ticket失败，请重新尝试"),
    ERROR_GET_STOKEN(227, "获取stoken失败，请重新尝试"),
    INVALID_USER_FILE(228, "找不到用户文件"),
    ERROR_USER_FILE(229, "用户文件错误"),
    INVALID_EXCHANGE_FILE(230, "找不到兑换文件"),
    ERROR_EXCHANGE_FILE(231, "兑换文件错误"),
    ERROR_COOKIE_LOGOUT(232, "登录状态已失效，请重新添加用户"),
    INVALID_HOST(233, "请输入域名"),
    INVALID_NAME_PATH(234, "请输入书名爬取规则"),
    INVALID_CHAPTER_PATH(235, "请输入章节爬取规则"),
    INVALID_CONTENT_PATH(236, "请输入正文爬取规则"),
    ERROR_BEGIN(237, "小说正在更新中"),
    ERROR_NOT_PRESENT(238, "文件不存在，请先更新章节"),

    //3、4开头，外部接口错误
    INVALID_MOBILE(301, "请输入手机号"),
    INVALID_CAPTCHA(302, "请输入验证码"),
    INVALID_UID(303, "请选择正确的用户"),
    INVALID_MYS_UID(305, "缺少兑换用户UID信息"),
    INVALID_GOODS(306, "请选择需要兑换的商品"),
    INVALID_GAME_UID(307, "请选择正确的游戏角色"),
    INVALID_ADDRESS(308, "请选择正确的地址"),
    ERROR_EXCHANGE_TIME(309, "商品无法兑换"),

    //系统异常
    INVALID_REQUEST(901, "无效的请求，被拒绝或服务不存在"),
    INVALID_AUTHORIZATION(902, "无效或过期的身份认证信息"),
    SYSTEM_ERROR(999, "系统异常");



    MsgEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Getter
    @Setter
    private int code;

    @Getter
    @Setter
    private String msg;
}
