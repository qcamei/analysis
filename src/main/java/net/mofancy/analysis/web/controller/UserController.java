package net.mofancy.analysis.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.mofancy.analysis.postgres.persistence.User;
import net.mofancy.analysis.postgres.service.UserService;
import net.mofancy.analysis.postgres.vo.PermissionInfo;
import net.mofancy.analysis.service.PermissionService;
import net.mofancy.analysis.web.common.ApiResponse;

@RestController
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserService userService;
	
	@Autowired
	private PermissionService permissionService;

	
	@RequestMapping(value = "/getUserList")
	public ApiResponse getUserList() {
		List<User> list = userService.getUserList();
        return ApiResponse.buildSuccess(list);
	}
	
	@RequestMapping(value = "/permissions")
	public List<PermissionInfo> getAllPermission(){
        return permissionService.getAllPermission();
    }
   
    @RequestMapping(value = "/getPermissionByUserid")
    public List<PermissionInfo> getPermissionByUserid(Integer userid){
        return permissionService.getPermissionByUserid(userid);
    }
}
