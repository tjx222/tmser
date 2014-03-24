package com.tmser.core.nav;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
/**
 * 导航条
 *
 * @author tjx
 * @version 2.0
 * 2014-1-7
 */
public class Nav implements Serializable{
	private static final long serialVersionUID = 1707013880934068493L;
	
	public static final String RA_NAVMAP = "ra_navmap";
	public static final String NAV_ROOT = "ra-navs";
	
	private List<NavElem> elems;
	private String extend;
	private String needback;
	private String id;
	
	public Nav(){
		this.elems = new ArrayList<NavElem>();
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public List<NavElem> getElems() {
		return elems;
	}
	public void addElem(NavElem elem) {
		this.elems.add(elem);
	}
	public String getExtend() {
		return extend;
	}
	public void setExtend(String extend) {
		this.extend = extend;
	}
	public String getNeedback() {
		return needback;
	}
	public void setNeedback(String needback) {
		this.needback = needback;
	}
	
	
}
