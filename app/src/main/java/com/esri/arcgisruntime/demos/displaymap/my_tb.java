package com.esri.arcgisruntime.demos.displaymap;

import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;

public class my_tb extends LitePalSupport {
    private String name;
    private String pointCollection;
    private String mdata;

    public String getMdata() {
        return mdata;
    }

    public void setMdata(String mdata) {
        this.mdata = mdata;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPointCollection() {
        return pointCollection;
    }

    public void setPointCollection(String pointCollection) {
        this.pointCollection = pointCollection;
    }
}
