package com.esri.arcgisruntime.demos.displaymap;

import com.esri.arcgisruntime.geometry.PointCollection;
import com.esri.arcgisruntime.geometry.Polyline;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;

public class geometry_WhiteBlank {
    private SimpleLineSymbol lineSymbol;
    private Polyline polyline;

    public geometry_WhiteBlank() {
    }

    public geometry_WhiteBlank(SimpleLineSymbol lineSymbol, Polyline polyline) {
        this.lineSymbol = lineSymbol;
        this.polyline = polyline;
    }

    public SimpleLineSymbol getLineSymbol() {
        return lineSymbol;
    }

    public void setLineSymbol(SimpleLineSymbol lineSymbol) {
        this.lineSymbol = lineSymbol;
    }

    public Polyline getPolyline() {
        return polyline;
    }

    public void setPolyline(Polyline polyline) {
        this.polyline = polyline;
    }

    public void setLineSymbol(int color_Whiteblank, int width) {
        this.lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, color_Whiteblank, width);
    }

    public void setPolyline(PointCollection points) {
        this.polyline = new Polyline(points);
    }

    public Graphic getFillGraphic() {
        return new Graphic(polyline, lineSymbol);
    }
}
