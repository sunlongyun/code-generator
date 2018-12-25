package com.lianshang.sqlSessionFactoryBeanConfig;

import java.util.Map;

/**
 * 动态数据源配置实现类继承该接口
 */
public interface PkgDtsCfg {
	/**
	 * 获取配置
	 * @return
	 */
	public Map<String, String> getConfig();
}
