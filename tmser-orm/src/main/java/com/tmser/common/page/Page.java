package com.tmser.common.page;

import java.io.Serializable;


/**
 * 分页参数类
 * @author tjx
 * 2014-01-11
 */
public  class  Page  implements Serializable{
	
	private static final long serialVersionUID = 4167285075144313208L;
	
	private boolean isCustomPageSize = false;
	private int pageSize = 10;
    private int currentPage =1;
    private boolean needTotal = true; //是否查询总记录数
    private int totalCount;
    
    public Page(){
    	
    }
   
   /**
    * @param needTotal 是否需要查询记录总数
    */
   public Page(boolean needTotal){
    	this.needTotal = needTotal;
   }
    /**
     * 
     * @param pageSize 每页记录数，不可以小于1, 默认为10
     */
    public Page(int pageSize){
    	setPageSize(pageSize);
    }
    
	public boolean customPageSize() {
		return isCustomPageSize;
	}

	/**
     * 
     * @param pageSize 每页记录数，不可以小于1, 默认为10
     * @param needTotal 是否查询总记录数
     */
    public Page(int pageSize, boolean needTotal){
    	this.needTotal = needTotal;
    	setPageSize(pageSize);
    }
    /**
     * 
     * @param pageSize 每页记录数，不可以小于1, 默认为10
     * @param currentPage 当前所在页，索引从1开始，不可以小于1,默认为1
     */
    public Page(int pageSize,int currentPage){
    	reset(pageSize,currentPage);
    }
    
    /**
     * 
     * @param pageSize 每页记录数，不可以小于1, 默认为10
     * @param currentPage 当前所在页，索引从1开始，不可以小于1,默认为1
     * @param needTotal 是否查询总记录数
     */
    public Page(int pageSize,int currentPage,boolean needTotal){
    	this.needTotal = needTotal;
    	reset(pageSize,currentPage);
    }

    public void reset(int pageSize,int currentPage){
    	if(needTotal && (pageSize < 1 || currentPage < 1)){
    		throw new IllegalArgumentException("pagesize and currentPage can't little than 1.");
    	}
    	this.pageSize = pageSize;
    	this.currentPage = currentPage;
    }

	public void setPageSize(int pageSize) {
		if(needTotal && pageSize < 1){
    		throw new IllegalArgumentException("pagesize can't little than 1.");
    	}
    	this.pageSize = pageSize;
    	this.isCustomPageSize = true;
	}

	public void setCurrentPage(int currentPage) {
		if(currentPage < 1){
    		throw new IllegalArgumentException("currentPage can't little than 1.");
    	}
		this.currentPage = currentPage;
	}

	/**
	 * 获取当前页，从第一页开始
	 * @return 当前页
	 */
    public int getCurrentPage(){
		return currentPage;
	}
	
	/**
	 * 页大小，即每页包含记录数
	 * @return
	 */
	public int getPageSize(){
		return pageSize;
	}
	
	/**
	 * 设置总页数
	 * @param total
	 */
	public void setTotalCount(int total){
		this.totalCount = total;
	}
	
	public int getTotalCount(){
		return totalCount;
	}

	public void setNeedTotal(boolean needTotal) {
		this.needTotal = needTotal;
	}
	
	public boolean needTotal() {
		return needTotal;
	}

	public String toString(){
		return new StringBuilder("[pageSize:").append(pageSize).append(",currentPage:")
				.append(currentPage).append("]").toString();
	}
	
}
