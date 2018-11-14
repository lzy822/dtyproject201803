package com.esri.arcgisruntime.demos.displaymap;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @auther lizhengyang
 *
 * @version 1.0
 */
public class DataUtil {

    private static final String TAG = "DataUtil";
    /**
     * @auther lizhengyang
     *
     * @version 1.0
     *
     * @param xzqList
     * Ref List<xzq>
     *
     * @return boolean
     * Ref 如果出现异常就返回false
     */
    public static boolean xzqClassify(List<xzq> xzqList){
        try {
            for (int i = 0; i < xzqList.size(); ++i){
                xzq xzq = new xzq();
                if (xzqList.get(i).getType().contains("县级")){
                    xzq.setGrade(1);
                }else if (xzqList.get(i).getType().contains("乡级")){
                    xzq.setGrade(2);
                }
                xzq.updateAll("xzqdm = ?", xzqList.get(i).getXzqdm());
            }
            return true;
        }catch (Exception e){
            return false;
        }
    }

    /**
     * @auther lizhengyang
     *
     * @version 1.0
     *
     * @param xzqList
     * Ref List<xzq>
     *
     * @return long[]
     *
     * Ref {number of grade1, number of grade2}
     */
    public static long[] xzqCalGrade(List<xzq> xzqList){
        try {
            int num1 = 0;
            int num2 = 0;
            for (int i = 0; i < xzqList.size(); ++i){
                if (xzqList.get(i).getGrade() == 1){
                    num1++;
                }else if (xzqList.get(i).getGrade() == 2){
                    num2++;
                }
            }
            return new long[]{num1, num2};
        }catch (Exception e){
            return new long[]{0, 0};
        }
    }

    /**
     * @auther lizhengyang
     *
     * @version 1.0
     *
     * @param xzqList
     *
     * @return List<xzq>
     */
    public static List<xzq> bubbleSortX(List<xzq> xzqList) {
        int len = xzqList.size();
        for (int i = 0; i < len - 1; ++i) {
            for (int j = 0; j < len - 1 - i; ++j) {
                if (Long.valueOf(xzqList.get(j).getXzqdm().substring(0, 6)) > Long.valueOf(xzqList.get(j + 1).getXzqdm().substring(0, 6))) {        // 相邻元素两两对比
                    xzq temp = xzqList.get(j + 1);        // 元素交换
                    xzqList.set(j + 1, xzqList.get(j));
                    xzqList.set(j, temp);
                }
            }
        }
        return xzqList;
    }

    /**
     * @auther lizhengyang
     *
     * @version 1.0
     *
     * @param xzqList
     *
     * @return List<xzq>
     */
    public static List<xzq> bubbleSortXZ(List<xzq> xzqList) {
        int len = xzqList.size();
        for (int i = 0; i < len - 1; ++i) {
            for (int j = 0; j < len - 1 - i; ++j) {
                if (xzqList.get(j).getXzqdm().length() == 9 && xzqList.get(j + 1).getXzqdm().length() == 9) {
                    if (Long.valueOf(xzqList.get(j).getXzqdm()) > Long.valueOf(xzqList.get(j + 1).getXzqdm())) {        // 相邻元素两两对比
                        xzq temp = xzqList.get(j + 1);        // 元素交换
                        xzqList.set(j + 1, xzqList.get(j));
                        xzqList.set(j, temp);
                    }
                }
            }
        }
        return xzqList;
    }

    /**
     * @auther lizhengyang
     *
     * @version 1.0
     *
     * @param xzqList
     *
     * @return List<xzq>
     */
    public static List<xzq> bubbleSort(List<xzq> xzqList) {
        return bubbleSortXZ(bubbleSortX(xzqList));
    }

    //找到某字符在字符串中出现的次数
    public static int appearNumber(String srcText, String findText) {
        int count = 0;
        Pattern p = Pattern.compile(findText);
        Matcher m = p.matcher(srcText);
        while (m.find()) {
            count++;
        }
        return count;
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

    //获取照片文件路径
    public static String getRealPathFromUriForPhoto(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    //获取File可使用路径
    public static String getRealPath(String filePath) {
        try {
            if (!filePath.contains("raw")) {
                String str = "content://com.android.tuzhi.fileprovider/external_files";
                String Dir = Environment.getExternalStorageDirectory().toString();
                filePath = Dir + filePath.substring(str.length());
            }else {
                filePath = filePath.substring(5);
                //locError("here");
                //locError(filePath);
            }
        }catch (Exception e){
            Log.w(TAG, e.toString());
        }

        return filePath;
    }



    public static int getPicRotate(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    public static void addPhotoToDB(String path, String ic, String poic, String time){
        MPHOTO mphoto = new MPHOTO();
        mphoto.setPdfic(ic);
        mphoto.setPoic(poic);
        mphoto.setPath(path);
        mphoto.setTime(time);
        mphoto.save();
    }

    public static void addTapeToDB(String path, String ic, String poic, String time){
        MTAPE mtape = new MTAPE();
        mtape.setPath(path);
        mtape.setPdfic(ic);
        mtape.setPoic(poic);
        mtape.setTime(time);
        mtape.save();
    }

    public static void addPOI(String ic, String poic, String name, float x, float y, String time){
        String[] strings = MyApplication.getContext().getResources().getStringArray(R.array.Type);
        POI poi = new POI();
        poi.setIc(ic);
        if (name.contains("图片")) poi.setPhotonum(1);
        else if (name.contains("录音")) poi.setTapenum(1);
        poi.setPoic(poic);
        poi.setName(name);
        poi.setX(x);
        poi.setY(y);
        poi.setType(strings[0]);
        poi.setTime(time);
        poi.save();
    }

    public static void addPOI(String ic, String poic, String name, float x, float y, String time, int num){
        String[] strings = MyApplication.getContext().getResources().getStringArray(R.array.Type);
        POI poi = new POI();
        poi.setIc(ic);
        if (name.contains("图片")) poi.setPhotonum(1);
        else if (name.contains("录音")) poi.setTapenum(1);
        poi.setPoic(poic);
        poi.setName(name);
        poi.setX(x);
        poi.setY(y);
        poi.setType(strings[num]);
        poi.setTime(time);
        poi.save();
    }

    //获取图片缩略图
    public static Bitmap getImageThumbnail(String imagePath, int width, int height) {
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        // 获取这个图片的宽和高，注意此处的bitmap为null
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        //Log.w(TAG, "getImageThumbnail: " + Integer.toString(options.outWidth) + ";" + Integer.toString(options.outHeight) );
        options.inJustDecodeBounds = false; // 设为 false
        // 计算缩放比
        int h = options.outHeight;
        int w = options.outWidth;
        int beWidth = w / width;
        int beHeight = h / height;
        int be = 1;
        if (beWidth < beHeight) {
            be = beWidth;
        } else {
            be = beHeight;
        }
        if (be <= 0) {
            be = 1;
        }
        options.inSampleSize = be;
        // 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为 false
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        // 利用ThumbnailUtils来创建缩略图，这里要指定要缩放哪个Bitmap对象
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }
}
