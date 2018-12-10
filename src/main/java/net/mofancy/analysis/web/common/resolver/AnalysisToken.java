package net.mofancy.analysis.web.common.resolver;

/**
 * @author wangfei
 * 后端接口校验用户对象
 */
public class AnalysisToken {
	private Integer userid;
	
	public AnalysisToken() {}
	
	public AnalysisToken(Integer userid) {
		this.userid = userid;
	}

	public Integer getUserid() {
		return userid;
	}

	public void setUserid(Integer userid) {
		this.userid = userid;
	}
	
}
