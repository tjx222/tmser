package com.tmser.util;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ObjectUtils;

import java.lang.annotation.Annotation;

/**
 * <pre>
 *    描述信息
 * </pre>
 *
 * @version $Id: AnnotationUtil.java, v1.0 2022/4/13 17:50 tmser Exp $
 */
public abstract class AnnotationUtil {


    /**
     * Returns the value of the given specific property of the given annotation. If the value of that property is the
     * properties default, we fall back to the value of the {@code value} attribute.
     *
     * @param annotation must not be {@literal null}.
     * @param property   must not be {@literal null} or empty.
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T getSpecificPropertyOrDefaultFromValue(Annotation annotation, String property) {

        Object propertyDefaultValue = AnnotationUtils.getDefaultValue(annotation, property);
        Object propertyValue = AnnotationUtils.getValue(annotation, property);

        Object result = ObjectUtils.nullSafeEquals(propertyDefaultValue, propertyValue) //
                ? AnnotationUtils.getValue(annotation) //
                : propertyValue;

        if (result == null) {
            throw new IllegalStateException("Exepected to be able to look up an annotation property value but failed!");
        }

        return (T) result;
    }
}
