package net.mofancy.analysis.postgres.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface FamilyMapper {
	
    List<Map<String,Object>> getFamilys (@Param("projectId")int projectId);
    
    List<Map<String,Object>> getDigitalList(@Param("projectId")int projectId);
    
    List<Map<String, Object>> getTopGoods(@Param("projectId")int projectId,@Param("parentKey") int parentKey);

	List<Map<String, Object>> getDistributionList(@Param("projectId")int projectId,@Param("parentKey") int parentKey);

	List<Map<String, Object>> getSolutionsList(@Param("projectId")int projectId,@Param("parentKey")int parentKey);
	
	List<Map<String, Object>> getSolutionsList2(@Param("projectId")int projectId,@Param("parentKey")int parentKey,@Param("g")int g);
	
	List<Map<String, Object>> getSolutionsList3(@Param("projectId")int projectId,@Param("solutionKey")int solutionKey);
	
	List<Map<String, Object>> getSolutionsList4(Map<String,Object> params);
	
	List<Map<String, Object>> selectThemeList(@Param("projectId")int projectId);
	
	List<Map<String, Object>> selectThemeDistributionList(@Param("projectId")int projectId);

	int getClusterProjectParam(Map<String, Object> params);

	Integer getClusterKeySeq();

	int saveClusterProjectParam(Map<String, Object> params);

	int saveProdClusteredGroup(Map<String, Object> params);

	int saveProdClusterSku(Map<String, Object> params);

	int saveClusterProjectParam2(Map<String, Object> params);

	int deleteClusterProjectParam(Map<String, Object> params);

	int deleteProdClusteredGroup(Map<String, Object> params);

	int deleteProdClusterSku(Map<String, Object> params);

	int deleteClusterProjectParam2(Map<String, Object> params);

	int updateSolutionName(Map<String, Object> params);

	int deleteProdClusterSku2(Map<String, Object> params);

	int updateGroupName(Map<String, Object> params);

	List<Map<String, Object>> exportExcel(Map<String, Object> params);

}
