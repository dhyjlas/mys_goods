package com.dhyjlas.mys.config;

import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.ToStringSerializer;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

/**
 * <p>File: FastJsonConfigure.java </p>
 * <p>Title: </p>
 * <p>Description: </p>
 *
 * @author yjl.yu/dhyjlas@163.com
 * @version 1.0
 */
@Slf4j
public class MyJsonConfig extends FastJsonConfig {
    public MyJsonConfig() {
        log.info("==========初始化fastjson配置==========");
        this.setCharset(StandardCharsets.UTF_8);
        this.setSerializeConfig(initializeSerializeConfig());
        this.setParserConfig(ParserConfig.getGlobalInstance());
        this.setSerializerFeatures(initializeSerializerFeature());
        this.setWriteContentLength(Boolean.TRUE);
        this.setDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    private SerializerFeature[] initializeSerializerFeature() {
        return new SerializerFeature[]{SerializerFeature.PrettyFormat,
                SerializerFeature.DisableCircularReferenceDetect,
                SerializerFeature.WriteNullStringAsEmpty,
                SerializerFeature.WriteNullListAsEmpty,
                //SerializerFeature.WriteMapNullValue,
                SerializerFeature.WriteNullNumberAsZero,
                SerializerFeature.WriteNullBooleanAsFalse};
    }

    private SerializeConfig initializeSerializeConfig() {
        SerializeConfig serializeConfig = SerializeConfig.globalInstance;

        serializeConfig.put(BigInteger.class, ToStringSerializer.instance);
        serializeConfig.put(BigDecimal.class, ToStringSerializer.instance);
        serializeConfig.put(Long.class, ToStringSerializer.instance);
        serializeConfig.put(Long.TYPE, ToStringSerializer.instance);
        serializeConfig.put(Double.class, ToStringSerializer.instance);
        serializeConfig.put(Double.TYPE, ToStringSerializer.instance);
        serializeConfig.put(Float.class, ToStringSerializer.instance);
        serializeConfig.put(Float.TYPE, ToStringSerializer.instance);

        return serializeConfig;
    }
}
