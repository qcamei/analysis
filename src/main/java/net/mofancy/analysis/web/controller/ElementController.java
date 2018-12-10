package net.mofancy.analysis.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.mofancy.analysis.postgres.persistence.Element;
import net.mofancy.analysis.postgres.service.ElementService;
import net.mofancy.analysis.postgres.vo.PageData;
import net.mofancy.analysis.web.common.ApiResponse;

@RestController
@RequestMapping("/element")
public class ElementController {

	@Autowired
	private ElementService elementService;

	@RequestMapping(value = "/getElementList")
	public ApiResponse getElementList(Integer pageSize, Integer pageNum,String name, Integer menuId) {
		PageData pageData = elementService.getElementList(pageSize, pageNum,name,menuId);
		return ApiResponse.buildSuccess(pageData);
	}

	@RequestMapping(value = "/selectAuthorityElementByUserId")
	public ApiResponse selectAuthorityElementByUserId() {
		Integer userId = 1;
		List<Element> list = elementService.selectAuthorityElementByUserId(userId);
		return ApiResponse.buildSuccess(list);
	}
	
	@RequestMapping(value = "/selectAuthorityElementByMenuId")
	public ApiResponse selectAuthorityElementByMenuId(Integer menuId) {
		List<Element> list = elementService.selectAuthorityElementByMenuId(menuId);
		return ApiResponse.buildSuccess(list);
	}
}
