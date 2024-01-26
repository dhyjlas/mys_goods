package com.dhyjlas.mys.entity.body;

import com.dhyjlas.mys.entity.BaseEntity;
import lombok.Data;

/**
 * <p>File: MobileCaptcha.java </p>
 * <p>Title: </p>
 * <p>Description: </p>
 *
 * @author yjl.yu/dhyjlas@163.com
 * @version 1.0
 */
@Data
public class MobileCaptcha extends BaseEntity {
    private String mobile;
    private String captcha;
}
