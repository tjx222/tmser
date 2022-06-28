package com.tmser.database.mybatis;

public enum ESqlMethod {
    SELECT_BY_BIZ_ID("selectByBizId", "根据BizID 查询一条数据", "SELECT %s FROM %s WHERE %s=#{%s} %s"),
    SELECT_BY_BIZ_IDS("selectByBizIds", "根据BizID集合，批量查询数据", "<script>SELECT %s FROM %s WHERE %s IN (%s) %s </script>");

    private final String method;
    private final String desc;
    private final String sql;

    ESqlMethod(String method, String desc, String sql) {
        this.method = method;
        this.desc = desc;
        this.sql = sql;
    }

    public String getMethod() {
        return method;
    }

    public String getDesc() {
        return desc;
    }

    public String getSql() {
        return sql;
    }

}
