package com.lianshang.sqlSessionFactoryBeanConfig;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import lombok.extern.slf4j.Slf4j;

/**
 * 动态数据源
 * @author Administrator
 *
 */
@Slf4j
public class DynamicDatasource extends AbstractRoutingDataSource{
	public static final String defaultKey = "default";
	/**
	 * 存储keys
	 */
	private static Set<String> keys = new HashSet<>();
	{
		keys.add(defaultKey);
	}
	/**
	 * 添加数据源key
	 * @param key
	 */
	public static void putKey(String key){
		keys.add(key);
	}
	/**
	 * 根据dataSourceKey决定使用哪个数据源
	 */
	private static ThreadLocal<String> dataSourceKey = new ThreadLocal<>();
	@Override
	protected Object determineCurrentLookupKey() {
		String key = dataSourceKey.get();
		log.info("key==>{}", key);
		return key;
	}
	
	/**
	 * 修改数据源标志(取匹配精度最高的key)
	 * @param fullPackageName 全包路径
	 */
	public static void setDataSourceKey(String fullPackageName){
		
		if(null == keys || keys.isEmpty()){
			throw new RuntimeException("数据源集合为空");
		}
		//取最长匹配的key
		String maxLongKey = "";
		Iterator<String> it =  keys.iterator();
		while(it.hasNext()){
			String k = it.next();
			if(fullPackageName.contains(k)){
				if(k.length() > maxLongKey.length()){
					maxLongKey =  k;
				}
			}
		}
		
		if(StringUtils.isEmpty(maxLongKey)){
			log.info("未找到匹配的数据源，尝试使用默认数据源");
			maxLongKey = defaultKey;
		}
		log.info("dataSource key :{}", maxLongKey);
		dataSourceKey.set(maxLongKey);
	}
	
}
