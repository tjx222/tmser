package com.tmser.core.nav;

import java.io.Serializable;
/**
 * 导航元素
 *
 * @author tjx
 * @version 2.0
 * 2014-1-7
 */
public class NavElem implements Serializable{
	private static final long serialVersionUID = -6188382819938616131L;
	
	/**
	 * 子元素名称，与key 属性必填一项
	 * 作为导航子元素时，支持动态参数
	 */
	private String name;
	/**
	 * 元素链接
	 */
	private String href;
	
	/**
	 * 目标窗口
	 */
	private String target;
	
	/**
	 * 元素的国际化配置中的key,与bundle 配合使用
	 */
	private String key;

	/**
	 * 作为模块，或分组元素是否选择状态，默认false
	 */
	private Boolean chose;
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getHref() {
		return href;
	}
	public void setHref(String href) {
		this.href = href;
	}
	public String getTarget() {
		return target;
	}
	public void setTarget(String target) {
		this.target = target;
	}

	public Boolean getChose() {
		return chose;
	}
	public void setChose(Boolean chose) {
		this.chose = chose;
	}
	
}