package net.mofancy.analysis.postgres.mapper;

import java.util.List;
import java.util.Map;

public interface ProjectMapper {
	
    List<Map<String,Object>> selectListByCondition(Map<String,Object> record);
    
    List<Map<String,Object>> getProjectById(Map<String, Object> params);

	int checkProjectExist(String projName);

	int deleteProjectParam(int projectId);

	int saveNewProject(Map<String, Object> params);

	int saveNewProjectParam(Map<String, Object> params);
	
	int createNewProjCustomer(Map<String, Object> params);
    
	int getProjectId();

	int getJobId();

	Map<String,Object> getProjectParam(Map<String, Object> params);

	List<Map<String, Object>> getGoodsList(Map<String, Object> params);

	List<Map<String, Object>> deptDataGrid(Map<String, Object> params);


}
