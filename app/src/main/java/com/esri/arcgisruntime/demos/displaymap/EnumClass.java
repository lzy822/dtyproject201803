package com.esri.arcgisruntime.demos.displaymap;

public class EnumClass {
    //记录测量类型（距离测量或面积测量）
    public final static int MESSURE_NONE_TYPE = 0;
    public final static int MESSURE_DISTANCE_TYPE = 1;
    public final static int MESSURE_AREA_TYPE = 2;

    //记录坐标显示类型（经纬度或投影坐标）
    public final static int COORDINATE_DEFAULT_TYPE = 0;
    public final static int COORDINATE_BLH_TYPE = 1;
    public final static int COORDINATE_XYZ_TYPE = 2;

    //记录当前坐标获取模式类型（中心点或非中心点）
    public static final int CENTERMODE = -1;
    public static final int NOCENTERMODE = -2;

    //记录当前简单查询类型（红线查询或兴趣点查询）
    public static final int RED_LINE_QUERY = 1;
    public static final int POI_QUERY = -1;

    //记录当前绘图类型
    public static final int POI_DRAW_TYPE = 2;
    public static final int TRAIL_DRAW_TYPE = 1;
    public static final int NONE_DRAW_TYPE = 0;
    public static final int LINE_DRAW_TYPE = 3;
    public final static int SEARCH_DEMO = -3;

    //记录当前使用者放缩情况
    public static final int ZOOM_NONE = 0;
    public static final int ZOOM_OUT = 1;
    public static final int ZOOM_IN = 2;

    //记录申请动态权限的字符串
    public static final String READ_EXTERNAL_STORAGE = "android.permission.READ_EXTERNAL_STORAGE";
    public static final String WRITE_EXTERNAL_STORAGE = "android.permission.WRITE_EXTERNAL_STORAGE";

    //记录示例文件的地址
    public static final String SAMPLE_FILE = "dt/图志简介.dt";

    //记录请求码
    public final static int REQUEST_CODE_PHOTO = 42;
    public final static int REQUEST_CODE_TAPE = 43;
    public static final int TAKE_PHOTO = 119;
    public static final int PERMISSION_CODE = 42042;
    public static final int GET_TIF_FILE = 17;
    public static final int GET_SHP_FILE = 18;
    public static final int GET_PDF_FILE = 19;

    //记录当前pdf文件的读取类型
    public static final int NONE_FILE_TYPE = 0;
    public static final int FILE_FILE_TYPE = 1;
    public static final int ASSET_FILE_TYPE = 2;
}
