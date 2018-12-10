package net.mofancy.analysis.postgres.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.mofancy.analysis.postgres.mapper.ResourceAuthorityMapper;
import net.mofancy.analysis.postgres.persistence.ResourceAuthority;

@Service
public class ResourceAuthorityService {
	
	@Autowired
	private ResourceAuthorityMapper resourceAuthorityMapper;


	public int allotResourceAuthority(ResourceAuthority resourceAuthority,Integer sign) {
		if(sign==1) {//添加权限
			if(resourceAuthority.getId()==null) {
				return resourceAuthorityMapper.insertSelective(resourceAuthority);
				 
			}
			
		} else if (sign==0) {//删除权限
			if(resourceAuthority.getId()!=null) {
				resourceAuthorityMapper.deleteByPrimaryKey(resourceAuthority.getId());
			}
		}
		
		return 0;
		
	}

	

}
