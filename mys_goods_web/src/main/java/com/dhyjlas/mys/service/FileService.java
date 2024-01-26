package com.dhyjlas.mys.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.dhyjlas.mys.consts.MsgEnum;
import com.dhyjlas.mys.entity.AddressInfo;
import com.dhyjlas.mys.entity.ExchangeInfo;
import com.dhyjlas.mys.entity.UserInfo;
import com.dhyjlas.mys.exception.BusinessException;
import com.dhyjlas.mys.util.FileUtil;
import com.hy.corecode.idgen.WFGIdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>File: UserService.java </p>
 * <p>Title: 文件操作相关</p>
 * <p>Description: </p>
 *
 * @author yjl.yu/dhyjlas@163.com
 * @version 1.0
 */
@Slf4j
@Service
public class FileService {
    private final WFGIdGenerator wfgIdGenerator;

    @Value("${user.path}")
    private String userPath;

    @Value("${exchange.path}")
    private String exchangePath;

    public FileService(WFGIdGenerator wfgIdGenerator) {
        this.wfgIdGenerator = wfgIdGenerator;
    }

    /**
     * 读取用户文件列表
     *
     * @return List<UserInfo>
     */
    public List<UserInfo> listUserInfo() {
        File path = new File(userPath);
        List<UserInfo> userInfoList = new ArrayList<>();
        if (path.exists() && path.isDirectory()) {
            File[] files = path.listFiles();
            if (files == null) {
                return userInfoList;
            }
            for (File file : files) {
                try {
                    UserInfo userInfo = JSON.parseObject(FileUtil.read(file), UserInfo.class);
                    if (StringUtils.equals(file.getName(), userInfo.getMys_uid() + ".json")) {
                        userInfoList.add(userInfo);
                    }
                } catch (Exception e) {
                    log.error("解析异常 file->{}", file);
                }
            }
        }

        return userInfoList;
    }

    /**
     * 读取一个用户文件
     *
     * @return List<UserInfo>
     */
    public UserInfo findUserInfo(String uid) {
        File file = new File(userPath + "/" + uid + ".json");
        if (!file.exists() || file.isDirectory()) {
            throw new BusinessException(MsgEnum.INVALID_USER_FILE);
        }
        try {
            return JSON.parseObject(FileUtil.read(file), UserInfo.class);
        } catch (Exception e) {
            throw new BusinessException(MsgEnum.ERROR_USER_FILE);
        }

    }

    /**
     * 写入一个用户文件
     *
     * @param userInfo 用户信息
     * @return boolean
     */
    public boolean writeUserInfo(UserInfo userInfo) {
        if (userInfo == null || StringUtils.isBlank(userInfo.getMys_uid())) {
            return false;
        }

        File file = new File(userPath + "/" + userInfo.getMys_uid() + ".json");
        return FileUtil.write(file, JSON.toJSONString(userInfo, SerializerFeature.PrettyFormat, SerializerFeature.SortField));
    }

    /**
     * 删除一个用户文件
     *
     * @param userInfo 用户信息
     * @return
     */
    public boolean deleteUserInfo(UserInfo userInfo) {
        File file = new File(userPath + "/" + userInfo.getMys_uid() + ".json");
        if (file.exists()) {
            return file.delete();
        }
        return true;
    }

    /**
     * 读取兑换文件列表
     *
     * @return List<ExchangeInfo>
     */
    public List<ExchangeInfo> listExchangeInfo() {
        File path = new File(exchangePath);
        List<ExchangeInfo> exchangeInfoList = new ArrayList<>();
        List<UserInfo> userInfoList = this.listUserInfo();
        if (path.exists() && path.isDirectory()) {
            File[] files = path.listFiles();
            if (files == null) {
                return exchangeInfoList;
            }
            for (File file : files) {
                ExchangeInfo exchangeInfo = JSON.parseObject(FileUtil.read(file), ExchangeInfo.class);
                if (exchangeInfo == null) {
                    continue;
                }
                exchangeInfoList.add(exchangeInfo);

                for (UserInfo userInfo : userInfoList) {
                    if (!StringUtils.equals(userInfo.getMys_uid(), exchangeInfo.getMys_uid())) {
                        continue;
                    }
                    for (AddressInfo addressInfo : userInfo.getAddress_list()) {
                        if (StringUtils.equals(addressInfo.getId(), exchangeInfo.getAddress_id())) {
                            exchangeInfo.setAddressInfo(addressInfo);
                            break;
                        }
                    }
                }
            }
        }

        return exchangeInfoList;
    }

    /**
     * 写入一个兑换文件
     *
     * @param exchangeInfo 兑换信息
     * @return boolean
     */
    public boolean writeExchangeInfo(ExchangeInfo exchangeInfo) {
        if (exchangeInfo == null || StringUtils.isBlank(exchangeInfo.getMys_uid())) {
            return false;
        }

        if (exchangeInfo.getId() == null || exchangeInfo.getId() == 0) {
            exchangeInfo.setId(wfgIdGenerator.next());
        }
        File file = new File(exchangePath + "/" + exchangeInfo.getMys_uid() + "_" + exchangeInfo.getId() + ".json");
        return FileUtil.write(file, JSON.toJSONString(exchangeInfo, SerializerFeature.PrettyFormat, SerializerFeature.SortField));
    }

    /**
     * 删除一个兑换文件
     *
     * @param exchangeInfo 兑换文件
     * @return
     */
    public boolean deleteExchangeInfo(ExchangeInfo exchangeInfo) {
        File file = new File(exchangePath + "/" + exchangeInfo.getMys_uid() + "_" + exchangeInfo.getId() + ".json");
        if (file.exists()) {
            return file.delete();
        }
        return true;
    }
}
