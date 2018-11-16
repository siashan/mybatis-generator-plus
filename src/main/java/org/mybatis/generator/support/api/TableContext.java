package org.mybatis.generator.support.api;

/**
 * @Author: kfc
 * @Description: <br/>
 * Date:Create in 2018/5/10 9:53
 * @Modified By:
 */
public class TableContext {
    private String serviceName;
    private String mapper;
    private String model;
    private String example;

    private String comment;

    public TableContext(){}

    public TableContext(String serviceName, String mapper, String model, String example, String comment) {
        this.serviceName = serviceName;
        this.mapper = mapper;
        this.model = model;
        this.example = example;
        this.comment = comment;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
