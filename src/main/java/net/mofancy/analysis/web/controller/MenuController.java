package net.mofancy.analysis.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.mofancy.analysis.postgres.persistence.Menu;
import net.mofancy.analysis.postgres.service.MenuService;
import net.mofancy.analysis.postgres.vo.AuthorityMenuTree;
import net.mofancy.analysis.postgres.vo.MenuTree;
import net.mofancy.analysis.web.common.ApiResponse;

@RestController
@RequestMapping("/menu")
public class MenuController {

	@Autowired
	private MenuService menuService;

	@RequestMapping(value = "/listMenu")
    public ApiResponse listMenu(Integer parentId) {
		List<MenuTree> list = menuService.listMenu(parentId);
        return ApiResponse.buildSuccess(list);
    }
	
	@RequestMapping(value = "/getMenuList")
    public ApiResponse getMenuList(Integer parentId) {
		List<MenuTree> list = menuService.getMenuList(parentId);
        return ApiResponse.buildSuccess(list);
    }

    @RequestMapping(value = "/authorityTree")
    public ApiResponse listAuthorityMenu() {
    	List<AuthorityMenuTree> list = menuService.listAuthorityMenu();
    	return ApiResponse.buildSuccess(list);
    }

    @RequestMapping(value = "/user/authorityTree")
    public ApiResponse listUserAuthorityMenu(Integer parentId){
    	Integer userId = 1;
    	List<MenuTree> list = menuService.listUserAuthorityMenu(userId,parentId);
    	return ApiResponse.buildSuccess(list);
    }

    @RequestMapping(value = "/listUserAuthoritySystem")
    public ApiResponse listUserAuthoritySystem() {
    	Integer userId = 1;
    	List<Menu> list = menuService.listUserAuthoritySystem(userId);
    	return ApiResponse.buildSuccess(list);
    }

   
}
