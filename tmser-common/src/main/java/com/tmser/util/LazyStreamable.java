package com.tmser.util;

import java.util.Iterator;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * <pre>
 *    描述信息
 * </pre>
 *
 * @version $Id: LazyStreamable.java, v1.0 2022/4/13 17:31 tmser Exp $
 */
final class LazyStreamable<T> implements Streamable<T> {

    private final Supplier<? extends Stream<T>> stream;

    private LazyStreamable(Supplier<? extends Stream<T>> stream) {
        this.stream = stream;
    }

    public static <T> LazyStreamable<T> of(Supplier<? extends Stream<T>> stream) {
        return new LazyStreamable<T>(stream);
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Iterable#iterator()
     */
    @Override
    public Iterator<T> iterator() {
        return stream().iterator();
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.util.Streamable#stream()
     */
    @Override
    public Stream<T> stream() {
        return stream.get();
    }

    public Supplier<? extends Stream<T>> getStream() {
        return this.stream;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "LazyStreamable(stream=" + this.getStream() + ")";
    }
}

