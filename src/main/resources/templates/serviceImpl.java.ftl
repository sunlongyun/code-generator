package ${package.ServiceImpl};

import ${package.Entity}.${entity};
import ${cfg.dtoPath}.${entity}Dto;
import ${package.Mapper}.${table.mapperName};
import ${package.Service}.${table.serviceName};

import com.lianshang.generator.commons.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * ${table.comment!} 服务实现类
 * </p>
 *
 * @author ${author}
 * @since ${date}
 */
@Service
public class ${table.serviceImplName} extends LsBaseMapper<${table.mapperName},${entity}, ${entity}Dto> implements ${table.serviceName} {

}