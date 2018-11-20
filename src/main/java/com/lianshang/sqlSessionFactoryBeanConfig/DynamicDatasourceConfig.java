package com.lianshang.sqlSessionFactoryBeanConfig;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
@Slf4j
@Configuration
@ConditionalOnBean(DataSource.class)
@ConditionalOnWebApplication
public class DynamicDatasourceConfig {

	@Bean("dynamicDatasource")
    @ConditionalOnMissingBean
	@Primary
    public  DynamicDatasource getDynamicDatasource(ApplicationContext applicationContext, ObjectProvider<List<PkgDtsCfg>> provider){
    	//动态数据源
    	DynamicDatasource dynamicDataSource = new DynamicDatasource();
    	//目标数据源
    	Map<Object, Object> targetDataSources = new HashMap<>();
    	//用户自定义的jar包和数据源的映射
    	List<PkgDtsCfg> pkgDtsCfgs =  provider.getIfAvailable();
    	
    	if(null != pkgDtsCfgs && !pkgDtsCfgs.isEmpty()){
    		int num = 0;
    		for(PkgDtsCfg pkgDtsCfg : pkgDtsCfgs){
    			num++;
    			Map<String, String> config =  pkgDtsCfg.getConfig();
    			Iterator<String> iterator =  config.keySet().iterator();
    			while(iterator.hasNext()){
    				String key  = iterator.next();
    			String dataSourceName = config.get(key);
					DataSource dataSource = (DataSource) applicationContext.getBean(dataSourceName);
    			if(null == dataSource){
    				throw new RuntimeException("未找到名字为["+dataSourceName+"]的数据源");
    			}
    			targetDataSources.put(key, dataSource);
    			DynamicDatasource.putKey(key);
    			}
    		}
    		if(num >0){
				//有数据源，则第一个设置为默认数据源
				Object firstDataSource  = targetDataSources.values().iterator().next();
				dynamicDataSource.setDefaultTargetDataSource(firstDataSource);
				targetDataSources.put(DynamicDatasource.defaultKey, firstDataSource);
			}else{
				Map<String, DataSource> dataSourceMap = applicationContext.getBeansOfType(DataSource.class);
				if(dataSourceMap.isEmpty()){
					log.error("请注意!!! 当前环境无可用的数据源......");
				}else {
					Object firstDataSource =  dataSourceMap.values().iterator().next();
					dynamicDataSource.setDefaultTargetDataSource(firstDataSource);
					targetDataSources.put(DynamicDatasource.defaultKey, firstDataSource);
				}
			}
    	}
    
		dynamicDataSource.setTargetDataSources(targetDataSources);
    	return dynamicDataSource;
    }
}
