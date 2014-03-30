package com.tmser.core.tag;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;
import org.apache.struts2.views.jsp.ComponentTagSupport;

import com.opensymphony.xwork2.util.ValueStack;
public class PageTag extends ComponentTagSupport {
  /**
	 * 
	 */
  private static final long serialVersionUID = 1L;
  private String url; //跳转的url;
  private String name;
  private int style = 4;
  private String ajax = "false"; //是否使用ajax 提交
  private String callback; //ajax 提交回调

  public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
      return new PageBean(stack);
  }

  protected void populateParams() {
      super.populateParams();
      PageBean tag = (PageBean) component;
      tag.setUrl(url);
      tag.setName(name);
      tag.setStyle(style);
      tag.setAjax("true".equalsIgnoreCase(ajax));
      tag.setCallback(callback);
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return this.name;
  }

  public void setUrl(String s) {
    this.url = s;
  }

  public String getUrl() {
    return this.url;
  }

  public void setStyle(int style) {
    this.style = style;
  }

  public int getStyle() {
    return this.style;
  }

 public void setAjax(String isAjax) {
		this.ajax = isAjax;
 }

public void setCallback(String callback) {
	this.callback = callback;
}

}
