package com.tmser.core.tag;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;
import org.apache.struts2.views.jsp.ContextBeanTag;

import com.opensymphony.xwork2.util.ValueStack;


/**
 * 导航标签
 * @author tjx
 */
public class NavTag extends ContextBeanTag{
	
	protected static final long serialVersionUID = -9104260518603844927L;
	
	/**
	 * 要显示的导航 id
	 * 导航配置在config/nav/下相关配置文件中
	 */
	protected String id; 
	/**
	 *  导航包装div标签样式
	 */
	protected String className = "page_links";
	
	/**
	 * 导航元素分隔符
	 */
	protected String delimiter = "&nbsp;>&nbsp;"; 
	
	/**
	 * 要隐藏导航元素索引列表。以 ","分隔，自0开始。不符合要求的输入将被忽略
	 */
	protected String hidden; 
    
	public String getHidden() {
		return hidden;
	}

	public void setHidden(String hidden) {
		this.hidden = hidden;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setDelimiter(String delimiter) {
		 if(delimiter != null && !"".equals(delimiter.trim()))
				 this.delimiter = delimiter;
	}

	public void setClassName(String className) {
		this.className = className;
	}

    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new NavBean(stack);
    }

    protected void populateParams() {
        super.populateParams();

        NavBean text = (NavBean) component;
        text.setId(id);
        text.setHidden(hidden);
        text.setDelimiter(delimiter);
        text.setClassName(className);
    }
    
    /**
     * Release any acquired resources.
     */
    public void release() {
	super.release();
	delimiter = null;
	className = null;
	id = null;
	hidden = null;
    }
}
