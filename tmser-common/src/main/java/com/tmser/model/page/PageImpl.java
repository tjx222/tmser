package com.tmser.model.page;

import com.tmser.model.sort.Sort;
import lombok.Setter;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 分页及排序信息
 */
public class PageImpl<T> implements Page<T>, Serializable {

    /**
     * 查询数据列表
     */
    protected List<T> content;

    /**
     * 总数
     */
    protected long total = 0;
    /**
     * 每页显示条数，默认 10
     */
    protected long size = 10;

    /**
     * 当前页
     */
    protected long current = 1;

    /**
     * 排序字段信息
     */
    @Setter
    protected Sort sort;

    /**
     * 是否进行 count 查询
     */
    protected boolean searchCount = true;

    /**
     * maxLimit
     */
    @Setter
    protected Long maxLimit;

    public PageImpl() {
    }

    protected PageImpl(long page, long size, Sort sort) {

        this(page, size);

        Assert.notNull(sort, "Sort must not be null!");

        this.sort = sort;
    }

    public PageImpl(long current, long size) {
        this(current, size, 0, true, null, null);
    }

    public PageImpl(long current, long size, long total) {
        this(current, size, total, true, null, null);
    }

    public PageImpl(long current, long size, boolean searchCount) {
        this(current, size, 0, searchCount, null, null);
    }

    public PageImpl(long current, long size, boolean searchCount, Sort sort) {
        this(current, size, 0, searchCount, sort, null);
    }

    public PageImpl(long current, long size, long total, boolean searchCount, Sort sort, List<T> content) {
        if (current > 1) {
            this.current = current;
        }
        this.size = size;
        this.total = total;
        this.searchCount = searchCount;
        this.sort = sort;
        this.content = content;
    }


    /**
     * 是否存在下一页
     *
     * @return true / false
     */
    public boolean hasNext() {
        return this.current < this.getPages();
    }

    public List<T> getContent() {
        return this.content;
    }

    public PageImpl<T> setContent(List<T> content) {
        this.content = content;
        return this;
    }

    public long getTotal() {
        return this.total;
    }

    public int getTotalPages() {
        return getSize() == 0 ? 1 : (int) Math.ceil((double) total / (double) getSize());
    }

    public PageImpl<T> setTotal(long total) {
        this.total = total;
        return this;
    }


    public long getSize() {
        return this.size;
    }

    @Override
    public Sort getSort() {
        return sort;
    }

    @Override
    public Pageable withPage(long pageNumber) {
        return new PageImpl<T>(pageNumber, getSize(), getSort());
    }


    public PageImpl<T> setSize(long size) {
        this.size = size;
        return this;
    }


    public long getCurrent() {
        return this.current;
    }


    public PageImpl<T> setCurrent(long current) {
        this.current = current;
        return this;
    }

    /**
     * 最大每页分页数限制,优先级高于分页插件内的 maxLimit
     */
    public Long maxLimit() {
        return this.maxLimit;
    }

    public int getNumber() {
        return (int) current;
    }

    public boolean isLast() {
        return !hasNext();
    }

    public long getTotalElements() {
        return total;
    }

    /**
     * 查找 order 中正序排序的字段数组
     *
     * @param filter 过滤器
     * @return 返回正序排列的字段数组
     */
    private String[] mapOrderToArray(Predicate<Sort.Order> filter) {
        List<String> columns = sort.stream()
                .filter(i -> (filter.test(i)))
                .map(Sort.Order::getProperty).collect(Collectors.toList());

        return columns.toArray(new String[0]);
    }

    /**
     * 移除符合条件的条件
     *
     * @param filter 条件判断
     */
    private void removeOrder(Predicate<Sort.Order> filter) {
        Iterator<Sort.Order> iterator = sort.stream().iterator();
        while (iterator.hasNext()) {
            Sort.Order next = iterator.next();
            if (filter.test(next)) {
                iterator.remove();
            }
        }
    }


    public PageImpl<T> setSearchCount(boolean searchCount) {
        this.searchCount = searchCount;
        return this;
    }

    @Override
    public boolean searchCount() {
        return searchCount;
    }

    /* --------------- 以下为静态构造方式 --------------- */
    public static <T> PageImpl<T> of(Pageable pageable) {
        return of(pageable.getCurrent(), pageable.getSize(), pageable.searchCount(), pageable.getSort());
    }

    public static <T> PageImpl<T> of(long current, long size) {
        return of(current, size, 0);
    }

    public static <T> PageImpl<T> of(long current, long size, Sort sort) {
        return of(current, size, true, sort);
    }

    public static <T> PageImpl<T> of(long current, long size, boolean searchCount, Sort sort) {
        return new PageImpl<>(current, size, searchCount, sort);
    }

    public static <T> PageImpl<T> of(long current, long size, long total) {
        return of(current, size, total, true, null);
    }

    public static <T> PageImpl<T> of(long current, long size, long total, List<T> records) {
        return of(current, size, total, true, records);
    }

    public static <T> PageImpl<T> of(long current, long size, boolean searchCount) {
        return of(current, size, 0, searchCount, null);
    }

    public static <T> PageImpl<T> of(long current, long size, long total, boolean searchCount, List<T> records) {
        return new PageImpl(current, size, total, searchCount, null, records);
    }


}
