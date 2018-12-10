package net.mofancy.analysis.web.common.converter;

import java.text.ParseException;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.core.convert.converter.Converter;

public class CustomDateConverter implements Converter<String, Date> {
	
	private final static String[] PATTERN = {"yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm:ss.SSS"};

	@Override
	public Date convert(String source) {
		if (StringUtils.isBlank(source)) return null;
		Date dest = null;
		try {
			dest = DateUtils.parseDate(source, PATTERN);
		} catch (ParseException pe) {
			throw new IllegalArgumentException("转化日期类型失败: " + source, pe);
		}
		return dest;
	}
	
}