package com.esri.arcgisruntime.demos.displaymap;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.PartCollection;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.PointCollection;
import com.esri.arcgisruntime.geometry.Polyline;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.geometry.SpatialReferences;

import org.litepal.LitePal;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.text.NumberFormat;
import java.util.ArrayList;
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
                }else if (xzqList.get(i).getType().contains("村级")){
                    xzq.setGrade(3);
                }
                xzq.updateAll("xzqdm = ?", xzqList.get(i).getXzqdm());
            }
            return true;
        }catch (Exception e){
            return false;
        }
    }

    public static void makeWhiteBlankKML(){
        final List<whiteblank> whiteBlanks = LitePal.findAll(whiteblank.class);
        int size_whiteBlanks = whiteBlanks.size();
        List<PointCollection> plist = new ArrayList<>();
        for (int i = 0; i < size_whiteBlanks; ++i) {
            PointCollection points = new PointCollection(SpatialReference.create(4523));
            //geometry_WhiteBlank geometryWhiteBlank = new geometry_WhiteBlank(whiteblanks.get(i).getLineSymbol(), whiteblanks.get(i).getPolyline());
            String[] strings = whiteBlanks.get(i).getPts().split("lzy");
            Log.w(TAG, "drawWhiteBlank1: " + strings.length);
            for (int kk = 0; kk < strings.length; ++kk) {
                String[] strings1 = strings[kk].split(",");
                if (strings1.length == 2) {
                    Log.w(TAG, "drawWhiteBlank2: " + strings1[0] + "; " + strings1[1]);
                    com.esri.arcgisruntime.geometry.Point wgs84Point = (com.esri.arcgisruntime.geometry.Point) GeometryEngine.project(new Point(Double.valueOf(strings1[0]), Double.valueOf(strings1[1])), SpatialReference.create(4523));
                    points.add(wgs84Point);
                }
            }
            Polyline polyline = (Polyline)GeometryEngine.project(new Polyline(points), SpatialReference.create(4523));
            PartCollection parts = new PartCollection(polyline.getParts());
            PointCollection pointCollection = new PointCollection(parts.getPartsAsPoints());
            plist.add(pointCollection);
        }
        //PointCollection polyline = GeometryEngine.project(points, SpatialReference.create(4490));
        StringBuffer sb = new StringBuffer();
        makeKMLHead(sb, "WhiteBlank");
        for (int i = 0; i < size_whiteBlanks; ++i){
            sb.append("    ").append("<Placemark id=\"ID_").append(plusID(i)).append("\">").append("\n");
            sb.append("      ").append("<name>").append(i).append("</name>").append("\n");
            sb.append("      ").append("<Snippet></Snippet>").append("\n");
            //属性表内容
            sb = makeCDATAHead(sb);
            sb = makeCDATATail(sb);
            sb.append("      ").append("<styleUrl>#LineStyle00</styleUrl>").append("\n");
            sb.append("      ").append("<MultiGeometry>").append("\n");
            sb.append("        ").append("<LineString>").append("\n");
            sb.append("          ").append("<extrude>0</extrude>").append("\n");
            sb.append("          ").append("<tessellate>1</tessellate><altitudeMode>clampToGround</altitudeMode>").append("\n");
            String[] lines_str = whiteBlanks.get(i).getPts().split("lzy");
            Log.w(TAG, "onClick: 2020/9/7: " + whiteBlanks.get(i).getPts());
            StringBuffer str = new StringBuffer();
            /*for (int k = 0; k < lines_str.length; ++k) {
                str.append(" ").append(lines_str[k]).append(",").append("0");
            }*/
            for (int j = 0; j < plist.get(i).size(); j++) {
                str.append(" ").append(plist.get(i).get(j).getX()).append(",").append(plist.get(i).get(j).getY()).append(",").append("0");
            }
            sb.append("          ").append("<coordinates>").append(str).append("</coordinates>").append("\n");
            sb.append("        ").append("</LineString>").append("\n");
            sb.append("      ").append("</MultiGeometry>").append("\n");
            sb.append("    ").append("</Placemark>").append("\n");
            //
        }
        sb = makeKMLTailForLine(sb);
        File file = new File(Environment.getExternalStorageDirectory() + "/TuZhi/" + "/Output");
        if (!file.exists() && !file.isDirectory()){
            file.mkdirs();
        }
        String outputPath = Long.toString(System.currentTimeMillis());
        File file1 = new File(Environment.getExternalStorageDirectory() + "/TuZhi/" + "/Output",  "白板" + outputPath + ".kml");
        try {
            FileOutputStream of = new FileOutputStream(file1);
            of.write(sb.toString().getBytes());
            of.close();
        }catch (IOException e){
            Log.w(TAG, e.toString());
        }
    }

    //获取照片文件路径
    public static String getRealPathFromUriForVedio(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Video.Media.DATA };
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /**
     * 保存bitmap到本地
     *
     * @param bitmap Bitmap
     */
    public static void saveBitmap(Bitmap bitmap, String path) {
        String savePath;
        File filePic;
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            savePath = path;
        } else {
            Log.e("tag", "saveBitmap failure : sdcard not mounted");
            return;
        }
        try {
            filePic = new File(savePath);
            if (!filePic.exists()) {
                filePic.getParentFile().mkdirs();
                filePic.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(filePic);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            Log.e("tag", "saveBitmap: " + e.getMessage());
            return;
        }
        Log.i("tag", "saveBitmap success: " + filePic.getAbsolutePath());
    }

    public static void getSpatialIndex(){
        List<Trail> trails = LitePal.findAll(Trail.class);
        for (int i = 0; i < trails.size(); ++i) {
            Log.w(TAG, "getSpatialIndex2: " + trails.get(i).getPath());
            float[] spatialIndex = getSpatialIndex(trails.get(i).getPath());
            Trail l = new Trail();
            l.setMaxlat(spatialIndex[0]);
            l.setMinlat(spatialIndex[1]);
            l.setMaxlng(spatialIndex[2]);
            l.setMinlng(spatialIndex[3]);
            l.updateAll("name = ?", trails.get(i).getName());
        }
    }
    public static float[] getSpatialIndex(String line){
        String[] strings = line.split(" ");
        float maxlat = 0;
        float minlat = 0;
        float maxlng = 0;
        float minlng = 0;
        for (int i = 0; i < strings.length; i = i + 2){
            Log.w(TAG, "recordTrail: " + strings[i]);
            float temp = Float.valueOf(strings[i]);
            if (temp > maxlat) maxlat = temp;
            else if (temp < minlat) minlat = temp;
        }
        for (int i = 1; i < strings.length; i = i + 2){
            float temp = Float.valueOf(strings[i]);
            if (temp > maxlng) maxlng = temp;
            else if (temp < minlng) minlng = temp;
        }
        float[] spatialIndex = {maxlat, minlat, maxlng, minlng};
        return spatialIndex;
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
                String str = "content://com.android.displaymap.fileprovider/external_files";
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

    public static void makeKML(){
        List<File> files = new ArrayList<File>();
        //POI
        List<POI> pois = LitePal.findAll(POI.class);
        if (pois.size() > 0) {
            files.add(makePOIKML(pois));
        }
        //Trail
        //List<Trail> trails = LitePal.findAll(Trail.class);
        //if (trails.size() > 0) files.add(makeTrailKML(trails));
        //Lines_WhiteBlank
        //List<Lines_WhiteBlank> whiteBlanks = LitePal.findAll(Lines_WhiteBlank.class);
        //if (whiteBlanks.size() > 0) files.add(makeWhiteBlankKML(whiteBlanks));
    }

    public static String plusID(int num){
        String str = "";
        if (num >= 0 & num < 10) str = "0000" + String.valueOf(num);
        else if (num >= 10 & num < 100) str = "000" + String.valueOf(num);
        else if (num >= 100 & num < 1000) str = "00" + String.valueOf(num);
        else if (num >= 1000 & num < 10000) str = "0" + String.valueOf(num);
        else str = String.valueOf(num);
        return str;
    }

    public static StringBuffer makeKMLHead(StringBuffer sb, String str){
        sb = sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>").append("\n");
        sb = sb.append("<kml xmlns=\"http://www.opengis.net/kml/2.2\" xmlns:gx=\"http://www.google.com/kml/ext/2.2\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"").append("\n");
        sb = sb.append(" xsi:schemaLocation=\"http://www.opengis.net/kml/2.2 http://schemas.opengis.net/kml/2.2.0/ogckml22.xsd http://www.google.com/kml/ext/2.2 http://code.google.com/apis/kml/schema/kml22gx.xsd\">").append("\n");
        sb = sb.append("<Document id=\"" + str + "\">").append("\n");
        sb = sb.append("  ").append("<name>" + str + "</name>").append("\n");
        sb = sb.append("  ").append("<Snippet></Snippet>").append("\n");
        if (str.equals("WhiteBlank")) sb.append("  ").append("<description><![CDATA[界线]]></description>").append("\n");
        sb = sb.append("  ").append("<Folder id=\"FeatureLayer0\">").append("\n");
        sb = sb.append("    ").append("<name>" + str + "</name>").append("\n");
        sb = sb.append("    ").append("<Snippet></Snippet>").append("\n");
        if (str.equals("WhiteBlank")) sb.append("    ").append("<description><![CDATA[界线]]></description>").append("\n");
        return sb;
    }

    public static StringBuffer makeCDATAHead(StringBuffer sb){
        sb.append("      ").append("<description><![CDATA[<html xmlns:fo=\"http://www.w3.org/1999/XSL/Format\" xmlns:msxsl=\"urn:schemas-microsoft-com:xslt\">").append("\n");
        sb.append("\n");
        sb.append("<head>").append("\n");
        sb.append("\n");
        sb.append("<META http-equiv=\"Content-Type\" content=\"text/html\">").append("\n");
        sb.append("\n");
        sb.append("<meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\">").append("\n");
        sb.append("\n");
        sb.append("</head>").append("\n");
        sb.append("\n");
        sb.append("<body style=\"margin:0px 0px 0px 0px;overflow:auto;background:#FFFFFF;\">").append("\n");
        sb.append("\n");
        sb.append("<table style=\"font-family:Arial,Verdana,Times;font-size:12px;text-align:left;width:100%;border-collapse:collapse;padding:3px 3px 3px 3px\">").append("\n");
        sb.append("\n");
        sb.append("<tr style=\"text-align:center;font-weight:bold;background:#9CBCE2\">").append("\n");
        sb.append("\n");
        return sb;
    }

    public static StringBuffer makeCDATATail(StringBuffer sb){
        sb.append("</table>").append("\n");
        sb.append("\n");
        sb.append("</td>").append("\n");
        sb.append("\n");
        sb.append("</tr>").append("\n");
        sb.append("\n");
        sb.append("</table>").append("\n");
        sb.append("\n");
        sb.append("</body>").append("\n");
        sb.append("\n");
        sb.append("</html>").append("\n");
        sb.append("\n");
        sb.append("]]></description>").append("\n");
        return sb;
    }

    public static StringBuffer makeKMLTail(StringBuffer sb){
        sb.append("  ").append("</Folder>").append("\n");
        sb.append("  ").append("<Style id=\"IconStyle00\">").append("\n");
        sb.append("    ").append("<IconStyle>").append("\n");
        sb.append("      ").append("<Icon><href>Layer0_Symbol_2017ee40_0.png</href></Icon>").append("\n");
        sb.append("      ").append("<scale>0.250000</scale>").append("\n");
        sb.append("    ").append("</IconStyle>").append("\n");
        sb.append("    ").append("<LabelStyle>").append("\n");
        sb.append("      ").append("<color>00000000</color>").append("\n");
        sb.append("      ").append("<scale>0.000000</scale>").append("\n");
        sb.append("    ").append("</LabelStyle>").append("\n");
        sb.append("    ").append("<PolyStyle>").append("\n");
        sb.append("      ").append("<color>ff000000</color>").append("\n");
        sb.append("      ").append("<outline>0</outline>").append("\n");
        sb.append("    ").append("</PolyStyle>").append("\n");
        sb.append("  ").append("</Style>").append("\n");
        sb.append("</Document>").append("\n");
        sb.append("</kml>").append("\n");
        return sb;
    }

    public static StringBuffer makeKMLTailForLine(StringBuffer sb){
        sb.append("  ").append("</Folder>").append("\n");
        sb.append("  ").append("<Style id=\"LineStyle00\">").append("\n");
        sb.append("    ").append("<LabelStyle>").append("\n");
        sb.append("      ").append("<color>00000000</color>").append("\n");
        sb.append("      ").append("<scale>0.000000</scale>").append("\n");
        sb.append("    ").append("</LabelStyle>").append("\n");
        sb.append("    ").append("<LabelStyle>").append("\n");
        sb.append("      ").append("<color>00000000</color>").append("\n");
        sb.append("      ").append("<scale>0.000000</scale>").append("\n");
        sb.append("    ").append("</LabelStyle>").append("\n");
        sb.append("    ").append("<LineStyle>").append("\n");
        sb.append("      ").append("<color>ff005aad</color>").append("\n");
        sb.append("      ").append("<width>1.000000</width>").append("\n");
        sb.append("    ").append("</LineStyle>").append("\n");
        sb.append("  ").append("</Style>").append("\n");
        sb.append("</Document>").append("\n");
        sb.append("</kml>").append("\n");
        return sb;
    }

    public static File makePOIKML(final List<POI> pois){
        StringBuffer sb = new StringBuffer();
        int size_POI = pois.size();
        makeKMLHead(sb, "POI");
        for (int i = 0; i < size_POI; ++i){
            sb.append("    ").append("<Placemark id=\"ID_").append(plusID(i)).append("\">").append("\n");
            sb.append("      ").append("<name>").append(pois.get(i).getPoic()).append("</name>").append("\n");
            sb.append("      ").append("<Snippet></Snippet>").append("\n");
            //属性表内容
            sb = makeCDATAHead(sb);
            sb.append("<td>").append(pois.get(i).getPoic()).append("</td>").append("\n");
            sb.append("\n");
            sb.append("</tr>").append("\n");
            sb.append("\n");
            sb.append("<tr>").append("\n");
            sb.append("\n");
            sb.append("<td>").append("\n");
            sb.append("\n");
            sb.append("<table style=\"font-family:Arial,Verdana,Times;font-size:12px;text-align:left;width:100%;border-spacing:0px; padding:3px 3px 3px 3px\">").append("\n");
            sb.append("\n");
            //
            sb.append("<tr>").append("\n");
            sb.append("\n");
            sb.append("<td>").append("id").append("</td>").append("\n");
            sb.append("\n");
            sb.append("<td>").append(pois.get(i).getId()).append("</td>").append("\n");
            sb.append("\n");
            sb.append("</tr>").append("\n");
            sb.append("\n");
            //
            //
            sb.append("<tr bgcolor=\"#D4E4F3\">").append("\n");
            sb.append("\n");
            sb.append("<td>").append("name").append("</td>").append("\n");
            sb.append("\n");
            sb.append("<td>").append(pois.get(i).getName()).append("</td>").append("\n");
            sb.append("\n");
            sb.append("</tr>").append("\n");
            sb.append("\n");
            //
            //
            sb.append("<tr bgcolor=\"#D4E4F3\">").append("\n");
            sb.append("\n");
            sb.append("<td>").append("ic").append("</td>").append("\n");
            sb.append("\n");
            sb.append("<td>").append(pois.get(i).getIc()).append("</td>").append("\n");
            sb.append("\n");
            sb.append("</tr>").append("\n");
            sb.append("\n");
            //
            //
            sb.append("<tr bgcolor=\"#D4E4F3\">").append("\n");
            sb.append("\n");
            sb.append("<td>").append("type").append("</td>").append("\n");
            sb.append("\n");
            sb.append("<td>").append(pois.get(i).getType()).append("</td>").append("\n");
            sb.append("\n");
            sb.append("</tr>").append("\n");
            sb.append("\n");
            //
            //
            sb.append("<tr bgcolor=\"#D4E4F3\">").append("\n");
            sb.append("\n");
            sb.append("<td>").append("POIC").append("</td>").append("\n");
            sb.append("\n");
            sb.append("<td>").append(pois.get(i).getPoic()).append("</td>").append("\n");
            sb.append("\n");
            sb.append("</tr>").append("\n");
            sb.append("\n");
            //
            //
            sb.append("<tr bgcolor=\"#D4E4F3\">").append("\n");
            sb.append("\n");
            sb.append("<td>").append("photoStr").append("</td>").append("\n");
            sb.append("\n");
            List<MPHOTO> mphotos = LitePal.where("poic = ?", pois.get(i).getPoic()).find(MPHOTO.class);
            String photoStr = "";
            for (int j = 0; j < mphotos.size(); ++j){
                if (j == 0){
                    photoStr = mphotos.get(j).getPath().substring(mphotos.get(j).getPath().lastIndexOf("/"), mphotos.get(j).getPath().length());
                }else photoStr = photoStr + "|" + mphotos.get(j).getPath().substring(mphotos.get(j).getPath().lastIndexOf("/") + 1, mphotos.get(j).getPath().length());
            }
            sb.append("<td>").append(photoStr).append("</td>").append("\n");
            sb.append("\n");
            sb.append("</tr>").append("\n");
            sb.append("\n");
            //
            //
            sb.append("<tr bgcolor=\"#D4E4F3\">").append("\n");
            sb.append("\n");
            sb.append("<td>").append("description").append("</td>").append("\n");
            sb.append("\n");
            sb.append("<td>").append(pois.get(i).getDescription()).append("</td>").append("\n");
            sb.append("\n");
            sb.append("</tr>").append("\n");
            sb.append("\n");
            //
            //
            sb.append("<tr bgcolor=\"#D4E4F3\">").append("\n");
            sb.append("\n");
            sb.append("<td>").append("tapeStr").append("</td>").append("\n");
            sb.append("\n");
            List<MTAPE> mtapes = LitePal.where("poic = ?", pois.get(i).getPoic()).find(MTAPE.class);
            String tapeStr = "";
            for (int j = 0; j < mtapes.size(); ++j){
                if (j == 0){
                    tapeStr = mtapes.get(j).getPath().substring(mtapes.get(j).getPath().lastIndexOf("/"), mtapes.get(j).getPath().length());
                }else tapeStr = tapeStr + "|" + mtapes.get(j).getPath().substring(mtapes.get(j).getPath().lastIndexOf("/") + 1, mtapes.get(j).getPath().length());
            }
            sb.append("<td>").append(tapeStr).append("</td>").append("\n");
            sb.append("\n");
            sb.append("</tr>").append("\n");
            sb.append("\n");
            //
            //
            sb.append("<tr bgcolor=\"#D4E4F3\">").append("\n");
            sb.append("\n");
            sb.append("<td>").append("time").append("</td>").append("\n");
            sb.append("\n");
            sb.append("<td>").append(pois.get(i).getTime()).append("</td>").append("\n");
            sb.append("\n");
            sb.append("</tr>").append("\n");
            sb.append("\n");
            //
            //
            sb.append("<tr bgcolor=\"#D4E4F3\">").append("\n");
            sb.append("\n");
            sb.append("<td>").append("x").append("</td>").append("\n");
            sb.append("\n");
            sb.append("<td>").append(pois.get(i).getX()).append("</td>").append("\n");
            sb.append("\n");
            sb.append("</tr>").append("\n");
            sb.append("\n");
            //
            //
            sb.append("<tr bgcolor=\"#D4E4F3\">").append("\n");
            sb.append("\n");
            sb.append("<td>").append("y").append("</td>").append("\n");
            sb.append("\n");
            sb.append("<td>").append(pois.get(i).getY()).append("</td>").append("\n");
            sb.append("\n");
            sb.append("</tr>").append("\n");
            sb.append("\n");
            //
            sb = makeCDATATail(sb);
            sb.append("      ").append("<styleUrl>#IconStyle00</styleUrl>").append("\n");
            sb.append("      ").append("<Point>").append("\n");
            sb.append("        ").append("<altitudeMode>clampToGround</altitudeMode>").append("\n");
            sb.append("        ").append("<coordinates>").append(" ").append(pois.get(i).getY()).append(",").append(pois.get(i).getX()).append(",").append(0).append("</coordinates>").append("\n");
            sb.append("      ").append("</Point>").append("\n");
            sb.append("    ").append("</Placemark>").append("\n");
            //
        }
        sb = makeKMLTail(sb);
        File file = new File(Environment.getExternalStorageDirectory() + "/TuZhi/" + "/Output");
        if (!file.exists() && !file.isDirectory()){
            file.mkdirs();
        }
        String outputPath = Long.toString(System.currentTimeMillis());
        File file1 = new File(Environment.getExternalStorageDirectory() + "/TuZhi/" + "/Output",  outputPath + ".kml");
        try {
            FileOutputStream of = new FileOutputStream(file1);
            of.write(sb.toString().getBytes());
            of.close();
        }catch (IOException e){
            Log.w(TAG, e.toString());
        }
        return file1;
    }

    public static File makeTapeKML(final List<MTAPE> mtapes, List<File> files){
        StringBuffer sb = new StringBuffer();
        int size_mtape = mtapes.size();
        makeKMLHead(sb, "MTAPE");
        for (int i = 0; i < size_mtape; ++i){
            sb.append("<id>").append(mtapes.get(i).getId()).append("</id>").append("\n");
            sb.append("<pdfic>").append(mtapes.get(i).getPdfic()).append("</pdfic>").append("\n");
            sb.append("<POIC>").append(mtapes.get(i).getPoic()).append("</POIC>").append("\n");
            String path = mtapes.get(i).getPath();
            sb.append("<path>").append(path).append("</path>").append("\n");
            files.add(new File(path));
            sb.append("<time>").append(mtapes.get(i).getTime()).append("</time>").append("\n");
        }
        sb.append("</MTAPE>").append("\n");
        File file = new File(Environment.getExternalStorageDirectory() + "/TuZhi/" + "/Output");
        if (!file.exists() && !file.isDirectory()){
            file.mkdirs();
        }
        String outputPath = Long.toString(System.currentTimeMillis());
        File file1 = new File(Environment.getExternalStorageDirectory() + "/TuZhi/" + "/Output",  outputPath + ".dtdb");
        try {
            FileOutputStream of = new FileOutputStream(file1);
            of.write(sb.toString().getBytes());
            of.close();
        }catch (IOException e){
            Log.w(TAG, e.toString());
        }
        return file1;
    }

    public static File makePhotoKML(final List<MPHOTO> mphotos, List<File> files){
        StringBuffer sb = new StringBuffer();
        int size_mphoto = mphotos.size();
        makeKMLHead(sb, "MPHOTO");
        for (int i = 0; i < size_mphoto; ++i){
            sb.append("<id>").append(mphotos.get(i).getId()).append("</id>").append("\n");
            sb.append("<pdfic>").append(mphotos.get(i).getPdfic()).append("</pdfic>").append("\n");
            sb.append("<POIC>").append(mphotos.get(i).getPoic()).append("</POIC>").append("\n");
            String path = mphotos.get(i).getPath();
            sb.append("<path>").append(path).append("</path>").append("\n");
            files.add(new File(path));
            sb.append("<time>").append(mphotos.get(i).getTime()).append("</time>").append("\n");
        }
        sb.append("</MPHOTO>").append("\n");
        File file = new File(Environment.getExternalStorageDirectory() + "/TuZhi/" + "/Output");
        if (!file.exists() && !file.isDirectory()){
            file.mkdirs();
        }
        String outputPath = Long.toString(System.currentTimeMillis());
        File file1 = new File(Environment.getExternalStorageDirectory() + "/TuZhi/" + "/Output",  outputPath + ".dtdb");
        try {
            FileOutputStream of = new FileOutputStream(file1);
            of.write(sb.toString().getBytes());
            of.close();
        }catch (IOException e){
            Log.w(TAG, e.toString());
        }
        return file1;
    }

    public static StringBuffer makeTxtHead(StringBuffer sb){
        sb = sb.append("ic").append(";");
        sb = sb.append("name").append(";");
        sb = sb.append("poic").append(";");
        sb = sb.append("photo").append(";");
        sb = sb.append("tape").append(";");
        sb = sb.append("video").append(";");
        sb = sb.append("description").append(";");
        sb = sb.append("time").append(";");
        sb = sb.append("type").append(";");
        sb = sb.append("x").append(";");
        sb = sb.append("y").append("\n");
        return sb;
    }

    public static StringBuffer makeTxtHead1(StringBuffer sb){
        sb = sb.append("XH").append(";");
        sb = sb.append("DY").append(";");
        sb = sb.append("MC").append(";");
        sb = sb.append("BZMC").append(";");
        sb = sb.append("XZQMC").append(";");
        sb = sb.append("XZQDM").append(";");
        sb = sb.append("SZDW").append(";");
        sb = sb.append("SCCJ").append(";");
        sb = sb.append("GG").append(";");
        sb = sb.append("IMGPATH").append(";");
        sb = sb.append("x").append(";");
        sb = sb.append("y").append("\n");
        return sb;
    }

    public static StringBuffer makeTxtHeadDMP(StringBuffer sb){
        sb = sb.append("xh").append(";");
        sb = sb.append("qydm").append(";");
        sb = sb.append("lbdm").append(";");
        sb = sb.append("bzmc").append(";");
        sb = sb.append("cym").append(";");
        sb = sb.append("jc").append(";");
        sb = sb.append("bm").append(";");
        sb = sb.append("dfyz").append(";");
        sb = sb.append("zt").append(";");
        sb = sb.append("dmll").append(";");
        sb = sb.append("dmhy").append(";");
        sb = sb.append("lsyg").append(";");
        sb = sb.append("dlstms").append(";");
        sb = sb.append("zlly").append(";");
        sb = sb.append("lat").append(";");
        sb = sb.append("lng").append(";");
        sb = sb.append("tapepath").append(";");
        sb = sb.append("imgpath").append("\n");
        return sb;
    }

    public static void makeKML(String save_folder_name, String SubFolder){
        List<File> files = new ArrayList<File>();
        //POI
        List<POI> pois = LitePal.findAll(POI.class);
        if (pois.size() > 0) {
            files.add(makePOIKML(pois, save_folder_name, SubFolder));
        }
        //Trail
        //List<Trail> trails = LitePal.findAll(Trail.class);
        //if (trails.size() > 0) files.add(makeTrailKML(trails));
        //Lines_WhiteBlank
        //List<Lines_WhiteBlank> whiteBlanks = LitePal.findAll(Lines_WhiteBlank.class);
        //if (whiteBlanks.size() > 0) files.add(makeWhiteBlankKML(whiteBlanks));
    }

    public static File makePOIKML(final List<POI> pois, String save_folder_name, String SubFolder){
        StringBuffer sb = new StringBuffer();
        int size_POI = pois.size();
        makeKMLHead(sb, "POI");
        for (int i = 0; i < size_POI; ++i){
            sb.append("    ").append("<Placemark id=\"ID_").append(plusID(i)).append("\">").append("\n");
            sb.append("      ").append("<name>").append(pois.get(i).getPoic()).append("</name>").append("\n");
            sb.append("      ").append("<Snippet></Snippet>").append("\n");
            //属性表内容
            sb = makeCDATAHead(sb);
            sb.append("<td>").append(pois.get(i).getPoic()).append("</td>").append("\n");
            sb.append("\n");
            sb.append("</tr>").append("\n");
            sb.append("\n");
            sb.append("<tr>").append("\n");
            sb.append("\n");
            sb.append("<td>").append("\n");
            sb.append("\n");
            sb.append("<table style=\"font-family:Arial,Verdana,Times;font-size:12px;text-align:left;width:100%;border-spacing:0px; padding:3px 3px 3px 3px\">").append("\n");
            sb.append("\n");
            //
            sb.append("<tr>").append("\n");
            sb.append("\n");
            sb.append("<td>").append("id").append("</td>").append("\n");
            sb.append("\n");
            sb.append("<td>").append(pois.get(i).getId()).append("</td>").append("\n");
            sb.append("\n");
            sb.append("</tr>").append("\n");
            sb.append("\n");
            //
            //
            sb.append("<tr bgcolor=\"#D4E4F3\">").append("\n");
            sb.append("\n");
            sb.append("<td>").append("name").append("</td>").append("\n");
            sb.append("\n");
            sb.append("<td>").append(pois.get(i).getName()).append("</td>").append("\n");
            sb.append("\n");
            sb.append("</tr>").append("\n");
            sb.append("\n");
            //
            //
            sb.append("<tr bgcolor=\"#D4E4F3\">").append("\n");
            sb.append("\n");
            sb.append("<td>").append("ic").append("</td>").append("\n");
            sb.append("\n");
            sb.append("<td>").append(pois.get(i).getIc()).append("</td>").append("\n");
            sb.append("\n");
            sb.append("</tr>").append("\n");
            sb.append("\n");
            //
            //
            sb.append("<tr bgcolor=\"#D4E4F3\">").append("\n");
            sb.append("\n");
            sb.append("<td>").append("type").append("</td>").append("\n");
            sb.append("\n");
            sb.append("<td>").append(pois.get(i).getType()).append("</td>").append("\n");
            sb.append("\n");
            sb.append("</tr>").append("\n");
            sb.append("\n");
            //
            //
            sb.append("<tr bgcolor=\"#D4E4F3\">").append("\n");
            sb.append("\n");
            sb.append("<td>").append("POIC").append("</td>").append("\n");
            sb.append("\n");
            sb.append("<td>").append(pois.get(i).getPoic()).append("</td>").append("\n");
            sb.append("\n");
            sb.append("</tr>").append("\n");
            sb.append("\n");
            //
            //
            sb.append("<tr bgcolor=\"#D4E4F3\">").append("\n");
            sb.append("\n");
            sb.append("<td>").append("photoStr").append("</td>").append("\n");
            sb.append("\n");
            List<MPHOTO> mphotos = LitePal.where("poic = ?", pois.get(i).getPoic()).find(MPHOTO.class);
            String photoStr = "";
            for (int j = 0; j < mphotos.size(); ++j){
                if (j == 0){
                    photoStr = mphotos.get(j).getPath().substring(mphotos.get(j).getPath().lastIndexOf("/"), mphotos.get(j).getPath().length());
                }else photoStr = photoStr + "|" + mphotos.get(j).getPath().substring(mphotos.get(j).getPath().lastIndexOf("/") + 1, mphotos.get(j).getPath().length());
            }
            sb.append("<td>").append(photoStr).append("</td>").append("\n");
            sb.append("\n");
            sb.append("</tr>").append("\n");
            sb.append("\n");
            //
            //
            sb.append("<tr bgcolor=\"#D4E4F3\">").append("\n");
            sb.append("\n");
            sb.append("<td>").append("description").append("</td>").append("\n");
            sb.append("\n");
            sb.append("<td>").append(pois.get(i).getDescription()).append("</td>").append("\n");
            sb.append("\n");
            sb.append("</tr>").append("\n");
            sb.append("\n");
            //
            //
            sb.append("<tr bgcolor=\"#D4E4F3\">").append("\n");
            sb.append("\n");
            sb.append("<td>").append("tapeStr").append("</td>").append("\n");
            sb.append("\n");
            List<MTAPE> mtapes = LitePal.where("poic = ?", pois.get(i).getPoic()).find(MTAPE.class);
            String tapeStr = "";
            for (int j = 0; j < mtapes.size(); ++j){
                if (j == 0){
                    tapeStr = mtapes.get(j).getPath().substring(mtapes.get(j).getPath().lastIndexOf("/"), mtapes.get(j).getPath().length());
                }else tapeStr = tapeStr + "|" + mtapes.get(j).getPath().substring(mtapes.get(j).getPath().lastIndexOf("/") + 1, mtapes.get(j).getPath().length());
            }
            sb.append("<td>").append(tapeStr).append("</td>").append("\n");
            sb.append("\n");
            sb.append("</tr>").append("\n");
            sb.append("\n");
            //
            //
            sb.append("<tr bgcolor=\"#D4E4F3\">").append("\n");
            sb.append("\n");
            sb.append("<td>").append("VideoStr").append("</td>").append("\n");
            sb.append("\n");
            List<MVEDIO> mvedios = LitePal.where("poic = ?", pois.get(i).getPoic()).find(MVEDIO.class);
            String VideoStr = "";
            for (int j = 0; j < mvedios.size(); ++j){
                if (j == 0){
                    VideoStr = mvedios.get(j).getPath().substring(mvedios.get(j).getPath().lastIndexOf("/"), mvedios.get(j).getPath().length());
                }else VideoStr = VideoStr + "|" + mvedios.get(j).getPath().substring(mvedios.get(j).getPath().lastIndexOf("/") + 1, mvedios.get(j).getPath().length());
            }
            sb.append("<td>").append(VideoStr).append("</td>").append("\n");
            sb.append("\n");
            sb.append("</tr>").append("\n");
            sb.append("\n");
            //
            //
            sb.append("<tr bgcolor=\"#D4E4F3\">").append("\n");
            sb.append("\n");
            sb.append("<td>").append("time").append("</td>").append("\n");
            sb.append("\n");
            sb.append("<td>").append(pois.get(i).getTime()).append("</td>").append("\n");
            sb.append("\n");
            sb.append("</tr>").append("\n");
            sb.append("\n");
            //
            //
            sb.append("<tr bgcolor=\"#D4E4F3\">").append("\n");
            sb.append("\n");
            sb.append("<td>").append("x").append("</td>").append("\n");
            sb.append("\n");
            sb.append("<td>").append(pois.get(i).getX()).append("</td>").append("\n");
            sb.append("\n");
            sb.append("</tr>").append("\n");
            sb.append("\n");
            //
            //
            sb.append("<tr bgcolor=\"#D4E4F3\">").append("\n");
            sb.append("\n");
            sb.append("<td>").append("y").append("</td>").append("\n");
            sb.append("\n");
            sb.append("<td>").append(pois.get(i).getY()).append("</td>").append("\n");
            sb.append("\n");
            sb.append("</tr>").append("\n");
            sb.append("\n");
            //
            sb = makeCDATATail(sb);
            sb.append("      ").append("<styleUrl>#IconStyle00</styleUrl>").append("\n");
            sb.append("      ").append("<Point>").append("\n");
            sb.append("        ").append("<altitudeMode>clampToGround</altitudeMode>").append("\n");
            Point cgcs2000Point = (Point)GeometryEngine.project(new Point(pois.get(i).getY(), pois.get(i).getX(), SpatialReferences.getWgs84()), SpatialReference.create(4490));
            sb.append("        ").append("<coordinates>").append(" ").append(cgcs2000Point.getX()).append(",").append(cgcs2000Point.getY()).append(",").append(0).append("</coordinates>").append("\n");
            sb.append("      ").append("</Point>").append("\n");
            sb.append("    ").append("</Placemark>").append("\n");
            //
        }
        sb = makeKMLTail(sb);
        File file = new File(Environment.getExternalStorageDirectory() + "/" + save_folder_name + "/Output" + "/" + SubFolder);
        if (!file.exists() && !file.isDirectory()){
            file.mkdirs();
        }
        String outputPath = Long.toString(System.currentTimeMillis());
        File file1 = new File(Environment.getExternalStorageDirectory() + "/" + save_folder_name + "/Output" + "/" + SubFolder,  outputPath + ".kml");
        try {
            FileOutputStream of = new FileOutputStream(file1);
            of.write(sb.toString().getBytes());
            of.close();
            MediaScannerConnection.scanFile(MainActivity.instance, new String[]{(file1).getAbsolutePath()},null,null);
        }catch (IOException e){
            Log.w(TAG, e.toString());
        }
        return file1;
    }

    public static void makeTrailKML(String save_folder_name, String SubFolder){
        final List<Trail> trails = LitePal.findAll(Trail.class);
        int size_trail = trails.size();
        List<PointCollection> plist = new ArrayList<>();

        for (int i = 0; i < size_trail; ++i) {
            PointCollection points = new PointCollection(SpatialReference.create(4490));
            //geometry_WhiteBlank geometryWhiteBlank = new geometry_WhiteBlank(whiteblanks.get(i).getLineSymbol(), whiteblanks.get(i).getPolyline());
            Log.w(TAG, "makeTrailKML: " + trails.get(i).getPath());
            String[] strings = trails.get(i).getPath().split(" ");
            Log.w(TAG, "drawWhiteBlank1: " + strings.length);
            for (int kk = 0; kk < strings.length; kk=kk+2) {
                com.esri.arcgisruntime.geometry.Point wgs84Point = (com.esri.arcgisruntime.geometry.Point) GeometryEngine.project(new Point(Double.valueOf(strings[kk+1]), Double.valueOf(strings[kk]), SpatialReferences.getWgs84()), SpatialReference.create(4490));

                points.add(wgs84Point.getX(), wgs84Point.getY());
            }
            Polyline polyline = (Polyline)GeometryEngine.project(new Polyline(points), SpatialReference.create(4490));
            PartCollection parts = new PartCollection(polyline.getParts());
            PointCollection pointCollection = new PointCollection(parts.getPartsAsPoints());
            plist.add(pointCollection);
        }

        //PointCollection polyline = GeometryEngine.project(points, SpatialReference.create(4490));
        StringBuffer sb = new StringBuffer();
        makeKMLHead(sb, "WhiteBlank");
        for (int i = 0; i < size_trail; ++i){
            sb.append("    ").append("<Placemark id=\"ID_").append(plusID(i)).append("\">").append("\n");
            sb.append("      ").append("<name>").append(i).append("</name>").append("\n");
            sb.append("      ").append("<Snippet></Snippet>").append("\n");
            //属性表内容
            sb = makeCDATAHead(sb);
            sb = makeCDATATail(sb);
            sb.append("      ").append("<styleUrl>#LineStyle00</styleUrl>").append("\n");
            sb.append("      ").append("<MultiGeometry>").append("\n");
            sb.append("        ").append("<LineString>").append("\n");
            sb.append("          ").append("<extrude>0</extrude>").append("\n");
            sb.append("          ").append("<tessellate>1</tessellate><altitudeMode>clampToGround</altitudeMode>").append("\n");
            String[] lines_str = trails.get(i).getPath().split("lzy");
            Log.w(TAG, "onClick: 2020/9/7: " + trails.get(i).getPath());
            StringBuffer str = new StringBuffer();
            /*for (int k = 0; k < lines_str.length; ++k) {
                str.append(" ").append(lines_str[k]).append(",").append("0");
            }*/
            for (int j = 0; j < plist.get(i).size(); j++) {
                //Point pt = (Point)GeometryEngine.project(new Point(plist.get(i).get(j).getX(), plist.get(i).get(j).getY(), SpatialReference.create(4490)), SpatialReference.create(4490));
                str.append(" ").append(plist.get(i).get(j).getX()).append(",").append(plist.get(i).get(j).getY()).append(",").append("0");
            }
            sb.append("          ").append("<coordinates>").append(str).append("</coordinates>").append("\n");
            sb.append("        ").append("</LineString>").append("\n");
            sb.append("      ").append("</MultiGeometry>").append("\n");
            sb.append("    ").append("</Placemark>").append("\n");
            //
        }
        sb = makeKMLTailForLine(sb);
        File file = new File(Environment.getExternalStorageDirectory() + "/" + save_folder_name + "/Output/" + SubFolder);
        if (!file.exists() && !file.isDirectory()){
            file.mkdirs();
        }
        String outputPath = Long.toString(System.currentTimeMillis());
        File file1 = new File(Environment.getExternalStorageDirectory() + "/" + save_folder_name + "/Output/" + SubFolder,  "轨迹" + outputPath + ".kml");
        try {
            FileOutputStream of = new FileOutputStream(file1);
            of.write(sb.toString().getBytes());
            of.close();
            MediaScannerConnection.scanFile(MainActivity.instance, new String[]{(file1).getAbsolutePath()},null,null);
        }catch (IOException e){
            Log.w(TAG, e.toString());
        }
    }

    public static void makeWhiteBlankKML(String save_folder_name, String SubFolder){
        final List<whiteblank> whiteBlanks = LitePal.findAll(whiteblank.class);
        int size_whiteBlanks = whiteBlanks.size();
        List<PointCollection> plist = new ArrayList<>();

        for (int i = 0; i < size_whiteBlanks; ++i) {
            PointCollection points = new PointCollection(SpatialReference.create(4523));
            //geometry_WhiteBlank geometryWhiteBlank = new geometry_WhiteBlank(whiteblanks.get(i).getLineSymbol(), whiteblanks.get(i).getPolyline());
            String[] strings = whiteBlanks.get(i).getPts().split("lzy");
            Log.w(TAG, "drawWhiteBlank1: " + strings.length);
            for (int kk = 0; kk < strings.length; ++kk) {
                String[] strings1 = strings[kk].split(",");
                if (strings1.length == 2) {
                    Log.w(TAG, "drawWhiteBlank2: " + strings1[0] + "; " + strings1[1]);
                    com.esri.arcgisruntime.geometry.Point wgs84Point = (com.esri.arcgisruntime.geometry.Point) GeometryEngine.project(new Point(Double.valueOf(strings1[0]), Double.valueOf(strings1[1])), SpatialReference.create(4523));
                    points.add(wgs84Point);
                }
            }
            Polyline polyline = (Polyline)GeometryEngine.project(new Polyline(points), SpatialReference.create(4523));
            PartCollection parts = new PartCollection(polyline.getParts());
            PointCollection pointCollection = new PointCollection(parts.getPartsAsPoints());
            plist.add(pointCollection);
        }
        //PointCollection polyline = GeometryEngine.project(points, SpatialReference.create(4490));
        StringBuffer sb = new StringBuffer();
        makeKMLHead(sb, "WhiteBlank");
        for (int i = 0; i < size_whiteBlanks; ++i){
            sb.append("    ").append("<Placemark id=\"ID_").append(plusID(i)).append("\">").append("\n");
            sb.append("      ").append("<name>").append(i).append("</name>").append("\n");
            sb.append("      ").append("<Snippet></Snippet>").append("\n");
            //属性表内容
            sb = makeCDATAHead(sb);
            sb = makeCDATATail(sb);
            sb.append("      ").append("<styleUrl>#LineStyle00</styleUrl>").append("\n");
            sb.append("      ").append("<MultiGeometry>").append("\n");
            sb.append("        ").append("<LineString>").append("\n");
            sb.append("          ").append("<extrude>0</extrude>").append("\n");
            sb.append("          ").append("<tessellate>1</tessellate><altitudeMode>clampToGround</altitudeMode>").append("\n");
            String[] lines_str = whiteBlanks.get(i).getPts().split("lzy");
            Log.w(TAG, "onClick: 2020/9/7: " + whiteBlanks.get(i).getPts());
            StringBuffer str = new StringBuffer();
            /*for (int k = 0; k < lines_str.length; ++k) {
                str.append(" ").append(lines_str[k]).append(",").append("0");
            }*/
            for (int j = 0; j < plist.get(i).size(); j++) {
                Point pt = (Point)GeometryEngine.project(new Point(plist.get(i).get(j).getX(), plist.get(i).get(j).getY(), SpatialReference.create(4523)), SpatialReference.create(4490));
                Log.w(TAG, "drawWhiteBlank2: " + plist.get(i).get(j).getX() + ", " + plist.get(i).get(j).getY());
                str.append(" ").append(pt.getX()).append(",").append(pt.getY()).append(",").append("0");
            }
            sb.append("          ").append("<coordinates>").append(str).append("</coordinates>").append("\n");
            sb.append("        ").append("</LineString>").append("\n");
            sb.append("      ").append("</MultiGeometry>").append("\n");
            sb.append("    ").append("</Placemark>").append("\n");
            //
        }
        sb = makeKMLTailForLine(sb);
        File file = new File(Environment.getExternalStorageDirectory() + "/" + save_folder_name + "/Output/" + SubFolder);
        if (!file.exists() && !file.isDirectory()){
            file.mkdirs();
        }
        String outputPath = Long.toString(System.currentTimeMillis());
        File file1 = new File(Environment.getExternalStorageDirectory() + "/" + save_folder_name + "/Output/" + SubFolder,  "白板" + outputPath + ".kml");
        try {
            FileOutputStream of = new FileOutputStream(file1);
            of.write(sb.toString().getBytes());
            of.close();
            MediaScannerConnection.scanFile(MainActivity.instance, new String[]{(file1).getAbsolutePath()},null,null);
        }catch (IOException e){
            Log.w(TAG, e.toString());
        }
    }

    public static void makeTxt(String type, String ic, String save_folder_name, String SubFolder){
        try {
            //TODO 导出poi点信息
            final List<POI> pois = LitePal.where("type = ? and ic = ?", type, ic).find(POI.class);
            Log.w(TAG, "makeTxt: " + pois.size());
            StringBuffer sb = new StringBuffer();
            int size_POI = pois.size();
            sb = makeTxtHead(sb);
            for (int i = 0; i < size_POI; ++i) {
                //属性表内容
                sb.append(pois.get(i).getIc()).append(";").append(pois.get(i).getName()).append(";").append(pois.get(i).getPoic()).append(";");
                List<MPHOTO> mphotos = LitePal.where("poic = ?", pois.get(i).getPoic()).find(MPHOTO.class);
                String photoStr = "";
                for (int j = 0; j < mphotos.size(); ++j) {
                    if (j == 0) {
                        photoStr = mphotos.get(j).getPath().substring(mphotos.get(j).getPath().lastIndexOf("/") + 1, mphotos.get(j).getPath().length());
                    } else
                        photoStr = photoStr + "|" + mphotos.get(j).getPath().substring(mphotos.get(j).getPath().lastIndexOf("/") + 1, mphotos.get(j).getPath().length());
                }
                photoStr = URLDecoder.decode(photoStr, "utf-8");
                sb.append(photoStr).append(";");
                List<MTAPE> mtapes = LitePal.where("poic = ?", pois.get(i).getPoic()).find(MTAPE.class);
                String tapeStr = "";
                for (int j = 0; j < mtapes.size(); ++j) {
                    if (j == 0) {
                        tapeStr = mtapes.get(j).getPath().substring(mtapes.get(j).getPath().lastIndexOf("/") + 1, mtapes.get(j).getPath().length());
                    } else
                        tapeStr = tapeStr + "|" + mtapes.get(j).getPath().substring(mtapes.get(j).getPath().lastIndexOf("/") + 1, mtapes.get(j).getPath().length());
                }
                tapeStr = URLDecoder.decode(tapeStr, "utf-8");
                List<MVEDIO> mvedios = LitePal.where("poic = ?", pois.get(i).getPoic()).find(MVEDIO.class);
                String VideoStr = "";
                for (int j = 0; j < mvedios.size(); ++j) {
                    if (j == 0) {
                        VideoStr = mvedios.get(j).getPath().substring(mvedios.get(j).getPath().lastIndexOf("/") + 1, mvedios.get(j).getPath().length());
                    } else
                        VideoStr = VideoStr + "|" + mvedios.get(j).getPath().substring(mvedios.get(j).getPath().lastIndexOf("/") + 1, mvedios.get(j).getPath().length());
                }
                VideoStr = URLDecoder.decode(VideoStr, "utf-8");
                sb.append(tapeStr).append(";").append(VideoStr).append(";").append(pois.get(i).getDescription()).append(";").append(pois.get(i).getTime()).append(";").append(pois.get(i).getType()).append(";").append(pois.get(i).getY()).append(";").append(pois.get(i).getX()).append("\n");
            }
            makeFile(sb, type, save_folder_name, SubFolder);
        }catch (UnsupportedEncodingException e){
            Log.w(TAG, e.toString());
        }
    }

    public static void makeTxt(String type, String save_folder_name, String SubFolder){
        try {
            final List<POI> pois = LitePal.where("type = ?", type).find(POI.class);
            Log.w(TAG, "makeTxt: " + pois.size());
            StringBuffer sb = new StringBuffer();
            int size_POI = pois.size();
            sb = makeTxtHead(sb);
            for (int i = 0; i < size_POI; ++i) {
                //属性表内容
                sb.append(pois.get(i).getIc()).append(";").append(pois.get(i).getName()).append(";").append(pois.get(i).getPoic()).append(";");
                List<MPHOTO> mphotos = LitePal.where("poic = ?", pois.get(i).getPoic()).find(MPHOTO.class);
                String photoStr = "";
                for (int j = 0; j < mphotos.size(); ++j) {
                    if (j == 0) {
                        photoStr = mphotos.get(j).getPath().substring(mphotos.get(j).getPath().lastIndexOf("/") + 1, mphotos.get(j).getPath().length());
                    } else
                        photoStr = photoStr + "|" + mphotos.get(j).getPath().substring(mphotos.get(j).getPath().lastIndexOf("/") + 1, mphotos.get(j).getPath().length());
                }
                photoStr = URLDecoder.decode(photoStr, "utf-8");
                sb.append(photoStr).append(";");
                List<MTAPE> mtapes = LitePal.where("poic = ?", pois.get(i).getPoic()).find(MTAPE.class);
                String tapeStr = "";
                for (int j = 0; j < mtapes.size(); ++j) {
                    if (j == 0) {
                        tapeStr = mtapes.get(j).getPath().substring(mtapes.get(j).getPath().lastIndexOf("/") + 1, mtapes.get(j).getPath().length());
                    } else
                        tapeStr = tapeStr + "|" + mtapes.get(j).getPath().substring(mtapes.get(j).getPath().lastIndexOf("/") + 1, mtapes.get(j).getPath().length());
                }
                tapeStr = URLDecoder.decode(tapeStr, "utf-8");
                List<MVEDIO> mvedios = LitePal.where("poic = ?", pois.get(i).getPoic()).find(MVEDIO.class);
                String VideoStr = "";
                for (int j = 0; j < mvedios.size(); ++j) {
                    if (j == 0) {
                        VideoStr = mvedios.get(j).getPath().substring(mvedios.get(j).getPath().lastIndexOf("/") + 1, mvedios.get(j).getPath().length());
                    } else
                        VideoStr = VideoStr + "|" + mvedios.get(j).getPath().substring(mvedios.get(j).getPath().lastIndexOf("/") + 1, mvedios.get(j).getPath().length());
                }
                VideoStr = URLDecoder.decode(VideoStr, "utf-8");
                Point cgcs2000Point = (Point)GeometryEngine.project(new Point(pois.get(i).getY(), pois.get(i).getX(), SpatialReferences.getWgs84()), SpatialReference.create(4490));
                sb.append(tapeStr).append(";").append(VideoStr).append(";").append(pois.get(i).getDescription()).append(";").append(pois.get(i).getTime()).append(";").append(pois.get(i).getType()).append(";").append(cgcs2000Point.getX()).append(";").append(cgcs2000Point.getY()).append("\n");
            }
            makeFile(sb, type, save_folder_name, SubFolder);
        }catch (UnsupportedEncodingException e){
            Log.w(TAG, e.toString());
        }
    }

    public static void makeTxt1(String save_folder_name, String SubFolder){
        try {
            final List<DMBZ> pois = LitePal.findAll(DMBZ.class);
            Log.w(TAG, "makeTxt: " + pois.size());
            StringBuffer sb = new StringBuffer();
            int size_POI = pois.size();
            sb = makeTxtHeadDMP(sb);
            for (int i = 0; i < size_POI; ++i) {
                //属性表内容
                sb.append(pois.get(i).getXH()).append(";").append(pois.get(i).getDY()).append(";").append(pois.get(i).getMC()).append(";").append(pois.get(i).getBZMC()).append(";").append(pois.get(i).getXZQMC()).append(";").append(pois.get(i).getXZQDM()).append(";").append(pois.get(i).getSZDW()).append(";").append(pois.get(i).getSCCJ()).append(";").append(pois.get(i).getGG()).append(";").append(pois.get(i).getIMGPATH()).append(";").append(pois.get(i).getLng()).append(";");
                sb.append(pois.get(i).getLat()).append("\n");
            }
            makeFileDMP(sb, save_folder_name, SubFolder);
        }catch (Exception e){
            Log.w(TAG, e.toString());
        }
    }

    public static void makeFileDMP(StringBuffer sb, String save_folder_name, String SubFolder){
        File file = new File(Environment.getExternalStorageDirectory() + "/" + save_folder_name + "/Output" + "/" + SubFolder);
        if (!file.exists() && !file.isDirectory()) {
            file.mkdirs();
        }
        String outputPath = Long.toString(System.currentTimeMillis());
        File file1 = new File(Environment.getExternalStorageDirectory() + "/" + save_folder_name + "/Output" + "/" + SubFolder, "DMP" + outputPath + ".txt");
        try {
            FileOutputStream of = new FileOutputStream(file1);
            of.write(sb.toString().getBytes());
            of.close();
        } catch (IOException e) {
            Log.w(TAG, e.toString());
        }
    }

    public static void makeTxtDMP(String save_folder_name, String SubFolder){
        try {
            final List<DMPoint> pois = LitePal.findAll(DMPoint.class);
            Log.w(TAG, "makeTxt: " + pois.size());
            StringBuffer sb = new StringBuffer();
            int size_POI = pois.size();
            sb = makeTxtHeadDMP(sb);
            for (int i = 0; i < size_POI; ++i) {
                //属性表内容
                sb.append(pois.get(i).getXh()).append(";").append(pois.get(i).getQydm()).append(";").append(pois.get(i).getLbdm()).append(";").append(pois.get(i).getBzmc()).append(";").append(pois.get(i).getCym()).append(";").append(pois.get(i).getJc()).append(";").append(pois.get(i).getBm()).append(";").append(pois.get(i).getDfyz()).append(";").append(pois.get(i).getZt()).append(";").append(pois.get(i).getDmll()).append(";").append(pois.get(i).getDmhy()).append(";").append(pois.get(i).getLsyg()).append(";").append(pois.get(i).getDlstms()).append(";").append(pois.get(i).getZlly()).append(";").append(pois.get(i).getLat()).append(";").append(pois.get(i).getLng()).append(";").append(pois.get(i).getTapepath());
                sb.append(pois.get(i).getImgpath()).append("\n");
            }
            makeFileDMP(sb, save_folder_name, SubFolder);
        }catch (Exception e){
            Log.w(TAG, e.toString());
        }
    }

    public static void makeFile(StringBuffer sb, String type, String save_folder_name, String SubFolder){
        File file = new File(Environment.getExternalStorageDirectory() + "/" + save_folder_name + "/Output" + "/" + SubFolder);
        if (!file.exists() && !file.isDirectory()) {
            file.mkdirs();
        }
        String outputPath = Long.toString(System.currentTimeMillis());
        File file1 = new File(Environment.getExternalStorageDirectory() + "/" + save_folder_name + "/Output" + "/" + SubFolder, "DMBZ" + type + outputPath + ".txt");
        try {
            FileOutputStream of = new FileOutputStream(file1);
            of.write(sb.toString().getBytes());
            of.close();
            MediaScannerConnection.scanFile(MainActivity.instance, new String[]{(file).getAbsolutePath()},null,null);
            MediaScannerConnection.scanFile(MainActivity.instance, new String[]{(file1).getAbsolutePath()},null,null);
        } catch (IOException e) {
            Log.w(TAG, e.toString());
        }
    }

    public static void makeTxt(String type){
        try {
            final List<POI> pois = LitePal.where("type = ?", type).find(POI.class);
            Log.w(TAG, "makeTxt: " + pois.size());
            StringBuffer sb = new StringBuffer();
            int size_POI = pois.size();
            sb = makeTxtHead(sb);
            for (int i = 0; i < size_POI; ++i) {
                //属性表内容
                sb.append(pois.get(i).getIc()).append(";").append(pois.get(i).getName()).append(";").append(pois.get(i).getPoic()).append(";");
                List<MPHOTO> mphotos = LitePal.where("poic = ?", pois.get(i).getPoic()).find(MPHOTO.class);
                String photoStr = "";
                for (int j = 0; j < mphotos.size(); ++j) {
                    if (j == 0) {
                        photoStr = mphotos.get(j).getPath().substring(mphotos.get(j).getPath().lastIndexOf("/") + 1, mphotos.get(j).getPath().length());
                    } else
                        photoStr = photoStr + "|" + mphotos.get(j).getPath().substring(mphotos.get(j).getPath().lastIndexOf("/") + 1, mphotos.get(j).getPath().length());
                }
                photoStr = URLDecoder.decode(photoStr, "utf-8");
                sb.append(photoStr).append(";");
                List<MTAPE> mtapes = LitePal.where("poic = ?", pois.get(i).getPoic()).find(MTAPE.class);
                String tapeStr = "";
                for (int j = 0; j < mtapes.size(); ++j) {
                    if (j == 0) {
                        tapeStr = mtapes.get(j).getPath().substring(mtapes.get(j).getPath().lastIndexOf("/") + 1, mtapes.get(j).getPath().length());
                    } else
                        tapeStr = tapeStr + "|" + mtapes.get(j).getPath().substring(mtapes.get(j).getPath().lastIndexOf("/") + 1, mtapes.get(j).getPath().length());
                }
                tapeStr = URLDecoder.decode(tapeStr, "utf-8");
                sb.append(tapeStr).append(";").append(pois.get(i).getDescription()).append(";").append(pois.get(i).getTime()).append(";").append(pois.get(i).getType()).append(";").append(pois.get(i).getY()).append(";").append(pois.get(i).getX()).append("\n");
            }
            makeFile(sb, type);
        }catch (UnsupportedEncodingException e){
            Log.w(TAG, e.toString());
        }
    }

    public static void makeTxt1(){
        try {
            final List<DMBZ> pois = LitePal.findAll(DMBZ.class);
            Log.w(TAG, "makeTxt: " + pois.size());
            StringBuffer sb = new StringBuffer();
            int size_POI = pois.size();
            sb = makeTxtHeadDMP(sb);
            for (int i = 0; i < size_POI; ++i) {
                //属性表内容
                sb.append(pois.get(i).getXH()).append(";").append(pois.get(i).getDY()).append(";").append(pois.get(i).getMC()).append(";").append(pois.get(i).getBZMC()).append(";").append(pois.get(i).getXZQMC()).append(";").append(pois.get(i).getXZQDM()).append(";").append(pois.get(i).getSZDW()).append(";").append(pois.get(i).getSCCJ()).append(";").append(pois.get(i).getGG()).append(";").append(pois.get(i).getIMGPATH()).append(";").append(pois.get(i).getLng()).append(";");
                sb.append(pois.get(i).getLat()).append("\n");
            }
            makeFileDMP(sb);
        }catch (Exception e){
            Log.w(TAG, e.toString());
        }
    }

    public static void makeTxtDMP(){
        try {
            final List<DMPoint> pois = LitePal.findAll(DMPoint.class);
            Log.w(TAG, "makeTxt: " + pois.size());
            StringBuffer sb = new StringBuffer();
            int size_POI = pois.size();
            sb = makeTxtHeadDMP(sb);
            for (int i = 0; i < size_POI; ++i) {
                //属性表内容
                sb.append(pois.get(i).getXh()).append(";").append(pois.get(i).getQydm()).append(";").append(pois.get(i).getLbdm()).append(";").append(pois.get(i).getBzmc()).append(";").append(pois.get(i).getCym()).append(";").append(pois.get(i).getJc()).append(";").append(pois.get(i).getBm()).append(";").append(pois.get(i).getDfyz()).append(";").append(pois.get(i).getZt()).append(";").append(pois.get(i).getDmll()).append(";").append(pois.get(i).getDmhy()).append(";").append(pois.get(i).getLsyg()).append(";").append(pois.get(i).getDlstms()).append(";").append(pois.get(i).getZlly()).append(";").append(pois.get(i).getLat()).append(";").append(pois.get(i).getLng()).append(";").append(pois.get(i).getTapepath());
                sb.append(pois.get(i).getImgpath()).append("\n");
            }
            makeFileDMP(sb);
        }catch (Exception e){
            Log.w(TAG, e.toString());
        }
    }

    public static void makeFile(StringBuffer sb, String type){
        File file = new File(Environment.getExternalStorageDirectory() + "/TuZhi/" + "/Output");
        if (!file.exists() && !file.isDirectory()) {
            file.mkdirs();
        }
        String outputPath = Long.toString(System.currentTimeMillis());
        File file1 = new File(Environment.getExternalStorageDirectory() + "/TuZhi/" + "/Output", "DMBZ" + type + outputPath + ".txt");
        try {
            FileOutputStream of = new FileOutputStream(file1);
            of.write(sb.toString().getBytes());
            of.close();
        } catch (IOException e) {
            Log.w(TAG, e.toString());
        }
    }

    public static void makeFile1(StringBuffer sb){
        File file = new File(Environment.getExternalStorageDirectory() + "/TuZhi/" + "/Output");
        if (!file.exists() && !file.isDirectory()) {
            file.mkdirs();
        }
        String outputPath = Long.toString(System.currentTimeMillis());
        File file1 = new File(Environment.getExternalStorageDirectory() + "/TuZhi/" + "/Output", "DMBZ" + outputPath + ".txt");
        try {
            FileOutputStream of = new FileOutputStream(file1);
            of.write(sb.toString().getBytes());
            of.close();
        } catch (IOException e) {
            Log.w(TAG, e.toString());
        }
    }

    public static void makeFileDMP(StringBuffer sb){
        File file = new File(Environment.getExternalStorageDirectory() + "/TuZhi/" + "/Output");
        if (!file.exists() && !file.isDirectory()) {
            file.mkdirs();
        }
        String outputPath = Long.toString(System.currentTimeMillis());
        File file1 = new File(Environment.getExternalStorageDirectory() + "/TuZhi/" + "/Output", "DMP" + outputPath + ".txt");
        try {
            FileOutputStream of = new FileOutputStream(file1);
            of.write(sb.toString().getBytes());
            of.close();
        } catch (IOException e) {
            Log.w(TAG, e.toString());
        }
    }

    public static String[] bubbleSort(String[] arr) {
        int len = arr.length;
        for (int i = 0; i < len - 1; ++i) {
            for (int j = 0; j < len - 1 - i; ++j) {
                if (arr[j].toUpperCase().charAt(0) > arr[j + 1].toUpperCase().charAt(0)) {        // 相邻元素两两对比
                    String temp = arr[j+1];        // 元素交换
                    arr[j+1] = arr[j];
                    arr[j] = temp;
                }
            }
        }
        return arr;
    }
}
