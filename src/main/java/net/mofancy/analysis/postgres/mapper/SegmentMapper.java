package net.mofancy.analysis.postgres.mapper;

import java.util.List;
import java.util.Map;

public interface SegmentMapper {

	List<Map<String,Object>> getSegmentList(Map<String, Object> params);

	List<Map<String, Object>> getSegmentSummary(Map<String, Object> params);

	List<Map<String, Object>> getSegmentPurchaseThemes(Map<String, Object> params);

	int saveSegmentName(Map<String, Object> params);

	List<Map<String, Object>> getSegmentProfile(Map<String, Object> params);
	
}
