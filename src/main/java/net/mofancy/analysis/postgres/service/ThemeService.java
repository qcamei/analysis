package net.mofancy.analysis.postgres.service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import CustomJobs.RunFactorAnalysisJob;
import CustomJobs.RunSegmentationJob;
import FactorAnalysis.FactorAnalysis;
import QueueManager.RunMultiSqlJob;
import global.util.Util;
import net.mofancy.analysis.postgres.mapper.ThemeMapper;
import net.mofancy.analysis.postgres.segment.JobDBSegment;
import net.mofancy.analysis.postgres.segment.JqueueSegment;
import net.mofancy.analysis.postgres.vo.PageData;
import net.mofancy.analysis.util.JdbcUtils;
import net.mofancy.analysis.util.JobUtils;
import net.mofancy.analysis.web.common.exception.ParameterIllegalException;

@Service
public class ThemeService {
	@Autowired
	private ThemeMapper themeMapper;

	public List<Map<String, Object>> getDistributionList(Integer projectId) {
		return themeMapper.getDistributionList(projectId);
	}

	public List<Map<String, Object>> getThemeList(Integer projectId) {
		return themeMapper.getThemeList(projectId);
	}
	
	public List<Map<String, Object>> getAnalysis(Integer projectId) {
		return themeMapper.getAnalyses(projectId);
	}
	/**
	 * 获取主题数
	 * @param projectId
	 * @param analysisKey
	 * @return
	 */
	public List<Map<String, Object>> getNumFactor(Integer projectId,Integer analysisKey) {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("projectId", projectId);
		params.put("analysisKey", analysisKey);
		return themeMapper.getNumFactor(params);
	}
	
	/**
	 * 运算主题分析
	 * @param datasetKey
	 * @param projectId
	 * @param analysisName
	 * @param minSpend
	 * @param maxSpend
	 * @param params
	 */
	@Transactional
	public void runFactorAnalysis(Integer datasetKey,Integer projectId,String analysisName,Integer minSpend,Integer maxSpend,String params) {
		
		JSONObject arr = JSONObject.parseObject(params);
		String keys = arr.getString("keys");
		String codes = arr.getString("codes");
		String groups = arr.getString("groups");
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("projectId", projectId);
		map.put("analysisName", analysisName);
		
		List<Map<String,Object>> list = themeMapper.purchaseThemes(map);
		if(list!=null&&list.size()>0) {
			throw new ParameterIllegalException("运算失败，该主题分析名称已经存在!");
		}
		map.put("analysisName", null);
		map.put("codes", codes);
		map.put("groups", groups);
		list = themeMapper.purchaseThemes(map);
		if(list!=null&&list.size()>0) {
			throw new ParameterIllegalException("运算失败，该主题分析已经存在!");
		}
		
		int jobID = JdbcUtils.getJobId();
		RunFactorAnalysisJob job = new RunFactorAnalysisJob(jobID);
		Integer analysisKey = themeMapper.getAnalysisKey();
		String xml = "<projID>" + projectId + "</projID>";
		xml += "<analysis_name>" + analysisName + "</analysis_name>"; //名字从correlationTextBox读取，用户输入
		xml += "<analysis_key>"+analysisKey+"</analysis_key>";//数据库得到nextval('sub_proj_seq')值

		//下面三个是逗号分隔的参数，从product family 列表中选取打钩的。每个department只能有一个product family solution商品簇组合选择。所以如果有很多方案时，命名比较关键，这样可以用过滤filter把想要的全部选出来。表格表头的checkbox是自动·选取每个department的第一商品簇组合
		xml += "<dept_keys>" + keys + "</dept_keys>";//每个department的系统生成的数字key
		xml += "<dept_codes>" + codes + "</dept_codes>";//每个department的原有code
		xml += "<solution_keys>" + groups + "</solution_keys>";//每个选取的商品簇组合的key
		//以上选择全部加到一起有时候可能超过project_param表中param_att或char_val的预设的200字符长度，如果在建库时department过多或code过长。最好办法就是手动把该schema下的这两个字段长度加长。
		xml += "<minSpend>" + minSpend + "</minSpend>";//minSpendTextBox中读取，用户输入，可空，设置消费者最低消费下限
		xml += "<maxSpend>" + maxSpend + "</maxSpend>";//maxSpendTextBox中读取，用户输入，可空，设置消费者最高消费上限
		xml += "<skuCol>prod_sys_key</skuCol>";
		xml += "<deptCol>dept_sys_key</deptCol>";
		xml += "<transaction>tran_proj"+projectId+"</transaction>";
		//只有这个消费范围内的，含边界，才会计算customer profile, 从而得到factore loading table
		xml += "<multi>1</multi>";//是否多线程处理，数据池参数

		job.setCommand(xml);

		JqueueSegment jqueue = new JqueueSegment("Qi",datasetKey, projectId+"", "RunFactorAnalysisJob", "0",job);
		jqueue.runJQueueJob();
	}
	/**
	 * 获取主题分析
	 * @param projectId
	 * @return
	 */
	public List<Map<String, Object>> purchaseThemes(Integer projectId) {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("projectId", projectId);
		return themeMapper.purchaseThemes(params);
	}

	/**
	 * 获取表格数据
	 * @param projectId
	 * @param numFactor
	 * @param analysisKey
	 * @param rotation
	 * @param includeGroups
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	public PageData getFactorResult(Integer projectId, Integer numFactor, Integer analysisKey,String rotation, String includeGroups,Integer pageNum,Integer pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("projectId", projectId);
		params.put("numFactor", numFactor);
		params.put("analysisKey", analysisKey);
		params.put("rotation", "ORIGINAL");
		params.put("includeGroups", includeGroups);
		String factorList = "";
		for(int i = 1;i<=numFactor;i++) {
			factorList += "round(f.factor_"+i+",3)*100 AS factor_"+i+",";
		}
		if(factorList.length()>0) {
			factorList = factorList.substring(0, factorList.length()-1);
		}
		params.put("factorList", factorList);
		List<Map<String,Object>> list = themeMapper.getFactorResult(params);
		PageData  pageData=new PageData();
		PageInfo<Map<String,Object>> pageInfo=new PageInfo<Map<String,Object>>(list);  
		pageData.setTotal(pageInfo.getTotal());
		pageData.setPageNum(pageInfo.getPageNum());
		pageData.setPageSize(pageInfo.getPageSize());
		pageData.setDataList(list);
		return pageData;
	}
	/**
	 * 获取已保存的主题方案
	 * @param projectId
	 * @return
	 */
	public List<Map<String, Object>> getSavedSolutions(Integer projectId) {
		return themeMapper.getSavedSolutions(projectId);
	}
	/**
	 * 获取theme柱形图数据
	 * @param projectId
	 * @param numFactor
	 * @param analysisKey
	 * @param rotation
	 * @param includeGroups
	 * @return
	 */
	public Map<String, Object> loadFactorResult(Integer projectId, Integer numFactor, Integer analysisKey,String rotation, String includeGroups) {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("projectId", projectId);
		params.put("numFactor", numFactor);
		params.put("analysisKey", analysisKey);
		params.put("rotation", "ORIGINAL");
		params.put("includeGroups", includeGroups);
		String factorList = "";
		for(int i = 1;i<=numFactor;i++) {
			factorList += "f.factor_"+i+",";
		}
		if(factorList.length()>0) {
			factorList = factorList.substring(0, factorList.length()-1);
		}
		
		params.put("factorList", factorList);
		List<Map<String,Object>> list = themeMapper.getFactorResult(params);
		if(list!=null&&list.size()<1) {
			throw new ParameterIllegalException("No product families found for this theme analysis");
		}
		int numGroup = list.size();
		List<String> categories = themeMapper.getFactorName(params);
		
		
		//把矩阵转到二维数组中，准备做旋转
		double[][] m = new double[numGroup][numFactor];
		double check = 0;
		boolean rotationFlag = true;
		
		
		//检查是否有整行都是0的数据，否则无法标准化加旋转
		for (int i = 0;i<list.size();i++) {
			check = 0;
			Map<String,Object> map = list.get(i);
			for(int j=0;j<numFactor;j++) {
				m[i][j] = Util.ntod(map.get("factor_"+(j+1)));
				check += Math.abs(m[i][j]);
			}
			if (check == 0) {
				rotationFlag = false;
			} 
		}
		
		if (rotationFlag) {
			//可旋转时，从rotationDropDownList中读取旋转方式，调用java 类FactorAnalysis 
			if ((!Util.strEmpty(rotation)) && (!rotation.equals("ORIGINAL"))){
				FactorAnalysis loadOrig = new FactorAnalysis(m);
				//通过url外部重新给迭代上线次数，默认1000，极偶尔的时候迭代次数会超过1000
				int maxTries = Util.atoi("0");
				if (maxTries > 0) {
					loadOrig.setMaxTries(maxTries);
				}
				//运行旋转
				loadOrig.Rotations(rotation, numGroup);
				m = loadOrig.getMatrix();
				
			}
		} else {
			throw new ParameterIllegalException("Rotation not possible as some values are nil");
		}
		List<List<Double>> series = new ArrayList<List<Double>>();
		for (int i = 0; i < numFactor; i++) {
			
			List<Double> arrs = new ArrayList<Double>();
			
			for (int j = 0; j < numGroup; j++) {
				m[j][i] = (double)Math.round(m[j][i]*100*100)/100;
				if(m[j][i]<20) {
					continue;
				}
				arrs.add(m[j][i]);
				if(arrs.size()>5) {
					Collections.sort(arrs,Collections.reverseOrder());
					arrs.remove(5);
					
				} else {
					for(int k = arrs.size();k<5;k++) {
						arrs.add(0.0);
					}
				}
			}
			series.add(arrs);
		}
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("categories", categories);
		map.put("series", series);
		
		return map;
	}
	
	/**
	 * 更新主题名
	 * @param projectId
	 * @param numFactor
	 * @param analysisKey
	 * @param name
	 * @param rotation
	 * @param factorInd
	 */
	@Transactional
	public void saveFactorName(Integer projectId,Integer numFactor, Integer analysisKey, String name,String rotation,Integer factorInd) {
		//检测该主题名是否已经存在
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("projectId", projectId);
		params.put("numFactor", numFactor);
		params.put("analysisKey", analysisKey);
		params.put("name", name);
		params.put("username", "Qi");
		params.put("rotation", rotation);
		params.put("factorInd", factorInd);
		int count = themeMapper.checkFactorName(params);
		if(count>0) {
			throw new ParameterIllegalException("该主题名已经存在！");
		}
		
		if(themeMapper.updateFactorName(params)==0) {
			themeMapper.saveFactorName(params);
		}
	}
	
	/**
	 * 保存主题方案
	 * @param projectId
	 * @param numFactor
	 * @param analysisKey
	 * @param rotation
	 * @param includeGroups
	 */
	@Transactional
	public void saveFactorSolution(Integer projectId,Integer numFactor, Integer analysisKey,String rotation,String includeGroups) {
		
		String factorName =((numFactor < 10) ? " " : "") + numFactor +  " 主题 "+rotation;
		
		int paramKey = themeMapper.getSubProjSeq();
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("projectId", projectId);
		params.put("analysisKey", analysisKey);
		params.put("paramKey", paramKey);
		params.put("numFactor", numFactor);
		params.put("rotation", rotation);
		params.put("factorName", factorName);
		params.put("username", "Qi");
		
		int count = themeMapper.checkFactorSolution2(params);
		
		if(count>0) {
			return;
		}
		
		themeMapper.saveFactorSolution(params);
		
		themeMapper.deleteClusterFactor(params);
		
		//save the matrix
		params.put("rotation", "ORIGINAL");
		params.put("includeGroups", includeGroups);
		String factorList = "";
		for(int i = 1;i<=numFactor;i++) {
			factorList += "f.factor_"+i+",";
		}
		if(factorList.length()>0) {
			factorList = factorList.substring(0, factorList.length()-1);
		}
		
		params.put("factorList", factorList);
		List<Map<String,Object>> list = themeMapper.getFactorResult(params);
		int num_group = list.size();
		double[][] m = new double[num_group][numFactor];
		List<Integer> groupKeys = new ArrayList<Integer>();
		
		for (int j = 0; j < num_group; j++) {
			Map<String,Object> map = list.get(j);
			groupKeys.add(Integer.parseInt(map.get("group_key").toString()));
			for (int i = 0; i < numFactor; i++)
			{
				
				m[j][i] = Util.ntod((map.get("factor_" + (i + 1)).toString())) / 100d;
			}
		}
		factorList = JobUtils.repeatStr(numFactor,"factor_%s",",");
		
		String comma = "";
		String query = "";
		for (int j =0 ;j<groupKeys.size() ;j++) {
			query += " "+comma+" SELECT "+projectId+","+paramKey+","+numFactor+",'"+rotation+"',"+groupKeys.get(j);
			for (int i = 0;i<numFactor;i++) {
				query += "," +m[j][i];
			}
			comma = " UNION ";
		}
		params.put("analysisKey", paramKey);
		params.put("factorList", factorList);
		params.put("query", query);
		themeMapper.saveClusterFactor(params);
		
	}
	
	/**
	 * 主题列表
	 * @param projectId
	 * @param parentKey
	 * @param numFactor
	 * @return
	 */
	public List<Map<String, Object>> purchaseThemes2(Integer projectId, Integer parentKey,Integer numFactor) {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("projectId", projectId);
		params.put("parentKey", parentKey);
		params.put("numFactor", numFactor);
		return themeMapper.purchaseThemes2(params);
	}
	
	/**
	 * 删除已保存主题方案
	 * @param projectId
	 * @param analysisKey
	 */
	@Transactional
	public void deleteFactorSolution(Integer projectId, Integer analysisKey) {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("projectId", projectId);
		params.put("analysisKey", analysisKey);
		int count = themeMapper.checkFactorSolution(params);
		
		if(count<1) {
//			themeMapper.deleteClusterFactor3(params);
			themeMapper.deleteCustomerSegments(params);
			themeMapper.deleteCustomerNormalProfile(params);
			themeMapper.deleteProjectParam(params);
			themeMapper.deleteProjectParam2(params);
		}
		
	}
	
	/**
	 * 删除主题分析
	 * @param projectId
	 * @param analysisKey
	 */
	@Transactional
	public void deleteFactorAnalysis(Integer projectId, Integer analysisKey) {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("projectId", projectId);
		params.put("analysisKey", analysisKey);
		int count = themeMapper.checkSavedSolutions(params);
		if(count>0) {
			throw new ParameterIllegalException("删除失败,该主题分析存在已保存的主题方案！");
		}
		themeMapper.deleteCorrelationVector(params);
		themeMapper.deleteProdClusteredGroup(params);
		themeMapper.deleteProdAssociatedGroup(params);
		try {
			themeMapper.dropCustomerClusterSummary(params);
		} catch (Exception e) {
			themeMapper.deleteCustomerClusterSummary(params);
		}
		
		themeMapper.deleteClusterFactor2(params);
		themeMapper.deleteProjectParam3(params);
		themeMapper.deleteProjectParam4(params);
		themeMapper.deleteProjectParam5(params);
	}

	public Map<String, Object> getSavedSolutionsById(Integer projectId, Integer themeKey) {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("projectId", projectId);
		params.put("themeKey", themeKey);
		return themeMapper.getSavedSolutionsById(params);
	}

	public Map<String, Object> loadFactorResult2(Integer projectId, Integer numFactor, Integer analysisKey,String rotation, String includeGroups,Integer factorInd) {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("projectId", projectId);
		params.put("numFactor", numFactor);
		params.put("analysisKey", analysisKey);
		params.put("rotation", "ORIGINAL");
		params.put("includeGroups", includeGroups);
		String factorList = "";
		for(int i = 1;i<=numFactor;i++) {
			factorList += "f.factor_"+i+",";
		}
		if(factorList.length()>0) {
			factorList = factorList.substring(0, factorList.length()-1);
		}
		
		params.put("factorList", factorList);
		List<Map<String,Object>> list = themeMapper.getFactorResult(params);
		if(list!=null&&list.size()<1) {
			throw new ParameterIllegalException("No product families found for this theme analysis");
		}
		int numGroup = list.size();
		
		
		//把矩阵转到二维数组中，准备做旋转
		double[][] m = new double[numGroup][numFactor];
		double check = 0;
		boolean rotationFlag = true;
		
		
		//检查是否有整行都是0的数据，否则无法标准化加旋转
		for (int i = 0;i<list.size();i++) {
			check = 0;
			Map<String,Object> map = list.get(i);
			for(int j=0;j<numFactor;j++) {
				m[i][j] = Util.ntod(map.get("factor_"+(j+1)));
				check += Math.abs(m[i][j]);
			}
			if (check == 0) {
				rotationFlag = false;
			} 
		}
		
		if (rotationFlag) {
			//可旋转时，从rotationDropDownList中读取旋转方式，调用java 类FactorAnalysis 
			if ((!Util.strEmpty(rotation)) && (!rotation.equals("ORIGINAL"))){
				FactorAnalysis loadOrig = new FactorAnalysis(m);
				//运行旋转
				loadOrig.Rotations(rotation, numGroup);
				m = loadOrig.getMatrix();
				
			}
		} else {
			throw new ParameterIllegalException("Rotation not possible as some values are nil");
		}
		Set<Double> series = new TreeSet<Double>();
		
		List<Double> arrs = new ArrayList<Double>();
		List<Double> a = new ArrayList<Double>();
		List<Double> b = new ArrayList<Double>();
		Map<Double,Integer> seat = new HashMap<Double,Integer>();
		for (int j = 0; j < numGroup; j++) {
			m[j][factorInd] = (double)Math.round(m[j][factorInd]*100*100)/100;
			arrs.add(m[j][factorInd]);
			seat.put(m[j][factorInd], j);
			if(m[j][factorInd]>20) {
				a.add(m[j][factorInd]);
			}
			if(m[j][factorInd]<-20) {
				b.add(m[j][factorInd]);
			}
		}
		Collections.sort(arrs,Collections.reverseOrder());
		series.addAll(a);
		series.addAll(arrs.subList(0, 5));
		series.addAll(b);
		List<Double> results = new ArrayList<Double>();
		results.addAll(series);
		Collections.sort(results,Collections.reverseOrder());
		Map<String,Object> map = new HashMap<String,Object>();
		List<String> categories = new ArrayList<String>();
		List<Integer> groupKeys = new ArrayList<Integer>();
		for (Double d : results) {
			Integer index = seat.get(d);
			categories.add((String)list.get(index).get("group_name"));
			groupKeys.add(Integer.parseInt(list.get(index).get("group_key").toString()));
		}
		map.put("groupKeys", groupKeys);
		map.put("categories", categories);
		map.put("series", results);
		
		return map;
	}

	
	public void runCustomerProfile(Integer datasetKey,Integer projectId,Integer solutionKey,String selectedFactors,String solutionName,String themeName,Integer minDept,Integer minCluster) {
		int count = selectedFactors.split(",").length;
		//这里强制要求一参数只能建一个segmentation，不能重复建。
		
		//根据前面的矩阵旋转自动生成默认命名
		String profileName = solutionName + "-" + count + "/" + themeName + "-" + minDept + "D" + minCluster + "P";
		Integer profileKey = null;
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("projectId", projectId);
		params.put("solutionKey", solutionKey);
		List<Map<String,Object>> list = themeMapper.getAllSavedProfileKey(params);
		
		if(list!=null&&list.size()>0) {
			if(StringUtils.isEmpty(list.get(0).get("profile_key"))) {
				profileKey = themeMapper.getSubProjSeq();
			} else {
				throw new ParameterIllegalException("Solution with same parameters has already run, just click \"Build Segments\" button.");
			}
			
		} else {
			throw new ParameterIllegalException("没有可用的主题！");
		}

		//调用长query计算每个消费者对应每个勾选的factor的score。
		//runCustomerProfile
		int jobID = JdbcUtils.getJobId();
		RunMultiSqlJob job = new RunMultiSqlJob(jobID);
		JqueueSegment jqueue = new JqueueSegment("Qi", datasetKey, projectId+"", "runCustomerProfile", "0",job);
		JobDBSegment jobDBSegment = new JobDBSegment(jqueue);
		
		jobDBSegment.runCustomerProfile(projectId, solutionKey, profileKey, profileName, selectedFactors, minDept, minCluster, 0, 5000, "Qi");
	}
	
	public void runSegmentationJob(Integer datasetKey,Integer projectId,Integer profileKey,Integer solutionKey,Integer minSeg,Integer maxSeg) {
		
		//第二步，调用RunSegmentationJob 
		if (minSeg < 2 || maxSeg > 30 || maxSeg < minSeg) {
			throw new ParameterIllegalException("Valid segment range is between 2 and 30");
		}
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("projectId", projectId);
		params.put("solutionKey", solutionKey);
		List<Map<String,Object>> list = themeMapper.getAllSavedProfileKey(params);
		
		if(list!=null&&list.size()>0) {
			if(StringUtils.isEmpty(list.get(0).get("profile_key"))) {
				throw new ParameterIllegalException("请先初始化该主题！");
			} else {
				profileKey = Integer.parseInt(list.get(0).get("profile_key").toString());
			}
		} else {
			throw new ParameterIllegalException("没有可用的主题！");
		}

		int jobID = JdbcUtils.getJobId();
		RunSegmentationJob job = new RunSegmentationJob(jobID);

		String xml = "<projID>" + projectId + "</projID>";
		xml += "<minSeg>" + minSeg + "</minSeg>"; //最小segment数 minSegmentTextBox
		xml += "<maxSeg>" + maxSeg + "</maxSeg>";//最大segment数 maxSegmentTextBox
		xml += "<savedSolution>" + solutionKey + "</savedSolution>"; //第一步里选的factor solution
		xml += "<key>" + profileKey + "</key>";//第一步里生成的profile的key
		xml += "<path>temp</path>";//数据池参数，可以由JQueue写output的临时文件夹

		job.setCommand(xml);
		JqueueSegment jqueue = new JqueueSegment("Qi", datasetKey, projectId+"", "runSegmentationJob", "0",job);
		jqueue.runJQueueJob();
	}

	public List<Map<String,Object>> getClusterDistribution(Integer projectId, Integer analysisKey) {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("projectId", projectId);
		params.put("analysisKey", analysisKey);
		List<Map<String,Object>> list = themeMapper.getClusterDistribution(params);
		List<Map<String,Object>> resultList = new ArrayList<Map<String,Object>>();
		if(list!=null&&list.size()>0) {
			Map<String,Object> map = list.get(list.size()-1);
			Integer maxNumCust = Integer.parseInt(map.get("num_cust").toString());
			for (int i = 0 ; i<list.size();i++) {
				Map<String, Object> obj = list.get(i);
				Map<String,Object> item = new HashMap<String,Object>();
				item.put("stats", ">="+obj.get("stats"));
				item.put("num_cust", obj.get("num_cust"));
				Integer numCust = Integer.parseInt(obj.get("num_cust").toString());
				Integer cust = 0;
				if(i>0) {
					cust = Integer.parseInt(list.get(i-1).get("num_cust").toString());
				}
				DecimalFormat df = new DecimalFormat("#.00");
				item.put("cust_spend", df.format(numCust*1.0/maxNumCust));
				item.put("spend", df.format(((numCust-cust)*1.0)/maxNumCust));
				resultList.add(item);
			}
		}
		return resultList;
	}
	
	public List<Map<String, Object>> getDeptDistribution(Integer projectId, Integer analysisKey) {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("projectId", projectId);
		params.put("analysisKey", analysisKey);
		List<Map<String,Object>> list = themeMapper.getDeptDistribution(params);
		List<Map<String,Object>> resultList = new ArrayList<Map<String,Object>>();
		if(list!=null&&list.size()>0) {
			Map<String,Object> map = list.get(list.size()-1);
			Integer maxNumCust = Integer.parseInt(map.get("num_cust").toString());
			for (int i = 0 ; i<list.size();i++) {
				Map<String, Object> obj = list.get(i);
				Map<String,Object> item = new HashMap<String,Object>();
				item.put("stats", ">="+obj.get("stats"));
				item.put("num_cust", obj.get("num_cust"));
				Integer numCust = Integer.parseInt(obj.get("num_cust").toString());
				Integer cust = 0;
				if(i>0) {
					cust = Integer.parseInt(list.get(i-1).get("num_cust").toString());
				}
				DecimalFormat df = new DecimalFormat("#.00");
				item.put("cust_spend", df.format(numCust*1.0/maxNumCust));
				item.put("spend", df.format(((numCust-cust)*1.0)/maxNumCust));
				resultList.add(item);
			}
		}
		return resultList;
	}
	
}
