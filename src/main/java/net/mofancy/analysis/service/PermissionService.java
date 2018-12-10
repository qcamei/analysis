package net.mofancy.analysis.service;



import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import net.mofancy.analysis.constant.CommonConstant;
import net.mofancy.analysis.postgres.persistence.Element;
import net.mofancy.analysis.postgres.persistence.Menu;
import net.mofancy.analysis.postgres.persistence.User;
import net.mofancy.analysis.postgres.service.ElementService;
import net.mofancy.analysis.postgres.service.MenuService;
import net.mofancy.analysis.postgres.service.UserService;
import net.mofancy.analysis.postgres.vo.FrontUser;
import net.mofancy.analysis.postgres.vo.MenuTree;
import net.mofancy.analysis.postgres.vo.PermissionInfo;
import net.mofancy.analysis.util.AuthenticateUtils;
import net.mofancy.analysis.util.TreeUtil;

/**
 * Created by ace on 2017/9/12.
 */
@Service
public class PermissionService {
    @Autowired
    private UserService userService;
    @Autowired
    private MenuService menuService;
    @Autowired
    private ElementService elementService;
    

   

    public List<PermissionInfo> getAllPermission() {
        List<Menu> menus = menuService.selectListAll();
        List<PermissionInfo> result = new ArrayList<PermissionInfo>();
        menu2permission(menus, result);
        List<Element> elements = elementService.selectListAll();
        element2permission(result, elements);
        return result;
    }

    private void menu2permission(List<Menu> menus, List<PermissionInfo> result) {
        PermissionInfo info;
        for (Menu menu : menus) {
            if (StringUtils.isEmpty(menu.getHref())) {
                menu.setHref("/" + menu.getCode());
            }
            info = new PermissionInfo();
            info.setCode(menu.getCode());
            info.setType(CommonConstant.RESOURCE_TYPE_MENU);
            info.setName(CommonConstant.RESOURCE_ACTION_VISIT);
            String uri = menu.getHref();
            info.setUri(uri);
            info.setMethod(CommonConstant.RESOURCE_REQUEST_METHOD_GET);
            result.add(info);
            info.setMenu(menu.getTitle());
        }
    }

    public List<PermissionInfo> getPermissionByUserid(Integer userid) {
        User user = userService.getUserByUserById(userid);
        List<Menu> menus = menuService.getUserAuthorityMenuByUserId(user.getId());
        List<PermissionInfo> result = new ArrayList<PermissionInfo>();
        menu2permission(menus, result);
        List<Element> elements = elementService.getAuthorityElementByUserId(user.getId());
        element2permission(result, elements);
        return result;
    }

    private void element2permission(List<PermissionInfo> result, List<Element> elements) {
        PermissionInfo info;
        for (Element element : elements) {
            info = new PermissionInfo();
            info.setCode(element.getCode());
            info.setType(element.getType());
            info.setUri(element.getUri());
            info.setMethod(element.getMethod());
            info.setName(element.getName());
            info.setMenu(element.getMenuId()+"");
            result.add(info);
        }
    }


    private List<MenuTree> getMenuTree(List<Menu> menus, int root) {
        List<MenuTree> trees = new ArrayList<MenuTree>();
        MenuTree node = null;
        for (Menu menu : menus) {
            node = new MenuTree();
            BeanUtils.copyProperties(menu, node);
            trees.add(node);
        }
        return TreeUtil.bulid(trees, root);
    }

    public FrontUser getUserInfo(String token) throws Exception {
        
    	Integer userid = AuthenticateUtils.getUserId(token);
        User user = userService.getUserByUserById(userid);
        FrontUser frontUser = new FrontUser();
        BeanUtils.copyProperties(user, frontUser);
        List<PermissionInfo> permissionInfos = this.getPermissionByUserid(userid);
        Stream<PermissionInfo> menus = permissionInfos.parallelStream().filter((permission) -> {
            return permission.getType().equals(CommonConstant.RESOURCE_TYPE_MENU);
        });
        frontUser.setMenus(menus.collect(Collectors.toList()));
        Stream<PermissionInfo> elements = permissionInfos.parallelStream().filter((permission) -> {
            return !permission.getType().equals(CommonConstant.RESOURCE_TYPE_MENU);
        });
        frontUser.setElements(elements.collect(Collectors.toList()));
        return frontUser;
    }

    public List<MenuTree> getMenusByUsername(String token) throws Exception {
       Integer userid = AuthenticateUtils.getUserId(token);
       User user = userService.getUserByUserById(userid);
       List<Menu> menus = menuService.getUserAuthorityMenuByUserId(user.getId());
       return getMenuTree(menus,CommonConstant.ROOT);
    }
}
