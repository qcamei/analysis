package net.mofancy.analysis.web.controller;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.mofancy.analysis.postgres.service.FamilyService;
import net.mofancy.analysis.util.ExcelUtil;
import net.mofancy.analysis.web.common.ApiResponse;
import net.mofancy.analysis.web.common.exception.ParameterIllegalException;
import net.mofancy.analysis.web.common.resolver.AnalysisToken;

@RestController
@RequestMapping("/family")
public class FamilyController {
	
	@Autowired
	private FamilyService familyService;
	
	
	@RequestMapping("/familys")
	public ApiResponse getFamilys(AnalysisToken analysisToken,Integer datasetKey,Integer projectId) {
		List<Map<String,Object>> list = familyService.getFamilys(projectId);
		return ApiResponse.buildSuccess(list);
	}
	
	@RequestMapping("/digitalList")
	public ApiResponse getDigitalList(AnalysisToken analysisToken,Integer datasetKey,Integer projectId) {
		List<Map<String,Object>> list = familyService.getDigitalList(projectId);
		return ApiResponse.buildSuccess(list);
	}
	
	@RequestMapping("/top20")
	public ApiResponse getTopGoods(AnalysisToken analysisToken,Integer datasetKey,Integer projectId,Integer parentKey) {
		List<Map<String,Object>> list = familyService.getTopGoods(projectId,parentKey);
		return ApiResponse.buildSuccess(list);
	}
	
	@RequestMapping("/distributionList")
	public ApiResponse gettDistributionList(AnalysisToken analysisToken,Integer datasetKey,Integer projectId,Integer parentKey) {
		List<Map<String,Object>> list = familyService.getDistributionList(projectId,parentKey);
		return ApiResponse.buildSuccess(list);
	}
	
	@RequestMapping("/solutionsList")
	public ApiResponse getSolutionsList(AnalysisToken analysisToken,Integer datasetKey,Integer projectId,Integer parentKey) {
		List<Map<String,Object>> list = familyService.getSolutionsList(projectId,parentKey);
		double sum = 0.0;
		for (Map<String, Object> map : list) {
			sum += Integer.parseInt(map.get("pseudot2_ind").toString());
		}
		
		for (int i = 0;i<list.size();i++) {
			Map<String,Object> obj = list.get(i);
			BigDecimal bigDecimal = new BigDecimal(obj.get("pseudot2_ind").toString());
			obj.put("pseudot2_ind", bigDecimal.divide(new BigDecimal(sum),3,BigDecimal.ROUND_FLOOR).doubleValue());
			list.set(i, obj);
		}
		
		return ApiResponse.buildSuccess(list);
	}
	
	@RequestMapping("/solutionsList2")
	public ApiResponse getSolutionsList2(AnalysisToken analysisToken,Integer datasetKey,Integer projectId,Integer parentKey,Integer g) {
		List<Map<String,Object>> list = familyService.getSolutionsList2(projectId,parentKey,g);
		return ApiResponse.buildSuccess(list);
	}
	
	@RequestMapping("/solutionsList3")
	public ApiResponse getSolutionsList3(AnalysisToken analysisToken,Integer datasetKey,Integer projectId,Integer solutionKey) {
		List<Map<String,Object>> list = familyService.getSolutionsList3(projectId,solutionKey);
		return ApiResponse.buildSuccess(list);
	}
	
	@RequestMapping("/solutionsList4")
	public ApiResponse getSolutionsList4(AnalysisToken analysisToken,Integer datasetKey,Integer projectId,Integer solutionKey,Integer groupKey) {
		List<Map<String,Object>> list = familyService.getSolutionsList4(projectId,solutionKey,groupKey);
		return ApiResponse.buildSuccess(list);
	}
	
	
	@RequestMapping("/runCluster")
	public ApiResponse runCluster(AnalysisToken analysisToken,Integer datasetKey,Integer projectId,Integer deptKey,String deptName,Integer minSale) {
		familyService.runCluster(projectId,deptKey,deptName,datasetKey,minSale);
		return ApiResponse.buildSuccess("正在计算,请稍等...");
	}
	
	
	@RequestMapping("/runClusterAll")
	public ApiResponse runClusterAll(AnalysisToken analysisToken,Integer datasetKey,Integer projectId,Integer minSale) {
		familyService.runClusterAll(projectId,datasetKey,minSale);
		return ApiResponse.buildSuccess("正在计算,请稍等...");
	}
	
	@RequestMapping("/saveClusterSolution")
	public ApiResponse saveClusterSolution(AnalysisToken analysisToken,Integer datasetKey,Integer projectId,Integer deptKey,String name,Integer numCluster) {
		
		if(StringUtils.isEmpty(name)) {
			throw new ParameterIllegalException("商品组合名字不能为空!");
		}
		
		if(StringUtils.isEmpty(projectId)) {
			throw new ParameterIllegalException("参数projectId不能为空!");
		}
		
		if(StringUtils.isEmpty(deptKey)) {
			throw new ParameterIllegalException("参数deptKey不能为空!");
		}
		
		if(StringUtils.isEmpty(numCluster)) {
			throw new ParameterIllegalException("参数numCluster不能为空!");
		}
		
		
		familyService.saveClusterSolution(projectId,deptKey,name,numCluster);
		return ApiResponse.buildSuccess("保存成功");
	}
	
	@RequestMapping("/deleteSolution")
	public ApiResponse deleteSolution(AnalysisToken analysisToken,Integer datasetKey,Integer projectId,Integer solutionKey) {
		familyService.deleteSolution(projectId,solutionKey);
		return ApiResponse.buildSuccess("删除成功");
	}
	
	@RequestMapping("/updateSolutionName")
	public ApiResponse updateSolutionName(AnalysisToken analysisToken,Integer datasetKey,Integer projectId,Integer deptKey,Integer solutionKey,String name) {
		familyService.updateSolutionName(projectId, deptKey, solutionKey, name);
		return ApiResponse.buildSuccess("修改成功");
	}
	
	@RequestMapping("/updateGroupName")
	public ApiResponse updateGroupName(AnalysisToken analysisToken,Integer datasetKey,Integer projectId,Integer groupKey,String name) {
		familyService.updateGroupName(projectId, groupKey, name);
		return ApiResponse.buildSuccess("修改成功");
	}
	
	@RequestMapping("/exportExcel")
	public void exportExcel(AnalysisToken analysisToken,Integer datasetKey, HttpServletResponse response) {
		List<Map<String,Object>> list = familyService.exportExcel();
		
		//excel列名
		String[] title = {"dept_code","dept_desc", "group_key", "group_name", "pseudo_code","pseudo_des"};
		//excel文件名
		String fileName = ".xls";
		//sheet名
		String sheetName = "商品家族组合";
		String [][] content = new String[list.size()][];
		for (int i = 0; i < list.size(); i++) {
            content[i] = new String[title.length];
            Map<String, Object> obj = list.get(i);
            content[i][0] = obj.get("dept_code").toString();
            content[i][1] = obj.get("dept_desc").toString();
            content[i][2] = obj.get("group_key").toString();
            content[i][3] = obj.get("group_name").toString();
            content[i][4] = obj.get("pseudo_code").toString();
            content[i][5] = obj.get("pseudo_des").toString();
		}
		//创建HSSFWorkbook 
		HSSFWorkbook wb = ExcelUtil.getHSSFWorkbook(sheetName, title, content, null);
		try {
			response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
			OutputStream os = response.getOutputStream();
			wb.write(os);
			os.flush();
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
