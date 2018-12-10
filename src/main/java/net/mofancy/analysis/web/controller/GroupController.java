package net.mofancy.analysis.web.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import net.mofancy.analysis.postgres.persistence.Group;
import net.mofancy.analysis.postgres.service.GroupService;
import net.mofancy.analysis.postgres.vo.AuthorityMenuTree;
import net.mofancy.analysis.postgres.vo.GroupTree;
import net.mofancy.analysis.postgres.vo.GroupUsers;
import net.mofancy.analysis.web.common.ApiResponse;
import net.mofancy.analysis.web.common.exception.ParameterIllegalException;

@RestController
@RequestMapping("/group")
public class GroupController {

	@Autowired
	private GroupService groupService;

	
	@RequestMapping(value = "/getGroupList")
	public ApiResponse getGroupList() {
		List<Group> list = groupService.getGroupList();
        return ApiResponse.buildSuccess(list);
	}
   
	
	@RequestMapping(value = "/getAuthorityMenu", method = RequestMethod.POST)
    public ApiResponse getMenuAuthority(Integer id){
		List<AuthorityMenuTree> list = groupService.getAuthorityMenu(id);
        return ApiResponse.buildSuccess(list);
    }
	
	@RequestMapping(value = "/tree", method = RequestMethod.POST)
    public ApiResponse tree(String name,Integer groupType) {
        List<GroupTree> list = groupService.tree(name,groupType);
        return  ApiResponse.buildSuccess(list);
    }
	
	@RequestMapping(value = "/getAuthorityElement", method = RequestMethod.POST)
	public ApiResponse getAuthorityElement(Integer groupId,Integer menuId) {
		List<Map<String,Object>> list = groupService.getAuthorityElement(groupId,menuId);
        return  ApiResponse.buildSuccess(list);
	}
	
	@RequestMapping(value = "/getGroupUsers", method = RequestMethod.POST)
	public ApiResponse getGroupUsers(Integer groupId) {
		GroupUsers groupUsers = groupService.getGroupUsers(groupId);
        return  ApiResponse.buildSuccess(groupUsers);
	}
	
	@RequestMapping(value = "/saveGroupInfo", method = RequestMethod.POST)
	public ApiResponse saveGroupInfo(Group group) {
		groupService.saveGroupInfo(group);
        return  ApiResponse.buildSuccess("角色添加成功！");
	}
	
	@RequestMapping(value = "/updateGroupInfo", method = RequestMethod.POST)
	public ApiResponse updateGroupInfo(Group group) {
		groupService.updateGroupInfo(group);
        return  ApiResponse.buildSuccess("角色修改成功！");
	}
	
	@RequestMapping(value = "/deleteGroupInfo", method = RequestMethod.POST)
	public ApiResponse deleteGroupInfo(Integer id) {
		if(StringUtils.isEmpty(id)) {
			throw new ParameterIllegalException("角色ID不能为空!");
		}
		groupService.deleteGroupInfo(id);
        return  ApiResponse.buildSuccess("角色删除成功！");
	}
	
	@RequestMapping(value = "/saveGroupLeader", method = RequestMethod.POST)
	public ApiResponse saveGroupLeader(Integer groupId,Integer userId) {
		
		if(StringUtils.isEmpty(groupId)) {
			throw new ParameterIllegalException("角色ID不能为空!");
		}
		
		if(StringUtils.isEmpty(userId)) {
			throw new ParameterIllegalException("用户ID不能为空!");
		}
		
		groupService.saveGroupLeader(groupId,userId);
		return  ApiResponse.buildSuccess("保存成功！");
	}
	
	@RequestMapping(value = "/saveGroupMember", method = RequestMethod.POST)
	public ApiResponse saveGroupMember(Integer groupId,Integer userId) {
		
		if(StringUtils.isEmpty(groupId)) {
			throw new ParameterIllegalException("角色ID不能为空!");
		}
		
		if(StringUtils.isEmpty(userId)) {
			throw new ParameterIllegalException("用户ID不能为空!");
		}
		
		groupService.saveGroupMember(groupId,userId);
		return  ApiResponse.buildSuccess("保存成功！");
	}
	
	@RequestMapping(value = "/deleteGroupLeader", method = RequestMethod.POST)
	public ApiResponse deleteGroupLeader(Integer groupId,Integer userId) {
		
		if(StringUtils.isEmpty(groupId)) {
			throw new ParameterIllegalException("角色ID不能为空!");
		}
		
		if(StringUtils.isEmpty(userId)) {
			throw new ParameterIllegalException("用户ID不能为空!");
		}
		
		groupService.deleteGroupLeader(groupId,userId);
		return  ApiResponse.buildSuccess("删除成功！");
	}
	
	@RequestMapping(value = "/deleteGroupMember", method = RequestMethod.POST)
	public ApiResponse deleteGroupMember(Integer groupId,Integer userId) {
		
		if(StringUtils.isEmpty(groupId)) {
			throw new ParameterIllegalException("角色ID不能为空!");
		}
		
		if(StringUtils.isEmpty(userId)) {
			throw new ParameterIllegalException("用户ID不能为空!");
		}
		
		groupService.deleteGroupMember(groupId,userId);
		return  ApiResponse.buildSuccess("删除成功！");
	}
}
