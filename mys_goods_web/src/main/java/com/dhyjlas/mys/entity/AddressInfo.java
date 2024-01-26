package com.dhyjlas.mys.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>File: AddressInfo.java </p>
 * <p>Title: 地址信息</p>
 * <p>Description: </p>
 *
 * @author yjl.yu/dhyjlas@163.com
 * @version 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AddressInfo extends BaseEntity {
    /**
     * 地址ID
     */
    private String id;

    /**
     * 联系人姓名
     */
    private String connect_name;

    /**
     * 联系人区号
     */
    private String connect_areacode;

    /**
     * 联系人电话
     */
    private String connect_mobile;

    /**
     * 省份
     */
    private String province_name;

    /**
     * 城市
     */
    private String city_name;

    /**
     * 区县
     */
    private String county_name;

    /**
     * 详细地址
     */
    private String addr_ext;

}
