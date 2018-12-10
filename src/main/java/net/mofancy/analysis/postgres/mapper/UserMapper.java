package net.mofancy.analysis.postgres.mapper;

import java.util.List;
import net.mofancy.analysis.postgres.persistence.User;
import net.mofancy.analysis.postgres.persistence.UserExample;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    List<User> selectByExample(UserExample example);

    User selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") User record, @Param("example") UserExample example);

    int updateByExample(@Param("record") User record, @Param("example") UserExample example);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);
    
    List<User> selectMemberByGroupId(@Param("groupId") int groupId);
    
    List<User> selectLeaderByGroupId(@Param("groupId") int groupId);
}