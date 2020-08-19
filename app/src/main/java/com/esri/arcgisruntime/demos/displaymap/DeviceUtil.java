package com.esri.arcgisruntime.demos.displaymap;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DeviceUtil {
    //不可使用，因为不同应用的正式包在相同设备上得到的ANDROID_ID结果不同？
    public static String GetAndroidID(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    //获取音频文件路径
    public static String getRealPathFromUriForAudio(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Audio.Media.DATA };
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public static String renamePath(String path){
        File file = new File(path);
        path = path.substring(0, path.lastIndexOf(".")) + ".dt";
        file.renameTo(new File(path));
        return path;
    }

    //计算识别码
    public static String getPassword1(String deviceId){
        //String password;
        //password = "l" + encryption(deviceId) + "ZY";
        SimpleDateFormat df = new SimpleDateFormat(MyApplication.getContext().getResources().getText(R.string.Date_1).toString());
        Date nowDate = new Date(System.currentTimeMillis());

        return encryption(deviceId) + encryption1(df.format(nowDate));
    }

    //计算识别码
    public static String getPassword1ForAndroid10(String deviceId){
        //String password;
        //password = "l" + encryption(deviceId) + "ZY";
        SimpleDateFormat df = new SimpleDateFormat(MyApplication.getContext().getResources().getText(R.string.Date_1).toString());
        Date nowDate = new Date(System.currentTimeMillis());

        return encryptionForAndroid10(deviceId) + encryption1(df.format(nowDate));
    }

    //编码
    public static String encryption(String password){
        password = password.replace("0", "q");
        password = password.replace("1", "R");
        password = password.replace("2", "V");
        password = password.replace("3", "z");
        password = password.replace("4", "T");
        password = password.replace("5", "b");
        password = password.replace("6", "L");
        password = password.replace("7", "s");
        password = password.replace("8", "W");
        password = password.replace("9", "F");
        password = password.replace("A", "d");
        password = password.replace("B", "o");
        password = password.replace("C", "O");
        password = password.replace("D", "n");
        password = password.replace("E", "v");
        password = password.replace("F", "C");
        return password;
    }

    //编码
    public static String encryptionForAndroid10(String password){
        password = password.replace("0", "q");
        password = password.replace("1", "R");
        password = password.replace("2", "V");
        password = password.replace("3", "z");
        password = password.replace("4", "T");
        password = password.replace("5", "b");
        password = password.replace("6", "L");
        password = password.replace("7", "s");
        password = password.replace("8", "W");
        password = password.replace("9", "F");
        password = password.replace("a", "d");
        password = password.replace("b", "o");
        password = password.replace("c", "O");
        password = password.replace("d", "n");
        password = password.replace("e", "v");
        password = password.replace("f", "C");
        return password;
    }

    //编码
    public static String encryption1(String password){
        password = password.replace("0", "a");
        password = password.replace("1", "Z");
        password = password.replace("2", "o");
        password = password.replace("3", "M");
        password = password.replace("4", "e");
        password = password.replace("5", "w");
        password = password.replace("6", "z");
        password = password.replace("7", "R");
        password = password.replace("8", "D");
        password = password.replace("9", "q");
        return password;
    }

    //反转授权码
    public static String reverseStr(String str){
        return str.substring(6, 10) + str.substring(0, 6) + str.substring(10);
    }

    //恢复反转授权码
    public static String reReverseStr(String str){
        return str.substring(4,10) + str.substring(0, 4) + str.substring(10);
    }

    //日期提取算法
    public static String getDateFromStr(String password){
        password = password.replace("a", "0");
        password = password.replace("Z", "1");
        password = password.replace("o", "2");
        password = password.replace("M", "3");
        password = password.replace("e", "4");
        password = password.replace("w", "5");
        password = password.replace("z", "6");
        password = password.replace("R", "7");
        password = password.replace("D", "8");
        password = password.replace("q", "9");
        password = password.substring(0, 4) + "年" + password.substring(4, 6) + "月" + password.substring(6, 8) + "日";
        return password;
    }

    //核对日期
    public static boolean verifyDate(String endDate){
        SimpleDateFormat df = new SimpleDateFormat(MyApplication.getContext().getResources().getText(R.string.Date).toString());
        Date nowDate = new Date(System.currentTimeMillis());
        Date endTimeDate = null;
        try {
            if (!endDate.isEmpty()){
                endTimeDate = df.parse(endDate);
            }
        }catch (ParseException e){
            Toast.makeText(MyApplication.getContext(), R.string.InputLicenseError + "_3", Toast.LENGTH_LONG).show();
        }
        if (nowDate.getTime() > endTimeDate.getTime()){
            return false;
        }else return true;
    }

    //日期加法
    public static String datePlus(String day, int days) {
        SimpleDateFormat df = new SimpleDateFormat(MyApplication.getContext().getResources().getText(R.string.Date).toString());
        Date base = null;
        try {
            base = df.parse(day);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(base);
        cal.add(Calendar.DATE, days);
        String dateOK = df.format(cal.getTime());

        return dateOK;
    }
}
