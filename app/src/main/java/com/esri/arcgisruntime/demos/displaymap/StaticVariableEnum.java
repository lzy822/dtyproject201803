package com.esri.arcgisruntime.demos.displaymap;

import android.os.Environment;

public class StaticVariableEnum {
    public static final String READ_EXTERNAL_STORAGE = "android.permission.READ_EXTERNAL_STORAGE";
    public static final String WRITE_EXTERNAL_STORAGE = "android.permission.WRITE_EXTERNAL_STORAGE";
    public static final String ACCESS_FINE_LOCATION = "android.permission.ACCESS_FINE_LOCATION";
    public static final String ACCESS_COARSE_LOCATION = "android.permission.ACCESS_COARSE_LOCATION";
    public static final String INTERNET = "android.permission.INTERNET";
    public static final int PERMISSION_CODE = 42042;
    public static final int PERMISSION_CODE_1 = 42043;
    public static final int PERMISSION_CODE_2 = 42044;
    public static final String GDBROOTPATH = Environment.getExternalStorageDirectory().toString() + "/临沧市基本农田/临沧市5309省标准乡级土地利用总体规划及基本农田数据库2000.geodatabase";
    public static final String MMPKROOTPATH = Environment.getExternalStorageDirectory().toString() + "/临沧市基本农田/临沧市土地利用规划和基本农田数据.mmpk";
    public static final String PGDBROOTPATH = Environment.getExternalStorageDirectory().toString() + "/临沧市基本农田/临沧市地类图斑p图斑数据库.geodatabase";
}
