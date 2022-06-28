package com.tmser.spring.web;

/**
 * <pre>
 *    描述信息
 * </pre>
 *
 * @version $Id: PageableDefault.java, v1.0 2022/4/14 9:41 tmser Exp $
 */


import java.lang.annotation.*;

/**
 * Annotation to set defaults when injecting a {@link Pageable} into a controller
 * method. Instead of configuring {@link #sort()} you can also use {@link SortDefault}.
 *
 * @author Oliver Gierke
 * @since 1.6
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface PageableDefault {

    /**
     * Alias for {@link #size()}. Prefer to use the {@link #size()} method as it makes the annotation declaration more
     * expressive and you'll probably want to configure the {@link #page()} anyway.
     *
     * @return
     */
    long value() default 10;

    /**
     * need search count
     */
    boolean searchCount() default true;

    /**
     * The default-size the injected {@link com.tmser.model.page.Page} should get if no corresponding
     * parameter defined in request (default is 10).
     */
    long size() default 10;

    /**
     * The default-pagenumber the injected {@link com.tmser.model.page.Page} should get if no corresponding
     * parameter defined in request (default is 1).
     */
    long page() default 1;

    /**
     * The properties to sort by by default. If unset, no sorting will be applied at all.
     *
     * @return
     */
    String[] sort() default {};

}