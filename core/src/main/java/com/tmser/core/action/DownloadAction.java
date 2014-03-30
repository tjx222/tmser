package com.tmser.core.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionContext;
import com.tmser.core.config.GlobalConfig;
import com.tmser.core.config.GlobalConfig.Basepath;
import com.tmser.core.utils.Encodes;

/**
*
* 通用下载
* @author tjx
* @version 2.0
* 2014-2-13
*/
public class DownloadAction extends BaseAction{
	 /**
	 * 
	 */
	private static final long serialVersionUID = -3036837032581084659L;
	/**
	 * 下载显示的文件名
	 */
	 private String filename;

	 /**
	  * 下载文件相对路径
	  */
	 private String filepath;
	 
    public void setFilename(String filename) {
            this.filename = filename;
    }
    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }
    public String getFilename() throws UnsupportedEncodingException{
   	 ServletActionContext.getResponse().setHeader("charset","charset=ISO8859-1");
   	 HttpServletRequest request = ServletActionContext.getRequest();  
        String Agent = request.getHeader("User-Agent");  
        if (null != Agent) {  
            Agent = Agent.toLowerCase();  
            if (Agent.indexOf("msie") != -1) { 
                filename = java.net.URLEncoder.encode(filename,"UTF-8");  
            }else{
           	 this.filename = new String(this.filename.getBytes(),Charset.forName("ISO8859-1"));
            }
        }else{
       	 this.filename = new String(this.filename.getBytes(),Charset.forName("ISO8859-1"));
        }
        return this.filename;
    }
    public InputStream getInputStream() throws FileNotFoundException {
		try {
			return new FileInputStream(new File(GlobalConfig.getRootPath(Basepath.NORMAL_FILE),filepath));
		} catch (Exception e) {
			throw new FileNotFoundException("下载文件不存在 :["+ filepath+"]");
		}
    }
    
    public String execute() throws FileNotFoundException,IllegalAccessException{
   	filepath = (String)getFromRequest("filepath");
   	filename = (String)getFromRequest("filename");
   	if(filepath == null){
   		throw new FileNotFoundException("下载文件不存在 :["+ filepath+"]");
   	}
   	String md5 = Encodes.md5(filepath);
		String path = (String) ActionContext.getContext().getSession()
							.get(md5);
		if(path == null || !filepath.equals(path)){
				throw new IllegalAccessException("您没有权限下载该文件！");
		}
		ActionContext.getContext().getSession().put(md5,null);
      return SUCCESS;
}
}