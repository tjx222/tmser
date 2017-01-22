package com.tmser.common.page;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.Transient;

public class PageList<T> implements Serializable {
	private static final long serialVersionUID = -6868950745522147470L;
	private List<T> datalist;
	private Page page;
	private int startRecode;
	private boolean hasNext;

	/**
	 * 构造分页对象
	 * 
	 * @param datalist
	 *            当前页数据列表
	 * @param page
	 *            分页信息 起始记录数，每页记录数，总记录数使用默认值
	 */
	public PageList(List<T> datalist, Page page) {
		if (page == null)
			throw new IllegalArgumentException("Page parameter can't be null");
		this.page = page;
		if(datalist == null){
			datalist = new ArrayList<T>();
		}
		removeTail(datalist);
		this.datalist = datalist;
		this.startRecode = (getCurrentPage() - 1) * getPageSize();
	}
	
	public Page getPage(){
		return page;
	}

	public List<T> getDatalist() {
		if (datalist == null) {
			datalist = Collections.emptyList();
		}
		return datalist;
	}

	protected void removeTail(List<T> datalist){
		int size = datalist.size();
		int pageSize = page.getPageSize();
		if(size > pageSize )
			hasNext = true;
		else
			hasNext = false;
		
		while(size > pageSize){
			datalist.remove(size -1);
			size = datalist.size();
		}
	}
	public void setDatalist(List<T> datalist) {
		if (datalist == null) {
			datalist = new ArrayList<T>();
		}
		removeTail(datalist);
		this.datalist = datalist;
	}

	/**
	 * 当前页记录数
	 * 
	 * @return
	 */
	public int currentPageCount() {
		return datalist.size();
	}

	/**
	 * 是否有下一页
	 * 
	 * @return
	 */
	
	public boolean hasNextPage() {
		return hasNext || currentPageCount() + (page.getCurrentPage() - 1)
				* page.getPageSize() < page.getTotalCount();
	}

	/**
	 * 是否有前一页，因startOfCurPage 本页起始记录，只要其大于零说明本页大于pageSize，则上页存在
	 * 
	 * @return
	 */
	public boolean hasPreviousPage() {
		return getCurrentPage() > 1;
	}

	/**
	 * 获取当前页
	 * 
	 * @return
	 */
	public int getCurrentPage() {
		return page.getCurrentPage();
	}

	/**
	 * 获取下页页数
	 * 
	 * @return
	 */
	public int nextPage() {
		return getCurrentPage() + 1;
	}

	/**
	 * 获取上一页
	 * 
	 * @return
	 */
	public int previousPage() {
		if (getCurrentPage() == 1) {
			return 1;
		}
		return getCurrentPage() - 1;
	}

	/**
	 * 本页第一条记录序号
	 * 
	 * @return
	 */
	public int startRecode() {
		if (getTotalCount() == 0) {
			return 0;
		}

		return startRecode + 1;
	}

	/**
	 * 本页末条记录数
	 * 
	 * @return
	 */
	public int endOfCurPage() {
		return startRecode + currentPageCount();
	}

	/**
	 * 下一页的起始记录数
	 * 
	 * @return
	 */
	public int startOfNextPage() {
		return startRecode + getPageSize();
	}

	/**
	 * 前一页起始记录
	 * 
	 * @return
	 */
	public int startOfPreviousPage() {
		return Math.max(startRecode - getPageSize(), 0);
	}

	/**
	 * 最后一页起始记录
	 * 
	 * @return
	 */
	public int startOfLastPage() {
		if (getTotalCount() % getPageSize() == 0) {
			return getTotalCount() - getPageSize();
		}

		return getTotalCount() - getTotalCount() % getPageSize();
	}

	/**
	 * 总记录数
	 * 
	 * @return
	 */
	@Transient
	public int getTotalCount() {
		return page.getTotalCount();
	}

	/**
	 * 总页数
	 * 
	 * @return
	 */
	@Transient
	public int getTotalPages() {
		int total = getTotalCount();
		int pageSize = getPageSize();
		return total % pageSize == 0 ? total / pageSize : total / pageSize + 1;
	}

	/**
	 * 每页记录数
	 * @return
	 */
	@Transient
	public int getPageSize() {
		return page.getPageSize();
	}

	/**
	 * 给定页数之前的总记录数 如：page =1 ,pagesize =10 则返回0，因为只有一页
	 * 
	 * @param page
	 * @return
	 */
	public int startCount(int page) {
		return (page - 1) > 0 ? (page - 1) * getPageSize() : 0;
	}
	
}
