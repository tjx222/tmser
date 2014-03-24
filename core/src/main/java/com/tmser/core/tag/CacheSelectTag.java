package com.tmser.core.tag;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import javax.annotation.Resource;

import com.tmser.core.utils.CacheLoader;

/**
 * 下拉框自定义标签
 * @author 张凯
 * @date 2014-1-13
 *
 */
public class CacheSelectTag extends AbstractSelectTag{
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * 是否取缓存
	 * 可选值true\false，默认为true
	 */
	private String isCache;
	
	/**
	 * 缓存类型
	 */
	private String cacheType;
	
	/**
	 * 填充选项
	 */
	@SuppressWarnings("unchecked")
	protected void fillOptions(StringBuffer selectBuffer) {
		Map<String, String> optionMap = Collections.EMPTY_MAP;
		try {
			//获取缓存map
			optionMap = (Map<String, String>)CacheLoader.getCache(cacheType, Boolean.valueOf("false".equals(isCache)?isCache:"true"));

		} catch (Exception e) {
			log.warn("取缓存[" + cacheType + "]异常", e);
		}
		if (optionMap != null && !optionMap.isEmpty()) {
			for (Iterator<String> iter = optionMap.keySet().iterator(); iter.hasNext();) {
				Object code = iter.next();//编码
				Object name = optionMap.get(code);//值
				String value = name.toString();
				String checked = "";
				if (selected != null && selected.equals(code)) {
					checked = "selected=\"true\"";
				}
				selectBuffer.append("<option value=\"" + code + "\" " + checked
						+ " title=\"" + value + "\">" + value + "</option>");
			}
		}
	}

	/***********************Getter Setter***************************/
	public String getIsCache() {
		return isCache;
	}

	public void setIsCache(String isCache) {
		this.isCache = isCache;
	}

	public String getCacheType() {
		return cacheType;
	}

	public void setCacheType(String cacheType) {
		this.cacheType = cacheType;
	}
}
