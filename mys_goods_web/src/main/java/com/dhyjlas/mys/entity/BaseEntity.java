package com.dhyjlas.mys.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * <p>File: BaseEntity.java </p>
 * <p>Title: </p>
 * <p>Description: </p>
 *
 * @author yjl.yu/dhyjlas@163.com
 * @version 1.0
 */
@Data
public class BaseEntity implements Serializable {
    private Integer currentPage;
    private Integer pageSize;
    private List<Long> ids;

    private Long startMillis;
    private Long overMillis;
}
