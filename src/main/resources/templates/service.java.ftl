package ${package.Service};

import com.lianshang.generator.commons.IService;
import ${cfg.dtoPath}.${entity}Dto;
/**
 * <p>
 * ${table.comment!} 服务类
 * </p>
 *
 * @author ${author}
 * @since ${date}
 */
public interface I${table.serviceName} extends ${superServiceClass}<${entity}Dto> {

}
