package com.tmser.model.response;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.tmser.CommonError;
import com.tmser.IError;
import com.tmser.exception.BaseException;
import lombok.Data;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Data
public class ResponseData<T> implements Serializable {
    private static final String CONTENT = "content";
    private static final String TOTAL_ROWS = "total";
    private static final NullObject DEFAULT_OBJECT_RETUN = new NullObject();

    public static class NullObject implements Serializable {
    }


    private String code;
    private String message;
    protected T data;

    private ResponseData() {
    }

    public static ResponseData error(Object object, String message, String errorCode) {
        ResponseData result = new ResponseData();
        result.data = object != null ? object : DEFAULT_OBJECT_RETUN;
        result.message = Strings.isNullOrEmpty(message) ? CommonError.FAILED.getMessage() : message;
        result.code = errorCode == null ? CommonError.FAILED.getCode() : errorCode;
        return result;
    }

    public static ResponseData error(Object object, String message) {
        return error(object, message, null);
    }

    /**
     * eg.
     * <p>
     * <pre>
     *      {
     *          "ret" : false,
     *          "msg" : "错误的id，修改失败",
     *          "errcode" : 1
     *      }
     * </pre>
     *
     * @param message
     * @param errCode
     * @return
     */
    public static ResponseData error(String message, String errCode) {
        return error(null, message, errCode);
    }

    /**
     * eg.
     * <p>
     * <pre>
     *      {
     *          "ret" : false,
     *          "msg" : "错误的id，修改失败"
     *      }
     * </pre>
     *
     * @param message
     * @return
     */
    public static ResponseData error(String message) {
        return error(null, message, null);
    }

    public static ResponseData error(BaseException exception) {
        return error(null, exception.getMessage(), exception.getErrorCode());
    }

    public static ResponseData error(Object object, BaseException exception) {
        return error(object, exception.getMessage(), exception.getErrorCode());
    }

    /**
     * eg.
     * <p>
     * <pre>
     *      {
     *          "ret" : false
     *      }
     * </pre>
     *
     * @return
     */
    public static ResponseData error() {
        return error(null, null, null);
    }

    /**
     * eg.
     * <p>
     * <pre>
     *      {
     *          "code" : 0,
     *          "data": {
     *                      "encodedRID": "2916181129",
     *                      "operator": "gambol2",
     *                      "createTime": 1411363837776,
     *                      "rescueStatus": 1,
     *                      "pFunction": "free",
     *                      "departure": "北京",
     *                      "arrive": "大连"
     *          },
     *          "message";"SUCCESS"
     *      }
     * </pre>
     *
     * @param object
     * @param msg
     * @return
     */
    public static ResponseData success(Object object, String msg) {
        ResponseData result = new ResponseData();
        result.data = object != null ? object : DEFAULT_OBJECT_RETUN;
        result.message = Strings.isNullOrEmpty(msg) ? CommonError.SUCCESS.getMessage() : msg;
        result.code = CommonError.SUCCESS.getCode();
        return result;
    }

    /**
     * eg.
     * <p>
     * <pre>
     *      {
     *          "code" : 0,
     *          "data": {
     *                      "encodedRID": "2916181129",
     *                      "operator": "gambol2",
     *                      "createTime": 1411363837776,
     *                      "rescueStatus": 1,
     *                      "pFunction": "free",
     *                      "departure": "北京",
     *                      "arrive": "大连"
     *          },
     *          "message";"SUCCESS"
     *      }
     * </pre>
     *
     * @param object
     * @return
     */
    public static ResponseData success(Object object) {
        return success(object, null);
    }

    /**
     * eg.
     * <p>
     * <pre>
     *      {
     *          "code" : 0,
     *          "message": "SUCCESS"
     *      }
     * </pre>
     *
     * @return
     */
    public static ResponseData success() {
        return success(null, null);
    }

    /**
     * eg.
     * <pre>
     *
     *
     *  {
     *      "code": 0,
     *      "data": {
     *          "content": [
     *                          {
     *                              "operationTime": 1411389538087,
     *                              "username": "haifeng111222",
     *                              "encodedSID": "3279243797",
     *                              "operation": "关闭订单",
     *                              "totalQuotas": 30,
     *                              "operator": "kris.zhang"
     *                          },
     *                          {
     *                              "operationTime": 1411389517575,
     *                              "username": "haifeng111222",
     *                              "encodedSID": "3279243797",
     *                              "operation": "关闭订单",
     *                              "totalQuotas": 30,
     *                              "operator": "kris.zhang"
     *                          }
     *                      ],
     *          "totalRows": 4
     *      },
     *  }
     * </pre>
     *
     * @param list
     * @param totalRows
     * @return
     */
    public static <T> ResponseData list(int totalRows, Collection<T> list) {
        ResponseData result = new ResponseData();
        Map<String, Object> map = Maps.newHashMap();
        if (totalRows == 0) {
            totalRows = list.size();
        }

        if (list != null) {
            map.put(CONTENT, list);
        }
        map.put(TOTAL_ROWS, totalRows);

        result.data = map;
        result.code = IError.SUCCESS_CODE;
        return result;
    }

    public Collection<T> content() {
        if (this.data == null) {
            return Collections.emptyList();
        }
        if (this.data instanceof Map) {
            return (List) ((Map) this.data).get(CONTENT);
        } else if (this.data instanceof List) {
            return (List) this.data;
        }
        return Collections.emptyList();
    }

    public Integer total() {
        if (this.data == null) {
            return 0;
        }
        if (this.data instanceof Map) {
            return (Integer) ((Map) this.data).get(TOTAL_ROWS);
        } else if (this.data instanceof List) {
            return ((List) this.data).size();
        }
        return 0;
    }

}
