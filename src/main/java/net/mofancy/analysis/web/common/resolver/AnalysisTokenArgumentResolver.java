package net.mofancy.analysis.web.common.resolver;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import net.mofancy.analysis.properties.ApplicationProperties;
import net.mofancy.analysis.util.AuthenticateUtils;
import net.mofancy.analysis.web.common.exception.AnalysisTokenIllegalException;


public class AnalysisTokenArgumentResolver implements HandlerMethodArgumentResolver {
	
	private Logger log = LoggerFactory.getLogger(getClass());

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return (parameter.getParameterType().equals(AnalysisToken.class));
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
		AnalysisToken token = null;
		if (ApplicationProperties.STANDALONE_MODE) {
			// 独立模式
			Object oSub = webRequest.getAttribute(AuthenticateUtils.TOKEN_KEY, NativeWebRequest.SCOPE_REQUEST);
			if (oSub == null) {
				log.error("非法请求,认证参数不合法");
				//throw new ParameterIllegalException("登录已过期");
			}
//			token = new AnalysisToken(Integer.valueOf(oSub.toString()));
			webRequest.removeAttribute(AuthenticateUtils.TOKEN_KEY, NativeWebRequest.SCOPE_REQUEST);
		} else {
			// 服务模式
			String strSub = webRequest.getHeader(AuthenticateUtils.TOKEN_KEY);
			if (StringUtils.isBlank(strSub)) {
				log.error("非法请求,认证参数不合法");
				throw new AnalysisTokenIllegalException("登录已过期");
			}
			token = new AnalysisToken(Integer.valueOf(strSub));
		}
		return token;
	}

}
