package net.mofancy.analysis.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.mofancy.analysis.properties.DatabaseProperties;

public class JdbcUtils {
	
	public static List<Map<String,Object>> select(Integer id) {
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		Connection conn = getConnection();
		String sql = "SELECT a.dataset_sys_key AS dataset_key,a.dataset_name,b.param_val AS dbname,c.param_val AS dburl,COALESCE(d.param_val,'public') AS dbschema,COALESCE(e.param_val,'postgres') AS dbpwd FROM dataset_list a\r\n" + 
				"INNER JOIN dataset_parameter b ON\r\n" + 
				"a.dataset_sys_key = b.dataset_sys_key AND b.param_name = 'DatabaseName'\r\n" + 
				"INNER JOIN dataset_parameter c ON\r\n" + 
				"a.dataset_sys_key = c.dataset_sys_key AND c.param_name = 'DatabaseServer'\r\n" + 
				" LEFT JOIN dataset_parameter d ON\r\n" + 
				" a.dataset_sys_key = d.dataset_sys_key AND d.param_name = 'DatabaseSchema'\r\n" + 
				"  LEFT JOIN dataset_parameter e ON\r\n" + 
				" a.dataset_sys_key = e.dataset_sys_key AND e.param_name = 'DatabasePwd' WHERE 1=1 ";
		if(id!=null) {
			sql += " AND a.dataset_sys_key = "+id+" ";
		}
	    PreparedStatement pstmt;
	    try {
	    	 pstmt = (PreparedStatement)conn.prepareStatement(sql);
	         ResultSet rs = pstmt.executeQuery();
	         ResultSetMetaData rsm =rs.getMetaData(); 
	         int col = rs.getMetaData().getColumnCount();
	         while (rs.next()) {
	        	 Map<String,Object> map = new HashMap<String,Object>();
	             for (int i = 1; i <= col; i++) {
	            	 String value = rs.getString(i);
	                 map.put(rsm.getColumnName(i), value);
	             }
	             list.add(map);
	         }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return list;
	}
	
	public static int getJobId() {
		int id = 0;
		String sql = "SELECT nextval('jqueue_job_seq')";
	    PreparedStatement pstmt;
	    Connection conn = getConnection();
	    try {
	    	 pstmt = (PreparedStatement)conn.prepareStatement(sql);
	         ResultSet rs = pstmt.executeQuery();
	         while (rs.next()) {
	        	id = Integer.parseInt(rs.getString(1));
	         }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return id;
	}
	
	private static Connection getConnection() {
	    String driver = "org.postgresql.Driver";
	    String url = DatabaseProperties.URL;
	    String username = DatabaseProperties.USERNAME;
	    String password = DatabaseProperties.PASSWORD;
	    Connection conn = null;
	    try {
	        Class.forName(driver); //classLoader,加载对应驱动
	        conn = (Connection) DriverManager.getConnection(url, username, password);
	    } catch (ClassNotFoundException e) {
	        e.printStackTrace();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    
	    return conn;
	}
	
	
	public static void main(String[] args) {
		System.out.println(getJobId());
	}
}
