package net.mofancy.analysis;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import net.mofancy.analysis.postgres.service.FamilyService;
import net.mofancy.analysis.postgres.service.ProjectService;


@RunWith(SpringRunner.class)
@SpringBootTest
public class AnalysisApplicationTests {
	
	Logger log = LoggerFactory.getLogger(getClass());
	@Autowired
	private ProjectService projectService;
	
	@Autowired
	private FamilyService familyService;
	
	@Test
	public void contextLoads() {
		Map<String, String> map = new HashMap<String,String>();
		map.put("projName", "测试7");
		map.put("projDesc", "测试7");
		map.put("period", "#26 weeks");
		map.put("goodsLevel", "L1");
		projectService.saveProject(map,5);
		
		
		
	}
	
	@Test
	public void test01() {
		
		familyService.runCluster(93, 12459, "Beauty",5,0);
		
		
	}

}
