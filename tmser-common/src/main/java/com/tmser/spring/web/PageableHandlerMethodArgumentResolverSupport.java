package com.tmser.spring.web;

import com.google.common.collect.Lists;
import com.tmser.model.page.PageImpl;
import com.tmser.model.page.Pageable;
import com.tmser.model.sort.Sort;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * <pre>
 *    描述信息
 * </pre>
 *
 * @version $Id: PageableHandlerMethodArgumentResolverSupport.java, v1.0 2022/4/13 16:28 tmser Exp $
 */
public class PageableHandlerMethodArgumentResolverSupport {
    private static final String INVALID_DEFAULT_PAGE_SIZE = "Invalid default page size configured for method %s! Must not be less than one!";

    private static final String DEFAULT_PAGE_PARAMETER = "current";
    private static final String DEFAULT_SIZE_PARAMETER = "size";
    private static final String DEFAULT_SEARCH_COUNT_PARAMETER = "searchCount";
    private static final String DEFAULT_PROPERTY_DELIMITER = ",";
    private static final String DEFAULT_PREFIX = "";
    private static final int DEFAULT_MAX_PAGE_SIZE = 2000;
    static final Pageable DEFAULT_PAGE_REQUEST = PageImpl.of(0, 20);

    private Pageable fallbackPageable = DEFAULT_PAGE_REQUEST;
    private String pageParameterName = DEFAULT_PAGE_PARAMETER;
    private String sizeParameterName = DEFAULT_SIZE_PARAMETER;
    private String prefix = DEFAULT_PREFIX;
    private int maxPageSize = DEFAULT_MAX_PAGE_SIZE;
    private String searchCountParameterName = DEFAULT_SEARCH_COUNT_PARAMETER;
    private boolean oneIndexedParameters = true;

    private String propertyDelimiter = DEFAULT_PROPERTY_DELIMITER;

    /**
     * Configures the {@link Pageable} to be used as fallback in case no {@link PageableDefault} or
     * {@link PageableDefault} (the latter only supported in legacy mode) can be found at the method parameter to be
     * resolved.
     * <p>
     * If you set this to {@literal Optional#empty()}, be aware that you controller methods will get {@literal null}
     * handed into them in case no {@link Pageable} data can be found in the request. Note, that doing so will require you
     * supply bot the page <em>and</em> the size parameter with the requests as there will be no default for any of the
     * parameters available.
     *
     * @param fallbackPageable the {@link Pageable} to be used as general fallback.
     */
    public void setFallbackPageable(Pageable fallbackPageable) {

        Assert.notNull(fallbackPageable, "Fallback Pageable must not be null!");

        this.fallbackPageable = fallbackPageable;
    }

    /**
     * Returns whether the given {@link Pageable} is the fallback one.
     *
     * @param pageable can be {@literal null}.
     * @return
     */
    public boolean isFallbackPageable(Pageable pageable) {
        return fallbackPageable.equals(pageable);
    }

    /**
     * Configures the maximum page size to be accepted. This allows to put an upper boundary of the page size to prevent
     * potential attacks trying to issue an {@link OutOfMemoryError}. Defaults to {@link #DEFAULT_MAX_PAGE_SIZE}.
     *
     * @param maxPageSize the maxPageSize to set
     */
    public void setMaxPageSize(int maxPageSize) {
        this.maxPageSize = maxPageSize;
    }

    /**
     * Retrieves the maximum page size to be accepted. This allows to put an upper boundary of the page size to prevent
     * potential attacks trying to issue an {@link OutOfMemoryError}. Defaults to {@link #DEFAULT_MAX_PAGE_SIZE}.
     *
     * @return the maximum page size allowed.
     */
    protected int getMaxPageSize() {
        return this.maxPageSize;
    }

    /**
     * Configures the parameter name to be used to find the page number in the request. Defaults to {@code page}.
     *
     * @param pageParameterName the parameter name to be used, must not be {@literal null} or empty.
     */
    public void setPageParameterName(String pageParameterName) {

        Assert.hasText(pageParameterName, "Page parameter name must not be null or empty!");
        this.pageParameterName = pageParameterName;
    }

    /**
     * Retrieves the parameter name to be used to find the page number in the request. Defaults to {@code page}.
     *
     * @return the parameter name to be used, never {@literal null} or empty.
     */
    protected String getPageParameterName() {
        return this.pageParameterName;
    }

    /**
     * Configures the parameter name to be used to find the page size in the request. Defaults to {@code size}.
     *
     * @param sizeParameterName the parameter name to be used, must not be {@literal null} or empty.
     */
    public void setSizeParameterName(String sizeParameterName) {

        Assert.hasText(sizeParameterName, "Size parameter name must not be null or empty!");
        this.sizeParameterName = sizeParameterName;
    }

    /**
     * Retrieves the parameter name to be used to find the page size in the request. Defaults to {@code size}.
     *
     * @return the parameter name to be used, never {@literal null} or empty.
     */
    protected String getSizeParameterName() {
        return this.sizeParameterName;
    }

    /**
     * Configures a general prefix to be prepended to the page number and page size parameters. Useful to namespace the
     * property names used in case they are clashing with ones used by your application. By default, no prefix is used.
     *
     * @param prefix the prefix to be used or {@literal null} to reset to the default.
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix == null ? DEFAULT_PREFIX : prefix;
    }

    /**
     * Configures whether to expose and assume 1-based page number indexes in the request parameters. Defaults to
     * {@literal false}, meaning a page number of 0 in the request equals the first page. If this is set to
     * {@literal true}, a page number of 1 in the request will be considered the first page.
     *
     * @param oneIndexedParameters the oneIndexedParameters to set
     */
    public void setOneIndexedParameters(boolean oneIndexedParameters) {
        this.oneIndexedParameters = oneIndexedParameters;
    }

    /**
     * Indicates whether to expose and assume 1-based page number indexes in the request parameters. Defaults to
     * {@literal false}, meaning a page number of 0 in the request equals the first page. If this is set to
     * {@literal true}, a page number of 1 in the request will be considered the first page.
     *
     * @return whether to assume 1-based page number indexes in the request parameters.
     */
    protected boolean isOneIndexedParameters() {
        return this.oneIndexedParameters;
    }

    protected Pageable getPageable(MethodParameter methodParameter, @Nullable String pageString,
                                   @Nullable String pageSizeString, @Nullable String searchCountString) {

        Optional<Pageable> defaultOrFallback = getDefaultFromAnnotationOrFallback(methodParameter).toOptional();

        Optional<Long> page = parseAndApplyBoundaries(pageString, Long.MAX_VALUE, true);
        Optional<Long> pageSize = parseAndApplyBoundaries(pageSizeString, maxPageSize, false);

        Optional<Boolean> searchCount = parseBoolean(searchCountString, true);

        if (!(page.isPresent() && pageSize.isPresent()) && !defaultOrFallback.isPresent()) {
            return Pageable.unpaged();
        }

        long p = page
                .orElseGet(() -> defaultOrFallback.map(Pageable::getCurrent).orElseThrow(IllegalStateException::new));
        long ps = pageSize
                .orElseGet(() -> defaultOrFallback.map(Pageable::getSize).orElseThrow(IllegalStateException::new));
        boolean sc = searchCount.orElseGet(() -> defaultOrFallback.map(Pageable::searchCount).orElseThrow(IllegalStateException::new));

        // Limit lower bound
        ps = ps < 1 ? defaultOrFallback.map(Pageable::getSize).orElseThrow(IllegalStateException::new) : ps;
        // Limit upper bound
        ps = ps > maxPageSize ? maxPageSize : ps;

        return PageImpl.of(p, ps, sc, defaultOrFallback.map(Pageable::getSort).orElseGet(Sort::unsorted));
    }

    private Optional<Boolean> parseBoolean(String searchCountString, boolean b) {
        if (!StringUtils.hasText(searchCountString)) {
            return Optional.empty();
        }
        return Optional.of(!"0".equals(searchCountString) && !"false".equalsIgnoreCase(searchCountString));
    }

    /**
     * Returns the name of the request parameter to find the {@link Pageable} information in. Inspects the given
     * {@link MethodParameter} for {@link Qualifier} present and prefixes the given source parameter name with it.
     *
     * @param source    the basic parameter name.
     * @param parameter the {@link MethodParameter} potentially qualified.
     * @return the name of the request parameter.
     */
    protected String getParameterNameToUse(String source, @Nullable MethodParameter parameter) {
        return source;
    }

    protected String getSearchCountParameterName() {
        return this.searchCountParameterName;
    }

    protected void setSearchCountParameterName(String searchCountParameterName) {
        this.searchCountParameterName = searchCountParameterName;
    }

    private Pageable getDefaultFromAnnotationOrFallback(MethodParameter methodParameter) {

        PageableDefault defaults = methodParameter.getParameterAnnotation(PageableDefault.class);

        if (defaults != null) {
            return getDefaultPageRequestFrom(methodParameter, defaults);
        }

        return fallbackPageable;
    }

    private static Pageable getDefaultPageRequestFrom(MethodParameter parameter, PageableDefault defaults) {

        long defaultPageNumber = defaults.page();
        long defaultPageSize = getSpecificPropertyOrDefaultFromValue(defaults, "size");

        if (defaultPageSize < 1) {
            Method annotatedMethod = parameter.getMethod();
            throw new IllegalStateException(String.format(INVALID_DEFAULT_PAGE_SIZE, annotatedMethod));
        }

        if (defaults.sort().length == 0) {
            return PageImpl.of(defaultPageNumber, defaultPageSize);
        }

        return PageImpl.of(defaultPageNumber, defaultPageSize, parseParameterIntoSort(Lists.newArrayList(defaults.sort()), DEFAULT_PROPERTY_DELIMITER));
    }

    static Sort parseParameterIntoSort(List<String> source, String delimiter) {

        List<Sort.Order> allOrders = new ArrayList<>();

        for (String part : source) {

            if (part == null) {
                continue;
            }

            SortHandlerMethodArgumentResolverSupport.SortOrderParser.parse(part, delimiter) //
                    .parseDirection() //
                    .forEachOrder(allOrders::add);
        }

        return allOrders.isEmpty() ? Sort.unsorted() : Sort.by(allOrders);
    }


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

    /**
     * Tries to parse the given {@link String} into an integer and applies the given boundaries. Will return 0 if the
     * {@link String} cannot be parsed.
     *
     * @param parameter  the parameter value.
     * @param upper      the upper bound to be applied.
     * @param shiftIndex whether to shift the index if {@link #oneIndexedParameters} is set to true.
     * @return
     */
    private Optional<Long> parseAndApplyBoundaries(@Nullable String parameter, long upper, boolean shiftIndex) {

        if (!StringUtils.hasText(parameter)) {
            return Optional.empty();
        }

        try {
            long parsed = Long.parseLong(parameter) - (oneIndexedParameters && shiftIndex ? 1 : 0);
            return Optional.of(parsed < 0 ? 0 : parsed > upper ? upper : parsed);
        } catch (NumberFormatException e) {
            return Optional.of(0L);
        }
    }


}