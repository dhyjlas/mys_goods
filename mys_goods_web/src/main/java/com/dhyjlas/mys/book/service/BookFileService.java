package com.dhyjlas.mys.book.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.dhyjlas.mys.book.entity.Book;
import com.dhyjlas.mys.book.entity.Website;
import com.dhyjlas.mys.util.FileUtil;
import com.hy.corecode.idgen.WFGIdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>File: BookFileService.java </p>
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Create By: 2024/04/03 09:19 </p>
 * <p>Company: nbhope.cn </p>
 *
 * @author yjl.yu/dhyjlas@163.com
 * @version 1.0
 */
@Slf4j
@Service
public class BookFileService {
    @Value("${book.config.path}")
    private String configPath;

    @Value("${book.content.path}")
    private String contentPath;

    private final WFGIdGenerator wfgIdGenerator;

    public BookFileService(WFGIdGenerator wfgIdGenerator) {
        this.wfgIdGenerator = wfgIdGenerator;
    }

    /**
     * 获取配置的站点规则列表
     *
     * @return List<Website>
     */
    public List<Website> getWebsiteList() {
        File file = new File(configPath);
        List<Website> websiteList = new ArrayList<>();
        if (file.exists() && file.isFile()) {
            try {
                websiteList = JSON.parseArray(FileUtil.read(file), Website.class);
            } catch (Exception e) {
                log.error("解析异常 file->{}", file);
            }
        }
        return websiteList == null ? new ArrayList<>() : websiteList;
    }

    /**
     * 写入站点规则列表
     *
     * @param websiteList 站点规则列表
     */
    public void writeWebsiteList(List<Website> websiteList) {
        if (websiteList == null) {
            return;
        }
        File file = new File(configPath);
        File path = file.getParentFile();
        if (!path.exists()) {
            path.mkdirs();
        }
        FileUtil.write(file, JSON.toJSONString(websiteList, SerializerFeature.PrettyFormat, SerializerFeature.SortField));
    }

    /**
     * 获取配置的图书列表
     *
     * @return List<Book>
     */
    public List<Book> getBookList() {
        File path = new File(contentPath);
        List<Book> bookList = new ArrayList<>();
        if (path.exists() && path.isDirectory()) {
            File[] files = path.listFiles();
            if (files == null) {
                return bookList;
            }
            for (File file : files) {
                try {
                    Book book = JSON.parseObject(FileUtil.read(file), Book.class);
                    if (book == null) {
                        continue;
                    }
                    bookList.add(book);
                } catch (Exception e) {
                    log.error("解析异常 file->{}", file);
                }
            }
        }

        return bookList;
    }

    /**
     * 获取图书
     *
     * @return List<Book>
     */
    public Book getBook(String fileName) {
        if (StringUtils.isBlank(fileName)) {
            return null;
        }
        File file = new File(contentPath + "/" + fileName);
        try {
            Book book = JSON.parseObject(FileUtil.read(file), Book.class);
            if (book == null) {
                return null;
            }
            return book;
        } catch (Exception e) {
            log.error("解析异常 file->{}", file);
        }
        return null;
    }

    /**
     * 写入一个图书
     *
     * @param book 图书信息
     * @return boolean
     */
    public boolean writeBook(Book book) {
        if (book == null) {
            return false;
        }

        if (StringUtils.isBlank(book.getFileName())) {
            book.setFileName(wfgIdGenerator.next() + " " + book.getBookName() + ".json");
        }
        book.setIds(null);
        book.setPageSize(null);
        book.setCurrentPage(null);
        book.setContentUrl(null);
        File file = new File(contentPath + "/" + book.getFileName());
        File path = file.getParentFile();
        if (!path.exists()) {
            path.mkdirs();
        }
        return FileUtil.write(file, JSON.toJSONString(book, SerializerFeature.PrettyFormat, SerializerFeature.SortField));
    }

    /**
     * 删除一个图书
     *
     * @param book 图书信息
     * @return boolean
     */
    public boolean deleteBook(Book book) {
        File file = new File(contentPath + "/" + book.getFileName());
        if (file.exists()) {
            return file.delete();
        }
        return true;
    }
}
