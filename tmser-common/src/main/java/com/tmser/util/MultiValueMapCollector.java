package com.tmser.util;

import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

/**
 * <pre>
 *    描述信息
 * </pre>
 *
 * @version $Id: MultiValueMapCollector.java, v1.0 2022/4/13 17:30 tmser Exp $
 */
public class MultiValueMapCollector<T, K, V> implements Collector<T, org.springframework.util.MultiValueMap<K, V>, org.springframework.util.MultiValueMap<K, V>> {

    private final Function<T, K> keyFunction;
    private final Function<T, V> valueFunction;

    private MultiValueMapCollector(Function<T, K> keyFunction, Function<T, V> valueFunction) {
        this.keyFunction = keyFunction;
        this.valueFunction = valueFunction;
    }

    static <T, K, V> MultiValueMapCollector<T, K, V> of(Function<T, K> keyFunction, Function<T, V> valueFunction) {
        return new MultiValueMapCollector<T, K, V>(keyFunction, valueFunction);
    }

    /*
     * (non-Javadoc)
     * @see java.util.stream.Collector#supplier()
     */
    @Override
    public Supplier<org.springframework.util.MultiValueMap<K, V>> supplier() {
        return () -> CollectionUtils.toMultiValueMap(new HashMap<>());
    }

    /*
     * (non-Javadoc)
     * @see java.util.stream.Collector#accumulator()
     */
    @Override
    public BiConsumer<org.springframework.util.MultiValueMap<K, V>, T> accumulator() {
        return (map, t) -> map.add(keyFunction.apply(t), valueFunction.apply(t));
    }

    /*
     * (non-Javadoc)
     * @see java.util.stream.Collector#combiner()
     */
    @Override
    public BinaryOperator<org.springframework.util.MultiValueMap<K, V>> combiner() {

        return (map1, map2) -> {

            for (K key : map2.keySet()) {
                map1.addAll(key, map2.get(key));
            }

            return map1;
        };
    }

    /*
     * (non-Javadoc)
     * @see java.util.stream.Collector#finisher()
     */
    @Override
    public Function<org.springframework.util.MultiValueMap<K, V>, MultiValueMap<K, V>> finisher() {
        return Function.identity();
    }

    /*
     * (non-Javadoc)
     * @see java.util.stream.Collector#characteristics()
     */
    @Override
    public Set<Characteristics> characteristics() {
        return EnumSet.of(Characteristics.IDENTITY_FINISH, Characteristics.UNORDERED);
    }
}
