package com.lianshang.sqlSessionFactoryBeanConfig;

import java.io.IOException;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.plugin.Interceptor;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import com.github.pagehelper.PageHelper;

import lombok.extern.slf4j.Slf4j;
	
/**
 * 描述:
 *
 * @AUTHOR 孙龙云
 * @date 2018-11-09 下午4:01
 */
@Slf4j
@Configuration
public class SessionFactoryBeanConfig2{
    @Autowired
    private  PageHelperPropertyConfig pageHelperConfig;
    
    @Value("${mybatis-plus.mapper-locations}")
    private String mybatisMapperPath;
    /**
     * 动态数据源
     */
    @Autowired
    private DynamicDatasource dataSource;
    /**
     * 实例化SqlSessionFactoryBean
     * @return
     */
    @Bean    
    @Primary
    public SqlSessionFactoryBean getSqlSessionFactoryBean() {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        PageHelper pageHelper = pageHelper();
        sqlSessionFactoryBean.setPlugins(new Interceptor[]{pageHelper});
        Resource[] resources = null;
        try {
        	if(StringUtils.isEmpty(mybatisMapperPath)){
        		mybatisMapperPath = "classpath:mapper/*.xml";
        	}
            resources = new PathMatchingResourcePatternResolver().getResources(mybatisMapperPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        sqlSessionFactoryBean.setMapperLocations(resources);
        return sqlSessionFactoryBean;
    }

    public PageHelper pageHelper() {
        // 分页插件
        PageHelper pageHelper = new PageHelper();

        Properties properties = new Properties();

        properties.setProperty("offsetAsPageNum", pageHelperConfig.getOffsetAsPageNum());
        properties.setProperty("rowBoundsWithCount", pageHelperConfig.getRowBoundsWithCount());
        properties.setProperty("reasonable", pageHelperConfig.getReasonable());
        properties.setProperty("pageSizeZero", pageHelperConfig.getPageSizeZero());
        properties.setProperty("returnPageInfo", pageHelperConfig.getReturnPageInfo());
        properties.setProperty("supportMethodsArguments", pageHelperConfig.getSupportMethodsArguments());
        properties.setProperty("dialect", pageHelperConfig.getDialect());
        properties.setProperty("params", pageHelperConfig.getParams());

        pageHelper.setProperties(properties);
        return pageHelper;
    }


    /**
     * 实例化 DataSourceTransactionManager
     * @return
     */
    @Bean
    @Primary
    public DataSourceTransactionManager getDataSourceTransactionManager() {
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(dataSource);
        return transactionManager;
    }
    
    
    /**
     * pager-helper配置
     */
    @Bean
    @ConfigurationProperties("pager-helper")
    public PageHelperPropertyConfig getPageHelperConfig(){
    	return new PageHelperPropertyConfig();
    }
    
//  @Bean("druidDataSource")
//  public DataSource getLianshangDataSource() {
//      DruidDataSource druidDataSource = new DruidDataSource();
//
//      druidDataSource.setUrl(dataSourceConfig.getUrl());
//      druidDataSource.setDriverClassName(dataSourceConfig.getDriverClassName());
//      druidDataSource.setUsername(dataSourceConfig.getUsername());
//      druidDataSource.setPassword(dataSourceConfig.getPassword());
//      
//      return druidDataSource;
//  }
//  @Bean("druidDataSource2")
//  public DataSource getLianshangDataSource2() {
//      DruidDataSource druidDataSource = new DruidDataSource();
//
//      druidDataSource.setUrl(dataSourceConfig.getUrl());
//      druidDataSource.setDriverClassName(dataSourceConfig.getDriverClassName());
//      druidDataSource.setUsername(dataSourceConfig.getUsername());
//      druidDataSource.setPassword(dataSourceConfig.getPassword());
//      
//      return druidDataSource;
//  }
//  /**
//   * datasource配置
//   * @return
//   */
//  @Bean
//  @ConfigurationProperties("spring.datasource.druid")
//  public DataSourcePropertyConfig getLianshangDataSourceConfig(){
//  	return new DataSourcePropertyConfig();
//  }
  
}
