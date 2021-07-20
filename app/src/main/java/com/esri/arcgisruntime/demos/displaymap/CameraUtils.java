package com.esri.arcgisruntime.demos.displaymap;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;

import java.io.File;
import java.io.IOException;

public class CameraUtils {
    public static final String CAMERA_IMAGE = "/sdcard/test.jpg";

    public static final String CAMERA_VIDEO = "/sdcard/test.mp4";

    private static final int REQUEST_EXTERNAL_STORAGE = 1;

    private static String[] PERMISSIONS_CAMERA = {
            Manifest.permission.CAMERA,
    };

    private static String[] PERMISSIONS_VIDEO = {
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
    };

    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE };

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        System.out.println("检测权限有没有"+permission);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            //动态获取权限
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE);
        }
    }

    public static void verifyCameraPermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.CAMERA);
        System.out.println("检测拍照权限有没有"+permission);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            //动态获取权限
            ActivityCompat.requestPermissions(activity,PERMISSIONS_CAMERA ,
                    REQUEST_EXTERNAL_STORAGE);
        }
    }

    public static void verifyVideoPermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.RECORD_AUDIO);
        System.out.println("检测录像权限有没有"+permission);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            //动态获取权限
            ActivityCompat.requestPermissions(activity,PERMISSIONS_VIDEO ,
                    REQUEST_EXTERNAL_STORAGE);
        }
    }

    public static void pickImageFromSystem(Activity activity)
    {
        verifyStoragePermissions(activity);   //在这里手动获取权限   我不知道是我真机MIUI12的问题还是所有android6上下  和android10上下都有

        /**
         * 参数一:打开系统相册的ACTION 参数二:返回数据的方式(从系统相册的数据库获取)
         */
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        activity.startActivityForResult(intent,
                RequestCode.FLAG_REQUEST_SYSTEM_IMAGE);
    }

    public static void pickVedioFromSystem(Activity activity)
    {
        verifyStoragePermissions(activity);   //在这里手动获取权限   我不知道是我真机MIUI12的问题还是所有android6上下  和android10上下都有

        /**
         * 参数一:打开系统相册的ACTION 参数二:返回数据的方式(从系统相册的数据库获取)
         */
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        activity.startActivityForResult(intent,
                RequestCode.FLAG_REQUEST_SYSTEM_IMAGE);
    }

    public static String getImagePathFromSystem(Activity activity, Intent data)
    {

        // TODO 处理从相册返回的图片数据
        Uri uri = data.getData();// 使用getData方法获取要调用的接口
        // 第二个参数表示要查询的数据的字段名
        Cursor c = activity.getContentResolver().query(uri,
                new String[]{MediaStore.Images.Media.DATA}, null, null, null);
        if (c != null)
        {
            c.moveToFirst();
            // 通过游标来获取名为MediaStore.Images.Media.DATA字段的值
            String path = c.getString(c
                    .getColumnIndex(MediaStore.Images.Media.DATA));
            return path;
        }
        return null;
    }

    public static void openCameraForImage(Activity activity)
    {
        //检查是否有相机权限
        verifyCameraPermissions(activity);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 仅当设置了MediaStore.EXTRA_OUTPUT参数时,系统将不再返回缩略图,而是会被完整保存以下路径
        //        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(CAMERA_IMAGE)));
        activity.startActivityForResult(intent,
                CameraUtils.RequestCode.FLAG_REQUEST_CAMERA_IMAGE);
    }

    public static void openCameraForVideo(Activity activity)
    {
        //检查是否有拍摄权限
        verifyVideoPermissions(activity);
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        // 仅当设置了MediaStore.EXTRA_OUTPUT参数时,系统将不再返回缩略图,而是会被完整保存以下路径
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(CAMERA_VIDEO)));
        activity.startActivityForResult(intent,
                RequestCode.FLAG_REQUEST_CAMERA_VIDEO);
    }

    public interface RequestCode
    {
        int FLAG_REQUEST_SYSTEM_IMAGE = -1;

        int FLAG_REQUEST_SYSTEM_VIDEO = 0;

        int FLAG_REQUEST_CAMERA_IMAGE = 1;

        int FLAG_REQUEST_CAMERA_VIDEO = 2;
    }

}

