package com.tmser.common.bo;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Transient;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.tmser.common.page.Page;


@SuppressWarnings("serial")
public abstract class QueryObject implements Serializable,PageAble,OrderAble{
	
	public static enum JOINTYPE { INNER,LEFT,RIGHT,FULL;}

	/**
	 * join
	 */
	@Transient
	private StringBuilder join;
	
	
	/**
	 * 别名
	 */
	@Transient
	private String alias;
	
	
	/**
	 * 扩展属性
	 */
	@Transient
	private String flags;
	
	/**
	 * 扩展属性
	 */
	@Transient
	private String flago;

	/**
	 * 分页参数
	 */
	@Transient
	private Page page = new Page();
	
	/**
	 * 自定义排序
	 */
	@Transient 
	private String tspOrder;
	
	
	/**
	 * 自定义查询字段
	 */
	@Transient 
	private String customCulomn;
	
	/**
	 * 是否允许转换page to json
	 */
	@Transient 
	private Boolean needParseToJson = false;
	
	/**
	 * 自定义查询条件
	 * 更新时若使用了customCondition ，则不更据id 更新
	 * sql语句格式是 named 参数化形式
	 * 不用传递复杂的条件，特别是非标准sql 支持的语句
	 */
	@Transient 
	private NamedConditon customCondition;
	
	/**
     * 自定义查询字段
     */
    @Transient 
    private String group;
    
    public void parsePage(boolean needParseToJson){
    	this.needParseToJson = needParseToJson;
    }

	public void currentPage(int currentPage) {
		this.needParseToJson = true;
		page.setCurrentPage(currentPage);
	}

	/**
	 * 设置每页显示结果数，优先使用用户设置的结果条数
	 * @param pageSize
	 */
	public void pageSize(int pageSize) {
		this.needParseToJson = true;
		if(!page.customPageSize() || page.getPageSize() < 1 
				|| page.getPageSize() > 100){
			page.setPageSize(pageSize);
		}
	}

	@Override
	public boolean needParseToJson(){
		return needParseToJson;
	}
	
	public QueryObject addJoin(JOINTYPE join,String tablename){
		this.join = new StringBuilder(" ").append(join.name()).append(" join ").append(tablename);
		return this;
	}
	
	public String join(){
		return this.join != null ? this.join.toString() : "";
	}
	
	/**
	 * join 时 on 的条件，必须参数化输入，参数设置到
	 * @param onConditions
	 */
	public void on(String onConditions){
		if(this.join == null){
			throw new NullPointerException("must be set addJoin first!");
		}
		this.join.append(" on ").append(onConditions);
	}
	
	/**
	 * 分页参数
	 */
	@Override
	public Page getPage() {
		return page;
	}

	public void addPage(Page page) {
		this.needParseToJson = true;
		this.page = page;
	}

	
	public String customCulomn() {
		return customCulomn;
	}

	public void addCustomCulomn(String customCulomn) {
		this.customCulomn = customCulomn;
	}
	
	public NamedConditon customCondition() {
		return customCondition;
	}

	public void addCustomCondition(String customCondition,Map<String, Object> paramMap) {
		this.customCondition = new NamedConditon(customCondition,paramMap);
	}
	
	/**
	 * 无参数条件查询
	 * @param customCondition
	 */
	public void addCustomCondition(String customCondition) {
		this.customCondition = new NamedConditon(customCondition,null);
	}
	
	
	public NamedConditon buildCondition(String customCondition) {
		this.customCondition = new NamedConditon(customCondition,new HashMap<String, Object>());
		return this.customCondition;
	}
	
	/**
	 * 自定义排序
	 */
	@Override
	public String order() {
		return tspOrder;
	}

	/**
	 * 自定义排序
	 */
	public void addOrder(String order) {
		this.tspOrder = order;
	}
	
	/**
	 * 自定义排序
	 */
	public void setOrder(String order) {
		this.tspOrder = order;
	}
	
	   /**
     * 自定义排序
     */
    public String group() {
        return group;
    }

    /**
     * 自定义排序
     */
    public void addGroup(String group) {
        this.group = group;
    }
	
    public String alias(){
    	if(this.alias == null){
    		return "";
    	}
    	return this.alias;
    }
    
    public void addAlias(String alias){
    	this.alias = alias;
    }

	/**
	 *
	 */
	public String getFlags() {
		return flags;
	}

	/**
	 * 扩展属性s
	 */
	public void setFlags(String flags) {
		this.flags = flags;
	}

	/**
	 * 扩展属性o
	 */
	public String getFlago() {
		return flago;
	}

	/**
	 * 扩展属性o
	 */
	public void setFlago(String flago) {
		this.flago = flago;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
	
	@Override
	public abstract boolean equals(Object obj);

	@Override
	public abstract int hashCode();
	
	public static class NamedConditon{
		private final String conditon;
		private final Map<String, Object> paramMap;
		public NamedConditon(String conditon,Map<String, Object> paramMap)
		{
			this.conditon = conditon;
			if(paramMap == null)
				this.paramMap = Collections.emptyMap();
			else
				this.paramMap = paramMap;
		}
		public String getConditon() {
			return conditon;
		}
		public Map<String, Object> getParamMap() {
			return paramMap;
		}
		public NamedConditon put(String name,Object value){
			this.paramMap.put(name, value);
			return this;
		}
		
	}
}
