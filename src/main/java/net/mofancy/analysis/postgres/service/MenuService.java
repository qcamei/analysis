package net.mofancy.analysis.postgres.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.mofancy.analysis.postgres.mapper.MenuMapper;
import net.mofancy.analysis.postgres.persistence.Menu;
import net.mofancy.analysis.postgres.persistence.MenuExample;
import net.mofancy.analysis.postgres.vo.AuthorityMenuTree;
import net.mofancy.analysis.postgres.vo.MenuTree;
import net.mofancy.analysis.util.TreeUtil;

@Service
public class MenuService {
	
	@Autowired
	private MenuMapper menuMapper;

	public List<MenuTree> listMenu(Integer parentId) {
        MenuExample example = new MenuExample();
        return getMenuTree(menuMapper.selectByExample(example), -1);
	}
	
	
	private List<MenuTree> getMenuTree(List<Menu> menus,int root) {
        List<MenuTree> trees = new ArrayList<MenuTree>();
        MenuTree node = null;
        for (Menu menu : menus) {
            node = new MenuTree();
            BeanUtils.copyProperties(menu, node);
            trees.add(node);
        }
        return TreeUtil.bulid(trees,root) ;
    }


	public List<AuthorityMenuTree> listAuthorityMenu() {
		List<AuthorityMenuTree> trees = new ArrayList<AuthorityMenuTree>();
        AuthorityMenuTree node = null;
        MenuExample example = new MenuExample();
        for (Menu menu : menuMapper.selectByExample(example)) {
            node = new AuthorityMenuTree();
            node.setLabel(menu.getTitle());
            BeanUtils.copyProperties(menu, node);
            trees.add(node);
        }
        return TreeUtil.bulid(trees,-1);
	}


	public List<MenuTree> listUserAuthorityMenu(Integer userId, Integer parentId) {
		return getMenuTree(menuMapper.selectAuthorityMenuByUserId(userId),parentId);
	}


	public List<Menu> listUserAuthoritySystem(Integer userId) {
		
		return menuMapper.selectAuthoritySystemByUserId(userId);
	}


	public List<MenuTree> getMenuList(Integer parentId) {
		MenuExample example = new MenuExample();
        return getMenuTree(menuMapper.selectByExample(example), -1);
	}


	public List<Menu> selectListAll() {
		MenuExample example = new MenuExample();
		return menuMapper.selectByExample(example);
	}


	public List<Menu> getUserAuthorityMenuByUserId(Integer id) {
		
		return menuMapper.selectAuthorityMenuByUserId(id);
	}
	

}
