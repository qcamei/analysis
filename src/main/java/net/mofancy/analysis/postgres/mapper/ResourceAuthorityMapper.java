package net.mofancy.analysis.postgres.mapper;

import java.util.List;
import java.util.Map;

import net.mofancy.analysis.postgres.persistence.ResourceAuthority;
import net.mofancy.analysis.postgres.persistence.ResourceAuthorityExample;
import org.apache.ibatis.annotations.Param;

public interface ResourceAuthorityMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ResourceAuthority record);

    int insertSelective(ResourceAuthority record);

    List<ResourceAuthority> selectByExampleWithBLOBs(ResourceAuthorityExample example);

    List<ResourceAuthority> selectByExample(ResourceAuthorityExample example);

    ResourceAuthority selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") ResourceAuthority record, @Param("example") ResourceAuthorityExample example);

    int updateByExampleWithBLOBs(@Param("record") ResourceAuthority record, @Param("example") ResourceAuthorityExample example);

    int updateByExample(@Param("record") ResourceAuthority record, @Param("example") ResourceAuthorityExample example);

    int updateByPrimaryKeySelective(ResourceAuthority record);

    int updateByPrimaryKeyWithBLOBs(ResourceAuthority record);

    int updateByPrimaryKey(ResourceAuthority record);

	List<Map<String, Object>> getAuthorityElement(Map<String, Object> params);
}