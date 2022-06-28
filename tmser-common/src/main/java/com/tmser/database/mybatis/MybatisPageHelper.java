package com.tmser.database.mybatis;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.tmser.model.page.Page;
import com.tmser.model.page.PageImpl;
import com.tmser.model.page.Pageable;
import com.tmser.model.sort.Sort;

import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * <pre>
 *    框架分页转mybatis 分页
 * </pre>
 *
 * @version $Id: MybatisPageHelper.java, v1.0 2022/5/5 9:56 tmser Exp $
 */
public final class MybatisPageHelper {

    /**
     * common page 转 mybatis page
     *
     * @param page common page
     * @return
     */
    public static <T> IPage<T> changeToMybatisPage(Pageable page) {
        return changeToMybatisPage(page, true);
    }


    /**
     * common page 转 mybatis page
     *
     * @param page common page
     * @return
     */
    public static <T> IPage<T> changeToMybatisPage(Pageable page, boolean needOptimizeCountSql) {

        com.baomidou.mybatisplus.extension.plugins.pagination.Page mybatisPage = com.baomidou.mybatisplus.extension.plugins.pagination.Page
                .of(page.getCurrent(), page.getSize(), page.searchCount())
                .addOrder(Optional.ofNullable(page.getSort())
                        .map(s ->
                                s.stream().map(order -> order.getDirection() == Sort.Direction.ASC ? OrderItem.asc(order.getProperty()) : OrderItem.desc(order.getProperty()))
                                        .collect(Collectors.toList())
                        ).orElse(Collections.emptyList()));
        mybatisPage.setMaxLimit(page.maxLimit());
        mybatisPage.setOptimizeCountSql(needOptimizeCountSql);
        return mybatisPage;
    }

    /**
     * 填充page data
     */
    public static <T> Page<T> fillPageData(IPage<T> dataPage, Page<T> page) {
        return page.setContent(dataPage.getRecords()).setTotal(dataPage.getTotal());
    }

    /**
     * 填充page data
     */
    public static <T> Page<T> fillPageData(IPage<T> dataPage, Pageable page) {
        return PageImpl.<T>of(page).setContent(dataPage.getRecords()).setTotal(dataPage.getTotal());
    }

}
