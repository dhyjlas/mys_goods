package com.dhyjlas.mys.entity;

import com.alibaba.fastjson2.annotation.JSONType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * <p>File: UserInfo.java </p>
 * <p>Title: 用户信息</p>
 * <p>Description: </p>
 *
 * @author yjl.yu/dhyjlas@163.com
 * @version 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
@JSONType(orders={"mys_uid","nickname","cookie","point","game_list","address_list","channel_dict"})
public class UserInfo extends BaseEntity {
    /**
     * 用户ID
     */
    private String mys_uid;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 用户cookie
     */
    private String cookie;

    /**
     * 米游币数量
     */
    private Integer point;

    /**
     * 游戏账户列表
     */
    private List<GameInfo> game_list;

    /**
     * 地址列表
     */
    private List<AddressInfo> address_list;

    /**
     * 频道等级
     */
    private LinkedHashMap<String, Integer> channel_dict;
}
