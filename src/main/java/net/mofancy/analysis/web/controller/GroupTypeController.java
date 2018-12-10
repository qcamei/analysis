package net.mofancy.analysis.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.mofancy.analysis.postgres.persistence.GroupType;
import net.mofancy.analysis.postgres.service.GroupTypeService;
import net.mofancy.analysis.web.common.ApiResponse;

@RestController
@RequestMapping("/groupType")
public class GroupTypeController {

	@Autowired
	private GroupTypeService groupTypeService;

	
	@RequestMapping(value = "/getGroupTypeList")
	public ApiResponse getGroupTypeList() {
		List<GroupType> list = groupTypeService.getGroupTypeList();
        return ApiResponse.buildSuccess(list);
	}
   
}
