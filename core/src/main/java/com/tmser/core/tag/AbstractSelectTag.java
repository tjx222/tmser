package com.tmser.core.tag;

import java.io.IOException;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tmser.core.utils.StringUtils;

/**
 * 下拉框标签抽象类
 * @author 张凯
 * @date 2014-1-14
 *
 */
public abstract class AbstractSelectTag extends TagSupport{

	private static final long serialVersionUID = 1L;

	protected static final Logger log = LoggerFactory.getLogger(CacheSelectTag.class);
	
	/**
	 * id
	 */
	protected String id;
	
	/**
	 * name
	 */
	protected String name;
	
	/**
	 * css
	 */
	protected String cssClass;
	
	/**
	 * style
	 */
	protected String style;
	
	/**
	 * 默认选中值
	 */
	protected String selected;
	
	/**
	 * 多项选择
	 * 可选值multiple，默认不开启
	 */
	protected String multiple;
	
	/**
	 * 多项选择个数
	 * 配合multiple使用
	 */
	protected String size;
	
	/**
	 * 是否生成请选择一项
	 * 可选值true\false，默认为false
	 */
	protected String empty;
	
	/**
	 * 是否置灰
	 * 可选值disabled，默认不置灰
	 */
	protected String disabled;
	
	/**
	 * onchange事件
	 */
	protected String onchange;
	
	@Override
	public int doStartTag() {
		StringBuffer selectBuffer = new StringBuffer();
		//起始标签
		selectBuffer.append("<select ");
		if (StringUtils.isNotBlank(name)) {
			selectBuffer.append("name=\"" + name + "\" ");
		}
		if (StringUtils.isNotBlank(id)) {
			selectBuffer.append("id=\"" + id + "\" ");
		}
		if (StringUtils.isNotBlank(multiple)) {
			selectBuffer.append("multiple=\"" + multiple + "\" ");
		}
		if(StringUtils.isNotBlank(size)){
			selectBuffer.append("size=\"" + size + "\" ");
		}
		if (StringUtils.isNotBlank(disabled)) {
			selectBuffer.append("disabled=\"" + disabled + "\" ");
		}
		if (StringUtils.isNotBlank(cssClass)) {
			selectBuffer.append("class=\"" + cssClass + "\" ");
		}
		if (StringUtils.isNotBlank(style)) {
			selectBuffer.append("style=\"" + style + "\" ");
		}
		if (StringUtils.isNotBlank(onchange)) {
			selectBuffer.append("onchange=\"" + onchange + "\" ");
		}
		selectBuffer.append(">");
		
		if("true".equals(empty)) {
			selectBuffer.append("<option value=\"\">请选择</option>");
		}
		
		//填充select的option。
		fillOptions(selectBuffer);
		
		//结束标签
		selectBuffer.append("</select>");
		JspWriter out = pageContext.getOut();
		try {
			out.print(selectBuffer);
		} catch (IOException e) {
			log.warn("<select>标签生成发生错误。", e);
		}
		
		//跳过了开始和结束标签之间的代码
		return SKIP_BODY;
		
	}
	
	/**
	 * 填充选项
	 */
	abstract protected void fillOptions(StringBuffer selectBuffer);

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCssClass() {
		return cssClass;
	}

	public void setCssClass(String cssClass) {
		this.cssClass = cssClass;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public String getSelected() {
		return selected;
	}

	public void setSelected(String selected) {
		this.selected = selected;
	}

	public String getMultiple() {
		return multiple;
	}

	public void setMultiple(String multiple) {
		this.multiple = multiple;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getEmpty() {
		return empty;
	}

	public void setEmpty(String empty) {
		this.empty = empty;
	}

	public String getDisabled() {
		return disabled;
	}

	public void setDisabled(String disabled) {
		this.disabled = disabled;
	}

	public String getOnchange() {
		return onchange;
	}

	public void setOnchange(String onchange) {
		this.onchange = onchange;
	}
}
