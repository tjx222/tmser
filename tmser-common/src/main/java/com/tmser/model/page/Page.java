package com.tmser.model.page;

import java.util.List;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;

/**
 * 分页接口
 */
public interface Page<T> extends Pageable {

    /**
     * 自动优化 COUNT SQL【 默认：true 】
     *
     * @return true 是 / false 否
     */
    default boolean optimizeCountSql() {
        return true;
    }

    /**
     * 是否有下一页
     *
     * @return true 有下一页
     */
    default boolean hasNext() {
        return getTotal() > getCurrent() * getSize();
    }

    /**
     * 是否有上一页
     *
     * @return
     */
    default boolean hasPrevious() {
        return getCurrent() > 1;
    }

    /**
     * 当前分页总页数
     */
    default long getPages() {
        if (getSize() == 0) {
            return 0L;
        }
        long pages = getTotal() / getSize();
        if (getTotal() % getSize() != 0) {
            pages++;
        }
        return pages;
    }

    /**
     * 内部什么也不干
     * <p>只是为了 json 反序列化时不报错</p>
     */
    default Page<T> setPages(long pages) {
        // to do nothing
        return this;
    }

    /**
     * 分页记录列表
     *
     * @return 分页对象记录列表
     */
    List<T> getContent();

    /**
     * 设置分页记录列表
     */
    Page<T> setContent(List<T> records);

    /**
     * 当前满足条件总行数
     *
     * @return 总条数
     */
    long getTotal();

    /**
     * 设置当前满足条件总行数
     */
    Page<T> setTotal(long total);


    /**
     * 设置当前页
     */
    Page<T> setCurrent(long current);

    /**
     * IPage 的泛型转换
     *
     * @param mapper 转换函数
     * @param <R>    转换后的泛型
     * @return 转换泛型后的 IPage
     */
    @SuppressWarnings("unchecked")
    default <R> Page<R> convert(Function<? super T, ? extends R> mapper) {
        List<R> collect = this.getContent().stream().map(mapper).collect(toList());
        return ((Page<R>) this).setContent(collect);
    }

}
