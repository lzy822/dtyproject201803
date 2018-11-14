package com.esri.arcgisruntime.demos.displaymap;

import org.litepal.crud.LitePalSupport;

public class DMBZ extends LitePalSupport {
    private String XH;
    private String DY;
    private String MC;
    private String BZMC;
    private String XZQMC;
    private String XZQDM;
    private String SZDW;
    private String SCCJ;
    private String GG;
    private String IMGPATH;
    private String TAPEPATH;
    private float lat;
    private float lng;
    private String time;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public float getLat() {
        return lat;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public float getLng() {
        return lng;
    }

    public void setLng(float lng) {
        this.lng = lng;
    }

    public String getXH() {
        return XH;
    }

    public void setXH(String XH) {
        this.XH = XH;
    }

    public String getDY() {
        return DY;
    }

    public void setDY(String DY) {
        this.DY = DY;
    }

    public String getIMGPATH() {
        return IMGPATH;
    }

    public void setIMGPATH(String IMGPATH) {
        this.IMGPATH = IMGPATH;
    }

    public String getTAPEPATH() {
        return TAPEPATH;
    }

    public void setTAPEPATH(String TAPEPATH) {
        this.TAPEPATH = TAPEPATH;
    }

    public String getMC() {
        return MC;
    }

    public void setMC(String MC) {
        this.MC = MC;
    }

    public String getXZQDM() {
        return XZQDM;
    }

    public void setXZQDM(String XZQDM) {
        this.XZQDM = XZQDM;
    }

    public String getBZMC() {
        return BZMC;
    }

    public void setBZMC(String BZMC) {
        this.BZMC = BZMC;
    }

    public String getXZQMC() {
        return XZQMC;
    }

    public void setXZQMC(String XZQMC) {
        this.XZQMC = XZQMC;
    }

    public String getSZDW() {
        return SZDW;
    }

    public void setSZDW(String SZDW) {
        this.SZDW = SZDW;
    }

    public String getSCCJ() {
        return SCCJ;
    }

    public void setSCCJ(String SCCJ) {
        this.SCCJ = SCCJ;
    }

    public String getGG() {
        return GG;
    }

    public void setGG(String GG) {
        this.GG = GG;
    }
}
