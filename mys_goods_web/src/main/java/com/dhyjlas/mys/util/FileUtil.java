package com.dhyjlas.mys.util;

import com.dhyjlas.mys.consts.MsgEnum;
import com.dhyjlas.mys.exception.BusinessException;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * <p>File: FileUtil.java </p>
 * <p>Title: 文件工具类</p>
 * <p>Description: </p>
 *
 * @author yjl.yu/dhyjlas@163.com
 * @version 1.0
 */
public class FileUtil {
    /**
     * 读取一个文件
     *
     * @param file 读取的文件
     * @return String
     */
    public static String read(File file) {
        try (InputStream in = new FileInputStream(file)) {
            StringWriter writer = new StringWriter();
            IOUtils.copy(in, writer, StandardCharsets.UTF_8);
            return writer.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * 写入一个文件
     *
     * @param file 写入的文件
     * @param context 写入的内容
     * @return boolean
     */
    public static boolean write(File file, String context) {
        File path = file.getParentFile();
        if(!path.exists()){
            if(!path.mkdirs()){
                throw new BusinessException(MsgEnum.ERROR_MSDIR);
            }
        }
        try (OutputStream outputStream = new FileOutputStream(file)) {
            byte[] bytesToWrite = context.getBytes(StandardCharsets.UTF_8);
            IOUtils.write(bytesToWrite, outputStream);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
