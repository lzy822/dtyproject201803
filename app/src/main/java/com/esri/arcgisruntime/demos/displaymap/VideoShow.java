package com.esri.arcgisruntime.demos.displaymap;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
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
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import org.litepal.LitePal;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.esri.arcgisruntime.demos.displaymap.CameraUtils.RequestCode.FLAG_REQUEST_CAMERA_IMAGE;
import static com.esri.arcgisruntime.demos.displaymap.CameraUtils.RequestCode.FLAG_REQUEST_CAMERA_VIDEO;
import static com.esri.arcgisruntime.demos.displaymap.CameraUtils.RequestCode.FLAG_REQUEST_SYSTEM_VIDEO;

public class VideoShow extends AppCompatActivity {

    private static final String TAG = "VideoShow";
    private String POIC;
    private List<mVedioobj> mVedioobjList = new ArrayList<>();
    private mVedioobjAdapter adapter;
    private RecyclerView recyclerView;
    private GridLayoutManager layoutManager;
    private String deletePath;
    private final static int REQUEST_CODE_TAPE = 43;
    private int isLongClick = 1;
    Toolbar toolbar;
    private int POIType;
    private String DMXH;
    private String DML;
    private String DMP;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_show);
        //声明ToolBar
        setTitle(VideoShow.this.getResources().getText(R.string.VideoList));
        Intent intent = getIntent();
        POIType = intent.getIntExtra("type", -1);
        if (POIType == 0) POIC = intent.getStringExtra("POIC");
        else if (POIType == 1) DMXH = intent.getStringExtra("DMBZ");
        else if (POIType == 2) DML = intent.getStringExtra("DML");
        else if (POIType == 3) DMP = intent.getStringExtra("DMP");
        toolbar = (Toolbar) findViewById(R.id.VedioShow_Toolbal);
        setSupportActionBar(toolbar);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (POIType == 0)
            refreshCardFromPOI();
        /*else if (POIType == 1) refreshCardFromDMBZ();
        else if (POIType == 2) refreshCardFromDML();
        else if (POIType == 3) refreshCardFromDMP();*/
    }

    private void refreshCardFromPOI(){
        mVedioobjList.clear();
        List<MVEDIO> mvedios = LitePal.where("POIC = ?", POIC).find(MVEDIO.class);
        for (MVEDIO mvedio : mvedios){
            mVedioobj mvedioobj = new mVedioobj(mvedio.getPoic(), mvedio.getPoic(), mvedio.getTime(), mvedio.getPath(), mvedio.getThumbnailImg());
            mVedioobjList.add(mvedioobj);
        }
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_video);
        layoutManager = new GridLayoutManager(this,1);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new mVedioobjAdapter(mVedioobjList);
        adapter.setOnItemLongClickListener(new mVedioobjAdapter.OnRecyclerItemLongListener() {
            @Override
            public void onItemLongClick(View view, String path) {
                setTitle(VideoShow.this.getResources().getText(R.string.IsLongClicking));
                if (isLongClick != 0) {
                    deletePath = path;
                    isLongClick = 0;
                    invalidateOptionsMenu();
                    Log.w(TAG, "onItemLongClick: " + deletePath);
                }
                else {
                    mVedioobjAdapter.ViewHolder holder = new mVedioobjAdapter.ViewHolder(view);
                    if (holder.cardView.getCardBackgroundColor().getDefaultColor() != Color.GRAY){
                        holder.cardView.setCardBackgroundColor(Color.GRAY);
                        deletePath = deletePath + "wslzy" + path;
                    }else {
                        holder.cardView.setCardBackgroundColor(Color.WHITE);
                        if (deletePath.contains("wslzy")) {
                            String replace = "wslzy" + path;
                            if (!deletePath.contains(replace)){
                                replace = path + "wslzy";
                            }
                            deletePath = deletePath.replace(replace, "");
                        }else {
                            resetView();
                        }
                    }
                }
            }
        });
        adapter.setOnItemClickListener(new mVedioobjAdapter.OnRecyclerItemClickListener() {
            @Override
            public void onItemClick(View view, String path, int position) {
                mVedioobjAdapter.ViewHolder holder = new mVedioobjAdapter.ViewHolder(view);
                if (isLongClick == 0){
                    if (holder.cardView.getCardBackgroundColor().getDefaultColor() != Color.GRAY){
                        holder.cardView.setCardBackgroundColor(Color.GRAY);
                        deletePath = deletePath + "wslzy" + path;
                    }else {
                        holder.cardView.setCardBackgroundColor(Color.WHITE);
                        if (deletePath.contains("wslzy")) {
                            String replace = "wslzy" + path;
                            if (!deletePath.contains(replace)){
                                replace = path + "wslzy";
                            }
                            deletePath = deletePath.replace(replace, "");
                        }else {
                            resetView();
                        }
                    }
                }else {
                    /*File file = new File(path);
                    if (file.exists()) {
                        MediaPlayer mediaPlayer = MediaPlayer.create(VideoShow.this, Uri.parse(path));
                        mediaPlayer.start();
                    }else {
                        Toast.makeText(MyApplication.getContext(), R.string.TakeTapeError1, Toast.LENGTH_LONG).show();
                    }*/
                    // TODO 2021/1/26 完成点击视频后的播放模块
                    showPopueWindowForPhoto(path);

                }
                Log.w(TAG, "onItemClick: " + deletePath );
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void showPopueWindowForPhoto(String path){
        //final RelativeLayout linearLayout= (RelativeLayout) getLayoutInflater().inflate(R.layout.popupwindow_photo_show, null);
        View popView = View.inflate(this,R.layout.popupwindow_video_show,null);
        final VideoView videoView = (VideoView) popView.findViewById(R.id.videoshow_all);
        Log.w(TAG, "showPopueWindowForPhoto: " + path);
        //File outputImage = new File(path);
        //

        videoView.setVideoPath(path);//setVideoURI(Uri.parse(uriString));
        videoView.start();

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

    /*private void refreshCardFromDMBZ(){
        mTapeobjList.clear();
        List<DMBZ> dmbzList = LitePal.where("xh = ?", DMXH).find(DMBZ.class);
        String ImgPathTemp = dmbzList.get(0).getTAPEPATH();
        if (ImgPathTemp != null) {
            String[] imgPath = new String[DataUtil.appearNumber(ImgPathTemp, ".mp3")];
            Log.w(TAG, "run: " + ImgPathTemp);
            for (int k = 0; k < imgPath.length; k++) {
                imgPath[k] = ImgPathTemp.substring(0, ImgPathTemp.indexOf(".mp3") + 4);
                if (k < imgPath.length - 1)
                    ImgPathTemp = ImgPathTemp.substring(ImgPathTemp.indexOf(".mp3") + 5);
                Log.w(TAG, "run: " + ImgPathTemp);
            }
            for (int kk = 0; kk < imgPath.length; kk++) {
                String rootpath = Environment.getExternalStorageDirectory().toString() + "/地名标志录音/";
                String path;
                if (!imgPath[kk].contains(Environment.getExternalStorageDirectory().toString())) {
                    path = rootpath + imgPath[kk];
                } else {
                    path = imgPath[kk];
                }
                mTapeobj mtapeobj = new mTapeobj(dmbzList.get(0).getBZMC(), dmbzList.get(0).getBZMC(), dmbzList.get(0).getTime(), path);
                mTapeobjList.add(mtapeobj);
            }
        }
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_tape);
        layoutManager = new GridLayoutManager(this,1);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new mTapeobjAdapter(mTapeobjList);
        adapter.setOnItemLongClickListener(new mTapeobjAdapter.OnRecyclerItemLongListener() {
            @Override
            public void onItemLongClick(View view, String path) {
                setTitle(tapeshow.this.getResources().getText(R.string.IsLongClicking));
                deletePath = path;
                isLongClick = 0;
                invalidateOptionsMenu();
                Log.w(TAG, "onItemLongClick: " + deletePath );
            }
        });
        adapter.setOnItemClickListener(new mTapeobjAdapter.OnRecyclerItemClickListener() {
            @Override
            public void onItemClick(View view, String path, int position) {
                mTapeobjAdapter.ViewHolder holder = new mTapeobjAdapter.ViewHolder(view);
                if (isLongClick == 0){
                    if (holder.cardView.getCardBackgroundColor().getDefaultColor() != Color.GRAY){
                        holder.cardView.setCardBackgroundColor(Color.GRAY);
                        deletePath = deletePath + "wslzy" + path;
                    }else {
                        holder.cardView.setCardBackgroundColor(Color.WHITE);
                        if (deletePath.contains("wslzy")) {
                            String replace = "wslzy" + path;
                            deletePath = deletePath.replace(replace, "");
                        }else {
                            resetView();
                        }
                    }
                }else {
                    File file = new File(path);
                    if (file.exists()) {
                        MediaPlayer mediaPlayer = MediaPlayer.create(tapeshow.this, Uri.parse(path));
                        mediaPlayer.start();
                    }else {
                        Toast.makeText(MyApplication.getContext(), R.string.TakeTapeError1, Toast.LENGTH_LONG).show();
                    }
                }
                Log.w(TAG, "onItemClick: " + deletePath );
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void refreshCardFromDML(){
        mTapeobjList.clear();
        List<DMLine> dmLines = LitePal.where("mapid = ?", DML).find(DMLine.class);
        String ImgPathTemp = dmLines.get(0).getTapepath();
        if (ImgPathTemp != null) {
            String[] imgPath = new String[DataUtil.appearNumber(ImgPathTemp, "\\|") + 1];
            Log.w(TAG, "run: " + ImgPathTemp);
            for (int k = 0; k < imgPath.length; k++) {
                if (imgPath.length > 1) {
                    if (k < imgPath.length - 1) {
                        imgPath[k] = ImgPathTemp.substring(0, ImgPathTemp.indexOf("|"));
                        ImgPathTemp = ImgPathTemp.substring(ImgPathTemp.indexOf("|") + 1);
                    }else imgPath[k] = ImgPathTemp;
                }else imgPath[k] = ImgPathTemp;
            }
            for (int kk = 0; kk < imgPath.length; kk++) {
                String rootpath = Environment.getExternalStorageDirectory().toString() + "/盘龙区多媒体数据/录音/";
                String path;
                if (!imgPath[kk].contains(Environment.getExternalStorageDirectory().toString())) {
                    path = rootpath + imgPath[kk];
                } else {
                    path = imgPath[kk];
                }
                mTapeobj mtapeobj = new mTapeobj(dmLines.get(0).getBzmc(), dmLines.get(0).getBzmc(), dmLines.get(0).getTime(), path);
                mTapeobjList.add(mtapeobj);
            }
        }
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_tape);
        layoutManager = new GridLayoutManager(this,1);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new mTapeobjAdapter(mTapeobjList);
        adapter.setOnItemLongClickListener(new mTapeobjAdapter.OnRecyclerItemLongListener() {
            @Override
            public void onItemLongClick(View view, String path) {
                setTitle(tapeshow.this.getResources().getText(R.string.IsLongClicking));
                deletePath = path;
                isLongClick = 0;
                invalidateOptionsMenu();
                Log.w(TAG, "onItemLongClick: " + deletePath );
            }
        });
        adapter.setOnItemClickListener(new mTapeobjAdapter.OnRecyclerItemClickListener() {
            @Override
            public void onItemClick(View view, String path, int position) {
                mTapeobjAdapter.ViewHolder holder = new mTapeobjAdapter.ViewHolder(view);
                if (isLongClick == 0){
                    if (holder.cardView.getCardBackgroundColor().getDefaultColor() != Color.GRAY){
                        holder.cardView.setCardBackgroundColor(Color.GRAY);
                        deletePath = deletePath + "wslzy" + path;
                    }else {
                        holder.cardView.setCardBackgroundColor(Color.WHITE);
                        if (deletePath.contains("wslzy")) {
                            String replace = "wslzy" + path;
                            deletePath = deletePath.replace(replace, "");
                        }else {
                            resetView();
                        }
                    }
                }else {
                    File file = new File(path);
                    if (file.exists()) {
                        MediaPlayer mediaPlayer = MediaPlayer.create(tapeshow.this, Uri.parse(path));
                        mediaPlayer.start();
                    }else {
                        Toast.makeText(MyApplication.getContext(), R.string.TakeTapeError1, Toast.LENGTH_LONG).show();
                    }
                }
                Log.w(TAG, "onItemClick: " + deletePath );
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void refreshCardFromDMP(){
        mTapeobjList.clear();
        List<DMPoint> dmPoints = LitePal.where("mapid = ?", DMP).find(DMPoint.class);
        String ImgPathTemp = dmPoints.get(0).getTapepath();
        if (ImgPathTemp != null) {
            String[] imgPath = new String[DataUtil.appearNumber(ImgPathTemp, "\\|") + 1];
            Log.w(TAG, "run: " + ImgPathTemp);
            for (int k = 0; k < imgPath.length; k++) {
                if (imgPath.length > 1) {
                    if (k < imgPath.length - 1) {
                        imgPath[k] = ImgPathTemp.substring(0, ImgPathTemp.indexOf("|"));
                        ImgPathTemp = ImgPathTemp.substring(ImgPathTemp.indexOf("|") + 1);
                    }else imgPath[k] = ImgPathTemp;
                }else imgPath[k] = ImgPathTemp;
            }
            for (int kk = 0; kk < imgPath.length; kk++) {
                String rootpath = Environment.getExternalStorageDirectory().toString() + "/盘龙区多媒体数据/录音/";
                String path;
                if (!imgPath[kk].contains(Environment.getExternalStorageDirectory().toString())) {
                    path = rootpath + imgPath[kk];
                } else {
                    path = imgPath[kk];
                }
                mTapeobj mtapeobj = new mTapeobj(dmPoints.get(0).getBzmc(), dmPoints.get(0).getBzmc(), dmPoints.get(0).getTime(), path);
                mTapeobjList.add(mtapeobj);
            }
        }
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_tape);
        layoutManager = new GridLayoutManager(this,1);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new mTapeobjAdapter(mTapeobjList);
        adapter.setOnItemLongClickListener(new mTapeobjAdapter.OnRecyclerItemLongListener() {
            @Override
            public void onItemLongClick(View view, String path) {
                setTitle(tapeshow.this.getResources().getText(R.string.IsLongClicking));
                deletePath = path;
                isLongClick = 0;
                invalidateOptionsMenu();
                Log.w(TAG, "onItemLongClick: " + deletePath );
            }
        });
        adapter.setOnItemClickListener(new mTapeobjAdapter.OnRecyclerItemClickListener() {
            @Override
            public void onItemClick(View view, String path, int position) {
                mTapeobjAdapter.ViewHolder holder = new mTapeobjAdapter.ViewHolder(view);
                if (isLongClick == 0){
                    if (holder.cardView.getCardBackgroundColor().getDefaultColor() != Color.GRAY){
                        holder.cardView.setCardBackgroundColor(Color.GRAY);
                        deletePath = deletePath + "wslzy" + path;
                    }else {
                        holder.cardView.setCardBackgroundColor(Color.WHITE);
                        if (deletePath.contains("wslzy")) {
                            String replace = "wslzy" + path;
                            deletePath = deletePath.replace(replace, "");
                        }else {
                            resetView();
                        }
                    }
                }else {
                    File file = new File(path);
                    if (file.exists()) {
                        MediaPlayer mediaPlayer = MediaPlayer.create(tapeshow.this, Uri.parse(path));
                        mediaPlayer.start();
                    }else {
                        Toast.makeText(MyApplication.getContext(), R.string.TakeTapeError1, Toast.LENGTH_LONG).show();
                    }
                }
                Log.w(TAG, "onItemClick: " + deletePath );
            }
        });
        recyclerView.setAdapter(adapter);
    }*/

    private void resetView(){
        isLongClick = 1;
        setTitle(VideoShow.this.getResources().getText(R.string.VideoList));
        if (POIType == 0)
            refreshCardFromPOI();
        /*else if (POIType == 1) refreshCardFromDMBZ();
        else if (POIType == 2) refreshCardFromDML();
        else if (POIType == 3) refreshCardFromDMP();*/
        invalidateOptionsMenu();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.poiinfotoolbar, menu);
        menu.findItem(R.id.back_andupdate).setVisible(false);
        menu.findItem(R.id.query_poi_map).setVisible(false);
        return true;
    }

    private void showPopueWindowForVideo(){
        View popView = View.inflate(this,R.layout.popupwindow_addvideo,null);
        Button bt_pickvideo = (Button) popView.findViewById(R.id.btn_pop_pickvideo);
        Button bt_takevideo = (Button) popView.findViewById(R.id.btn_pop_takevideo);
        Button bt_cancle = (Button) popView.findViewById(R.id.btn_pop_cancel_video);
        //获取屏幕宽高
        int weight = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels * 1/3;

        final PopupWindow popupWindow = new PopupWindow(popView, weight ,height);
        //popupWindow.setAnimationStyle(R.style.anim_popup_dir);
        popupWindow.setFocusable(true);
        //点击外部popueWindow消失
        popupWindow.setOutsideTouchable(true);

        bt_pickvideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //launchPicker();
                pickVideo(VideoShow.this);
                popupWindow.dismiss();

            }
        });
        bt_takevideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //takePhoto();
                takeVideo(VideoShow.this);
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

    public void pickVideo(Activity activity){

        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.RECORD_AUDIO);
        }
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.CAMERA);
        }
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()){
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(activity, permissions, 119);
        }else {
            pickVideo();
        }
    }

    private void pickVideo(){
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        this.startActivityForResult(intent,
                FLAG_REQUEST_SYSTEM_VIDEO);
    }

    public void takeVideo(Activity activity){
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.RECORD_AUDIO);
        }
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.CAMERA);
        }
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()){
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(activity, permissions, 118);
        }else {
            takeVideo();
        }
    }

    private void takeVideo(){
        File file2 = new File(Environment.getExternalStorageDirectory() + "/TuZhi/video");
        if (!file2.exists() && !file2.isDirectory()){
            file2.mkdirs();
        }
        long timenow = System.currentTimeMillis();
        File outputImage = new File(Environment.getExternalStorageDirectory() + "/TuZhi/video", Long.toString(timenow) + ".mp4");
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
            imageUri = FileProvider.getUriForFile(this, "com.android.displaymap.fileprovider", outputImage);

        }else imageUri = Uri.fromFile(outputImage);
        Log.w(TAG, "takeVideo: " + imageUri.toString());
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        MediaScannerConnection.scanFile(VideoShow.this, new String[]{
                outputImage.getAbsolutePath()},null,null);
        startActivityForResult(intent, FLAG_REQUEST_CAMERA_VIDEO);
    }

    public void ResultForPickVideo(Uri uri){

        try {
            String path = DataUtil.getRealPathFromUriForVedio(this, uri);
            /*MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(path);
            Bitmap bitmap = retriever.getFrameAtTime(0, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
            int degree = DataUtil.getPicRotate(path);
            if (degree != 0) {
                Matrix m = new Matrix();
                m.setRotate(degree); // 旋转angle度
                Log.w(TAG, "showPopueWindowForPhoto: " + degree);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
            }*/
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(VideoShow.this.getResources().getText(R.string.DateAndTime).toString());
            Date date = new Date(System.currentTimeMillis());
            List<POI> POIs = LitePal.where("poic = ?", POIC).find(POI.class);
            POI poi = new POI();
            long time = System.currentTimeMillis();
            poi.setPhotonum(POIs.get(0).getVedionum() + 1);
            poi.updateAll("poic = ?", POIC);
            MVEDIO mvedio = new MVEDIO();
            mvedio.setPoic(POIC);
            mvedio.setPath(path);
            mvedio.setTime(simpleDateFormat.format(date));

            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(path);
            Bitmap bitmap = retriever.getFrameAtTime(0, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
            int degree = DataUtil.getPicRotate(path);
            if (degree != 0) {
                Matrix m = new Matrix();
                m.setRotate(degree); // 旋转angle度
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
            }
            File file2 = new File(Environment.getExternalStorageDirectory() + "/TuZhi/video/img");
            if (!file2.exists() && !file2.isDirectory()){
                file2.mkdirs();
            }
            long timenow = System.currentTimeMillis();
            File outputImage = new File(Environment.getExternalStorageDirectory() + "/TuZhi/video/img", Long.toString(timenow) + ".jpg");
            try {
                if (outputImage.exists()){
                    outputImage.delete();
                }
                outputImage.createNewFile();
            }catch (IOException e){
                e.printStackTrace();
            }
            DataUtil.saveBitmap(bitmap, outputImage.getAbsolutePath());
            mvedio.setThumbnailImg(outputImage.getAbsolutePath());

            mvedio.save();
            List<MVEDIO> videos = LitePal.where("poic = ?", POIC).find(MVEDIO.class);
            TextView txt_videonum = (TextView) findViewById(R.id.txt_videonumshow);
            txt_videonum.setText(Integer.toString(videos.size()));
            /*iv.setImageBitmap(bitmap);
            videoView.setVideoPath(path);//setVideoURI(Uri.parse(uriString));
            videoView.start();*/
            Toast.makeText(this, path, Toast.LENGTH_LONG).show();
        }
        catch (Exception e){

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.back_pois:
                this.finish();
                break;
            case R.id.restore_pois:
                resetView();
                break;
            case R.id.deletepoi:
                isLongClick = 1;
                invalidateOptionsMenu();
                //LitePal.deleteAll(MTAPE.class, "POIC = ?", POIC, "path = ?", deletePath);
                //LitePal.deleteAll(MTAPE.class, "path = ?", deletePath);
                //LitePal.deleteAll(MTAPE.class, "POIC = ?", POIC);
                setTitle(VideoShow.this.getResources().getText(R.string.TapeList));
                if (POIType == 0)
                {
                    parseSelectedPathToPOI();
                    refreshCardFromPOI();
                }
                /*else if (POIType == 1) {
                    parseSelectedPathToDMBZ();
                    refreshCardFromDMBZ();
                }*/
                break;
            case R.id.add_pois:
                try {
                    showPopueWindowForVideo();
                    //else if (POIType == 1) refreshCardFromDMBZ();
                }catch (ActivityNotFoundException e){
                    Toast.makeText(MyApplication.getContext(), R.string.TakeTapeError, Toast.LENGTH_LONG).show();
                }
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Uri uri = data.getData();
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_TAPE){
            long time = System.currentTimeMillis();
            if (POIType == 0) {
                List<POI> POIs = LitePal.where("POIC = ?", POIC).find(POI.class);
                POI poi = new POI();
                poi.setTapenum(POIs.get(0).getTapenum() + 1);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(VideoShow.this.getResources().getText(R.string.DateAndTime).toString());
                Date date = new Date(time);
                poi.updateAll("POIC = ?", POIC);
                MTAPE mtape = new MTAPE();
                mtape.setPath(DataUtil.getRealPathFromUriForAudio(this, uri));
                mtape.setPdfic(POIs.get(0).getIc());
                mtape.setPoic(POIC);
                mtape.setTime(simpleDateFormat.format(date));
                mtape.save();
            }
            /*(else if (POIType == 1){
                List<DMBZ> dmbzs = LitePal.where("xh = ?", DMXH).find(DMBZ.class);
                DMBZ dmbz = new DMBZ();
                if (dmbzs.get(0).getTAPEPATH() != null) {
                    String imgpath = dmbzs.get(0).getTAPEPATH();
                    if (DataUtil.appearNumber(imgpath, ".MP3") > 0) dmbz.setTAPEPATH(imgpath + "|" + DataUtil.getRealPathFromUriForPhoto(this, uri));
                    else dmbz.setTAPEPATH(DataUtil.getRealPathFromUriForPhoto(this, uri));
                }else dmbz.setTAPEPATH(DataUtil.getRealPathFromUriForPhoto(this, uri));
                dmbz.updateAll("xh = ?", DMXH);
                refreshCardFromDMBZ();
            }
            else if (POIType == 2){
                List<DMLine> dmLines = LitePal.where("mapid = ?", DML).find(DMLine.class);
                DMLine dmLine = new DMLine();
                if (dmLines.get(0).getTapepath() != null) {
                    String tapepath = dmLines.get(0).getTapepath();
                    if (DataUtil.appearNumber(tapepath, ".MP3") > 0) dmLine.setTapepath(tapepath + "|" + DataUtil.getRealPathFromUriForPhoto(this, uri));
                    else dmLine.setTapepath(DataUtil.getRealPathFromUriForPhoto(this, uri));
                }else dmLine.setTapepath(DataUtil.getRealPathFromUriForPhoto(this, uri));
                dmLine.updateAll("mapid = ?", DML);
                refreshCardFromDML();
            }
            else if (POIType == 3){
                List<DMPoint> dmPoints = LitePal.where("mapid = ?", DMP).find(DMPoint.class);
                DMPoint dmPoint = new DMPoint();
                if (dmPoints.get(0).getTapepath() != null) {
                    String tapepath = dmPoints.get(0).getTapepath();
                    if (DataUtil.appearNumber(tapepath, ".MP3") > 0) dmPoint.setTapepath(tapepath + "|" + DataUtil.getRealPathFromUriForPhoto(this, uri));
                    else dmPoint.setTapepath(DataUtil.getRealPathFromUriForPhoto(this, uri));
                }else dmPoint.setTapepath(DataUtil.getRealPathFromUriForPhoto(this, uri));
                dmPoint.updateAll("mapid = ?", DMP);
                refreshCardFromDMP();
            }*/

        }
        else if (resultCode == RESULT_OK && requestCode == FLAG_REQUEST_SYSTEM_VIDEO){
            ResultForPickVideo(uri);
        }
        else if (resultCode == RESULT_OK && requestCode == FLAG_REQUEST_CAMERA_VIDEO){
            if (imageUri != null) {
                Toast.makeText(this, imageUri.toString(), Toast.LENGTH_LONG).show();
                String imageuri1;
                if (Build.VERSION.SDK_INT >= 24) {
                    imageuri1 = DataUtil.getRealPath(imageUri.toString());
                } else {
                    imageuri1 = imageUri.toString().substring(7);
                }
                    /*videoView.setVideoPath(imageuri);//setVideoURI(Uri.parse(uriString));
                    videoView.start();*/
                try {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(VideoShow.this.getResources().getText(R.string.DateAndTime).toString());
                    Date date = new Date(System.currentTimeMillis());
                    List<POI> POIs = LitePal.where("poic = ?", POIC).find(POI.class);
                    POI poi = new POI();
                    poi.setPhotonum(POIs.get(0).getVedionum() + 1);
                    poi.updateAll("poic = ?", POIC);
                    MVEDIO mvedio = new MVEDIO();
                    mvedio.setPoic(POIC);
                    mvedio.setPath(imageuri1);
                    mvedio.setTime(simpleDateFormat.format(date));
                    MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                    retriever.setDataSource(imageuri1);
                    Bitmap bitmap = retriever.getFrameAtTime(0, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
                    int degree = DataUtil.getPicRotate(imageuri1);
                    if (degree != 0) {
                        Matrix m = new Matrix();
                        m.setRotate(degree); // 旋转angle度
                        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
                    }
                    File file2 = new File(Environment.getExternalStorageDirectory() + "/TuZhi/video/img");
                    if (!file2.exists() && !file2.isDirectory()) {
                        file2.mkdirs();
                    }
                    long timenow = System.currentTimeMillis();
                    File outputImage = new File(Environment.getExternalStorageDirectory() + "/TuZhi/video/img", Long.toString(timenow) + ".jpg");
                    try {
                        if (outputImage.exists()) {
                            outputImage.delete();
                        }
                        outputImage.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    DataUtil.saveBitmap(bitmap, outputImage.getAbsolutePath());
                    mvedio.setThumbnailImg(outputImage.getAbsolutePath());
                    mvedio.save();
                    //iv.setImageBitmap(bitmap);
                } catch (Exception e) {

                }
            }
            else
                Toast.makeText(VideoShow.this, "拍摄错误，请重新拍摄",Toast.LENGTH_LONG).show();
        }
    }

    private void parseSelectedPathToPOI(){
        List<POI> pois = LitePal.where("poic = ?", POIC).find(POI.class);
        if (deletePath.contains("wslzy")){
            String[] nums = deletePath.split("wslzy");
            for (int i = 0; i < nums.length; i++){
                Log.w(TAG, "parseSelectedPath: " + nums[i]);
                try {
                    List<MVEDIO> videos = LitePal.where("poic = ?", POIC).find(MVEDIO.class);
                    for (int j = 0; j < videos.size(); j++) {
                        if (videos.get(j).getPath().equals(nums[i])) {
                            File file = new File(videos.get(j).getThumbnailImg());
                            file.delete();
                            break;
                        }
                    }
                }
                catch (Exception e){

                }
                LitePal.deleteAll(MVEDIO.class, "poic = ? and path = ?", POIC, nums[i]);
            }
        }else {
            Log.w(TAG, "parseSelectedPath111: " + deletePath);
            try {
                List<MVEDIO> videos = LitePal.where("poic = ?", POIC).find(MVEDIO.class);
                for (int j = 0; j < videos.size(); j++) {
                    if (videos.get(j).getPath().equals(deletePath)){
                        File file = new File(videos.get(j).getThumbnailImg());
                        file.delete();
                        break;
                    }
                }
            }
            catch (Exception e){

            }
            LitePal.deleteAll(MVEDIO.class, "poic = ? and path = ?", POIC, deletePath);
        }

        //
        POI poi = pois.get(0);
        List<MVEDIO> videos = LitePal.where("poic = ?", POIC).find(MVEDIO.class);
        poi.setVedionum(videos.size());
        poi.updateAll("poic = ?", POIC);
        deletePath = "";
    }

    private void parseSelectedPathToDMBZ(){
        if (deletePath.contains("wslzy")){
            String[] nums = deletePath.split("wslzy");
            Log.w(TAG, "parseSelectedPathToDMBZ: " + nums[0] );
            List<DMBZ> pois1 = LitePal.where("xh = ?", DMXH).find(DMBZ.class);
            String path = pois1.get(0).getTAPEPATH();
            Log.w(TAG, "parseSelectedPathToDMBZ: " + path);
            for (int i = 0; i < nums.length; i++){
                //LitePal.deleteAll(MPHOTO.class, "poic = ? and path = ?", POIC, nums[i]);
                if (DataUtil.appearNumber(path, ".mp3") > 0) {
                    if (DataUtil.appearNumber(path, ".mp3") > 1) {
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
            poi.setTAPEPATH(path);
            poi.updateAll("xh = ?", DMXH);
        }else {
            List<DMBZ> pois1 = LitePal.where("xh = ?", DMXH).find(DMBZ.class);
            String path = pois1.get(0).getTAPEPATH();
            if (DataUtil.appearNumber(path, ".mp3") > 0) {
                DMBZ poi = new DMBZ();
                if (DataUtil.appearNumber(path, ".mp3") > 1) {
                    if (path.indexOf(deletePath) != 0)
                        poi.setTAPEPATH(path.replace("|" + deletePath, ""));
                    else
                        poi.setTAPEPATH(path.replace(deletePath + "|", ""));
                } else {
                    poi.setTAPEPATH("");
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
            String path = dmPoints.get(0).getTapepath();
            Log.w(TAG, "parseSelectedPathToDMBZ: " + path);
            for (int i = 0; i < nums.length; i++){
                //LitePal.deleteAll(MPHOTO.class, "poic = ? and path = ?", POIC, nums[i]);
                if (DataUtil.appearNumber(path, ".MP3") > 0) {
                    if (DataUtil.appearNumber(path, ".MP3") > 1) {
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
            dmPoint.setTapepath(path);
            dmPoint.updateAll("mapid = ?", DMP);
        }else {
            List<DMPoint> dmPoints = LitePal.where("mapid = ?", DMP).find(DMPoint.class);
            String path = dmPoints.get(0).getTapepath();
            if (DataUtil.appearNumber(path, ".MP3") > 0) {
                DMPoint dmPoint = new DMPoint();
                if (DataUtil.appearNumber(path, ".MP3") > 1) {
                    if (path.indexOf(deletePath) != 0)
                        dmPoint.setTapepath(path.replace("|" + deletePath, ""));
                    else
                        dmPoint.setTapepath(path.replace(deletePath + "|", ""));
                } else {
                    dmPoint.setTapepath("");
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
            String path = dmLines.get(0).getTapepath();
            Log.w(TAG, "parseSelectedPathToDMBZ: " + path);
            for (int i = 0; i < nums.length; i++){
                //LitePal.deleteAll(MPHOTO.class, "poic = ? and path = ?", POIC, nums[i]);
                if (DataUtil.appearNumber(path, ".MP3") > 0) {
                    if (DataUtil.appearNumber(path, ".MP3") > 1) {
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
            poi.setTapepath(path);
            poi.updateAll("mapid = ?", DML);
        }else {
            List<DMLine> dmLines = LitePal.where("mapid = ?", DML).find(DMLine.class);
            String path = dmLines.get(0).getTapepath();
            if (DataUtil.appearNumber(path, ".MP3") > 0) {
                DMLine dmLine = new DMLine();
                if (DataUtil.appearNumber(path, ".MP3") > 1) {
                    if (path.indexOf(deletePath) != 0)
                        dmLine.setTapepath(path.replace("|" + deletePath, ""));
                    else
                        dmLine.setTapepath(path.replace(deletePath + "|", ""));
                } else {
                    dmLine.setTapepath("");
                }
                dmLine.updateAll("mapid = ?", DML);
            }
        }
    }
}

