package com.dhyjlas.mys.book.service;

import com.dhyjlas.mys.book.entity.Book;
import com.dhyjlas.mys.book.entity.KeyValue;
import com.dhyjlas.mys.book.entity.Website;
import com.dhyjlas.mys.util.LocalCache;
import com.dhyjlas.mys.util.XPathCrawlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.NumberUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * <p>File: BookService.java </p>
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Create By: 2024/04/03 09:49 </p>
 * <p>Company: nbhope.cn </p>
 *
 * @author yjl.yu/dhyjlas@163.com
 * @version 1.0
 */
@Slf4j
@Service
public class BookService {
    public final Pattern HTTP_PATTERN = Pattern.compile("(http|https)://([\\w.]+/?)\\S*");

    @Value("${book.download.path}")
    private String downloadPath;

    private final BookFileService bookFileService;

    public BookService(BookFileService bookFileService) {
        this.bookFileService = bookFileService;
    }

    /**
     * 下载书
     *
     * @param book 书信息
     * @return boolean
     * @throws IOException          IOException
     * @throws InterruptedException InterruptedException
     */
    public boolean download(Book book) throws IOException, InterruptedException {
        LocalCache.BOOK_DOWNLOAD.put(book.getFileName(), 0);

        int start = 0;
        if (book.getChapterNum() != null && book.getChapterNum() > 0 && book.getProgress() != null && book.getProgress() > 0) {
            File oldFile = new File(downloadPath + "/" + book.getBookName() + ".txt");
            if (oldFile.exists() && oldFile.isFile()) {
                start = book.getProgress();
            }
        }

        Map<String, String> header = labelValueToMap(book.getHeader());
        Document document = XPathCrawlUtils.connAndGetDocument(book.getChapterUrl(), header);
        List<String> textList = XPathCrawlUtils.parseToList(document, book.getChapterPath() + "/text()");
        List<String> urlList = XPathCrawlUtils.parseToList(document, book.getChapterPath() + "/@href");

        int size = Math.min(textList.size(), urlList.size());
        if (start >= size) {
            return true;
        }

        Book newBook = bookFileService.getBook(book.getFileName());
        newBook.setChapterNum(size);
        bookFileService.writeBook(newBook);

        File pFile = new File(downloadPath);
        if (!pFile.exists()) {
            pFile.mkdirs();
        }

        Path path = Paths.get(downloadPath + "/" + book.getBookName() + ".txt");
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8, start == 0 ? StandardOpenOption.CREATE : StandardOpenOption.APPEND)) {
            if (start == 0) {
                writer.write(book.getBookName());
            }
            writer.newLine();
            writer.newLine();
            for (int i = start; i < size; i++) {
                log.info("正在下载【{}】({}/{})", textList.get(i), i + 1, size);
                String content = "";
                for (int j = 0; j < 10; j++) {
                    try {
                        content = getContent(book, urlList.get(i), header);
                    } catch (Exception e) {
                        log.error("失败重试 {}", j);
                        Thread.sleep(j * 1000);
                    }

                    if (StringUtils.isNotBlank(content)) {
                        break;
                    }
                }
                writer.write(textList.get(i));
                writer.newLine();
                writer.write(content);
                writer.newLine();
                writer.newLine();
                writer.flush();

                LocalCache.BOOK_DOWNLOAD.put(book.getFileName(), i + 1);
            }
            writer.flush();
        }

        LocalCache.BOOK_DOWNLOAD.invalidate(book.getFileName());
        newBook = bookFileService.getBook(book.getFileName());
        newBook.setProgress(size);
        bookFileService.writeBook(newBook);

        return true;
    }


    public String getContent(Book book, String chapterUrl) throws IOException {
        Map<String, String> header = labelValueToMap(book.getHeader());
        return this.getContent(book, chapterUrl, header);
    }

    public String getContent(Book book, String chapterUrl, Map<String, String> header) throws IOException {
        String url;
        if (HTTP_PATTERN.matcher(chapterUrl).matches()) {
            url = chapterUrl;
        } else if ("/".equals(chapterUrl.substring(0, 1))) {
            URL fullUrl = new URL(book.getChapterUrl());
            String fullProtocolHost = fullUrl.getProtocol() + "://" + fullUrl.getHost();
            url = fullProtocolHost + chapterUrl;
        } else {
            String url0 = book.getChapterUrl().endsWith("/") ? book.getChapterUrl() : book.getChapterUrl() + "/";
            url = url0 + chapterUrl;
        }

        Document document = XPathCrawlUtils.connAndGetDocument(url, header);
        String content = XPathCrawlUtils.parseToString(document, book.getContentPath());
        if (book.getReplace() != null) {
            for (KeyValue keyValue : book.getReplace()) {
                content = content.replaceAll(keyValue.getKey(), keyValue.getValue());
            }
        }
        content = content.replaceAll("<[^>]*>", "").replaceAll(" ", "");
        return content;
    }

    private Map<String, String> labelValueToMap(List<KeyValue> keyValues) {
        if (CollectionUtils.isEmpty(keyValues)) {
            return null;
        }
        Map<String, String> map = new HashMap<>(keyValues.size());
        for (KeyValue keyValue : keyValues) {
            map.put(keyValue.getKey(), keyValue.getValue());
        }
        return map;
    }

    /**
     * 获取书名
     *
     * @param website 站点信息
     * @param url     url
     * @return String
     * @throws InterruptedException InterruptedException
     */
    public String getBookName(Website website, String url) throws InterruptedException {
        for (int i = 0; i < 3; i++) {
            try {
                Document document = XPathCrawlUtils.connAndGetDocument(url, labelValueToMap(website.getHeader()));
                return XPathCrawlUtils.parseToString(document, website.getBookNamePath());
            } catch (Exception e) {
                log.error("失败重试 {}", i);
                Thread.sleep(i * 1000);
            }
        }
        return "";
    }

    /**
     * 获取章节信息
     *
     * @param book 小说信息
     * @return List<KeyValue>
     */
    public List<KeyValue> getChapterList(Book book) throws IOException {
        Map<String, String> header = labelValueToMap(book.getHeader());
        Document document = XPathCrawlUtils.connAndGetDocument(book.getChapterUrl(), header);
        List<String> textList = XPathCrawlUtils.parseToList(document, book.getChapterPath() + "/text()");
        List<String> urlList = XPathCrawlUtils.parseToList(document, book.getChapterPath() + "/@href");

        int size = Math.min(textList.size(), urlList.size());
        List<KeyValue> keyValueList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            keyValueList.add(new KeyValue(urlList.get(i), textList.get(i)));
        }
        return keyValueList;
    }
}
