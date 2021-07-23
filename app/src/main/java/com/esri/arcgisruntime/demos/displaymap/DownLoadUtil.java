package com.esri.arcgisruntime.demos.displaymap;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class DownLoadUtil {
    /** sd卡目录路径 */
    private String sdcard;

    // public DownLoadUtil(String url)
    // {
    // this.urlstr = url;
    // //获取设备sd卡目录
    // this.sdcard = Environment.getExternalStorageDirectory() + "/";
    // urlcon = getConnection();
    // }
    public String downloadFile(String filename,String u) {
        try {
            // 判断SD卡是否存在，并且是否具有读写权限
            if (Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
                // 获得存储卡的路径
                String sdpath = Environment.getExternalStorageDirectory() + "/";
                sdcard = sdpath + "download/";

                String finalUrl = u + filename;
                Log.e("finalUrl", finalUrl);
                URL url = new URL(finalUrl);

                URLConnection rulConnection = url.openConnection();
                HttpURLConnection conn = (HttpURLConnection) rulConnection;
                // 设定请求的方法为"POST"，默认是GET
                conn.setRequestMethod("POST");
                conn.connect();

                // 创建输入流
                InputStream is = conn.getInputStream();

                File file = new File(sdcard);
                // 判断文件目录是否存在
                if (!file.exists()) {
                    file.mkdir();
                }

                String filename2 = filename
                        .substring(filename.lastIndexOf("/") + 1);

                File apkFile = new File(sdcard, urldecodeUTF8(filename2));
                // System.out.println(apkFile);
                FileOutputStream fos = new FileOutputStream(apkFile);

                // 写入到文件中

                int len = 2048;
                // 缓存
                byte buf[] = new byte[len];
                while ((len = is.read(buf)) != -1) {
                    // 写入文件
                    fos.write(buf, 0, len);
                }
                fos.close();
                is.close();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 关闭下载对话框显示
        return sdcard;

    }

    public static String urlEncodeUTF8(String source) {
        String result = source;
        try {
            result = java.net.URLEncoder.encode(source, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String urldecodeUTF8(String source) {
        String result = source;
        try {
            result = java.net.URLDecoder.decode(source, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }
}
