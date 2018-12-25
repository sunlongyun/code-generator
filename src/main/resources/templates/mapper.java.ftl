package ${package.Mapper};

import ${package.Entity}.${entity};
import ${superMapperClassPackage};
/**
 * <p>
 * ${table.comment!} Mapper 接口
 * </p>
 *
 * @author ${author}g
 * @since ${date}
 */
<#if kotlin>
interface ${table.mapperName} : ${superMapperClass}<${entity}>
<#else>
public interface ${table.mapperName} extends LsBaseMapper<${entity}> {

}
</#if>
