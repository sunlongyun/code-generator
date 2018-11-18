package com.lianshang.sqlSessionFactoryBeanConfig;

import lombok.Data;

/**
 * 获取数据源配置
 * @author sunlongyun
 */
@Data
public class PageHelperPropertyConfig {
	
    private String offsetAsPageNum;
    private String rowBoundsWithCount;
    private String reasonable;
    private String pageSizeZero;
    private String returnPageInfo;
    private String supportMethodsArguments;
    private String dialect;
    private String params;

}
