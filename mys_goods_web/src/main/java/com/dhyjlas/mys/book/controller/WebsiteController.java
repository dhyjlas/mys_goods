package com.dhyjlas.mys.book.controller;

import com.dhyjlas.mys.book.entity.Website;
import com.dhyjlas.mys.book.service.BookFileService;
import com.dhyjlas.mys.consts.MsgEnum;
import com.dhyjlas.mys.exception.BusinessException;
import com.dhyjlas.mys.util.JsonResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>File: WebsiteController.java </p>
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Create By: 2024/04/03 13:08 </p>
 * <p>Company: nbhope.cn </p>
 *
 * @author yjl.yu/dhyjlas@163.com
 * @version 1.0
 */
@Slf4j
@RestController
public class WebsiteController {
    @Autowired
    private BookFileService bookFileService;

    /**
     * 站点列表
     * @param website 查询信息
     * @return JsonResult
     */
    @RequestMapping("/website/list")
    public JsonResult list(@RequestBody Website website){
        List<Website> websiteList = bookFileService.getWebsiteList();
        if(StringUtils.isNotBlank(website.getHost())) {
            websiteList = websiteList.stream().filter(e -> StringUtils.indexOf(e.getHost(), website.getHost()) > -1).collect(Collectors.toList());
        }
        return JsonResult.success().data(websiteList);
    }

    /**
     * 保存站点
     * @param website 站点信息
     * @return JsonResult
     */
    @RequestMapping("/website/save")
    public JsonResult save(@RequestBody Website website){
        if(StringUtils.isBlank(website.getHost())){
            throw new BusinessException(MsgEnum.INVALID_HOST);
        }
        if(StringUtils.isBlank(website.getBookNamePath())){
            throw new BusinessException(MsgEnum.INVALID_NAME_PATH);
        }
        if(StringUtils.isBlank(website.getChapterPath())){
            throw new BusinessException(MsgEnum.INVALID_CHAPTER_PATH);
        }
        if(StringUtils.isBlank(website.getContentPath())){
            throw new BusinessException(MsgEnum.INVALID_CONTENT_PATH);
        }

        List<Website> websiteList = bookFileService.getWebsiteList();
        Website find = websiteList.stream().filter(e->StringUtils.equalsIgnoreCase(website.getHost(), e.getHost())).findFirst().orElse(null);
        if(find == null){
            websiteList.add(website);
        }else{
            find.setHost(website.getHost());
            find.setBookNamePath(website.getBookNamePath());
            find.setChapterPath(website.getChapterPath());
            find.setContentPath(website.getContentPath());
            find.setHeader(CollectionUtils.isEmpty(website.getHeader()) ? null : website.getHeader());
            find.setReplace(CollectionUtils.isEmpty(website.getReplace()) ? null : website.getReplace());
        }

        bookFileService.writeWebsiteList(websiteList);
        return JsonResult.success();
    }

    @RequestMapping("/website/delete")
    public JsonResult delete(@RequestBody Website website){
        if(StringUtils.isBlank(website.getHost())){
            throw new BusinessException(MsgEnum.INVALID_HOST);
        }
        List<Website> websiteList = bookFileService.getWebsiteList();
        websiteList = websiteList.stream().filter(e->!StringUtils.equalsIgnoreCase(website.getHost(), e.getHost())).collect(Collectors.toList());

        bookFileService.writeWebsiteList(websiteList);
        return JsonResult.success();
    }
}
