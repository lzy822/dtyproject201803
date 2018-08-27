package com.esri.arcgisruntime.demos.displaymap;

import org.litepal.crud.LitePalSupport;

import java.util.List;

public class memoryxzqinfo extends LitePalSupport{
    private String name;
    private String keyAndValues;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKeyAndValues() {
        return keyAndValues;
    }

    public void setKeyAndValues(String keyAndValues) {
        this.keyAndValues = keyAndValues;
    }
}
