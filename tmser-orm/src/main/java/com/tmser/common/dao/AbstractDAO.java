package com.tmser.common.dao;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.GenerationType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import com.tmser.common.bo.QueryObject;
import com.tmser.common.bo.QueryObject.NamedConditon;
import com.tmser.common.orm.Column;
import com.tmser.common.orm.DefaultMapperCreater;
import com.tmser.common.orm.MapperAware;
import com.tmser.common.orm.MapperContainer;
import com.tmser.common.orm.MapperCreater;
import com.tmser.common.orm.SqlMapping;
import com.tmser.common.orm.Table;
import com.tmser.common.orm.ValidateAbleSqlParameterSource;
import com.tmser.common.page.Page;
import com.tmser.common.page.PageList;
import com.tmser.common.page.PageUtil;
import com.tmser.utils.Identities;
import com.tmser.utils.Reflections;
import com.tmser.utils.StringUtils;

/**
 * 抽象DAO
 * 封装jdbcTemplate
 * 
 * 各Bo DAO类都需继承该类
 * 
 * @author tjx
 * @version 2.0
 * 2014-1-15
 */
public abstract class AbstractDAO<E extends QueryObject, K extends Serializable>
		extends AbstractQuery implements BaseDAO<E, K>, MapperAware{

	private static final Logger logger = LoggerFactory.getLogger(AbstractDAO.class);
	/**
	 * ORM 配置容器
	 */
	@Autowired
	protected MapperContainer mapperContainer;
	
	/**
	 * mapper 创建器，
	 */
	@Autowired(required=false)
	protected MapperCreater mapperCreater = new DefaultMapperCreater();
	
	@Autowired(required=false)
	private SqlMapping sqlMapping = null;
	
	private Table table;
	
	private SimpleJdbcInsert simpleJdbcInsert;

	protected RowMapper<E> mapper;

	private Class<E> entity;

	public AbstractDAO() {
		init();
	}

	@Override
	public Table getTable() {
		final Table t = this.table;
		return t;
	}
	
	protected MapperCreater getMapperCreater() {
		final MapperCreater fm = mapperCreater;
		return fm;
	}
	
	public SqlMapping getSqlMapping() {
		return sqlMapping;
	}

	public void setSqlMapping(SqlMapping sqlMapping) {
		this.sqlMapping = sqlMapping;
	}

	/**
	 * bean 初始化方法
	 */
	@Override
	public void afterPropertiesSet(){
		super.afterPropertiesSet();
		this.table = mapperContainer.getTable(entity.getSimpleName());
		this.simpleJdbcInsert = new SimpleJdbcInsert(getJdbcTemplate())
		.withTableName(this.getTable().getTableName());
		Column c = table.getColumn(table.getPkName());
		if(c.isAutoIncrement() && c.getGenerationType() == GenerationType.IDENTITY){
			this.simpleJdbcInsert.usingGeneratedKeyColumns(table.getPkName());
		}
		this.mapper = mapperCreater.createMapper(entity);
	}
	
	/**
	 * 根据Id删除记录
	 */
	@Override
	public void delete(K id) {
		if(table.getPkName() == null){
			throw new IllegalStateException("Table ["+table.getTableName() +"] hasn't PK.");
		}
		StringBuilder sqlStr = new StringBuilder("delete from ");
		sqlStr.append(getTable().getTableName()).append(" where ").append(table.getPkName()).append("= ?");
		getJdbcTemplate().update(sqlStr.toString(), new Object[] { id });
	}

	/**
	 * 根据id查询对象
	 */
	@Override
	public E get(K id) {
		if(table.getPkName() == null){
			throw new IllegalStateException("Table ["+table.getTableName() +"] hasn't PK.");
		}
		StringBuilder sqlStr = new StringBuilder("select * from ").append(
				getTable().getTableName()).append(" where ").append(table.getPkName()).append(" = ?");
		E e = null;
		try {
			e = getJdbcTemplate().queryForObject(sqlStr.toString(), new Object[] { id }, mapper);
		} catch (IncorrectResultSizeDataAccessException e1) {
			//do nothing
		}
		return e;
	}

	/**
	 * 插入记录
	 */
	@Override
	public E insert(E e) {
		Column c = table.getColumn(table.getPkName());
		if(c.isAutoIncrement()){
			Object key = null;
			if(Number.class.isAssignableFrom((Class<?>)c.getAttrType())){
				key = simpleJdbcInsert.executeAndReturnKey(new ValidateAbleSqlParameterSource(e,getTable()));
			    Reflections.setFieldValue(e, table.getColumn(table.getPkName()).getName(),parseKey((Number)key,c.getAttrType()));
			}else if(String.class.equals(c.getAttrType())){
				key = Identities.uuid2();
				Reflections.setFieldValue(e, table.getColumn(table.getPkName()).getName(),key);
				simpleJdbcInsert.execute(new ValidateAbleSqlParameterSource(e,getTable()));
			}
		}else{
			simpleJdbcInsert.execute(new ValidateAbleSqlParameterSource(e,getTable()));
		}
		
		return e;
	}
	
	protected Object parseKey(Number key,Type t){
		if(Integer.class.equals(t)){
			key = Integer.valueOf(key.intValue());
		}else if(Long.class.equals(t)){
			key = Long.valueOf(key.longValue());
		}else if(Short.class.equals(t)){
			key = Short.valueOf(key.shortValue());
		}
		
		return key;
	}

	/**
	 * 	当数据量比较大的时候自动使用分页插入模式，以提升性能
	 *  开启分页插入模式的条件：
	 *   1、列表总长度大于单次最大插入数目，默认100，可配置。
	 */
	@Override
	public void batchInsert(List<E> updateList){
		if(null == updateList || 0 == updateList.size()){
			return;
		}
		// 如果选择批量插入，并且列表长度大于单次插入最大值，则使用分页插入
		int size = updateList.size();
		if(pageSize < size){
			// 需要的分页总数
			int totalPage = (0 == size / pageSize) ? 
					(size / pageSize) : (size / pageSize+1);
			// 开始分页插入
			for(int pageNo=0; pageNo<totalPage; pageNo++){
				int fromIndex = pageSize * pageNo;
				int endIndex = (size - fromIndex) > pageSize ? fromIndex + pageSize : size;
				insertSqlParameterSourceInfoDB(updateList.subList(fromIndex,endIndex));
			}
		}else {
			// 不分页直接插入
			insertSqlParameterSourceInfoDB(updateList);
		}
	}
	
	/**
	 * 更新对象
	 * 根据传递对象的主键更新对象。null 属性不更新
	 * 要求model 对应表必须设置了主键并正确的注解了，并且model 主键值不能为空
	 * 
	 */
	@Override
	public int update(final E model) throws IllegalArgumentException{
		if(table.getPkName() == null){
			throw new IllegalArgumentException("This table doesn't have a PK column!");
		}
		
		assertNotNull(model,"The object for update ");
		
		StringBuilder sqlStr = new StringBuilder("update ");
		sqlStr.append("`").append(table.getSimpleName()).append("` set ");

		Map<String, Object> paramMap = new HashMap<String, Object>();
		
		String qs = compileUpdateParams(table,model,paramMap,false);
		if(qs == null){
			logger.info("model :" +model +"  no need to update");
			return 0;
		}
		sqlStr.append(qs);
		logger.debug(sqlStr.toString());
		return getNamedParameterJdbcTemplate().update(mappingSql(sqlStr.toString()), paramMap);
	}
	
	/**
	 * 更新对象,null 值不过滤。
	 * 根据传递对象的主键更新对象。
	 * 要求model 对应表必须设置了主键并正确的注解了，并且model 主键值不能为空
	 * 
	 */
	protected void updateWithNullValue(final E model) throws IllegalArgumentException{
		if(table.getPkName() == null){
			throw new IllegalArgumentException("This table doesn't have a PK column!");
		}
		
		assertNotNull(model,"The object for update ");
		
		StringBuilder sqlStr = new StringBuilder("update `");
		sqlStr.append(table.getSimpleName()).append("` set ");

		Map<String, Object> paramMap = new HashMap<String, Object>();
		
		String qs = compileUpdateParams(table,model,paramMap,true);
		if(qs == null){
			logger.info("model :" +model +"  no need to update");
			return;
		}
		sqlStr.append(qs);
		logger.debug(sqlStr.toString());
		getNamedParameterJdbcTemplate().update(mappingSql(sqlStr.toString()), paramMap);
	}
	
	/**
	 * 分页查询
	 * 
	 */
	@Override
	public PageList<E> listPage(E model) {
		assertNotNull(model, "model");
		String customCulomn = model.customCulomn();
		String searchColumn = StringUtils.isBlank(customCulomn)? "*" : customCulomn;
		StringBuilder sqlStr = new StringBuilder("select ")
			.append(searchColumn).append(" from `")
			.append(table.getSimpleName()).append("` ").append(model.alias()).append(" ")
			.append(model.join()).append(" where 1=1 ");
		
		Page page = model.getPage();
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
		
		List<Column> columns = table.getColumns();
		
		sqlStr.append(compileQureyParams(columns,model,paramMap));
		
		//自定义条件
		NamedConditon customConditon = model.customCondition();
		if(customConditon != null){
			sqlStr.append(" ").append(customConditon.getConditon());
			paramMap.putAll(customConditon.getParamMap());
		}
		
		if(page.needTotal())
			 page.setTotalCount(getTotalCountByNamedParams(sqlStr.toString(),paramMap));
		else if(page.getPageSize() == Integer.MAX_VALUE){
		    page.setPageSize(page.getPageSize() -1);
		}
		
        //分组
        String group = model.group();
                
        if(StringUtils.isNotBlank(group)){
            sqlStr.append(" group by ").append(group );
        }
		
		//排序
		String order = model.order();
		if(StringUtils.isNotBlank(order)){
			sqlStr.append(" order by ").append(order);
		}
		
		// 获取分页sql
		String sql = PageUtil.gernatePageSql(mappingSql(sqlStr.toString()), page);
		
		// 查询
		List<E> resultList = getNamedParameterJdbcTemplate().query(sql, paramMap, mapper);
		logger.debug("sql:{},param:{}",sql,paramMap);
		return new PageList<E>(resultList,page);
	}
	
	
	/**
	 * 按模型查询，并限制结果条数
	 * @param model 查询模型
	 * @param limit 结果条数限制
	 * @author tjx
	 */
	@Override
	public List<E> list(E model,int limit) {
		assertNotNull(model, "model"); 
		//查询字段
		String customCulomn = model.customCulomn();
		String searchColumn = StringUtils.isBlank(customCulomn) ? "*" :customCulomn;
		
		StringBuilder sqlStr = new StringBuilder("select ")
			.append(searchColumn).append(" from `")
			.append(table.getSimpleName()).append("` ").append(model.alias()).append(" ")
			.append(model.join())		
			.append(" where 1=1 ");
		
		Page page = new Page(limit-1,false);
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
		
		List<Column> columns = table.getColumns();
		
		sqlStr.append(compileQureyParams(columns,model,paramMap));
		
		//自定义条件
		NamedConditon customConditon = model.customCondition();
		if(customConditon != null){
			sqlStr.append(" ").append(customConditon.getConditon());
			paramMap.putAll(customConditon.getParamMap());
		}
		
        //分组
        String group = model.group();
        if(StringUtils.isNotBlank(group)){
            sqlStr.append(" group by ").append(group);
        }
		//排序
		String order = model.order();
		if(StringUtils.isNotBlank(order)){
			sqlStr.append(" order by ").append(order);
		}
		
		String sql = PageUtil.gernatePageSql(mappingSql(sqlStr.toString()), page);
		
		// 查询
		logger.debug(sql);
		return getNamedParameterJdbcTemplate().query(sql, paramMap, mapper);  
	}
	
	/**
	 * 按模型查询单条记录
	 * @param model
	 * @return
	 * @see com.tmser.common.dao.BaseDAO#findOne(java.lang.Object)
	 */
	@Override
	public E getOne(E model) {
		List<E> rs = list(model,1);
		return rs != null && rs.size() > 0 ? rs.get(0) : null;
	}
	
	
	/**
	 * 按模型查询全部
	 * @param model 查询模型
	 * @author tjx
	 */
	@Override
	public List<E> listAll(E model) {
		return list(model,Integer.MAX_VALUE);
	}
	
	
	/**
	 * 按model 进行 统计
	 * @param sql 要执行的sql
	 * @return 统计结果
	 * @throws DataAccessException
	 */
	@Override
	public int count(E model){
		//查询字段
		String customCulomn = model.customCulomn();
		String searchColumn = StringUtils.isBlank(customCulomn) ? "*" :customCulomn;
		StringBuilder sqlStr = new StringBuilder("select ").append(searchColumn).append(" from `");
		sqlStr.append(table.getSimpleName()).append("` ").append(model.alias()).append(" ").append(model.join()).append(" where 1=1 ");
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
		
		List<Column> columns = table.getColumns();
		
		sqlStr.append(compileQureyParams(columns,model,paramMap));
		
		//自定义条件
		NamedConditon customConditon = model.customCondition();
		if(customConditon != null){
			sqlStr.append(" ").append(customConditon.getConditon());
			paramMap.putAll(customConditon.getParamMap());
		}
		
		 //分组
        String group = model.group();
        if(StringUtils.isNotBlank(group)){
            sqlStr.append(" group by ").append(group);
        }
        
		return getTotalCountByNamedParams(mappingSql(sqlStr.toString()),paramMap);
	}
	
	@Override
	public RowMapper<E> getMapper() {
		return mapper;
	}
	
	@Override
	protected String mappingSql(String oldsql){
		return this.sqlMapping.mapping(oldsql);
	}
	/**
	 * 初始化获取泛型具体类型
	 */
	@SuppressWarnings("unchecked")
	private void init() {
		if (this.entity == null) {
			Class<?> c = getClass();
			ParameterizedType ptype = null;
			do { // 遍历所有超类，直到找泛型定义
				try {
					ptype = (ParameterizedType) c.getGenericSuperclass();
				} catch (Exception e) {
				}
				c = c.getSuperclass();
			} while (ptype == null && c != null);
			if (ptype == null) {
				throw new IllegalStateException("子类中没有定义泛型的具体类型");
			}
			this.entity = (Class<E>) ptype.getActualTypeArguments()[0];
		    logger.trace("init DAO {}, entity is [{}]", getClass().getName(),this.entity);
		}
	}
	
	@Override
	public Class<E> thisBoClass(){
		return this.entity;
	}

	/**
	 * 以传人对象做模型，拼接查询条件
	 * @param columns
	 * @param model
	 * @param paramMap
	 * @return
	 */
	private String compileQureyParams(List<Column> columns,E model,Map<String,Object> paramMap){
		StringBuilder queryParams = new StringBuilder();
		for(Column column : columns){
			Object value = Reflections.getFieldValue(model, column.getName());
			if(value != null){
				if(!String.class.equals(column.getAttrType())
						|| StringUtils.isNotBlank(String.valueOf(value))) {
				    String op = " = :_";
				    if(String.class.equals(column.getAttrType())){
				        String v = ((String)value).trim();
				        if(v.startsWith(SqlMapping.LIKE_PRFIX) || v.endsWith(SqlMapping.LIKE_PRFIX)){
				            op = " like :_";
				            value = v.replace(SqlMapping.LIKE_PRFIX, "%");
				        }
				    }
				    String alias = StringUtils.isEmpty(model.alias()) ? "" : model.alias().trim()+".";
					queryParams.append(" and ").append(alias).append("`").append(column.getName()).append("`")
					.append(op).append(column.getName());
					paramMap.put("_"+column.getName(), value);
				}
				
			}
		}
		
		return queryParams.toString();
	}
	
	/**
	 * 将参数列表插入到数据库中
	 * @param updateList 插入列表
	 */
	private void insertSqlParameterSourceInfoDB(List<E> updateList){
		SqlParameterSource[] paramSources = new SqlParameterSource[updateList.size()];
		int index = 0;
		for(E e : updateList){
			SqlParameterSource paramSource = new ValidateAbleSqlParameterSource(e,getTable());
			paramSources[index++] = paramSource;
		}
		
		simpleJdbcInsert.executeBatch(paramSources);
	}
	
	/**
	 * 以传人对象做模型，拼接更新
	 * @param table
	 * @param model
	 * @param paramMap
	 * @return
	 */
	private String compileUpdateParams(Table table,E model,Map<String,Object> paramMap,boolean filterNull){
		String pkColumn = table.getPkName();
		String pkName = table.getColumn(pkColumn).getName();
		Object pkValue = Reflections.getFieldValue(model,pkName);
		
		if(pkValue == null){
			throw new IllegalArgumentException("model's PK value must not be null!");
		}
		
		StringBuilder queryParams = new StringBuilder();
		List<Column> columns = table.getColumns();
		for(Column column : columns){
			if(column.isPK())
				continue;
			Object value = Reflections.getFieldValue(model, column.getName());
			if(value != null || filterNull){
				if(queryParams.length() > 0)
					queryParams.append(", ");
				queryParams.append("`").append(column.getName()).append("`")
				.append(" = :_").append(column.getName());
				
				paramMap.put("_"+column.getName(), value);
			}
		}
		if(model.customCulomn() != null){
			if(queryParams.length() > 0)
				queryParams.append(", ");
			queryParams.append(model.customCulomn());
		}
		if(queryParams.length() > 0){
			queryParams.append(" where ").append(pkName).append("= :")
			.append(pkName);
			paramMap.put(pkName, pkValue);
			
			//自定义条件
			NamedConditon customConditon = model.customCondition();
			if(customConditon != null){
					queryParams.append(" ").append(customConditon.getConditon());
						paramMap.putAll(customConditon.getParamMap());
			}
			return queryParams.toString();
		}
		
		return null;
	}
	
}
