package com.dhyjlas.mys.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.*;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.apache.hc.core5.util.Timeout;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>File: HttpUtil.java </p>
 * <p>Title: </p>
 * <p>Description: </p>
 *
 * @author yjl.yu/dhyjlas@163.com
 * @version 1.0
 */
public class HttpUtil {
    private static final RequestConfig requestConfig = RequestConfig.custom()
            .setConnectTimeout(Timeout.ofMilliseconds(30000L))
            .setConnectionRequestTimeout(Timeout.ofMilliseconds(30000L))
            .setResponseTimeout(Timeout.ofMilliseconds(30000L))
            .build();

    /**
     * 发送get请求
     *
     * @param uri URL
     * @return String
     */
    public static Result get(String uri) {
        return get(uri, "utf-8", null);
    }

    /**
     * 发送get请求
     *
     * @param uri    URL
     * @param header 请求头
     * @return String
     */
    public static Result get(String uri, Map<String, String> header) {
        return get(uri, "utf-8", header);
    }

    /**
     * 发送get请求
     *
     * @param uri         URL
     * @param charsetName 编码
     * @param header      请求头
     * @return String
     */
    public static Result get(String uri, String charsetName, Map<String, String> header) {
        HttpGet httpGet = new HttpGet(uri);
        return executeRequest(httpGet, charsetName, header);
    }


    /**
     * 发送post请求
     *
     * @param uri URL
     * @return String
     */
    public static Result post(String uri) {
        return post(uri, null);
    }

    /**
     * 发送post请求
     *
     * @param uri URL
     * @param map 请求参数
     * @return String
     */
    public static Result post(String uri, Map<String, String> map) {
        return post(uri, map, null);
    }

    /**
     * 发送post请求
     *
     * @param uri         URL
     * @param map         请求参数
     * @param charsetName 编码
     * @return String
     */
    public static Result post(String uri, Map<String, String> map, String charsetName) {
        return post(uri, null, map, charsetName);
    }

    /**
     * 发送post请求
     *
     * @param uri         URL
     * @param header      请求头
     * @param map         请求参数
     * @param charsetName 编码
     * @return String
     */
    public static Result post(String uri, Map<String, String> header, Map<String, String> map, String charsetName) {
        HttpPost httpPost = new HttpPost(uri);
        HttpEntity httpEntity = getEntityFromMap(map, charsetName);
        httpPost.setEntity(httpEntity);
        return executeRequest(httpPost, charsetName, header);
    }

    /**
     * 发送post请求
     *
     * @param url         URL
     * @param headers     请求头
     * @param requestBody 请求体
     * @param charset     编码
     * @return String
     */
    public static Result post(String url, Map<String, String> headers, String requestBody, String charset) {
        HttpPost httpPost = new HttpPost(url);
        StringEntity entity = new StringEntity(requestBody, Charset.forName(charset));
        httpPost.setEntity(entity);
        return executeRequest(httpPost, charset, headers);
    }

    /**
     * 发送post请求
     *
     * @param request     HttpUriRequestBase
     * @param charsetName 编码
     * @param header      请求头
     * @return String
     */
    private static Result executeRequest(HttpUriRequestBase request, String charsetName, Map<String, String> header) {
        Result result = new Result();

        if (null == request) {
            throw new NullPointerException("HttpUriRequestBase为空！");
        }
        request.setConfig(requestConfig);
        String responseText;
        if (null != header) {
            for (Map.Entry<String, String> entry : header.entrySet()) {
                request.setHeader(entry.getKey(), entry.getValue());
            }
        }

        try (CloseableHttpClient httpclient = HttpClients.custom().setDefaultRequestConfig(requestConfig).build()) {
            try (CloseableHttpResponse response = httpclient.execute(request)) {
                if (null == response) {
                    return null;
                }
                int code = response.getCode();
                // 处理重定向
                if ((code == HttpStatus.SC_MOVED_TEMPORARILY) || (code == HttpStatus.SC_MOVED_PERMANENTLY)
                        || (code == HttpStatus.SC_SEE_OTHER) || (code == HttpStatus.SC_TEMPORARY_REDIRECT)) {
                    Header redirectLocation = response.getFirstHeader("Location");
                    String newUri = redirectLocation.getValue();
                    if (StringUtils.isNotBlank(newUri)) {
                        request.setUri(URI.create(newUri));
                        return executeRequest(request, charsetName, header);
                    }
                }
                HttpEntity httpEntity = response.getEntity();
                result.setCode(response.getCode());

                Header[] cookieHeaders = response.getHeaders("Set-Cookie");
                if(cookieHeaders.length > 0) {
                    for (Header cookieHeader : cookieHeaders) {
                        if (cookieHeader != null && !"".equalsIgnoreCase(cookieHeader.getValue())) {
                            String cookies = cookieHeader.getValue();
                            result.addCookies(cookies);
                        }
                    }
                }

                if (StringUtils.isNotBlank(charsetName)) {
                    responseText = EntityUtils.toString(httpEntity, charsetName);
                } else {
                    responseText = EntityUtils.toString(httpEntity);
                }
                result.setText(responseText);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 根据Map请求参数获取HTTPEntity
     *
     * @param map         Map<String, String>格式的请求参数
     * @param charsetName 编码名称
     * @return HttpEntity HttpEntity
     */
    private static HttpEntity getEntityFromMap(Map<String, String> map, String charsetName) {
        List<NameValuePair> list = getListFromMap(map);
        return getEntityFromList(list, charsetName);
    }

    /**
     * 根据Map请求参数获取get请求格式的字符串
     *
     * @param map         Map<String,String>请求参数
     * @param charsetName 请求编码名称
     * @return String get请求格式的字符串
     */
    private static String getStringFromMap(Map<String, String> map, String charsetName) {
        String httpParameter = null;
        List<NameValuePair> list = getListFromMap(map);
        HttpEntity entity = getEntityFromList(list, charsetName);
        try {
            httpParameter = EntityUtils.toString(entity);
        } catch (ParseException e) {
            e.getStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return httpParameter;
    }

    /**
     * 将Map<String,String>请求参数转化为List<NameValuePair>
     *
     * @param map Map<String, String>请求参数
     * @return List<NameValuePair> List<NameValuePair>
     */
    private static List<NameValuePair> getListFromMap(Map<String, String> map) {
        List<NameValuePair> list = new ArrayList<>();
        if (map != null) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                list.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
        }
        return list;
    }

    /**
     * 将List<NameValuePair>转化为HttpEneity请求参数
     *
     * @param list        List<NameValuePair>
     * @param charsetName 请求编码
     * @return HttpEntity HttpEntity
     */
    private static HttpEntity getEntityFromList(List<NameValuePair> list, String charsetName) {
        UrlEncodedFormEntity entity;
        if (StringUtils.isBlank(charsetName)) {
            charsetName = "utf-8";
        }
        entity = new UrlEncodedFormEntity(list, Charset.forName(charsetName));
        return entity;
    }

    @Data
    @Slf4j
    public static class Result{
        private int code;
        private JSONObject json;
        private JSONObject cookies = new JSONObject();
        private String text;

        public JSONObject getJson() {
            try {
                return JSON.parseObject(text);
            }catch (Exception e){
                log.error("返回的类型不为json text->{}", text);
                return new JSONObject();
            }
        }

        public void addCookies(String cookiesStr) {
            String[] items = cookiesStr.split(";");
            for(String item : items){
                int index = item.indexOf('=');
                if(index >= 0) {
                    cookies.put(item.substring(0, index).trim(), item.substring(index + 1).trim());
                }
            }
        }

        @Override
        public String toString(){
            return JSON.toJSONString(this);
        }
    }
}
