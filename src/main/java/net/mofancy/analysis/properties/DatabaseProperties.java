package net.mofancy.analysis.properties;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import global.util.Util;

public class DatabaseProperties {
	
	public static String URL;
	public static String USERNAME;
	public static String PASSWORD;
	public static String NAME;
	public static String SCHEMA;
	public static String TYPE;
	public static String JQTYPE;
	public static String PORT;
	public static String SERVER;
	public static Integer INITIALSIZE;
	public static Integer MAXTOTAL;
	public static Integer MAXIDLE;
	public static Integer MINIDLE;
	public static Integer MAXWAITMILLIS;
	public static String VALIDATIONQUERY;
	public static String FULLURL;
	public static String SOCKETNUMBER;
	
	static {
        Properties properties = new Properties();
        String def = System.getProperty("user.dir");
	    String s = Util.fileSeparator(def);
	    if (!def.endsWith(s)) {
	      def = def + s;
	    }
	    def = def + "properties" + s;
	    InputStream in = null;
        try {
        	in = new FileInputStream(def + "database.properties");
            properties.load(new InputStreamReader(in, StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        URL = properties.getProperty("jdbc.url").toString();
        
        USERNAME = properties.getProperty("jdbc.username").toString();
        PASSWORD = properties.getProperty("jdbc.password").toString();
        NAME = properties.getProperty("jdbc.name").toString();
        SCHEMA = properties.getProperty("jdbc.schema").toString();
        TYPE = properties.getProperty("jdbc.type").toString();
        JQTYPE = properties.getProperty("jdbc.jqtype").toString();
        PORT = properties.getProperty("jdbc.port").toString();
        SERVER = properties.getProperty("jdbc.server").toString();
        INITIALSIZE = Integer.parseInt(properties.getProperty("jdbc.initialSize"));
        MAXTOTAL = Integer.parseInt(properties.getProperty("jdbc.maxTotal"));
        MAXIDLE = Integer.parseInt(properties.getProperty("jdbc.maxIdle"));
        MINIDLE = Integer.parseInt(properties.getProperty("jdbc.minIdle"));
        MAXWAITMILLIS = Integer.parseInt(properties.getProperty("jdbc.maxWaitMillis"));
        VALIDATIONQUERY = properties.getProperty("jdbc.validationQuery");
        FULLURL = properties.getProperty("jdbc.url").toString()+"&user="+USERNAME+"&password="+PASSWORD;
        SOCKETNUMBER = properties.getProperty("jqueue.socketNumber");
        
    }

}
