package com.esri.arcgisruntime.demos.displaymap;

import com.esri.arcgisruntime.geometry.Polyline;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;

import org.litepal.crud.LitePalSupport;

public class whiteblank extends LitePalSupport{
    private String ObjectID;
    private String pts;
    private int color;

    public String getObjectID() {
        return ObjectID;
    }

    public void setObjectID(String objectID) {
        ObjectID = objectID;
    }

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
