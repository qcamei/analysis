package net.mofancy.analysis.postgres.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import CustomJobs.RunYulesQJob;
import QueueManager.RunMultiSqlJob;
import net.mofancy.analysis.config.DataSourceContextHolder;
import net.mofancy.analysis.config.DatabaseConfig;
import net.mofancy.analysis.postgres.mapper.ProjectMapper;
import net.mofancy.analysis.postgres.segment.JobDBSegment;
import net.mofancy.analysis.postgres.segment.JqueueSegment;
import net.mofancy.analysis.postgres.vo.PageData;
import net.mofancy.analysis.util.JdbcUtils;
import net.mofancy.analysis.util.JobUtils;
import net.mofancy.analysis.web.common.exception.ParameterIllegalException;

@Service
public class ProjectService {
	
	@Autowired
	private ProjectMapper projectMapper;
	
	
	public PageData selectListByCondition(Integer id, Integer pageNum,Integer pageSize,String projectName) {
		PageHelper.startPage(pageNum, pageSize);
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		
		Set<String> set=DatabaseConfig.dbMap.keySet();
        for (String s : set) {
        	if(id!=null&&!id.toString().equals(s)) {
        		continue;
        	}
        	DataSourceContextHolder.setDB(DatabaseConfig.dbMap.get(s));
			Map<String,Object> params = new HashMap<String,Object>();
			params.put("datasetKey", s);
			params.put("datasetName", DatabaseConfig.dbMap.get(s).toString());
			if(!StringUtils.isEmpty(projectName)) {
				params.put("projectName", projectName);
			}
			list.addAll(projectMapper.selectListByCondition(params));
        }
        
        long total = 0;
        if(id==null) {
        	total = list.size();
        	int start = (pageNum-1)*pageSize;
        	start = start<0?0:start;
            int end = pageNum*pageSize;
            list = list.subList(start, end>list.size()?list.size():end);
        }
        
		
		PageData  pageData=new PageData();
		PageInfo<Map<String,Object>> pageInfo=new PageInfo<Map<String,Object>>(list);  
		pageData.setTotal(total>0?total:pageInfo.getTotal());
		pageData.setPageNum(pageInfo.getPageNum());
		pageData.setPageSize(pageInfo.getPageSize());
		pageData.setDataList(list);
		return pageData;
	}


	public List<Map<String, Object>> getProjectById(Integer datasetKey,Integer projectId) {
		String datasetName = DatabaseConfig.dbMap.get(datasetKey.toString()).toString();
		
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("datasetName", datasetName);
		params.put("projectId", projectId);
		return projectMapper.getProjectById(params);
	}
	
	public void deleteProject(int projectId,Integer datasetKey) {
		String[] params = {"CustomerSegmentsTable","CustomerClusterSummaryTable","CustomerNormalProfileTable","CustomerSummaryTable","ProductMatrixTable","ProductSegmentSummaryTable","ProdClusterSKUTable","CustomerDeploySegmentsTable"};
		String[] proj_dt = new String[params.length];
		for (int i = 0;i<params.length; i++) {
			String charVal = null;
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("paramName",params[i]);
			map.put("projectId",projectId);
			map = projectMapper.getProjectParam(map);
			if(map!=null) {
				charVal = map.get("char_val").toString();
			}
			proj_dt[i] = charVal;
		}
		
		int jobID = JdbcUtils.getJobId();
		int multiProcess = 1;
		RunMultiSqlJob job = new RunMultiSqlJob(jobID);
		JqueueSegment jqueue = new JqueueSegment("Qi", datasetKey, projectId+"", "deleteProject", "0",job);
		JobDBSegment jobDBSegment = new JobDBSegment(jqueue);
		
		jobDBSegment.deleteProject(projectId,proj_dt,multiProcess);
		
	}


	@Transactional
	public void saveProject(Map<String, String> map,Integer datasetKey) {
		
		String projName = map.get("projName").toString(); //相当于newProjFromPoolNameTextBox的值
		String projDesc = map.get("projDesc").toString(); //相当于newProjFromPoolDescTextBox的值
		String minCust = "0";//map.get("minCust").toString();   //相当于newProjFromPoolMinCustDropDownList的值
		String minSale = "0.001";//map.get("minSale").toString();   //相当于newProjFromPoolMinSaleDropDownList的值
		String period = map.get("period").toString();
		String goodsLevel = map.get("goodsLevel").toString();
		//检测该project_name是否存在
		if(projectMapper.checkProjectExist(projName)>0) {
			//该沙盘名字已存在!
			throw new ParameterIllegalException("该沙盘名字已存在!");
		}
		
		//新增沙盘
		Map<String,Object> params = new HashMap<String,Object>();
		int projectId = projectMapper.getProjectId();
		
		ArrayList<String[]> p = new ArrayList<String[]>();
		p = JobUtils.AddNewProjParam(p, "Period", "", period, "");
//		p = JobUtils.AddNewProjParam(p, "", "Customers", "Customers", "All cusomters");
		p = JobUtils.AddNewProjParam(p, "PseudoDept", "TRIPPS department", goodsLevel, "Hier1");
		p = JobUtils.AddNewProjParam(p, "PseudoProd", "TRIPPS product", "1", "SKUs");
		p = JobUtils.AddNewProjParam(p, "Restrict", "Restrict by", "#ALL", "All");
		p = JobUtils.AddNewProjParam(p, "Store", "Stores", "#ALL", "All Stores");
		p = JobUtils.AddNewProjParam(p, "MinCust", "Product matrix threshold: customers", minCust, minCust);
		p = JobUtils.AddNewProjParam(p, "MinSale", "Product matrix threshold: sales", minSale, minSale);
		projectMapper.deleteProjectParam(projectId);
		for (String[] entry : p) {
			params = new HashMap<String,Object>();
			params.put("createdBy", "Qi");
			params.put("projectId", projectId);
			if(!StringUtils.isEmpty(entry[3])) {
				params.put("paramType", "PROJ_SETUP");
				params.put("paramName", entry[1]);
				params.put("charVal", entry[3]);
				projectMapper.saveNewProjectParam(params);
			} 
			if(!StringUtils.isEmpty(entry[0])) {
				params.put("paramType", "NEW_PROJ");
				params.put("paramName", entry[0]);
				params.put("charVal", entry[2]);
				projectMapper.saveNewProjectParam(params);
			}
		}
		
		int jobID = JdbcUtils.getJobId();
		int multiProcess = -1;
		RunMultiSqlJob job = new RunMultiSqlJob(jobID);
		JqueueSegment jqueue = new JqueueSegment("Qi", datasetKey, projectId+"", "createNewProjectProdSummary", "0",job);
		JobDBSegment jobDBSegment = new JobDBSegment(jqueue);
		
		jobDBSegment.createNewProjectBase(projectId,"LFS");
		jobDBSegment.createNewProjectLFSFromTran(projectId, projName, projDesc, multiProcess);
		jobDBSegment.createNewProject(projectId, multiProcess);
		jobDBSegment.createProdSummary(projectId, multiProcess);
		
		String prod_matrix = "prod_matrix_" + projectId;
		String deptCol = "dept_sys_key";
		String skuCol = "prod_sys_key";
		String deptType = "C_P" + projectId;
		String skuType = "PP_P" + projectId;
		String xml = "<projID>" + projectId + "</projID>";
		xml += "<deptID>-1</deptID>";
		xml += "<deptCode>-1</deptCode>";
		xml += "<sample>ALL</sample>";
		xml += "<minCust>" + minCust + "</minCust>";
		xml += "<minSale>" + minSale + "</minSale>";
		xml += "<transaction>tran_proj" + projectId + "_#dept_key#</transaction>";
		xml += "<prod_matrix>" + prod_matrix + "</prod_matrix>";
		xml += "<deptType>" + deptType + "</deptType>";
		xml += "<skuType>" + skuType + "</skuType>";
		xml += "<deptCol>" + deptCol + "</deptCol>";
		xml += "<skuCol>" + skuCol + "</skuCol>";
		xml += "<tempPath>temp</tempPath>";
		xml += "<user>Qi</user>";
		
		jobID = JdbcUtils.getJobId();
		RunYulesQJob yq_job = new RunYulesQJob(jobID);
		yq_job.setDesc("Run Product Matrix for All");
		yq_job.setCommand(xml);
		jqueue = new JqueueSegment("Qi",  datasetKey, projectId+"", projName, "0",yq_job);
		jqueue.runJQueueJob();
		
	}


	public PageData getGoodsList(Integer projectId,Integer pageNum, Integer pageSize,String prodCode,String prodDesc,String parentCode,String parentDesc,String sort) {
		PageHelper.startPage(pageNum, pageSize);
		Map<String,Object> params = new HashMap<String,Object>();
		if(!StringUtils.isEmpty(prodCode)) {
			params.put("prodCode", prodCode);
		}
		if(!StringUtils.isEmpty(prodDesc)) {
			params.put("prodDesc", prodDesc);
		}
		if(!StringUtils.isEmpty(parentCode)) {
			params.put("parentCode", parentCode);
		}
		if(!StringUtils.isEmpty(parentDesc)) {
			params.put("parentDesc", parentDesc);
		}
		if(!StringUtils.isEmpty(projectId)) {
			params.put("projectId", projectId);
		}
		
		if(!StringUtils.isEmpty(sort)) {
			params.put("sort", sort);
		} else {
			params.put("sort", "1");
		}
		
		
		
		List<Map<String,Object>> list = projectMapper.getGoodsList(params);
		
		PageData  pageData=new PageData();
		PageInfo<Map<String,Object>> pageInfo=new PageInfo<Map<String,Object>>(list);  
		pageData.setTotal(pageInfo.getTotal());
		pageData.setPageNum(pageInfo.getPageNum());
		pageData.setPageSize(pageInfo.getPageSize());
		pageData.setDataList(list);
		return pageData;
	}


	public PageData deptDataGrid(Integer pageNum, Integer pageSize,Integer projectId) {
		PageHelper.startPage(pageNum, pageSize);
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("projectId", projectId);
		List<Map<String,Object>> list = projectMapper.deptDataGrid(params);
		
		PageData  pageData=new PageData();
		PageInfo<Map<String,Object>> pageInfo=new PageInfo<Map<String,Object>>(list);  
		pageData.setTotal(pageInfo.getTotal());
		pageData.setPageNum(pageInfo.getPageNum());
		pageData.setPageSize(pageInfo.getPageSize());
		pageData.setDataList(list);
		return pageData;
	}

}
