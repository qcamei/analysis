package net.mofancy.analysis.web.common.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import net.mofancy.analysis.util.AuthenticateUtils;
import net.mofancy.analysis.web.common.exception.AnalysisTokenIllegalException;

/**
 * 拦截处理用户Apptoken
 * @author wangfei
 *
 */
public class AnalysisTokenInterceptor extends HandlerInterceptorAdapter {
	private final Logger log = LoggerFactory.getLogger(getClass());

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		int verifyResult = AuthenticateUtils.verifyToken(request);
		if (verifyResult == 0) {
			log.warn("签名未通过验证!");
			throw new AnalysisTokenIllegalException("认证参数不正确");
		}
		if (verifyResult == -1) {
			log.warn("认证参数已过期!");
			throw new AnalysisTokenIllegalException("认证参数已过期");
		}
		return super.preHandle(request, response, handler);
		
	}
	
}
