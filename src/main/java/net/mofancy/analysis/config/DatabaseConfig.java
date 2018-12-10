package net.mofancy.analysis.config;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.mybatis.spring.annotation.MapperScan;
import org.postgresql.Driver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import net.mofancy.analysis.properties.DatabaseProperties;
import net.mofancy.analysis.util.JdbcUtils;

@Configuration
@EnableTransactionManagement
@MapperScan("net.mofancy.analysis.postgres.mapper")
public class DatabaseConfig {
	
	public static DynamicDataSource dynamicDataSource = new DynamicDataSource();
	
	public static Map<String,String> dbMap = new HashMap<String,String>();
	
	// 配置多数据源
	public static Map<Object, Object> dsMap = new HashMap<Object, Object>();
	
	public static DataSource getDataSource(String url,String username,String password) throws SQLException {
		
		BasicDataSource ds = new BasicDataSource();
		ds.setDriver(new Driver());
		ds.setUrl(url);
		ds.setUsername(username);
		ds.setPassword(password);
		ds.setInitialSize(DatabaseProperties.INITIALSIZE);
		ds.setMaxTotal(DatabaseProperties.MAXTOTAL);
		ds.setMaxIdle(DatabaseProperties.MAXIDLE);
		ds.setMinIdle(DatabaseProperties.MINIDLE);
		ds.setMaxWaitMillis(DatabaseProperties.MAXWAITMILLIS);
		ds.setValidationQuery(DatabaseProperties.VALIDATIONQUERY);
		ds.setTestOnBorrow(true);
		ds.setTestOnReturn(true);
		ds.setTestWhileIdle(true);
		return ds;
	}
	
	
	
	/**
     * 动态数据源: 通过AOP在不同数据源之间动态切换
     * @return
	 * @throws Exception 
     */
    @Bean(name = "dbconfig")
    public DataSource dataSource() throws Exception {
    	
    	List<Map<String,Object>> list = JdbcUtils.select(null);
        // 默认数据源
        dynamicDataSource.setDefaultTargetDataSource(getDataSource(DatabaseProperties.URL,DatabaseProperties.USERNAME,DatabaseProperties.PASSWORD));
        
        for (Map<String, Object> map : list) {
        	String temp = "jdbc:postgresql://LOCALHOST:5432/DBNAME?currentSchema=SCHEMA&useUnicode=true&characterEncoding=utf8&autoReconnect=true&failOverReadOnly=false&useSSL=false";
        	String url = temp.replace("LOCALHOST", map.get("dburl").toString()).replace("DBNAME", map.get("dbname").toString()).replace("SCHEMA", map.get("dbschema").toString());
        	dsMap.put(map.get("dataset_name").toString(), getDataSource(url,"postgres".toString(),map.get("dbpwd").toString()));
        	dbMap.put(map.get("dataset_key").toString(), map.get("dataset_name").toString());
		}

        dynamicDataSource.setTargetDataSources(dsMap);
        
        return dynamicDataSource;
    }
    
	
}
