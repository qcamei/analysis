package net.mofancy.analysis.postgres.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import net.mofancy.analysis.postgres.mapper.ElementMapper;
import net.mofancy.analysis.postgres.persistence.Element;
import net.mofancy.analysis.postgres.persistence.ElementExample;
import net.mofancy.analysis.postgres.persistence.ElementExample.Criteria;
import net.mofancy.analysis.postgres.vo.PageData;

@Service
public class ElementService {
	@Autowired
	private ElementMapper elementMapper;
	
	public PageData getElementList(Integer pageSize, Integer pageNum, String name, Integer menuId) {
		
		PageHelper.startPage(pageNum, pageSize);
		
		ElementExample example = new ElementExample();
		Criteria criteria = example.createCriteria();
		criteria.andMenuIdEqualTo(menuId);
	    if(StringUtils.isEmpty(name)){
	      criteria.andNameLike("%"+name+"%");
	    }
	    List<Element> list = elementMapper.selectByExample(example);
	    
	    PageData  pageData=new PageData();
		PageInfo<Element> pageInfo=new PageInfo<Element>(list);  
		pageData.setTotal(pageInfo.getTotal());
		pageData.setPageNum(pageInfo.getPageNum());
		pageData.setPageSize(pageInfo.getPageSize());
		pageData.setDataList(list);
	    
		return pageData;
	}
	
	public List<Element> selectAuthorityElementByUserId(Integer userId) {
		return elementMapper.selectAuthorityElementByUserId(userId);
	}

	public List<Element> selectAuthorityElementByMenuId(Integer menuId) {
		return elementMapper.selectAuthorityElementByMenuId(menuId);
	}

	public List<Element> selectListAll() {
		ElementExample example = new ElementExample();
		return elementMapper.selectByExample(example);
	}

	public List<Element> getAuthorityElementByUserId(Integer userId) {
		return elementMapper.getAuthorityElementByUserId(userId);
	}
	
	

}
