package net.mofancy.analysis.postgres.mapper;

import java.util.List;
import java.util.Map;

public interface DataPoolMapper {

	List<Map<String,Object>> selectListByCondition(Map<String,Object> record);

}
