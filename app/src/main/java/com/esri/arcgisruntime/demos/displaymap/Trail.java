package com.esri.arcgisruntime.demos.displaymap;

import org.litepal.crud.LitePalSupport;

import java.util.List;

/**
 * 路径记录类
 * 用于存储记录的路径信息，与地图相关联
 * 该内容有对应的数据表
 *
 * @author  李正洋
 *
 * @since   1.3
 * Created by 54286 on 2018/3/15.
 */

public class Trail extends LitePalSupport {
    private int id;
    private String ic;
    private String name;
    private String path;
    private String starttime;
    private String endtime;
    private float maxlat;
    private float maxlng;
    private float minlat;
    private float minlng;

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

    public String getStarttime() {
        return starttime;
    }

    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }

    public String getEndtime() {
        return endtime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }

    public String getIc() {
        return ic;
    }

    public void setIc(String ic) {
        this.ic = ic;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}

