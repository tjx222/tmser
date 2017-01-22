package com.tmser.common.orm;


import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import javax.persistence.GenerationType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.jdbc.support.JdbcUtils;

import com.tmser.utils.SpringContextHolder;
import com.tmser.utils.StringUtils;



/**
 * Orm 工具类
 * 
 * @author tjx
 * @version 2.0 2014-1-15
 */

public abstract class OrmHelper {
	private final static Logger log = LoggerFactory.getLogger(OrmHelper.class);

	/**
	 * ORM 配置容器
	 */
	private static MapperContainer mapperContainer;
	
	/**
	 * 获取映射文件容器
	 * @return
	 */
	public static MapperContainer getMapperContainer() {
		if (mapperContainer != null) {
			return mapperContainer;
		}
		return SpringContextHolder.getBean(MapperContainer.class);
	}

	
	/**
	 * 将查询结果映射成java实体类
	 * 
	 * @param <T>
	 *            实体类类型
	 * @param rs
	 *            结果集
	 * @param classType
	 *            实体类类型
	 * @param primitivesDefaultedForNullValue 类型匹配错误时是否设为null 
	 * @return 实体类对象
	 * @throws SQLException
	 *             sql异常
	 */
	public static <T> T mapperBean(ResultSet rs,int rowNum, Class<T> classType,boolean primitivesDefaultedForNullValue)
			throws SQLException {
		T t = BeanUtils.instantiate(classType);
		BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(t);
			// 获取table信息
		Table table = getMapperContainer().getTable(classType.getSimpleName());
		ResultSetMetaData rsmd = rs.getMetaData();
			
		String tablename = table.getTableName();
			// 查询结果映射成实体类
		for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				if(!rsmd.getTableName(i).equalsIgnoreCase(tablename)){
					continue;
				}
				Column column = table.getColumn(JdbcUtils.lookupColumnName(rsmd, i));
				if(column == null){
					log.warn("Table [{}] 's column [{}] didn't be mapper to the BO!",
							tablename,rsmd.getColumnName(i));
					continue;
				}
				
				Object value = null;
				if(column != null){
					try {
						value = JdbcUtils.getResultSetValue(rs, i,(Class<?>) column.getAttrType());
						bw.setPropertyValue(column.getName(), value);
					}
					catch (TypeMismatchException e) {
						if (value == null && primitivesDefaultedForNullValue) {
							log.debug("Intercepted TypeMismatchException for row {}"+
									" and column '{}' with value {}"+
									 " when setting property '{}' of type {} on object: {}",
									rowNum,column,value, column.getName(),column.getAttrType(),t);
						}
						else {
							throw e;
						}
					}
				}
		}
		
		return t;
	}

	/**
	 * 根据扫描到的bo 定义类，解析其中jpa,并组成table
	 * 
	 * @param beanDefinition
	 * @return
	 */
	public static Table parseTable(ScannedGenericBeanDefinition beanDefinition) {
		if (beanDefinition == null) {
			return null;
		}
		AnnotationMetadata metadata = beanDefinition.getMetadata();
		if (!metadata.hasAnnotation("javax.persistence.Entity") &&
				!metadata.hasAnnotation("javax.persistence.Table")) {
			log.debug("{} doesn't be Annotation!",beanDefinition.getBeanClassName());
			return null;
		}

		Class<?> c = null;
		try {
			c = beanDefinition.getBeanClass();
		} catch (IllegalStateException e) {
			try {
				c = beanDefinition.resolveBeanClass(OrmHelper.class
						.getClassLoader());
			} catch (ClassNotFoundException ec) {
				log.error("{} doesn't found!",c != null ? c.getName():"class");
				return null;
			}
		}
		
		Table t = new Table();

		if(metadata.hasAnnotation("javax.persistence.Table")){
			Annotation tableAnnotation = AnnotationUtils.getAnnotation(c,
					javax.persistence.Table.class);
			
			t.setCatalog((String) AnnotationUtils.getValue(tableAnnotation,
					"catalog"));
			t.setTableName((String) AnnotationUtils.getValue(tableAnnotation,
					"name"));
			t.setSchema((String) AnnotationUtils
					.getValue(tableAnnotation, "schema"));
			
		}else{
			t.setTableName(c.getSimpleName());
		}
		
		t.setName(c.getName());
		t.setSimpleName(c.getSimpleName());
		fillTableColums(t, c);
		return t;
	}
	
	/**
	 * 根据bo 类型获取其映射文件
	 * @param type bo 类型
	 * @return
	 */
	public static Table getTable(Class<?> type){
		return getMapperContainer().getTable(type.getSimpleName());
	}
	
	/**
	 * 解析栏目
	 * 
	 * @param t 表信息
	 * @param c
	 */
	final static void fillTableColums(Table t, Class<?> c) {
		do {
			Field[] attrs = c.getDeclaredFields();
			for (Field f : attrs) {
				if (!Modifier.isStatic(f.getModifiers())) {
					Column clm = parseColumnFromField(f);
					if (clm != null){
						t.addColumn(clm.getColumn(), clm);
						if(clm.isPK()){
							t.setPkName(clm.getColumn());
						}
					}
				}
			}
			c = c.getSuperclass();
		} while (!c.equals(Object.class));

	}

	static Column parseColumnFromField(Field f) {
		Column clm = null;
		Annotation annotation = AnnotationUtils.getAnnotation(f,
				javax.persistence.Column.class);
		Annotation transientAnnotation = AnnotationUtils.getAnnotation(f,
				javax.persistence.Transient.class);
		if(transientAnnotation == null){
			if (annotation != null) {
				clm = new Column();
				clm.setColumn(StringUtils.defaultIfBlank(
						(String) AnnotationUtils.getValue(annotation, "name"),
						f.getName()).toUpperCase());
				clm.setNullable((Boolean) AnnotationUtils.getValue(annotation,
						"nullable"));
				clm.setLength((Integer) AnnotationUtils.getValue(annotation,
						"length"));
				clm.setUnique((Boolean) AnnotationUtils.getValue(annotation,
						"unique"));
				clm.setColumnDefinition((String) AnnotationUtils.getValue(
						annotation, "columnDefinition"));
	
			}else{
				if(clm == null)	{
						clm = new Column();
						clm.setColumn(f.getName().toUpperCase());
				}
			}
			
			if(f.isAnnotationPresent(javax.persistence.Id.class)){
				clm.setPK(true);
				if(f.isAnnotationPresent(javax.persistence.GeneratedValue.class)){
					Annotation gn = AnnotationUtils.getAnnotation(f,
							javax.persistence.GeneratedValue.class);
						if((GenerationType) AnnotationUtils.getValue(gn, "strategy") != null){
							clm.setAutoIncrement(true);
							clm.setGenerationType((GenerationType) AnnotationUtils.getValue(gn, "strategy"));
						}
				}
			}
			clm.setName(f.getName());
			clm.setAttrType(f.getGenericType());
		}
		return clm;
	}
	
}
