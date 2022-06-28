package com.tmser.spring.web;

import com.tmser.model.sort.Sort;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Arrays;

/**
 * <pre>
 *    描述信息
 * </pre>
 *
 * @version $Id: SortHandlerMethodArgumentResolver.java, v1.0 2022/4/13 16:23 tmser Exp $
 */
public class SortHandlerMethodArgumentResolver extends SortHandlerMethodArgumentResolverSupport implements HandlerMethodArgumentResolver {

    /*
     * (non-Javadoc)
     * @see org.springframework.web.method.support.HandlerMethodArgumentResolver#supportsParameter(org.springframework.core.MethodParameter)
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return Sort.class.isAssignableFrom(parameter.getParameterType());
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.web.method.support.HandlerMethodArgumentResolver#resolveArgument(org.springframework.core.MethodParameter, org.springframework.web.method.support.ModelAndViewContainer, org.springframework.web.context.request.NativeWebRequest, org.springframework.web.bind.support.WebDataBinderFactory)
     */
    @Override
    public Object resolveArgument(MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) {

        String[] directionParameter = webRequest.getParameterValues(getSortParameter(parameter));

        // No parameter
        if (directionParameter == null) {
            return getDefaultFromAnnotationOrFallback(parameter);
        }

        // Single empty parameter, e.g "sort="
        if (directionParameter.length == 1 && !StringUtils.hasText(directionParameter[0])) {
            return getDefaultFromAnnotationOrFallback(parameter);
        }

        return parseParameterIntoSort(Arrays.asList(directionParameter), getPropertyDelimiter());
    }
}