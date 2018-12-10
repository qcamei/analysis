package net.mofancy.analysis.postgres.mapper;

import java.util.List;
import net.mofancy.analysis.postgres.persistence.GroupMember;
import net.mofancy.analysis.postgres.persistence.GroupMemberExample;
import org.apache.ibatis.annotations.Param;

public interface GroupMemberMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(GroupMember record);

    int insertSelective(GroupMember record);

    List<GroupMember> selectByExample(GroupMemberExample example);

    GroupMember selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") GroupMember record, @Param("example") GroupMemberExample example);

    int updateByExample(@Param("record") GroupMember record, @Param("example") GroupMemberExample example);

    int updateByPrimaryKeySelective(GroupMember record);

    int updateByPrimaryKey(GroupMember record);
}