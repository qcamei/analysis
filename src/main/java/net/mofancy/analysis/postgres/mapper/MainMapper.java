package net.mofancy.analysis.postgres.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface MainMapper {
	
	
	List<Map<String, Object>> getThemeList(@Param("projectId")int projectId);

	List<Map<String, Object>> getArcZincProess(Map<String,Object> params);

	Map<String, Object> checkJQueue(Map<String, Object> params);
	
}
