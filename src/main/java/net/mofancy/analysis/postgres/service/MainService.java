package net.mofancy.analysis.postgres.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import net.mofancy.analysis.postgres.mapper.MainMapper;

@Service
public class MainService {
	
	@Autowired
	private MainMapper mainMapper;
	
	public List<Map<String,Object>> getArcZincProess(String processType) {
		
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("submittedBy", "Qi");
		
		if(StringUtils.isEmpty(processType)) {
			params.put("processType", processType);
		}
		return mainMapper.getArcZincProess(params);
	}
	
	public Map<String,Object> checkJQueue(String processType) {
		
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("submittedBy", "Qi");
		
		return mainMapper.checkJQueue(params);
	}

}
