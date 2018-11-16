package ${package.Mapper};

import ${package.Entity}.${entity};
import ${superMapperClassPackage};
import ${cfg.examplePath}.${entity}Example;
import java.util.List;

/**
 * <p>
 * ${table.comment!} Mapper 接口
 * </p>
 *
 * @author ${author}
 * @since ${date}
 */
<#if kotlin>
interface ${table.mapperName} : ${superMapperClass}<${entity}>
<#else>
public interface ${table.mapperName} extends ${superMapperClass}<${entity}> {
       /**
       ** 通用查询
       **/
       public List<${entity}> selectByExample(${entity}Example example);
}
</#if>
