package com.tmser.core.action;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;

import javax.servlet.http.Cookie;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.tmser.core.bo.Message;
import com.tmser.core.listener.LogListener;
import com.tmser.core.listener.MessageListener;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

/**
 * Action 基类
 * @author tjx
 * 2013-12-30
 * @version 2.0
 */
public class BaseAction extends ActionSupport {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired(required = false)
	private LogListener logListener;
	
	@Autowired(required = false)
	private List<MessageListener> messageListeners;
	
	
	public LogListener getLogListener() {
		return logListener;
	}

	public void setLogListener(LogListener logListener) {
		this.logListener = logListener;
	}

	public List<MessageListener> getMessageListeners() {
		return messageListeners;
	}

	public void setMessageListeners(List<MessageListener> messageListeners) {
		this.messageListeners = messageListeners;
	}
	
	public void addLog(String loginfo) {
		
		if(logListener != null){
			logListener.addLog(loginfo);
		}
		
	}

	public void sendMessage(Message msg) {
		if(messageListeners != null){
			for(MessageListener ml : messageListeners){
				ml.sendMessage(msg);
			}
		}
	}

	/**
	 * 设置request
	 * @param key
	 * @param value
	 */
	protected void putToRequest(String key,Object value){
		ActionContext.getContext().put(key, value);
	}
	
	/**
	 * 设置session
	 * @param key
	 * @param value
	 */
	protected void putToSession(String key,Object value){
		ActionContext.getContext().getSession().put(key, value);
	}
	
	/**
	 * 设置cookie 
	 * @param name cookie 名称
	 * @param value cookie 值
	 * @param sec cookie 生命
	 * @param domain 
	 * @throws UnsupportedEncodingException
	 */
	protected void addCookie(String name,String value,int sec,String domain) throws UnsupportedEncodingException{
		value = URLEncoder.encode(value,"UTF-8");
		Cookie cookie = new Cookie(name, value);
		cookie.setMaxAge(sec);
		cookie.setPath(domain);
		ServletActionContext.getResponse().addCookie(cookie);
    }
	
	/**
	 * 设置cookie,保存7天
	 * @param name
	 * @param value
	 * @throws UnsupportedEncodingException
	 */
	protected void addCookie(String name,String value) throws UnsupportedEncodingException{
		addCookie(name,value,60*60*24*7,"/");
    }
	
	/**
	 * 得到指定的cookie 值
	 * @param cookieName
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	protected String getCookie(String cookieName) throws UnsupportedEncodingException { 
		Cookie[] cookies = ServletActionContext.getRequest().getCookies();  
		String value = ""; 
		if (cookies != null) {  
		       for (Cookie cookie : cookies) {
		            if (cookieName.equals(cookie.getName())) {  //获取具体的cookie；
		                 value = URLDecoder.decode(cookie.getValue(),"UTF-8");
		                 return value;  
		                }  
		            }  
		        }  
		 return null;  
	}
		 
	/**
	 * 删除指定的cookie  
	 * @param cookieName
	 * @return
	 */
    protected Cookie delCookie(String cookieName) {
	        Cookie[] cookies = ServletActionContext.getRequest().getCookies();  
		    if (cookies != null) {  
		            for (Cookie cookie : cookies) {  
		                if (cookieName.equals(cookie.getName())) {  
		                    cookie.setValue("");  
		                    cookie.setMaxAge(0);  
		                    cookie.setPath("/");  //添加，清除时需要加路径才能清除掉；
		                    ServletActionContext.getResponse().addCookie(cookie); 
		                }  
		            }  
		        }  
		        return null;  
	}
	
    /**
     * 根据key获取session 值
     * @param key
     * @return
     */
	protected Object getFromSession(String key){
		return ActionContext.getContext().getSession().get(key);
	}
	
    /**
     * 设置application
     * @param key
     * @param value
     * @return
     */
	protected void putToAppliaction(String key,Object value){
		ActionContext.getContext().getApplication().put(key, value);
	}
	
	/**
	 * 根据key获取application 值
	 * @param key
	 * @return
	 */
	protected Object getFromAppliaction(String key){
		return ActionContext.getContext().getApplication().get(key);
	}
	
	/**
	 * 获取Url 参数
	 * @param key
	 * @return
	 */
	protected String getParam(String key){
		String value = null;
		Object vs = ActionContext.getContext().getParameters().get(key);
		if(vs != null && vs instanceof String[]){
			String[] varr = (String[]) vs;
			if( varr.length > 0){
				value = varr[0];
			}
		}
		return value;
	}
	
	/**
	 * 获取Url 参数数组
	 * @param key
	 * @return
	 */
	protected String[] getParams(String key){
		String[] varr = null;
		Object vs = ActionContext.getContext().getParameters().get(key);
		if(vs != null && vs instanceof String[]){
			varr = (String[]) vs;
		}
		return varr;
	}

}
