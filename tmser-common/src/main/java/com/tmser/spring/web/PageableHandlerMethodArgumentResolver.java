package com.tmser.spring.web;

import com.tmser.model.page.PageImpl;
import com.tmser.model.page.Pageable;
import com.tmser.model.sort.Sort;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * <pre>
 *    描述信息
 * </pre>
 *
 * @version $Id: PageableHandlerMethodArgumentResolver.java, v1.0 2022/4/13 16:27 tmser Exp $
 */
public class PageableHandlerMethodArgumentResolver extends PageableHandlerMethodArgumentResolverSupport
        implements HandlerMethodArgumentResolver {

    private static final SortHandlerMethodArgumentResolver DEFAULT_SORT_RESOLVER = new SortHandlerMethodArgumentResolver();
    private SortHandlerMethodArgumentResolver sortResolver;

    /**
     * Constructs an instance of this resolved with a default {@link SortHandlerMethodArgumentResolver}.
     */
    public PageableHandlerMethodArgumentResolver() {
        this((SortHandlerMethodArgumentResolver) null);
    }


    /**
     * Constructs an instance of this resolver with the specified {@link SortHandlerMethodArgumentResolver}.
     *
     * @param sortResolver the sort resolver to use
     * @since 1.13
     */
    public PageableHandlerMethodArgumentResolver(@Nullable SortHandlerMethodArgumentResolver sortResolver) {
        this.sortResolver = sortResolver == null ? DEFAULT_SORT_RESOLVER : sortResolver;
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.web.method.support.HandlerMethodArgumentResolver#supportsParameter(org.springframework.core.MethodParameter)
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return Pageable.class.isAssignableFrom(parameter.getParameterType());
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.web.method.support.HandlerMethodArgumentResolver#resolveArgument(org.springframework.core.MethodParameter, org.springframework.web.method.support.ModelAndViewContainer, org.springframework.web.context.request.NativeWebRequest, org.springframework.web.bind.support.WebDataBinderFactory)
     */
    @Override
    public Pageable resolveArgument(MethodParameter methodParameter, @Nullable ModelAndViewContainer mavContainer,
                                    NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) {

        String page = webRequest.getParameter(getParameterNameToUse(getPageParameterName(), methodParameter));
        String pageSize = webRequest.getParameter(getParameterNameToUse(getSizeParameterName(), methodParameter));
        String searchCount = webRequest.getParameter(getParameterNameToUse(getSearchCountParameterName(), methodParameter));

        Sort sort = (Sort) sortResolver.resolveArgument(methodParameter, mavContainer, webRequest, binderFactory);
        Pageable pageable = getPageable(methodParameter, page, pageSize, searchCount);

        if (sort.isSorted()) {
            return PageImpl.of(pageable.getCurrent(), pageable.getSize(), sort);
        }

        return pageable;
    }

}