package com.tmser.core.tag;


import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import com.tmser.core.page.PageList;



public class PageTag extends TagSupport {
  /**
	 * 
	 */
  private static final long serialVersionUID = 1L;
  private String url; //跳转的url;
  private String name;
  private String style;

  /**
   * 构造函数
   */
  public PageTag() {
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

  public void setStyle(String style) {
    this.style = style;
  }

  public String getStyle() {
    return this.style;
  }


  public int doEndTag() throws JspException {
    PageList pageList = (PageList)pageContext.getRequest().getAttribute(name);
    if(pageList == null){
    	return EVAL_PAGE;
    }
    try {
      pageContext.getOut().write(getTagString(pageList, style));
    } catch (IOException ioexception) {
      throw new JspException("IO Error: " + ioexception.getMessage());
    }
    return EVAL_PAGE;
  }

  /**
   * 根据样式,获取分页标签字符串
   * @param pageList Page
   * @param style String
   * @return String
   */
  public String getTagString(PageList pageList, String style) {
    if ("0".equals(style)) {
    	return getTagString0(pageList);//下拉方式
    }
    else if("1".equals(style)) {
    	return getTagString1(pageList);//文本方式
    }
    else if("2".equals(style)) {
        return getTagString2(pageList);//页码方式
    }
    else if("3".equals(style)) {
    	return getTagString3(pageList);
    }
    else if("4".equals(style)) {
    	return getTagString4(pageList);
    }
    else{
    	return getTagString2(pageList);//页码方式
    }
  }

  
  
  /**
   * 下拉方式的标签
   * @param pageList Page
   * @return String
   */
  public String getTagString0(PageList pageList) {
    StringBuilder bf = new StringBuilder();

    //共1条记录,上为第0到1条
    bf.append("<TABLE width=\"100%\" border=0  align=\"center\"><TR>");

    //首页、上一页、下一页、末页
    bf.append("<td><font color='black' style='float:right;'>");
    if (pageList.isHasPreviousPage()) {
      bf.append("<A  class='black' href=\"javascript:turnPage('");
      bf.append(url);
      bf.append("','1')\">首页</A>");

      bf.append("&nbsp;&nbsp;<A  class='black' href=\"javascript:turnPage('");
      bf.append(url);
      bf.append("','");
      bf.append(pageList.getPreviousPage());
      bf.append("')\">上一页</A>");
    }else{
    	 bf.append("<A  class='black' href=\"javascript:alert('已到首页');\">首页</A>");
         bf.append("&nbsp;&nbsp;<A  class='black' href=\"javascript:alert('已到首页');\">上一页</A>");
    }
    if (pageList.isHasCyNextPage()) {
      bf.append("&nbsp;&nbsp;<A  class='black' href=\"javascript:turnPage('");
      bf.append(url);
      bf.append("','");
      bf.append(pageList.getNextPage());
      bf.append("')\">下一页</A>");
    }else{
    	  bf.append("&nbsp;&nbsp;<A  class='black' href=\"javascript:alert('已到尾页');\">下一页</A>");
    }
    bf.append("</font></td>");
    return bf.toString();
  }
  public String getPageOption(PageList pageList ){
      String sData = "";
      if(pageList.getTotalCount() > 0)
      {
          for(int i = 1; i <= pageList.getTotalPages(); i++)
              if(i == pageList.getCurrentPage())
                  sData = (new StringBuilder(String.valueOf(sData)))
                  .append("<option value='").append(pageList.getStartCount(i))
                  .append("' selected>\u7B2C").append(i).append("\u9875</option>").toString();
              else
                  sData = (new StringBuilder(String.valueOf(sData))).append("<option value='")
                  .append(pageList.getStartCount(i)).append("'>\u7B2C").append(i).append("\u9875</option>").toString();

      }
      return sData;
  }

    
  
  /**
   * 文本方式的标签
   * @param pageList Page
   * @return String
   */
  public String getTagString1(PageList pageList) {
    StringBuilder bf = new StringBuilder();

    //共1条记录,上为第0到1条
    bf.append("<TABLE width=\"100%\" border=0  align=\"center\"><TR><TD align=left>&nbsp;&nbsp;共<FONT color=red>");
    bf.append(pageList.getTotalCount());
//    bf.append("</FONT>条记录,上为第<FONT color=red>");
//    bf.append(pageList.getViewStartOfCurPage());
//    bf.append("</FONT>到<FONT color=red>");
//    bf.append(pageList.getEndOfCurPage());
//    bf.append("</FONT>条</TD>");
    bf.append("</FONT>条记录</TD>");

    //首页、上一页、下一页、末页
    bf.append("<td>");
    if (pageList.isHasPreviousPage()) {
      bf.append("<A  class='black' href=\"javascript:turnPage('");
      bf.append(url);
      bf.append("','1')\">首页</A>");

      bf.append("&nbsp;&nbsp;<A  class='black' href=\"javascript:turnPage('");
      bf.append(url);
      bf.append("','");
      bf.append(pageList.getPreviousPage());
      bf.append("')\">上一页</A>");
    }
    if (pageList.isHasNextPage()) {
      bf.append("&nbsp;&nbsp;<A  class='black' href=\"javascript:turnPage('");
      bf.append(url);
      bf.append("','");
      bf.append(pageList.getNextPage());
      bf.append("')\">下一页</A>");

      bf.append("&nbsp;&nbsp;<A  class='black' href=\"javascript:turnPage('");
      bf.append(url);
      bf.append("','");
      bf.append(pageList.getTotalPages());
      bf.append("')\">末页</A>");
    }
    bf.append("</td>");

    //转到第几页
    if(pageList.getTotalCount()>0){
      bf.append("<TD align=\"right\" width=\"220\">");
      bf.append(getPageText(pageList));
      bf.append("&nbsp;<input type=\"button\" class='page_go' name=\"gopagebutton\" value='goto' onclick=\"gotoPageTxt('" +
                url + "')\">");
      bf.append("<td>");
    }
    bf.append("</TR></TABLE>");
    return bf.toString();
  }
  public String getPageText(PageList pageList) {
      String sData = "";
      if(pageList.getTotalCount() > 0)
          sData = (new StringBuilder("\u7B2C<font color='red'>"))
          .append(pageList.getCurrentPage())
          .append("</font>\u9875 \u5171<font color='red'>")
          .append(pageList.getTotalPages())
          .append("</font>\u9875 \u8F6C\u5230 <input type='text' size='3' maxlength='5' id='go_page' class='page_input' name='gopage' value=''><input type='hidden' id='page_size' name='page_size' value='")
          .append(pageList.getPageSize()).append("'><input type='hidden' id='total_pages' name='totalPages' value='")
          .append(pageList.getTotalPages()).append("'>").toString();
      return sData;
  }
  
  
  
  /**
   * 页号方式的标签
   * @param pageList Page
   * @return String
   */
  public String getTagString2(PageList pageList) {
    StringBuilder bf = new StringBuilder();
    if(pageList.getTotalPages()>1)
    {
    int pages = 1;//默认显示第一页
    pages=pageList.getCurrentPage();//获取当前页码
    int pagescount =pageList.getTotalPages(); //获取总页数
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
    bf.append("<ul class=\"page\">");
    if (pages > 1) {
    	bf.append("<li onmouseout=\"this.className=''\" onmouseover=\"this.className='hover'\">");
      	 bf.append("<A  class='black' href=\"javascript:turnPage('");
        bf.append(url);
        bf.append("&startcount=");
        bf.append( pageList.getPageSize()*(pageList.getCurrentPage()-2));
        bf.append("')\">");
        bf.append("上一页</A></li>");
    }//>显示上一页
    //<显示分页码
    for (int i = listbegin; i < listend; i++) {
        if (i != pages) {//如果i不等于当前页
          
            		 bf.append("<li onmouseout=\"this.className=''\" onmouseover=\"this.className='hover'\" >");	 
                     bf.append("<A  class='black' href=\"javascript:turnPage('");
                     bf.append(url);
                     bf.append("&startcount=");
                     bf.append( pageList.getPageSize()*(i-1));
                     bf.append("')\">");
                     bf.append(i);
                     bf.append("</A></li>");
                   
        } else {
        	bf.append("<li class=\"current\" style=\"cursor:default;\">");
  		   bf.append(i);
  		   bf.append("</li>");
        }
    }//显示分页码>
    //<显示下一页
    if (pages != pagescount) {
    	bf.append("<li onmouseout=\"this.className=''\" onmouseover=\"this.className='hover'\">");
   	    bf.append("<A  class='black' href=\"javascript:turnPage('");
        bf.append(url);
        bf.append("&startcount=");
        bf.append( pageList.getPageSize()*(pages));
        bf.append("')\">");
        bf.append("下一页</A></li>");
    }//>显示下一页
    bf.append("</ul>");
    }
    return bf.toString();
  }

  
  
  /**
   * 页号方式的标签
   * @param pageList Page
   * @return String
   */
  public String getTagString3(PageList pageList) {
    StringBuilder bf = new StringBuilder();
    if(pageList.getTotalPages()>1)
    {
	    int pages = 1;//默认显示第一页
	    pages=pageList.getCurrentPage();//获取当前页码
	    int pagescount =pageList.getTotalPages(); //获取总页数
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
	    bf.append("<div class=\"page_box\">");//modified by wczhang
	    bf.append("<div class=\"right\">");
	    bf.append("<ul class=\"page\">");
	    if (pages > 1) {
	    	bf.append("<li onmouseout=\"this.className=''\" onmouseover=\"this.className='hover'\">");
	      	 bf.append("<A  class='black' href=\"javascript:turnPage('");
	        bf.append(url);
	        bf.append("&startcount=");
	        bf.append( pageList.getPageSize()*(pageList.getCurrentPage()-2));
	        bf.append("')\">");
	        bf.append("上一页</A></li>");
	    }//>显示上一页
	    //<显示分页码
	    for (int i = listbegin; i < listend; i++) {
	        if (i != pages) {//如果i不等于当前页
	          
	            		 bf.append("<li onmouseout=\"this.className=''\" onmouseover=\"this.className='hover'\" >");	 
	                     bf.append("<A  class='black' href=\"javascript:turnPage('");
	                     bf.append(url);
	                     bf.append("&startcount=");
	                     bf.append( pageList.getPageSize()*(i-1));
	                     bf.append("')\">");
	                     bf.append(i);
	                     bf.append("</A></li>");
	                   
	        } else {
	        	bf.append("<li class=\"current\" style=\"cursor:default;\">");
	  		   bf.append(i);
	  		   bf.append("</li>");
	        }
	    }//显示分页码>
	    //<显示下一页
	    if (pages != pagescount) {
	    	bf.append("<li onmouseout=\"this.className=''\" onmouseover=\"this.className='hover'\">");
	   	    bf.append("<A  class='black' href=\"javascript:turnPage('");
	        bf.append(url);
	        bf.append("&startcount=");
	        bf.append( pageList.getPageSize()*(pages));
	        bf.append("')\">");
	        bf.append("下一页</A></li>");
	    }//>显示下一页
	    bf.append("</ul>");
	    bf.append("</div>");
	    bf.append("</div>");
    }
    return bf.toString();
  }

  
  
  /**
   * 页号方式的标签
   * @param pageList Page
   * @return String
   */
  public String getTagString4(PageList pageList) {
    StringBuilder bf = new StringBuilder();
    if(pageList.getTotalPages()>1){
	    int pages = 1;//默认显示第一页
	    pages=pageList.getCurrentPage();//获取当前页码
	    int pagescount =pageList.getTotalPages(); //获取总页数
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
	    	bf.append("<li class=\"pre\">");
	      	bf.append("<A href=\"javascript:turnPage('");
	        bf.append(url);
	        bf.append("','");
	        bf.append(pageList.getPreviousPage());
	        bf.append("')\">");
	        bf.append("上一页</A></li>");
	    }//显示上一页>
	    //<显示分页码
	    for (int i = listbegin; i < listend; i++) {
	        if (i != pages) {//如果i不等于当前页	          
        		 bf.append("<li>");	 
                 bf.append("<A href=\"javascript:turnPage('");
                 bf.append(url);
                 bf.append("','");
                 bf.append(i);
                 bf.append("')\">");
                 bf.append(i);
                 bf.append("</A></li>");
	        } 
	        else {
	        	bf.append("<li><span>");
	  		   	bf.append(i);
	  		   	bf.append("</span></li>");
	        }
	    }//显示分页码>
	    //<显示下一页
	    if (pages != pagescount) {
	    	bf.append("<li class=\"next\">");
	   	    bf.append("<A href=\"javascript:turnPage('");
	        bf.append(url);
	        bf.append("','");
	        bf.append(pageList.getNextPage());
	        bf.append("')\">");
	        bf.append("下一页</A></li>");
	    }//显示下一页>
	    bf.append("<div class=\"clear\"/>");
	    bf.append("</ul>");
    }
    return bf.toString();
  }
  
  
  
  public void release() {
	  super.release();
	  name = null;
	  url = null;
	  style = "2";
  }

}
