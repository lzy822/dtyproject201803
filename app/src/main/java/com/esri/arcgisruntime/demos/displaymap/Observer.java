package com.esri.arcgisruntime.demos.displaymap;

public abstract class Observer {
    protected Subject subject;
    public abstract void update();
}
