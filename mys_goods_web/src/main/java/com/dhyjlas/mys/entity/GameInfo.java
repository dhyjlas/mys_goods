package com.dhyjlas.mys.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>File: GameInfo.java </p>
 * <p>Title: 游戏账户信息</p>
 * <p>Description: </p>
 *
 * @author yjl.yu/dhyjlas@163.com
 * @version 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class GameInfo extends BaseEntity {
    /**
     * 游戏ID
     */
    private String game_uid;

    /**
     * 游戏区服
     */
    private String game_biz;

    /**
     * 游戏昵称
     */
    private String nickname;

    /**
     * 游戏等级
     */
    private int level;

    /**
     * 游戏次区服
     */
    private String region;

    /**
     * 游戏次区服名称
     */
    private String region_name;
}
