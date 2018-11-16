package org.mybatis.generator.support.api;

/**
 * @Author: kfc
 * @Description: <br/>
 * Date:Create in 2018/5/10 9:45
 * @Modified By:
 */
public class PackageContext {
    private String packageTargetName;
    private String packageName;
    private String mapper;
    private String model;
    private String example;
    private String implementService;

    public PackageContext(){

    }

    public PackageContext(String packageName, String mapper, String model, String example,String packageTargetName,String implementService) {
        this.packageName = packageName;
        this.mapper = mapper;
        this.model = model;
        this.example = example;
        this.packageTargetName = packageTargetName;
        this.implementService = implementService;
    }

    public String getImplementService() {
        return implementService;
    }

    public void setImplementService(String implementService) {
        this.implementService = implementService;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getMapper() {
        return mapper;
    }

    public void setMapper(String mapper) {
        this.mapper = mapper;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }

    public String getPackageTargetName() {
        return packageTargetName;
    }

    public void setPackageTargetName(String packageTargetName) {
        this.packageTargetName = packageTargetName;
    }
}
