package com.esri.arcgisruntime.demos.displaymap;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Toast;

import org.litepal.LitePal;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class photoshow extends AppCompatActivity {

    private static final String TAG = "photoshow";
    private String POIC;
    private List<mPhotobj> mPhotobjList = new ArrayList<>();
    private mPhotobjAdapter adapter;
    private RecyclerView recyclerView;
    private GridLayoutManager layoutManager;
    private String deletePath;
    private final static int REQUEST_CODE_PHOTO = 42;
    private final static int TAKE_PHOTO = 41;
    private int isLongClick = 1;
    Toolbar toolbar;
    Uri imageUri;
    private List<bt> bts;
    boolean isCreateBitmap = false;
    private int POIType;
    private String DMXH;
    private String DML;
    private String DMP;

    private void refreshCardFromPOI(){
        mPhotobjList.clear();
        List<MPHOTO> mphotos = LitePal.where("poic = ?", POIC).find(MPHOTO.class);
        for (MPHOTO mphoto : mphotos){
            mPhotobj mphotobj = new mPhotobj(mphoto.getPoic(), mphoto.getPoic(), mphoto.getTime(), mphoto.getPath());
            mPhotobjList.add(mphotobj);
        }
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_photo);
        layoutManager = new GridLayoutManager(this,1);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new mPhotobjAdapter(mPhotobjList);
        adapter.setOnItemLongClickListener(new mPhotobjAdapter.OnRecyclerItemLongListener() {
            @Override
            public void onItemLongClick(View view, String path, String time) {
                setTitle(photoshow.this.getResources().getText(R.string.IsLongClicking));
                //deletePath = path;
                deletePath = time;
                isLongClick = 0;
                invalidateOptionsMenu();
            }
        });
        adapter.setOnItemClickListener(new mPhotobjAdapter.OnRecyclerItemClickListener() {
            @Override
            public void onItemClick(View view, String path, int position, String time) {
                mPhotobjAdapter.ViewHolder holder = new mPhotobjAdapter.ViewHolder(view);
                if (isLongClick == 0){
                    if (holder.cardView.getCardBackgroundColor().getDefaultColor() != Color.GRAY){
                        holder.cardView.setCardBackgroundColor(Color.GRAY);
                        //deletePath = deletePath + "wslzy" + path;
                        deletePath = deletePath + "wslzy" + time;
                    }else {
                        holder.cardView.setCardBackgroundColor(Color.WHITE);
                        if (deletePath.contains("wslzy")) {
                            //String replace = "wslzy" + path;
                            String replace = "wslzy" + time;
                            deletePath = deletePath.replace(replace, "");
                            if (deletePath.length() == deletePath.replace(replace, "").length()){
                                //String replace1 = path + "wslzy";
                                String replace1 = time + "wslzy";
                                deletePath = deletePath.replace(replace1, "");
                            }
                        }else {
                            resetView();
                        }
                    }
                }else {
                    Log.w(TAG, "onItemClick: " + path );
                    if (isCreateBitmap) showPopueWindowForPhoto(path);
                }
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void pickFile() {
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                EnumClass.READ_EXTERNAL_STORAGE);
        int permissionCheck1 = ContextCompat.checkSelfPermission(this,
                EnumClass.WRITE_EXTERNAL_STORAGE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED || permissionCheck1 != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            EnumClass.READ_EXTERNAL_STORAGE, EnumClass.WRITE_EXTERNAL_STORAGE},
                    2
            );

            return;
        }else takePhoto();
    }

    private void takePhoto(){
        File file2 = new File(Environment.getExternalStorageDirectory() + "/TuZhi/photo");
        if (!file2.exists() && !file2.isDirectory()){
            file2.mkdirs();
        }
        long timenow = System.currentTimeMillis();
        File outputImage = new File(Environment.getExternalStorageDirectory() + "/TuZhi/photo", Long.toString(timenow) + ".jpg");
        try {
            if (outputImage.exists()){
                outputImage.delete();
            }
            outputImage.createNewFile();
        }catch (IOException e){
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT >= 24){
            //locError(Environment.getExternalStorageDirectory() + "/maphoto/" + Long.toString(timenow) + ".jpg");
            imageUri = FileProvider.getUriForFile(photoshow.this, "com.android.displaymap.fileprovider", outputImage);
        }else imageUri = Uri.fromFile(outputImage);
        Log.w(TAG, "takePhoto: " + imageUri);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, TAKE_PHOTO);
    }

    private void requestCameraPermission(){
        if (Build.VERSION.SDK_INT >= 23) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(photoshow.this, Manifest.permission.CAMERA);
            if(checkCallPhonePermission != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(photoshow.this,new String[]{Manifest.permission.CAMERA},1);
                return;
            }else{
                pickFile();
            }
        } else {
            pickFile();
        }
    }

    private void refreshCardFromDMBZ(){
        mPhotobjList.clear();
        List<DMBZ> dmbzList = LitePal.where("xh = ?", DMXH).find(DMBZ.class);
        String ImgPathTemp = dmbzList.get(0).getIMGPATH();
        String[] imgPath = new String[DataUtil.appearNumber(ImgPathTemp, "\\|") + 1];
        Log.w(TAG, "run: " + ImgPathTemp);
        for (int k = 0; k < imgPath.length; k++){
            if (imgPath.length > 1) {
                if (k < imgPath.length - 1) {
                    imgPath[k] = ImgPathTemp.substring(0, ImgPathTemp.indexOf("|"));
                    ImgPathTemp = ImgPathTemp.substring(ImgPathTemp.indexOf("|") + 1);
                }else imgPath[k] = ImgPathTemp;
            }else imgPath[k] = ImgPathTemp;
        }
        for (int kk = 0; kk < imgPath.length; kk++) {
            String rootpath = Environment.getExternalStorageDirectory().toString() + "/地名标志照片/";
            String path;
            if (!imgPath[kk].contains(Environment.getExternalStorageDirectory().toString())) {
                path = rootpath + imgPath[kk];
            } else {
                path = imgPath[kk];
            }
            mPhotobj mphotobj = new mPhotobj(path.replace(Environment.getExternalStorageDirectory().toString() + "/地名标志照片/", ""), path.replace(Environment.getExternalStorageDirectory().toString() + "/地名标志照片/", ""), dmbzList.get(0).getTime(), path);
            mPhotobjList.add(mphotobj);
        }
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_photo);
        layoutManager = new GridLayoutManager(this,1);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new mPhotobjAdapter(mPhotobjList);
        adapter.setOnItemLongClickListener(new mPhotobjAdapter.OnRecyclerItemLongListener() {
            @Override
            public void onItemLongClick(View view, String path, String time) {
                setTitle(photoshow.this.getResources().getText(R.string.IsLongClicking));
                //deletePath = path;
                deletePath = path.replace(Environment.getExternalStorageDirectory().toString() + "/地名标志照片/", "");
                Log.w(TAG, "onItemLongClick: " + deletePath);
                isLongClick = 0;
                invalidateOptionsMenu();
            }
        });
        adapter.setOnItemClickListener(new mPhotobjAdapter.OnRecyclerItemClickListener() {
            @Override
            public void onItemClick(View view, String path, int position, String time) {
                mPhotobjAdapter.ViewHolder holder = new mPhotobjAdapter.ViewHolder(view);
                if (isLongClick == 0){
                    if (holder.cardView.getCardBackgroundColor().getDefaultColor() != Color.GRAY){
                        holder.cardView.setCardBackgroundColor(Color.GRAY);
                        //deletePath = deletePath + "wslzy" + path;
                        deletePath = deletePath + "wslzy" + path.replace(Environment.getExternalStorageDirectory().toString() + "/地名标志照片/", "");
                    }else {
                        holder.cardView.setCardBackgroundColor(Color.WHITE);
                        if (deletePath.contains("wslzy")) {
                            //String replace = "wslzy" + path;
                            String replace = "wslzy" + path.replace(Environment.getExternalStorageDirectory().toString() + "/地名标志照片/", "");
                            deletePath = deletePath.replace(replace, "");
                            if (deletePath.length() == deletePath.replace(replace, "").length()){
                                //String replace1 = path + "wslzy";
                                String replace1 = path + "wslzy";
                                deletePath = deletePath.replace(replace1, "");
                            }
                        }else {
                            resetView();
                        }
                    }
                    Log.w(TAG, "onItemLongClick: " + deletePath);
                }else {
                    //Log.w(TAG, "onItemClick: " + path );
                    if (isCreateBitmap) {
                        Log.w(TAG, "onItemClick: " + path );
                        showPopueWindowForPhoto(path);
                    }
                }
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void refreshCardFromDML(){
        mPhotobjList.clear();
        List<DMLine> dmLines = LitePal.where("mapid = ?", DML).find(DMLine.class);
        String ImgPathTemp = dmLines.get(0).getImgpath();
        if (ImgPathTemp != null) {
            String[] imgPath = new String[DataUtil.appearNumber(ImgPathTemp, "\\|") + 1];
            Log.w(TAG, "run: " + ImgPathTemp);
            for (int k = 0; k < imgPath.length; k++) {
                if (imgPath.length > 1) {
                    if (k < imgPath.length - 1) {
                        imgPath[k] = ImgPathTemp.substring(0, ImgPathTemp.indexOf("|"));
                        ImgPathTemp = ImgPathTemp.substring(ImgPathTemp.indexOf("|") + 1);
                    } else imgPath[k] = ImgPathTemp;
                } else imgPath[k] = ImgPathTemp;
            }
            for (int kk = 0; kk < imgPath.length; kk++) {
                String rootpath = Environment.getExternalStorageDirectory().toString() + "/盘龙区多媒体数据/照片/";
                String path;
                if (!imgPath[kk].contains(Environment.getExternalStorageDirectory().toString())) {
                    path = rootpath + imgPath[kk];
                } else {
                    path = imgPath[kk];
                }
                mPhotobj mphotobj = new mPhotobj(path.replace(Environment.getExternalStorageDirectory().toString() + "/盘龙区多媒体数据/照片/", ""), path.replace(Environment.getExternalStorageDirectory().toString() + "/盘龙区多媒体数据/照片/", ""), dmLines.get(0).getTime(), path);
                mPhotobjList.add(mphotobj);
            }
        }
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_photo);
        layoutManager = new GridLayoutManager(this,1);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new mPhotobjAdapter(mPhotobjList);
        adapter.setOnItemLongClickListener(new mPhotobjAdapter.OnRecyclerItemLongListener() {
            @Override
            public void onItemLongClick(View view, String path, String time) {
                setTitle(photoshow.this.getResources().getText(R.string.IsLongClicking));
                //deletePath = path;
                deletePath = path.replace(Environment.getExternalStorageDirectory().toString() + "/盘龙区多媒体数据/照片/", "");
                Log.w(TAG, "onItemLongClick: " + deletePath);
                isLongClick = 0;
                invalidateOptionsMenu();
            }
        });
        adapter.setOnItemClickListener(new mPhotobjAdapter.OnRecyclerItemClickListener() {
            @Override
            public void onItemClick(View view, String path, int position, String time) {
                mPhotobjAdapter.ViewHolder holder = new mPhotobjAdapter.ViewHolder(view);
                if (isLongClick == 0){
                    if (holder.cardView.getCardBackgroundColor().getDefaultColor() != Color.GRAY){
                        holder.cardView.setCardBackgroundColor(Color.GRAY);
                        //deletePath = deletePath + "wslzy" + path;
                        deletePath = deletePath + "wslzy" + path.replace(Environment.getExternalStorageDirectory().toString() + "/盘龙区多媒体数据/照片/", "");
                    }else {
                        holder.cardView.setCardBackgroundColor(Color.WHITE);
                        if (deletePath.contains("wslzy")) {
                            //String replace = "wslzy" + path;
                            String replace = "wslzy" + path.replace(Environment.getExternalStorageDirectory().toString() + "/盘龙区多媒体数据/照片/", "");
                            deletePath = deletePath.replace(replace, "");
                            if (deletePath.length() == deletePath.replace(replace, "").length()){
                                //String replace1 = path + "wslzy";
                                String replace1 = path + "wslzy";
                                deletePath = deletePath.replace(replace1, "");
                            }
                        }else {
                            resetView();
                        }
                    }
                    Log.w(TAG, "onItemLongClick: " + deletePath);
                }else {
                    //Log.w(TAG, "onItemClick: " + path );
                    if (isCreateBitmap) {
                        Log.w(TAG, "onItemClick: " + path );
                        showPopueWindowForPhoto(path);
                    }
                }
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void refreshCardFromDMP(){
        mPhotobjList.clear();
        List<DMPoint> dmPoints = LitePal.where("mapid = ?", DMP).find(DMPoint.class);
        String ImgPathTemp = dmPoints.get(0).getImgpath();
        String[] imgPath = new String[DataUtil.appearNumber(ImgPathTemp, "\\|") + 1];
        Log.w(TAG, "run: " + ImgPathTemp);
        for (int k = 0; k < imgPath.length; k++){
            if (imgPath.length > 1) {
                if (k < imgPath.length - 1) {
                    imgPath[k] = ImgPathTemp.substring(0, ImgPathTemp.indexOf("|"));
                    ImgPathTemp = ImgPathTemp.substring(ImgPathTemp.indexOf("|") + 1);
                }else imgPath[k] = ImgPathTemp;
            }else imgPath[k] = ImgPathTemp;
        }
        for (int kk = 0; kk < imgPath.length; kk++) {
            String rootpath = Environment.getExternalStorageDirectory().toString() + "/盘龙区多媒体数据/照片/";
            String path;
            if (!imgPath[kk].contains(Environment.getExternalStorageDirectory().toString())) {
                path = rootpath + imgPath[kk];
            } else {
                path = imgPath[kk];
            }
            mPhotobj mphotobj = new mPhotobj(path.replace(Environment.getExternalStorageDirectory().toString() + "/盘龙区多媒体数据/照片/", ""), path.replace(Environment.getExternalStorageDirectory().toString() + "/盘龙区多媒体数据/照片/", ""), dmPoints.get(0).getTime(), path);
            mPhotobjList.add(mphotobj);
        }
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_photo);
        layoutManager = new GridLayoutManager(this,1);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new mPhotobjAdapter(mPhotobjList);
        adapter.setOnItemLongClickListener(new mPhotobjAdapter.OnRecyclerItemLongListener() {
            @Override
            public void onItemLongClick(View view, String path, String time) {
                setTitle(photoshow.this.getResources().getText(R.string.IsLongClicking));
                //deletePath = path;
                deletePath = path.replace(Environment.getExternalStorageDirectory().toString() + "/盘龙区多媒体数据/照片/", "");
                Log.w(TAG, "onItemLongClick: " + deletePath);
                isLongClick = 0;
                invalidateOptionsMenu();
            }
        });
        adapter.setOnItemClickListener(new mPhotobjAdapter.OnRecyclerItemClickListener() {
            @Override
            public void onItemClick(View view, String path, int position, String time) {
                mPhotobjAdapter.ViewHolder holder = new mPhotobjAdapter.ViewHolder(view);
                if (isLongClick == 0){
                    if (holder.cardView.getCardBackgroundColor().getDefaultColor() != Color.GRAY){
                        holder.cardView.setCardBackgroundColor(Color.GRAY);
                        //deletePath = deletePath + "wslzy" + path;
                        deletePath = deletePath + "wslzy" + path.replace(Environment.getExternalStorageDirectory().toString() + "/盘龙区多媒体数据/照片/", "");
                    }else {
                        holder.cardView.setCardBackgroundColor(Color.WHITE);
                        if (deletePath.contains("wslzy")) {
                            //String replace = "wslzy" + path;
                            String replace = "wslzy" + path.replace(Environment.getExternalStorageDirectory().toString() + "/盘龙区多媒体数据/照片/", "");
                            deletePath = deletePath.replace(replace, "");
                            if (deletePath.length() == deletePath.replace(replace, "").length()){
                                //String replace1 = path + "wslzy";
                                String replace1 = path + "wslzy";
                                deletePath = deletePath.replace(replace1, "");
                            }
                        }else {
                            resetView();
                        }
                    }
                    Log.w(TAG, "onItemLongClick: " + deletePath);
                }else {
                    //Log.w(TAG, "onItemClick: " + path );
                    if (isCreateBitmap) {
                        Log.w(TAG, "onItemClick: " + path );
                        showPopueWindowForPhoto(path);
                    }
                }
            }
        });
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //toolbar = (Toolbar) findViewById(R.id.toolbar);
        switch (isLongClick){
            case 1:
                toolbar.setBackgroundColor(Color.rgb(63, 81, 181));
                menu.findItem(R.id.deletepoi).setVisible(false);
                menu.findItem(R.id.restore_pois).setVisible(false);
                break;
            case 0:
                toolbar.setBackgroundColor(Color.rgb(233, 150, 122));
                menu.findItem(R.id.back_pois).setVisible(false);
                menu.findItem(R.id.deletepoi).setVisible(true);
                menu.findItem(R.id.restore_pois).setVisible(true);
                menu.findItem(R.id.add_pois).setVisible(false);
                break;
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photoshow);
        bts = new ArrayList<>();
        /////////////////////////
        //声明ToolBar
        toolbar = (Toolbar) findViewById(R.id.toolbar4);
        setSupportActionBar(toolbar);
        setTitle(photoshow.this.getResources().getText(R.string.PhotoList));
        Intent intent = getIntent();
        POIType = intent.getIntExtra("type", -1);

        if (POIType == 1) {
            DMXH = intent.getStringExtra("DMBZ");
        }
        else if (POIType == 0) {
            POIC = intent.getStringExtra("POIC");
        }else if (POIType == 2) DML = intent.getStringExtra("DML");
        else if (POIType == 3) DMP = intent.getStringExtra("DMP");

        getBitmap();
    }

    private void getBitmap(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                bts.clear();
                if (POIType == 0) {
                    List<MPHOTO> mphotos = LitePal.where("poic = ?", POIC).find(MPHOTO.class);
                    int size = mphotos.size();
                    for (int i = 0; i < size; i++) {
                        String path = mphotos.get(i).getPath();
                        Log.w(TAG, "openImg: " + path);
                        File file = new File(path);
                        if (file.exists()) {
                            Bitmap bitmap = DataUtil.getImageThumbnail(path, 2048, 2048);
                            int degree = DataUtil.getPicRotate(path);
                            if (degree != 0) {
                                Matrix m = new Matrix();
                                m.setRotate(degree); // 旋转angle度
                                Log.w(TAG, "showPopueWindowForPhoto: " + degree);
                                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
                            }
                            bt btt = new bt(bitmap, path);
                            bts.add(btt);
                        } else {
                            Bitmap bitmap = Bitmap.createBitmap(80, 120, Bitmap.Config.ALPHA_8);
                            bts.add(new bt(bitmap, ""));
                        }
                    }
                }else if (POIType == 1){
                    List<DMBZ> dmList = LitePal.where("xh = ?", DMXH).find(DMBZ.class);
                    if (dmList.size() >= 1) {
                        String ImgPathTemp = dmList.get(0).getIMGPATH();
                        openImg(ImgPathTemp);
                    }
                }else if (POIType == 2){
                    List<DMLine> dmList = LitePal.where("mapid = ?", DML).find(DMLine.class);
                    if (dmList.size() >= 1) {
                        String ImgPathTemp = dmList.get(0).getImgpath();
                        openImg(ImgPathTemp);
                    }
                }else if (POIType == 3){
                    List<DMPoint> dmList = LitePal.where("mapid = ?", DMP).find(DMPoint.class);
                    if (dmList.size() >= 1) {
                        String ImgPathTemp = dmList.get(0).getImgpath();
                        openImg(ImgPathTemp);
                    }
                }
                isCreateBitmap = true;
            }
        }).start();
    }

    private void openImg(String ImgPathTemp){
        if (ImgPathTemp != null) {
            String[] imgPath = new String[DataUtil.appearNumber(ImgPathTemp, "\\|") + 1];
            Log.w(TAG, "openImg: " + ImgPathTemp);
            for (int k = 0; k < imgPath.length; k++) {
                if (imgPath.length > 1) {
                    if (k < imgPath.length - 1) {
                        imgPath[k] = ImgPathTemp.substring(0, ImgPathTemp.indexOf("|"));
                        ImgPathTemp = ImgPathTemp.substring(ImgPathTemp.indexOf("|") + 1);
                    } else imgPath[k] = ImgPathTemp;
                } else imgPath[k] = ImgPathTemp;
            }
            for (int kk = 0; kk < imgPath.length; kk++) {
                String rootpath = Environment.getExternalStorageDirectory().toString() + "/盘龙区多媒体数据/照片/";
                String path;
                if (!imgPath[kk].contains(Environment.getExternalStorageDirectory().toString())) {
                    path = rootpath + imgPath[kk];
                } else {
                    path = imgPath[kk];
                }
                File file = new File(path);
                if (file.exists()) {
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
                        int degree = DataUtil.getPicRotate(path);
                        if (degree != 0) {
                            Matrix m = new Matrix();
                            m.setRotate(degree); // 旋转angle度
                            Log.w(TAG, "showPopueWindowForPhoto: " + degree);
                            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
                        }
                        bts.add(new bt(bitmap, imgPath[kk]));
                    } catch (IOException e) {
                        Bitmap bitmap = Bitmap.createBitmap(80, 120, Bitmap.Config.ALPHA_8);
                        bts.add(new bt(bitmap, ""));
                    }
                } else {
                    Bitmap bitmap = Bitmap.createBitmap(80, 120, Bitmap.Config.ALPHA_8);
                    bts.add(new bt(bitmap, ""));
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (POIType == 0) refreshCardFromPOI();
        else if (POIType == 1) refreshCardFromDMBZ();
        else if (POIType == 2) refreshCardFromDML();
        else if (POIType == 3) refreshCardFromDMP();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.poiinfotoolbar, menu);
        menu.findItem(R.id.back_andupdate).setVisible(false);
        menu.findItem(R.id.query_poi_map).setVisible(false);
        return true;
    }

    private void resetView(){
        isLongClick = 1;
        setTitle(photoshow.this.getResources().getText(R.string.PhotoList));
        if (POIType == 0) refreshCardFromPOI();
        else if (POIType == 1) refreshCardFromDMBZ();
        else if (POIType == 2) refreshCardFromDML();
        else if (POIType == 3) refreshCardFromDMP();
        invalidateOptionsMenu();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.back_pois:
                this.finish();
                //showPopueWindowForPhoto("/storage/emulated/0/DCIM/Camera/IMG_20180322_230831.jpg");
                break;
            case R.id.restore_pois:
                resetView();
                break;
            case R.id.deletepoi:
                isLongClick = 1;
                setTitle(photoshow.this.getResources().getText(R.string.PhotoList));
                invalidateOptionsMenu();
                //LitePal.deleteAll(MPHOTO.class, "POIC = ?", POIC, "path = ?", deletePath);
                if (POIType == 0) {
                    parseSelectedPathToPOI();
                    refreshCardFromPOI();
                } else if (POIType == 1) {
                    parseSelectedPathToDMBZ();
                    refreshCardFromDMBZ();
                }else if (POIType == 2) {
                    parseSelectedPathToDML();
                    refreshCardFromDML();
                }else if (POIType == 3) {
                    parseSelectedPathToDMP();
                    refreshCardFromDMP();
                }
                break;
            case R.id.add_pois:
                showPopueWindowForPhoto();
                //refreshCardFromPOI();
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_PHOTO) {
            Uri uri = data.getData();
            if (POIType == 0) {
                List<POI> POIs = LitePal.where("poic = ?", POIC).find(POI.class);
                POI poi = new POI();
                poi.setPhotonum(POIs.get(0).getPhotonum() + 1);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(photoshow.this.getResources().getText(R.string.DateAndTime).toString());
                Date date = new Date(System.currentTimeMillis());
                poi.updateAll("poic = ?", POIC);
                MPHOTO mphoto = new MPHOTO();
                mphoto.setPdfic(POIs.get(0).getIc());
                mphoto.setPoic(POIC);
                //Log.w(TAG, "onActivityResult: " + uri.getPath() );
                //mphoto.setPath(getRealPath(uri.getPath()));
                mphoto.setPath(DataUtil.getRealPathFromUriForPhoto(this, uri));
                mphoto.setTime(simpleDateFormat.format(date));
                mphoto.save();
                refreshCardFromPOI();
            }else if (POIType == 1){
                List<DMBZ> dmbzs = LitePal.where("xh = ?", DMXH).find(DMBZ.class);
                DMBZ dmbz = new DMBZ();
                if (dmbzs.get(0).getIMGPATH() != null) {
                    String imgpath = dmbzs.get(0).getIMGPATH();
                    if (DataUtil.appearNumber(imgpath, "\\|") + 1 > 0) dmbz.setIMGPATH(imgpath + "|" + DataUtil.getRealPathFromUriForPhoto(this, uri));
                    else dmbz.setIMGPATH(DataUtil.getRealPathFromUriForPhoto(this, uri));
                }else dmbz.setIMGPATH(DataUtil.getRealPathFromUriForPhoto(this, uri));
                dmbz.updateAll("xh = ?", DMXH);
                refreshCardFromDMBZ();
            }else if (POIType == 2){
                List<DMLine> dmLines = LitePal.where("mapid = ?", DML).find(DMLine.class);
                DMLine dmLine = new DMLine();
                if (dmLines.get(0).getImgpath() != null) {
                    String imgpath = dmLines.get(0).getImgpath();
                    if (DataUtil.appearNumber(imgpath, "\\|") + 1 > 0) dmLine.setImgpath(imgpath + "|" + DataUtil.getRealPathFromUriForPhoto(this, uri));
                    else dmLine.setImgpath(DataUtil.getRealPathFromUriForPhoto(this, uri));
                }else dmLine.setImgpath(DataUtil.getRealPathFromUriForPhoto(this, uri));
                dmLine.updateAll("mapid = ?", DML);
                refreshCardFromDML();
            }else if (POIType == 3){
                List<DMPoint> dmPoints = LitePal.where("mapid = ?", DMP).find(DMPoint.class);
                DMPoint dmPoint = new DMPoint();
                if (dmPoints.get(0).getImgpath() != null) {
                    String imgpath = dmPoints.get(0).getImgpath();
                    if (DataUtil.appearNumber(imgpath, "\\|") + 1 > 0) dmPoint.setImgpath(imgpath + "|" + DataUtil.getRealPathFromUriForPhoto(this, uri));
                    else dmPoint.setImgpath(DataUtil.getRealPathFromUriForPhoto(this, uri));
                }else dmPoint.setImgpath(DataUtil.getRealPathFromUriForPhoto(this, uri));
                dmPoint.updateAll("mapid = ?", DMP);
                refreshCardFromDMP();
            }
            getBitmap();
        }
        if (resultCode == RESULT_OK && requestCode == TAKE_PHOTO) {

            if (imageUri != null) {
                String imageuri;
                if (Build.VERSION.SDK_INT >= 24) {
                    imageuri = DataUtil.getRealPath(imageUri.toString());
                } else {
                    imageuri = imageUri.toString().substring(7);
                }
                File file = new File(imageuri);
                if (file.length() != 0) {
                    try {
                        MediaStore.Images.Media.insertImage(getContentResolver(), imageuri, "title", "description");
                        // 最后通知图库更新
                        photoshow.this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + imageuri)));
                    } catch (IOException e) {
                    }
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(photoshow.this.getResources().getText(R.string.DateAndTime).toString());
                    Date date = new Date(System.currentTimeMillis());
                    if (POIType == 0) {
                        List<POI> POIs = LitePal.where("poic = ?", POIC).find(POI.class);
                        POI poi = new POI();
                        poi.setPhotonum(POIs.get(0).getPhotonum() + 1);
                        poi.updateAll("poic = ?", POIC);
                        MPHOTO mphoto = new MPHOTO();
                        mphoto.setPoic(POIC);
                        mphoto.setPath(imageuri);
                        mphoto.setTime(simpleDateFormat.format(date));
                        mphoto.save();
                        refreshCardFromPOI();
                    } else if (POIType == 1) {
                        List<DMBZ> dmbzs = LitePal.where("xh = ?", DMXH).find(DMBZ.class);
                        DMBZ dmbz = new DMBZ();
                        if (dmbzs.get(0).getIMGPATH() != null) {
                            String imgpath = dmbzs.get(0).getIMGPATH();
                            if (DataUtil.appearNumber(imgpath, "\\|") + 1 > 0)
                                dmbz.setIMGPATH(imgpath + "|" + imageuri);
                            else dmbz.setIMGPATH(imageuri);
                        } else dmbz.setIMGPATH(imageuri);
                        dmbz.updateAll("xh = ?", DMXH);
                        refreshCardFromDMBZ();
                    } else if (POIType == 2) {
                        List<DMLine> dmLines = LitePal.where("mapid = ?", DML).find(DMLine.class);
                        DMLine dmLine = new DMLine();
                        if (dmLines.get(0).getImgpath() != null) {
                            String imgpath = dmLines.get(0).getImgpath();
                            if (DataUtil.appearNumber(imgpath, "\\|") + 1 > 0)
                                dmLine.setImgpath(imgpath + "|" + imageuri);
                            else dmLine.setImgpath(imageuri);
                        } else dmLine.setImgpath(imageuri);
                        dmLine.updateAll("mapid = ?", DML);
                        refreshCardFromDML();
                    } else if (POIType == 3) {
                        List<DMPoint> dmPoints = LitePal.where("mapid = ?", DMP).find(DMPoint.class);
                        DMPoint dmPoint = new DMPoint();
                        if (dmPoints.get(0).getImgpath() != null) {
                            String imgpath = dmPoints.get(0).getImgpath();
                            if (DataUtil.appearNumber(imgpath, "\\|") + 1 > 0)
                                dmPoint.setImgpath(imgpath + "|" + imageuri);
                            else dmPoint.setImgpath(imageuri);
                        } else dmPoint.setImgpath(imageuri);
                        dmPoint.updateAll("mapid = ?", DMP);
                        refreshCardFromDMP();
                    }
                    getBitmap();
                } else {
                    file.delete();
                    Toast.makeText(photoshow.this, R.string.TakePhotoError, Toast.LENGTH_LONG).show();
                }
            }
            else
                Toast.makeText(photoshow.this, "拍摄错误，请重新拍摄",Toast.LENGTH_LONG).show();
        }
    }

    void launchPicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        try {
            startActivityForResult(intent, REQUEST_CODE_PHOTO);
        } catch (ActivityNotFoundException e) {
            //alert user that file manager not working
            Toast.makeText(this, R.string.toast_pick_file_error, Toast.LENGTH_SHORT).show();
        }
    }

    private void parseSelectedPathToPOI(){
        if (deletePath.contains("wslzy")){
            String[] nums = deletePath.split("wslzy");
            Log.w(TAG, "parseSelectedPathToPOI: " + nums[0] );
            for (int i = 0; i < nums.length; i++){
                //LitePal.deleteAll(MPHOTO.class, "poic = ? and path = ?", POIC, nums[i]);
                LitePal.deleteAll(MPHOTO.class, "poic = ? and time = ?", POIC, nums[i]);
            }
        }else {
            //LitePal.deleteAll(MPHOTO.class, "poic = ? and path = ?", POIC, deletePath);
            LitePal.deleteAll(MPHOTO.class, "poic = ? and time = ?", POIC, deletePath);
        }
    }

    private void parseSelectedPathToDMBZ(){
        if (deletePath.contains("wslzy")){
            String[] nums = deletePath.split("wslzy");
            Log.w(TAG, "parseSelectedPathToDMBZ: " + nums[0] );
            List<DMBZ> pois1 = LitePal.where("xh = ?", DMXH).find(DMBZ.class);
            String path = pois1.get(0).getIMGPATH();
            Log.w(TAG, "parseSelectedPathToDMBZ: " + path);
            for (int i = 0; i < nums.length; i++){
                //LitePal.deleteAll(MPHOTO.class, "poic = ? and path = ?", POIC, nums[i]);
                if (DataUtil.appearNumber(path, "\\|") + 1 > 0) {
                    if (DataUtil.appearNumber(path, "\\|") + 1 > 1) {
                        if (path.indexOf(nums[i]) != 0)
                            path = path.replace("|" + nums[i], "");
                        else{
                            path = path.replace(nums[i] + "|", "");
                        }
                    } else {
                        path = "";
                    }
                }
            }
            DMBZ poi = new DMBZ();
            poi.setIMGPATH(path);
            poi.updateAll("xh = ?", DMXH);
        }else {
            List<DMBZ> pois1 = LitePal.where("xh = ?", DMXH).find(DMBZ.class);
            String path = pois1.get(0).getIMGPATH();
            if (DataUtil.appearNumber(path, "\\|") + 1 > 0) {
                DMBZ poi = new DMBZ();
                if (DataUtil.appearNumber(path, "\\|") + 1 > 1) {
                    if (path.indexOf(deletePath) != 0)
                        poi.setIMGPATH(path.replace("|" + deletePath, ""));
                    else
                        poi.setIMGPATH(path.replace(deletePath + "|", ""));
                } else {
                    poi.setIMGPATH("");
                }
                poi.updateAll("xh = ?", DMXH);
            }
        }
    }

    private void parseSelectedPathToDMP(){
        if (deletePath.contains("wslzy")){
            String[] nums = deletePath.split("wslzy");
            Log.w(TAG, "parseSelectedPathToDMBZ: " + nums[0] );
            List<DMPoint> dmPoints = LitePal.where("mapid = ?", DMP).find(DMPoint.class);
            String path = dmPoints.get(0).getImgpath();
            Log.w(TAG, "parseSelectedPathToDMBZ: " + path);
            for (int i = 0; i < nums.length; i++){
                //LitePal.deleteAll(MPHOTO.class, "poic = ? and path = ?", POIC, nums[i]);
                if (DataUtil.appearNumber(path, "\\|") + 1 > 0) {
                    if (DataUtil.appearNumber(path, "\\|") + 1 > 1) {
                        if (path.indexOf(nums[i]) != 0)
                            path = path.replace("|" + nums[i], "");
                        else{
                            path = path.replace(nums[i] + "|", "");
                        }
                    } else {
                        path = "";
                    }
                }
            }
            DMPoint dmPoint = new DMPoint();
            dmPoint.setImgpath(path);
            dmPoint.updateAll("mapid = ?", DMP);
        }else {
            List<DMPoint> dmPoints = LitePal.where("mapid = ?", DMP).find(DMPoint.class);
            String path = dmPoints.get(0).getImgpath();
            if (DataUtil.appearNumber(path, "\\|") + 1 > 0) {
                DMPoint dmPoint = new DMPoint();
                if (DataUtil.appearNumber(path, "\\|") + 1 > 1) {
                    if (path.indexOf(deletePath) != 0)
                        dmPoint.setImgpath(path.replace("|" + deletePath, ""));
                    else
                        dmPoint.setImgpath(path.replace(deletePath + "|", ""));
                } else {
                    dmPoint.setImgpath("");
                }
                dmPoint.updateAll("mapid = ?", DML);
            }
        }
    }

    private void parseSelectedPathToDML(){
        if (deletePath.contains("wslzy")){
            String[] nums = deletePath.split("wslzy");
            Log.w(TAG, "parseSelectedPathToDMBZ: " + nums[0] );
            List<DMLine> dmLines = LitePal.where("mapid = ?", DML).find(DMLine.class);
            String path = dmLines.get(0).getImgpath();
            Log.w(TAG, "parseSelectedPathToDMBZ: " + path);
            for (int i = 0; i < nums.length; i++){
                //LitePal.deleteAll(MPHOTO.class, "poic = ? and path = ?", POIC, nums[i]);
                if (DataUtil.appearNumber(path, "\\|") + 1 > 0) {
                    if (DataUtil.appearNumber(path, "\\|") + 1 > 1) {
                        if (path.indexOf(nums[i]) != 0)
                            path = path.replace("|" + nums[i], "");
                        else{
                            path = path.replace(nums[i] + "|", "");
                        }
                    } else {
                        path = "";
                    }
                }
            }
            DMLine poi = new DMLine();
            poi.setImgpath(path);
            poi.updateAll("mapid = ?", DML);
        }else {
            List<DMLine> dmLines = LitePal.where("mapid = ?", DML).find(DMLine.class);
            String path = dmLines.get(0).getImgpath();
            if (DataUtil.appearNumber(path, "\\|") + 1 > 0) {
                DMLine dmLine = new DMLine();
                if (DataUtil.appearNumber(path, "\\|") + 1 > 1) {
                    if (path.indexOf(deletePath) != 0)
                        dmLine.setImgpath(path.replace("|" + deletePath, ""));
                    else
                        dmLine.setImgpath(path.replace(deletePath + "|", ""));
                } else {
                    dmLine.setImgpath("");
                }
                dmLine.updateAll("mapid = ?", DML);
            }
        }
    }

    private void showPopueWindowForPhoto(){
        View popView = View.inflate(this,R.layout.popupwindow_camera_need,null);
        Button bt_album = (Button) popView.findViewById(R.id.btn_pop_album);
        Button bt_camera = (Button) popView.findViewById(R.id.btn_pop_camera);
        Button bt_cancle = (Button) popView.findViewById(R.id.btn_pop_cancel);
        //获取屏幕宽高
        int weight = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels * 1/3;

        final PopupWindow popupWindow = new PopupWindow(popView, weight ,height);
        //popupWindow.setAnimationStyle(R.style.anim_popup_dir);
        popupWindow.setFocusable(true);
        //点击外部popueWindow消失
        popupWindow.setOutsideTouchable(true);

        bt_album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchPicker();
                popupWindow.dismiss();

            }
        });
        bt_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestCameraPermission();
                popupWindow.dismiss();

            }
        });
        bt_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();

            }
        });
        //popupWindow消失屏幕变为不透明
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1.0f;
                getWindow().setAttributes(lp);
            }
        });
        //popupWindow出现屏幕变为半透明
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.5f;
        getWindow().setAttributes(lp);
        popupWindow.showAtLocation(popView, Gravity.BOTTOM,0,50);

    }

    private void showPopueWindowForPhoto(String path){
        //final RelativeLayout linearLayout= (RelativeLayout) getLayoutInflater().inflate(R.layout.popupwindow_photo_show, null);
        View popView = View.inflate(this,R.layout.popupwindow_photo_show,null);
        final ImageView imageView1 = (ImageView) popView.findViewById(R.id.photoshow_all1);
        Log.w(TAG, "showPopueWindowForPhoto: " + path);
        //File outputImage = new File(path);
        //
        int size = bts.size();
        for (int i = 0; i < size; i++){
            if (path.contains(bts.get(i).getM_path())) imageView1.setImageBitmap(bts.get(i).getM_bm());
        }

        //获取屏幕宽高
        int weight = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels * 2 / 3;

        final PopupWindow popupWindow = new PopupWindow(popView, weight ,height);
        //popupWindow.setAnimationStyle(R.style.anim_popup_dir);

        popupWindow.setFocusable(true);
        //点击外部popueWindow消失
        popupWindow.setOutsideTouchable(true);
        //popupWindow消失屏幕变为不透明
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1.0f;
                getWindow().setAttributes(lp);
            }
        });
        /*popupWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //imageView1.setVisibility(View.INVISIBLE);
                popupWindow.dismiss();
                return false;
            }
        });*/
        //popupWindow出现屏幕变为半透明
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.5f;
        getWindow().setAttributes(lp);
        popupWindow.showAtLocation(popView, Gravity.BOTTOM,0,50);

    }

    private Bitmap getImageThumbnail(String imagePath, int width, int height) {
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        // 获取这个图片的宽和高，注意此处的bitmap为null
        bitmap = BitmapFactory.decodeFile(imagePath, options);
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
