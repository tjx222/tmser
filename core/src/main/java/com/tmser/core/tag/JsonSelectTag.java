package com.tmser.core.tag;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * json下拉框标签
 * @author 张凯
 * @date 2014-1-14
 *
 */
public class JsonSelectTag extends AbstractSelectTag {

	private static final long serialVersionUID = 1L;
	
	/**
	 * JSON
	 */
	private String json;

	/**
	 * 填充选项
	 */
	@Override
	protected void fillOptions(StringBuffer selectBuffer) {
		// TODO Auto-generated method stub
		ObjectMapper objectMapper = new ObjectMapper();
		Map optionMap = Collections.EMPTY_MAP;
		try {
			optionMap = objectMapper.readValue(json, Map.class);
		} catch (JsonParseException e) {
			log.warn("<tsp:json>解析出错，json[" + json + "]");
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (optionMap != null && !optionMap.isEmpty()) {
			for (Iterator iter = optionMap.keySet().iterator(); iter.hasNext();) {
				Object code = iter.next();//编码
				Object name = optionMap.get(code);//名称
				String value = name.toString();
				String checked = "";
				if (selected != null && selected.equals(code)) {
					checked = "selected=\"true\"";
				}
				selectBuffer.append("<option value=\"" + code + "\" " + checked
						+ " title=\"" + value + "\">" + value + "</option>");
			}
		}
	}

	public String getJson() {
		return json;
	}

	public void setJson(String json) {
		this.json = json;
	}
}
