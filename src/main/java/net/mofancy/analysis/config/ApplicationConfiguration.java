package net.mofancy.analysis.config;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.Filter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import net.mofancy.analysis.properties.ApplicationProperties;
import net.mofancy.analysis.web.common.converter.CustomDateConverter;
import net.mofancy.analysis.web.common.interceptor.AnalysisTokenInterceptor;
import net.mofancy.analysis.web.common.resolver.AnalysisTokenArgumentResolver;



@Configuration
public class ApplicationConfiguration extends WebMvcConfigurerAdapter {
	
	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder()
                .indentOutput(true)
                .dateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")); // 格式化JSON中的日期显示
        converters.add(new MappingJackson2HttpMessageConverter(builder.build()));
	}
	
	@Override
	public void addFormatters(FormatterRegistry registry) {
		Converter<String, Date> dateConverter = new CustomDateConverter(); // 字符串参数转为日期对象
		registry.addConverter(dateConverter);
		super.addFormatters(registry);
	}

	@Bean
	public Filter characterEncodingFilter() {
        final CharacterEncodingFilter filter = new CharacterEncodingFilter();
        filter.setEncoding(StandardCharsets.UTF_8.name()); // 编码UTF-8
        filter.setForceEncoding(true);
        return filter;
    }

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		if (ApplicationProperties.STANDALONE_MODE) {
			AnalysisTokenInterceptor appInterceptor = new AnalysisTokenInterceptor();
			registry.addInterceptor(appInterceptor);
		}
		super.addInterceptors(registry);
	}

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
		HandlerMethodArgumentResolver resoover = new AnalysisTokenArgumentResolver();
		argumentResolvers.add(resoover);
		super.addArgumentResolvers(argumentResolvers);
	}

}
