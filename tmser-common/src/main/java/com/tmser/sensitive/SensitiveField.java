package com.tmser.sensitive;

import java.lang.annotation.*;

/**
 * @author tmser
 * @version 1.0
 * @title
 * @description 需要加解密字段注解
 * @changeRecord
 */
@Inherited
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SensitiveField {

}
