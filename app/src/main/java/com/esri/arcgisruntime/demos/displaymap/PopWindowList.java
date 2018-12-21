package com.esri.arcgisruntime.demos.displaymap;

public class PopWindowList {
    private String name;
    private Double jbnt;
    private Double hd;
    private Double yld;
    private Double qtld;
    private Double ld;
    private Double gmld;
    private Double ncjmdyd;
    private Double zrbld;
    private Double yd;
    private Double st;
    private Double ktsm;
    private Double tt;
    private Double ssnyd;
    private Double ckyd;
    private Double tsyd;
    private Double czyd;
    private Double sgjzyd;
    private Double glyd;
    private Double sjd;
    private Double hlsm;
    private Double qtdljsyd;
    private Double fjmsssyd;
    private Double sksm;
    private Double ncdl;
    private Double mcd;
    private Double myjcyd;
    private Double gkmtyd;
    private Double hpsm;

    public PopWindowList(String data) {
        String[] keyAndValue = data.split(",");
        ld = 0.0;
        for (int i = 0; i < keyAndValue.length; ++i){
            String[] keyAndValue1 = keyAndValue[i].split(":");
            switch (keyAndValue1[0]){
                case "图层":
                    name = keyAndValue1[1];
                    break;
                case "基本农田保护区":
                    jbnt = Double.valueOf(keyAndValue1[1]);
                    break;
                case "旱地":
                    hd = Double.valueOf(keyAndValue1[1]);
                    break;
                case "林地":
                    ld = Double.valueOf(keyAndValue1[1]);
                    break;
                case "有林地":
                    yld = Double.valueOf(keyAndValue1[1]);
                    ld += yld;
                    break;
                case "灌木林地":
                    gmld = Double.valueOf(keyAndValue1[1]);
                    ld += gmld;
                    break;
                case "其他林地":
                    qtld = Double.valueOf(keyAndValue1[1]);
                    ld += qtld;
                    break;
                case "农村居民点用地":
                    ncjmdyd = Double.valueOf(keyAndValue1[1]);
                    break;
                case "自然保留地":
                    zrbld = Double.valueOf(keyAndValue1[1]);
                    break;
                case "园地":
                    yd = Double.valueOf(keyAndValue1[1]);
                    break;
                case "水田":
                    st = Double.valueOf(keyAndValue1[1]);
                    break;
                case "坑塘水面":
                    ktsm = Double.valueOf(keyAndValue1[1]);
                    break;
                case "滩涂":
                    tt = Double.valueOf(keyAndValue1[1]);
                    break;
                case "设施农用地":
                    ssnyd = Double.valueOf(keyAndValue1[1]);
                    break;
                case "采矿用地":
                    ckyd = Double.valueOf(keyAndValue1[1]);
                    break;
                case "特殊用地":
                    tsyd = Double.valueOf(keyAndValue1[1]);
                    break;
                case "城镇用地":
                    czyd = Double.valueOf(keyAndValue1[1]);
                    break;
                case "水工建筑用地":
                    sgjzyd = Double.valueOf(keyAndValue1[1]);
                    break;
                case "公路用地":
                    glyd = Double.valueOf(keyAndValue1[1]);
                    break;
                case "水浇地":
                    sjd = Double.valueOf(keyAndValue1[1]);
                    break;
                case "河流水面":
                    hlsm = Double.valueOf(keyAndValue1[1]);
                    break;
                case "其他独立建设用地":
                    qtdljsyd = Double.valueOf(keyAndValue1[1]);
                    break;
                case "风景名胜设施用地":
                    fjmsssyd = Double.valueOf(keyAndValue1[1]);
                    break;
                case "水库水面":
                    sksm = Double.valueOf(keyAndValue1[1]);
                    break;
                case "农村道路":
                    ncdl = Double.valueOf(keyAndValue1[1]);
                    break;
                case "牧草地":
                    mcd = Double.valueOf(keyAndValue1[1]);
                    break;
                case "港口码头用地":
                    gkmtyd = Double.valueOf(keyAndValue1[1]);
                    break;
                case "湖泊水面":
                    hpsm = Double.valueOf(keyAndValue1[1]);
                    break;
            }
        }
    }

    public static String titleToString(){
        return "图层名" +
                ",基本农田（亩）" +
                ",旱地（亩）" +
                ",林地（亩）" +
                ",有林地（亩）" +
                ",其他林地（亩）" +
                ",灌木林地（亩）" +
                ",农村居民点用地（亩）" +
                ",自然保留地（亩）" +
                ", 园地（亩）" +
                ",水田（亩）" +
                ",坑塘水面（亩）" +
                ",滩涂（亩）" +
                ",设施农用地（亩）" +
                ",采矿用地（亩）" +
                ",特殊用地（亩）" +
                ",城镇用地（亩）" +
                ",水工建筑用地（亩）" +
                ",公路用地（亩）" +
                ",水浇地（亩）" +
                ",河流水面（亩）" +
                ",其他独立建设用地（亩）" +
                ",风景名胜设施用地（亩）" +
                ",水库水面（亩）" +
                ",农村道路（亩）" +
                ",牧草地（亩）" +
                ",民用机场用地（亩）" +
                ",港口码头用地（亩）" +
                ",湖泊水面（亩）";
    }

    @Override
    public String toString() {
        return name +
                "," + jbnt +
                "," + hd +
                "," + (ld+yld+qtld+gmld) +
                "," + yld +
                "," + qtld +
                "," + gmld +
                "," + ncjmdyd +
                "," + zrbld +
                "," + yd +
                "," + st +
                "," + ktsm +
                "," + tt +
                "," + ssnyd +
                "," + ckyd +
                "," + tsyd +
                "," + czyd +
                "," + sgjzyd +
                "," + glyd +
                "," + sjd +
                "," + hlsm +
                "," + qtdljsyd +
                "," + fjmsssyd +
                "," + sksm +
                "," + ncdl +
                "," + mcd +
                "," + myjcyd +
                "," + gkmtyd +
                "," + hpsm;
    }

    public Double getYld() {
        return yld;
    }

    public void setYld(Double yld) {
        this.yld = yld;
    }

    public Double getQtld() {
        return qtld;
    }

    public void setQtld(Double qtld) {
        this.qtld = qtld;
    }

    public Double getGmld() {
        return gmld;
    }

    public void setGmld(Double gmld) {
        this.gmld = gmld;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getJbnt() {
        return jbnt;
    }

    public void setJbnt(Double jbnt) {
        this.jbnt = jbnt;
    }

    public Double getHd() {
        return hd;
    }

    public void setHd(Double hd) {
        this.hd = hd;
    }

    public Double getLd() {
        return ld;
    }

    public void setLd(Double ld) {
        this.ld = ld;
    }

    public Double getNcjmdyd() {
        return ncjmdyd;
    }

    public void setNcjmdyd(Double ncjmdyd) {
        this.ncjmdyd = ncjmdyd;
    }

    public Double getZrbld() {
        return zrbld;
    }

    public void setZrbld(Double zrbld) {
        this.zrbld = zrbld;
    }

    public Double getYd() {
        return yd;
    }

    public void setYd(Double yd) {
        this.yd = yd;
    }

    public Double getSt() {
        return st;
    }

    public void setSt(Double st) {
        this.st = st;
    }

    public Double getKtsm() {
        return ktsm;
    }

    public void setKtsm(Double ktsm) {
        this.ktsm = ktsm;
    }

    public Double getTt() {
        return tt;
    }

    public void setTt(Double tt) {
        this.tt = tt;
    }

    public Double getSsnyd() {
        return ssnyd;
    }

    public void setSsnyd(Double ssnyd) {
        this.ssnyd = ssnyd;
    }

    public Double getCkyd() {
        return ckyd;
    }

    public void setCkyd(Double ckyd) {
        this.ckyd = ckyd;
    }

    public Double getTsyd() {
        return tsyd;
    }

    public void setTsyd(Double tsyd) {
        this.tsyd = tsyd;
    }

    public Double getCzyd() {
        return czyd;
    }

    public void setCzyd(Double czyd) {
        this.czyd = czyd;
    }

    public Double getSgjzyd() {
        return sgjzyd;
    }

    public void setSgjzyd(Double sgjzyd) {
        this.sgjzyd = sgjzyd;
    }

    public Double getGlyd() {
        return glyd;
    }

    public void setGlyd(Double glyd) {
        this.glyd = glyd;
    }

    public Double getSjd() {
        return sjd;
    }

    public void setSjd(Double sjd) {
        this.sjd = sjd;
    }

    public Double getHlsm() {
        return hlsm;
    }

    public void setHlsm(Double hlsm) {
        this.hlsm = hlsm;
    }

    public Double getQtdljsyd() {
        return qtdljsyd;
    }

    public void setQtdljsyd(Double qtdljsyd) {
        this.qtdljsyd = qtdljsyd;
    }

    public Double getFjmsssyd() {
        return fjmsssyd;
    }

    public void setFjmsssyd(Double fjmsssyd) {
        this.fjmsssyd = fjmsssyd;
    }

    public Double getSksm() {
        return sksm;
    }

    public void setSksm(Double sksm) {
        this.sksm = sksm;
    }

    public Double getNcdl() {
        return ncdl;
    }

    public void setNcdl(Double ncdl) {
        this.ncdl = ncdl;
    }

    public Double getMcd() {
        return mcd;
    }

    public void setMcd(Double mcd) {
        this.mcd = mcd;
    }

    public Double getMyjcyd() {
        return myjcyd;
    }

    public void setMyjcyd(Double myjcyd) {
        this.myjcyd = myjcyd;
    }

    public Double getGkmtyd() {
        return gkmtyd;
    }

    public void setGkmtyd(Double gkmtyd) {
        this.gkmtyd = gkmtyd;
    }

    public Double getHpsm() {
        return hpsm;
    }

    public void setHpsm(Double hpsm) {
        this.hpsm = hpsm;
    }
}
