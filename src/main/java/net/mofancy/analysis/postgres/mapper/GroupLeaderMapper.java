package net.mofancy.analysis.postgres.mapper;

import java.util.List;
import net.mofancy.analysis.postgres.persistence.GroupLeader;
import net.mofancy.analysis.postgres.persistence.GroupLeaderExample;
import org.apache.ibatis.annotations.Param;

public interface GroupLeaderMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(GroupLeader record);

    int insertSelective(GroupLeader record);

    List<GroupLeader> selectByExample(GroupLeaderExample example);

    GroupLeader selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") GroupLeader record, @Param("example") GroupLeaderExample example);

    int updateByExample(@Param("record") GroupLeader record, @Param("example") GroupLeaderExample example);

    int updateByPrimaryKeySelective(GroupLeader record);

    int updateByPrimaryKey(GroupLeader record);
}