package com.esri.arcgisruntime.demos.displaymap;

public class QueryTaskInfo {
    private String xzq;
    private String xzqdm;
    private double area;
    private String typename;
    private String type;

    public QueryTaskInfo(String xzq, double area, String typename, String type) {
        this.xzq = xzq;
        this.area = area;
        this.typename = typename;
        this.type = type;
    }

    public QueryTaskInfo(double area) {
        this.area = area;
    }

    public String getXzqdm() {
        return xzqdm;
    }

    public void setXzqdm(String xzqdm) {
        this.xzqdm = xzqdm;
    }

    public String getXzq() {
        return xzq;
    }

    public void setXzq(String xzq) {
        this.xzq = xzq;
    }

    public double getArea() {
        return area;
    }

    public void setArea(double area) {
        this.area = area;
    }

    public String getTypename() {
        return typename;
    }

    public void setTypename(String typename) {
        this.typename = typename;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
