package net.mofancy.analysis.web.common.exception;

/**
 * 非法参数异常
 * @author wangfei
 *
 */
public class ParameterIllegalException extends RuntimeException {
	private static final long serialVersionUID = -5416232933485291796L;

	public ParameterIllegalException(String msg) {
		super(msg);
	}
}
