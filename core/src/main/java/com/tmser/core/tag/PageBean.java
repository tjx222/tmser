package com.tmser.core.tag;

import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.List;

import org.apache.struts2.components.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opensymphony.xwork2.util.ValueStack;
import com.tmser.core.orm.page.PageList;

/**
 * 
 * 导航标签实现类
 * @author tjx
 * @version 2.0
 * 2014-1-9
 */
public class PageBean extends Component{
	private static final Logger LOG = LoggerFactory.getLogger(PageBean.class);
	protected List<String> values = Collections.emptyList();
	private String url; //跳转的url;
	private String name;
	private int style = 4;
	private boolean isAjax; //是否使用ajax 提交
	private String callback; //ajax 提交回调
	
	
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

	  public boolean isAjax() {
		return isAjax;
	}

	public void setAjax(boolean isAjax) {
		this.isAjax = isAjax;
	}

	public String getCallback() {
		return callback;
	}

	public void setCallback(String callback) {
		this.callback = callback;
	}
	
	public PageBean(ValueStack stack) {
			super(stack);
	}

    public boolean start(Writer writer) {
    	boolean result = super.start(writer);
    	PageList<?> pageList = null;

        if (name == null) {
        	name = "top";
        }
        else {
        	name = stripExpressionIfAltSyntax(name);
        }

        pageList = (PageList<?>) getStack().findValue(name, PageList.class, throwExceptionOnELFailure);

        try {
            if (pageList != null) {
                writer.write(getTagString(pageList, style));
            }
        } catch (IOException e) {
            if (LOG.isInfoEnabled()) {
        	LOG.info("Could not find value pagelist", e);
            }
        }
        return result;
    }
    
    /**
     * 根据样式,获取分页标签字符串
     * @param pageList Page
     * @param style String
     * @return String
     */
    public String getTagString(PageList<?> pageList, int style) {
  	  String content = null;
  	  switch(style){
  	  	case 0: content = getTagString0(pageList); break;
  	  	case 1: 
  	  	case 2: 
  	  	case 3: 
  	  	case 4: content = getTagString4(pageList); break;
  	  	default: content = getTagString4(pageList); break;
  	  }
  	  return content;
    }

    
    /**
     * 下拉方式的标签
     * @param pageList Page
     * @return String
     */
    public String getTagString0(PageList<?> pageList) {
      StringBuilder bf = new StringBuilder();

      //共1条记录,上为第0到1条
      bf.append("<TABLE width=\"100%\" border=0 style='float:right;' align=\"center\"><TR>");
      //首页、上一页、下一页、末页
      bf.append("<td><font color='black' style='float:right;'>");
      if (pageList.hasPreviousPage()) {
        bf.append("<span class='black' style='cursor: pointer;' onclick=\"javascript:")
        .append(isAjax?"turnPageAjax":"turnPage")
        .append("(this,'").append(url).append("','1'")
        .append(isAjax?","+callback:"")
        .append(")\">首页</span>")
        .append("&nbsp;&nbsp;<span class='black' style='cursor: pointer;' onclick=\"javascript:")
        .append(isAjax?"turnPageAjax":"turnPage")
        .append("(this,'").append(url).append("',").append(pageList.previousPage())
        .append(isAjax?","+callback:"").append(")\">上一页</span>");
      }else{
//      	 bf.append("<span class='black' >首页</span>");
      }
      if (pageList.hasNextPage()) {
        bf.append("&nbsp;&nbsp;<span class='black'  style='cursor: pointer;' onclick=\"javascript:")
        .append(isAjax?"turnPageAjax":"turnPage")
        .append("(this,'").append(url).append("',").append(pageList.nextPage())
        .append(isAjax?","+callback:"").append(")\">下一页</span>");
      }
      bf.append("</font></td>");
      return bf.toString();
    }
    
    /**
     * 页号方式的标签
     * @param pageList Page
     * @return String
     */
    public String getTagString4(PageList<?> pageList) {
      StringBuilder bf = new StringBuilder();
      if(pageList.getTotalPages()>1){
  	    int pages = 1;//默认显示第一页
  	    pages = pageList.getCurrentPage();//获取当前页码
  	    int pagescount = pageList.getTotalPages(); //获取总页数
  	    if (pagescount < pages) {
  	        pages = pagescount;//如果分页变量大总页数，则将分页变量设计为总页数
  	    }
  	    if (pages < 1) {
  	        pages = 1;//如果分页变量小于１,则将分页变量设为１
  	    }
  	    int listbegin = (pages - 2);//从第几页开始显示分页信息
  	    if (listbegin < 1) {
  	        listbegin = 1;
  	    }
  	    int listend = pages + 3;//分页信息显示到第几页
  	    if (listend > pagescount) {
  	        listend = pagescount + 1;
  	    }
  	    bf.append("<ul>");
  	    //<显示上一页
  	    if (pages > 1) {
  	    	bf.append("<li class=\"pre\">")
  	    	.append("<span  style='cursor: pointer;' onclick=\"javascript:")
  	      	.append(isAjax?"turnPageAjax":"turnPage")
  	      	.append("(this,'").append(url).append("',").append(pageList.previousPage())
  	        .append(isAjax?","+callback:"").append(")\">").append("上一页</span></li>");
  	    }//显示上一页>
  	    //<显示分页码
  	    for (int i = listbegin; i < listend; i++) {
  	        if (i != pages) {//如果i不等于当前页	          
          		 bf.append("<li>");	 
                   bf.append("<span style='cursor: pointer;' onclick=\"javascript:")
                   .append(isAjax?"turnPageAjax":"turnPage")
                   .append("(this,'")
                   .append(url).append("',")
                   .append(i).append(isAjax?","+callback :"")
                   .append(")\">").append(i).append("</span></li>");
  	        } 
  	        else {
  	        	bf.append("<li class=\"active\"><span>");
  	  		   	bf.append(i);
  	  		   	bf.append("</span></li>");
  	        }
  	    }//显示分页码>
  	    //<显示下一页
  	    if (pages != pagescount) {
  	    	bf.append("<li class=\"next\">")
  	   	      .append("<span  style='cursor: pointer;' onclick=\"javascript:")
  	   	      .append(isAjax?"turnPageAjax":"turnPage")
  	   	      .append("(this,'").append(url).append("',").append(pageList.nextPage())
  	          .append(isAjax?","+callback:"").append(")\">")
  	          .append("下一页</span></li>");
  	    }//显示下一页>
  	    bf.append("</ul><div class=\"clear\"/>");
      }
      return bf.toString();
    }
    
	
}
