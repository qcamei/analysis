package net.mofancy.analysis.postgres.mapper;

import java.util.List;
import net.mofancy.analysis.postgres.persistence.Element;
import net.mofancy.analysis.postgres.persistence.ElementExample;
import org.apache.ibatis.annotations.Param;

public interface ElementMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Element record);

    int insertSelective(Element record);

    List<Element> selectByExample(ElementExample example);

    Element selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") Element record, @Param("example") ElementExample example);

    int updateByExample(@Param("record") Element record, @Param("example") ElementExample example);

    int updateByPrimaryKeySelective(Element record);

    int updateByPrimaryKey(Element record);

    List<Element> selectAuthorityElementByUserId(@Param("userId")Integer userId);
    
    List<Element> selectAuthorityMenuElementByUserId(@Param("userId")Integer userId,@Param("menuId")Integer menuId);

	List<Element> getAuthorityElementByUserId(@Param("userId")Integer userId);

	List<Element> selectAuthorityElementByMenuId(Integer menuId);
    
}