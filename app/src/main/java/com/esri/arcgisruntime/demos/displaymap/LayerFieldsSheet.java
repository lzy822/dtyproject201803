package com.esri.arcgisruntime.demos.displaymap;

import com.esri.arcgisruntime.layers.FeatureLayer;

import java.util.List;

public class LayerFieldsSheet {
    private String LayerShowName;
    private String LayerPath;
    private FeatureLayer featureLayer;
    private Boolean visible;

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    public String getLayerShowName() {
        return LayerShowName;
    }

    public void setLayerShowName(String layerShowName) {
        LayerShowName = layerShowName;
    }

    List<FieldNameSheet> FieldNameSheetList;

    public FeatureLayer getFeatureLayer() {
        return featureLayer;
    }

    public void setFeatureLayer(FeatureLayer featureLayer) {
        this.featureLayer = featureLayer;
        getLayerShowName(featureLayer.getName());
    }

    public List<FieldNameSheet> getFieldNameSheetList() {
        return FieldNameSheetList;
    }

    public void setFieldNameSheetList(List<FieldNameSheet> fieldNameSheetList) {
        FieldNameSheetList = fieldNameSheetList;
    }

    public LayerFieldsSheet(String name, String path, List<FieldNameSheet> fieldNameSheetList) {
        FieldNameSheetList = fieldNameSheetList;
        LayerPath = path;
        getLayerShowName(name);
    }

    public String getLayerPath() {
        return LayerPath;
    }

    public void setLayerPath(String layerPath) {
        LayerPath = layerPath;
    }

    public LayerFieldsSheet(FeatureLayer featureLayer, String path, List<FieldNameSheet> fieldNameSheetList) {
        this.featureLayer = featureLayer;
        FieldNameSheetList = fieldNameSheetList;
        getLayerShowName(featureLayer.getName());
        LayerPath = path;
    }

    private void getLayerShowName(String name){
        switch (name){
            case "DGX":
                LayerShowName = "等高线";
                break;
            case "STBHHX":
                LayerShowName = "生态保护红线";
                break;
            case "YJJBNT":
                LayerShowName = "永久基本农田";
                break;
            case "XZQ":
                LayerShowName = "乡镇级行政区";
                break;
            case "PDT":
                LayerShowName = "坡度图";
                break;
            case "DLTB_3diao":
                LayerShowName = "三调地类图斑";
                break;
            case "DLTB_2diao":
                LayerShowName = "二调地类图斑";
                break;
            case "DK":
                LayerShowName = "农村土地承包经营权";
                break;
            case "CJDCQ":
                LayerShowName = "村级调查区";
                break;
            default:
                LayerShowName = name;
                break;
        }
    }
}
