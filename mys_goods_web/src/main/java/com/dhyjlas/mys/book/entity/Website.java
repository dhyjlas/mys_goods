package com.dhyjlas.mys.book.entity;

import com.alibaba.fastjson2.annotation.JSONType;
import com.dhyjlas.mys.entity.BaseEntity;
import com.dhyjlas.mys.entity.LabelValue;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * <p>File: Website.java </p>
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Create By: 2024/04/03 09:07 </p>
 * <p>Company: nbhope.cn </p>
 *
 * @author yjl.yu/dhyjlas@163.com
 * @version 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
@JSONType(orders={"host","bookNamePath","chapterPath","contentPath","header","replace"})
public class Website extends BaseEntity {
    private String host;
    private String bookNamePath;
    private String chapterPath;
    private String contentPath;
    private List<KeyValue> header;
    private List<KeyValue> replace;
}
