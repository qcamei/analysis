package net.mofancy.analysis.postgres.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface ThemeMapper {
	
	
	List<Map<String, Object>> getThemeList(Integer projectId);
	
	List<Map<String, Object>> getDistributionList(Integer projectId);

	List<Map<String, Object>> getAnalyses(Integer projectId);
	
	List<Map<String, Object>> getNumFactor(Map<String,Object> params);
	
	List<Map<String, Object>> getThemes2(@Param("projectId")Integer projectId,@Param("parentKey")Integer parentKey);
	@Select("SELECT nextval('sub_proj_seq')")
	Integer getAnalysisKey();

	List<Map<String, Object>> purchaseThemes(Map<String,Object> params);

	List<Map<String, Object>> getFactorResult(Map<String,Object> params);

	List<Map<String, Object>> getSavedSolutions(Integer projectId);

	List<String> getFactorName(Map<String, Object> params);

	int checkFactorName(Map<String,Object> params);

	int updateFactorName(Map<String, Object> params);

	int saveFactorName(Map<String, Object> params);

	List<Map<String, Object>> purchaseThemes2(Map<String, Object> params);

	int saveFactorSolution(Map<String, Object> params);

	int deleteClusterFactor(Map<String, Object> params);

	int saveClusterFactor(Map<String, Object> params);

	int checkFactorSolution(Map<String, Object> params);

	int deleteCustomerSegments(Map<String, Object> params);

	int deleteCustomerNormalProfile(Map<String, Object> params);

	int deleteProjectParam(Map<String, Object> params);

	int deleteCorrelationVector(Map<String, Object> params);

	int deleteProdAssociatedGroup(Map<String, Object> params);

	int deleteProdClusteredGroup(Map<String, Object> params);

	int dropCustomerClusterSummary(Map<String, Object> params);

	int deleteCustomerClusterSummary(Map<String, Object> params);

	int deleteClusterFactor2(Map<String, Object> params);

	int deleteProjectParam2(Map<String, Object> params);

	int deleteProjectParam3(Map<String, Object> params);

	int deleteProjectParam4(Map<String, Object> params);

	int deleteClusterFactor3(Map<String, Object> params);

	int deleteProjectParam5(Map<String, Object> params);

	int checkFactorSolution2(Map<String, Object> params);

	int checkSavedSolutions(Map<String, Object> params);

	Map<String,Object> getSavedSolutionsById(Map<String, Object> params);

	List<Map<String, Object>> getDeptDistribution(Map<String, Object> params);

	List<Map<String, Object>> getClusterDistribution(Map<String, Object> params);

	int getSubProjSeq();

	List<Map<String, Object>> getAllSavedProfileKey(Map<String,Object> params);

}
