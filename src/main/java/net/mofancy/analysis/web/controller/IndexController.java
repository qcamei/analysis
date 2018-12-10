package net.mofancy.analysis.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.mofancy.analysis.postgres.service.MainService;
import net.mofancy.analysis.web.common.ApiResponse;
import net.mofancy.analysis.web.common.resolver.AnalysisToken;

@RestController
public class IndexController {
	
	@Autowired
	private MainService mainService;
	
	@RequestMapping("/getArcZincProess")
	public ApiResponse getArcZincProess(AnalysisToken analysisToken,String processType) {
		
		return ApiResponse.buildSuccess(mainService.getArcZincProess(processType));
	}
	
	@RequestMapping("/checkJQueue")
	public ApiResponse checkJQueue(AnalysisToken analysisToken,String processType) {
		
		return ApiResponse.buildSuccess(mainService.checkJQueue(processType));
	} 
}
