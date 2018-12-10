package net.mofancy.analysis.util;

import java.nio.charset.StandardCharsets;

/**
 * Java根据html模板创建 html文件
 * @author wangfei
 *
 */
public class CreateHtmlUtils {
  
	public static String htmlFile(String ht){
		String s = null;
		try {
			 s = FileServerUtils.uploadDoc(ht.getBytes(StandardCharsets.UTF_8), "html");
		} catch (Exception e) {
		}
		return s; 
	}
	

}
