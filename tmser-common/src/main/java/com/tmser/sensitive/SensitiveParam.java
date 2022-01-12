package com.tmser.sensitive;

import java.lang.annotation.*;

/**
 * @author tmser
 * @version 1.0
 * @title
 * @description 需要加解密参数
 * @changeRecord
 */
@Inherited
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface SensitiveParam {

    /**
     * 标识参数为map 时，标识需要作为敏感词处理的可以
     * @return key 数组
     */
    String[] keys() default {};
}
