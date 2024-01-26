package com.dhyjlas.mys;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * <p>File: MysGoodsApplication.java </p>
 * <p>Title: </p>
 * <p>Description: </p>
 *
 * @author yjl.yu/dhyjlas@163.com
 * @version 1.0
 */
@EnableScheduling
@EnableCaching
@SpringBootApplication
public class MysGoodsApplication {

    public static void main(String[] args) {
        SpringApplication.run(MysGoodsApplication.class, args);
    }

}
