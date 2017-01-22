package com.tmser.common.orm;

import java.util.Map;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.parser.NameReplacement;
import net.sf.jsqlparser.schema.NameAdapter;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.replace.Replace;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.update.Update;
import net.sf.jsqlparser.util.deparser.StatementDeParser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import com.tmser.common.orm.parse.OrmNameReplacement;
import com.tmser.common.orm.parse.TablesNameMapFinder;
import com.tmser.utils.StringUtils;


/**
 * sql 解析工具
 * 
 * @author jxtan
 * @date 2014年10月23日
 */
public class DefaultSqlMapping implements SqlMapping,InitializingBean{
	
	private static final Logger logger = LoggerFactory.getLogger(DefaultSqlMapping.class);
	
	private NameReplacement nameReplacement = new OrmNameReplacement();

	public void setNameReplacement(NameReplacement nameReplacement) {
		this.nameReplacement = nameReplacement;
	}

	/**
	 * 解析sql 工具
	 * @param sql
	 * @return
	 */
	public String mapping(String sql){
		if(StringUtils.isBlank(sql)){
			return sql;
		}
		
		logger.debug("need parse sql is [{}]",sql);
		Statement statement;
		try {
			statement = CCJSqlParserUtil.parse(sql);
		} catch (JSQLParserException e) {
			logger.error("parse ["+sql+"] failed",e);
			return sql;
		}
		
		try{
			findTables(statement);
			//Start of value modification
			StringBuilder buffer = new StringBuilder();
			statement.accept(new StatementDeParser(buffer));
			
			logger.debug("sql parse result is [{}]",buffer);
			return buffer.toString();
		}finally{
			TableMapHolder.clear();
		}

		
	}
	
	private void findTables(Statement statement){
		Map<String,String> tablemap = null;
		TablesNameMapFinder tablesNameMapFinder = new TablesNameMapFinder();
		if(statement instanceof Select){
			tablemap = tablesNameMapFinder.getTableList((Select)statement);
		}else if(statement instanceof Replace){
			tablemap = tablesNameMapFinder.getTableList((Replace)statement);
		}else if(statement instanceof Update){
			tablemap = tablesNameMapFinder.getTableList((Update)statement);
		}else if(statement instanceof Delete){
			tablemap = tablesNameMapFinder.getTableList((Delete)statement);
		}else if(statement instanceof Insert){
			tablemap = tablesNameMapFinder.getTableList((Insert)statement);
		}
		
		if(tablemap != null){
			TableMapHolder.setTableMap(tablemap);
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		NameAdapter.setNameReplacement(nameReplacement);
	}
	
}
