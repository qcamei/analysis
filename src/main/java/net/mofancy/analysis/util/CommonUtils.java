package net.mofancy.analysis.util;

import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class CommonUtils {
	
	public static JSONObject convertListToJsonString(Map<String, Object> resultMap) {
	    StringBuffer sb = new StringBuffer("");
	    try {
	        JSONArray jsonArray = JSONArray.parseArray(resultMap.get("results").toString());
	        sb.append("{");
	        sb.append("code");
	        sb.append(":0,");
	        sb.append("count");
	        sb.append(":");
	        sb.append(resultMap.get("totalCount"));
	        sb.append(",");
	        sb.append("data");
	        sb.append(":");
	        sb.append(jsonArray.toString());
	        sb.append(",");
	        sb.append("msg");
	        sb.append(":");
	        sb.append("''");
	        sb.append("}");
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
	    return JSONObject.parseObject(sb.toString());
	}
	
	
}
