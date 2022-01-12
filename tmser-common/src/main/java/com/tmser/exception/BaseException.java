package com.tmser.exception;

import com.tmser.CommonError;
import com.tmser.IError;
import com.tmser.util.MessageUtils;
import lombok.ToString;

/**
 * <pre>
 *	基础异常，异常信息存储配置文件validateMessage
 * </pre>
 *
 * @author tmser
 * @version $Id: BaseException.java, v 1.0 2015年1月23日 下午7:21:54 tmser Exp $
 */
@ToString
public class BaseException extends RuntimeException {

	private static final long serialVersionUID = -8945722267535215558L;

	//所属模块
    private String module;

    /**
     * 错误码
     */
    private IError error;

    /**
     * 错误码对应的参数
     */
    private Object[] args;

    /**
     * 错误消息
     */
    private String errorMessage;


    public BaseException(String module, IError error, Object[] args, String errorMessage) {
        this.module = module;
        this.error = error == null ? CommonError.UNKNOWN : error;
        this.args = args;
        this.errorMessage = errorMessage;
        if (errorMessage == null) {
            this.errorMessage = MessageUtils.message(this.error.getErrorKey(), this.error.getMessage(), args);
        }
    }

    public BaseException(String module, IError code, Object[] args) {
        this(module, code, args, null);
    }

    public BaseException(String module, String defaultMessage) {
        this(module, null, null, defaultMessage);
    }

    public BaseException(IError code, Object[] args) {
        this(null, code, args, null);
    }

    public BaseException(IError code) {
        this(null, code, null, null);
    }

    public BaseException(String defaultMessage) {
        this(null, null, null, defaultMessage);
    }

    @Override
    public String getMessage() {
        return errorMessage;

    }

    public String getModule() {
        return module;
    }

    public Integer getErrorCode() {
        return error != null ? error.getCode() : CommonError.UNKNOWN.getCode();
    }

    public IError getError() {
        return error;
    }

    public Object[] getArgs() {
        return args;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

}