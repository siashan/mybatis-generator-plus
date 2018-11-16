/**
 * Copyright 2006-2017 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mybatis.generator.support.api;

import freemarker.template.Template;
import org.mybatis.generator.api.*;
import org.mybatis.generator.codegen.RootClassInfo;
import org.mybatis.generator.config.*;
import org.mybatis.generator.exception.InvalidConfigurationException;
import org.mybatis.generator.exception.ShellException;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.mybatis.generator.internal.NullProgressCallback;
import org.mybatis.generator.internal.ObjectFactory;
import org.mybatis.generator.internal.XmlFileMergerJaxp;
import org.mybatis.generator.support.internal.JavaFileMergerJaxp;

import java.io.*;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.mybatis.generator.internal.util.ClassloaderUtility.getCustomClassloader;
import static org.mybatis.generator.internal.util.messages.Messages.getString;

/**
 * 这个类是MyBatis生成器的主界面。该工具的典型执行包括以下步骤:
 * 此类在源码的基础上做了修改，增加了生成service层的代码
 * <ol>
 * <li>创建一个配置对象。配置可以是解析XML配置文件的结果，也可以只在Java中创建。</li>
 * <li>创建一个MyBatisGenerator对象</li>
 * <li>调用一个generate()方法。</li>
 * </ol>
 *
 * @author kfc
 * @see org.mybatis.generator.config.xml.ConfigurationParser
 */
public class MyBatisGeneratorFix {

    /**
     * 配置类.
     */
    private Configuration configuration;

    /**
     * 脚本回调.
     */
    private ShellCallback shellCallback;

    /**
     * T要生成的Java文件.
     */
    private List<GeneratedJavaFile> generatedJavaFiles;

    /**
     * 要生成的xml文件.
     */
    private List<GeneratedXmlFile> generatedXmlFiles;

    /**
     * 警告.
     */
    private List<String> warnings;

    /**
     * The projects.
     */
    private Set<String> projects;

    /**
     * 构造一个MyBatisGenerator对象。
     *
     * @param configuration 此调用的配置。
     * @param shellCallback 一个shell回调接口的实例。您可以指定null，在这种情况下，将使用DefaultShellCallback。
     * @param warnings      在执行过程中生成的任何警告都会被添加到这个列表中。警告不会影响工具的运行，但是它们可能会影响结果。典型的警告是不支持的数据类型。在这种情况下，该列将被忽略，生成将继续。如果不希望返回警告，可以指定null。
     * @throws InvalidConfigurationException 如果指定的配置无效。
     */
    public MyBatisGeneratorFix(Configuration configuration, ShellCallback shellCallback,
                               List<String> warnings) throws InvalidConfigurationException {
        super();
        if (configuration == null) {
            throw new IllegalArgumentException(getString("RuntimeError.2")); //$NON-NLS-1$
        } else {
            this.configuration = configuration;
        }

        if (shellCallback == null) {
            this.shellCallback = new DefaultShellCallback(false);
        } else {
            this.shellCallback = shellCallback;
        }

        if (warnings == null) {
            this.warnings = new ArrayList<String>();
        } else {
            this.warnings = warnings;
        }
        generatedJavaFiles = new ArrayList<GeneratedJavaFile>();
        generatedXmlFiles = new ArrayList<GeneratedXmlFile>();
        projects = new HashSet<String>();

        this.configuration.validate();
    }

    /**
     * 这是生成代码的主要方法。这种方法是长期运行的，但是可以提供进展，并且可以通过ProgressCallback接口来取消该方法。该方法的这个版本运行所有配置的上下文。
     *
     * @param callback 如果不需要进度信息，可以使用ProgressCallback接口的实例，或者null。
     * @throws SQLException         SQL 异常
     * @throws IOException          发出I/O异常的信号。
     * @throws InterruptedException 如果该方法被取消，则通过ProgressCallback。
     */
    public void generate(ProgressCallback callback) throws SQLException,
            IOException, InterruptedException {
        generate(callback, null, null, true);
    }

    /**
     * 这是生成代码的主要方法。这种方法是长期运行的，但是可以提供进展，并且可以通过ProgressCallback接口来取消该方法。
     *
     * @param callback   如果不需要进度信息，可以使用ProgressCallback接口的实例，或者null。
     * @param contextIds 一组包含要运行的上下文id的字符串。只有在该列表中指定id的上下文将被运行。如果列表是null或空的，那么所有上下文都是运行的。
     * @throws SQLException         SQL 异常
     * @throws IOException          发出I/O异常的信号。
     * @throws InterruptedException 如果该方法被取消，则通过ProgressCallback。
     */
    public void generate(ProgressCallback callback, Set<String> contextIds)
            throws SQLException, IOException, InterruptedException {
        generate(callback, contextIds, null, true);
    }

    /**
     * 这是生成代码的主要方法。这种方法是长期运行的，但是可以提供进展，并且可以通过ProgressCallback接口来取消该方法。
     *
     * @param callback                 如果不需要进度信息，可以使用ProgressCallback接口的实例，或者null。
     * @param contextIds               一组包含要运行的上下文id的字符串。只有在该列表中指定id的上下文将被运行。如果列表是null或空的，那么所有上下文都是运行的。
     * @param fullyQualifiedTableNames 生成的一组表名。该集合的元素必须是与配置中指定的字符串完全匹配的字符串。例如，如果表名=“foo”和schema =“bar”，那么完全限定的表名是“foo.bar”。如果设置为null或空，则配置中的所有表将用于代码生成。
     * @throws SQLException         SQL 异常
     * @throws IOException          发出I/O异常的信号。
     * @throws InterruptedException 如果该方法被取消，则通过ProgressCallback。
     */
    public void generate(ProgressCallback callback, Set<String> contextIds,
                         Set<String> fullyQualifiedTableNames) throws SQLException,
            IOException, InterruptedException {
        generate(callback, contextIds, fullyQualifiedTableNames, true);
    }

    /**
     * 这是生成代码的主要方法。这种方法是长期运行的，但是可以提供进展，并且可以通过ProgressCallback接口来取消该方法。
     *
     * @param callback                 如果不需要进度信息，可以使用ProgressCallback接口的实例，或者null。
     * @param contextIds               一组包含要运行的上下文id的字符串。只有在该列表中指定id的上下文将被运行。如果列表是null或空的，那么所有上下文都是运行的。
     * @param fullyQualifiedTableNames 生成的一组表名。该集合的元素必须是与配置中指定的字符串完全匹配的字符串。例如，如果表名=“foo”和schema =“bar”，那么完全限定的表名是“foo.bar”。如果设置为null或空，则配置中的所有表将用于代码生成。
     * @param writeFiles               如果是真的，那么生成的文件将被写入磁盘。如果是假的，那么生成器就运行，但没有写入任何内容。
     * @throws SQLException         SQL 异常
     * @throws IOException          发出I/O异常的信号。
     * @throws InterruptedException 如果该方法被取消，则通过ProgressCallback。
     */
    public void generate(ProgressCallback callback, Set<String> contextIds,
                         Set<String> fullyQualifiedTableNames, boolean writeFiles) throws SQLException,
            IOException, InterruptedException {

        if (callback == null) {
            callback = new NullProgressCallback();
        }

        generatedJavaFiles.clear();
        generatedXmlFiles.clear();
        ObjectFactory.reset();
        RootClassInfo.reset();

        // calculate the contexts to run
        List<Context> contextsToRun;
        if (contextIds == null || contextIds.size() == 0) {
            contextsToRun = configuration.getContexts();
        } else {
            contextsToRun = new ArrayList<Context>();
            for (Context context : configuration.getContexts()) {
                if (contextIds.contains(context.getId())) {
                    contextsToRun.add(context);
                }
            }
        }

        // setup custom classloader if required
        if (configuration.getClassPathEntries().size() > 0) {
            ClassLoader classLoader = getCustomClassloader(configuration.getClassPathEntries());
            ObjectFactory.addExternalClassLoader(classLoader);
        }

        // now run the introspections...
        int totalSteps = 0;
        for (Context context : contextsToRun) {
            totalSteps += context.getIntrospectionSteps();
        }
        callback.introspectionStarted(totalSteps);

        // 要生成的Table
        for (Context context : contextsToRun) {
            context.introspectTables(callback, warnings,
                    fullyQualifiedTableNames);
        }

        // now run the generates
        totalSteps = 0;
        for (Context context : contextsToRun) {
            totalSteps += context.getGenerationSteps();
        }
        callback.generationStarted(totalSteps);

        for (Context context : contextsToRun) {
            context.generateFiles(callback, generatedJavaFiles,
                    generatedXmlFiles, warnings);
        }

        // now save the files
        if (writeFiles) {
            callback.saveStarted(generatedXmlFiles.size()
                    + generatedJavaFiles.size());

            // 写入xml文件
            for (GeneratedXmlFile gxf : generatedXmlFiles) {
                projects.add(gxf.getTargetProject());
                writeGeneratedXmlFile(gxf, callback);
            }

            // 写入 mapper  model 文件
            for (GeneratedJavaFile gjf : generatedJavaFiles) {
                projects.add(gjf.getTargetProject());
                writeGeneratedJavaFile(gjf, callback);
            }


            // 写入service文件
            for (Context context : contextsToRun) {

                try {
                    List<GenServiceContext> genServiceContexts = context.generateServiceFile();
                    for (GenServiceContext gen : genServiceContexts) {
                        PackageContext packageContext = gen.getPackageContext();
                        TableContext tableContext = gen.getTableContext();
                        freemarker.template.Configuration configuration = new freemarker.template.Configuration(freemarker.template.Configuration.VERSION_2_3_28);
                        configuration.setDefaultEncoding(Charset.forName("UTF-8").name());
                        configuration.setClassForTemplateLoading(MyBatisGeneratorFix.class, "/");
                        Template template = configuration.getTemplate("templates/service.java.ftl");
                        DefaultShellCallback shellCallback = new DefaultShellCallback(true);
                        File directory = shellCallback.getDirectory(packageContext.getPackageTargetName(), packageContext.getPackageName());
                        if (!directory.isDirectory()) {
                            directory.mkdirs();
                        }
                        // 已经存在的service
                        File existingFile = new File(directory, tableContext.getServiceName() + ".java");
                        File tempFile = new File(System.getProperty("java.io.tmpdir"), tableContext.getServiceName() + ".java");


                        Map<String, Object> map = new HashMap<>();
                        map.put("package", packageContext);
                        map.put("table", tableContext);
                        map.put("author", gen.getAuthor());
                        map.put("date", gen.getDate());
                        // 合并
                        if (existingFile.exists()) {
                            // 临时文件
                            FileOutputStream fileOutputStreamTemp = new FileOutputStream(tempFile);
                            template.process(map, new OutputStreamWriter(fileOutputStreamTemp, Charset.forName("UTF-8").name()));
                            fileOutputStreamTemp.close();
                            String source = new JavaFileMergerJaxp().getNewJavFile(existingFile, tempFile);
                            writeFile(existingFile,source,"utf-8");
                            tempFile.delete();
                        }else{
                            // 最终生成文件
                            FileOutputStream fileOutputStream = new FileOutputStream(existingFile);
                            template.process(map, new OutputStreamWriter(fileOutputStream, Charset.forName("UTF-8").name()));
                            fileOutputStream.close();
                        }
                    }

                } catch (Exception e) {
                    throw new RuntimeException("生成service报错了");
                }
            }


            for (String project : projects) {
                shellCallback.refreshProject(project);
            }
        }

        callback.done();
    }


    private void writeGeneratedJavaFile(GeneratedJavaFile gjf, ProgressCallback callback)
            throws InterruptedException, IOException {
        File targetFile;
        String source;
        try {
            File directory = shellCallback.getDirectory(gjf
                    .getTargetProject(), gjf.getTargetPackage());
            targetFile = new File(directory, gjf.getFileName());
            if (targetFile.exists()) {
                if (gjf.isMergeable()) {
//                    source = shellCallback.mergeJavaFile(gjf
//                            .getFormattedContent(), targetFile,
//                            MergeConstants.OLD_ELEMENT_TAGS,
//                            gjf.getFileEncoding());
                    // 合并Java代码
                    source = new JavaFileMergerJaxp().getNewJavaFile(gjf.getFormattedContent(), targetFile.getAbsolutePath());
                } else if (shellCallback.isOverwriteEnabled()) {
                    source = gjf.getFormattedContent();
                    warnings.add(getString("Warning.11", //$NON-NLS-1$
                            targetFile.getAbsolutePath()));
                } else {
                    source = gjf.getFormattedContent();
                    targetFile = getUniqueFileName(directory, gjf
                            .getFileName());
                    warnings.add(getString(
                            "Warning.2", targetFile.getAbsolutePath())); //$NON-NLS-1$
                }
            } else {
                source = gjf.getFormattedContent();
            }

            callback.checkCancel();
            callback.startTask(getString(
                    "Progress.15", targetFile.getName())); //$NON-NLS-1$
            writeFile(targetFile, source, gjf.getFileEncoding());
        } catch (ShellException e) {
            warnings.add(e.getMessage());
        }
    }

    private void writeGeneratedXmlFile(GeneratedXmlFile gxf, ProgressCallback callback)
            throws InterruptedException, IOException {
        File targetFile;
        String source;
        try {
            File directory = shellCallback.getDirectory(gxf
                    .getTargetProject(), gxf.getTargetPackage());
            targetFile = new File(directory, gxf.getFileName());
            if (targetFile.exists()) {
                if (gxf.isMergeable()) {
                    source = XmlFileMergerJaxp.getMergedSource(gxf,
                            targetFile);
                } else if (shellCallback.isOverwriteEnabled()) {
                    source = gxf.getFormattedContent();
                    warnings.add(getString("Warning.11", //$NON-NLS-1$
                            targetFile.getAbsolutePath()));
                } else {
                    source = gxf.getFormattedContent();
                    targetFile = getUniqueFileName(directory, gxf
                            .getFileName());
                    warnings.add(getString(
                            "Warning.2", targetFile.getAbsolutePath())); //$NON-NLS-1$
                }
            } else {
                source = gxf.getFormattedContent();
            }

            callback.checkCancel();
            callback.startTask(getString(
                    "Progress.15", targetFile.getName())); //$NON-NLS-1$
            writeFile(targetFile, source, "UTF-8"); //$NON-NLS-1$
        } catch (ShellException e) {
            warnings.add(e.getMessage());
        }
    }

    /**
     * Writes, or overwrites, the contents of the specified file.
     *
     * @param file         the file
     * @param content      the content
     * @param fileEncoding the file encoding
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private void writeFile(File file, String content, String fileEncoding) throws IOException {
        FileOutputStream fos = new FileOutputStream(file, false);
        OutputStreamWriter osw;
        if (fileEncoding == null) {
            osw = new OutputStreamWriter(fos);
        } else {
            osw = new OutputStreamWriter(fos, fileEncoding);
        }

        BufferedWriter bw = new BufferedWriter(osw);
        bw.write(content);
        bw.close();
    }

    /**
     * Gets the unique file name.
     *
     * @param directory the directory
     * @param fileName  the file name
     * @return the unique file name
     */
    private File getUniqueFileName(File directory, String fileName) {
        File answer = null;

        // try up to 1000 times to generate a unique file name
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < 1000; i++) {
            sb.setLength(0);
            sb.append(fileName);
            sb.append('.');
            sb.append(i);

            File testFile = new File(directory, sb.toString());
            if (!testFile.exists()) {
                answer = testFile;
                break;
            }
        }

        if (answer == null) {
            throw new RuntimeException(getString(
                    "RuntimeError.3", directory.getAbsolutePath())); //$NON-NLS-1$
        }

        return answer;
    }

    /**
     * Returns the list of generated Java files after a call to one of the generate methods.
     * This is useful if you prefer to process the generated files yourself and do not want
     * the generator to write them to disk.
     *
     * @return the list of generated Java files
     */
    public List<GeneratedJavaFile> getGeneratedJavaFiles() {
        return generatedJavaFiles;
    }

    /**
     * Returns the list of generated XML files after a call to one of the generate methods.
     * This is useful if you prefer to process the generated files yourself and do not want
     * the generator to write them to disk.
     *
     * @return the list of generated XML files
     */
    public List<GeneratedXmlFile> getGeneratedXmlFiles() {
        return generatedXmlFiles;
    }
}
