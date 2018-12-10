package net.mofancy.analysis.postgres.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.mofancy.analysis.postgres.mapper.UserMapper;
import net.mofancy.analysis.postgres.persistence.User;
import net.mofancy.analysis.postgres.persistence.UserExample;

@Service
public class UserService {
	
	@Autowired
	private UserMapper userMapper;

	public List<User> getUserList() {
		UserExample example = new UserExample();
		return userMapper.selectByExample(example);
	}

	public User getUserByUserById(Integer userid) {
		return userMapper.selectByPrimaryKey(userid);
	}

	

}
