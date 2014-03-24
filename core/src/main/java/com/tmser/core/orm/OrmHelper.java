package com.tmser.core.orm;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.type.AnnotationMetadata;

import com.tmser.core.utils.Reflections;
import com.tmser.core.utils.SpringContextHolder;
import com.tmser.core.utils.StringUtils;

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
		return SpringContextHolder.getBean("tspMapperContainer");
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
	 * @return 实体类对象
	 * @throws SQLException
	 *             sql异常
	 */
	public static <T> T mapperBean(ResultSet rs, Class<T> classType)
			throws SQLException {
		T t = null;
		try {
			t = classType.newInstance();
			// 获取table信息
			Table table = getMapperContainer().getTable(classType.getName());
			ResultSetMetaData rsmd = rs.getMetaData();
			
			String tablename = table.getTableName();

			// 查询结果映射成实体类
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				if(!rsmd.getTableName(i).equalsIgnoreCase(tablename)){
					continue;
				}
				Column column = table.getColumn(rsmd.getColumnName(i));
				if(column == null){
					log.warn("Table [" + tablename +"] 's column ["+rsmd.getColumnName(i)+"] didn't be mapper to the BO!" );
					continue;
				}
				
				Object o = rs.getObject(i); //目前只处理Boolean
				if(o instanceof Boolean && !column.getAttrType().toString().equals(Boolean.class.toString())){
					Boolean v = (Boolean) o;
					if(column.getAttrType().toString().equals(Short.class.toString())){
						o = v ? Short.valueOf((short)1):Short.valueOf((short)0);
					}else if(column.getAttrType().toString().equals(Integer.class.toString())){
						o = v ? Integer.valueOf((short)1):Integer.valueOf((short)0);
					}
				}
				
				if(column != null){
					Reflections.invokeSetter(t, column.getName(), o);
				}
			}
		} catch (InstantiationException e) {
			e.printStackTrace();
			throw new SQLException(e.getMessage());
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new SQLException(e.getMessage());
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
			log.debug(beanDefinition.getBeanClassName()
					+ " doesn't be Annotation!");
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
				log.error(c.getName() + "doesn't found!");
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
		fillTableColums(t, c);
		return t;
	}

	/**
	 * 根据传递的类型，创建对象
	 * @param entity
	 * @return
	 */
	public static Object getEntity(Class<?> entity) {
		Object b = null;
		try {
			b = entity.newInstance();
		} catch (InstantiationException e) {
			log.error(entity.getName()
					+ " can't be create! err: InstantiationException");
		} catch (IllegalAccessException e) {
			log.error(entity.getName()
					+ " can't be create! err: IllegalAccessException");
		}
		return b;
	}
	
	/**
	 * 根据bo 类型获取其映射文件
	 * @param type bo 类型
	 * @return
	 */
	public static Table getTable(Class<?> type){
		return getMapperContainer().getTable(type.getName());
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
			}
			clm.setName(f.getName());
			clm.setAttrType(f.getGenericType());
		}
		return clm;
	}
	
}
