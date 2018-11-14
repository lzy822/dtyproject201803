package com.esri.arcgisruntime.demos.displaymap;

import org.litepal.crud.LitePalSupport;

/**
 * Created by 54286 on 2018/3/19.
 */

public class MPHOTO extends LitePalSupport {
    private int id;
    private String pdfic;
    private String poic;
    private String path;
    private String time;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPdfic() {
        return pdfic;
    }

    public void setPdfic(String pdfic) {
        this.pdfic = pdfic;
    }

    public String getPoic() {
        return poic;
    }

    public void setPoic(String poic) {
        this.poic = poic;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
