package com.esri.arcgisruntime.demos.displaymap;

import com.esri.arcgisruntime.geometry.Polyline;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;

import org.litepal.crud.DataSupport;

public class whiteblank extends DataSupport{
    private String pts;
    private int color;

    public String getPts() {
        return pts;
    }

    public void setPts(String pts) {
        this.pts = pts;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
