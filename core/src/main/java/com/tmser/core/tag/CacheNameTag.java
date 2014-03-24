package com.tmser.core.tag;

import java.io.IOException;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tmser.core.utils.CacheLoader;
import com.tmser.core.utils.StringUtils;

/**
 * 翻译器自定义标签
 * @author 张凯
 * @date 2014-1-13
 *
 */
public class CacheNameTag extends TagSupport{
	
	private static final Logger log = LoggerFactory.getLogger(CacheNameTag.class);

	private static final long serialVersionUID = 1L;
	
	/**
	 * 需要翻译的id
	 */
	private String key;
	
	/**
	 * 缓存类型
	 */
	private String cacheType;
	
	/**
	 * 是否取缓存
	 * 可选值true\false，默认为true
	 */
	private String cache;
	
	public int doStartTag() {
		String name = "";
		try {
			name = (String)CacheLoader.getCache(cacheType, key, Boolean.valueOf("false".equals(cache)?cache:"true"));
		} catch (Exception e) {
			log.warn("取缓存[" + cacheType + "," + key + "]异常", e);
		}
		if(StringUtils.isBlank(name)){
			name = "";
		}
		
		JspWriter out = pageContext.getOut();
		try {
			out.print(name);
		} catch (IOException e) {
			log.warn("<name>标签生成发生错误。", e);
		}
		//跳过了开始和结束标签之间的代码
		return SKIP_BODY;
		
	}

	public String getCacheType() {
		return cacheType;
	}

	public void setCacheType(String cacheType) {
		this.cacheType = cacheType;
	}

	public String getCache() {
		return cache;
	}

	public void setCache(String cache) {
		this.cache = cache;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
}
