package com.tmser.common.dao;

import java.io.Serializable;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;

import com.tmser.common.page.PageList;


/**
 * 
 * Mapper 基本方法
 * 
 * @author tjx
 * 
 * @param <E>
 *            所操作的实体类类型
 * @param <K>
 *            主键类型
 * @author tjx
 * @version 2.0
 */
public interface BaseDAO<E, K extends Serializable> {

	/**
	 * 插入
	 * 
	 * @param e
	 *            要插入的对象属性
	 */
	E insert(E e);

	/**
	 * 更新
	 * 
	 * @param e
	 *            更新的属性
	 * @return 受影响的记录条数        
	 */
	int	update(E e);
	

	/**
	 * 根据主键id 获取记录
	 * 
	 * @param id
	 *            id
	 * @return 获取的对象记录
	 */
	E get(K id);

	/**
	 * 根据模板对象分页查询
	 * 
	 * @param model
	 *            查询条件
	 * @return 结果集合
	 */
	PageList<E> listPage(E model);
	
	/**
	 * 按模型查询一条记录
	 * 
	 * @param model  查询条件
	 * @return E
	 */
	E getOne(E model);

	/**
	 * 根据模板对象分页查询,并限制结果数
	 * 
	 * @param model  查询条件
	 * @param limit 结果条数
	 * @return 结果集合
	 */
	List<E> list(E model,int limit);
	
	/**
	 * 根据模板对象分页查询
	 * 
	 * @param model  查询条件
	 * @return 结果集合
	 */
	List<E> listAll(E model);
	
	/**
	 * 根据id 删除
	 * 
	 * @param id
	 *            对象id
	 */
	void delete(K id);
	
	/**
	 * 根据模式统计结果
	 * @param model
	 * @return
	 */
	int count(E model);
	
	/**
	 * 获取当前BO 的映射
	 * 
	 */
	RowMapper<E> getMapper();
	
	/**
	 * 批量插入
	 * <p>
	 * 	批量将对象列表插入到数据库中，主键自动生成。
	 *  
	 *  使用示例
	 *  -------------------------------------------------------
	 *  List<Person> addlist = new ArrayList<Person>();
	 *  Person p1 = new Person();
	 *  p1.init(...);
	 *  Person p2 = new Person();
	 *  p2.init(...);
	 *  
	 *  addlist.add(p1); 
	 *  addlist.add(p2);
	 *  
	 *  batchInsert(addlist);
	 *  ...
	 * </p>
	 * @param updateList 插入列表
	 */
	void batchInsert(List<E> updateList);
	
	/**
	 * 返回当前dao 相关的bo 类
	 * @return
	 */
	Class<E> thisBoClass();
}
