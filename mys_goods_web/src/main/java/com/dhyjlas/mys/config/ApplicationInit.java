package com.dhyjlas.mys.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * <p>File: ApplicationInit.java </p>
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Create By: 2022/07/09 10:40 </p>
 * <p>Company: nbhope.cn </p>
 *
 * @author yjl.yu/dhyjlas@163.com
 * @version 1.0
 */
@Component
public class ApplicationInit implements ApplicationRunner {
    @Value("${windeows.web.isOpen:false}")
    private boolean isOpen;

    @Value("${windeows.web.openUrl:http://localhost:${server.port}/}")
    private String openUrl;

    @Override
    public void run(ApplicationArguments args) {
        if (isOpen) {
            String runCmd = "cmd /c start " + openUrl;
            System.out.println("运行的命令: " + runCmd);
            Runtime run = Runtime.getRuntime();
            try {
                run.exec(runCmd);
                System.out.println("启动浏览器打开项目成功");
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("启动项目自动打开浏览器失败");
            }
        }
    }
}

