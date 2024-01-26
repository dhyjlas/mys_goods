package com.dhyjlas.mys.entity;

import lombok.Data;

/**
 * <p>File: GoodsInfo.java </p>
 * <p>Title: 商品信息</p>
 * <p>Description: </p>
 *
 * @author yjl.yu/dhyjlas@163.com
 * @version 1.0
 */
@Data
public class GoodsInfo extends BaseEntity {
    /**
     * 商品id
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
     * 商品价格
     */
    private int price;

    /**
     * 商品兑换总数量是否无限制
     */
    private boolean unlimit;

    /**
     * 商品兑换总数量
     */
    private int total;

    /**
     * 商品账户限购类型
     */
    private String account_cycle_type;

    /**
     * 商品账户限购数量
     */
    private String account_cycle_limit;

    /**
     * 商品账户已兑换数量
     */
    private String account_exchange_num;

    /**
     * 商品状态
     */
    private String status;

    /**
     * 商品下次兑换数量
     */
    private int next_num;

    /**
     * 商品下次兑换时间
     */
    private long next_time;

    /**
     * 商品兑换规则
     */
    private Object rules;

    /**
     * 商品区服
     */
    private String game_biz;

    /**
     * 商品销售开始时间
     */
    private long sale_start_time;

    /**
     * 商品图片
     */
    private String icon;
}
