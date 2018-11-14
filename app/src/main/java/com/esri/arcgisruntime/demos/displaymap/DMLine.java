package com.esri.arcgisruntime.demos.displaymap;

import org.litepal.crud.LitePalSupport;

import java.util.List;

public class DMLine extends LitePalSupport {
    private String xh;
    private String qydm;
    private String lbdm;
    private String bzmc;
    private String cym;
    private String jc;
    private String bm;
    private String dfyz;
    private String zt;
    private String dmll;
    private String dmhy;
    private String lsyg;
    private String dlstms;
    private String zlly;
    private String imgpath;
    private String tapepath;
    private String time;
    private List<String> multiline;
    private int linenum;
    private float maxlat;
    private float maxlng;
    private float minlat;
    private float minlng;
    private String dimingid;
    private String mapid;

    public int getLinenum() {
        return linenum;
    }

    public void setLinenum(int linenum) {
        this.linenum = linenum;
    }

    public String getDimingid() {
        return dimingid;
    }

    public void setDimingid(String dimingid) {
        this.dimingid = dimingid;
    }

    public String getMapid() {
        return mapid;
    }

    public void setMapid(String mapid) {
        this.mapid = mapid;
    }

    public String getQydm() {
        return qydm;
    }

    public void setQydm(String qydm) {
        this.qydm = qydm;
    }

    public String getXh() {
        return xh;
    }

    public void setXh(String xh) {
        this.xh = xh;
    }

    public String getLbdm() {
        return lbdm;
    }

    public void setLbdm(String lbdm) {
        this.lbdm = lbdm;
    }

    public List<String> getMultiline() {
        return multiline;
    }

    public void setMultiline(List<String> multiline) {
        this.multiline = multiline;
    }

    public float getMaxlat() {
        return maxlat;
    }

    public void setMaxlat(float maxlat) {
        this.maxlat = maxlat;
    }

    public float getMaxlng() {
        return maxlng;
    }

    public void setMaxlng(float maxlng) {
        this.maxlng = maxlng;
    }

    public float getMinlat() {
        return minlat;
    }

    public void setMinlat(float minlat) {
        this.minlat = minlat;
    }

    public float getMinlng() {
        return minlng;
    }

    public void setMinlng(float minlng) {
        this.minlng = minlng;
    }

    public String getBzmc() {
        return bzmc;
    }

    public void setBzmc(String bzmc) {
        this.bzmc = bzmc;
    }

    public String getCym() {
        return cym;
    }

    public void setCym(String cym) {
        this.cym = cym;
    }

    public String getJc() {
        return jc;
    }

    public void setJc(String jc) {
        this.jc = jc;
    }

    public String getBm() {
        return bm;
    }

    public void setBm(String bm) {
        this.bm = bm;
    }

    public String getDfyz() {
        return dfyz;
    }

    public void setDfyz(String dfyz) {
        this.dfyz = dfyz;
    }

    public String getZt() {
        return zt;
    }

    public void setZt(String zt) {
        this.zt = zt;
    }

    public String getDmll() {
        return dmll;
    }

    public void setDmll(String dmll) {
        this.dmll = dmll;
    }

    public String getDmhy() {
        return dmhy;
    }

    public void setDmhy(String dmhy) {
        this.dmhy = dmhy;
    }

    public String getLsyg() {
        return lsyg;
    }

    public void setLsyg(String lsyg) {
        this.lsyg = lsyg;
    }

    public String getDlstms() {
        return dlstms;
    }

    public void setDlstms(String dlstms) {
        this.dlstms = dlstms;
    }

    public String getZlly() {
        return zlly;
    }

    public void setZlly(String zlly) {
        this.zlly = zlly;
    }

    public String getImgpath() {
        return imgpath;
    }

    public void setImgpath(String imgpath) {
        this.imgpath = imgpath;
    }

    public String getTapepath() {
        return tapepath;
    }

    public void setTapepath(String tapepath) {
        this.tapepath = tapepath;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
