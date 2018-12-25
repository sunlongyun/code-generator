package com.lianshang.sqlSessionFactoryBeanConfig;


import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusProperties;
import com.baomidou.mybatisplus.autoconfigure.SpringBootVFS;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.MybatisXMLLanguageDriver;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.incrementer.IKeyGenerator;
import com.baomidou.mybatisplus.core.injector.ISqlInjector;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.github.pagehelper.PageHelper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.mapper.ClassPathMapperScanner;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.*;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.*;

/**
 * mybatis-plus 
 * @author 孙龙云
 */
@Configuration
@ConditionalOnClass({SqlSessionFactory.class, SqlSessionFactoryBean.class})
@EnableConfigurationProperties(MybatisPlusProperties.class)
@AutoConfigureAfter({DataSourceAutoConfiguration.class})
@AutoConfigureBefore({MybatisPlusAutoConfiguration.class,MybatisSqlSessionFactoryBean.class})
@ConditionalOnMissingBean({DynamicDatasource.class,SqlSessionFactory.class})
@ConditionalOnProperty("mybatis-plus.mapper-locations")
public class SessionFactoryBeanConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(SessionFactoryBeanConfiguration.class);

    private final MybatisPlusProperties properties;

    private final Interceptor[] interceptors;

    private final ResourceLoader resourceLoader;

    private final DatabaseIdProvider databaseIdProvider;

    private final List<ConfigurationCustomizer> configurationCustomizers;

    private final ApplicationContext applicationContext;
    @Autowired
    private  PageHelperPropertyConfig pageHelperConfig;
    public SessionFactoryBeanConfiguration(MybatisPlusProperties properties,
                                           ObjectProvider<Interceptor[]> interceptorsProvider,
                                           ResourceLoader resourceLoader,
                                           ObjectProvider<DatabaseIdProvider> databaseIdProvider,
                                           ObjectProvider<List<ConfigurationCustomizer>> configurationCustomizersProvider,
                                           ApplicationContext applicationContext) {
        this.properties = properties;
        this.interceptors = interceptorsProvider.getIfAvailable();
        this.resourceLoader = resourceLoader;
        this.databaseIdProvider = databaseIdProvider.getIfAvailable();
        this.configurationCustomizers = configurationCustomizersProvider.getIfAvailable();
        this.applicationContext = applicationContext;

        logger.info("SessionFactoryBeanConfig初始化----------");
    }

    @PostConstruct
    public void checkConfigFileExists() {
        if (this.properties.isCheckConfigLocation() && StringUtils.hasText(this.properties.getConfigLocation())) {
            Resource resource = this.resourceLoader.getResource(this.properties.getConfigLocation());
            Assert.state(resource.exists(), "Cannot find config location: " + resource
                + " (please add config file or check your Mybatis configuration)");
        }
    }

    @Bean
    @ConditionalOnMissingBean
    public SqlSessionFactory sqlSessionFactory(DynamicDatasource dynamicDatasource) throws Exception {
        MybatisSqlSessionFactoryBean factory = new MybatisSqlSessionFactoryBean();
        factory.setDataSource(dynamicDatasource);
        factory.setVfs(SpringBootVFS.class);
        if (StringUtils.hasText(this.properties.getConfigLocation())) {
            factory.setConfigLocation(this.resourceLoader.getResource(this.properties.getConfigLocation()));
        }
        applyConfiguration(factory);
        if (this.properties.getConfigurationProperties() != null) {
            factory.setConfigurationProperties(this.properties.getConfigurationProperties());
        }
        if (!ObjectUtils.isEmpty(this.interceptors)) {
            factory.setPlugins(this.interceptors);
        }
        if (this.databaseIdProvider != null) {
            factory.setDatabaseIdProvider(this.databaseIdProvider);
        }
        if (StringUtils.hasLength(this.properties.getTypeAliasesPackage())) {
            factory.setTypeAliasesPackage(this.properties.getTypeAliasesPackage());
        }
        if (StringUtils.hasLength(this.properties.getTypeEnumsPackage())) {
            factory.setTypeEnumsPackage(this.properties.getTypeEnumsPackage());
        }
        if (this.properties.getTypeAliasesSuperType() != null) {
            factory.setTypeAliasesSuperType(this.properties.getTypeAliasesSuperType());
        }
        if (StringUtils.hasLength(this.properties.getTypeHandlersPackage())) {
            factory.setTypeHandlersPackage(this.properties.getTypeHandlersPackage());
        }
        if (!ObjectUtils.isEmpty(this.properties.resolveMapperLocations())) {
            factory.setMapperLocations(this.properties.resolveMapperLocations());
        }

        //pageherper
        factory.setPlugins(new Interceptor[]{pageHelper()});
        GlobalConfig globalConfig;
        if (!ObjectUtils.isEmpty(this.properties.getGlobalConfig())) {
            globalConfig = this.properties.getGlobalConfig();
        } else {
            globalConfig = new GlobalConfig();
        }
        //注入填充器
        if (this.applicationContext.getBeanNamesForType(MetaObjectHandler.class,
            false, false).length > 0) {
            MetaObjectHandler metaObjectHandler = this.applicationContext.getBean(MetaObjectHandler.class);
            globalConfig.setMetaObjectHandler(metaObjectHandler);
        }
        //注入主键生成器
        if (this.applicationContext.getBeanNamesForType(IKeyGenerator.class, false,
            false).length > 0) {
            IKeyGenerator keyGenerator = this.applicationContext.getBean(IKeyGenerator.class);
            globalConfig.getDbConfig().setKeyGenerator(keyGenerator);
        }
        //注入sql注入器
        if (this.applicationContext.getBeanNamesForType(ISqlInjector.class, false,
            false).length > 0) {
            ISqlInjector iSqlInjector = this.applicationContext.getBean(ISqlInjector.class);
            globalConfig.setSqlInjector(iSqlInjector);
        }
        factory.setGlobalConfig(globalConfig);
        return factory.getObject();
    }

    private void applyConfiguration(MybatisSqlSessionFactoryBean factory) {
        MybatisConfiguration configuration = this.properties.getConfiguration();
        if (configuration == null && !StringUtils.hasText(this.properties.getConfigLocation())) {
            configuration = new MybatisConfiguration();
        }
        if (configuration != null && !CollectionUtils.isEmpty(this.configurationCustomizers)) {
            for (ConfigurationCustomizer customizer : this.configurationCustomizers) {
                customizer.customize(configuration);
            }
        }
        if (null != configuration) {
            configuration.setDefaultScriptingLanguage(MybatisXMLLanguageDriver.class);
        }
        factory.setConfiguration(configuration);
    }

    @Bean
    @ConditionalOnMissingBean
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        ExecutorType executorType = this.properties.getExecutorType();
        if (executorType != null) {
            return new SqlSessionTemplate(sqlSessionFactory, executorType);
        } else {
            return new SqlSessionTemplate(sqlSessionFactory);
        }
    }

    /**
     * This will just scan the same base package as Spring Boot does. If you want
     * more power, you can explicitly use
     * {@link org.mybatis.spring.annotation.MapperScan} but this will get typed
     * mappers working correctly, out-of-the-box, similar to using Spring Data JPA
     * repositories.
     */
    public static class AutoConfiguredMapperScannerRegistrar
        implements BeanFactoryAware, ImportBeanDefinitionRegistrar, ResourceLoaderAware {

        private BeanFactory beanFactory;

        private ResourceLoader resourceLoader;

        @Override
        public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {

            logger.debug("Searching for mappers annotated with @Mapper");

            ClassPathMapperScanner scanner = new ClassPathMapperScanner(registry);

            try {
                if (this.resourceLoader != null) {
                    scanner.setResourceLoader(this.resourceLoader);
                }

                List<String> packages = AutoConfigurationPackages.get(this.beanFactory);
                if (logger.isDebugEnabled()) {
                    packages.forEach(pkg -> logger.debug("Using auto-configuration base package '{}'", pkg));
                }

                scanner.setAnnotationClass(Mapper.class);
                scanner.registerFilters();
                scanner.doScan(StringUtils.toStringArray(packages));
            } catch (IllegalStateException ex) {
                logger.debug("Could not determine auto-configuration package, automatic mapper scanning disabled.", ex);
            }
        }

        @Override
        public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
            this.beanFactory = beanFactory;
        }

        @Override
        public void setResourceLoader(ResourceLoader resourceLoader) {
            this.resourceLoader = resourceLoader;
        }
    }

    @org.springframework.context.annotation.Configuration
    @Import({AutoConfiguredMapperScannerRegistrar.class})
    @ConditionalOnMissingBean(MapperFactoryBean.class)
    public static class MapperScannerRegistrarNotFoundConfiguration {

        @PostConstruct
        public void afterPropertiesSet() {
            logger.debug("No {} found.", MapperFactoryBean.class.getName());
        }
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

        logger.info("pageHelper==>{}",pageHelperConfig);
        return pageHelper;
    }


    /**
     * 实例化 DataSourceTransactionManager
     * @return
     */
    @Bean
    @Primary
    public DataSourceTransactionManager getDataSourceTransactionManager(DynamicDatasource dynamicDatasource) {
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(dynamicDatasource);
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


    @Bean("dynamicDatasource")
    @ConditionalOnMissingBean
    public  DynamicDatasource getDynamicDatasource(ApplicationContext applicationContext, ObjectProvider<List<PkgDtsCfg>> provider){
        //动态数据源
        DynamicDatasource dynamicDataSource = new DynamicDatasource();
        //目标数据源
        Map<Object, Object> targetDataSources = new HashMap<>();
        //用户自定义的jar包和数据源的映射
        List<PkgDtsCfg> pkgDtsCfgs =  provider.getIfAvailable();

        int num = 0;
        if (null != pkgDtsCfgs && !pkgDtsCfgs.isEmpty()) {
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
        }

        if (num > 0 && !targetDataSources.values().isEmpty()) {
            //有数据源，则第一个设置为默认数据源
            Object firstDataSource  = targetDataSources.values().iterator().next();
            dynamicDataSource.setDefaultTargetDataSource(firstDataSource);
            targetDataSources.put(DynamicDatasource.defaultKey, firstDataSource);
        }else{
            Map<String, DataSource> dataSourceMap = applicationContext.getBeansOfType(DataSource.class);
            if(dataSourceMap.isEmpty()){
                logger.error("请注意!!! 当前环境无可用的数据源......");
            }else {
                Iterator<String> it = dataSourceMap.keySet().iterator();
                while (it.hasNext()) {
                    String dataSourceName = it.next();
                    DataSource firstDataSource = dataSourceMap.get(dataSourceName);
                    if (null != firstDataSource) {
                        dynamicDataSource.setDefaultTargetDataSource(firstDataSource);
                        targetDataSources.put(DynamicDatasource.defaultKey, firstDataSource);
                        break;
                    }
                }
            }
        }
        dynamicDataSource.setTargetDataSources(targetDataSources);
        return dynamicDataSource;
    }


}
