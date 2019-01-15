package com.lianshang.generator.commons;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.ConstVal;
import com.baomidou.mybatisplus.generator.config.FileOutConfig;
import com.baomidou.mybatisplus.generator.config.TemplateConfig;
import com.baomidou.mybatisplus.generator.config.builder.ConfigBuilder;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.FileType;
import com.baomidou.mybatisplus.generator.engine.AbstractTemplateEngine;
import freemarker.template.Configuration;
import freemarker.template.Template;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 描述:
 *
 * @AUTHOR 孙龙云
 * @date 2018-11-15 下午2:14
 */
public class LianshangFreemarkerTemplateEngine extends AbstractTemplateEngine {

    private Configuration configuration;

    private List<GenerateFileTypeEnum> coverableFileTypeList = new ArrayList<>();

    @Override
    public LianshangFreemarkerTemplateEngine init(ConfigBuilder configBuilder) {
        super.init(configBuilder);
        configuration = new Configuration();
        configuration.setDefaultEncoding(ConstVal.UTF8);
        configuration.setClassForTemplateLoading(LianshangFreemarkerTemplateEngine.class, StringPool.SLASH);
        return this;
    }

    public LianshangFreemarkerTemplateEngine (List<GenerateFileTypeEnum> coverableFileTypeList) {
        super();
        this.coverableFileTypeList = coverableFileTypeList;
    }

    @Override
    public void writer(Map<String, Object> objectMap, String templatePath, String outputFile) throws Exception {
        Template template = configuration.getTemplate(templatePath);
        FileOutputStream fileOutputStream = new FileOutputStream(new File(outputFile));
        template.process(objectMap, new OutputStreamWriter(fileOutputStream, ConstVal.UTF8));
        fileOutputStream.close();
        logger.debug("模板:" + templatePath + ";  文件:" + outputFile);
    }


    @Override
    public String templateFilePath(String filePath) {
        StringBuilder fp = new StringBuilder();
        fp.append(filePath).append(".ftl");
        return fp.toString();
    }

    /**
     * <p>
     * 输出 java xml 文件
     * </p>
     */
    public AbstractTemplateEngine batchOutput() {
        try {
            List<TableInfo> tableInfoList = getConfigBuilder().getTableInfoList();
            for (TableInfo tableInfo : tableInfoList) {
                Map<String, Object> objectMap = getObjectMap(tableInfo);
                Map<String, String> pathInfo = getConfigBuilder().getPathInfo();
                TemplateConfig template = getConfigBuilder().getTemplate();
                // 自定义内容
                InjectionConfig injectionConfig = getConfigBuilder().getInjectionConfig();
                if (null != injectionConfig) {
                    injectionConfig.initMap();
                    objectMap.put("cfg", injectionConfig.getMap());
                    List<FileOutConfig> focList = injectionConfig.getFileOutConfigList();
                    if (CollectionUtils.isNotEmpty(focList)) {
                        for (FileOutConfig foc : focList) {
                            if (isCreate(FileType.OTHER, foc.outputFile(tableInfo))) {
                                writer(objectMap, foc.getTemplatePath(), foc.outputFile(tableInfo));
                            }
                        }
                    }
                }
                // Mp.java
                String entityName = tableInfo.getEntityName();
                if (null != entityName && null != pathInfo.get(ConstVal.ENTITY_PATH)) {

                    String entityFile = String.format((pathInfo.get(ConstVal.ENTITY_PATH) + File.separator + "%s" + suffixJavaOrKt()), entityName);
                    if (isNeedCoverThisTypeFile(FileType.ENTITY, entityFile, GenerateFileTypeEnum.ENTITY)) {
                        writer(objectMap, templateFilePath(template.getEntity(getConfigBuilder().getGlobalConfig().isKotlin())), entityFile);
                    }

                    String entityPath = pathInfo.get(ConstVal.ENTITY_PATH);

                    String dtoPath = entityPath.replaceAll("entity$", "dto");
                    String dtoFile = String.format(dtoPath + File.separator + "%s" + suffixJavaOrKt(), entityName+"Dto");
                    if (isNeedCoverThisTypeFile(FileType.OTHER, dtoFile, GenerateFileTypeEnum.DTO)) {
                        String entityTemplateFilePath = templateFilePath(template.getEntity(getConfigBuilder().getGlobalConfig().isKotlin()));
                        String dtoTemplateFilePath = entityTemplateFilePath.replaceAll("entity\\.", "dto.");
                        writer(objectMap, dtoTemplateFilePath, dtoFile);
                    }


                    String examplePath = entityPath.replaceAll("entity$", "example");
                    String exampleFile = String.format(examplePath + File.separator + "%s" + suffixJavaOrKt(), entityName+"Example");
                    if (isNeedCoverThisTypeFile(FileType.OTHER, exampleFile, GenerateFileTypeEnum.EXAMPLE)) {
                        String entityTemplateFilePath = templateFilePath(template.getEntity(getConfigBuilder().getGlobalConfig().isKotlin()));
                        String exampleTemplateFilePath = entityTemplateFilePath.replaceAll("entity\\.", "example.");

                        writer(objectMap, exampleTemplateFilePath, exampleFile);
                    }
                }
                // MpMapper.java
                if (null != tableInfo.getMapperName() && null != pathInfo.get(ConstVal.MAPPER_PATH)) {
                    String mapperFile = String.format((pathInfo.get(ConstVal.MAPPER_PATH) + File.separator + tableInfo.getMapperName() + suffixJavaOrKt()), entityName);
                    if (isNeedCoverThisTypeFile(FileType.MAPPER, mapperFile, GenerateFileTypeEnum.MAPPER_INTERFACE)) {
                        writer(objectMap, templateFilePath(template.getMapper()), mapperFile);
                    }
                }
                // MpMapper.xml
                if (null != tableInfo.getXmlName() && null != pathInfo.get(ConstVal.XML_PATH)) {
                    String xmlFile = String.format((pathInfo.get(ConstVal.XML_PATH) + File.separator + tableInfo.getXmlName() + ConstVal.XML_SUFFIX), entityName);
                    if (isNeedCoverThisTypeFile(FileType.XML, xmlFile, GenerateFileTypeEnum.MAPPER_XML)) {
                        writer(objectMap, templateFilePath(template.getXml()), xmlFile);
                    }
                }
                // IMpService.java
                if (null != tableInfo.getServiceName() && null != pathInfo.get(ConstVal.SERVICE_PATH)) {
                    String serviceFile = String.format((pathInfo.get(ConstVal.SERVICE_PATH) + File.separator + tableInfo.getServiceName() + suffixJavaOrKt()), entityName);
                    if (isNeedCoverThisTypeFile(FileType.SERVICE, serviceFile, GenerateFileTypeEnum.SERVICE)) {
                        writer(objectMap, templateFilePath(template.getService()), serviceFile);
                    }
                }
                // MpServiceImpl.java
                if (null != tableInfo.getServiceImplName() && null != pathInfo.get(ConstVal.SERVICE_IMPL_PATH)) {
                    String implFile = String.format((pathInfo.get(ConstVal.SERVICE_IMPL_PATH) + File.separator + tableInfo.getServiceImplName() + suffixJavaOrKt()), entityName);
                    if (isNeedCoverThisTypeFile(FileType.SERVICE_IMPL, implFile, GenerateFileTypeEnum.SERVICE_IMPL)) {
                        writer(objectMap, templateFilePath(template.getServiceImpl()), implFile);
                    }
                }
                // MpController.java
                if (null != tableInfo.getControllerName() && null != pathInfo.get(ConstVal.CONTROLLER_PATH)) {
                    String controllerFile = String.format((pathInfo.get(ConstVal.CONTROLLER_PATH) + File.separator + tableInfo.getControllerName() + suffixJavaOrKt()), entityName);
                    if (isNeedCoverThisTypeFile(FileType.CONTROLLER, controllerFile, GenerateFileTypeEnum.CONTROLLER)) {
                        writer(objectMap, templateFilePath(template.getController()), controllerFile);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("无法创建文件，请检查配置信息！", e);
        }
        return this;
    }

    private boolean isNeedCoverThisTypeFile(FileType fileType, String file, GenerateFileTypeEnum fileTypeEnum){
        boolean isCreate = isCreate(fileType, file);
        if(!isCreate){
            return coverableFileTypeList.contains(fileTypeEnum);
        }
        return isCreate;
    }
}
