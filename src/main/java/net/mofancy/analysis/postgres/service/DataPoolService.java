package net.mofancy.analysis.postgres.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.mofancy.analysis.postgres.mapper.DataPoolMapper;

@Service
@Transactional(readOnly = true)
public class DataPoolService {
	@Autowired
	private DataPoolMapper dataPoolMapper;
	
	public List<Map<String,Object>> selectListByCondition(Map<String,Object> record) {
		return dataPoolMapper.selectListByCondition(record);
	}
	
	
}
