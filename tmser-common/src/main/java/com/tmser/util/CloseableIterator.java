package com.tmser.util;

import java.io.Closeable;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * <pre>
 *    描述信息
 * </pre>
 *
 * @version $Id: CloseableIterator.java, v1.0 2022/4/13 17:26 tmser Exp $
 */
public interface CloseableIterator<T> extends Iterator<T>, Closeable {

    /*
     * (non-Javadoc)
     * @see java.io.Closeable#close()
     */
    @Override
    void close();

    /**
     * Create a {@link Spliterator} over the elements provided by this {@link Iterator}. Implementations should document
     * characteristic values reported by the spliterator. Such characteristic values are not required to be reported if
     * the spliterator reports {@link Spliterator#SIZED} and this collection contains no elements.
     * <p>
     * The default implementation should be overridden by subclasses that can return a more efficient spliterator. To
     * preserve expected laziness behavior for the {@link #stream()} method, spliterators should either have the
     * characteristic of {@code IMMUTABLE} or {@code CONCURRENT}, or be late-binding.
     *
     * @return a {@link Spliterator} over the elements in this {@link Iterator}.
     * @since 2.4
     */
    default Spliterator<T> spliterator() {
        return Spliterators.spliterator(this, 0, 0);
    }

    /**
     * Return a sequential {@code Stream} with this {@link Iterator} as its source. The resulting stream calls
     * {@link #close()} when {@link Stream#close() closed}. The resulting {@link Stream} must be closed after use, it can
     * be declared as a resource in a {@code try}-with-resources statement.
     * <p>
     * This method should be overridden when the {@link #spliterator()} method cannot return a spliterator that is
     * {@code IMMUTABLE}, {@code CONCURRENT}, or <em>late-binding</em>. (See {@link #spliterator()} for details.)
     *
     * @return a sequential {@code Stream} over the elements in this {@link Iterator}.
     * @since 2.4
     */
    default Stream<T> stream() {
        return StreamSupport.stream(spliterator(), false).onClose(this::close);
    }
}