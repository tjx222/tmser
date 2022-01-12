package com.tmser.sensitive;

import java.lang.annotation.*;

/**
 * @author tmser
 * @version 1.0
 * @title
 * @description 需要加解密的类注解
 * @changeRecord
 */
@Inherited
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface SensitiveClass {
}
