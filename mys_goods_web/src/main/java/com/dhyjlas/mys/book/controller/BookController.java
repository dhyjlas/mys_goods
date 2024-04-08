package com.dhyjlas.mys.book.controller;

import com.dhyjlas.mys.book.entity.Book;
import com.dhyjlas.mys.book.entity.Website;
import com.dhyjlas.mys.book.service.BookFileService;
import com.dhyjlas.mys.book.service.BookService;
import com.dhyjlas.mys.consts.MsgEnum;
import com.dhyjlas.mys.util.JsonResult;
import com.dhyjlas.mys.util.LocalCache;
import com.dhyjlas.mys.util.ThreadPoolUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>File: BookController.java </p>
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Create By: 2024/04/03 15:36 </p>
 * <p>Company: nbhope.cn </p>
 *
 * @author yjl.yu/dhyjlas@163.com
 * @version 1.0
 */
@Slf4j
@RestController
public class BookController {
    @Value("${book.download.path}")
    private String downloadPath;

    private final BookService bookService;

    private final BookFileService bookFileService;

    public BookController(BookService bookService, BookFileService bookFileService) {
        this.bookService = bookService;
        this.bookFileService = bookFileService;
    }

    /**
     * 小说列表
     *
     * @param book 小说信息
     * @return JsonResult
     */
    @RequestMapping("/book/list")
    public JsonResult list(@RequestBody Book book) {
        List<Book> bookList = bookFileService.getBookList();
        if (StringUtils.isNotBlank(book.getBookName())) {
            bookList = bookList.stream()
                    .filter(e -> StringUtils.indexOf(e.getBookName(), book.getBookName()) > -1)
                    .collect(Collectors.toList());
        }
        bookList.forEach(e -> {
            Integer progress = (Integer) LocalCache.BOOK_DOWNLOAD.getIfPresent(e.getFileName());
            if (progress != null) {
                e.setProgress(progress);
            }
        });
        return JsonResult.success().data(bookList);
    }

    /**
     * 校验目录地址
     *
     * @param book 小说信息
     * @return JsonResult
     */
    @RequestMapping("/book/check")
    public JsonResult check(@RequestBody Book book) throws InterruptedException {
        List<Website> websiteList = bookFileService.getWebsiteList();
        for (Website website : websiteList) {
            if (StringUtils.indexOf(book.getChapterUrl(), "://" + website.getHost() + "/") > -1) {
                book.setBookName(bookService.getBookName(website, book.getChapterUrl()));
                book.setChapterPath(website.getChapterPath());
                book.setContentPath(website.getContentPath());
                book.setHeader(website.getHeader());
                book.setReplace(website.getReplace());
                return JsonResult.success().data(book);
            }
        }
        return JsonResult.success();
    }

    /**
     * 保存小说信息
     *
     * @param book 小说信息
     * @return JsonResult
     */
    @RequestMapping("/book/save")
    public JsonResult save(@RequestBody Book book) {
        Book oldBook = bookFileService.getBook(book.getFileName());
        if (oldBook != null) {
            book.setProgress(oldBook.getProgress());
            book.setChapterNum(oldBook.getChapterNum());
        }
        bookFileService.writeBook(book);
        return JsonResult.success();
    }

    /**
     * 删除小说信息
     *
     * @param book 小说信息
     * @return JsonResult
     */
    @RequestMapping("/book/delete")
    public JsonResult delete(@RequestBody Book book) {
        bookFileService.deleteBook(book);
        return JsonResult.success();
    }

    /**
     * 更新章节
     *
     * @param book 小说信息
     * @return JsonResult
     */
    @RequestMapping("/book/begin")
    public JsonResult begin(@RequestBody Book book) {
        if (LocalCache.BOOK_DOWNLOAD.getIfPresent(book.getFileName()) != null) {
            return JsonResult.failure(MsgEnum.ERROR_BEGIN);
        }
        Book book1 = bookFileService.getBook(book.getFileName());
        ThreadPoolUtils.INSTANCE.execute(() -> {
            try {
                bookService.download(book1);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            } finally {
                LocalCache.BOOK_DOWNLOAD.invalidate(book1.getFileName());
            }
        });
        return JsonResult.success();
    }

    /**
     * 判断是否可以下载
     *
     * @param book 小说信息
     * @return JsonResult
     */
    @RequestMapping("/book/isDownload")
    public JsonResult isDownload(@RequestBody Book book) {
        if (LocalCache.BOOK_DOWNLOAD.getIfPresent(book.getFileName()) != null) {
            return JsonResult.failure(MsgEnum.ERROR_BEGIN);
        }

        String filename = downloadPath + "/" + book.getBookName() + ".txt ";
        File file = new File(filename);
        if (!file.exists() || file.isDirectory()) {
            return JsonResult.failure(MsgEnum.ERROR_NOT_PRESENT);
        }
        return JsonResult.success();
    }

    /**
     * 下载小说
     *
     * @param bookName 小说命
     * @param response HttpServletResponse
     * @return JsonResult
     * @throws IOException IOException
     */
    @RequestMapping("/book/download/{bookName}")
    public JsonResult download(@PathVariable("bookName") String bookName, HttpServletResponse response) throws IOException {
        String filename = downloadPath + "/" + bookName + ".txt ";
        File file = new File(filename);
        if (!file.exists() || file.isDirectory()) {
            return JsonResult.failure(MsgEnum.ERROR_NOT_PRESENT);
        }
        try (InputStream inputStream = new FileInputStream(filename); ServletOutputStream outputStream = response.getOutputStream()) {
            response.reset();
            response.setContentType("application/octet-stream");
            response.addHeader("Content-Disposition", "attachment; filename=\"" + URLEncoder.encode(bookName + ".txt", StandardCharsets.UTF_8) + "\"");
            byte[] b = new byte[1024];
            int len;
            while ((len = inputStream.read(b)) > 0) {
                outputStream.write(b, 0, len);
            }
        }
        return JsonResult.success();
    }

    @RequestMapping("/book/chapter")
    public JsonResult chapterList(@RequestBody Book book) throws IOException {
        return JsonResult.success().data(bookService.getChapterList(book));
    }

    @RequestMapping("/book/content")
    public JsonResult content(@RequestBody Book book) throws IOException {
        return JsonResult.success().data(bookService.getContent(book, book.getContentUrl()));
    }
}
