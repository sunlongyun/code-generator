package com.lianshang.sqlSessionFactoryBeanConfig;

import lombok.Data;

/**
 * 获取数据源配置
 * @author sunlongyun
 */
@Data
public class PageHelperPropertyConfig {
	
    private String offsetAsPageNum="false";
    private String rowBoundsWithCount="false";
    private String reasonable="false";
    private String pageSizeZero="false";
    private String returnPageInfo="false";
    private String supportMethodsArguments="true";
    private String dialect="mysql";
    private String params="false";

}
