package net.mofancy.analysis.web.common.exception;

/**
 * @author wangfei
 * APPToken非法
 */
public class AnalysisTokenIllegalException extends RuntimeException {
	private static final long serialVersionUID = 1140292201333010148L;
	
	public AnalysisTokenIllegalException(String msg) {
		super(msg);
	}

}
