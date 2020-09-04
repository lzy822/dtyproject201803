package com.esri.arcgisruntime.demos.displaymap;

import com.esri.arcgisruntime.geometry.Polygon;

public class ChartQueryResult {
    private String TypeName;
    private Double Area;
    private Polygon polygon;

    public ChartQueryResult(String typeName, Double area, Polygon polygon) {
        TypeName = typeName;
        Area = area;
        this.polygon = polygon;
    }

    public String getTypeName() {
        return TypeName;
    }

    public void setTypeName(String typeName) {
        TypeName = typeName;
    }

    public Double getArea() {
        return Area;
    }

    public void setArea(Double area) {
        Area = area;
    }

    public Polygon getPolygon() {
        return polygon;
    }

    public void setPolygon(Polygon polygon) {
        this.polygon = polygon;
    }
}
