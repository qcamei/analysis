package net.mofancy.analysis.properties;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class ApplicationProperties {
	
	public static Boolean STANDALONE_MODE;
	
	static {
        Properties properties = new Properties();
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("application.properties");
        try {
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
        STANDALONE_MODE = Boolean.valueOf(properties.getProperty("standaloneMode"));
    }

}
