package net.mofancy.analysis.web.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.mofancy.analysis.postgres.service.ThemeService;
import net.mofancy.analysis.postgres.vo.PageData;
import net.mofancy.analysis.web.common.ApiResponse;
import net.mofancy.analysis.web.common.exception.ParameterIllegalException;
import net.mofancy.analysis.web.common.resolver.AnalysisToken;

@RestController
@RequestMapping("/theme")
public class ThemeController {
	
	@Autowired
	private ThemeService themeService;
	
	@RequestMapping("/list")
	public ApiResponse getThemeList(AnalysisToken analysisToken,Integer datasetKey,Integer projectId) {
		List<Map<String,Object>> list = themeService.getThemeList(projectId);
		return ApiResponse.buildSuccess(list);
	}
	
	@RequestMapping("/distributionList")
	public ApiResponse getDistributionList(AnalysisToken analysisToken,Integer datasetKey,Integer projectId) {
		List<Map<String,Object>> list = themeService.getDistributionList(projectId);
		return ApiResponse.buildSuccess(list);
	}
	
	
	@RequestMapping("/getNumFactor")
	public ApiResponse getThemes(AnalysisToken analysisToken,Integer datasetKey,Integer projectId,Integer analysisKey) {
		List<Map<String,Object>> list = themeService.getNumFactor(projectId,analysisKey);
		return ApiResponse.buildSuccess(list);
	}
	
	
	@RequestMapping("/runFactorAnalysis")
	public ApiResponse runFactorAnalysis(AnalysisToken analysisToken,Integer datasetKey,Integer projectId,String analysisName,Integer minSpend,Integer maxSpend,String params) {
		if(StringUtils.isEmpty(analysisName)) {
			throw new ParameterIllegalException("主题分析名不能为空！");
		}
		if(StringUtils.isEmpty(params)) {
			throw new ParameterIllegalException("请至少选择一个商品组合！");
		}
		themeService.runFactorAnalysis(datasetKey,projectId,analysisName,minSpend,maxSpend,params);
		return ApiResponse.buildSuccess("正在运算主题分析，请稍等...!");
	}
	@RequestMapping("/purchaseThemes")
	public ApiResponse purchaseThemes(AnalysisToken analysisToken,Integer datasetKey,Integer projectId) {
		List<Map<String,Object>> list =themeService.purchaseThemes(projectId);
		return ApiResponse.buildSuccess(list);
	}
	
	@RequestMapping("/getFactorResult")
	public ApiResponse getFactorResult(AnalysisToken analysisToken,Integer datasetKey,Integer projectId,Integer numFactor,Integer analysisKey,String rotation,String includeGroups,@RequestParam(value = "page", defaultValue = "0")Integer pageNum,@RequestParam(value = "limit", defaultValue = "10") Integer pageSize) {
		
		PageData pageData = themeService.getFactorResult(projectId,numFactor,analysisKey,rotation,includeGroups,pageNum,pageSize);
		return ApiResponse.buildSuccess(pageData);
	}
	
	@RequestMapping("/getSavedSolutions")
	public ApiResponse getSavedSolutions(AnalysisToken analysisToken,Integer datasetKey,Integer projectId) {
		List<Map<String,Object>> list =themeService.getSavedSolutions(projectId);
		return ApiResponse.buildSuccess(list);
	}
	
	@RequestMapping("/loadFactorResult")
	public ApiResponse loadFactorResult(AnalysisToken analysisToken,Integer datasetKey,Integer projectId,Integer numFactor,Integer analysisKey,String rotation,String includeGroups) {
		Map<String,Object> resultMap =themeService.loadFactorResult(projectId,numFactor,analysisKey,rotation,includeGroups);
		return ApiResponse.buildSuccess(resultMap);
	}
	
	@RequestMapping("/saveFactorName")
	public ApiResponse saveFactorName(AnalysisToken analysisToken,Integer datasetKey,Integer projectId,Integer numFactor,Integer analysisKey,String name,String rotation,Integer factorInd,String includeGroups) {
		if(StringUtils.isEmpty(name)) {
			throw new ParameterIllegalException("主题名不能为空!");
		}
		if(StringUtils.isEmpty(factorInd)||factorInd==0) {
			throw new ParameterIllegalException("请选择主题列!");
		}
		themeService.saveFactorSolution(projectId, numFactor, analysisKey, rotation,includeGroups);
		themeService.saveFactorName(projectId,numFactor,analysisKey,name,rotation,factorInd);
		
		
		return ApiResponse.buildSuccess("更新成功!");
	}
	
	@RequestMapping("/deleteFactorSolution")
	public ApiResponse deleteFactorSolution(AnalysisToken analysisToken,Integer datasetKey,Integer projectId,Integer analysisKey) {
		
		themeService.deleteFactorSolution(projectId, analysisKey);
		
		return ApiResponse.buildSuccess("删除成功!");
	}
	
	@RequestMapping("/deleteFactorAnalysis")
	public ApiResponse deleteFactorAnalysis(AnalysisToken analysisToken,Integer datasetKey,Integer projectId,Integer analysisKey) {
		
		themeService.deleteFactorAnalysis(projectId, analysisKey);
		
		return ApiResponse.buildSuccess("删除成功!");
	}
	
	@RequestMapping("/purchaseThemes2")
	public ApiResponse purchaseThemes2(AnalysisToken analysisToken,Integer datasetKey,Integer projectId,Integer parentKey,Integer numFactor) {
		List<Map<String,Object>> list = themeService.purchaseThemes2(projectId,parentKey,numFactor);
		return ApiResponse.buildSuccess(list);
	}	
	
	
	@RequestMapping("/getSavedSolutionsById")
	public ApiResponse getSavedSolutionsById(AnalysisToken analysisToken,Integer datasetKey,Integer projectId,Integer themeKey) {
		Map<String,Object> resultMap= themeService.getSavedSolutionsById(projectId,themeKey);
		return ApiResponse.buildSuccess(resultMap);
	}	
	
	
	@RequestMapping("/loadFactorResult2")
	public ApiResponse loadFactorResult2(AnalysisToken analysisToken,Integer datasetKey,Integer projectId,Integer numFactor,Integer analysisKey,String rotation,String includeGroups,Integer factorInd) {
		Map<String,Object> resultMap =themeService.loadFactorResult2(projectId,numFactor,analysisKey,rotation,includeGroups,factorInd);
		return ApiResponse.buildSuccess(resultMap);
	}
	
	
	@RequestMapping("/runSegmentationJob")
	public ApiResponse runSegmentationJob(AnalysisToken analysisToken,Integer datasetKey,Integer projectId,Integer profileKey,Integer solutionKey,Integer minSeg,Integer maxSeg) {
		if(StringUtils.isEmpty(minSeg)||StringUtils.isEmpty(maxSeg)) {
			throw new ParameterIllegalException("市场细分数量不能为空!");
		}
		themeService.runSegmentationJob(datasetKey,projectId,profileKey, solutionKey,minSeg,maxSeg);
		return ApiResponse.buildSuccess("正在计算市场细分,请稍等...");
	}
	
	@RequestMapping("/runCustomerProfile")
	public ApiResponse runCustomerProfile(AnalysisToken analysisToken,Integer datasetKey,Integer projectId,Integer solutionKey,String selectedFactors,String solutionName,String themeName,Integer minDept,Integer minCluster) {
		if(StringUtils.isEmpty(solutionKey)) {
			throw new ParameterIllegalException("请选择主题方案！");
		}
		if(StringUtils.isEmpty(selectedFactors)) {
			throw new ParameterIllegalException("请至少选择一个主题！");
		}
		if(StringUtils.isEmpty(minDept)) {
			throw new ParameterIllegalException("请选择最少“Hier1”数！");
		}
		if(StringUtils.isEmpty(minCluster)) {
			throw new ParameterIllegalException("请选择最少“家族”数！");
		}
		themeService.runCustomerProfile(datasetKey,projectId,solutionKey,selectedFactors,solutionName,themeName,minDept,minCluster);
		return ApiResponse.buildSuccess("正在初始化,请稍等...");
	}
	
	@RequestMapping("/getClusterDistribution")
	public ApiResponse getClusterDistribution(AnalysisToken analysisToken,Integer datasetKey,Integer projectId,Integer analysisKey) {
		if(StringUtils.isEmpty(analysisKey)||analysisKey==0) {
			throw new ParameterIllegalException("请选择主题方案！");
		}
		List<Map<String,Object>> list = themeService.getClusterDistribution(projectId,analysisKey);
		return ApiResponse.buildSuccess(list);
	}
	
	@RequestMapping("/getDeptDistribution")
	public ApiResponse getDeptDistribution(AnalysisToken analysisToken,Integer datasetKey,Integer projectId,Integer analysisKey) {
		if(StringUtils.isEmpty(analysisKey)||analysisKey==0) {
			throw new ParameterIllegalException("请选择主题方案！");
		}
		List<Map<String,Object>> list = themeService.getDeptDistribution(projectId,analysisKey);
		return ApiResponse.buildSuccess(list);
	}
}


