package com.tmser.model.page;

import com.tmser.model.sort.Sort;

import java.util.Optional;

/**
 * <pre>
 *    描述信息
 * </pre>
 *
 * @version $Id: Pageable.java, v1.0 2022/4/14 9:54 tmser Exp $
 */
public interface Pageable {

    /**
     * Returns a {@link Pageable} instance representing no pagination setup.
     *
     * @return
     */
    static Pageable unpaged() {
        return Unpaged.INSTANCE;
    }

    /**
     * Returns whether the current {@link Pageable} contains pagination information.
     *
     * @return
     */
    default boolean isPaged() {
        return true;
    }

    /**
     * 计算当前分页偏移量
     */
    default long offset() {
        long current = getCurrent();
        if (current <= 1L) {
            return 0L;
        }
        return Math.max((current - 1) * getSize(), 0L);
    }

    /**
     * 最大每页分页数限制,优先级高于分页插件内的 maxLimit
     */
    default Long maxLimit() {
        return null;
    }

    /**
     * Returns whether the current {@link Pageable} does not contain pagination information.
     *
     * @return
     */
    default boolean isUnpaged() {
        return !isPaged();
    }


    /**
     * 当前页
     *
     * @return 当前页
     */
    long getCurrent();

    /**
     * Returns the number of items to be returned.
     *
     * @return the number of items of that page
     */
    long getSize();

    /**
     * 进行 count 查询 【 默认: true 】
     *
     * @return true 是 / false 否
     */
    default boolean searchCount() {
        return true;
    }

    /**
     * Returns the sorting parameters.
     *
     * @return
     */
    Sort getSort();

    /**
     * Returns the current {@link Sort} or the given one if the current one is unsorted.
     *
     * @param sort must not be {@literal null}.
     * @return
     */
    default Sort getSortOr(Sort sort) {
        return getSort().isSorted() ? getSort() : sort;
    }


    /**
     * Creates a new {@link Pageable} with {@code pageNumber} applied.
     *
     * @param pageNumber
     * @return a new {@link Pageable}.
     * @since 2.5
     */
    Pageable withPage(long pageNumber);

    /**
     * Returns an {@link Optional} so that it can easily be mapped on.
     *
     * @return
     */
    default Optional<Pageable> toOptional() {
        return isUnpaged() ? Optional.empty() : Optional.of(this);
    }

    enum Unpaged implements Pageable {

        INSTANCE;

        /*
         * (non-Javadoc)
         * @see org.springframework.data.domain.Pageable#isPaged()
         */
        @Override
        public boolean isPaged() {
            return false;
        }


        /*
         * (non-Javadoc)
         * @see org.springframework.data.domain.Pageable#getSort()
         */
        @Override
        public Sort getSort() {
            return Sort.unsorted();
        }

        /*
         * (non-Javadoc)
         * @see org.springframework.data.domain.Pageable#getPageSize()
         */
        @Override
        public long getSize() {
            throw new UnsupportedOperationException();
        }

        /*
         * (non-Javadoc)
         * @see org.springframework.data.domain.Pageable#getPageNumber()
         */
        @Override
        public long getCurrent() {
            throw new UnsupportedOperationException();
        }

        /*
         * (non-Javadoc)
         * @see org.springframework.data.domain.Pageable#getOffset()
         */
        @Override
        public long offset() {
            throw new UnsupportedOperationException();
        }

        /*
         * (non-Javadoc)
         * @see org.springframework.data.domain.Pageable#withPage(long)
         */
        @Override
        public Pageable withPage(long pageNumber) {

            if (pageNumber == 0) {
                return this;
            }

            throw new UnsupportedOperationException();
        }

    }
}