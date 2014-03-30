package com.tmser.core.action;

import java.io.ByteArrayInputStream;

import org.apache.commons.lang3.StringUtils;

import com.opensymphony.xwork2.ActionContext;
import com.tmser.core.config.Constants;
import com.tmser.core.utils.SecurityCode;
import com.tmser.core.utils.SecurityImage;

/**
 * 验证码控制层
 * @author 张凯 TJX
 * @date 2014-2-8
 */
public class RandomCodeAction extends BaseAction{

	private static final long serialVersionUID = 4824426686138283669L;
	
	private String codeSessionKey;
	
	 //图片流
	 private ByteArrayInputStream imageStream;
	 
	 public ByteArrayInputStream getImageStream() {
	        return imageStream;
	 }
	
	 public void setImageStream(ByteArrayInputStream imageStream) {
	        this.imageStream = imageStream;
	 }
	 
	@Override
	public String execute() throws Exception {
		   //获取默认难度和长度的验证码
		   String securityCode = SecurityCode.getSecurityCode();
		   imageStream = SecurityImage.getImageAsInputStream(securityCode);
		   String sessionKey = codeSessionKey;
		   if(StringUtils.isEmpty(sessionKey)){
			   sessionKey = Constants.DEFAULT_SECURITY_CODE;
		   }
		   //放入session中
		   ActionContext.getContext().getSession()
		   				.put(sessionKey, securityCode);
		  return SUCCESS;
	}

	public void setCodeSessionKey(String codeSessionKey) {
		this.codeSessionKey = codeSessionKey;
	}
	
}

