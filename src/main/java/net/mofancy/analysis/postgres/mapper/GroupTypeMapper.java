package net.mofancy.analysis.postgres.mapper;

import java.util.List;
import net.mofancy.analysis.postgres.persistence.GroupType;
import net.mofancy.analysis.postgres.persistence.GroupTypeExample;
import org.apache.ibatis.annotations.Param;

public interface GroupTypeMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(GroupType record);

    int insertSelective(GroupType record);

    List<GroupType> selectByExample(GroupTypeExample example);

    GroupType selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") GroupType record, @Param("example") GroupTypeExample example);

    int updateByExample(@Param("record") GroupType record, @Param("example") GroupTypeExample example);

    int updateByPrimaryKeySelective(GroupType record);

    int updateByPrimaryKey(GroupType record);
}