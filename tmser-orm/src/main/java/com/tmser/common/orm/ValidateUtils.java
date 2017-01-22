package com.tmser.common.orm;


/**
 * 字段验证
 * @author tjx
 * @version 2.0
 * 2014-1-20
 */
public final class ValidateUtils {
	/**
	 * 检查参数合法性
	 * 
	 * @param column
	 *            字段信息
	 * @param value
	 *            字段值
	 */
	public static void checkColumnValue(Column column, Object value) throws IllegalArgumentException{
		checkColumnNull(column, value);
		checkColumnLength(column, value);
	}

	/**
	 * 字段空值检查
	 * 
	 * @param column
	 *            字段信息
	 * @param value
	 *            字段值
	 */
	public static void checkColumnNull(Column column, Object value) {
		if (value == null) {
			// 非空属性判断
			if (!column.isNullable()) {
				throw new IllegalArgumentException(column.getColumn() + "不能为空！");
			}
		}
	}

	/**
	 * 字段长度检查
	 * 
	 * @param column
	 *            字段信息
	 * @param value
	 *            字段值
	 */
	public static void checkColumnLength(Column column, Object value) {
		if (value != null) {
			if (value instanceof String) {
				String valueStr = value.toString();
				int limitLength = column.getLength();
				if (limitLength > 0 && valueStr.length() > limitLength) {
					throw new IllegalArgumentException(column.getColumn()
							+ "超过长度限制！");
				}
			}
		}
	}
}
