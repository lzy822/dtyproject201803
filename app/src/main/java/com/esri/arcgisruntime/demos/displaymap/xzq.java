package com.esri.arcgisruntime.demos.displaymap;

import org.litepal.crud.LitePalSupport;

public class xzq extends LitePalSupport {
    private String xzqdm;
    private String xzqmc;
    private String sjxzq;
    private String type;
    private int grade;

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public String getXzqdm() {
        return xzqdm;
    }

    public void setXzqdm(String xzqdm) {
        this.xzqdm = xzqdm;
    }

    public String getXzqmc() {
        return xzqmc;
    }

    public void setXzqmc(String xzqmc) {
        this.xzqmc = xzqmc;
    }

    public String getSjxzq() {
        return sjxzq;
    }

    public void setSjxzq(String sjxzq) {
        this.sjxzq = sjxzq;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
