package net.mofancy.analysis.util;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.MessageSource;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

public class MessageUtils {
	 public static String getMessage(MessageSource messageSource,String code,Object[] args) {
    	ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		HttpServletRequest request = requestAttributes.getRequest();
		Locale locale= RequestContextUtils.getLocale(request);
		return messageSource.getMessage(code, args, locale);
    }
}
