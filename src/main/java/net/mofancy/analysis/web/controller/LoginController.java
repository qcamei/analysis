package net.mofancy.analysis.web.controller;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import net.mofancy.analysis.util.AuthenticateUtils;
import net.mofancy.analysis.web.common.ApiResponse;
import net.mofancy.analysis.web.common.exception.ParameterIllegalException;


@RestController
public class LoginController {
	
	private Logger log = LoggerFactory.getLogger(getClass());
	
	/**
	 * 用户登录接口
	 * @param username
	 * @param pwd
	 * @return
	 */
	@RequestMapping(value = "loginIn", method = RequestMethod.GET)
	public ApiResponse loginIn(String username,String password) {
		if (StringUtils.isBlank(username)) {
			log.info("用户名不能为空！");
			throw new ParameterIllegalException("用户名不能为空！");
		}
		if (StringUtils.isBlank(password)) {
			log.info("密码不能为空！");
			throw new ParameterIllegalException("密码不能为空！");
		}
		if(!username.equals("admin")) {
			throw new ParameterIllegalException("该用户不存在！");
		}
		if(!password.equals("123456")) {
			throw new ParameterIllegalException("密码错误！");
		}
		//生成token
		String token = AuthenticateUtils.generateTokenStr(1);
		Map<String,Object> resultMap = new HashMap<String,Object>();
		resultMap.put("token", token);
		return ApiResponse.buildSuccess(resultMap);
	}
	
}
