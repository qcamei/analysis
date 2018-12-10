package net.mofancy.analysis.web.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import net.mofancy.analysis.postgres.persistence.ResourceAuthority;
import net.mofancy.analysis.postgres.service.ResourceAuthorityService;
import net.mofancy.analysis.web.common.ApiResponse;
import net.mofancy.analysis.web.common.exception.ParameterIllegalException;

@RestController
@RequestMapping("/resourceAuthority")
public class ResourceAuthorityController {
	
	@Autowired
	public ResourceAuthorityService resourceAuthorityService;

	
	@RequestMapping(value = "/allotResourceAuthority", method = RequestMethod.POST)
	public ApiResponse allotResourceAuthority(ResourceAuthority resourceAuthority,Integer sign) {
		int id = resourceAuthorityService.allotResourceAuthority(resourceAuthority,sign);
		if(StringUtils.isEmpty(sign)) {
			throw new ParameterIllegalException("参数异常！");
		}
		if(sign==1) {
			Map<String,Object> params = new HashMap<String,Object>();
			params.put("id", id);
			params.put("msg", "资源权限添加成功！");
			return  ApiResponse.buildSuccess(params);
		} else {
			return  ApiResponse.buildSuccess("资源权限移除成功！");
		}
        
	}
	
}
