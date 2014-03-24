package com.tmser.core.constants;

import java.io.File;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

import com.tmser.core.utils.PropertiesLoader;

/**
 * 全局配置类,读取系统核心配置
 * @author tjx
 * @version 2014-02-10
 */
public class GlobalConfig {
	private static Properties SYS_CONFIG;
	public static final String DEFAULT_PROPERTIES = "classpath:config/properties/config-core.properties";
	
	public final static String SYS_MAX_UPLOAD_SIZE = "sys.maxUploadSize";
	public final static String SYS_NORMAL_FILE = "sys.normalfile";
	public final static String SYS_NET_FILE = "sys.netfile";
	public final static String SYS_DOWNLOAD_RS = "sys.downloadrs";
	
	/**
	 * 默认上传文件目录
	 */
	public static final String DEFAULT_PATH = "upload";
	
	/**
	 * 默认上传文件大小限制
	 */
	public static final int MAX_FILE_SIZE;
	
	static{
		loadProperties();
		int sizeLimit = 10;
		try {
			sizeLimit = Integer.parseInt(getConfig(SYS_MAX_UPLOAD_SIZE));
		} catch (NumberFormatException e) {
		}
		MAX_FILE_SIZE = sizeLimit;
	}
	
	private synchronized static void loadProperties(){
		if(SYS_CONFIG != null ) 
			return;
  	    SYS_CONFIG = new PropertiesLoader(DEFAULT_PROPERTIES).getProperties();
	}
	
	public static String getConfig(String key){
		return getConfig(key,null);
	}
	
	
	public static String getConfig(String key,String def){
		if(SYS_CONFIG == null){
			loadProperties();
		}
		return StringUtils.isBlank(SYS_CONFIG.getProperty(key)) ?  def : SYS_CONFIG.getProperty(key);
	}
	/**
	 * 上传类型获取相应的跟目录
	 * @param type
	 * @return
	 */
	public static String getRootPath(Basepath type){
		String basePath = null;
		switch(type){
			case NORMAL_FILE:basePath = getConfig(SYS_NORMAL_FILE);break;
			case NET_FILE:basePath = getConfig(SYS_NET_FILE);break;
			default:break;
		}
		
		if(basePath ==  null  || "".equals(basePath.trim()) 
				|| !(new File(basePath).isAbsolute())){
			String root = System.getProperty("qxptV2.root");
			if(root != null  &&  !"".equals(root))
				basePath = new File(root,DEFAULT_PATH).getAbsolutePath();
		}
		
		if(basePath ==  null  || "".equals(basePath.trim())){
			String root = new File(ClassLoader.getSystemResource("").getFile())
								.getParentFile().getParent();
			if(root != null  &&  !"".equals(root))
				basePath = new File(root,DEFAULT_PATH).getAbsolutePath();
		}
		
		return basePath;
	}
	
	public static String getDownloadRs(){
		return getConfig(SYS_DOWNLOAD_RS,ConsForSystem.DEFUALT_DOWNLOAD_RS);
	}
	
	/**
	 * 普通文件 NORMAL_FILE，默认
	 * 头像文件 PHOTO_FILE
	 */
	public static enum Basepath{
		NORMAL_FILE , NET_FILE
	}
}
