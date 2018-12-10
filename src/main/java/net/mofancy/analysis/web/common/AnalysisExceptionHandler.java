package net.mofancy.analysis.web.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import net.mofancy.analysis.web.common.exception.AnalysisTokenIllegalException;
import net.mofancy.analysis.web.common.exception.ParameterIllegalException;

@RestControllerAdvice
public class AnalysisExceptionHandler {

	private Logger log = LoggerFactory.getLogger(getClass());
	
	@ExceptionHandler(ParameterIllegalException.class)
	public ApiResponse parameterIllegalExceptionHandler(ParameterIllegalException e) {
		return new ApiResponse(ApiResponse.CODE_ILLEGAL_PARAMETER, e.getMessage());
	}
	
	@ExceptionHandler(AnalysisTokenIllegalException.class)
	public ApiResponse appTokenIllegalException(AnalysisTokenIllegalException e) {
		return new ApiResponse(ApiResponse.CODE_ILLEGAL_CONSULTANTTOKEN, e.getMessage());
	}
	
	@ExceptionHandler(Exception.class)
	public ApiResponse exceptionHandler(Exception e) {
		log.error("发生服务器级异常", e);
		return new ApiResponse(ApiResponse.CODE_SERVER_ERROR, e.getMessage());
	}
	
}
