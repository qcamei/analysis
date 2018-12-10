package net.mofancy.analysis.web.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.mofancy.analysis.config.DataSourceContextHolder;
import net.mofancy.analysis.config.DatabaseConfig;
import net.mofancy.analysis.postgres.service.DataPoolService;
import net.mofancy.analysis.postgres.vo.PageData;
import net.mofancy.analysis.web.common.ApiResponse;
import net.mofancy.analysis.web.common.resolver.AnalysisToken;

@RestController
@RequestMapping("/datapool")
public class DataPoolController {
	
	
	@Autowired
	private DataPoolService dataPoolService;
	
	@RequestMapping("/list")
	public ApiResponse getDataPoolList(AnalysisToken analysisToken,@RequestParam(value = "page", defaultValue = "1")Integer pageNum,@RequestParam(value = "limit", defaultValue = "10")Integer pageSize,String dbname) {
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		Set<String> set=DatabaseConfig.dbMap.keySet();
		int index = 0,start = (pageNum-1)*pageSize+1,end = pageNum*pageSize ;
        for (String s : set) {
        	if(!StringUtils.isEmpty(dbname)) {
        		if(DatabaseConfig.dbMap.get(s).toLowerCase().indexOf(dbname.toLowerCase())<0) {
        			continue;
        		}
        	}
        	index++;
        	if(index<start) {
        		continue;
        	} 
        	if(index>end) {
        		break;
        	}
        	DataSourceContextHolder.setDB(DatabaseConfig.dbMap.get(s));
			Map<String,Object> params = new HashMap<String,Object>();
			params.put("datasetKey", s);
			params.put("datasetName", DatabaseConfig.dbMap.get(s).toString());
			list.addAll(dataPoolService.selectListByCondition(params));
        }
        
        long totalCount = DatabaseConfig.dbMap.size();
        
		PageData pageData = new PageData();
		pageData.setDataList(list);
		pageData.setTotal(totalCount);
		pageData.setPageNum(pageNum);
		pageData.setPageSize(pageSize);
		
		return ApiResponse.buildSuccess(pageData);
	}
	
}
