package net.mofancy.analysis.web.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.mofancy.analysis.postgres.service.SegmentService;
import net.mofancy.analysis.web.common.ApiResponse;
import net.mofancy.analysis.web.common.exception.ParameterIllegalException;
import net.mofancy.analysis.web.common.resolver.AnalysisToken;

@RestController
@RequestMapping("/segment")
public class SegmentController {
	@Autowired
	private SegmentService segmentService;
	
	@RequestMapping("/list")
	public ApiResponse getSegmentList(AnalysisToken analysisToken,Integer datasetKey,Integer projectId) {
		Map<String,Object> resultMap = new HashMap<String,Object>();
		resultMap.put("list", segmentService.getSegmentList(projectId));
		return ApiResponse.buildSuccess(resultMap);
	}
	
	@RequestMapping("/getSegmentSummary")
	public ApiResponse getSegmentSummary(AnalysisToken analysisToken,Integer datasetKey,Integer projectId,Integer segmentationKey,Integer numFactor) {
		List<Map<String,Object>> list = segmentService.getSegmentSummary(projectId,segmentationKey,numFactor);
		return ApiResponse.buildSuccess(list);
	}
	
	
	@RequestMapping("/getSegmentPurchaseThemes")
	public ApiResponse getSegmentPurchaseThemes(AnalysisToken analysisToken,Integer datasetKey,Integer projectId,Integer analysisKey,Integer numFactor) {
		List<Map<String,Object>> list = segmentService.getSegmentPurchaseThemes(projectId,analysisKey,numFactor);
		return ApiResponse.buildSuccess(list);
	}
	
	@RequestMapping("/saveSegmentName")
	public ApiResponse saveSegmentName (AnalysisToken analysisToken,Integer datasetKey,Integer projectId,Integer segmentKey,String name) {
		if(StringUtils.isEmpty(name)) {
			throw new ParameterIllegalException("群体名不能为空！");
		}
		if(StringUtils.isEmpty(segmentKey)) {
			throw new ParameterIllegalException("segmentKey不能为空！");
		}
		segmentService.saveSegmentName(projectId,segmentKey,name);
		return ApiResponse.buildSuccess("群体名修改成功！");
	}
	
	
	@RequestMapping("/getSegmentProfile")
	public ApiResponse getSegmentProfile(AnalysisToken analysisToken,Integer datasetKey,Integer projectId,Integer solutionKey,Integer numFactor,Integer analysisKey,String summaryLevel) {
		List<Map<String,Object>> list = segmentService.getSegmentProfile(projectId,solutionKey,numFactor,analysisKey,summaryLevel);
		return ApiResponse.buildSuccess(list);
	}
	
}
