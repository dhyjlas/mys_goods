package com.dhyjlas.mys.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.math.BigInteger;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * <p>File: NetworkUtils.java </p>
 * <p>Title: </p>
 * <p>Description: </p>
 *
 * @author yjl.yu/dhyjlas@163.com
 * @version 1.0
 */
@Slf4j
public class NetworkUtils {
    private static final String[] IP_HEADER_CANDIDATES = {"X-Forwarded-For", "Proxy-Client-IP", "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR", "HTTP_X_FORWARDED", "HTTP_X_CLUSTER_CLIENT_IP", "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR", "HTTP_FORWARDED", "HTTP_VIA", "REMOTE_ADDR"};

    private NetworkUtils() {
        throw new AssertionError("No NetworkUtils instances for you!");
    }

    public static List<String> listLocalIPV6Address() throws SocketException {
        List<String> localAddress = new ArrayList<>();
        Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
        while (networkInterfaces.hasMoreElements()) {
            NetworkInterface networkInterface = networkInterfaces.nextElement();
            Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
            while (inetAddresses.hasMoreElements()) {
                InetAddress address = inetAddresses.nextElement();
                if (address instanceof Inet6Address && address.isSiteLocalAddress() && !address.isLoopbackAddress() && !address.isLinkLocalAddress()) {
                    String ip = address.getHostAddress();
                    localAddress.add(ip);
                }
            }
        }
        return localAddress;
    }

    public static List<String> listLocalIPV4Address() throws SocketException {
        List<String> localAddress = new ArrayList<>();
        Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
        while (networkInterfaces.hasMoreElements()) {
            NetworkInterface networkInterface = networkInterfaces.nextElement();
            Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
            while (inetAddresses.hasMoreElements()) {
                InetAddress address = inetAddresses.nextElement();
                if (address instanceof Inet4Address && address.isSiteLocalAddress() && !address.isLoopbackAddress() && !address.isLinkLocalAddress()) {
                    String ip = address.getHostAddress();
                    localAddress.add(ip);
                }
            }
        }
        return localAddress;
    }

    public static String getHostAddr() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.error("getHostAddr异常", e);
        }
        return "127.0.0.1";
    }

    public static String getHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            log.error("getHostName异常", e);
        }
        return "UNKNOWN";
    }

    /**
     * 判断指定的IP地址是否为有效地址
     *
     * @param ip IP地址
     * @return boolean true：有效，false：无效
     */
    private static boolean isValidAddress(String ip) {
        return (StringUtils.isNotBlank(ip) && !"unknown".equalsIgnoreCase(ip));
    }

    /**
     * 获取 字符串类型的客户端IP地址（如需真实IP，Nginx需要增加--with-http_realip_module）
     * 如果经过多个反向代理，会出现多个ip地址，第一个为正常的客户端地址
     *
     * @param request HttpServletRequest
     * @return String 客户端IP地址
     */
    public static String getClientAddress(final HttpServletRequest request) {
        String ip = null;
        for (String header : IP_HEADER_CANDIDATES) {
            ip = request.getHeader(header);
            if (isValidAddress(ip)) {
                break;
            }
        }
        if (!isValidAddress(ip)) {
            ip = request.getRemoteAddr();
        }
        if (StringUtils.isNotBlank(ip) && ip.contains(",")) {
            String[] strings = ip.split(",");
            ip = strings[0];
        }
        return ip;
    }

    /**
     * 将字符串格式IPv4网络地址转换为long型地址
     *
     * @param ipv4 IPV4网络地址
     * @return long long型IPV4地址
     */
    public static long ip2long(final String ipv4) {

        String[] splits = ipv4.split("\\.");
        long l = 0;
        l = l + (Long.valueOf(splits[0], 10)) << 24;
        l = l + (Long.valueOf(splits[1], 10) << 16);
        l = l + (Long.valueOf(splits[2], 10) << 8);
        l = l + (Long.valueOf(splits[3], 10));
        return l;
    }

    /**
     * 将long型IPV4地址转换为字符串格式
     *
     * @param l long型IPV4地址
     * @return String 字符串格式IPv4网络地址
     */
    public static String long2ip(final long l) {
        String ip = "";
        ip = ip + (l >>> 24);

        ip = ip + "." + ((0x00ffffff & l) >>> 16);
        ip = ip + "." + ((0x0000ffff & l) >>> 8);
        ip = ip + "." + (0x000000ff & l);
        return ip;
    }

    public static BigInteger ipv6ToInt(final String ipv6) {
        int compressIndex = ipv6.indexOf("::");
        if (compressIndex != -1) {
            String part1s = ipv6.substring(0, compressIndex);
            String part2s = ipv6.substring(compressIndex + 1);
            BigInteger part1 = ipv6ToInt(part1s);
            BigInteger part2 = ipv6ToInt(part2s);
            int part1hasDot = 0;
            char[] ch = part1s.toCharArray();
            for (char c : ch) {
                if (c == ':') {
                    part1hasDot++;
                }
            }
            return part1.shiftLeft(16 * (7 - part1hasDot)).add(part2);
        }
        String[] str = ipv6.split(":");
        BigInteger big = BigInteger.ZERO;
        for (int i = 0; i < str.length; i++) {
            //::1
            if (str[i].isEmpty()) {
                str[i] = "0";
            }
            big = big.add(BigInteger.valueOf(Long.valueOf(str[i], 16)).shiftLeft(16 * (str.length - i - 1)));
        }
        return big;
    }

    public static String intToIpv6(BigInteger big) {
        String str = "";
        BigInteger ff = BigInteger.valueOf(0xffff);
        for (int i = 0; i < 8; i++) {
            str = big.and(ff).toString(16) + ":" + str;
            big = big.shiftRight(16);
        }
        //去掉最后的：号
        str = str.substring(0, str.length() - 1);
        return str.replaceFirst("(^|:)(0+(:|$)){2,8}", "::");
    }

    /**
     * 将精简的ipv6地址扩展为全长度的ipv6地址
     *
     * @param strIpv6 精简的ipv6地址
     * @return String 全长度的ipv6地址
     */
    public static String completeIpv6(String strIpv6) {
        BigInteger big = ipv6ToInt(strIpv6);
        String str = big.toString(16);
        String completeIpv6Str = "";
        while (str.length() != 32) {
            str = "0" + str;
        }
        for (int i = 0; i <= str.length(); i += 4) {
            completeIpv6Str += str.substring(i, i + 4);
            if ((i + 4) == str.length()) {
                break;
            }
            completeIpv6Str += ":";
        }
        return completeIpv6Str;
    }
}
