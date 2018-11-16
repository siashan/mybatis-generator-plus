package org.mybatis.generator.support.plugins;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.config.MergeConstants;

import java.util.List;

public class SerializablePlugin extends PluginAdapter {

	@Override
	public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
		topLevelClass.addImportedType(new FullyQualifiedJavaType("java.io.Serializable"));
		topLevelClass.addSuperInterface(new FullyQualifiedJavaType("Serializable"));




		Field field = new Field();
        field.setVisibility(JavaVisibility.PRIVATE);
        field.setFinal(true);
        field.setStatic(true);
        field.setType(new FullyQualifiedJavaType("long"));
        field.setName("serialVersionUID");
        field.setInitializationString("1L");


        field.addJavaDocLine("/**");
		field.addJavaDocLine(" * "+ MergeConstants.NEW_ELEMENT_TAG);
		field.addJavaDocLine(" */"); //$NON-NLS-1$

		topLevelClass.addField(field);
		return super.modelBaseRecordClassGenerated(topLevelClass,introspectedTable);
	}

	@Override
	public boolean validate(List<String> warnings) {
		return true;
	}

}
