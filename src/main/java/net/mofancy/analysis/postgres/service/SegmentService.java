package net.mofancy.analysis.postgres.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.mofancy.analysis.postgres.mapper.SegmentMapper;

@Service
public class SegmentService {
	
	@Autowired
	private SegmentMapper segmentMapper;

	public List<Map<String,Object>> getSegmentList(Integer projectId) {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("projectId", projectId);
		return segmentMapper.getSegmentList(params);
		
	}

	public List<Map<String, Object>> getSegmentSummary(Integer projectId,Integer segmentationKey,Integer numFactor) {
		String  sqlStr1 = "",sqlStr2="",sqlStr3="",sqlStr4="",sqlStr5="";
			
		
		for(int i=1;i<=numFactor;i++) {
			
			sqlStr1 += ",CASE WHEN s.factor_sum=0 THEN 0 ELSE round(s.factor_"+i+"/s.factor_sum*100,4) END AS f"+i+" ";
			sqlStr2 += ",CASE WHEN s.factor_sum=0 OR ttl.tf"+i+"=0 OR ttl.factor_sum=0 THEN 0 ELSE round(s.factor_"+i+"/s.factor_sum/(ttl.tf"+i+"/ttl.factor_sum)*100,4) END AS fi"+i+" ";
			sqlStr3 += ",sum(s.factor_"+i+") AS tf"+i+" ";
			sqlStr4 += ",round(sum(s.factor_"+i+")/sum(s.factor_sum)*100,4) AS f"+i+" ";
			sqlStr5 += ",null AS fi"+i+" ";
		}
		
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("projectId", projectId);
		params.put("segmentationKey", segmentationKey);
		params.put("sqlStr1", sqlStr1);
		params.put("sqlStr2", sqlStr2);
		params.put("sqlStr3", sqlStr3);
		params.put("sqlStr4", sqlStr4);
		params.put("sqlStr5", sqlStr5);
		return segmentMapper.getSegmentSummary(params);
	}

	public List<Map<String, Object>> getSegmentPurchaseThemes(Integer projectId, Integer analysisKey,Integer numFactor) {
		
		String sqlStr = "";
		for(int i = 1;i<=numFactor;i++) {
			sqlStr += +i+",";
		}
		if(sqlStr.length()>0) {
			sqlStr = sqlStr.substring(0, sqlStr.length()-1);
		}
		
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("projectId", projectId);
		params.put("analysisKey", analysisKey);
		params.put("numFactor", numFactor);
		params.put("sqlStr", sqlStr);
		return segmentMapper.getSegmentPurchaseThemes(params);
	}
	
	@Transactional
	public int saveSegmentName(Integer projectId,Integer segmentKey,String name) {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("name", name);
		params.put("segmentKey", segmentKey);
		params.put("projectId", projectId);
		return segmentMapper.saveSegmentName(params);
	}

	public List<Map<String, Object>> getSegmentProfile(Integer projectId,Integer solutionKey,Integer numFactor,Integer analysisKey,String summaryLevel) {
		String  sqlStr1 = "",sqlStr2="",sqlStr3="",sqlStr4="",sqlStr5="",sqlStr6="",sqlStr7="",sqlStr8="";
			
		for(int i=0;i<=numFactor;i++) {
			sqlStr1 += ",COALESCE(sum(CASE WHEN segment_ind="+i+" THEN spend END),0) spend_"+i+" ";
			sqlStr2 += ",COALESCE(sum(CASE WHEN segment_ind="+i+" THEN customer_count END),0) cust_"+i+" ";
			sqlStr3 += ",CASE WHEN total_spend_perc!=0 THEN round(spend_perc_"+i+"*100.0/total_spend_perc,4) ELSE 0 END spend_index_"+i+" ";
			sqlStr4 += ",(cust_pene_"+i+"-total_cust_pene)*100.0 specificity_"+i+" ";
			sqlStr5 += ",CASE WHEN sum_spend_0!=0 THEN round(spend_"+i+"*100.0/sum_spend_0,15) ELSE 0 END spend_perc_"+i+" ";
			sqlStr6 += ",CASE WHEN sum_cust_0!=0 THEN round(cust_"+i+"*100.0/sum_cust_0,15) ELSE 0 END cust_pene_"+i+" ";
			sqlStr7 += ",COALESCE(sum(CASE WHEN segment_ind="+i+" THEN spend END),0) sum_spend_"+i+" ";
			sqlStr8 += ",COALESCE(sum(CASE WHEN segment_ind="+i+" THEN customer_count END),0) sum_cust_"+i+" ";
		}
		
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("projectId", projectId);
		params.put("summaryLevel", summaryLevel);
		params.put("analysisKey", analysisKey);
		params.put("solutionKey", solutionKey);
		params.put("sqlStr1", sqlStr1);
		params.put("sqlStr2", sqlStr2);
		params.put("sqlStr3", sqlStr3);
		params.put("sqlStr4", sqlStr4);
		params.put("sqlStr5", sqlStr5);
		params.put("sqlStr6", sqlStr6);
		params.put("sqlStr7", sqlStr7);
		params.put("sqlStr8", sqlStr8);
		return segmentMapper.getSegmentProfile(params);
	}

	
	
	
}
