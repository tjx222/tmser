package com.tmser.sensitive;

import java.lang.annotation.*;

/**
 * @author tmser
 * @version 1.0
 * @title
 * @description 需要解密的Map
 * @changeRecord
 */
@Inherited
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SensitiveMap {

    /**
     * 标识参数为map 时，标识需要作为敏感词处理的可以
     * @return key 数组
     */
    String[] keys() default {};
}
