package org.mybatis.generator.support.plugins;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @Author: kfc
 * @Description: <br/>
 * Date:Create in 2018/5/5 14:38
 * @Modified By:
 */
public class BaseMapperGeneratorPlugin extends PluginAdapter {
    private Logger logger = LoggerFactory.getLogger(BaseMapperGeneratorPlugin.class);

    @Override
    public boolean validate(List<String> list) {
        return true;
    }

    /**
     * 生成dao
     */
    @Override
    public boolean clientGenerated(Interface interfaze,
                                   TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {


        FullyQualifiedJavaType fqjt = new FullyQualifiedJavaType("BaseMapper<"
                + introspectedTable.getBaseRecordType() + ","
                + introspectedTable.getExampleType() + ">");

        String implementationPackage = this.getContext().getJavaClientGeneratorConfiguration().getProperties().getProperty("implementationPackage");
        FullyQualifiedJavaType imp = null;
        if (null != implementationPackage) {
            imp = new FullyQualifiedJavaType(implementationPackage);
        } else {
            imp = new FullyQualifiedJavaType("org.mybatis.generator.support.base.mapper.BaseMapper");
        }
        /**
         * 添加 extends MybatisBaseMapper
         */
        interfaze.addSuperInterface(fqjt);

        /**
         * 添加import my.mabatis.example.base.MybatisBaseMapper;
         */
        interfaze.addImportedType(imp);
        /**
         * 方法不需要
         */
        interfaze.getMethods().clear();
        interfaze.getAnnotations().clear();
        return true;
    }
}
