package com.lianshang.utils;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.lianshang.generator.commons.LianshangFreemarkerTemplateEngine;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 描述:
 *
 * @AUTHOR 孙龙云
 * @date 2018-11-15 上午10:33
 */
@Slf4j
public class LsCodeGeneratorUtil {

    /**
     * 代码生成器
     *
     * @param moduleName
     * @param jdbcUrl
     * @param driverClassName
     * @param userName
     * @param password
     * @param tableName
     */
    public static void generateCode(String moduleName, String packageParent, String jdbcUrl, String driverClassName,
                                    String userName, String password, String... tableName) {
        // 代码生成器
        AutoGenerator generator = new AutoGenerator();

        final String projectPath = System.getProperty("user.dir");
        // 全局配置
        GlobalConfig globalConfig = getGlobalConfig(projectPath);
        generator.setGlobalConfig(globalConfig);

        // 数据源配置
        DataSourceConfig dataSourceConfig = getDataSourceConfig(jdbcUrl,
          driverClassName, userName, password);
        generator.setDataSource(dataSourceConfig);

        // 包配置
        final PackageConfig packageConfig = getPackageConfig(moduleName, packageParent);
        generator.setPackageInfo(packageConfig);

        // 自定义配置
        InjectionConfig injectionConfig = getInjectionConfig(projectPath, moduleName, packageParent);
        generator.setCfg(injectionConfig);
        generator.setTemplate(new TemplateConfig().setXml(null));


        // 策略配置
        StrategyConfig strategy = getStategy(tableName);
        generator.setStrategy(strategy);
        generator.setTemplateEngine(new LianshangFreemarkerTemplateEngine());

        generator.execute();
    }

    /**
     * 自定义配置
     *
     * @param projectPath
     * @return
     */
    private static InjectionConfig getInjectionConfig(final String projectPath, final String moduleName,
                                                      final String packageParent) {
        return new InjectionConfig() {
            @Override
            public void initMap() {
                // to do nothing
                Map<String, Object> dataMap = new HashMap<>();
                dataMap.put("dtoPath", packageParent + "." + moduleName + ".dto");
                dataMap.put("examplePath", packageParent + "." + moduleName + ".example");
                this.setMap(dataMap);
                List<TableInfo> tableInfos =  this.getConfig().getTableInfoList();
                if(null != tableInfos){
                    for(TableInfo tableInfo :tableInfos){
                        log.info("tableInfo:{}",tableInfo);
                        tableInfo.setControllerName(null);
                    }
                }
            }

            @Override
            public List<FileOutConfig> getFileOutConfigList() {
                FileOutConfig xmlOutConfig = new FileOutConfig("/templates/mapper.xml.ftl") {
                    @Override
                    public String outputFile(TableInfo tableInfo) {
                        // 自定义输入文件名称
                        return projectPath + "/src/main/resources/mapper/"+moduleName
                          + "/" + tableInfo.getEntityName() + "Mapper" + StringPool.DOT_XML;
                    }
                };

                return Arrays.asList(xmlOutConfig);
            }


            /**
             * <p>
             * 检查文件目录，不存在自动递归创建
             * </p>
             *
             * @param filePath 文件路径
             */
            public void checkDir(String filePath) {
                if(filePath.contains("controller"))
                    return;
                getFileCreate().checkDir(filePath);
            }
        };

    }


    /**
     * 全局设置
     *
     * @param projectPath 项目路径
     * @return
     */
    private static GlobalConfig getGlobalConfig(String projectPath) {
        GlobalConfig globalConfig = new GlobalConfig();
        globalConfig.setOutputDir(projectPath + "/src/main/java/");
        globalConfig.setAuthor("孙龙云");
        globalConfig.setOpen(false);
        globalConfig.setFileOverride(false);
        globalConfig.setActiveRecord(true);
        globalConfig.setBaseResultMap(true);
        globalConfig.setBaseColumnList(true);
        globalConfig.setMapperName("%sMapper");
        globalConfig.setXmlName("%sMapper");
        globalConfig.setServiceName("%sService");
        globalConfig.setServiceImplName("%sServiceImpl");
        globalConfig.setControllerName("%sController");
        globalConfig.setSwagger2(false);
        globalConfig.setDateType(DateType.ONLY_DATE);
        globalConfig.setIdType(IdType.AUTO);

        return globalConfig;
    }

    /**
     * 设置数据库配置
     */
    private static StrategyConfig getStategy(String... tableNames) {
        StrategyConfig strategy = new StrategyConfig();
        strategy.setNaming(NamingStrategy.underline_to_camel);
        strategy.setColumnNaming(NamingStrategy.underline_to_camel);
        strategy.setEntityLombokModel(false);
        strategy.setSkipView(true);
        strategy.setEntityLombokModel(true);
        strategy.setEntityBuilderModel(false);
        strategy.setControllerMappingHyphenStyle(true);
        strategy.setSuperEntityClass(null);
        strategy.setSuperControllerClass(null);

        if (null != tableNames && tableNames.length > 0) {
            strategy.setInclude(tableNames);
        }
        strategy.entityTableFieldAnnotationEnable(true).setLogicDeleteFieldName("validity");

        return strategy;
    }

    /**
     * 获得数据源配置
     * dataSource 数据源
     *
     * @return
     */
    private static DataSourceConfig getDataSourceConfig(String jdbcUrl, String driverClassName,
                                                        String userName, String password) {
        DataSourceConfig dataSourceConfig = new DataSourceConfig();
        dataSourceConfig.setUrl(jdbcUrl);
        dataSourceConfig.setDriverName(driverClassName);
        dataSourceConfig.setUsername(userName);
        dataSourceConfig.setPassword(password);
        dataSourceConfig.setSchemaName("public");

        return dataSourceConfig;
    }

    /**
     * 包设置
     *
     * @return
     */
    private static PackageConfig getPackageConfig(String moduleName, String parent) {
        final PackageConfig packageConfig = new PackageConfig();
        packageConfig.setModuleName(moduleName);
        packageConfig.setParent(parent);
        Map<String, String> pathInfos = packageConfig.getPathInfo();
        log.info("pathInfos=>{}",pathInfos);
        return packageConfig;
    }
}
