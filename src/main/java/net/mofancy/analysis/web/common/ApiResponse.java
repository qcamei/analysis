package net.mofancy.analysis.web.common;

import java.util.HashMap;


public class ApiResponse {
	private int code;
	private String msg = "";
	private Object data;
	
	private static final int CODE_SUCCESS = 2000000; // 成功
	private static final String MSG_SUCCESS = "OK";
	public static final int CODE_ILLEGAL_PARAMETER = 3000000; // 参数不合法
	public static final int CODE_ILLEGAL_CONSULTANTTOKEN = 3000001; // ConsultantToken不合法或已过期
	public static final int CODE_SERVER_ERROR = 5000000; // 服务器未捕获异常

	public ApiResponse() {
		this(CODE_SUCCESS, MSG_SUCCESS);
	}
	
	public ApiResponse(int code, String msg) {
		this.code = code;
		this.msg = msg;
	}
	
	public static ApiResponse buildSuccess() {
		ApiResponse resp = new ApiResponse();
		resp.setData(new HashMap<>());
		return resp;
	}
	
	
	public static ApiResponse buildSuccess(Object data) {
		ApiResponse resp = new ApiResponse();
		resp.setData(data);
		return resp;
	}
	
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "ApiResponse [code=" + code + ", msg=" + msg + ", data=" + data + "]";
	}
}
