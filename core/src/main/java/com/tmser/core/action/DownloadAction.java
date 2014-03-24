package com.tmser.core.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import com.tmser.core.constants.GlobalConfig;
import com.tmser.core.constants.GlobalConfig.Basepath;
import com.tmser.core.utils.Encodes;
import com.opensymphony.xwork2.ActionContext;

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
     public String getFilename(){
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
