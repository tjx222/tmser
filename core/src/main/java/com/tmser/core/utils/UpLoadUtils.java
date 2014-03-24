package com.tmser.core.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tmser.core.constants.GlobalConfig;
import com.tmser.core.constants.GlobalConfig.Basepath;


/**
 * 文件上传辅助类，只支持本地文件系统的上传保存
 * @author tjx
 *
 */
public abstract class UpLoadUtils{
	private static Logger logger = LoggerFactory.getLogger(UpLoadUtils.class);
	public static final int SUCCESS = 0;
	public static final int FAILED = 1;
	public static final int TOOBIG = 2;
	
	/**
	 * 自定大小文件上传。
	 * 存储到用户选择的路径下，目前有两种方式可供选择，参考@see Basepath
	 * @param maxsize 要上传文件的最大容量
	 * @param file FormFile 要上传的file
	 * @param relativePath 文件存储的相对路径
	 * @param filename 重命名过的文件名称
	 * @param pathType @see Basepath 存储位置类型
	 * @return 0 成功， 1 失败,2 文件太大，使用自定义大小
	 */
	public static int fileUploadWithNewName(int maxsize, File file, String relativePath, String newname,
			String oldname,Basepath pathType){
		if(file == null || newname == null){
			return FAILED;
		}
		if(file.length() > (maxsize * 1024*1024)){
			return TOOBIG;
		}
		String rootpath =  GlobalConfig.getRootPath(pathType);
		if(rootpath == null || "".equals(rootpath)){
			logger.error("Root Path is empty!");
			return FAILED;
		}
		InputStream is = null;
	    File folder = new File(rootpath, relativePath);
	    File f = new File(folder,newname);
		try {
			is = new FileInputStream(file);
		} catch (Exception e) {
			return FAILED;
		}
		
	    return fileUpload(is,f);
	}
	
	/**
	 * 文件上传，大小不能超过sys.maxUploadSize 指定的大小
	 * 存储到用户选择的路径下，目前有两种方式可供选择，参考@see Basepath
	 * @param fiel FormFile 要上传的file
	 * @param relativePath 文件存储的相对路径
	 * @param newName 重命名过的文件名称
	 * @param pathType @see Basepath 存储位置类型
	 * @return 0 成功， 1 失败,2 文件太大，使用系统默认大小
	 */
	public static int fileUploadWithNewName(File file,String relativePath, String newName,Basepath pathType) 
	{
			if(file == null || newName == null){
				return FAILED;
			}
			if(overflow(file.length())){
				return TOOBIG;
			}
			InputStream is = null;
		    String rootpath = GlobalConfig.getRootPath(pathType);
			if(rootpath == null || "".equals(rootpath)){
				logger.error("Root Path is empty!");
				return FAILED;
			}
			
		    File folder = new File(rootpath, relativePath);
		    File f = new File(folder,newName);
			try {
				is = new FileInputStream(file);
			} catch (Exception e) {
				return FAILED;
			}
			
		    return fileUpload(is,f);
	}
	
	/**
	 * 
	 * @param fiel FormFile 要上传的file
	 * @param relativePath 文件存储的相对路径
	 * @param filename 重命名过的文件名称
	 * @return 0 成功， 1 失败,2 文件太大，使用系统默认大小
	 */
	public static int fileUploadWithNewName(File file,String relativePath, String newName) 
	{
		return fileUploadWithNewName(file,relativePath,newName,Basepath.NORMAL_FILE);
	}
	
	/**
	 * 使用随机文件名
	 * @param file FormFile 要上传的file
	 * @param relativePath 文件存储的相对路径
	 * @param oldname 上传文件的文件名，包含后缀。
	 * @param type 存储位置类型。@see GlobalConfig.Basepath
	 * @return 成功 返回文件保存后相对路径， 失败 返回null
	 */
	public static String fileUpload(File file,String relativePath,String oldname,Basepath type) 
	{
			if(file == null){
				return null;
			}
			
			if(overflow(file.length())){
				throw new FileTooBigException("the upload file is to big!");
			}
			
			String filepath = null;
			InputStream is = null;
			String filename = Identities.uuid2()+"."+FileUtils.getFileExt(oldname);
		    String rootpath =  GlobalConfig.getRootPath(type);
			if(rootpath == null || "".equals(rootpath)){
				logger.error("Root Path is empty!");
				return null;
			}
			
		    File folder = new File(rootpath, relativePath);
		    File f = new File(folder,filename);
			try {
				is = new FileInputStream(file);
			} catch (Exception e) {
				return null;
			}
			
			if(fileUpload(is,f) == 0){
				filepath = relativePath + File.separatorChar +filename;
			}
			
		    return filepath;
	}

	/**
	 * 直接保存文件流到指定文件，无大小限制
	 * @param is 要保存的文件流
	 * @param output 存储后文件
	 * @return 0 成功， 1 失败
	 */
	public static int fileUpload(InputStream is, File output)
	{
		if(is == null || output == null){
			return FAILED;
		}
		
		int result = SUCCESS;
		OutputStream bos = null;
		try {
		        File folder = output.getParentFile();
		        
		        //检查文件夹是否存在,如果不存在,新建一个
		        if (!folder.exists()) {
		          FileUtils.createDirectory(folder.getPath());
		        }
		        
		        bos = new BufferedOutputStream(new FileOutputStream(output));
		        int bytesRead = 0;
		        byte[] buffer = new byte[8192];
		        while ((bytesRead = is.read(buffer, 0, 8192)) != -1) {
		          bos.write(buffer, 0, bytesRead); //将文件写入服务器
		        }
		    } catch (IOException e) {
				result = FAILED;	
			}finally{
		    	if(bos != null)
					try {
						bos.close();
					} catch (IOException e) {
					}
					if(is != null){
			    		try {
							is.close();
						} catch (IOException e) {
						}
			    	}
		    	
		    }
		return result;
	}
	
	private static boolean overflow(long size){
		return size > GlobalConfig.MAX_FILE_SIZE * 1024*1024;
	}
}
