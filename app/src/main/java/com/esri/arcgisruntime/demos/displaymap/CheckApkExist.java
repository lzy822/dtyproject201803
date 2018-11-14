package com.esri.arcgisruntime.demos.displaymap;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

public class CheckApkExist {
    private static String BaiduMapPkgName = "com.baidu.BaiduMap";
    private static String TencentMapPkgName = "com.tencent.map";
    private static String GaodeMapPkgName = "com.autonavi.minimap";

    public static boolean checkApkExist(Context context, String packageName){
        if (TextUtils.isEmpty(packageName))
            return false;
        try {
            ApplicationInfo info = context.getPackageManager()
                    .getApplicationInfo(packageName,
                            PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static boolean checkTencentMapExist(Context context){
        return checkApkExist(context, TencentMapPkgName);
    }

    public static boolean checkGaodeMapExist(Context context){
        return checkApkExist(context, GaodeMapPkgName);
    }

    public static boolean checkBaiduMapExist(Context context){
        return checkApkExist(context, BaiduMapPkgName);
    }
}
