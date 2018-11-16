package org.mybatis.generator.support.internal;


import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.comments.Comment;
import org.mybatis.generator.config.MergeConstants;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import static org.mybatis.generator.api.dom.OutputUtilities.newLine;

public class JavaFileMergerJaxp {
    public String getNewJavaFile(String newFileSource, String existingFileFullPath) throws FileNotFoundException {
        CompilationUnit newCompilationUnit = JavaParser.parse(newFileSource);
        CompilationUnit existingCompilationUnit = JavaParser.parse(new File(existingFileFullPath));
        return mergerFile(newCompilationUnit, existingCompilationUnit);
    }

    public String getNewJavFile(File existingFile,File newFile ) throws FileNotFoundException {
        CompilationUnit newCompilationUnit = JavaParser.parse(newFile);
        CompilationUnit existingCompilationUnit = JavaParser.parse(existingFile);
        return mergerFile(newCompilationUnit, existingCompilationUnit);
    }

    @SuppressWarnings("unchecked")
    public String mergerFile(CompilationUnit newCompilationUnit, CompilationUnit existingCompilationUnit) {

        System.out.println("合并java代码：");
        StringBuilder sb = new StringBuilder(newCompilationUnit.getPackageDeclaration().get().toString());
        newCompilationUnit.removePackageDeclaration();



        //合并imports
        NodeList<ImportDeclaration> imports = newCompilationUnit.getImports();
        imports.addAll(existingCompilationUnit.getImports());
        Set importSet = new HashSet<ImportDeclaration>();
        importSet.addAll(imports);

        NodeList<ImportDeclaration> newImports = new NodeList<ImportDeclaration>();
        newImports.addAll(importSet);
        newCompilationUnit.setImports(newImports);
        for (ImportDeclaration i : newCompilationUnit.getImports()) {
            sb.append(i.toString());
        }
        newLine(sb);
        NodeList<TypeDeclaration<?>> types = newCompilationUnit.getTypes();
        NodeList<TypeDeclaration<?>> oldTypes = existingCompilationUnit.getTypes();

        for (int i = 0; i < types.size(); i++) {
            //截取Class
            String classNameInfo = types.get(i).toString().substring(0, types.get(i).toString().indexOf("{") + 1);
            sb.append(classNameInfo);
            newLine(sb);
            newLine(sb);
            //合并fields
            List<FieldDeclaration> fields = types.get(i).getFields();
            List<FieldDeclaration> oldFields = oldTypes.get(i).getFields();

            List<FieldDeclaration> newFields = new ArrayList<FieldDeclaration>();


            newFields.addAll(fields);

            // 判断老的代码
            for (FieldDeclaration f : oldFields) {
                Optional<Comment> comment = f.getComment();
                if (comment.isPresent()) {
                    boolean iscont = false;
                    for (String tag : MergeConstants.OLD_ELEMENT_TAGS) {
                        if (comment.toString().contains(tag)) {
                            iscont = true;
                            break;
                        }
                    }
                    if (!iscont){
                        newFields.add(f);
                    }
                }else{
                    newFields.add(f);
                }

            }

            for (FieldDeclaration f : newFields) {
                sb.append("\t" + f.toString());
                newLine(sb);
                newLine(sb);
            }


            // 合并构造方法
            NodeList<BodyDeclaration<?>> members = types.get(i).getMembers();
            for(BodyDeclaration b : members){
             if(  b instanceof ConstructorDeclaration){
                 String res = b.toString().replaceAll("\r\n", "\r\n\t");
                 sb.append("\t" + res);
                 newLine(sb);
                 newLine(sb);
             }
            }

            //合并methods
            List<MethodDeclaration> methods = types.get(i).getMethods();     // 新生成的方法
            List<MethodDeclaration> existingMethods = oldTypes.get(i).getMethods();   // 原来的方法

            for (MethodDeclaration f : methods) {
                String res = f.toString().replaceAll("\r\n", "\r\n\t");
                sb.append("\t" + res);
                newLine(sb);
                newLine(sb);
            }

            List<String> methodList = new ArrayList<String>();
            for (MethodDeclaration m : methods) {
                methodList.add(m.getName().toString());
            }
            methodList.add("toString");
            methodList.add("hashCode");
            methodList.add("equals");

            for (MethodDeclaration m : existingMethods) {
                if (methodList.contains(m.getName().toString())) {
                    continue;
                }

                boolean flag = true;
                for (String tag : MergeConstants.OLD_ELEMENT_TAGS) {
                    if (m.toString().contains(tag)) {
                        flag = false;
                        break;
                    }
                }
                if (flag) {
                    String res = m.toString().replaceAll("\r\n", "\r\n\t");
                    sb.append("\t" + res);
                    newLine(sb);
                    newLine(sb);
                }
            }

            //判断是否有内部类
            types.get(i).getChildNodes();
            for (Node n : types.get(i).getChildNodes()) {
                if (n.toString().contains("static class")) {
                    String res = n.toString().replaceAll("\r\n", "\r\n\t");
                    sb.append("\t" + res);
                }
            }

        }

        return sb.append(System.getProperty("line.separator") + "}").toString();
    }

}