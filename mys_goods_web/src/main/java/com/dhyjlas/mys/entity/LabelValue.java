package com.dhyjlas.mys.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * <p>File: LabelValue.java </p>
 * <p>Title: </p>
 * <p>Description: </p>
 *
 * @author yjl.yu/dhyjlas@163.com
 * @version 1.0
 */
@Data
public class LabelValue implements Serializable {
    private String label;
    private String value;
    private List<LabelValue> children;
}
