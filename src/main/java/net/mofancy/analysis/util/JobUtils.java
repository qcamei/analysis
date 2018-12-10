package net.mofancy.analysis.util;

import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;

public class JobUtils {
	
	public static ArrayList<String[]> AddNewProjParam(ArrayList<String[]> p, String key, String key_desc, String val, String desc) {
		if(StringUtils.isEmpty(val)) {
			return p;
		}
		String[] str = new String[4];
		str[0] = key;
		str[1] = key_desc;
		str[2] = val;
		str[3] = desc;
		p.add(str);
		
		return p;
	}
	
	public static String repeatStr(int num, String format, String join) {
		return repeatStr(1, num, format, join);
	}
	
	
	public static String repeatStr(int start, int end, String format, String join) {
		String str = "", j = "";
		for (int i = start; i <= end; i++)
		{
			str += j + String.format(format, "" + i);
			j = join;
		}
		return str;
	}
	
	public static String repeatStr(String[] list, String format, String join) {
		String str = "", j = "";
		for (String s : list)
		{
			str += j + String.format(format, s);
			j = join;
		}
		return str;
	}
	
	public static String sqlStr(String str) {
	    return "'" + str.replace("'", "''") + "'";
	}
	  
	public static String sqlStrNull(String str) {
		return StringUtils.isEmpty(str) ? "null" : sqlStr(str);
	}
	
	public static String sqlNum(Object str) {
	    if (str == null) {
	      return "null";
	    }
	    return str.toString();
	}
	  
	public static String sqlNumNull(Object str) {
	    if (str == null) {
	      return "null";
	    }
	    String s = str.toString();
	    return s.length() < 1 ? "null" : s;
	}
}
