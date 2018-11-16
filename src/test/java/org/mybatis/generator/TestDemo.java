package org.mybatis.generator;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.junit.Test;
import org.mybatis.generator.exception.ShellException;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.mybatis.generator.support.api.PackageContext;
import org.mybatis.generator.support.api.TableContext;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: kfc
 * @Description: <br/>
 * Date:Create in 2018/5/7 14:49
 * @Modified By:
 */
public class TestDemo {
    @Test
    public void testOne(){
        System.out.println(11111);
    }



    @Test
    public void testFree() throws IOException, TemplateException, ShellException {
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_23);
        configuration.setDefaultEncoding(Charset.forName("UTF-8").name());
        configuration.setClassForTemplateLoading(TestDemo.class, "/");
        Template template = configuration.getTemplate("templates/service.java.ftl");
        DefaultShellCallback shellCallback = new DefaultShellCallback(true);
        File directory = shellCallback.getDirectory("src/main/java", "org.mybatis.generator.test.service");
        if (!directory.isDirectory()){
            directory.mkdirs();
        }
        FileOutputStream fileOutputStream = new FileOutputStream(new File(directory,"TestService.java"));

        Map<String,Object> map  = new HashMap<String, Object>();
        map.put("author",System.getProperties().getProperty("user.name"));
        map.put("date",(new SimpleDateFormat("yyyy-MM-dd")).format(new Date()));

        PackageContext p = new PackageContext("org.mybatis.generator.test.service","org.mybatis.generator.test.mapper.TestMapper","org.mybatis.generator.test.model.Test","org.mybatis.generator.test.model.TestExample","",null);

        map.put("package",p);

        TableContext t = new TableContext("TestService","TestMapper","Test","TestExample","测试");

        map.put("table",t);



        template.process(map, new OutputStreamWriter(fileOutputStream, Charset.forName("UTF-8").name()));
        fileOutputStream.close();
    }

    private static String getRootPath() {
        File directory = new File("");// 参数为空
        String courseFile = null;
        try {
            courseFile = directory.getCanonicalPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return courseFile;
    }

}
