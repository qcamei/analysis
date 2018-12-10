package net.mofancy.analysis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;

import net.mofancy.analysis.jqueue.Jqueue;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class AnalysisApplication {
	
	private static String[] args;
    private static ConfigurableApplicationContext context;

	public static void main(String[] args) {
		AnalysisApplication.args = args;
		context = SpringApplication.run(AnalysisApplication.class, args);
		try {
			new Jqueue(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public static void restart() {
        context.close();
        AnalysisApplication.context = SpringApplication.run(AnalysisApplication.class, args);
    }
}
