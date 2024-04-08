package com.dhyjlas.mys.util;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.seimicrawler.xpath.JXDocument;
import org.seimicrawler.xpath.JXNode;
import org.springframework.util.StringUtils;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class XPathCrawlUtils {
    /**
     * 连接并获取数据
     *
     * @param url URL
     * @return Document
     * @throws IOException IOException
     */
    public static Document connAndGetDocument(String url, Map<String, String> headers) throws IOException {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] x509Certificates, String s) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] x509Certificates, String s) {
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            }};

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
        } catch (Exception ignored) {
        }

        Connection connection = Jsoup.connect(url).ignoreContentType(true).timeout(10000);
        if (headers != null) {
            for (String key : headers.keySet()) {
                connection.header(key, headers.get(key));
            }
        }
        return connection.get();
    }

    /**
     * 连接并获取数据
     *
     * @param url URL
     * @return Document
     * @throws IOException IOException
     */
    public static Document connAndPostDocument(String url, Map<String, String> map, Map<String, String> headers) throws IOException {
        Connection connection = Jsoup.connect(url).timeout(30000);
        if (map != null) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                //添加参数
                connection.data(entry.getKey(), entry.getValue());
            }
        }
        if (headers != null) {
            for (String key : headers.keySet()) {
                connection.header(key, headers.get(key));
            }
        }
        return connection.post();
    }

    /**
     * 解析返回字符串
     *
     * @param document Document
     * @param xpath Xpath
     * @return String
     */
    public static String parseToString(Document document, String xpath) {
        if (StringUtils.isEmpty(xpath)) {
            return "";
        }
        JXDocument jxd = JXDocument.create(document);
        List<JXNode> jxNodes = jxd.selN(xpath);
        for (JXNode node : jxNodes) {
            return node.toString();
        }
        return "";
    }

    /**
     * 解析返回数组
     *
     * @param document Document
     * @param xpath Xpath
     * @return List<String>
     */
    public static List<String> parseToList(Document document, String xpath) {
        JXDocument jxd = JXDocument.create(document);
        List<JXNode> jxNodes = jxd.selN(xpath);
        List<String> list = new ArrayList<>();
        for (JXNode node : jxNodes) {
            list.add(node.toString());
        }
        return list;
    }
}
