package net.mofancy.analysis.postgres.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import CustomJobs.RunClusterJob;
import net.mofancy.analysis.postgres.mapper.FamilyMapper;
import net.mofancy.analysis.postgres.segment.JqueueSegment;
import net.mofancy.analysis.util.JdbcUtils;
import net.mofancy.analysis.web.common.exception.ParameterIllegalException;

@Service
public class FamilyService {
	
	private Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private FamilyMapper familyMapper;
	
	
	public List<Map<String, Object>> getDigitalList(int projectId) {
		return familyMapper.getDigitalList(projectId);
	}
	
	public List<Map<String, Object>> getFamilys(int projectId) {
		return familyMapper.getFamilys(projectId);
	}
	
	public List<Map<String, Object>> getTopGoods(int projectId,int parentKey) {
		return familyMapper.getTopGoods(projectId,parentKey);
	}
	
	public List<Map<String, Object>> getDistributionList(int projectId,int parentKey) {
		return familyMapper.getDistributionList(projectId,parentKey);
	}
	
	public List<Map<String, Object>> getSolutionsList(int projectId,int parentKey) {
		return familyMapper.getSolutionsList(projectId,parentKey);
	}
	
	public List<Map<String, Object>> getSolutionsList2(int projectId,int parentKey,int g) {
		return familyMapper.getSolutionsList2(projectId,parentKey,g);
	}
	
	public List<Map<String, Object>> getSolutionsList3(int projectId,int solutionKey) {
		return familyMapper.getSolutionsList3(projectId,solutionKey);
	}
	
	public List<Map<String, Object>> getSolutionsList4(Integer projectId,Integer solutionKey,Integer groupKey) {
		Map<String,Object> params = new HashMap<String,Object>();
		if(!StringUtils.isEmpty(projectId)) {
			params.put("projectId", projectId);
		}
		if(!StringUtils.isEmpty(solutionKey)) {
			params.put("solutionKey", solutionKey);
		}
		if(!StringUtils.isEmpty(groupKey)) {
			params.put("groupKey", groupKey);
		}
		
		
		return familyMapper.getSolutionsList4(params);
	}

	public void runCluster(Integer projectId,Integer deptKey,String deptName,Integer datasetKey,Integer minSale) {
		
		String userName = "Qi";
		String checkJob = "0";
		int jobID = JdbcUtils.getJobId();
		
		RunClusterJob job = new RunClusterJob(jobID);
		job.setFinishAction("FinishJobAction(" + jobID + ",'Done','runClusterButton','"+deptKey+"')");
		job.setDesc("Cluster " + deptName);
		String proj = projectId.toString();
		String xml = "<projID>" + proj + "</projID>";
		xml += "<dept>" + deptKey + "</dept>";//department的key
		xml += "<matrixDistance>distance</matrixDistance>";//用矩阵里的distance列还是phi_stats列
		xml += "<min>" + 0.0 + "</min>";//最低消费数
		xml += "<max>" + (minSale==0?"":minSale) + "</max>";//最高消费数
		xml += "<prod_cluster_sku>prod_cluster_sku_"+ proj + "</prod_cluster_sku>";
		xml += "<sku_type>PP_P" + proj  + "</sku_type>";
		xml += "<tempPath>temp</tempPath>";//可以输出的temp文件夹（比如C:\temp）
		xml += "<method>auto</method>";
		xml += "<user>" + userName + "</user>";//提交的用户名
		job.setCommand(xml);
		log.info(xml);
		
		JqueueSegment jqueue = new JqueueSegment(userName, datasetKey, projectId+"", "RunCluster", checkJob,job);
		jqueue.runJQueueJob();
		
	}

	public void runClusterAll(Integer projectId, Integer datasetKey,Integer minSale) {
		List<Map<String,Object>> list = getFamilys(projectId);
		for (Map<String, Object> map : list) {
			Integer deptKey = Integer.parseInt(map.get("prod_sys_key").toString());
			String deptName = map.get("prod_code").toString();
			runCluster(projectId,deptKey,deptName,datasetKey,minSale);
		}
		
	}
	@Transactional
	public void saveClusterSolution(Integer projectId,Integer deptKey,String name,Integer numCluster) {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("name", name);
		params.put("deptKey", deptKey);
		params.put("numCluster", numCluster);
		params.put("projectId", projectId);
		int count = familyMapper.getClusterProjectParam(params);
		if(count>0) {
			throw new ParameterIllegalException("该商品组合名字已经存在!");
		} else {
			Integer clusterKey = familyMapper.getClusterKeySeq();
			params.put("clusterKey", clusterKey);
			params.put("username", "Qi");
			int result = familyMapper.saveClusterProjectParam(params);
			if(result==0) {
				log.error("保存失败saveClusterProjectParam");
				throw new ParameterIllegalException("保存失败!");
			}
			result = familyMapper.saveProdClusteredGroup(params);
			if(result==0) {
				log.error("保存失败saveProdClusteredGroup");
				throw new ParameterIllegalException("保存失败!");
			}
			result = familyMapper.saveProdClusterSku(params);
			if(result==0) {
				log.error("保存失败saveProdClusterSku");
				throw new ParameterIllegalException("保存失败!");
			}
			result = familyMapper.saveClusterProjectParam2(params);
			if(result==0) {
				log.error("保存失败saveClusterProjectParam2");
				throw new ParameterIllegalException("保存失败!");
			}
		}
	}
	@Transactional
	public void deleteSolution(Integer projectId,Integer solutionKey) {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("projectId", projectId);
		params.put("solutionKey", solutionKey);
		familyMapper.deleteClusterProjectParam(params);
		familyMapper.deleteProdClusteredGroup(params);
		familyMapper.deleteProdClusterSku(params);
		familyMapper.deleteProdClusterSku2(params);
		familyMapper.deleteClusterProjectParam2(params);
	}
	@Transactional
	public void updateSolutionName(Integer projectId,Integer deptKey,Integer solutionKey,String name) {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("projectId", projectId);
		params.put("deptKey", deptKey);
		params.put("solutionKey", solutionKey);
		params.put("name", name);
		familyMapper.updateSolutionName(params);
	}

	@Transactional
	public void updateGroupName(Integer projectId,Integer groupKey,String name) {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("projectId", projectId);
		params.put("groupKey", groupKey);
		params.put("name", name);
		familyMapper.updateGroupName(params);
	}
	
	public List<Map<String, Object>> exportExcel() {
		Map<String,Object> params = new HashMap<String,Object>();
		List<Map<String,Object>> list = familyMapper.exportExcel(params);
		return list;
	}
}
