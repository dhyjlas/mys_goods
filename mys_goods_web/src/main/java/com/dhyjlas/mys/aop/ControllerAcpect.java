package com.dhyjlas.mys.aop;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.dhyjlas.mys.util.NetworkUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>File: ControllerAcpect.java </p>
 * <p>Title: </p>
 * <p>Description: </p>
 *
 * @author yjl.yu/dhyjlas@163.com
 * @version 1.0
 */
@Slf4j
@Aspect
@Component
public class ControllerAcpect {
    @Pointcut("execution(* com.dhyjlas.mys..*Controller.*(..))")
    public void controller() {
    }

    @Around("controller()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        // 打印请求日志
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert attributes != null;
        HttpServletRequest request = attributes.getRequest();
        JSONArray params = new JSONArray();
        for (Object object : pjp.getArgs()) {
            try {
                JSON.toJSONString(object);
                params.add(object);
            } catch (Exception e) {
                params.add(object.toString());
            }
        }

        String content = "┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓\n" +
                String.format("┃  URL            : %s\n", request.getRequestURL().toString()) +
                String.format("┃  HTTP Method    : %s\n", request.getMethod()) +
                String.format("┃  Class Method   : %s.%s\n", pjp.getSignature().getDeclaringTypeName(), pjp.getSignature().getName()) +
                String.format("┃  IP             : %s\n", NetworkUtils.getClientAddress(request)) +
                String.format("┃  Request Args   : %s\n", JSON.toJSONString(params)) +
                "┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛";
        log.info("\n{}", content);

        return pjp.proceed();
    }
}
