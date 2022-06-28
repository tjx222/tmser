package com.tmser.util;

import org.springframework.lang.Nullable;

import java.util.function.Consumer;

/**
 * <pre>
 *    描述信息
 * </pre>
 *
 * @version $Id: Sink.java, v1.0 2022/4/13 17:28 tmser Exp $
 */
class Sink<T> implements Consumer<T> {

    private T value;

    /**
     * Returns the value captured.
     *
     * @return
     */
    public T getValue() {
        return value;
    }

    /*
     * (non-Javadoc)
     * @see java.util.function.Consumer#accept(java.lang.Object)
     */
    @Override
    public void accept(@Nullable T t) {
        this.value = t;
    }
}