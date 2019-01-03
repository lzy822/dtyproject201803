package com.esri.arcgisruntime.demos.displaymap;

public class layer{
    private String type;
    private String path;
    private String name;
    private boolean status;
    private int num;

    public layer(String name) {
        this.name = name;
    }

    public layer(String name, int num) {
        this.name = name;
        this.num = num;
    }

    public layer(String type, String path, String name) {
        this.type = type;
        this.path = path;
        this.name = name;
    }

    public layer(String type, String path, String name, boolean status) {
        this.type = type;
        this.path = path;
        this.name = name;
        this.status = status;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public layer(String name, boolean status) {
        this.name = name;
        this.status = status;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public layer(String path, String name) {
        this.path = path;
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
