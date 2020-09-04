package com.esri.arcgisruntime.demos.displaymap;

public class FieldNameSheet {
    private String RealName;
    private String ShowName;

    public String getRealName() {
        return RealName;
    }

    public void setRealName(String realName) {
        RealName = realName;
    }

    public String getShowName() {
        return ShowName;
    }

    public void setShowName(String showName) {
        ShowName = showName;
    }

    public FieldNameSheet(String realName, String showName) {
        RealName = realName;
        ShowName = showName;
    }
}
