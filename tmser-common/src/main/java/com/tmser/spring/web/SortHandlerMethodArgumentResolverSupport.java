package com.tmser.spring.web;

import com.tmser.model.sort.Sort;
import com.tmser.model.sort.Sort.Direction;
import com.tmser.model.sort.Sort.Order;
import com.tmser.util.AnnotationUtil;
import org.springframework.core.MethodParameter;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;

import java.util.*;
import java.util.function.Consumer;

/**
 * <pre>
 *    描述信息
 * </pre>
 *
 * @version $Id: SortHandlerMethodArgumentResolverSupport.java, v1.0 2022/4/13 16:25 tmser Exp $
 */
public abstract class SortHandlerMethodArgumentResolverSupport implements HandlerMethodArgumentResolver {

    private static final String DEFAULT_PARAMETER = "sort";
    private static final String DEFAULT_PROPERTY_DELIMITER = ",";
    private static final String DEFAULT_QUALIFIER_DELIMITER = "_";
    private static final Sort DEFAULT_SORT = Sort.unsorted();

    private static final String SORT_DEFAULT_NAME = SortDefault.class.getSimpleName();

    private Sort fallbackSort = DEFAULT_SORT;
    private String sortParameter = DEFAULT_PARAMETER;
    private String propertyDelimiter = DEFAULT_PROPERTY_DELIMITER;

    /**
     * propertyDel Configure the request parameter to lookup sort information from. Defaults to {@code sort}.
     *
     * @param sortParameter must not be {@literal null} or empty.
     */
    public void setSortParameter(String sortParameter) {

        Assert.hasText(sortParameter, "SortParameter must not be null nor empty!");
        this.sortParameter = sortParameter;
    }

    /**
     * Configures the delimiter used to separate property references and the direction to be sorted by. Defaults to
     * {@code}, which means sort values look like this: {@code firstname,lastname,asc}.
     *
     * @param propertyDelimiter must not be {@literal null} or empty.
     */
    public void setPropertyDelimiter(String propertyDelimiter) {

        Assert.hasText(propertyDelimiter, "Property delimiter must not be null or empty!");
        this.propertyDelimiter = propertyDelimiter;
    }

    /**
     * @return the configured delimiter used to separate property references and the direction to be sorted by
     */
    public String getPropertyDelimiter() {
        return propertyDelimiter;
    }

    /**
     * Configures the {@link Sort} to be used as fallback in case no {@link SortDefault} (the
     * latter only supported in legacy mode) can be found at the method parameter to be resolved.
     * <p>
     * If you set this to {@literal null}, be aware that you controller methods will get {@literal null} handed into them
     * in case no {@link Sort} data can be found in the request.
     *
     * @param fallbackSort the {@link Sort} to be used as general fallback.
     */
    public void setFallbackSort(Sort fallbackSort) {
        this.fallbackSort = fallbackSort;
    }

    /**
     * Reads the default {@link Sort} to be used from the given {@link MethodParameter}. Rejects the parameter if both an
     * {@link SortDefault} annotation is found as we cannot build a reliable {@link Sort}
     * instance then (property ordering).
     *
     * @param parameter will never be {@literal null}.
     * @return the default {@link Sort} instance derived from the parameter annotations or the configured fallback-sort
     * {@link #setFallbackSort(Sort)}.
     */
    protected Sort getDefaultFromAnnotationOrFallback(MethodParameter parameter) {
        SortDefault annotatedDefault = parameter.getParameterAnnotation(SortDefault.class);

        if (annotatedDefault != null) {
            return appendOrCreateSortTo(annotatedDefault, Sort.unsorted());
        }

        return fallbackSort;
    }

    protected String getSortParameter(MethodParameter parameter) {
        return sortParameter;
    }

    /**
     * Creates a new {@link Sort} instance from the given {@link SortDefault} or appends it to the given {@link Sort}
     * instance if it's not {@literal null}.
     *
     * @param sortDefault
     * @param sortOrNull
     * @return
     */
    private Sort appendOrCreateSortTo(SortDefault sortDefault, Sort sortOrNull) {
        String[] fields = AnnotationUtil.getSpecificPropertyOrDefaultFromValue(sortDefault, "sort");

        if (fields.length == 0) {
            return Sort.unsorted();
        }

        return parseParameterIntoSort(Arrays.asList(fields), getPropertyDelimiter());
    }

    /**
     * Parses the given sort expressions into a {@link Sort} instance. The implementation expects the sources to be a
     * concatenation of Strings using the given delimiter. If the last element is equal {@code ignorecase} (when using a
     * case-insensitive comparison), the sort order will be performed without respect to case. If the last element (or the
     * penultimate element if the last is {@code ignorecase}) can be parsed into a {@link Direction} it's considered a
     * {@link Direction} and a simple property otherwise.
     *
     * @param source    will never be {@literal null}.
     * @param delimiter the delimiter to be used to split up the source elements, will never be {@literal null}.
     * @return
     */
    Sort parseParameterIntoSort(List<String> source, String delimiter) {

        List<Order> allOrders = new ArrayList<>();

        for (String part : source) {

            if (part == null) {
                continue;
            }

            SortOrderParser.parse(part, delimiter) //
                    .parseIgnoreCase() //
                    .parseDirection() //
                    .forEachOrder(allOrders::add);
        }

        return allOrders.isEmpty() ? Sort.unsorted() : Sort.by(allOrders);
    }

    /**
     * Folds the given {@link Sort} instance into a {@link List} of sort expressions, accumulating {@link Order} instances
     * of the same direction into a single expression if they are in order.
     *
     * @param sort must not be {@literal null}.
     * @return
     */
    protected List<String> foldIntoExpressions(Sort sort) {

        List<String> expressions = new ArrayList<>();
        ExpressionBuilder builder = null;

        for (Sort.Order order : sort) {

            Sort.Direction direction = order.getDirection();

            if (builder == null) {
                builder = new ExpressionBuilder(direction);
            } else if (!builder.hasSameDirectionAs(order)) {
                builder.dumpExpressionIfPresentInto(expressions);
                builder = new ExpressionBuilder(direction);
            }

            builder.add(order.getProperty());
        }

        return builder == null ? Collections.emptyList() : builder.dumpExpressionIfPresentInto(expressions);
    }

    /**
     * Folds the given {@link Sort} instance into two expressions. The first being the property list, the second being the
     * direction.
     *
     * @param sort must not be {@literal null}.
     * @return
     * @throws IllegalArgumentException if a {@link Sort} with multiple {@link Direction}s has been handed in.
     */
    protected List<String> legacyFoldExpressions(Sort sort) {

        List<String> expressions = new ArrayList<>();
        ExpressionBuilder builder = null;

        for (Order order : sort) {

            Direction direction = order.getDirection();

            if (builder == null) {
                builder = new ExpressionBuilder(direction);
            } else if (!builder.hasSameDirectionAs(order)) {
                throw new IllegalArgumentException(String.format(
                        "%s in legacy configuration only supports a single direction to sort by!", getClass().getSimpleName()));
            }

            builder.add(order.getProperty());
        }

        return builder == null ? Collections.emptyList() : builder.dumpExpressionIfPresentInto(expressions);
    }

    /**
     * Returns whether the given source {@link String} consists of dots only.
     *
     * @param source must not be {@literal null}.
     * @return
     */
    static boolean notOnlyDots(String source) {
        return StringUtils.hasText(source.replace(".", ""));
    }

    /**
     * Helper to easily build request parameter expressions for {@link Sort} instances.
     *
     * @author Oliver Gierke
     */
    class ExpressionBuilder {

        private final List<String> elements = new ArrayList<>();
        private final Direction direction;

        /**
         * Sets up a new {@link ExpressionBuilder} for properties to be sorted in the given {@link Direction}.
         *
         * @param direction must not be {@literal null}.
         */
        ExpressionBuilder(Sort.Direction direction) {

            Assert.notNull(direction, "Direction must not be null!");
            this.direction = direction;
        }

        /**
         * Returns whether the given {@link Order} has the same direction as the current {@link ExpressionBuilder}.
         *
         * @param order must not be {@literal null}.
         * @return
         */
        boolean hasSameDirectionAs(Order order) {
            return this.direction == order.getDirection();
        }

        /**
         * Adds the given property to the expression to be built.
         *
         * @param property
         */
        void add(String property) {
            this.elements.add(property);
        }

        /**
         * Dumps the expression currently in build into the given {@link List} of {@link String}s. Will only dump it in case
         * there are properties piled up currently.
         *
         * @param expressions
         * @return
         */
        List<String> dumpExpressionIfPresentInto(List<String> expressions) {

            if (elements.isEmpty()) {
                return expressions;
            }

            elements.add(direction.name().toLowerCase());
            expressions.add(StringUtils.collectionToDelimitedString(elements, propertyDelimiter));

            return expressions;
        }
    }

    /**
     * Parser for sort {@link Order}.
     *
     * @author Mark Paluch
     * @since 2.3
     */
    static class SortOrderParser {

        private static final String IGNORECASE = "ignorecase";

        private final String[] elements;
        private final int lastIndex;
        private final Optional<Direction> direction;
        private final Optional<Boolean> ignoreCase;

        private SortOrderParser(String[] elements) {
            this(elements, elements.length, Optional.empty(), Optional.empty());
        }

        private SortOrderParser(String[] elements, int lastIndex, Optional<Direction> direction,
                                Optional<Boolean> ignoreCase) {
            this.elements = elements;
            this.lastIndex = Math.max(0, lastIndex);
            this.direction = direction;
            this.ignoreCase = ignoreCase;
        }

        /**
         * Parse the raw sort string delimited by {@code delimiter}.
         *
         * @param part      sort part to parse.
         * @param delimiter the delimiter to be used to split up the source elements, will never be {@literal null}.
         * @return the parsing state object.
         */
        public static SortOrderParser parse(String part, String delimiter) {

            String[] elements = Arrays.stream(part.split(delimiter)) //
                    .filter(SortHandlerMethodArgumentResolver::notOnlyDots) //
                    .toArray(String[]::new);

            return new SortOrderParser(elements);
        }

        /**
         * Parse the {@code ignoreCase} portion of the sort specification.
         *
         * @return a new parsing state object.
         */
        public SortOrderParser parseIgnoreCase() {

            Optional<Boolean> ignoreCase = lastIndex > 0 ? fromOptionalString(elements[lastIndex - 1]) : Optional.empty();

            return new SortOrderParser(elements, lastIndex - (ignoreCase.isPresent() ? 1 : 0), direction, ignoreCase);
        }

        /**
         * Parse the {@link Order} portion of the sort specification.
         *
         * @return a new parsing state object.
         */
        public SortOrderParser parseDirection() {

            Optional<Direction> direction = lastIndex > 0 ? Direction.fromOptionalString(elements[lastIndex - 1])
                    : Optional.empty();

            return new SortOrderParser(elements, lastIndex - (direction.isPresent() ? 1 : 0), direction, ignoreCase);
        }

        /**
         * Notify a {@link Consumer callback function} for each parsed {@link Order} object.
         *
         * @param callback block to be executed.
         */
        public void forEachOrder(Consumer<? super Order> callback) {

            for (int i = 0; i < lastIndex; i++) {
                toOrder(elements[i]).ifPresent(callback);
            }
        }

        private Optional<Boolean> fromOptionalString(String value) {
            return IGNORECASE.equalsIgnoreCase(value) ? Optional.of(true) : Optional.empty();
        }

        private Optional<Order> toOrder(String property) {

            if (!StringUtils.hasText(property)) {
                return Optional.empty();
            }

            Order order = direction.map(it -> new Order(it, property)).orElseGet(() -> Order.by(property));

            if (ignoreCase.isPresent()) {
                return Optional.of(order.ignoreCase());
            }

            return Optional.of(order);
        }
    }
}
