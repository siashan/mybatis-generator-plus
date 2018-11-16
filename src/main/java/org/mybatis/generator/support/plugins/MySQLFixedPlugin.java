package org.mybatis.generator.support.plugins;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.XmlElement;

import java.util.List;

/**
 * Description:MySql 插件  <br/>
 *
 * @param:
 * @return:
 * @Author: kfc
 * @Date: 2018/5/8 10:53
 */
public class MySQLFixedPlugin extends PluginAdapter {

	@Override
	public boolean sqlMapInsertSelectiveElementGenerated(XmlElement element,
			IntrospectedTable introspectedTable) {
		element.addAttribute(new Attribute("useGeneratedKeys", "true"));
		element.addAttribute(new Attribute("keyProperty", "id"));
		return super.sqlMapInsertSelectiveElementGenerated(element, introspectedTable);
	}
	
	@Override
	public boolean validate(List<String> warnings) {
		return true;
	}

}
