package com.tmser.common.dao;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;

import com.tmser.common.page.Page;
import com.tmser.common.page.PageList;
import com.tmser.common.page.PageUtil;
import com.tmser.utils.StringUtils;

public class AbstractQuery implements InitializingBean{
	
	protected final Logger log = LoggerFactory.getLogger(getClass());
	private static final Logger logger = LoggerFactory.getLogger(AbstractQuery.class);
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	protected NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	protected SimpleJdbcCall simpleJdbcCall;
	
	protected int pageSize = 100;
	
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}
	
	/**
	 * Setter method for property <tt>jdbcTemplate</tt>.
	 * @param jdbcTemplate value to be assigned to property jdbcTemplate
	 */
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public NamedParameterJdbcTemplate getNamedParameterJdbcTemplate() {
		return namedParameterJdbcTemplate;
	}

	/**
	 * 使用datasource 初始化template 与setJdbcTemplate 只有一个生效
	 */
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	/**
	 * 设置批量操作是，批量操作分页的最小记录数
	 * @param pageSize
	 */
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	

	public SimpleJdbcCall getSimpleJdbcCall() {
		return simpleJdbcCall;
	}

	/**
	 * bean 初始化方法
	 */
	@Override
	public void afterPropertiesSet(){
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(
				this.jdbcTemplate);
		this.simpleJdbcCall = new SimpleJdbcCall(this.jdbcTemplate);
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
				getJdbcTemplate().batchUpdate(mappingSql(sql),params.subList(fromIndex, endIndex));
			}
		}else {
			getJdbcTemplate().batchUpdate(mappingSql(sql),params);
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
				getNamedParameterJdbcTemplate().batchUpdate(mappingSql(sql),tmp);
			}
		}else {
			getNamedParameterJdbcTemplate().batchUpdate(mappingSql(sql),params);
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
		logger.debug("sql:" + sql+","+ argsToString(args));
		Integer rs = getJdbcTemplate().queryForObject(mappingSql(sql), Integer.class, args);
		return rs == null ? 0 : rs.intValue();
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
		logger.debug("sql:" + namedSql+","+ argsToString(params));
		Integer rs = getNamedParameterJdbcTemplate().queryForObject(mappingSql(namedSql), params , Integer.class);
		return rs == null ? 0 : rs.intValue();
	}
	
	/**
	 * 自定义单对象查询
	 * 参数占位符为"?"
	 * 返回一个map 结果
	 * 
	 * @param sql 要执行的sql
	 * @param args 参数列表
	 * @return map 结果,空返回null, 多条抛@see IncorrectResultSizeDataAccessException 异常
	 * @throws DataAccessException
	 */
	protected Map<String,Object> queryForMap(String sql, Object[] args) throws DataAccessException {
		assertNotNull(sql,"query sql");
		logger.debug("sql:" + sql +","+ argsToString(args));
		try{
			return getJdbcTemplate().queryForMap(mappingSql(sql), args);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}

	}
	
	/**
	 * 自定义单对象查询
	 * 参数占位符为":Name"
	 * 
	 * @param sql 要执行的sql
	 * @param args 参数列表
	 * @return map 结果，空返回null，多条抛@see IncorrectResultSizeDataAccessException 异常
	 * @throws DataAccessException
	 */
	protected Map<String,Object> queryForMapByNameSql(String sql, Map<String,Object> args) throws DataAccessException {
		assertNotNull(sql,"query sql");
		logger.debug("sql:" + sql +","+ argsToString(args));
		try {
			return getNamedParameterJdbcTemplate().queryForMap(mappingSql(sql), args);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}

	}
	
	/**
	 * 自定义TspMapper 查询
	 * 参数占位符为"?"
	 * 
	 * @param sql 要执行的sql
	 * @param args 参数列表
	 * @param rowMapper 自己类型转换器
	 * @return
	 * @throws DataAccessException
	 */
	protected <T> List<T> query(String sql, Object[] args, RowMapper<T> rowMapper) throws DataAccessException {
		assertNotNull(sql,"query sql");
		logger.debug("sql:" + sql +","+ argsToString(args));
		return getJdbcTemplate().query(mappingSql(sql), args, rowMapper);
	}
	
	/**
	 * 自定义查询
	 * 参数占位符为"?"
	 * 
	 * @param sql 要执行的sql
	 * @param args 参数列表
	 * @param rowMapper 自己类型转换器
	 * @return map 结果map列表
	 * @throws DataAccessException
	 */
	protected List<Map<String,Object>> query(String sql, Object[] args) throws DataAccessException {
		assertNotNull(sql,"query sql");
		logger.debug("sql:" + sql +","+ argsToString(args));
		return getJdbcTemplate().queryForList(mappingSql(sql), args);
	}
	
	
	/**
	 * 自定义Mapper 查询，返回单个结果
	 * 参数占位符为"?"
	 * 
	 * @param sql 要执行的sql
	 * @param args 参数列表
	 * @param rowMapper 自己类型转换器
	 * @return T 结果，空返回null，多条抛@see IncorrectResultSizeDataAccessException 异常
	 * @throws DataAccessException
	 */
	protected <T> T queryForSingle(String sql, Object[] args, RowMapper<T> rowMapper) throws DataAccessException {
		assertNotNull(sql,"query sql");
		logger.debug("sql:" + sql +","+ argsToString(args));
		try {
			return getJdbcTemplate().queryForObject(mappingSql(sql), args, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}
	
	/**
	 * 自定义TspMapper 查询
	 * 参数占位符使用 ":Name" 形式
	 * 
	 * @param sql 要执行的sql
	 * @param args sql 中的参数名值对
	 * @param rowMapper 自己类型转换器
	 * @return
	 * @throws DataAccessException
	 */
	protected <T> List<T> queryByNamedSql(String sql,Map<String,Object> args, RowMapper<T> rowMapper) throws DataAccessException {
		assertNotNull(sql,"query sql");
		return queryByNamed(mappingSql(sql), args, rowMapper);
	}
	
	private <T> List<T> queryByNamed(String sql,Map<String,Object> args, RowMapper<T> rowMapper) throws DataAccessException {
		assertNotNull(sql,"query sql");
		logger.debug("sql:" + sql+","+ argsToString(args));
		return getNamedParameterJdbcTemplate().query(sql, args, rowMapper);
	}
	
	private List<Map<String,Object>> queryByNamed(String sql, Map<String,Object> args) throws DataAccessException {
		assertNotNull(sql,"query sql");
		logger.debug("sql:" + sql +","+ argsToString(args));
		return getNamedParameterJdbcTemplate().queryForList(sql, args);
	}
	
	/**
	 * 自定义TspMapper 查询,返回单个结果
	 * 参数占位符使用 ":Name" 形式
	 * 
	 * @param sql 要执行的sql
	 * @param args sql 中的参数名值对
	 * @param rowMapper 自己类型转换器
	 * @return
	 * @throws DataAccessException
	 */
	protected <T>T queryByNamedSqlForSingle(String sql,Map<String,Object> args, RowMapper<T> rowMapper) throws DataAccessException {
		logger.debug("sql:" + sql+","+ argsToString(args));
		List<T> rs = queryByNamedSql(sql, args, rowMapper);
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
		return queryByNamed(mappingSql(sql), args);
	}
	
	/**
	 * 自定义TspMapper 查询,且可限制结果条数
	 * 参数占位符为"?"
	 * 
	 * @param sql 要执行的sql
	 * @param args 参数列表
	 * @param rowMapper 自己类型转换器
	 * @return
	 * @throws DataAccessException
	 */
	protected <T> List<T> queryWithLimit(String sql, Object[] args, RowMapper<T> rowMapper,int limit) throws DataAccessException {
		assertNotNull(sql,"query sql");
		String wrapperSql = PageUtil.gernatePageSql(sql, new Page(limit-1,false));
		return query(wrapperSql, args, rowMapper);
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
		String wrapperSql = PageUtil.gernatePageSql(sql, new Page(limit-1,false));
		return query(wrapperSql, args);
	}
	
	/**
	 * 自定义TspMapper 查询，且可限制结果条数
	 * 参数占位符使用 ":Name" 形式
	 * 
	 * @param sql 要执行的sql
	 * @param args sql 中的参数名值对
	 * @param rowMapper 自己类型转换器
	 * @return
	 * @throws DataAccessException
	 */
	protected <T> List<T> queryByNamedSqlWithLimit(String sql,Map<String,Object> args, RowMapper<T> rowMapper,int limit) throws DataAccessException {
		assertNotNull(sql,"query sql");
		String wrapperSql = PageUtil.gernatePageSql(mappingSql(sql), new Page(limit-1,false));
		return queryByNamed(wrapperSql, args, rowMapper);
	}
	
	
	/**
	 * 自定义TspMapper 查询，且可限制结果条数
	 * 参数占位符使用 ":Name" 形式
	 * 
	 * @param sql 要执行的sql
	 * @param args sql 中的参数名值对
	 * @param rowMapper 自己类型转换器
	 * @return
	 * @throws DataAccessException
	 */
	protected List<Map<String,Object>> queryByNamedSqlWithLimit(String sql,Map<String,Object> args, int limit) throws DataAccessException {
		assertNotNull(sql,"query sql");
		String wrapperSql = PageUtil.gernatePageSql(mappingSql(sql), new Page(limit-1,false));
		return queryByNamed(wrapperSql, args);
	}
	
	
	/**
	 * 自定义TspMapper 查询，且可进行分页
	 * 参数占位符为"?"
	 * 
	 * @param sql 要执行的sql
	 * @param args 参数列表
	 * @param rowMapper 自己类型转换器
	 * @return
	 * @throws DataAccessException
	 */
	protected <T> PageList<T> queryPage(String sql, Object[] args, RowMapper<T> rowMapper,final Page pageInfo) throws DataAccessException {
		assertNotNull(sql,"query sql");
	    if(pageInfo.needTotal()){
	            pageInfo.setTotalCount(getTotalCount(sql,args));
	        }else if(pageInfo.getPageSize() == Integer.MAX_VALUE){
	                pageInfo.setPageSize(pageInfo.getPageSize() - 1);
	        }
		String wrapperSql = PageUtil.gernatePageSql(sql,pageInfo);
		return new PageList<T>(query(wrapperSql, args, rowMapper),pageInfo);
	}
	
	
	/**
	 * map 查询，且可进行分页
	 * 参数占位符为"?"
	 * 
	 * @param sql 要执行的sql
	 * @param args 参数列表
	 * @param rowMapper 自己类型转换器
	 * @return 分页的map
	 * @throws DataAccessException
	 */
	protected PageList<Map<String,Object>> queryPage(String sql, Object[] args,final Page pageInfo) throws DataAccessException {
		assertNotNull(sql,"query sql");
        if(pageInfo.needTotal()){
            pageInfo.setTotalCount(getTotalCount(sql,args));
        }else if(pageInfo.getPageSize() == Integer.MAX_VALUE){
            pageInfo.setPageSize(pageInfo.getPageSize() -1);
        }
		String wrapperSql = PageUtil.gernatePageSql(sql,pageInfo);
		return new PageList<Map<String,Object>>(query(wrapperSql, args),pageInfo);
	}
	
	/**
	 * 自定义TspMapper 查询，且可进行分页
	 * 参数占位符使用 ":Name" 形式
	 * 
	 * @param sql 要执行的sql
	 * @param args sql 中的参数名值对
	 * @param rowMapper 自己类型转换器
	 * @return
	 * @throws DataAccessException
	 */
	protected <T> PageList<T> queryPageByNamedSql(String sql,Map<String,Object> args, RowMapper<T> rowMapper,Page pageInfo) throws DataAccessException {
		assertNotNull(sql,"query sql");
        if(pageInfo.needTotal()) {
            pageInfo.setTotalCount(getTotalCountByNamedParams(sql,args));
        }else if(pageInfo.getPageSize() == Integer.MAX_VALUE){
            pageInfo.setPageSize(pageInfo.getPageSize() -1);
        }
		String wrapperSql = PageUtil.gernatePageSql(mappingSql(sql), pageInfo);
		logger.debug("sql:{},{}" , wrapperSql,argsToString(args));
		return new PageList<T>(getNamedParameterJdbcTemplate().query(wrapperSql, args, rowMapper),pageInfo);
	}
	
	
	/**
	 * map 查询，且可进行分页
	 * 参数占位符使用 ":Name" 形式
	 * 
	 * @param sql 要执行的sql
	 * @param args sql 中的参数名值对
	 * @param rowMapper 自己类型转换器
	 * @return 分页的map
	 * @throws DataAccessException
	 */
	protected PageList<Map<String,Object>> queryPageByNamedSql(String sql,Map<String,Object> args, Page pageInfo) throws DataAccessException {
		assertNotNull(sql,"query sql");
		if(pageInfo.needTotal()) {
	             pageInfo.setTotalCount(getTotalCountByNamedParams(sql,args));
	    }else if(pageInfo.getPageSize() == Integer.MAX_VALUE){
	             pageInfo.setPageSize(pageInfo.getPageSize() -1);
	    }
		String wrapperSql = PageUtil.gernatePageSql(mappingSql(sql), pageInfo);
		logger.debug("sql:" + wrapperSql+","+ argsToString(args));
		return new PageList<Map<String,Object>>(queryByNamed(wrapperSql, args),pageInfo);
	}
	
	/**
	 * 自定义更新语句更新
	 * 参数占位符为"?"
	 * 
	 * @param sql
	 * @param args
	 */
	protected Integer update(String sql,Object...args){
		logger.debug("sql:" + sql+","+ argsToString(args));
		return getJdbcTemplate().update(mappingSql(sql),args);
	}
	
	protected void assertNotNull(Object e,String name) {
		if (e == null || StringUtils.isEmpty(e.toString())) {
			throw new NullPointerException(name + " cann't be null or empty");
		}
	}
	
	/**
	 * 自定义更新语句更新
	 * 参数占位符为"?"
	 * 
	 * @param sql
	 * @param args
	 */
	protected Integer updateWithNamedSql(String namedSql,Map<String,Object>args){
		logger.debug("sql:" + namedSql+","+ argsToString(args));
		return getNamedParameterJdbcTemplate().update(mappingSql(namedSql),args);
	}
	
	protected int getTotalCountByNamedParams(String sql,Map<String,Object> args){
		String countSql = "select count(0) from (" + sql + ") as total";
		return countByNamedSql(countSql, args);
	}
	
	private int getTotalCount(String sql,Object... args){
		String countSql = "select count(0) from (" + sql + ") as total";
		return count(countSql, args);
	}
	

	private String argsToString(Object[] args){
		if(args == null){
			return "";
		}
		
		StringBuilder s = new StringBuilder("args:[");
		for(Object o : args){
			s.append(o).append(",");
		}
		s.append("]");
		
		return s.toString();
	}
	
	private String argsToString(Map<String,Object> args){
		if(args == null){
			return "";
		}
		
		StringBuilder s = new StringBuilder("args:[");
		for(String key : args.keySet()){
			s.append(key).append(":").append(args.get(key)).append(",");
		}
		s.append("]");
		
		return s.toString();
	}
	
	protected String mappingSql(String oldsql){
			return oldsql;
	}
}
