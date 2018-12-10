package net.mofancy.analysis.postgres.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import net.mofancy.analysis.postgres.mapper.GroupLeaderMapper;
import net.mofancy.analysis.postgres.mapper.GroupMapper;
import net.mofancy.analysis.postgres.mapper.GroupMemberMapper;
import net.mofancy.analysis.postgres.mapper.MenuMapper;
import net.mofancy.analysis.postgres.mapper.ResourceAuthorityMapper;
import net.mofancy.analysis.postgres.mapper.UserMapper;
import net.mofancy.analysis.postgres.persistence.Group;
import net.mofancy.analysis.postgres.persistence.GroupExample;
import net.mofancy.analysis.postgres.persistence.GroupLeader;
import net.mofancy.analysis.postgres.persistence.GroupLeaderExample;
import net.mofancy.analysis.postgres.persistence.GroupMember;
import net.mofancy.analysis.postgres.persistence.GroupMemberExample;
import net.mofancy.analysis.postgres.vo.AuthorityMenuTree;
import net.mofancy.analysis.postgres.vo.GroupTree;
import net.mofancy.analysis.postgres.vo.GroupUsers;
import net.mofancy.analysis.util.TreeUtil;
import net.mofancy.analysis.web.common.exception.ParameterIllegalException;

@Service
public class GroupService {
	
	@Autowired
	private GroupMapper groupMapper;
	
	@Autowired
	private GroupMemberMapper groupMemberMapper;
	
	@Autowired
	private GroupLeaderMapper groupLeaderMapper;
	
	@Autowired
	private MenuMapper menuMapper;
	
	@Autowired
	private UserMapper userMapper;
	
	@Autowired
	private ResourceAuthorityMapper resourceAuthorityMapper;

	public List<Group> getGroupList() {
		GroupExample example = new GroupExample();
		return groupMapper.selectByExample(example);
	}

	public List<AuthorityMenuTree> getAuthorityMenu(Integer groupId) {
		List<Map<String,Object>> menus = menuMapper.selectMenuByAuthorityId(groupId, "group");
        List<AuthorityMenuTree> trees = new ArrayList<AuthorityMenuTree>();
        AuthorityMenuTree node = null;
        for (Map<String,Object> menu : menus) {
            node = new AuthorityMenuTree();
            node.setLabel(menu.get("title").toString());
            node.setIcon(menu.get("icon").toString());
            node.setId(Integer.parseInt(menu.get("id").toString()));
            node.setParentId(Integer.parseInt(menu.get("parent_id").toString()));
            
            List<Map<String,Object>> elements = getAuthorityElement(groupId,node.getId());
            node.setElements(elements);
            trees.add(node);
        }
        return TreeUtil.bulid(trees,-1);
	}

	public List<GroupTree> tree(String name, Integer groupType) {
		if(StringUtils.isEmpty(name)&&StringUtils.isEmpty(groupType)) {
            return new ArrayList<GroupTree>();
        }
        GroupExample example = new GroupExample();
        if (!StringUtils.isEmpty(name)) {
            example.createCriteria().andNameLike("%" + name + "%");
        }
        if (!StringUtils.isEmpty(groupType)) {
            example.createCriteria().andGroupTypeEqualTo(groupType);
        }
        return  getTree(groupMapper.selectByExample(example), -1);
	}
	
	private List<GroupTree> getTree(List<Group> groups,int root) {
        List<GroupTree> trees = new ArrayList<GroupTree>();
        GroupTree node = null;
        for (Group group : groups) {
            node = new GroupTree();
            node.setLabel(group.getName());
            BeanUtils.copyProperties(group, node);
            trees.add(node);
        }
        return TreeUtil.bulid(trees,root) ;
    }
	
	
	/**
     * 获取群组关联的资源
     *
     * @param groupId
     * @return
     */
    public List<Map<String,Object>> getAuthorityElement(Integer groupId,Integer menuId) {
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("authorityId", groupId);
        params.put("authorityType", "group");
        params.put("menuId", menuId);
        List<Map<String,Object>> authorities = resourceAuthorityMapper.getAuthorityElement(params);
        return authorities;
    }
    
    
    /**
     * 获取群组关联用户
     * @param groupId
     * @return
     */
    public GroupUsers getGroupUsers(int groupId) {
        return new GroupUsers(userMapper.selectMemberByGroupId(groupId),userMapper.selectLeaderByGroupId(groupId));
    }
    
    /**
     * 添加角色
     * @param group
     * @return
     */
    @Transactional
	public int saveGroupInfo(Group group) {
		return groupMapper.insertSelective(group);
	}
	
	/**
     * 修改角色信息
     * @param group
     * @return
     */
	@Transactional
	public int updateGroupInfo(Group group) {
		Group record = new Group();
		record.setDescription(group.getDescription());
		record.setName(group.getName());
		record.setId(group.getId());
		return groupMapper.updateByPrimaryKeySelective(record);
	}
	
	@Transactional
	public int deleteGroupInfo(Integer id ) {
		return groupMapper.deleteByPrimaryKey(id);
	}
	
	@Transactional
	public int saveGroupLeader(Integer groupId, Integer userId) {
		
		//查询该关系是否存在
		GroupLeaderExample example = new GroupLeaderExample();
		example.createCriteria().andGroupIdEqualTo(groupId).andUserIdEqualTo(userId);
		List<GroupLeader> list = groupLeaderMapper.selectByExample(example);
		if(list!=null&&list.size()>0) {
			throw new ParameterIllegalException("该用户已经存在！");
		}
		
		GroupLeader record = new GroupLeader();
		record.setGroupId(groupId);
		record.setUserId(userId);
		return groupLeaderMapper.insertSelective(record);
	}
	
	@Transactional
	public int saveGroupMember(Integer groupId, Integer userId) {
		
		//查询该关系是否存在
		GroupMemberExample example = new GroupMemberExample();
		example.createCriteria().andGroupIdEqualTo(groupId).andUserIdEqualTo(userId);
		List<GroupMember> list = groupMemberMapper.selectByExample(example);
		if(list!=null&&list.size()>0) {
			throw new ParameterIllegalException("该用户已经存在！");
		}
		
		GroupMember record = new GroupMember();
		record.setGroupId(groupId);
		record.setUserId(userId);
		return groupMemberMapper.insertSelective(record);
	}
	
	@Transactional
	public int deleteGroupLeader(Integer groupId, Integer userId) {
		//查询该关系是否存在
		GroupLeaderExample example = new GroupLeaderExample();
		example.createCriteria().andGroupIdEqualTo(groupId).andUserIdEqualTo(userId);
		List<GroupLeader> list = groupLeaderMapper.selectByExample(example);
		if(list!=null&&list.size()==0) {
			throw new ParameterIllegalException("该用户已经被删除！");
		}
		
		GroupLeader record = list.get(0);
		return groupLeaderMapper.deleteByPrimaryKey(record.getId());
		
	}
	
	@Transactional
	public int deleteGroupMember(Integer groupId, Integer userId) {
		//查询该关系是否存在
		GroupMemberExample example = new GroupMemberExample();
		example.createCriteria().andGroupIdEqualTo(groupId).andUserIdEqualTo(userId);
		List<GroupMember> list = groupMemberMapper.selectByExample(example);
		if(list!=null&&list.size()==0) {
			throw new ParameterIllegalException("该用户已经被删除！");
		}
		
		GroupMember record = list.get(0);
		return groupMemberMapper.deleteByPrimaryKey(record.getId());
		
	}
	
}
