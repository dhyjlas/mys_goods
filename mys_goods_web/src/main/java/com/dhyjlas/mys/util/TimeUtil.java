package com.dhyjlas.mys.util;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * <p>File: TimeUtil.java </p>
 * <p>Title: </p>
 * <p>Description: </p>
 *
 * @author yjl.yu/dhyjlas@163.com
 * @version 1.0
 */
public class TimeUtil {
    public static String getCurrentTime() {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now());
    }

    public static String getCurrentTime(String format) {
        return DateTimeFormatter.ofPattern(format).format(LocalDateTime.now());
    }

    public static LocalDateTime getLocalDateTime() {
        return LocalDateTime.now();
    }

    public static String getPlusTime(long seconds) {
        LocalDateTime localDateTime = LocalDateTime.now().plusSeconds(seconds);
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(localDateTime);
    }

    public static long getCurrentMillisTime() {
        return System.currentTimeMillis();
    }

    public static long getNtpServiceTime() {
        String ntpServer = "ntp.aliyun.com";
        try (NTPUDPClient client = new NTPUDPClient()) {
            InetAddress inetAddress = InetAddress.getByName(ntpServer);
            TimeInfo timeInfo = client.getTime(inetAddress);
            return timeInfo.getMessage().getTransmitTimeStamp().getTime();
        } catch (Exception e) {
            e.printStackTrace();
            return System.currentTimeMillis();
        }
    }

    /**
     * 获取时间偏移量 本地时间-NTP时间
     *
     * @return
     */
    public static long getTimeOffset() {
        try {
            int sum = 0;
            long start = System.currentTimeMillis();
            for (int i = 0; i < 10; i++) {
                sum += (System.currentTimeMillis() - getNtpServiceTime());
            }
            long end = System.currentTimeMillis();
            return sum / 10 - (end - start) / 10;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
