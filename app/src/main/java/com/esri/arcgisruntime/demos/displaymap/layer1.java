package com.esri.arcgisruntime.demos.displaymap;

import com.esri.arcgisruntime.layers.Layer;

public class layer1 {
    private Layer layer;
    private int num;

    public layer1(Layer layer, int num) {
        this.layer = layer;
        this.num = num;
    }

    public Layer getLayer() {
        return layer;
    }

    public void setLayer(Layer layer) {
        this.layer = layer;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }
}
