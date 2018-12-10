package net.mofancy.analysis.config;

import java.util.Objects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class DynamicDataSourceAspect {

    @Before("execution(* net.mofancy.analysis.web.controller.*.*(..))")
    public void beforeSwitchDS(JoinPoint point){

        //获得访问的方法名
        if("net.mofancy.analysis.web.controller.MainController".equals(point.getSignature().getDeclaringTypeName())
        ||"net.mofancy.analysis.web.controller.LoginController".equals(point.getSignature().getDeclaringTypeName())
        ||"net.mofancy.analysis.web.controller.DataPoolController".equals(point.getSignature().getDeclaringTypeName())
        ) {
        	return; 
        }
		try {
			int datasetKey = getMethodInfo(point);
			if(datasetKey!=0){
				String datasetName = DatabaseConfig.dbMap.get(datasetKey+"");
        		if(!datasetName.equals(DataSourceContextHolder.getDB())) {
        			DataSourceContextHolder.setDB(DatabaseConfig.dbMap.get(datasetKey+""));
        		}
	        			
	        } else {
	        	DataSourceContextHolder.setDB(DataSourceContextHolder.DEFAULT_DS);
	        }

		} catch (Exception e) {
			e.printStackTrace();
		}   
        
        
    }


    @After("execution(* net.mofancy.analysis.web.controller.*.*(..))")
    public void afterSwitchDS(JoinPoint point){
//        DataSourceContextHolder.clearDB();
    }
    
    private int getMethodInfo(JoinPoint point) {
        String[] parameterNames = ((MethodSignature) point.getSignature()).getParameterNames();
        int datasetKey = 0;
        if (Objects.nonNull(parameterNames)) {
            for (int i = 0; i < parameterNames.length; i++) {
                String value = point.getArgs()[i] != null ? point.getArgs()[i].toString() : "null";
                if("datasetKey".equals(parameterNames[i])&&!"null".equals(value)) {
                	datasetKey = Integer.parseInt(value);
                	break;
                }
            }
            
        }
        return datasetKey;
    }

}