package org.mybatis.generator.support.api;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author: kfc
 * @Description: <br/>
 * Date:Create in 2018/5/10 15:11
 * @Modified By:
 */
public class GenServiceContext {
    private PackageContext packageContext;
    private TableContext tableContext;

    private String author;
    private String date;

    public GenServiceContext (){}

    public GenServiceContext(PackageContext packageContext, TableContext tableContext) {
        this.packageContext = packageContext;
        this.tableContext = tableContext;
        this.author = System.getProperties().getProperty("user.name");
        this.date = (new SimpleDateFormat("yyyy-MM-dd")).format(new Date());
    }


    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public PackageContext getPackageContext() {
        return packageContext;
    }

    public void setPackageContext(PackageContext packageContext) {
        this.packageContext = packageContext;
    }

    public TableContext getTableContext() {
        return tableContext;
    }

    public void setTableContext(TableContext tableContext) {
        this.tableContext = tableContext;
    }
}
