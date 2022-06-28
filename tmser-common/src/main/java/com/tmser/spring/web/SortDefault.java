package com.tmser.spring.web;

import java.lang.annotation.*;

/**
 * <pre>
 *    描述信息
 * </pre>
 *
 * @version $Id: SortDefault.java, v1.0 2022/4/13 17:20 tmser Exp $
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface SortDefault {

    /**
     * Alias for {@link #sort()} to make a declaration configuring fields only more concise.
     *
     * @return
     */
    String[] value() default {};

    /**
     * The properties to sort by by default. If unset, no sorting will be applied at all.
     *
     * @return
     */
    String[] sort() default {};

    /**
     * Specifies whether to apply case-sensitive sorting. Defaults to {@literal true}.
     *
     * @return
     * @since 2.3
     */
    boolean caseSensitive() default true;

}
