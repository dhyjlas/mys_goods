package com.dhyjlas.mys.book.entity;

import com.alibaba.fastjson2.annotation.JSONType;
import com.dhyjlas.mys.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * <p>File: Book.java </p>
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Create By: 2024/04/03 09:15 </p>
 * <p>Company: nbhope.cn </p>
 *
 * @author yjl.yu/dhyjlas@163.com
 * @version 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
@JSONType(orders={"bookName","chapterUrl","bookNamePath","chapterPath","contentPath","header","replace","chapterList","fileName"})
public class Book extends BaseEntity {
    private String bookName;
    private String chapterUrl;

    private String chapterPath;
    private String contentPath;
    private List<KeyValue> header;
    private List<KeyValue> replace;

    private Integer chapterNum;
    private Integer progress;

    private String fileName;

    private String contentUrl;
}
