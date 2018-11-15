package com.esri.arcgisruntime.demos.displaymap;

public enum DisplayEnum {
    INQUERY, FINISHQUERY, NOQUERY,//按需查询功能
    I_QUREY, I_NOQUREY,//查询功能
    TDGHDL_FEATURE, XZQ_FEATURE,
    DRAW_POLYGON, DRAW_POLYLINE, DRAW_POINT, DRAW_NONE,//绘图功能
    ANA_NONE, ANA_NEED, ANA_XZQ, ANA_DISTANCE, ANA_AREA,//分析功能
    FUNC_ADDPOI, FUNC_ANA, FUNC_NONE,//正在进行的操作
    ADD_ALBUM, ADD_TAKEPHOTO, ADD_TAPE, ADD_HANDDRAW//添加兴趣点的方式
}