package com.esri.arcgisruntime.demos.displaymap;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;

import org.litepal.LitePal;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class singlepoi extends AppCompatActivity {

    private static final String TAG = "singlepoi";
    private String POIC;
    private String name;
    private EditText editText_name;
    private EditText editText_des;
    private final static int REQUEST_CODE_PHOTO = 42;
    private final static int REQUEST_CODE_TAPE = 43;
    private final static int TAKE_PHOTO = 41;
    Uri imageUri;
    Spinner type_spinner;
    float m_lat, m_lng;
    private int POITYPE = -1;
    private String DMXH;
    private String DMP;
    private String DML;
    private TextView edit_XH;
    private EditText edit_DY;
    private EditText edit_MC;
    private EditText edit_BZMC;
    private EditText edit_XZQMC;
    private EditText edit_XZQDM;
    private EditText edit_SZDW;
    private EditText edit_SCCJ;
    private EditText edit_GG;
    private TextView edit_qydm;
    private EditText edit_lbdm;
    private EditText edit_bzmc;
    private EditText edit_cym;
    private EditText edit_jc;
    private EditText edit_bm;
    private EditText edit_dfyz;
    private EditText edit_zt;
    private EditText edit_dmll;
    private EditText edit_dmhy;
    private EditText edit_lsyg;
    private EditText edit_dlstms;
    private EditText edit_zlly;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singlepoi);
        //声明ToolBar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        POITYPE = intent.getIntExtra("type", -1);
        Log.w(TAG, "onCreate: " + POITYPE);

        textView_photonum = (TextView) findViewById(R.id.txt_photonumshow);
        textView_photonum.setVisibility(View.VISIBLE);

        if (POITYPE == 0) {
            setTitle("兴趣点信息");
            POIC = intent.getStringExtra("POIC");
            type_spinner = (Spinner) findViewById(R.id.type_selection);
            type_spinner.setVisibility(View.VISIBLE);
            Log.w(TAG, "onCreate: 0 ");
        }else if (POITYPE == 1){
            setTitle("地名标志信息");
            DMXH = intent.getStringExtra("DMBZ");
            Log.w(TAG, "onCreate: 1 ");
            //RefreshDMBZ();
        }else if (POITYPE == 2){
            setTitle("地名信息");
            DML = intent.getStringExtra("DML");
            Log.w(TAG, "onCreate: 1 ");
            //RefreshDMBZ();
        }else if (POITYPE == 3){
            setTitle("地名信息");
            DMP = intent.getStringExtra("DMP");
            Log.w(TAG, "onCreate: 1 ");
            //RefreshDMBZ();
        }
    }

    private void GoDMBZSinglePhotoPage(String XH){
        Log.w(TAG, "updateMapPage: 1");
        Intent intent = new Intent(singlepoi.this, photoshow.class);
        intent.putExtra("DMBZ", XH);
        intent.putExtra("type", 1);
        startActivity(intent);
    }

    private void GoSinglePhotoPage(String XH, int type){
        Log.w(TAG, "updateMapPage: 1");
        if (type == 1) {
            Intent intent = new Intent(singlepoi.this, photoshow.class);
            intent.putExtra("DMBZ", XH);
            intent.putExtra("type", 1);
            startActivity(intent);
        } else if (type == 2){
            Intent intent = new Intent(singlepoi.this, photoshow.class);
            intent.putExtra("DML", DML);
            intent.putExtra("type", 2);
            startActivity(intent);
        }else if (type == 3){
            Intent intent = new Intent(singlepoi.this, photoshow.class);
            intent.putExtra("DMP", DMP);
            intent.putExtra("type", 3);
            startActivity(intent);
        }
    }

    private void GoDMBZSingleTapePage(String XH){
        Log.w(TAG, "updateMapPage: 1");
        Intent intent = new Intent(singlepoi.this, tapeshow.class);
        intent.putExtra("DMBZ", XH);
        intent.putExtra("type", 1);
        startActivity(intent);
    }

    private void GoSingleTapePage(String XH, int type){
        Log.w(TAG, "updateMapPage: 1");
        if (type == 1) {
            Intent intent = new Intent(singlepoi.this, tapeshow.class);
            intent.putExtra("DMBZ", XH);
            intent.putExtra("type", 1);
            startActivity(intent);
        }else if (type == 2){
            Intent intent = new Intent(singlepoi.this, tapeshow.class);
            intent.putExtra("DML", DML);
            intent.putExtra("type", 2);
            startActivity(intent);
        }else if (type == 3){
            Intent intent = new Intent(singlepoi.this, tapeshow.class);
            intent.putExtra("DMP", DMP);
            intent.putExtra("type", 3);
            startActivity(intent);
        }
    }

    private void RefreshDMBZ(){
        List<DMBZ> dmbzList = LitePal.where("xh = ?", DMXH).find(DMBZ.class);
        TextView txt_XH = (TextView) findViewById(R.id.txt_XH);
        txt_XH.setVisibility(View.VISIBLE);
        TextView txt_DY = (TextView) findViewById(R.id.txt_DY);
        txt_DY.setVisibility(View.VISIBLE);
        TextView txt_MC = (TextView) findViewById(R.id.txt_MC);
        txt_MC.setVisibility(View.VISIBLE);
        TextView txt_BZMC = (TextView) findViewById(R.id.txt_BZMC);
        txt_BZMC.setVisibility(View.VISIBLE);
        TextView txt_XZQMC = (TextView) findViewById(R.id.txt_XZQMC);
        txt_XZQMC.setVisibility(View.VISIBLE);
        TextView txt_XZQDM = (TextView) findViewById(R.id.txt_XZQDM);
        txt_XZQDM.setVisibility(View.VISIBLE);
        TextView txt_SZDW = (TextView) findViewById(R.id.txt_SZDW);
        txt_SZDW.setVisibility(View.VISIBLE);
        TextView txt_SCCJ = (TextView) findViewById(R.id.txt_SCCJ);
        txt_SCCJ.setVisibility(View.VISIBLE);
        TextView txt_GG = (TextView) findViewById(R.id.txt_GG);
        txt_GG.setVisibility(View.VISIBLE);
        edit_XH = (TextView) findViewById(R.id.edit_XH);
        edit_XH.setText(dmbzList.get(0).getXH());
        edit_XH.setVisibility(View.VISIBLE);
        edit_DY = (EditText) findViewById(R.id.edit_DY);
        edit_DY.setText(dmbzList.get(0).getDY());
        edit_DY.setVisibility(View.VISIBLE);
        edit_MC = (EditText) findViewById(R.id.edit_MC);
        edit_MC.setText(dmbzList.get(0).getMC());
        edit_MC.setVisibility(View.VISIBLE);
        edit_BZMC = (EditText) findViewById(R.id.edit_BZMC);
        edit_BZMC.setText(dmbzList.get(0).getBZMC());
        edit_BZMC.setVisibility(View.VISIBLE);
        edit_XZQMC = (EditText) findViewById(R.id.edit_XZQMC);
        edit_XZQMC.setText(dmbzList.get(0).getXZQMC());
        edit_XZQMC.setVisibility(View.VISIBLE);
        edit_XZQDM = (EditText) findViewById(R.id.edit_XZQDM);
        edit_XZQDM.setText(dmbzList.get(0).getXZQDM());
        edit_XZQDM.setVisibility(View.VISIBLE);
        edit_SZDW = (EditText) findViewById(R.id.edit_SZDW);
        edit_SZDW.setText(dmbzList.get(0).getSZDW());
        edit_SZDW.setVisibility(View.VISIBLE);
        edit_SCCJ = (EditText) findViewById(R.id.edit_SCCJ);
        edit_SCCJ.setText(dmbzList.get(0).getSCCJ());
        edit_SCCJ.setVisibility(View.VISIBLE);
        edit_GG = (EditText) findViewById(R.id.edit_GG);
        edit_GG.setText(dmbzList.get(0).getGG());
        edit_GG.setVisibility(View.VISIBLE);
        name = dmbzList.get(0).getBZMC();
        TextView txt_photonum = (TextView) findViewById(R.id.txt_photonumshow);
        //Log.w(TAG, "RefreshDMBZ: " + DataUtil.appearNumber(dmbzList.get(0).getIMGPATH(), ".jpg"));
        if (dmbzList.get(0).getIMGPATH() != null) {
            txt_photonum.setText(String.valueOf(DataUtil.appearNumber(dmbzList.get(0).getIMGPATH(), "\\|") + 1));
            ImageView imageView = (ImageView) findViewById(R.id.photo_image_singlepoi);
            if (DataUtil.appearNumber(dmbzList.get(0).getIMGPATH(), "\\|") + 1 > 0)
                getBitmapDMBZ(dmbzList.get(0).getIMGPATH(), imageView);

        }else txt_photonum.setText(String.valueOf(0));
        txt_photonum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoSinglePhotoPage(DMXH, POITYPE);
            }
        });
        txt_photonum.setVisibility(View.VISIBLE);
        TextView txt_tapenum = (TextView) findViewById(R.id.txt_tapenumshow);
        Log.w(TAG, "RefreshDMBZ: " + dmbzList.get(0).getTAPEPATH());
        if (dmbzList.get(0).getTAPEPATH() != null) txt_tapenum.setText(String.valueOf(DataUtil.appearNumber(dmbzList.get(0).getTAPEPATH(), ".mp3") + DataUtil.appearNumber(dmbzList.get(0).getTAPEPATH(), ".MP3")));
        else txt_tapenum.setText(String.valueOf(0));
        txt_tapenum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoSingleTapePage(DMXH, POITYPE);
            }
        });
        txt_tapenum.setVisibility(View.VISIBLE);
        TextView txt_loc = (TextView) findViewById(R.id.txt_locshow);
        m_lng = dmbzList.get(0).getLng();
        m_lat = dmbzList.get(0).getLat();
        txt_loc.setText(m_lat + ", " + m_lng);
        txt_loc.setVisibility(View.VISIBLE);
        FloatingActionButton fab_saveinfo = (FloatingActionButton) findViewById(R.id.fab_saveinfo1);
        fab_saveinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DMBZ poi = new DMBZ();
                poi.setXH(DMXH);
                poi.setDY(edit_DY.getText().toString());
                poi.setMC(edit_MC.getText().toString());
                poi.setBZMC(edit_BZMC.getText().toString());
                poi.setXZQMC(edit_XZQMC.getText().toString());
                poi.setXZQDM(edit_XZQDM.getText().toString());
                poi.setSZDW(edit_SZDW.getText().toString());
                poi.setSCCJ(edit_SCCJ.getText().toString());
                poi.setGG(edit_GG.getText().toString());
                poi.updateAll("xh = ?", DMXH);
            }
        });
        ImageButton addphoto = (ImageButton)findViewById(R.id.addPhoto_singlepoi);
        addphoto.setVisibility(View.VISIBLE);
        addphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopueWindowForPhoto();
            }
        });
        ImageButton addtape = (ImageButton)findViewById(R.id.addTape_singlepoi);
        addtape.setVisibility(View.VISIBLE);
        addtape.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    requestRecordAudioPermission();
                }catch (ActivityNotFoundException e){
                    Toast.makeText(MyApplication.getContext(), R.string.TakeTapeError, Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    private void RefreshDML(){
        List<DMLine> dmLines = LitePal.where("mapid = ?", DML).find(DMLine.class);
        // TODO : 设计并实现地名线属性编辑页面
        //提示性文字初始化
        TextView txt_XH = (TextView) findViewById(R.id.txt_XH);
        txt_XH.setVisibility(View.VISIBLE);
        TextView txt_qydm = (TextView) findViewById(R.id.txt_qydm);
        txt_qydm.setVisibility(View.VISIBLE);
        TextView txt_lbdm = (TextView) findViewById(R.id.txt_lbdm);
        txt_lbdm.setVisibility(View.VISIBLE);
        TextView txt_bzmc = (TextView) findViewById(R.id.txt_dmbzmc);
        txt_bzmc.setVisibility(View.VISIBLE);
        TextView txt_cym = (TextView) findViewById(R.id.txt_cym);
        txt_cym.setVisibility(View.VISIBLE);
        TextView txt_jc = (TextView) findViewById(R.id.txt_jc);
        txt_jc.setVisibility(View.VISIBLE);
        TextView txt_bm = (TextView) findViewById(R.id.txt_bm);
        txt_bm.setVisibility(View.VISIBLE);
        TextView txt_dfyz = (TextView) findViewById(R.id.txt_dfyz);
        txt_dfyz.setVisibility(View.VISIBLE);
        TextView txt_zt = (TextView) findViewById(R.id.txt_zt);
        txt_zt.setVisibility(View.VISIBLE);
        TextView txt_dmll = (TextView) findViewById(R.id.txt_dmll);
        txt_dmll.setVisibility(View.VISIBLE);
        TextView txt_dmhy = (TextView) findViewById(R.id.txt_dmhy);
        txt_dmhy.setVisibility(View.VISIBLE);
        TextView txt_lsyg = (TextView) findViewById(R.id.txt_lsyg);
        txt_lsyg.setVisibility(View.VISIBLE);
        TextView txt_dlstms = (TextView) findViewById(R.id.txt_dlstms);
        txt_dlstms.setVisibility(View.VISIBLE);
        TextView txt_zlly = (TextView) findViewById(R.id.txt_zlly);
        txt_zlly.setVisibility(View.VISIBLE);
        ////////////////////////
        //输入框体初始化
        edit_XH = (TextView) findViewById(R.id.edit_XH);
        edit_XH.setText(dmLines.get(0).getXh());
        edit_XH.setVisibility(View.VISIBLE);
        edit_qydm = (EditText) findViewById(R.id.edit_qydm);
        edit_qydm.setText(dmLines.get(0).getQydm());
        edit_qydm.setVisibility(View.VISIBLE);
        edit_lbdm = (EditText) findViewById(R.id.edit_lbdm);
        edit_lbdm.setText(dmLines.get(0).getLbdm());
        edit_lbdm.setVisibility(View.VISIBLE);
        edit_bzmc = (EditText) findViewById(R.id.edit_dmbzmc);
        edit_bzmc.setText(dmLines.get(0).getBzmc());
        edit_bzmc.setVisibility(View.VISIBLE);
        edit_cym = (EditText) findViewById(R.id.edit_cym);
        edit_cym.setText(dmLines.get(0).getCym());
        edit_cym.setVisibility(View.VISIBLE);
        edit_jc = (EditText) findViewById(R.id.edit_jc);
        edit_jc.setText(dmLines.get(0).getJc());
        edit_jc.setVisibility(View.VISIBLE);
        edit_bm = (EditText) findViewById(R.id.edit_bm);
        edit_bm.setText(dmLines.get(0).getBm());
        edit_bm.setVisibility(View.VISIBLE);
        edit_dfyz = (EditText) findViewById(R.id.edit_dfyz);
        edit_dfyz.setText(dmLines.get(0).getDfyz());
        edit_dfyz.setVisibility(View.VISIBLE);
        edit_zt = (EditText) findViewById(R.id.edit_zt);
        edit_zt.setText(dmLines.get(0).getZt());
        edit_zt.setVisibility(View.VISIBLE);

        edit_dmll = (EditText) findViewById(R.id.edit_dmll);
        edit_dmll.setText(dmLines.get(0).getDmll());
        edit_dmll.setVisibility(View.VISIBLE);

        edit_dmhy = (EditText) findViewById(R.id.edit_dmhy);
        edit_dmhy.setText(dmLines.get(0).getDmhy());
        edit_dmhy.setVisibility(View.VISIBLE);

        edit_lsyg = (EditText) findViewById(R.id.edit_lsyg);
        edit_lsyg.setText(dmLines.get(0).getLsyg());
        edit_lsyg.setVisibility(View.VISIBLE);

        edit_dlstms = (EditText) findViewById(R.id.edit_dlstms);
        edit_dlstms.setText(dmLines.get(0).getDlstms());
        edit_dlstms.setVisibility(View.VISIBLE);

        edit_zlly = (EditText) findViewById(R.id.edit_zlly);
        edit_zlly.setText(dmLines.get(0).getZlly());
        edit_zlly.setVisibility(View.VISIBLE);
        name = dmLines.get(0).getBzmc();
        TextView txt_photonum = (TextView) findViewById(R.id.txt_photonumshow);
        //Log.w(TAG, "RefreshDMBZ: " + DataUtil.appearNumber(dmLines.get(0).getIMGPATH(), ".jpg"));
        if (dmLines.get(0).getImgpath() != null) {
            txt_photonum.setText(String.valueOf(DataUtil.appearNumber(dmLines.get(0).getImgpath(), "\\|") + 1));
            ImageView imageView = (ImageView) findViewById(R.id.photo_image_singlepoi);
            if (DataUtil.appearNumber(dmLines.get(0).getImgpath(), "\\|")  + 1 > 0)
                getBitmapDM(dmLines.get(0).getImgpath(), imageView);

        }else txt_photonum.setText(String.valueOf(0));
        txt_photonum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoSinglePhotoPage(DML, POITYPE);
            }
        });
        txt_photonum.setVisibility(View.VISIBLE);
        TextView txt_tapenum = (TextView) findViewById(R.id.txt_tapenumshow);
        Log.w(TAG, "RefreshDMBZ: " + dmLines.get(0).getTapepath());
        if (dmLines.get(0).getTapepath() != null) txt_tapenum.setText(String.valueOf(DataUtil.appearNumber(dmLines.get(0).getTapepath(), ".mp3") + DataUtil.appearNumber(dmLines.get(0).getTapepath(), ".MP3")));
        else txt_tapenum.setText(String.valueOf(0));
        txt_tapenum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoSingleTapePage(DML, POITYPE);
            }
        });
        txt_tapenum.setVisibility(View.VISIBLE);
        TextView txt_loc = (TextView) findViewById(R.id.txt_locshow);
        m_lng = (dmLines.get(0).getMaxlng() + dmLines.get(0).getMinlng()) / 2;
        m_lat = (dmLines.get(0).getMaxlat() + dmLines.get(0).getMinlat()) / 2;
        txt_loc.setText(m_lat + ", " + m_lng);
        txt_loc.setVisibility(View.VISIBLE);
        FloatingActionButton fab_saveinfo = (FloatingActionButton) findViewById(R.id.fab_saveinfo1);
        fab_saveinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DMLine dmLine = new DMLine();
                dmLine.setQydm(edit_qydm.getText().toString());
                dmLine.setLbdm(edit_lbdm.getText().toString());
                dmLine.setBzmc(edit_bzmc.getText().toString());
                dmLine.setCym(edit_cym.getText().toString());
                dmLine.setJc(edit_jc.getText().toString());
                dmLine.setBm(edit_bm.getText().toString());
                dmLine.setDfyz(edit_dfyz.getText().toString());
                dmLine.setZt(edit_zt.getText().toString());
                dmLine.setDmll(edit_dmll.getText().toString());
                dmLine.setDmhy(edit_dmhy.getText().toString());
                dmLine.setLsyg(edit_lsyg.getText().toString());
                dmLine.setDlstms(edit_dlstms.getText().toString());
                dmLine.setZlly(edit_zlly.getText().toString());
                dmLine.updateAll("mapid = ?", DML);
            }
        });
        ImageButton addphoto = (ImageButton)findViewById(R.id.addPhoto_singlepoi);
        addphoto.setVisibility(View.VISIBLE);
        addphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopueWindowForPhoto();
            }
        });
        ImageButton addtape = (ImageButton)findViewById(R.id.addTape_singlepoi);
        addtape.setVisibility(View.VISIBLE);
        addtape.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
                    startActivityForResult(intent, REQUEST_CODE_TAPE);
                }catch (ActivityNotFoundException e){
                    Toast.makeText(MyApplication.getContext(), R.string.TakeTapeError, Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    private void RefreshDMP(){
        List<DMPoint> dmPoints = LitePal.where("mapid = ?", DMP).find(DMPoint.class);
        // TODO : 设计并实现地名点属性编辑页面
        //提示性文字初始化
        TextView txt_XH = (TextView) findViewById(R.id.txt_XH);
        txt_XH.setVisibility(View.VISIBLE);
        TextView txt_qydm = (TextView) findViewById(R.id.txt_qydm);
        txt_qydm.setVisibility(View.VISIBLE);
        TextView txt_lbdm = (TextView) findViewById(R.id.txt_lbdm);
        txt_lbdm.setVisibility(View.VISIBLE);
        TextView txt_bzmc = (TextView) findViewById(R.id.txt_dmbzmc);
        txt_bzmc.setVisibility(View.VISIBLE);
        TextView txt_cym = (TextView) findViewById(R.id.txt_cym);
        txt_cym.setVisibility(View.VISIBLE);
        TextView txt_jc = (TextView) findViewById(R.id.txt_jc);
        txt_jc.setVisibility(View.VISIBLE);
        TextView txt_bm = (TextView) findViewById(R.id.txt_bm);
        txt_bm.setVisibility(View.VISIBLE);
        TextView txt_dfyz = (TextView) findViewById(R.id.txt_dfyz);
        txt_dfyz.setVisibility(View.VISIBLE);
        TextView txt_zt = (TextView) findViewById(R.id.txt_zt);
        txt_zt.setVisibility(View.VISIBLE);
        TextView txt_dmll = (TextView) findViewById(R.id.txt_dmll);
        txt_dmll.setVisibility(View.VISIBLE);
        TextView txt_dmhy = (TextView) findViewById(R.id.txt_dmhy);
        txt_dmhy.setVisibility(View.VISIBLE);
        TextView txt_lsyg = (TextView) findViewById(R.id.txt_lsyg);
        txt_lsyg.setVisibility(View.VISIBLE);
        TextView txt_dlstms = (TextView) findViewById(R.id.txt_dlstms);
        txt_dlstms.setVisibility(View.VISIBLE);
        TextView txt_zlly = (TextView) findViewById(R.id.txt_zlly);
        txt_zlly.setVisibility(View.VISIBLE);
        ////////////////////////
        //输入框体初始化
        edit_XH = (TextView) findViewById(R.id.edit_XH);
        edit_XH.setText(dmPoints.get(0).getXh());
        edit_XH.setVisibility(View.VISIBLE);
        edit_qydm = (EditText) findViewById(R.id.edit_qydm);
        edit_qydm.setText(dmPoints.get(0).getQydm());
        edit_qydm.setVisibility(View.VISIBLE);
        edit_lbdm = (EditText) findViewById(R.id.edit_lbdm);
        edit_lbdm.setText(dmPoints.get(0).getLbdm());
        edit_lbdm.setVisibility(View.VISIBLE);
        edit_bzmc = (EditText) findViewById(R.id.edit_dmbzmc);
        edit_bzmc.setText(dmPoints.get(0).getBzmc());
        edit_bzmc.setVisibility(View.VISIBLE);
        edit_cym = (EditText) findViewById(R.id.edit_cym);
        edit_cym.setText(dmPoints.get(0).getCym());
        edit_cym.setVisibility(View.VISIBLE);
        edit_jc = (EditText) findViewById(R.id.edit_jc);
        edit_jc.setText(dmPoints.get(0).getJc());
        edit_jc.setVisibility(View.VISIBLE);
        edit_bm = (EditText) findViewById(R.id.edit_bm);
        edit_bm.setText(dmPoints.get(0).getBm());
        edit_bm.setVisibility(View.VISIBLE);
        edit_dfyz = (EditText) findViewById(R.id.edit_dfyz);
        edit_dfyz.setText(dmPoints.get(0).getDfyz());
        edit_dfyz.setVisibility(View.VISIBLE);
        edit_zt = (EditText) findViewById(R.id.edit_zt);
        edit_zt.setText(dmPoints.get(0).getZt());
        edit_zt.setVisibility(View.VISIBLE);

        edit_dmll = (EditText) findViewById(R.id.edit_dmll);
        edit_dmll.setText(dmPoints.get(0).getDmll());
        edit_dmll.setVisibility(View.VISIBLE);

        edit_dmhy = (EditText) findViewById(R.id.edit_dmhy);
        edit_dmhy.setText(dmPoints.get(0).getDmhy());
        edit_dmhy.setVisibility(View.VISIBLE);

        edit_lsyg = (EditText) findViewById(R.id.edit_lsyg);
        edit_lsyg.setText(dmPoints.get(0).getLsyg());
        edit_lsyg.setVisibility(View.VISIBLE);

        edit_dlstms = (EditText) findViewById(R.id.edit_dlstms);
        edit_dlstms.setText(dmPoints.get(0).getDlstms());
        edit_dlstms.setVisibility(View.VISIBLE);

        edit_zlly = (EditText) findViewById(R.id.edit_zlly);
        edit_zlly.setText(dmPoints.get(0).getZlly());
        edit_zlly.setVisibility(View.VISIBLE);
        name = dmPoints.get(0).getBzmc();
        TextView txt_photonum = (TextView) findViewById(R.id.txt_photonumshow);
        //Log.w(TAG, "RefreshDMBZ: " + DataUtil.appearNumber(dmPoints.get(0).getIMGPATH(), ".jpg"));
        if (dmPoints.get(0).getImgpath() != null) {
            txt_photonum.setText(String.valueOf(DataUtil.appearNumber(dmPoints.get(0).getImgpath(), "\\|") + 1));
            ImageView imageView = (ImageView) findViewById(R.id.photo_image_singlepoi);
            if (DataUtil.appearNumber(dmPoints.get(0).getImgpath(), "\\|") + 1 > 0)
                getBitmapDM(dmPoints.get(0).getImgpath(), imageView);
        }else txt_photonum.setText(String.valueOf(0));
        txt_photonum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoSinglePhotoPage(DMP, POITYPE);
            }
        });
        txt_photonum.setVisibility(View.VISIBLE);
        TextView txt_tapenum = (TextView) findViewById(R.id.txt_tapenumshow);
        Log.w(TAG, "RefreshDMBZ: " + dmPoints.get(0).getTapepath());
        if (dmPoints.get(0).getTapepath() != null) txt_tapenum.setText(String.valueOf(DataUtil.appearNumber(dmPoints.get(0).getTapepath(), ".mp3") + DataUtil.appearNumber(dmPoints.get(0).getTapepath(), ".MP3")));
        else txt_tapenum.setText(String.valueOf(0));
        txt_tapenum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoSingleTapePage(DMP, POITYPE);
            }
        });
        txt_tapenum.setVisibility(View.VISIBLE);
        TextView txt_loc = (TextView) findViewById(R.id.txt_locshow);
        m_lng = dmPoints.get(0).getLng();
        m_lat = dmPoints.get(0).getLat();
        txt_loc.setText(m_lat + ", " + m_lng);
        txt_loc.setVisibility(View.VISIBLE);
        FloatingActionButton fab_saveinfo = (FloatingActionButton) findViewById(R.id.fab_saveinfo1);
        fab_saveinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DMPoint dmPoint = new DMPoint();
                dmPoint.setQydm(edit_qydm.getText().toString());
                dmPoint.setLbdm(edit_lbdm.getText().toString());
                dmPoint.setBzmc(edit_bzmc.getText().toString());
                dmPoint.setCym(edit_cym.getText().toString());
                dmPoint.setJc(edit_jc.getText().toString());
                dmPoint.setBm(edit_bm.getText().toString());
                dmPoint.setDfyz(edit_dfyz.getText().toString());
                dmPoint.setZt(edit_zt.getText().toString());
                dmPoint.setDmll(edit_dmll.getText().toString());
                dmPoint.setDmhy(edit_dmhy.getText().toString());
                dmPoint.setLsyg(edit_lsyg.getText().toString());
                dmPoint.setDlstms(edit_dlstms.getText().toString());
                dmPoint.setZlly(edit_zlly.getText().toString());
                dmPoint.updateAll("mapid = ?", DMP);
            }
        });
        ImageButton addphoto = (ImageButton)findViewById(R.id.addPhoto_singlepoi);
        addphoto.setVisibility(View.VISIBLE);
        addphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopueWindowForPhoto();
            }
        });
        ImageButton addtape = (ImageButton)findViewById(R.id.addTape_singlepoi);
        addtape.setVisibility(View.VISIBLE);
        addtape.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
                    startActivityForResult(intent, REQUEST_CODE_TAPE);
                }catch (ActivityNotFoundException e){
                    Toast.makeText(MyApplication.getContext(), R.string.TakeTapeError, Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (POITYPE == 0) refresh();
        else if (POITYPE == 1) RefreshDMBZ();
        else if (POITYPE == 2) RefreshDML();
        else if (POITYPE == 3) RefreshDMP();
    }

    int showNum = 0;
    List<bt> bms;
    PointF pt0 = new PointF();
    boolean ges = false;
    TextView textView_photonum;
    String str = "";

    private void refresh(){
        TextView txt_name = (TextView) findViewById(R.id.txt_name);
        txt_name.setVisibility(View.VISIBLE);
        TextView txt_time = (TextView) findViewById(R.id.txt_time);
        txt_time.setVisibility(View.VISIBLE);
        TextView txt_des = (TextView) findViewById(R.id.txt_des);
        txt_des.setVisibility(View.VISIBLE);
        TextView txt_type = (TextView) findViewById(R.id.txt_type);
        txt_type.setVisibility(View.VISIBLE);
        TextView txt_photonum = (TextView) findViewById(R.id.txt_photonum);
        txt_photonum.setVisibility(View.VISIBLE);
        TextView txt_tapenum = (TextView) findViewById(R.id.txt_tapenum);
        txt_tapenum.setVisibility(View.VISIBLE);
        TextView txt_loc = (TextView) findViewById(R.id.txt_loc);
        txt_loc.setVisibility(View.VISIBLE);

        List<POI> pois = LitePal.where("poic = ?", POIC).find(POI.class);
        List<MTAPE> tapes = LitePal.where("poic = ?", POIC).find(MTAPE.class);
        final List<MPHOTO> photos = LitePal.where("poic = ?", POIC).find(MPHOTO.class);

        //
        String[] strings = getResources().getStringArray(R.array.Type);
        for (int i = 0; i < strings.length; i++) {
            Log.w(TAG, "refresh: " + strings[i]);
            if (strings[i].equals(pois.get(0).getType())) type_spinner.setSelection(i);
        }
        //

        type_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                str = type_spinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                str = type_spinner.getSelectedItem().toString();
            }
        });
        getBitmap(photos);
        Log.w(TAG, "pois: " + pois.size() + "\n");
        Log.w(TAG, "tapes1: " + pois.get(0).getTapenum() + "\n");
        Log.w(TAG, "photos1: " + pois.get(0).getPhotonum() + "\n");
        Log.w(TAG, "tapes: " + tapes.size() + "\n");
        Log.w(TAG, "photos: " + photos.size());
        POI poi1 = new POI();
        if (tapes.size() != 0) poi1.setTapenum(tapes.size());
        else poi1.setToDefault("tapenum");
        if (photos.size() != 0) {
            poi1.setPhotonum(photos.size());
            final ImageView imageView = (ImageView) findViewById(R.id.photo_image_singlepoi);
            /*imageView.setVisibility(View.VISIBLE);
            String path = photos.get(0).getPath();
            File file = new File(path);
            try {
                if (file.exists()) {
                    Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
                    int degree = DataUtil.getPicRotate(path);
                    if (degree != 0) {
                        Matrix m = new Matrix();
                        m.setRotate(degree); // 旋转angle度
                        Log.w(TAG, "showPopueWindowForPhoto: " + degree);
                        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
                    }
                    imageView.setImageBitmap(bitmap);
                }else {
                    Drawable drawable = MyApplication.getContext().getResources().getDrawable(R.drawable.imgerror);
                    BitmapDrawable bd = (BitmapDrawable) drawable;
                    Bitmap bitmap = Bitmap.createBitmap(bd.getBitmap(), 0, 0, bd.getBitmap().getWidth(), bd.getBitmap().getHeight());
                    bitmap = ThumbnailUtils.extractThumbnail(bitmap, 80, 120,
                            ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
                    imageView.setImageBitmap(bitmap);
                }
            }catch (IOException e){
                Log.w(TAG, e.toString());
            }*/
            if (photos.size() >= 1) {
                imageView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        float distanceX = 0;
                        float distanceY = 0;
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                //第一个手指按下
                                pt0.set(event.getX(0), event.getY(0));
                                Log.w(TAG, "onTouchdown: " + event.getX());
                                Log.w(TAG, "手指id: " + event.getActionIndex());
                                Log.w(TAG, "ACTION_POINTER_DOWN");
                                Log.w(TAG, "同时按下的手指数量: " + event.getPointerCount());
                                break;
                            case MotionEvent.ACTION_POINTER_DOWN:
                                //第二个手指按下
                                Log.w(TAG, "手指id: " + event.getActionIndex());
                                Log.w(TAG, "onTouchdown: " + event.getX());
                                Log.w(TAG, "ACTION_POINTER_DOWN");
                                Log.w(TAG, "同时按下的手指数量: " + event.getPointerCount());
                                break;
                            case MotionEvent.ACTION_UP:
                                //最后一个手指抬起
                                ges = false;
                                Log.w(TAG, "onTouchup: " + event.getX());
                                Log.w(TAG, "getPointerId: " + event.getPointerId(0));
                                distanceX = event.getX(0) - pt0.x;
                                distanceY = event.getY(0) - pt0.y;
                                Log.w(TAG, "onTouch: " + distanceX);
                                if (Math.abs(distanceX) > Math.abs(distanceY) & Math.abs(distanceX) > 200 & Math.abs(distanceY) < 100) {
                                    if (distanceX > 0) {
                                        Log.w(TAG, "bms.size : " + bms.size());
                                        showNum++;
                                        if (showNum > bms.size() - 1) {
                                            showNum = 0;
                                            imageView.setImageBitmap(bms.get(0).getM_bm());
                                        } else {
                                            imageView.setImageBitmap(bms.get(showNum).getM_bm());
                                        }
                                    } else {
                                        showNum--;
                                        if (showNum < 0) {
                                            showNum = bms.size() - 1;
                                            imageView.setImageBitmap(bms.get(showNum).getM_bm());
                                        } else {
                                            imageView.setImageBitmap(bms.get(showNum).getM_bm());
                                        }
                                    }
                                    Log.w(TAG, "同时抬起的手指数量: " + event.getPointerCount());
                                    Log.w(TAG, "手指id: " + event.getActionIndex());
                                }
                                break;
                            case MotionEvent.ACTION_MOVE:
                                if (event.getPointerCount() == 3) {
                                    Log.w(TAG, "3指滑动");

                                }
                                else if (event.getPointerCount() == 4) {
                                    if (!ges) {
                                        Log.w(TAG, "4指滑动");
                                        AlertDialog.Builder dialog = new AlertDialog.Builder(singlepoi.this);
                                        dialog.setTitle("提示");
                                        dialog.setMessage("确认删除图片吗?");
                                        dialog.setCancelable(false);
                                        dialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                List<POI> pois1 = LitePal.where("poic = ?", POIC).find(POI.class);
                                                if (pois1.get(0).getPhotonum() > 0) {
                                                    textView_photonum.setText(Integer.toString(pois1.get(0).getPhotonum() - 1));
                                                    POI poi = new POI();
                                                    poi.setPhotonum(pois1.get(0).getPhotonum() - 1);
                                                    poi.updateAll("poic = ?", POIC);
                                                    LitePal.deleteAll(MPHOTO.class, "poic = ? and path = ?", POIC, bms.get(showNum).getM_path());
                                                    bms.remove(showNum);
                                                    if (showNum > pois1.get(0).getPhotonum() - 1) {
                                                        if (bms.size() > 0) imageView.setImageBitmap(bms.get(0).getM_bm());
                                                        else imageView.setVisibility(View.GONE);
                                                    }
                                                    else if (showNum < pois1.get(0).getPhotonum() - 1) imageView.setImageBitmap(bms.get(showNum).getM_bm());
                                                    else imageView.setVisibility(View.GONE);
                                                    Toast.makeText(singlepoi.this, "已经删除图片", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                            }
                                        });
                                        dialog.show();
                                        ges = true;
                                    }
                                }
                                else if (event.getPointerCount() == 5) {
                                    Log.w(TAG, "5指滑动");
                                }
                                else if (event.getPointerCount() == 2) {
                                    Log.w(TAG, "2指滑动");
                                }
                                break;

                        }
                        return true;
                    }
                });
            }
        }
        else poi1.setToDefault("photonum");
        poi1.updateAll("poic = ?", POIC);
        Log.w(TAG, "refresh: " + poi1.updateAll("poic = ?", POIC));
        /*POI poi = new POI();
        poi.setPhotonum(photos.size());
        poi.setTapenum(tapes.size());
        poi.updateAll("POIC = ?", POIC);*/
        List<POI> pois1 = LitePal.where("poic = ?", POIC).find(POI.class);
        Log.w(TAG, "tapes2: " + pois1.get(0).getTapenum() + "\n");
        Log.w(TAG, "photos2: " + pois1.get(0).getPhotonum() + "\n");
        POI poi = pois.get(0);
        name = poi.getName();
        editText_name = (EditText) findViewById(R.id.edit_name);
        editText_name.setVisibility(View.VISIBLE);
        editText_name.setText(name);
        editText_des = (EditText) findViewById(R.id.edit_des);
        editText_des.setVisibility(View.VISIBLE);
        if (poi.getDescription() != null) {
            editText_des.setText(poi.getDescription());
        }else editText_des.setText("");
        TextView textView_time = (TextView) findViewById(R.id.txt_timeshow);
        textView_time.setVisibility(View.VISIBLE);
        textView_time.setText(poi.getTime());
        Log.w(TAG, Integer.toString(tapes.size()));
        textView_photonum.setText(Integer.toString(photos.size()));
        textView_photonum.setVisibility(View.VISIBLE);
        textView_photonum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //打开图片列表
                Intent intent1 = new Intent(singlepoi.this, photoshow.class);
                intent1.putExtra("POIC", POIC);
                intent1.putExtra("type", 0);
                startActivity(intent1);
            }
        });
        TextView textView_tapenum = (TextView) findViewById(R.id.txt_tapenumshow);
        textView_tapenum.setVisibility(View.VISIBLE);
        textView_tapenum.setText(Integer.toString(tapes.size()));
        textView_tapenum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //打开录音列表
                Intent intent2 = new Intent(singlepoi.this, tapeshow.class);
                intent2.putExtra("POIC", POIC);
                intent2.putExtra("type", 0);
                startActivity(intent2);
            }
        });
        TextView textView_loc = (TextView) findViewById(R.id.txt_locshow);
        textView_loc.setVisibility(View.VISIBLE);
        DecimalFormat df = new DecimalFormat("0.0000");
        m_lat = poi.getX();
        m_lng = poi.getY();
        textView_loc.setText(df.format(poi.getX()) + ", " + df.format(poi.getY()));
        ImageButton addphoto = (ImageButton)findViewById(R.id.addPhoto_singlepoi);
        addphoto.setVisibility(View.VISIBLE);
        addphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopueWindowForPhoto();
            }
        });
        ImageButton addtape = (ImageButton)findViewById(R.id.addTape_singlepoi);
        addtape.setVisibility(View.VISIBLE);
        addtape.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
                    startActivityForResult(intent, REQUEST_CODE_TAPE);
                }catch (ActivityNotFoundException e){
                    Toast.makeText(MyApplication.getContext(), R.string.TakeTapeError, Toast.LENGTH_LONG).show();
                }

            }
        });
        FloatingActionButton fab_saveinfo = (FloatingActionButton) findViewById(R.id.fab_saveinfo1);
        fab_saveinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                POI poi = new POI();
                poi.setName(editText_name.getText().toString());
                name = editText_name.getText().toString();
                poi.setDescription(editText_des.getText().toString());
                poi.setType(str);
                poi.updateAll("poic = ?", POIC);
                Toast.makeText(singlepoi.this, R.string.SaveInfo, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void DMBZGesture(final ImageView imageView){
        Log.w(TAG, "4指滑动");
        AlertDialog.Builder dialog = new AlertDialog.Builder(singlepoi.this);
        dialog.setTitle("提示");
        dialog.setMessage("确认删除图片吗?");
        dialog.setCancelable(false);
        dialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.w(TAG, "onClick: " + bms.size());
                List<DMBZ> pois1 = LitePal.where("xh = ?", DMXH).find(DMBZ.class);
                if (DataUtil.appearNumber(pois1.get(0).getIMGPATH(), "\\|") + 1 > 0) {
                    textView_photonum.setText(Integer.toString(DataUtil.appearNumber(pois1.get(0).getIMGPATH(), "\\|")));
                    DMBZ poi = new DMBZ();
                    if (DataUtil.appearNumber(pois1.get(0).getIMGPATH(), "\\|") + 1 > 1){
                        if (pois1.get(0).getIMGPATH().indexOf(bms.get(showNum).getM_path()) != 0)
                            poi.setIMGPATH(pois1.get(0).getIMGPATH().replace("|" + bms.get(showNum).getM_path(), ""));
                        else
                            poi.setIMGPATH(pois1.get(0).getIMGPATH().replace(bms.get(showNum).getM_path() + "|", ""));
                    }else {
                        poi.setIMGPATH("");
                    }
                    poi.updateAll("xh = ?", DMXH);
                    bms.remove(showNum);
                    Log.w(TAG, "onClick: " + bms.size());
                    if (showNum > DataUtil.appearNumber(pois1.get(0).getIMGPATH(), "\\|")) {
                        if (bms.size() > 0) imageView.setImageBitmap(bms.get(0).getM_bm());
                        else imageView.setVisibility(View.GONE);
                    }
                    else if (showNum < DataUtil.appearNumber(pois1.get(0).getIMGPATH(), "\\|")) imageView.setImageBitmap(bms.get(showNum).getM_bm());
                    else {
                        if (bms.size() > 0) imageView.setImageBitmap(bms.get(showNum - 1).getM_bm());
                        else imageView.setVisibility(View.GONE);
                    }
                    Toast.makeText(singlepoi.this, "已经删除图片", Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        dialog.show();
    }

    private void DMGesture(final ImageView imageView, final int type){
        Log.w(TAG, "4指滑动");
        AlertDialog.Builder dialog = new AlertDialog.Builder(singlepoi.this);
        dialog.setTitle("提示");
        dialog.setMessage("确认删除图片吗?");
        dialog.setCancelable(false);
        dialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.w(TAG, "onClick: " + bms.size());
                if (type == 2) {
                    List<DMLine> pois1 = LitePal.where("xh = ?", DML).find(DMLine.class);
                    if (DataUtil.appearNumber(pois1.get(0).getImgpath(), "\\|") + 1 > 0) {
                        textView_photonum.setText(Integer.toString(DataUtil.appearNumber(pois1.get(0).getImgpath(), "\\|")));
                        DMLine dmLine = new DMLine();
                        if (DataUtil.appearNumber(pois1.get(0).getImgpath(), "\\|") + 1 > 1) {
                            if (pois1.get(0).getImgpath().indexOf(bms.get(showNum).getM_path()) != 0)
                                dmLine.setImgpath(pois1.get(0).getImgpath().replace("|" + bms.get(showNum).getM_path(), ""));
                            else
                                dmLine.setImgpath(pois1.get(0).getImgpath().replace(bms.get(showNum).getM_path() + "|", ""));
                        } else {
                            dmLine.setImgpath("");
                        }
                        dmLine.updateAll("xh = ?", DMXH);
                        bms.remove(showNum);
                        Log.w(TAG, "onClick: " + bms.size());
                        if (showNum > DataUtil.appearNumber(pois1.get(0).getImgpath(), "\\|")) {
                            if (bms.size() > 0) imageView.setImageBitmap(bms.get(0).getM_bm());
                            else imageView.setVisibility(View.GONE);
                        } else if (showNum < DataUtil.appearNumber(pois1.get(0).getImgpath(), "\\|"))
                            imageView.setImageBitmap(bms.get(showNum).getM_bm());
                        else {
                            if (bms.size() > 0)
                                imageView.setImageBitmap(bms.get(showNum - 1).getM_bm());
                            else imageView.setVisibility(View.GONE);
                        }
                        Toast.makeText(singlepoi.this, "已经删除图片", Toast.LENGTH_SHORT).show();
                    }
                }else if (type == 3){
                    List<DMPoint> pois1 = LitePal.where("xh = ?", DMP).find(DMPoint.class);
                    if (DataUtil.appearNumber(pois1.get(0).getImgpath(), "\\|") + 1 > 0) {
                        textView_photonum.setText(Integer.toString(DataUtil.appearNumber(pois1.get(0).getImgpath(), "\\|")));
                        DMPoint dmPoint = new DMPoint();
                        if (DataUtil.appearNumber(pois1.get(0).getImgpath(), "\\|") + 1 > 1) {
                            if (pois1.get(0).getImgpath().indexOf(bms.get(showNum).getM_path()) != 0)
                                dmPoint.setImgpath(pois1.get(0).getImgpath().replace("|" + bms.get(showNum).getM_path(), ""));
                            else
                                dmPoint.setImgpath(pois1.get(0).getImgpath().replace(bms.get(showNum).getM_path() + "|", ""));
                        } else {
                            dmPoint.setImgpath("");
                        }
                        dmPoint.updateAll("xh = ?", DMXH);
                        bms.remove(showNum);
                        Log.w(TAG, "onClick: " + bms.size());
                        if (showNum > DataUtil.appearNumber(pois1.get(0).getImgpath(), "\\|")) {
                            if (bms.size() > 0) imageView.setImageBitmap(bms.get(0).getM_bm());
                            else imageView.setVisibility(View.GONE);
                        } else if (showNum < DataUtil.appearNumber(pois1.get(0).getImgpath(), "\\|"))
                            imageView.setImageBitmap(bms.get(showNum).getM_bm());
                        else {
                            if (bms.size() > 0)
                                imageView.setImageBitmap(bms.get(showNum - 1).getM_bm());
                            else imageView.setVisibility(View.GONE);
                        }
                        Toast.makeText(singlepoi.this, "已经删除图片", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        dialog.show();
    }

    private void getBitmap(final List<MPHOTO> photos){
        new Thread(new Runnable() {
            @Override
            public void run() {
                //Log.w(TAG, "run: photo.size" + photos.size());
                bms = new ArrayList<>();
                for (int i = 0; i < photos.size(); i++) {
                    String path = photos.get(i).getPath();
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
                            bms.add(new bt(bitmap,  path));
                        } catch (IOException e) {
                            Bitmap bitmap = Bitmap.createBitmap(80, 120, Bitmap.Config.ALPHA_8);
                            bms.add(new bt(bitmap, ""));
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    final ImageView imageView = (ImageView) findViewById(R.id.photo_image_singlepoi);
                                    imageView.setVisibility(View.VISIBLE);
                                    imageView.setImageBitmap(bms.get(0).getM_bm());
                                }
                            });
                        }
                    } else {
                        Bitmap bitmap = Bitmap.createBitmap(80, 120, Bitmap.Config.ALPHA_8);
                        bms.add(new bt(bitmap, ""));
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final ImageView imageView = (ImageView) findViewById(R.id.photo_image_singlepoi);
                            imageView.setVisibility(View.VISIBLE);
                            imageView.setImageBitmap(bms.get(0).getM_bm());
                        }
                    });
                }

                Log.w(TAG, "getBitmap: " + bms.size());
            }
        }).start();
    }

    private void getBitmapDMBZ(final String ImgPath, final ImageView imageView){
        new Thread(new Runnable() {
            @Override
            public void run() {
                //Log.w(TAG, "run: photo.size" + photos.size());
                bms = new ArrayList<>();
                String ImgPathTemp = ImgPath;
                String[] imgPath = new String[DataUtil.appearNumber(ImgPath, "\\|") + 1];
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
                    if (!imgPath[kk].contains(Environment.getExternalStorageDirectory().toString())){
                        path = rootpath + imgPath[kk];
                    }else {
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
                            bms.add(new bt(bitmap, imgPath[kk]));
                        } catch (IOException e) {
                            Bitmap bitmap = Bitmap.createBitmap(80, 120, Bitmap.Config.ALPHA_8);
                            bms.add(new bt(bitmap, ""));
                        }
                    } else {
                        Bitmap bitmap = Bitmap.createBitmap(80, 120, Bitmap.Config.ALPHA_8);
                        bms.add(new bt(bitmap, ""));
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (bms.size() > 0 & !bms.get(0).getM_path().isEmpty()) {
                            imageView.setImageBitmap(bms.get(0).getM_bm());
                            imageView.setVisibility(View.VISIBLE);
                            imageView.setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View v, MotionEvent event) {
                                    float distanceX = 0;
                                    float distanceY = 0;
                                    switch (event.getAction()) {
                                        case MotionEvent.ACTION_DOWN:
                                            //第一个手指按下
                                            pt0.set(event.getX(0), event.getY(0));
                                            Log.w(TAG, "onTouchdown: " + event.getX());
                                            Log.w(TAG, "手指id: " + event.getActionIndex());
                                            Log.w(TAG, "ACTION_POINTER_DOWN");
                                            Log.w(TAG, "同时按下的手指数量: " + event.getPointerCount());
                                            break;
                                        case MotionEvent.ACTION_POINTER_DOWN:
                                            //第二个手指按下
                                            Log.w(TAG, "手指id: " + event.getActionIndex());
                                            Log.w(TAG, "onTouchdown: " + event.getX());
                                            Log.w(TAG, "ACTION_POINTER_DOWN");
                                            Log.w(TAG, "同时按下的手指数量: " + event.getPointerCount());
                                            break;
                                        case MotionEvent.ACTION_UP:
                                            //最后一个手指抬起
                                            ges = false;
                                            Log.w(TAG, "onTouchup: " + event.getX());
                                            Log.w(TAG, "getPointerId: " + event.getPointerId(0));
                                            distanceX = event.getX(0) - pt0.x;
                                            distanceY = event.getY(0) - pt0.y;
                                            Log.w(TAG, "onTouch: " + distanceX);
                                            if (Math.abs(distanceX) > Math.abs(distanceY) & Math.abs(distanceX) > 200 & Math.abs(distanceY) < 100) {
                                                if (distanceX > 0) {
                                                    Log.w(TAG, "bms.size : " + bms.size());
                                                    showNum++;
                                                    if (showNum > bms.size() - 1) {
                                                        showNum = 0;
                                                        imageView.setImageBitmap(bms.get(0).getM_bm());
                                                    } else {
                                                        imageView.setImageBitmap(bms.get(showNum).getM_bm());
                                                    }
                                                } else {
                                                    showNum--;
                                                    if (showNum < 0) {
                                                        showNum = bms.size() - 1;
                                                        imageView.setImageBitmap(bms.get(showNum).getM_bm());
                                                    } else {
                                                        imageView.setImageBitmap(bms.get(showNum).getM_bm());
                                                    }
                                                }
                                                Log.w(TAG, "同时抬起的手指数量: " + event.getPointerCount());
                                                Log.w(TAG, "手指id: " + event.getActionIndex());
                                            }
                                            break;
                                        case MotionEvent.ACTION_MOVE:
                                            if (event.getPointerCount() == 3) {
                                                Log.w(TAG, "3指滑动");

                                            } else if (event.getPointerCount() == 4) {
                                                if (!ges) {
                                                    DMBZGesture(imageView);
                                                    ges = true;
                                                }
                                            } else if (event.getPointerCount() == 5) {
                                                Log.w(TAG, "5指滑动");
                                            } else if (event.getPointerCount() == 2) {
                                                Log.w(TAG, "2指滑动");
                                                Log.w(TAG, "onClick: ");
                                                List<DMBZ> dmbzList = LitePal.where("xh = ?", DMXH).find(DMBZ.class);
                                                String tapePath = dmbzList.get(0).getTAPEPATH();
                                                if (tapePath != null) {
                                                    if (!tapePath.isEmpty()) {
                                                        int num = DataUtil.appearNumber(tapePath, ".mp3") + DataUtil.appearNumber(tapePath, ".MP3");
                                                        if (num > 1) {
                                                            tapePath = tapePath.substring(0, tapePath.indexOf(".mp3") + 4);
                                                            File file = null;
                                                            if (tapePath.contains(Environment.getExternalStorageDirectory().toString())) {
                                                                file = new File(tapePath);
                                                            } else {
                                                                tapePath = Environment.getExternalStorageDirectory().toString() + "/地名标志录音/" + tapePath;
                                                                file = new File(tapePath);
                                                            }
                                                            Log.w(TAG, "onClick: " + tapePath);
                                                            if (file.exists()) {
                                                                MediaPlayer mediaPlayer = MediaPlayer.create(singlepoi.this, Uri.parse(tapePath));
                                                                mediaPlayer.start();
                                                            } else {
                                                                Toast.makeText(MyApplication.getContext(), R.string.TakeTapeError1, Toast.LENGTH_LONG).show();
                                                            }
                                                        } else if (num == 1) {
                                                            File file = null;
                                                            if (tapePath.contains(Environment.getExternalStorageDirectory().toString())) {
                                                                file = new File(tapePath);
                                                            } else {
                                                                tapePath = Environment.getExternalStorageDirectory().toString() + "/地名标志录音/" + tapePath;
                                                                file = new File(tapePath);
                                                            }
                                                            Log.w(TAG, "onClick: " + tapePath);
                                                            if (file.exists()) {
                                                                MediaPlayer mediaPlayer = MediaPlayer.create(singlepoi.this, Uri.parse(tapePath));
                                                                mediaPlayer.start();
                                                            } else {
                                                                Toast.makeText(MyApplication.getContext(), R.string.TakeTapeError1, Toast.LENGTH_LONG).show();
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            break;

                                    }
                                    return true;
                                }
                            });
                        }
                    }
                });
                Log.w(TAG, "getBitmap: " + bms.size());
            }
        }).start();
    }

    private void getBitmapDM(final String ImgPath, final ImageView imageView){
        new Thread(new Runnable() {
            @Override
            public void run() {
                //Log.w(TAG, "run: photo.size" + photos.size());
                bms = new ArrayList<>();
                String ImgPathTemp = ImgPath;
                String[] imgPath = new String[DataUtil.appearNumber(ImgPath, "\\|") + 1];
                Log.w(TAG, "run: " + ImgPathTemp);
                for (int k = 0; k < imgPath.length; k++){
                    if (imgPath.length > 1) {
                        if (k < imgPath.length - 1) {
                            imgPath[k] = ImgPathTemp.substring(0, ImgPathTemp.indexOf("|"));
                            ImgPathTemp = ImgPathTemp.substring(ImgPathTemp.indexOf("|") + 1);
                        }else imgPath[k] = ImgPathTemp;
                        Log.w(TAG, "run: " + ImgPathTemp);
                    }else imgPath[k] = ImgPathTemp;
                }
                for (int kk = 0; kk < imgPath.length; kk++) {
                    String rootpath = Environment.getExternalStorageDirectory().toString() + "/盘龙区多媒体数据/照片/";
                    String path;
                    if (!imgPath[kk].contains(Environment.getExternalStorageDirectory().toString())){
                        path = rootpath + imgPath[kk];
                    }else {
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
                            bms.add(new bt(bitmap, imgPath[kk]));
                        } catch (IOException e) {
                            Bitmap bitmap = Bitmap.createBitmap(80, 120, Bitmap.Config.ALPHA_8);
                            bms.add(new bt(bitmap, ""));
                        }
                    } else {
                        Bitmap bitmap = Bitmap.createBitmap(80, 120, Bitmap.Config.ALPHA_8);
                        bms.add(new bt(bitmap, ""));
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (bms.size() > 0 & !bms.get(0).getM_path().isEmpty()) {
                            imageView.setImageBitmap(bms.get(0).getM_bm());
                            imageView.setVisibility(View.VISIBLE);
                            imageView.setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View v, MotionEvent event) {
                                    float distanceX = 0;
                                    float distanceY = 0;
                                    switch (event.getAction()) {
                                        case MotionEvent.ACTION_DOWN:
                                            //第一个手指按下
                                            pt0.set(event.getX(0), event.getY(0));
                                            Log.w(TAG, "onTouchdown: " + event.getX());
                                            Log.w(TAG, "手指id: " + event.getActionIndex());
                                            Log.w(TAG, "ACTION_POINTER_DOWN");
                                            Log.w(TAG, "同时按下的手指数量: " + event.getPointerCount());
                                            break;
                                        case MotionEvent.ACTION_POINTER_DOWN:
                                            //第二个手指按下
                                            Log.w(TAG, "手指id: " + event.getActionIndex());
                                            Log.w(TAG, "onTouchdown: " + event.getX());
                                            Log.w(TAG, "ACTION_POINTER_DOWN");
                                            Log.w(TAG, "同时按下的手指数量: " + event.getPointerCount());
                                            break;
                                        case MotionEvent.ACTION_UP:
                                            //最后一个手指抬起
                                            ges = false;
                                            Log.w(TAG, "onTouchup: " + event.getX());
                                            Log.w(TAG, "getPointerId: " + event.getPointerId(0));
                                            distanceX = event.getX(0) - pt0.x;
                                            distanceY = event.getY(0) - pt0.y;
                                            Log.w(TAG, "onTouch: " + distanceX);
                                            if (Math.abs(distanceX) > Math.abs(distanceY) & Math.abs(distanceX) > 200 & Math.abs(distanceY) < 100) {
                                                if (distanceX > 0) {
                                                    Log.w(TAG, "bms.size : " + bms.size());
                                                    showNum++;
                                                    if (showNum > bms.size() - 1) {
                                                        showNum = 0;
                                                        imageView.setImageBitmap(bms.get(0).getM_bm());
                                                    } else {
                                                        imageView.setImageBitmap(bms.get(showNum).getM_bm());
                                                    }
                                                } else {
                                                    showNum--;
                                                    if (showNum < 0) {
                                                        showNum = bms.size() - 1;
                                                        imageView.setImageBitmap(bms.get(showNum).getM_bm());
                                                    } else {
                                                        imageView.setImageBitmap(bms.get(showNum).getM_bm());
                                                    }
                                                }
                                                Log.w(TAG, "同时抬起的手指数量: " + event.getPointerCount());
                                                Log.w(TAG, "手指id: " + event.getActionIndex());
                                            }
                                            break;
                                        case MotionEvent.ACTION_MOVE:
                                            if (event.getPointerCount() == 3) {
                                                Log.w(TAG, "3指滑动");

                                            } else if (event.getPointerCount() == 4) {
                                                if (!ges) {
                                                    DMGesture(imageView, POITYPE);
                                                    ges = true;
                                                }
                                            } else if (event.getPointerCount() == 5) {
                                                Log.w(TAG, "5指滑动");
                                            } else if (event.getPointerCount() == 2) {
                                                Log.w(TAG, "2指滑动");
                                                Log.w(TAG, "onClick: ");
                                                List<DMBZ> dmbzList = LitePal.where("xh = ?", DMXH).find(DMBZ.class);
                                                String tapePath = dmbzList.get(0).getTAPEPATH();
                                                if (tapePath != null) {
                                                    if (!tapePath.isEmpty()) {
                                                        int num = DataUtil.appearNumber(tapePath, ".mp3") + DataUtil.appearNumber(tapePath, ".MP3");
                                                        if (num > 1) {
                                                            tapePath = tapePath.substring(0, tapePath.indexOf(".mp3") + 4);
                                                            File file = null;
                                                            if (tapePath.contains(Environment.getExternalStorageDirectory().toString())) {
                                                                file = new File(tapePath);
                                                            } else {
                                                                tapePath = Environment.getExternalStorageDirectory().toString() + "/盘龙区多媒体数据/录音/" + tapePath;
                                                                file = new File(tapePath);
                                                            }
                                                            Log.w(TAG, "onClick: " + tapePath);
                                                            if (file.exists()) {
                                                                MediaPlayer mediaPlayer = MediaPlayer.create(singlepoi.this, Uri.parse(tapePath));
                                                                mediaPlayer.start();
                                                            } else {
                                                                Toast.makeText(MyApplication.getContext(), R.string.TakeTapeError1, Toast.LENGTH_LONG).show();
                                                            }
                                                        } else if (num == 1) {
                                                            File file = null;
                                                            if (tapePath.contains(Environment.getExternalStorageDirectory().toString())) {
                                                                file = new File(tapePath);
                                                            } else {
                                                                tapePath = Environment.getExternalStorageDirectory().toString() + "/地名标志录音/" + tapePath;
                                                                file = new File(tapePath);
                                                            }
                                                            Log.w(TAG, "onClick: " + tapePath);
                                                            if (file.exists()) {
                                                                MediaPlayer mediaPlayer = MediaPlayer.create(singlepoi.this, Uri.parse(tapePath));
                                                                mediaPlayer.start();
                                                            } else {
                                                                Toast.makeText(MyApplication.getContext(), R.string.TakeTapeError1, Toast.LENGTH_LONG).show();
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            break;

                                    }
                                    return true;
                                }
                            });
                        }
                    }
                });
                Log.w(TAG, "getBitmap: " + bms.size());
            }
        }).start();
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
            imageUri = FileProvider.getUriForFile(singlepoi.this, "com.android.displaymap.fileprovider", outputImage);
            Log.w(TAG, "takePhoto: " + imageUri);
        }else imageUri = Uri.fromFile(outputImage);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, TAKE_PHOTO);
    }

    private void requestCameraPermission(){
        if (Build.VERSION.SDK_INT >= 23) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(singlepoi.this, Manifest.permission.CAMERA);
            if(checkCallPhonePermission != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(singlepoi.this,new String[]{Manifest.permission.CAMERA},1);
                return;
            }else{
                pickFile();
            }
        } else {
            pickFile();
        }
    }

    private void requestRecordAudioPermission(){
        if (Build.VERSION.SDK_INT >= 23) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(singlepoi.this, Manifest.permission.CAMERA);
            if(checkCallPhonePermission != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(singlepoi.this,new String[]{Manifest.permission.RECORD_AUDIO},3);
                return;
            }else{
                Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
                startActivityForResult(intent, REQUEST_CODE_TAPE);
            }
        } else {
            Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
            startActivityForResult(intent, REQUEST_CODE_TAPE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickFile();
                } else {
                    Toast.makeText(singlepoi.this, "相机权限禁用了。请务必开启相机权限", Toast.LENGTH_SHORT).show();
                }
                break;
            case 2:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takePhoto();
                } else {
                    Toast.makeText(singlepoi.this, "相机权限禁用了。请务必开启相机权限", Toast.LENGTH_SHORT).show();
                }
                break;
            case 3:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
                    startActivityForResult(intent, REQUEST_CODE_TAPE);
                } else {
                    Toast.makeText(singlepoi.this, "相机权限禁用了。请务必开启相机权限", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_PHOTO) {
            Uri uri = data.getData();
            if (POITYPE == 0) {
                List<POI> POIs = LitePal.where("poic = ?", POIC).find(POI.class);
                POI poi = new POI();
                poi.setPhotonum(POIs.get(0).getPhotonum() + 1);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(singlepoi.this.getResources().getText(R.string.DateAndTime).toString());
                Date date = new Date(System.currentTimeMillis());
                poi.updateAll("poic = ?", POIC);
                MPHOTO mphoto = new MPHOTO();
                mphoto.setPdfic(POIs.get(0).getIc());
                mphoto.setPoic(POIC);
                //mphoto.setPath(getRealPath(uri.getPath()));
                mphoto.setPath(DataUtil.getRealPathFromUriForPhoto(this, uri));
                mphoto.setTime(simpleDateFormat.format(date));
                mphoto.save();
            }else if (POITYPE == 1){
                List<DMBZ> dmbzs = LitePal.where("xh = ?", DMXH).find(DMBZ.class);
                DMBZ dmbz = new DMBZ();
                if (dmbzs.get(0).getIMGPATH() != null) {
                    String imgpath = dmbzs.get(0).getIMGPATH();
                    if (DataUtil.appearNumber(imgpath, "\\|") + 1 > 0) dmbz.setIMGPATH(imgpath + "|" + DataUtil.getRealPathFromUriForPhoto(this, uri));
                    else dmbz.setIMGPATH(DataUtil.getRealPathFromUriForPhoto(this, uri));
                }else dmbz.setIMGPATH(DataUtil.getRealPathFromUriForPhoto(this, uri));
                dmbz.updateAll("xh = ?", DMXH);
            }else if (POITYPE == 2){
                List<DMLine> dmLines = LitePal.where("mapid = ?", DML).find(DMLine.class);
                DMLine dmLine = new DMLine();
                if (dmLines.get(0).getImgpath() != null) {
                    String imgpath = dmLines.get(0).getImgpath();
                    if (DataUtil.appearNumber(imgpath, "\\|") + 1 > 0) dmLine.setImgpath(imgpath + "|" + DataUtil.getRealPathFromUriForPhoto(this, uri));
                    else dmLine.setImgpath(DataUtil.getRealPathFromUriForPhoto(this, uri));
                }else dmLine.setImgpath(DataUtil.getRealPathFromUriForPhoto(this, uri));
                dmLine.updateAll("mapid = ?", DML);
            }else if (POITYPE == 3){
                List<DMPoint> dmPoints = LitePal.where("mapid = ?", DMP).find(DMPoint.class);
                DMPoint dmPoint = new DMPoint();
                if (dmPoints.get(0).getImgpath() != null) {
                    String imgpath = dmPoints.get(0).getImgpath();
                    if (DataUtil.appearNumber(imgpath, "\\|") + 1 > 0) dmPoint.setImgpath(imgpath + "|" + DataUtil.getRealPathFromUriForPhoto(this, uri));
                    else dmPoint.setImgpath(DataUtil.getRealPathFromUriForPhoto(this, uri));
                }else dmPoint.setImgpath(DataUtil.getRealPathFromUriForPhoto(this, uri));
                dmPoint.updateAll("mapid = ?", DMP);
            }
        }
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_TAPE){
            Uri uri = data.getData();
            //long time = System.currentTimeMillis();
            if (POITYPE == 0) {
                List<POI> POIs = LitePal.where("poic = ?", POIC).find(POI.class);
                POI poi = new POI();
                poi.setTapenum(POIs.get(0).getTapenum() + 1);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(singlepoi.this.getResources().getText(R.string.DateAndTime).toString());
                Date date = new Date(System.currentTimeMillis());
                poi.updateAll("poic = ?", POIC);
                MTAPE mtape = new MTAPE();
                mtape.setPath(DataUtil.getRealPathFromUriForAudio(this, uri));
                mtape.setPdfic(POIs.get(0).getIc());
                mtape.setPoic(POIC);
                mtape.setTime(simpleDateFormat.format(date));
                mtape.save();
            }else if (POITYPE == 1){
                List<DMBZ> dmbzs = LitePal.where("xh = ?", DMXH).find(DMBZ.class);
                DMBZ dmbz = new DMBZ();
                if (dmbzs.get(0).getTAPEPATH() != null) {
                    String tapepath = dmbzs.get(0).getTAPEPATH();
                    if (DataUtil.appearNumber(tapepath, "\\|") + 1 > 0) dmbz.setTAPEPATH(tapepath + "|" + DataUtil.getRealPathFromUriForPhoto(this, uri));
                    else dmbz.setTAPEPATH(DataUtil.getRealPathFromUriForPhoto(this, uri));
                }else dmbz.setTAPEPATH(DataUtil.getRealPathFromUriForPhoto(this, uri));
                dmbz.updateAll("xh = ?", DMXH);
            }else if (POITYPE == 2){
                List<DMLine> dmLines = LitePal.where("mapid = ?", DML).find(DMLine.class);
                DMLine dmLine = new DMLine();
                if (dmLines.get(0).getTapepath() != null) {
                    String tapepath = dmLines.get(0).getTapepath();
                    if (DataUtil.appearNumber(tapepath, "\\|") + 1 > 0) dmLine.setTapepath(tapepath + "|" + DataUtil.getRealPathFromUriForPhoto(this, uri));
                    else dmLine.setTapepath(DataUtil.getRealPathFromUriForPhoto(this, uri));
                }else dmLine.setTapepath(DataUtil.getRealPathFromUriForPhoto(this, uri));
                dmLine.updateAll("mapid = ?", DML);
            }else if (POITYPE == 3){
                List<DMPoint> dmPoints = LitePal.where("mapid = ?", DMP).find(DMPoint.class);
                DMPoint dmPoint = new DMPoint();
                if (dmPoints.get(0).getTapepath() != null) {
                    String tapepath = dmPoints.get(0).getImgpath();
                    if (DataUtil.appearNumber(tapepath, "\\|") + 1 > 0) dmPoint.setTapepath(tapepath + "|" + DataUtil.getRealPathFromUriForPhoto(this, uri));
                    else dmPoint.setTapepath(DataUtil.getRealPathFromUriForPhoto(this, uri));
                }else dmPoint.setTapepath(DataUtil.getRealPathFromUriForPhoto(this, uri));
                dmPoint.updateAll("mapid = ?", DMP);
            }
        }
        if (resultCode == RESULT_OK && requestCode == TAKE_PHOTO) {
            Log.w(TAG, "onActivityResult1: " + imageUri.toString());
            String imageuri;
            if (Build.VERSION.SDK_INT >= 24) {
                imageuri = DataUtil.getRealPath(imageUri.toString());
            }else {
                imageuri = imageUri.toString().substring(7);
            }
            File file = new File(imageuri);
            Log.w(TAG, "onActivityResult2: " + imageuri);
            if (file.length() != 0) {
                try {
                    MediaStore.Images.Media.insertImage(getContentResolver(), imageuri, "title", "description");
                    // 最后通知图库更新
                    singlepoi.this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + imageuri)));
                }catch (IOException e){
                }
                if (POITYPE == 0) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(singlepoi.this.getResources().getText(R.string.DateAndTime).toString());
                    Date date = new Date(System.currentTimeMillis());
                    List<POI> POIs = LitePal.where("poic = ?", POIC).find(POI.class);
                    POI poi = new POI();
                    long time = System.currentTimeMillis();
                    poi.setPhotonum(POIs.get(0).getPhotonum() + 1);
                    poi.updateAll("poic = ?", POIC);
                    MPHOTO mphoto = new MPHOTO();
                    mphoto.setPoic(POIC);
                    mphoto.setPath(imageuri);
                    mphoto.setTime(simpleDateFormat.format(date));
                    mphoto.save();
                }else if (POITYPE == 1){
                    List<DMBZ> dmbzs = LitePal.where("xh = ?", DMXH).find(DMBZ.class);
                    DMBZ dmbz = new DMBZ();
                    if (dmbzs.get(0).getIMGPATH() != null) {
                        String imgpath = dmbzs.get(0).getIMGPATH();
                        if (DataUtil.appearNumber(imgpath, "\\|") + 1 > 0) dmbz.setIMGPATH(imgpath + "|" + imageuri);
                        else dmbz.setIMGPATH(imageuri);
                    }else dmbz.setIMGPATH(imageuri);
                    dmbz.updateAll("xh = ?", DMXH);
                }else if (POITYPE == 2){
                    List<DMLine> dmLines = LitePal.where("mapid = ?", DML).find(DMLine.class);
                    DMLine dmLine = new DMLine();
                    if (dmLines.get(0).getImgpath() != null) {
                        String imgpath = dmLines.get(0).getImgpath();
                        if (DataUtil.appearNumber(imgpath, "\\|") + 1 > 0) dmLine.setImgpath(imgpath + "|" + imageuri);
                        else dmLine.setImgpath(imageuri);
                    }else dmLine.setImgpath(imageuri);
                    dmLine.updateAll("mapid = ?", DML);
                }else if (POITYPE == 3){
                    List<DMPoint> dmPoints = LitePal.where("mapid = ?", DMP).find(DMPoint.class);
                    DMPoint dmPoint = new DMPoint();
                    if (dmPoints.get(0).getImgpath() != null) {
                        String imgpath = dmPoints.get(0).getImgpath();
                        if (DataUtil.appearNumber(imgpath, "\\|") + 1 > 0) dmPoint.setImgpath(imgpath + "|" + imageuri);
                        else dmPoint.setImgpath(imageuri);
                    }else dmPoint.setImgpath(imageuri);
                    dmPoint.updateAll("mapid = ?", DMP);
                }
            }else {
                file.delete();
                Toast.makeText(singlepoi.this, R.string.TakePhotoError, Toast.LENGTH_LONG).show();
            }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.poiinfotoolbar, menu);
        menu.findItem(R.id.back_pois).setVisible(false);
        menu.findItem(R.id.restore_pois).setVisible(false);
        menu.findItem(R.id.add_pois).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.query_poi_map:
                AlertDialog.Builder dialog1 = new AlertDialog.Builder(singlepoi.this);
                dialog1.setTitle("提示");
                dialog1.setMessage("请选择定位方式");
                dialog1.setCancelable(false);
                dialog1.setPositiveButton("图上位置", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (POITYPE == 0) {
                            SharedPreferences.Editor editor = getSharedPreferences("update_query_attr_to_map", MODE_PRIVATE).edit();
                            editor.putString("poic", POIC);
                            editor.apply();
                        }else if (POITYPE == 1) {
                            SharedPreferences.Editor editor = getSharedPreferences("update_query_attr_to_map", MODE_PRIVATE).edit();
                            editor.putString("poic", "DMBZ" + DMXH);
                            editor.apply();
                        }else if (POITYPE == 2) {
                            SharedPreferences.Editor editor = getSharedPreferences("update_query_attr_to_map", MODE_PRIVATE).edit();
                            editor.putString("poic", "DML" + DML);
                            editor.apply();
                        }else if (POITYPE == 3) {
                            SharedPreferences.Editor editor = getSharedPreferences("update_query_attr_to_map", MODE_PRIVATE).edit();
                            editor.putString("poic", "DMP" + DMP);
                            editor.apply();
                        }
                        singlepoi.this.finish();
                    }
                });
                dialog1.setNegativeButton("路径规划", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Point pt = new Point(LatLng.wgs84togcj02(m_lng, m_lat));
                        Log.w(TAG, "onClick: " + m_lng + ", " + m_lat);
                        try {
                            if (CheckApkExist.checkTencentMapExist(MyApplication.getContext())) {
                                Intent intent = new Intent();
                                intent.setData(Uri.parse("qqmap://map/routeplan?type=walk&to=" + name + "&tocoord=" + pt.getY() + "," + pt.getX() + "&policy=1&referer=图志"));
                                startActivity(intent);
                            }else if (CheckApkExist.checkGaodeMapExist(MyApplication.getContext())) {
                                Intent intent = new Intent();
                                intent.setPackage("com.autonavi.minimap");
                                intent.setAction("android.intent.action.VIEW");
                                intent.addCategory("android.intent.category.DEFAULT");
                                intent.setData(Uri.parse("androidamap://route?sourceApplication=图志&dlat=" + pt.getY() + "&dlon=" + pt.getX() + "&dname=" + name + "&dev=1&t=2"));
                                startActivity(intent);
                            }else if (CheckApkExist.checkBaiduMapExist(MyApplication.getContext())) {
                                Intent intent = new Intent();
                                intent.setData(Uri.parse("baidumap://map/direction?" +
                                        "destination=latlng:" + m_lat + "," + m_lng + "|name:" + name +"&mode=walking"));
                                startActivity(intent);
                            }else {
                                Toast.makeText(singlepoi.this, R.string.QueryPathError, Toast.LENGTH_SHORT).show();
                            }
                        }catch (Exception e){
                            Toast.makeText(singlepoi.this, R.string.QueryPathError1, Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                dialog1.show();
                break;
            case R.id.back_andupdate:
                if (POITYPE == 0) {
                    POI poi = new POI();
                    poi.setName(editText_name.getText().toString());
                    poi.setDescription(editText_des.getText().toString());
                    poi.setType(str);
                    poi.updateAll("poic = ?", POIC);
                }else if (POITYPE == 1) {
                    DMBZ poi = new DMBZ();
                    poi.setXH(DMXH);
                    poi.setDY(edit_DY.getText().toString());
                    poi.setMC(edit_MC.getText().toString());
                    poi.setBZMC(edit_BZMC.getText().toString());
                    poi.setXZQMC(edit_XZQMC.getText().toString());
                    poi.setXZQDM(edit_XZQDM.getText().toString());
                    poi.setSZDW(edit_SZDW.getText().toString());
                    poi.setSCCJ(edit_SCCJ.getText().toString());
                    poi.setGG(edit_GG.getText().toString());
                    poi.updateAll("xh = ?", DMXH);
                }else if (POITYPE == 2) {
                    DMLine dmLine = new DMLine();
                    dmLine.setQydm(edit_qydm.getText().toString());
                    dmLine.setLbdm(edit_lbdm.getText().toString());
                    dmLine.setBzmc(edit_bzmc.getText().toString());
                    dmLine.setCym(edit_cym.getText().toString());
                    dmLine.setJc(edit_jc.getText().toString());
                    dmLine.setBm(edit_bm.getText().toString());
                    dmLine.setDfyz(edit_dfyz.getText().toString());
                    dmLine.setZt(edit_zt.getText().toString());
                    dmLine.setDmll(edit_dmll.getText().toString());
                    dmLine.setDmhy(edit_dmhy.getText().toString());
                    dmLine.setLsyg(edit_lsyg.getText().toString());
                    dmLine.setDlstms(edit_dlstms.getText().toString());
                    dmLine.setZlly(edit_zlly.getText().toString());
                    dmLine.updateAll("mapid = ?", DML);
                }else if (POITYPE == 3) {
                    DMPoint dmPoint = new DMPoint();
                    dmPoint.setQydm(edit_qydm.getText().toString());
                    dmPoint.setLbdm(edit_lbdm.getText().toString());
                    dmPoint.setBzmc(edit_bzmc.getText().toString());
                    dmPoint.setCym(edit_cym.getText().toString());
                    dmPoint.setJc(edit_jc.getText().toString());
                    dmPoint.setBm(edit_bm.getText().toString());
                    dmPoint.setDfyz(edit_dfyz.getText().toString());
                    dmPoint.setZt(edit_zt.getText().toString());
                    dmPoint.setDmll(edit_dmll.getText().toString());
                    dmPoint.setDmhy(edit_dmhy.getText().toString());
                    dmPoint.setLsyg(edit_lsyg.getText().toString());
                    dmPoint.setDlstms(edit_dlstms.getText().toString());
                    dmPoint.setZlly(edit_zlly.getText().toString());
                    dmPoint.updateAll("mapid = ?", DMP);
                }
                this.finish();
                break;
            case R.id.deletepoi:
                AlertDialog.Builder dialog = new AlertDialog.Builder(singlepoi.this);
                dialog.setTitle("提示");
                dialog.setMessage("确认删除兴趣点吗?");
                dialog.setCancelable(false);
                dialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (POITYPE == 0) {
                            LitePal.deleteAll(POI.class, "poic = ?", POIC);
                            LitePal.deleteAll(MPHOTO.class, "poic = ?", POIC);
                            LitePal.deleteAll(MTAPE.class, "poic = ?", POIC);
                        }else if (POITYPE == 1) {
                            LitePal.deleteAll(DMBZ.class, "xh = ?", DMXH);
                        }else if (POITYPE == 2) {
                            LitePal.deleteAll(DMLine.class, "mapid = ?", DML);
                        }else if (POITYPE == 3) {
                            LitePal.deleteAll(DMPoint.class, "mapid = ?", DMP);
                        }
                        singlepoi.this.finish();
                    }
                });
                dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                dialog.show();
                break;
            default:
        }
        return true;
    }
}
