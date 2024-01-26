package com.dhyjlas.mys.entity;

import com.alibaba.fastjson2.annotation.JSONType;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>File: ExchangeInfo.java </p>
 * <p>Title: 兑换商品信息</p>
 * <p>Description: </p>
 *
 * @author yjl.yu/dhyjlas@163.com
 * @version 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
@JSONType(orders={"id", "mys_uid","goods_id","goods_name","type","game_biz","exchange_num","game_uid","region","address_id","exchange_time","status","orderSn"})
public class ExchangeInfo extends BaseEntity{
    /**
     * ID
     */
    private Long id;

    /**
     * 兑换用户ID
     */
    private String mys_uid;

    /**
     * 商品ID
     */
    private String goods_id;

    /**
     * 商品名称
     */
    private String goods_name;

    /**
     * 商品类型
     */
    private int type;

    /**
     * 商品区服
     */
    private String game_biz;

    /**
     * 兑换数量
     */
    private int exchange_num;

    /**
     * 游戏ID
     */
    private String game_uid;

    /**
     * 游戏区服
     */
    private String region;

    /**
     * 地址ID
     */
    private String address_id;

    /**
     * 兑换时间
     */
    private long exchange_time;

    /**
     * 兑换状态
     */
    private String status;

    /**
     * 订单号
     */
    private String orderSn;

    /**
     * 线程数
     */
    private int thread;

    /**
     * 重试次数
     */
    private int retry;

    private AddressInfo addressInfo;
}
