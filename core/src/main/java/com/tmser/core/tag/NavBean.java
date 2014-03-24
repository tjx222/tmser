package com.tmser.core.tag;

import java.io.IOException;
import java.io.Writer;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.components.ContextBean;
import org.apache.struts2.components.Param;
import org.apache.struts2.util.TextProviderHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tmser.core.nav.Nav;
import com.tmser.core.nav.NavElem;
import com.tmser.core.nav.NavHolder;
import com.opensymphony.xwork2.util.ValueStack;

/**
 * 
 * 导航标签实现类
 * @author tjx
 * @version 2.0
 * 2014-1-9
 */
public class NavBean extends ContextBean implements Param.UnnamedParametric{
	private static final Logger LOG = LoggerFactory.getLogger(NavBean.class);
	protected List<String> values = Collections.emptyList();
	protected static HashMap<String,MessageFormat> formats = new HashMap<String,MessageFormat>();
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
	
	protected String actualName;
	
	public NavBean(ValueStack stack) {
			super(stack);
	}
	public void setId(String id) {
		this.id = id;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	public void setHidden(String hidden) {
		this.hidden = hidden;
	}
	
    public void addParameter(String key, Object value) {
        addParameter(value);
    }
    
	@Override
	public void addParameter(Object value) {
	        if (values.isEmpty()) {
	            values = new ArrayList<String>(4);
	        }

	     values.add(value.toString());
	}
	
	private String getMsg(String name,String body){
		    actualName = findString(name, "name", "You must specify the i18n key. Example: welcome.header");
	        String defaultMessage;
	        if (StringUtils.isNotEmpty(body)) {
	            defaultMessage = body;
	        } else {
	            defaultMessage = actualName;
	        }

	       return TextProviderHelper.getText(actualName, defaultMessage, getStack());
	}
	
    public boolean end(Writer writer, String body) {
        if(!"".equals(id)){
        	List<NavElem> navList = getNavElems();
    		if(navList.size()>0){
    			StringBuilder bf = new StringBuilder();
    		    int index = 0;
    		    bf.append("<div id=\"ra-navs\" ");
    		    if(className != null && !"".equals(className.trim())){
    		    	 bf.append("class=\"").append(className).append("\"");
    		     }
    		    bf.append(">");
    			List<Integer>  hiddenNavs = parseDisplay(navList.size());
    			int count = navList.size()-hiddenNavs.size();
    			int navIndex = 0;
    		    for(NavElem navElem : navList)
    		    {
    		    	 if(hiddenNavs.size()>0){//不显示hidden 中列出的导航栏目
    		    		 if(hiddenNavs.contains(index)){
    		    			 index ++;
    		    			 continue;
    		    		 }
    		    	 }
    		    	 navIndex++;
    		    	 index ++;
    		         if(navElem.getHref() != null && !"".equals(navElem.getHref()))
    		         {
    		    	     bf.append("<a target=\"").append(navElem.getTarget()).append("\" href=\"")
    		    	     .append(formatMsg(StringEscapeUtils.unescapeHtml4(navElem.getHref()),values));
    		    	   	 bf.append("\" id=\"ra-navs-nav").append(index).append("\">")
    		    	   	 .append(getNavElemName(navElem,body)) .append("</a>");
    		         }else{
    		        	 bf.append(getNavElemName(navElem,body)); 
    		    	 }
    		         
    		         if(navIndex  != count){
    		        	 bf.append(delimiter);
    		         }
    		        
    		    }
    		    bf.append("</div>");
    		    try {
					writer.write(bf.toString());
				} catch (IOException e) {
					 LOG.error("Could not write out Nav tag", e);
				}
    		}
		}
        return super.end(writer, "");
    }
	/*
	 * 解析要显示导航列表
	 */
	protected List<Integer> parseDisplay(int max){
		List<Integer> rs = Collections.emptyList();
		String displayIndexs = hidden;
		if(displayIndexs != null){
			rs = new ArrayList<Integer>();
			 while (displayIndexs.length() > 0) {
		            String index = null;
		            int comma = displayIndexs.indexOf(',');
		            if (comma >= 0) {
		                index = displayIndexs.substring(0, comma).trim();
		                displayIndexs = displayIndexs.substring(comma + 1);
		            } else {
		            	index = displayIndexs.trim();
		            	displayIndexs = "";
		            }
		            try {
						int i = Integer.valueOf(index);
						if(i < max && i >= 0){
							rs.add(i);
						}
					} catch (NumberFormatException e) {
						// do nothing
					}
		            if (displayIndexs.length() < 1) {
		                break;
		            }
		        }
		}
		
		return rs;
	}
	
	protected List<NavElem> getNavElems(){
	    Map<String,Nav> mp = NavHolder.getAllNavs();
	    
	    List<NavElem> navList = Collections.emptyList();
	    Nav nav = mp.get(id);
	    
	    if(nav != null){
	    	if(nav.getExtend() != null){
	    		navList  = mergeNavElems(mp,Collections.unmodifiableList(nav.getElems()),nav);
	    	}else{
	    		navList = Collections.unmodifiableList(nav.getElems());
	    	}
	    }
	    
	    return navList;
	}
	
	/**
	 * 合并父类导航
	 * @param mp 所有导航几乎
	 * @param nav 要合并的导航
	 * @return
	 */
	protected List<NavElem> mergeNavElems(Map<String,Nav> mp,List<NavElem> childList,Nav nav){
	    
		List<NavElem> navList = Collections.emptyList();
		if(nav == null){
			return navList;
		}
		
		Nav parent = mp.get(nav.getExtend());
		if(parent !=  null){
			navList = copyNavElemList(parent);
	    	if(navList != null && nav.getNeedback() != null 
	    			&& "true".equals(nav.getNeedback())
	    			&& navList.size()>0){//删除最后一级条件，nav needback属性为true,且父级导航元素个数多于一个
	    		navList.remove(navList.size()-1);
	    	}
			navList.addAll(childList);
			
			if(parent.getExtend() != null){//递归添加父级导航
				navList = mergeNavElems(mp,navList,parent);
			}
		}else{
			return childList;
		}
		
	    return navList;
	}
	
	protected List<NavElem> copyNavElemList(Nav nav){
		List<NavElem> rsList = Collections.emptyList();
		List<NavElem>  navElemList = Collections.unmodifiableList(nav.getElems());
		if(navElemList != null 	&& navElemList.size()>0){
			rsList = new ArrayList<NavElem>(navElemList.size());
			for(NavElem ne : navElemList){
				rsList.add(ne);
			}
		}
		return rsList;
		
	}
	
	protected String getNavElemName(NavElem e,String body){
		String name = "";
		if(e.getKey()!= null){
				name = getMsg(e.getKey(), body);
		}else if(e.getName() != null){
			name= formatMsg(e.getName(),values);
		}
		
		return StringEscapeUtils.escapeHtml4(name);
	}
	
	
	protected String formatMsg(final String msg,List<String> args){
		String rs = "";
		MessageFormat format = null;
		if(msg!=null && !"".equals(msg.trim())){
			synchronized (formats) {
			    format = (MessageFormat) formats.get(msg);
				if (format == null) {
				    	format = new MessageFormat(msg);
				    	formats.put(msg, format);
				}
			    Object[] argsArray = ((args != null) ? args.toArray() : null);
				rs = format.format(argsArray);
			}
		}
		return rs;
	}
}
