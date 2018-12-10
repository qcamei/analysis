package net.mofancy.analysis.postgres.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.mofancy.analysis.postgres.mapper.GroupTypeMapper;
import net.mofancy.analysis.postgres.persistence.GroupType;
import net.mofancy.analysis.postgres.persistence.GroupTypeExample;

@Service
public class GroupTypeService {
	
	@Autowired
	private GroupTypeMapper groupTypeMapper;

	public List<GroupType> getGroupTypeList() {
		GroupTypeExample example = new GroupTypeExample();
		return groupTypeMapper.selectByExample(example);
	}

	

}
