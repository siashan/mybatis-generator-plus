package ${package.packageName};

<#if (package.implementService)??>
import ${package.implementService};
<#else>
import org.mybatis.generator.support.base.service.BaseService;
</#if>
import ${package.mapper};
import ${package.model};
import ${package.example};
import org.springframework.stereotype.Service;

/**
 * <p>
    * ${table.comment} 服务实现类
    * </p>
 *
 * @author ${author}
 * @since ${date}
 */
@Service
public class ${table.serviceName} extends BaseService<${table.mapper},${table.model},${table.example}>  {

}

