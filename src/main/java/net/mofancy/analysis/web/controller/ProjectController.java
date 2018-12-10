package net.mofancy.analysis.web.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;

import net.mofancy.analysis.postgres.service.ProjectService;
import net.mofancy.analysis.postgres.vo.PageData;
import net.mofancy.analysis.util.WebUtils;
import net.mofancy.analysis.web.common.ApiResponse;
import net.mofancy.analysis.web.common.exception.ParameterIllegalException;
import net.mofancy.analysis.web.common.resolver.AnalysisToken;

@RestController
@RequestMapping("/project")
public class ProjectController {
	@Autowired
	private ProjectService projectService;
	
	@RequestMapping("/list")
	public ApiResponse getProjectList(AnalysisToken analysisToken,Integer datasetKey,@RequestParam(value = "page", defaultValue = "1")Integer pageNum,@RequestParam(value = "limit", defaultValue = "10")Integer pageSize,String projectName) {
		PageData pageData = projectService.selectListByCondition(datasetKey,pageNum,pageSize,projectName);
		return ApiResponse.buildSuccess(pageData);
	}
	
	@RequestMapping("/getGoodsList")
	public ApiResponse getGoodsList(AnalysisToken analysisToken,Integer datasetKey,Integer projectId,@RequestParam(value = "page", defaultValue = "1")Integer pageNum,@RequestParam(value = "limit", defaultValue = "20")Integer pageSize,String prodCode,String prodDesc,String parentCode,String parentDesc,String sort) {
		PageData pageData = projectService.getGoodsList(projectId,pageNum,pageSize,prodCode,prodDesc,parentCode,parentDesc,sort);
		return ApiResponse.buildSuccess(pageData);
	}
	
	
	@RequestMapping("/detail")
	public ApiResponse getProjectById(AnalysisToken analysisToken,Integer datasetKey,Integer projectId) {
		JSONObject resultMap = new JSONObject();
		List<Map<String,Object>> list = projectService.getProjectById(datasetKey,projectId);
		for (Map<String, Object> map : list) {
			resultMap.put(map.get("param_name").toString(),map.get("param_info"));
		}
		
		return ApiResponse.buildSuccess(resultMap);
	}
	
	@RequestMapping("/save")
	public ApiResponse saveProject(AnalysisToken analysisToken,Integer datasetKey,HttpServletRequest request) {
		
		Map<String,String> params = WebUtils.getParameterMap(request);
		
		if(StringUtils.isEmpty(params.get("projName"))) {
			throw new ParameterIllegalException("沙盘名称不能为空!");
		}
		
		if(StringUtils.isEmpty(params.get("projDesc"))) {
			throw new ParameterIllegalException("沙盘描述不能为空!");
		}
		
		if(StringUtils.isEmpty(params.get("period"))) {
			throw new ParameterIllegalException("时间区间不能为空!");
		}
		
		if(StringUtils.isEmpty(params.get("goodsLevel"))) {
			throw new ParameterIllegalException("商品层级不能为空!");
		}
		
		if (params.get("projDesc").toString().length() > 500) {
			throw new ParameterIllegalException("沙盘描述长度不能超过500个字符!");
		}
		
		projectService.saveProject(params,datasetKey);
		
		return ApiResponse.buildSuccess("新建成功");
	}
	
	
	@RequestMapping("/delete")
	public ApiResponse deleteProject(AnalysisToken analysisToken,HttpServletRequest request,Integer projectId,Integer datasetKey) {
		
		if(StringUtils.isEmpty(projectId)) {
			throw new ParameterIllegalException("参数projectId不能为空!");
		}
		
		projectService.deleteProject(projectId,datasetKey);
		return ApiResponse.buildSuccess("删除成功");
	}
	
	@RequestMapping("/deptDataGrid")
	public ApiResponse deptDataGrid(AnalysisToken analysisToken,Integer datasetKey,@RequestParam(value = "page", defaultValue = "0")Integer pageNum,@RequestParam(value = "limit", defaultValue = "10") Integer pageSize,Integer projectId) {
		PageData pageData = projectService.deptDataGrid(pageNum,pageSize,projectId);
		return ApiResponse.buildSuccess(pageData);
	}
	
	
}
