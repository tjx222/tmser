package com.tmser.core.dao;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.DependsOn;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import com.tmser.core.bo.BaseObject;
import com.tmser.core.constants.ConsForSystem;
import com.tmser.core.exception.PersistentNotSetException;
import com.tmser.core.orm.Column;
import com.tmser.core.orm.MapperAware;
import com.tmser.core.orm.MapperContainer;
import com.tmser.core.orm.MapperFactory;
import com.tmser.core.orm.Table;
import com.tmser.core.orm.TspMapper;
import com.tmser.core.orm.ValidateAbleSqlParameterSource;
import com.tmser.core.orm.search.SearchSqlHelper;
import com.tmser.core.page.Page;
import com.tmser.core.page.PageList;
import com.tmser.core.page.PageUtil;
import com.tmser.core.utils.Reflections;
import com.tmser.core.utils.StringUtils;

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
@DependsOn(ConsForSystem.TSP_MAPPER_CONTAINER)
public abstract class AbstractDAO<E extends BaseObject, K extends Serializable>
		implements BaseDAO<E, K>, MapperAware ,InitializingBean{

	protected final Logger log = LoggerFactory.getLogger(getClass());

	/**
	 * ORM 配置容器
	 */
	@Autowired
	@Qualifier(ConsForSystem.TSP_MAPPER_CONTAINER)
	protected MapperContainer mapperContainer;

	private Table table;
	
	private int pageSize = 100;

	@Autowired
	protected JdbcTemplate jdbcTemplate;

	protected NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	private SimpleJdbcInsert simpleJdbcInsert;

	protected SimpleJdbcCall simpleJdbcCall;
	
	protected TspMapper<E> mapper;

	private Class<E> entity;

	public AbstractDAO() {
		init();
	}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}
	
	public NamedParameterJdbcTemplate getNamedParameterJdbcTemplate() {
		return namedParameterJdbcTemplate;
	}

	public SimpleJdbcCall getSimpleJdbcCall() {
		return simpleJdbcCall;
	}

	public Table getTable() {
		final Table t = this.table;
		return t;
	}
	
	/**
	 * 设置批量操作是，批量操作分页的最小记录数
	 * @param pageSize
	 */
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	/**
	 * 使用datasource 初始化template 与setJdbcTemplate 只有一个生效
	 * 
	 */
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	
	/**
	 * 根据Id删除记录
	 */
	@Override
	public void delete(K id) {
		StringBuilder sqlStr = new StringBuilder("delete from ");
		sqlStr.append(getTable().getTableName()).append(" where ").append(table.getPkName()).append("= ?");
		jdbcTemplate.update(sqlStr.toString(), new Object[] { id });
	}

	/**
	 * 根据id查询对象
	 */
	@Override
	public E get(K id) {
		StringBuilder sqlStr = new StringBuilder("select * from ").append(
				getTable().getTableName()).append(" where ").append(table.getPkName()).append(" = ?");
		return jdbcTemplate.queryForObject(sqlStr.toString(),
				new Object[] { id }, mapper);
	}

	/**
	 * 插入记录
	 */
	@Override
	public E insert(E e) {
		SqlParameterSource parameters = new ValidateAbleSqlParameterSource(e,getTable());
		Number key = simpleJdbcInsert.executeAndReturnKey(parameters);
		Column c = table.getColumn(table.getPkName());
		if(Integer.class.equals(c.getAttrType())){
			key = Integer.valueOf(key.intValue());
		}
		Reflections.setFieldValue(e, table.getColumn(table.getPkName()).getName(),key);
		return e;
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
	 * 将参数列表插入到数据库中
	 * @param updateList 插入列表
	 * @param inserActor 指定的插入执行器
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
	 * 更新对象
	 * 根据传递对象的主键更新对象。
	 * 要求model 对应表必须设置了主键并正确的注解了，并且model 主键值不能为空
	 * 
	 */
	@Override
	public void update(final E model) throws IllegalArgumentException{
		if(table.getPkName() == null){
			throw new IllegalArgumentException("This table doesn't have a PK column!");
		}
		
		assertNotNull(model,"The object for update ");
		
		StringBuilder sqlStr = new StringBuilder("update ");
		sqlStr.append(table.getTableName()).append(" set ");

		Map<String, Object> paramMap = new HashMap<String, Object>();
		
		String qs = compileUpdateParams(table,model,paramMap);
		if(qs == null){
			log.info("model :" +model +"  no need to update");
			return;
		}
		sqlStr.append(qs);
		if(log.isDebugEnabled()) {
			log.debug(sqlStr.toString());
		}
		getNamedParameterJdbcTemplate().update(sqlStr.toString(), paramMap);
	}
	
	/**
	 * 分页查询
	 * 
	 */
	@SuppressWarnings("unchecked")
	@Override
	public PageList listPage(E model) {
		StringBuilder sqlStr = new StringBuilder("select * from ");
		sqlStr.append(table.getTableName())	.append(" where 1=1 ");
		
		Page page =(Page) Reflections.getFieldValue(model, "page");
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
		
		List<Column> columns = table.getColumns();
		
		sqlStr.append(compileQureyParams(columns,model,paramMap));
		
		if(page.needTotal())
			 page.setTotalCount(countByNamedSql(sqlStr.toString(),paramMap));
		
		//排序
		String order =(String) Reflections.getFieldValue(model, "tspOrder");
		if(StringUtils.isNotBlank(order)){
			sqlStr.append(" order by ").append(SearchSqlHelper.parseSql(order, model.getClass()));
		}
		
		// 获取分页sql
		String sql = PageUtil.gernatePageSql(sqlStr.toString(), page);
		
		// 查询
		List<E> resultList = namedParameterJdbcTemplate.query(sql, paramMap, mapper);
		if(log.isDebugEnabled()) {
			log.debug(sql);
		}
		return new PageList(resultList,page);
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
		Reflections.invokeSetter(model, "page", new Page(limit,false));
		return listPage(model).getDatalist();
	}
	
	/**
	 * 按模型查询全部
	 * @param model 查询模型
	 * @author tjx
	 */
	@Override
	public List<E> listAll(E model) {
		assertNotNull(model, "model");
		Reflections.invokeSetter(model, "page", new Page(Integer.MAX_VALUE,false));
		return listPage(model).getDatalist();
	}
	
	
	/**
	 * 按model 进行 统计
	 * @param sql 要执行的sql
	 * @return 统计结果
	 * @throws DataAccessException
	 */
	public int count(E model){
		StringBuilder sqlStr = new StringBuilder("select * from ");
		sqlStr.append(table.getTableName())	.append(" where 1=1 ");
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
		
		List<Column> columns = table.getColumns();
		
		sqlStr.append(compileQureyParams(columns,model,paramMap));
		
		return countByNamedSql(sqlStr.toString(),paramMap);
	}
	
	public TspMapper<E> getMapper() {
		return mapper;
	}
	
	/**
	 * 批量执行
	 * 参数占位符使用 "?" 
	 * @param sql
	 * @param params
	 * @return
	 */
	protected void batchUpdate(final String sql, List<Object[]> params) {
		assertNotNull(sql,"batch update sql");
		
		int size = params.size();
		if(pageSize < size){
			// 需要的分页总数
			int totalPage = (0==size / pageSize) ? 
					(size / pageSize) : (size / pageSize+1);
			// 开始分页插入
			for(int pageNo=0; pageNo<totalPage; pageNo++){
				int fromIndex = pageSize * pageNo;
				int endIndex = (size - fromIndex) > pageSize ? fromIndex + pageSize : size;
				getJdbcTemplate().batchUpdate(sql,params.subList(fromIndex, endIndex));
			}
		}else {
			getJdbcTemplate().batchUpdate(sql,params);
		}
		
	 }
	
	/**
	 * 批量执行
	 * 参数占位符使用 ":Name" 形式
	 * @param sql
	 * @param params
	 */
	@SuppressWarnings("unchecked")
	protected void batchUpdateByNameParam(final String sql, Map<String, ?>[] params) {
		assertNotNull(sql,"batch update sql");
		int size = params.length;
		if(pageSize < size){
			// 需要的分页总数
			int totalPage = (0==size / pageSize) ? 
					(size / pageSize) : (size / pageSize+1);
			// 开始分页插入
			for(int pageNo=0; pageNo < totalPage; pageNo++){
				int fromIndex = pageSize * pageNo;
				int lenth = (size - fromIndex) > pageSize ? pageSize : (size - fromIndex);
				Map<String,?>[] tmp = new Map[lenth];
				System.arraycopy(params, fromIndex, tmp, 0, lenth);
				getNamedParameterJdbcTemplate().batchUpdate(sql,tmp);
			}
		}else {
			getNamedParameterJdbcTemplate().batchUpdate(sql,params);
		}
	 }
	
	/**
	 * 执行存储过程
	 * @param pName 存储过程名称
	 * @param params 存储过程参数
	 * @return 
	 */
	protected Map<String,Object> executeProcedure(String pName, Map<String , Object> params){
	    SqlParameterSource in = new MapSqlParameterSource(params);
	    return getSimpleJdbcCall().withProcedureName(pName).execute(in);
	}

	
	/**
	 * 执行sql 统计
	 * 参数占位符为"?"
	 * 
	 * @param sql 要执行的sql
	 * @param args sql 中的参数
	 * @return 统计结果
	 * @throws DataAccessException
	 */
	protected int count(String sql, Object... args){
		assertNotNull(sql,"count sql");
		if(log.isDebugEnabled()) {
			log.debug("sql:" + sql+","+ argsToString(args));
		}
		return getJdbcTemplate().queryForObject(sql, Integer.class, args).intValue();
	}

	/**
	 * 执行sql 统计
	 * 参数占位符使用 ":Name" 形式
	 * @param sql 要执行的sql
	 * @param params namedSql 中的参数名值对
	 * @return 统计结果
	 * @throws DataAccessException
	 */
	protected int countByNamedSql(String namedSql, Map<String,Object> params) throws DataAccessException{
		assertNotNull(namedSql,"count namedSql");
		if(log.isDebugEnabled()) {
			log.debug("sql:" + namedSql+","+ argsToString(params));
		}
		return getNamedParameterJdbcTemplate().queryForObject(namedSql, params , Integer.class).intValue();
	}
	
	/**
	 * 自定义单对象查询
	 * 参数占位符为"?"
	 * 返回一个map 结果
	 * 
	 * @param sql 要执行的sql
	 * @param args 参数列表
	 * @return map 结果
	 * @throws DataAccessException
	 */
	protected Map<String,Object> queryForMap(String sql, Object[] args) throws DataAccessException {
		assertNotNull(sql,"query sql");
		if(log.isDebugEnabled()) {
			log.debug("sql:" + sql +","+ argsToString(args));
		}
		try{
			return getJdbcTemplate().queryForMap(sql, args);
		}catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}
	
	/**
	 * 自定义单对象查询
	 * 参数占位符为":Name"
	 * 
	 * @param sql 要执行的sql
	 * @param args 参数列表
	 * @return map 结果
	 * @throws DataAccessException
	 */
	protected Map<String,Object> queryForMap(String sql, Map<String,Object> args) throws DataAccessException {
		assertNotNull(sql,"query sql");
		if(log.isDebugEnabled()) {
			log.debug("sql:" + sql +","+ argsToString(args));
		}
		try {
			return getNamedParameterJdbcTemplate().queryForMap(sql, args);
		}catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}
	
	/**
	 * 自定义TspMapper 查询
	 * 参数占位符为"?"
	 * 
	 * @param sql 要执行的sql
	 * @param args 参数列表
	 * @param TspMapper 自己类型转换器
	 * @return
	 * @throws DataAccessException
	 */
	protected <T> List<T> query(String sql, Object[] args, TspMapper<T> TspMapper) throws DataAccessException {
		assertNotNull(sql,"query sql");
		if(log.isDebugEnabled()) {
			log.debug("sql:" + sql +","+ argsToString(args));
		}
		return getJdbcTemplate().query(sql, args, TspMapper);
	}
	
	/**
	 * 自定义查询
	 * 参数占位符为"?"
	 * 
	 * @param sql 要执行的sql
	 * @param args 参数列表
	 * @param TspMapper 自己类型转换器
	 * @return map 结果map列表
	 * @throws DataAccessException
	 */
	protected List<Map<String,Object>> query(String sql, Object[] args) throws DataAccessException {
		assertNotNull(sql,"query sql");
		if(log.isDebugEnabled()) {
			log.debug("sql:" + sql +","+ argsToString(args));
		}
		return getJdbcTemplate().queryForList(sql, args);
	}
	
	
	/**
	 * 自定义TspMapper 查询，返回单个结果
	 * 参数占位符为"?"
	 * 
	 * @param sql 要执行的sql
	 * @param args 参数列表
	 * @param TspMapper 自己类型转换器
	 * @return
	 * @throws DataAccessException
	 */
	protected <T> T queryForSingle(String sql, Object[] args, TspMapper<T> TspMapper) throws DataAccessException {
		List<T> rs = query(sql, args, TspMapper);
		if(rs != null && rs.size() > 0){
			return rs.get(0);
		}
		return null;
	}
	
	/**
	 * 自定义TspMapper 查询
	 * 参数占位符使用 ":Name" 形式
	 * 
	 * @param sql 要执行的sql
	 * @param args sql 中的参数名值对
	 * @param TspMapper 自己类型转换器
	 * @return
	 * @throws DataAccessException
	 */
	protected <T> List<T> queryByNamedSql(String sql,Map<String,Object> args, TspMapper<T> TspMapper) throws DataAccessException {
		assertNotNull(sql,"query sql");
		if(log.isDebugEnabled()) {
			log.debug("sql:" + sql+","+ argsToString(args));
		}
		return getNamedParameterJdbcTemplate().query(sql, args, TspMapper);
	}
	
	
	/**
	 * 自定义TspMapper 查询,返回单个结果
	 * 参数占位符使用 ":Name" 形式
	 * 
	 * @param sql 要执行的sql
	 * @param args sql 中的参数名值对
	 * @param TspMapper 自己类型转换器
	 * @return
	 * @throws DataAccessException
	 */
	protected <T>T queryByNamedSqlForSingle(String sql,Map<String,Object> args, TspMapper<T> TspMapper) throws DataAccessException {
		List<T> rs = queryByNamedSql(sql, args, TspMapper);
		if(rs != null && rs.size() > 0){
			return rs.get(0);
		}
		return null;
	}
	
	
	/**
	 * 自定义查询
	 * 参数占位符为":Name"
	 * 
	 * @param sql 要执行的sql
	 * @param args 参数列表
	 * @return 结果map列表
	 * @throws DataAccessException
	 */
	protected List<Map<String,Object>> queryByNamedSql(String sql, Map<String,Object> args) throws DataAccessException {
		assertNotNull(sql,"query sql");
		if(log.isDebugEnabled()) {
			log.debug("sql:" + sql +","+ argsToString(args));
		}
		return getNamedParameterJdbcTemplate().queryForList(sql, args);
	}
	
	
	/**
	 * 自定义TspMapper 查询,且可限制结果条数
	 * 参数占位符为"?"
	 * 
	 * @param sql 要执行的sql
	 * @param args 参数列表
	 * @param TspMapper 自己类型转换器
	 * @return
	 * @throws DataAccessException
	 */
	protected <T> List<T> queryWithLimit(String sql, Object[] args, TspMapper<T> TspMapper,int limit) throws DataAccessException {
		assertNotNull(sql,"query sql");
		String wrapperSql = PageUtil.gernatePageSql(sql, new Page(limit));
		return query(wrapperSql, args, TspMapper);
	}
	
	/**
	 * map查询,且可限制结果条数
	 * 参数占位符为"?"
	 * 
	 * @param sql 要执行的sql
	 * @param args 参数列表
	 * @return 返回map 列表
	 * @throws DataAccessException
	 */
	protected List<Map<String,Object>> queryWithLimit(String sql, Object[] args,int limit) throws DataAccessException {
		assertNotNull(sql,"query sql");
		String wrapperSql = PageUtil.gernatePageSql(sql, new Page(limit));
		return query(wrapperSql, args);
	}
	
	/**
	 * 自定义TspMapper 查询，且可限制结果条数
	 * 参数占位符使用 ":Name" 形式
	 * 
	 * @param sql 要执行的sql
	 * @param args sql 中的参数名值对
	 * @param TspMapper 自己类型转换器
	 * @return
	 * @throws DataAccessException
	 */
	protected <T> List<T> queryByNamedSqlWithLimit(String sql,Map<String,Object> args, TspMapper<T> TspMapper,int limit) throws DataAccessException {
		assertNotNull(sql,"query sql");
		String wrapperSql = PageUtil.gernatePageSql(sql, new Page(limit));
		return queryByNamedSql(wrapperSql, args, TspMapper);
	}
	
	
	/**
	 * 自定义TspMapper 查询，且可限制结果条数
	 * 参数占位符使用 ":Name" 形式
	 * 
	 * @param sql 要执行的sql
	 * @param args sql 中的参数名值对
	 * @param TspMapper 自己类型转换器
	 * @return
	 * @throws DataAccessException
	 */
	protected List<Map<String,Object>> queryByNamedSqlWithLimit(String sql,Map<String,Object> args, int limit) throws DataAccessException {
		assertNotNull(sql,"query sql");
		String wrapperSql = PageUtil.gernatePageSql(sql, new Page(limit));
		return queryByNamedSql(wrapperSql, args);
	}
	
	
	/**
	 * 自定义TspMapper 查询，且可进行分页
	 * 参数占位符为"?"
	 * 
	 * @param sql 要执行的sql
	 * @param args 参数列表
	 * @param TspMapper 自己类型转换器
	 * @return
	 * @throws DataAccessException
	 */
	protected <T> PageList queryPage(String sql, Object[] args, TspMapper<T> TspMapper,final Page pageInfo) throws DataAccessException {
		assertNotNull(sql,"query sql");
		String wrapperSql = PageUtil.gernatePageSql(sql,pageInfo);
		if(pageInfo.needTotal())
			 pageInfo.setTotalCount(count(sql,args));
		return new PageList(query(wrapperSql, args, TspMapper),pageInfo);
	}
	
	
	/**
	 * map 查询，且可进行分页
	 * 参数占位符为"?"
	 * 
	 * @param sql 要执行的sql
	 * @param args 参数列表
	 * @param TspMapper 自己类型转换器
	 * @return 分页的map
	 * @throws DataAccessException
	 */
	protected PageList queryPage(String sql, Object[] args,final Page pageInfo) throws DataAccessException {
		assertNotNull(sql,"query sql");
		String wrapperSql = PageUtil.gernatePageSql(sql,pageInfo);
		if(pageInfo.needTotal())
			 pageInfo.setTotalCount(count(sql,args));
		return new PageList(query(wrapperSql, args),pageInfo);
	}
	
	/**
	 * 自定义TspMapper 查询，且可进行分页
	 * 参数占位符使用 ":Name" 形式
	 * 
	 * @param sql 要执行的sql
	 * @param args sql 中的参数名值对
	 * @param TspMapper 自己类型转换器
	 * @return
	 * @throws DataAccessException
	 */
	protected <T> PageList queryPageByNamedSql(String sql,Map<String,Object> args, TspMapper<T> TspMapper,Page pageInfo) throws DataAccessException {
		assertNotNull(sql,"query sql");
		String wrapperSql = PageUtil.gernatePageSql(sql, pageInfo);
		if(log.isDebugEnabled()) {
			log.debug("sql:" + wrapperSql+","+ argsToString(args));
		}
		 if(pageInfo.needTotal()) {
			 pageInfo.setTotalCount(countByNamedSql(sql,args));
		 }
		return new PageList(getNamedParameterJdbcTemplate().query(wrapperSql, args, TspMapper),pageInfo);
	}
	
	
	/**
	 * map 查询，且可进行分页
	 * 参数占位符使用 ":Name" 形式
	 * 
	 * @param sql 要执行的sql
	 * @param args sql 中的参数名值对
	 * @param TspMapper 自己类型转换器
	 * @return 分页的map
	 * @throws DataAccessException
	 */
	protected PageList queryPageByNamedSql(String sql,Map<String,Object> args, Page pageInfo) throws DataAccessException {
		assertNotNull(sql,"query sql");
		String wrapperSql = PageUtil.gernatePageSql(sql, pageInfo);
		if(log.isDebugEnabled()) {
			log.debug("sql:" + wrapperSql+","+ argsToString(args));
		}
		 if(pageInfo.needTotal()) {
			 pageInfo.setTotalCount(countByNamedSql(sql,args));
		 }
		return new PageList(queryByNamedSql(wrapperSql, args),pageInfo);
	}
	
	/**
	 * 自定义更新语句更新
	 * 参数占位符为"?"
	 * 
	 * @param sql
	 * @param args
	 */
	protected void update(String sql,Object...args){
		if(log.isDebugEnabled()) {
			log.debug("sql:" + sql+","+ argsToString(args));
		}
		getJdbcTemplate().update(sql,args);
	}
	
	
	/**
	 * 自定义更新语句更新
	 * 参数占位符为"?"
	 * 
	 * @param sql
	 * @param args
	 */
	protected void updateWithNamedSql(String namedSql,Map<String,Object>args){
		if(log.isDebugEnabled()) {
			log.debug("sql:" + namedSql+","+ argsToString(args));
		}
		getNamedParameterJdbcTemplate().update(namedSql,args);
	}
	
	/**
	 * 
	 */
	public void afterPropertiesSet(){
		this.table = mapperContainer.getTable(entity.getName());
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(
				this.jdbcTemplate);
		this.simpleJdbcInsert = new SimpleJdbcInsert(this.jdbcTemplate)
		.withTableName(this.getTable().getTableName())
		.usingGeneratedKeyColumns(table.getPkName());
		this.simpleJdbcCall = new SimpleJdbcCall(this.jdbcTemplate);
		this.mapper = MapperFactory.getMapper(entity);
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
				throw new PersistentNotSetException("子类中没有定义泛型的具体类型");
			}
			this.entity = (Class<E>) ptype.getActualTypeArguments()[0];
			log.info("init DAO " + getClass().getName() + ", entity is ["
					+ this.entity.getName() + "]");
		}
	}

	private void assertNotNull(Object e,String name) {
		if (e == null || StringUtils.isEmpty(e.toString())) {
			throw new NullPointerException(name + " cann't be null or empty");
		}
	}
	
	private String argsToString(Object[] args){
		StringBuilder s = new StringBuilder("args:[");
		for(Object o : args){
			s.append(o).append(",");
		}
		s.append("]");
		
		return s.toString();
	}
	
	private String argsToString(Map<String,Object> args){
		StringBuilder s = new StringBuilder("args:[");
		for(String key : args.keySet()){
			s.append(key).append(":").append(args.get(key)).append(",");
		}
		s.append("]");
		
		return s.toString();
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
					queryParams.append(" and ").append(column.getColumn())
					.append(" = :").append(column.getName());
					paramMap.put(column.getName(), value);
				}
				
			}
		}
		
		return queryParams.toString();
	}
	
	/**
	 * 以传人对象做模型，拼接更新
	 * @param table
	 * @param model
	 * @param paramMap
	 * @return
	 */
	private String compileUpdateParams(Table table,E model,Map<String,Object> paramMap){
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
			if(value != null){
				if(queryParams.length() > 0)
					queryParams.append(", ");
				queryParams.append(column.getColumn())
				.append(" = :").append(column.getName());
				
				paramMap.put(column.getName(), value);
			}
		}
		
		if(queryParams.length() > 0){
			queryParams.append(" where ").append(pkColumn).append("= :")
			.append(pkName);
			paramMap.put(pkName, pkValue);
			
			return queryParams.toString();
		}
		
		return null;
	}
}
