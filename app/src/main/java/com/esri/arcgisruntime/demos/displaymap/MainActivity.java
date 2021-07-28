package com.esri.arcgisruntime.demos.displaymap;

import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.media.ExifInterface;
import android.media.Image;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.ArcGISRuntimeException;
import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureCollectionTable;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.data.FeatureTable;
import com.esri.arcgisruntime.data.Geodatabase;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.data.ShapefileFeatureTable;
import com.esri.arcgisruntime.geometry.AreaUnit;
import com.esri.arcgisruntime.geometry.AreaUnitId;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.GeodeticCurveType;
import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.GeometryType;
import com.esri.arcgisruntime.geometry.LinearUnit;
import com.esri.arcgisruntime.geometry.LinearUnitId;
import com.esri.arcgisruntime.geometry.PartCollection;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.PointCollection;
import com.esri.arcgisruntime.geometry.Polygon;
import com.esri.arcgisruntime.geometry.Polyline;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.layers.Layer;
import com.esri.arcgisruntime.layers.RasterLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.location.LocationDataSource;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.MobileMapPackage;
import com.esri.arcgisruntime.mapping.view.Callout;
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.LocationDisplay;
import com.esri.arcgisruntime.mapping.view.MapRotationChangedEvent;
import com.esri.arcgisruntime.mapping.view.MapRotationChangedListener;
import com.esri.arcgisruntime.mapping.view.MapScaleChangedEvent;
import com.esri.arcgisruntime.mapping.view.MapScaleChangedListener;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.raster.Raster;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.esri.arcgisruntime.symbology.SimpleRenderer;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.github.clans.fab.FloatingActionButton;
import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import lecho.lib.hellocharts.listener.PieChartOnValueSelectListener;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.PieChartView;
import top.defaults.colorpicker.ColorPickerPopup;

public class MainActivity extends AppCompatActivity {
    private MapView mMapView;
    private static final String TAG = "MainActivity";
    private List<layer> layerList = new ArrayList<>();
    private List<layer1> layers = new ArrayList<>();
    private DrawerLayout mDrawerLayout;

    private LocationManager locationManager;

    Location location;

    //上一个记录下来的坐标点坐标
    private float last_x, last_y;

    //轨迹线
    List<Trail> trails;

    //记录当前轨迹
    String m_cTrail = "";
    //是否结束轨迹绘制
    Boolean isLocateEnd = true;
    //当前记录的轨迹点数
    int isLocate = 0;

    String[] items;
    boolean isLoc = false;
    List<layer1> TPKlayers = new ArrayList<>();
    //初始化传感器管理器
    private SensorManager sensorManager;
    double OriginScale;
    //private layerAdapter adapter;
    //private RecyclerView recyclerView;
    String[] strings;
    String ic;
    //private GridLayoutManager layoutManager;
    //private ImageButton recyclerViewButton;
    FeatureLayer DMPointFeatureLayer;
    private boolean isClick = false;
    private TextView ScaleShow;
    ArcGISMap map;
    com.github.clans.fab.FloatingActionButton whiteBlank_fab;
    //记录画笔颜色
    private int color_Whiteblank;
    GraphicsOverlay graphicsOverlay_66;
    ImageView North;
    FloatingActionButton ResetBT;
    //声明bts容器
    List<bt> bts;
    boolean CreatePOI = false;
    int POIType = -1;
    int theNum;
    //记录用户当前坐标
    double m_long, m_lat;
    //记录是否缓存了图片
    boolean isCreateBitmap;
    //记录拍摄照片的存储位置
    Uri imageUri;
    //记录当前进行的操作
    DisplayEnum RunningFunction = DisplayEnum.FUNC_NONE;

    private DisplayEnum DrawType = DisplayEnum.DRAW_NONE;
    /*public static final int DRAW_POLYGON = -1;
    public static final int DRAW_POLYLINE = -2;
    public static final int DRAW_POINT = -3;
    public static final int DRAW_NONE = 0;*/

    //记录是否处于白板画图状态
    private boolean isWhiteBlank = false;
    GraphicsOverlay graphicsOverlay_9;
    GraphicsOverlay graphicsOverlay_10;
    PointCollection points = new PointCollection(SpatialReference.create(4523));;
    List<Graphic> graphics = new ArrayList<>();
    boolean isOk = false;
    boolean isOK1 = false;
    Point OriginLocation;

    List<xzq> xzqs;

    //记录是否开启白板功能
    private boolean isOpenWhiteBlank = false;
    Callout mCallout;
    boolean inMap;
    boolean isNorth = false;
    FloatingActionButton DrawFeature;
    PointCollection pointCollection;
    FloatingActionButton MapQueryBT;
    PieChartView pieChartView;
    List<KeyAndValue> keyAndValues;
    double wholeArea = 0;
    private DisplayEnum QueriedFeature = DisplayEnum.TDGHDL_FEATURE;
    FloatingActionButton LocHereBT;
    Point mLocation;
    LocationDisplay locationDisplay;
    FeatureLayer TDGHDLFeatureLayer = null;
    FeatureLayer XZQFeatureLayer = null;
    boolean hasTPK = false;

    //用于标识是否开启了"图查询"状态，该状态和"要素批量删除"状态相互冲突
    private Boolean MapQuery = false;
    //用于标识当前是否开启了"要素批量删除"状态，该状态和"图查询"状态相互冲突
    private Boolean Deleting = false;
    //用于标识当前是否开启了"轨迹记录"状态
    private Boolean IsRecordingTrail = false;

    private DisplayEnum QueryProcessType = DisplayEnum.NOQUERY;



    //FeatureLayer QueriedFeatureLayer;

    //private static final int INQUERY = -1;
    //private static final int FINISHQUERY = -2;
    //private static final int NOQUERY = -3;

    private void ParseAndUpdateTrails(){
        ParseTrails();
        UpdateTrails();
    }


    private void ParseTrails(){
        trails = LitePal.findAll(Trail.class);
        Log.w(TAG, "ParseTrails: " + trails.size());
        for (int i = 0; i < trails.size(); i++) {
            Log.w(TAG, "ParseTrails: " + trails.get(i).getPath());
            if (trails.get(i).getPath().contains("0.0 0.0"))
            {
                Log.w(TAG, "ParseTrails: " + i + "已删除");
                LitePal.deleteAll(Trail.class, "name = ?", trails.get(i).getName());
                trails = LitePal.findAll(Trail.class);
            }
        }
    }

    private void UpdateTrails(){
        if (isLocateEnd) {
            for (int ii = 0; ii < trails.size(); ii++) {
                String name = trails.get(ii).getName();
                boolean HasChoosed = false;
                for (int i = 0; i < ChoosedTrails.size(); i++) {
                    if (ChoosedTrails.get(i).equals(name))
                    {
                        HasChoosed = true;
                        break;
                    }
                }
                String str1 = trails.get(ii).getPath();
                String[] TrailString = str1.split(" ");
                float[] Trails = new float[TrailString.length];
                for (int i = 0; i < TrailString.length; ++i) {
                    Trails[i] = Float.valueOf(TrailString[i]);
                }
                PointCollection points = new PointCollection(SpatialReference.create(4523));
                for (int j = 0; j < Trails.length - 1; j = j + 2) {
                    Log.w(TAG, "updateChoosedPoi, Trails:  " + Trails[j+1] + ", " + Trails[j]);
                    //Point wgs84Point = new Point(Double.valueOf(Trails[j+1]), Double.valueOf(Trails[j]));
                    Point wgs84Point = (Point) GeometryEngine.project(new Point(Double.valueOf(Trails[j+1]), Double.valueOf(Trails[j]), SpatialReferences.getWgs84()), SpatialReference.create(4523));
                    points.add(wgs84Point);
                }
                SimpleLineSymbol lineSymbol = null;
                if (HasChoosed) {
                    lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.rgb(0, 255, 255), 3);
                }
                else {
                    lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.rgb(255, 0, 255), 3);
                }
                Polyline polyline = new Polyline(points);
                Graphic g = new Graphic(polyline, lineSymbol);
                graphics.add(g);
            }
            /*PointCollection points = new PointCollection(SpatialReference.create(4523));
            Point wgs84Point = (Point) GeometryEngine.project(new Point(103.6254, 27.7993, SpatialReferences.getWgs84()), SpatialReference.create(4523));
            points.add(wgs84Point);
            Point wgs84Point1 = (Point) GeometryEngine.project(new Point(103.6912, 27.9610, SpatialReferences.getWgs84()), SpatialReference.create(4523));
            points.add(wgs84Point1);
            Log.w(TAG, "updateChoosedPoi: " + wgs84Point1.getX() + ", " + wgs84Point1.getY());
            SimpleLineSymbol lineSymbol = null;
            lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.BLACK, 3);
            Polyline polyline = new Polyline(points);
            Graphic g = new Graphic(polyline, lineSymbol);
            graphics.add(g);
            Log.w(TAG, "updateChoosedPoi: " + "ok");*/
        }
    }

    private String ParseTrailToMultiline(String TrailStr){
        String Multiline = "";
        String[] TrailString = TrailStr.split(" ");
        float[] Trails = new float[TrailString.length];
        for (int i = 0; i < TrailString.length; ++i) {
            Trails[i] = Float.valueOf(TrailString[i]);
        }
        for (int j = 0; j < Trails.length - 1; j = j + 2) {
            Point wgs84Point = (Point) GeometryEngine.project(new Point(Double.valueOf(Trails[j+1]), Double.valueOf(Trails[j]), SpatialReferences.getWgs84()), SpatialReference.create(4523));
            if (j != 0)
                Multiline += " ";
            Multiline += wgs84Point.getX() + "," + wgs84Point.getY() + ",0";
        }
        return Multiline;
    }

    private String GetNowTime(){
        return Long.toString(System.currentTimeMillis());
    }

    private DisplayEnum isQurey = DisplayEnum.I_NOQUREY;//.I_NOQUREY;I_QUREY
    //private static final int QUREY = 0;
    //private static final int NOQUREY = 1;
    List<QueryInfo> queryInfos = new ArrayList<>();

    private SensorEventListener listener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (isNorth) {
                mMapView.setViewpointRotationAsync(event.values[0]);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    @Override
    public void onBackPressed() {
        SharedPreferences.Editor editor = getSharedPreferences("xzq", MODE_PRIVATE).edit();
        editor.putString("name", "");
        editor.apply();
        super.onBackPressed();
    }

    private void showPopueWindowForxzqTree(){
        final View popView = View.inflate(this, R.layout.popupwindow_xzqtree,null);
        RecyclerView recyclerView1 = (RecyclerView) popView.findViewById(R.id.xzqtree_recycler_view);
        GridLayoutManager layoutManager1 = new GridLayoutManager(popView.getContext(),1);
        recyclerView1.setLayoutManager(layoutManager1);
        //xzqTreeAdapter adapter1 = new xzqTreeAdapter(DataUtil.bubbleSort(LitePal.findAll(xzq.class)));
        xzqs = DataUtil.bubbleSort(LitePal.where("grade = ?", Integer.toString(1)).find(xzq.class));
        Log.w(TAG, "readXZQ: " + xzqs.size());
        final xzqTreeAdapter adapter1 = new xzqTreeAdapter(xzqs);
        adapter1.setOnItemClickListener(new xzqTreeAdapter.OnRecyclerItemClickListener() {
            @Override
            public void onItemClick(View view, String xzqmc, final int position) {
                if (XZQLayerName.equals("土地利用变更调查数据2020年") || XZQLayerName.equals("二调地类图斑") || XZQLayerName.equals("土地规划地类") || XZQLayerName.equals("永善县稳定耕地"))
                {
                    Intent intent = new Intent(MainActivity.this, chartshow.class);
                    intent.putExtra("LayerName", XZQLayerName);
                    intent.putExtra("xzqmc", xzqmc);
                    startActivity(intent);
                }else{
                    Intent intent = new Intent(MainActivity.this, chartshow.class);
                    intent.putExtra("LayerName", "土地利用变更调查数据2020年");
                    intent.putExtra("xzqmc", xzqmc);
                    startActivity(intent);
                    //Toast.makeText(MainActivity.this, "请使用预设图层进行空间分析", Toast.LENGTH_LONG).show();
                }
            }
        });
        recyclerView1.setAdapter(adapter1);
        //获取屏幕宽高
        final int weight = getResources().getDisplayMetrics().widthPixels;
        final int height = getResources().getDisplayMetrics().heightPixels - 60;

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

        FloatingActionButton button = (FloatingActionButton) popView.findViewById(R.id.xzqtree_back_pop);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = getSharedPreferences("xzq", MODE_PRIVATE).edit();
                editor.putString("name", "");
                editor.apply();
                popupWindow.dismiss();
            }
        });
        //popupWindow OnTouchListener

        //popupWindow出现屏幕变为半透明
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.5f;
        getWindow().setAttributes(lp);
        popupWindow.showAtLocation(popView, Gravity.NO_GRAVITY, 0,0);
    }

    //获取文件读取权限
    void fileReadPermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                StaticVariableEnum.READ_EXTERNAL_STORAGE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            StaticVariableEnum.READ_EXTERNAL_STORAGE},
                    StaticVariableEnum.PERMISSION_CODE_1
            );

            return;
        }

    }

    //获取位置权限
    void locationPermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                StaticVariableEnum.ACCESS_COARSE_LOCATION);
        int permissionCheck1 = ContextCompat.checkSelfPermission(this,
                StaticVariableEnum.ACCESS_FINE_LOCATION);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED || permissionCheck1 != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            StaticVariableEnum.ACCESS_COARSE_LOCATION, StaticVariableEnum.ACCESS_FINE_LOCATION},
                    StaticVariableEnum.PERMISSION_CODE_2
            );

            return;
        }

    }

    //获取文件读取权限
    void requestAuthority() {
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                StaticVariableEnum.READ_EXTERNAL_STORAGE);
        int permissionCheck2 = ContextCompat.checkSelfPermission(this,
                StaticVariableEnum.ACCESS_FINE_LOCATION);
        int permissionCheck3 = ContextCompat.checkSelfPermission(this,
                StaticVariableEnum.ACCESS_COARSE_LOCATION);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED || permissionCheck2 != PackageManager.PERMISSION_GRANTED || permissionCheck3 != PackageManager.PERMISSION_GRANTED) {
            Log.w(TAG, "requestAuthority: " + permissionCheck + ";" + permissionCheck2 + ";" + permissionCheck3);
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            StaticVariableEnum.READ_EXTERNAL_STORAGE, StaticVariableEnum.WRITE_EXTERNAL_STORAGE, StaticVariableEnum.ACCESS_FINE_LOCATION, StaticVariableEnum.ACCESS_COARSE_LOCATION},
                    StaticVariableEnum.PERMISSION_CODE
            );
        } else{
            // 如果许可检测成功， 则执行以下操作
            doSpecificOperation();
            initWidgetAndVariable();
            readMMPKData();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.cancel:
                Log.w(TAG, "onOptionsItemSelected: ");
                QueriedFeatureGraphic = null;
                if (graphicsOverlay_QueriedFeatureLayer != null) {
                    graphicsOverlay_QueriedFeatureLayer.getGraphics().clear();
                    //if (graphicsOverlay_QueriedFeatureLayer != null)
                    mMapView.getGraphicsOverlays().remove(graphicsOverlay_QueriedFeatureLayer);
                    graphicsOverlay_QueriedFeatureLayer = null;
                }

                isQurey = DisplayEnum.I_NOQUREY;
                invalidateOptionsMenu();
                break;
            case R.id.search:
                isQurey = DisplayEnum.I_QUREY;
                invalidateOptionsMenu();
                break;
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            default:

        }
        return true;
    }

    List<whiteblank> CurrentWhiteBlank = new ArrayList<>();
    private void showPopueWindowForWhiteblank(){
        CurrentWhiteBlank = new ArrayList<>();
        final View popView = View.inflate(this, R.layout.popupwindow_whiteblank,null);
        isWhiteBlank = true;
        FloatingActionButton back_pop = (FloatingActionButton) popView.findViewById(R.id.back_pop) ;
        back_pop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (CurrentWhiteBlank.size() > 0) {
                    for (int i = 0; i < graphics.size(); ++i) {
                        graphicsOverlay_10.getGraphics().remove(graphics.get(i));
                    }
                    if (graphics.size() != 0) graphics.remove(graphics.size() - 1);
                    graphicsOverlay_10.getGraphics().clear();
                    for (int i = 0; i < graphics.size(); ++i) {
                        graphicsOverlay_10.getGraphics().add(graphics.get(i));
                    }
                    mMapView.getGraphicsOverlays().remove(graphicsOverlay_10);
                    mMapView.getGraphicsOverlays().add(graphicsOverlay_10);
                    //LitePal.deleteAll(whiteblank.class, "", whiteblanks.size());
                    CurrentWhiteBlank.remove(CurrentWhiteBlank.size()-1);
                }
                else{
                    Toast.makeText(MainActivity.this, "已清空当次所画白板", Toast.LENGTH_LONG).show();
                }
            }
        });
        FloatingActionButton fff = (FloatingActionButton) popView.findViewById(R.id.colorSeeker_pop);
        fff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.w(TAG, "onClick: " + "取样器已经打开");

                new ColorPickerPopup.Builder(MainActivity.this)
                        .initialColor(Color.RED) // Set initial color
                        .enableBrightness(true) // Enable brightness slider or not
                        .enableAlpha(true) // Enable alpha slider or not
                        .okTitle("确定")
                        .cancelTitle("取消")
                        .showIndicator(true)
                        .showValue(true)
                        .build()
                        .show(new ColorPickerPopup.ColorPickerObserver() {
                            @Override
                            public void onColorPicked(int color) {
                                color_Whiteblank = color;
                            }
                        });
            }
        });

        FloatingActionButton eraseContent = (FloatingActionButton) popView.findViewById(R.id.eraseContent);
        eraseContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.w(TAG, "onClick: " + mMapView.getGraphicsOverlays().size());
                showWhiteBlankDialog();
            }
        });

        FloatingActionButton popwhiteblank = (FloatingActionButton) popView.findViewById(R.id.whiteblank_pop);
        FrameLayout frameLayout = (FrameLayout) popView.findViewById(R.id.fml_pop);
        //获取屏幕宽高
        final int weight = getResources().getDisplayMetrics().widthPixels;
        final int height = getResources().getDisplayMetrics().heightPixels;

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
        //popupWindow OnTouchListener
        frameLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //PointCollection points = new PointCollection(SpatialReference.create(4523));
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        //按下
                        points.clear();
                        isOk = false;
                        break;
                    case MotionEvent.ACTION_UP:
                        //抬起
                        //graphicsOverlay_9.setVisible(false);
                        while (mMapView.getGraphicsOverlays().size() != 0){
                            for (int i = 0; i < mMapView.getGraphicsOverlays().size(); ++i){
                                mMapView.getGraphicsOverlays().remove(i);
                            }
                        }
                        geometry_WhiteBlank geometryWhiteBlank = new geometry_WhiteBlank();
                        geometryWhiteBlank.setLineSymbol(color_Whiteblank, 3);
                        geometryWhiteBlank.setPolyline(points);
                        graphics.add(geometryWhiteBlank.getFillGraphic());
                        graphicsOverlay_10.getGraphics().add(graphics.get(graphics.size() - 1));
                        if (mMapView.getGraphicsOverlays().size() != 0) mMapView.getGraphicsOverlays().remove(graphicsOverlay_10);
                        mMapView.getGraphicsOverlays().add(graphicsOverlay_10);
                        whiteblank wb = new whiteblank();
                        String pts = "";
                        for (int i = 0; i < points.size(); ++i){
                            if (i == 0) {
                                pts = Double.toString(points.get(i).getX()) + "," + Double.toString(points.get(i).getY());
                            }else {
                                pts = pts + "lzy" + Double.toString(points.get(i).getX()) + "," + Double.toString(points.get(i).getY());
                            }
                        }
                        wb.setObjectID(GetNowTime());
                        wb.setPts(pts);
                        wb.setColor(color_Whiteblank);
                        //wb.save();
                        CurrentWhiteBlank.add(wb);
                        points.clear();
                        //Toast.makeText(MainActivity.this, "抬起", Toast.LENGTH_SHORT).show();
                        isOk = true;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        android.graphics.Point screenPoint = new android.graphics.Point(Math.round(event.getX()),
                                Math.round(event.getY() - getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop()));

                        //final android.graphics.Point screenPoint = new android.graphics.Point(Math.round(v.getX()),
                                //Math.round(v.getY() - mMapView.getTop()));
                        //Log.w(TAG, "onTouch: " + event.getX() + " ; " + event.getY());
                        // create a map point from screen point
                        Point mapPoint = mMapView.screenToLocation(screenPoint);
                        // convert to WGS84 for lat/lon format
                        Point wgs84Point = (Point) GeometryEngine.project(mapPoint, SpatialReference.create(4523));
                        //for (int i = 0; i < size; ++i){
                        points.add(wgs84Point);
                        //}
                        //Log.w(TAG, "onTouch: " + wgs84Point.getX() + " ; " + wgs84Point.getY());
                        //Log.w(TAG, "size0: " + size);
                        Log.w(TAG, "size1: " +
                                points.get(0).toString());
                        if (!points.isEmpty()) {
                            graphicsOverlay_9 = new GraphicsOverlay(GraphicsOverlay.RenderingMode.DYNAMIC);
                            SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, color_Whiteblank, 3);
                            Polyline polyline = new Polyline(points);
                            Graphic fillGraphic = new Graphic(polyline, lineSymbol);
                            graphicsOverlay_9.getGraphics().add(fillGraphic);
                            mMapView.getGraphicsOverlays().remove(graphicsOverlay_9);
                            mMapView.getGraphicsOverlays().add(graphicsOverlay_9);
                        }
                        break;
                }

                if (!isOk) {

                }
                //PointF pt = new PointF(event.getRawX(), event.getRawY());

                //Log.w(TAG, "onTouch: " + wgs84Point.getX() + " ; " + wgs84Point.getY());
                //pt_last = pt_current;
                //pt_current = pt;
                /*if (event.getRawY() >= height - 100 & event.getRawY() <= height & event.getRawX() >= 50 & event.getRawX() <= 150){
                    popupWindow.dismiss();
                    whiteblank.setImageResource(R.drawable.ic_brush_black_24dp);
                }*/

                return true;
            }
        });
        //popupWindow出现屏幕变为半透明
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 1f;
        getWindow().setAttributes(lp);
        popupWindow.showAtLocation(popView, Gravity.NO_GRAVITY,0,0);
        popwhiteblank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < CurrentWhiteBlank.size(); i++) {
                    CurrentWhiteBlank.get(i).save();
                }
                popupWindow.dismiss();
                whiteBlank_fab.setVisibility(View.VISIBLE);
                isOpenWhiteBlank = false;
                whiteBlank_fab.setImageResource(R.drawable.ic_brush_black_24dp);
                setTitle(R.string.app_name);
            }
        });
    }

    /* 获取状态栏高度
     * @param context
     * @return
             */
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen",
                "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * 获取导航栏高度
     * @param context
     * @return
     */
    public static int getDaoHangHeight(Context context) {
        int result = 0;
        int resourceId=0;
        int rid = context.getResources().getIdentifier("config_showNavigationBar", "bool", "android");
        if (rid!=0){
            resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
            //CMLog.show("高度："+resourceId);
            //CMLog.show("高度："+context.getResources().getDimensionPixelSize(resourceId) +"");
            return context.getResources().getDimensionPixelSize(resourceId);
        }else
            return 0;
    }


    private void ChooseAndShowWhiteblankLine(PointF pt1){
        List<whiteblank> whiteblanks = LitePal.findAll(whiteblank.class);

        //checkWhiteblankLine(multilines, pt1);
        Log.w(TAG, "ParseWhiteblanklinesToMultilines: " + pt1.x + ", " + pt1.y);
        String ChoosedObjectID = GetChoosedWhiteblankLine(whiteblanks, pt1);
        Log.w(TAG, "ParseWhiteblanklinesToMultilines: " + ChoosedObjectID);
        if (ChoosedObjectID != "") {
            AddWhiteblankLineByObjectID(ChoosedObjectID);
            initialiseGraphics();
            if (ShowPoi)
                updatePoi();
            if (ShowTrail)
                ParseAndUpdateTrails();
            if (ShowMyTuban)
                parseAndUpdateMyTuban();
            if (ShowWhiteBlank)
                updateWhiteBlank();
            updateGraphicsOverlayer();
        }
    }

    private void ChoosedAndShowTrail(PointF pt1){
        List<Trail> trails = LitePal.findAll(Trail.class);
        String ChoosedName = GetChoosedTrail(trails, pt1);
        if (ChoosedName != "") {
            AddTrailsByName(ChoosedName);
            initialiseGraphics();
            if (ShowPoi)
                updatePoi();
            if (ShowTrail)
                ParseAndUpdateTrails();
            if (ShowMyTuban)
                parseAndUpdateMyTuban();
            if (ShowWhiteBlank)
                updateWhiteBlank();
            updateGraphicsOverlayer();
        }
    }

    private void AddWhiteblankLineByObjectID(String objectID){
        Boolean hasSameObject = false;
        for (int i = 0; i < ChoosedWhiteblankLines.size(); i++) {
            if (ChoosedWhiteblankLines.get(i).equals(objectID))
            {
                ChoosedWhiteblankLines.remove(i);
                i--;
                hasSameObject = true;
                break;
            }
        }
        if (!hasSameObject)
            ChoosedWhiteblankLines.add(objectID);
    }

    private void AddPoisByPoic(String poic){
        Boolean hasSameIndex = false;
        for (int i = 0; i < ChoosedPois.size(); i++) {
            if (ChoosedPois.get(i).equals(poic))
            {
                ChoosedPois.remove(i);
                i--;
                hasSameIndex = true;
                break;
            }
        }
        if (!hasSameIndex)
            ChoosedPois.add(poic);
    }

    private void AddTrailsByName(String name){
        Boolean hasSameIndex = false;
        for (int i = 0; i < ChoosedTrails.size(); i++) {
            if (ChoosedTrails.get(i).equals(name) )
            {
                ChoosedTrails.remove(i);
                i--;
                hasSameIndex = true;
                break;
            }
        }
        if (!hasSameIndex)
            ChoosedTrails.add(name);
    }

    private void AddMyTubansByName(String name){
        Boolean hasSameIndex = false;
        for (int i = 0; i < ChoosedMyTubans.size(); i++) {
            if (ChoosedMyTubans.get(i).equals(name))
            {
                ChoosedMyTubans.remove(i);
                i--;
                hasSameIndex = true;
                break;
            }
        }
        if (!hasSameIndex)
            ChoosedMyTubans.add(name);
    }

    private List<String> ChoosedWhiteblankLines = new ArrayList<>();
    private List<String> ChoosedPois = new ArrayList<>();
    private List<String> ChoosedTrails = new ArrayList<>();
    private List<String> ChoosedMyTubans = new ArrayList<>();

    private String GetMultiLineByWhiteblank(String OldPts){
        //for (int i = 0; i < whiteblanks.size(); i++) {
        String line = OldPts;
        String[] pts = line.split("lzy");
        String NewLine = "";
        for (int j = 0; j < pts.length; j++) {
            String pt = pts[j] + ",0";
            if (j != pts.length-1)
                pt += " ";
            NewLine += pt;
        }
        //}
        return NewLine;
    }

    private String GetChoosedTrail(List<Trail> trails, PointF pt1){
        String ChoosedName = "";
        if (trails.size() > 0) {
            int linenum = trails.size();
            double deltas = 0;
            //显示线状要素
            for (int j = 0; j < linenum; ++j) {
                String line = ParseTrailToMultiline(trails.get(j).getPath());
                String[] strings = line.split(" ");
                for (int cc = 0; cc < strings.length - 1; ++cc) {
                    String[] ptx1 = strings[cc].split(",");
                    String[] ptx2 = strings[cc + 1].split(",");
                    com.esri.arcgisruntime.demos.displaymap.Point pointF = new com.esri.arcgisruntime.demos.displaymap.Point(Double.valueOf(ptx1[0]), Double.valueOf(ptx1[1]));
                    com.esri.arcgisruntime.demos.displaymap.Point pointF1 = new com.esri.arcgisruntime.demos.displaymap.Point(Double.valueOf(ptx2[0]), Double.valueOf(ptx2[1]));
                    PointF theTouchPt = pt1;
                    if (deltas == 0) {
                        double thedis = lineUtil.getDistance(lineUtil.getLineNormalEquation(pointF.getX(), pointF.getY(), pointF1.getX(), pointF1.getY()), theTouchPt);
                        Log.w(TAG, "ParseWhiteblanklinesToMultilines: " + j + thedis + ", " + deltas);
                        if (thedis <= (lineUtil.getDistance1(pointF, pt1) >= lineUtil.getDistance1(pointF1, pt1) ? lineUtil.getDistance1(pointF, pt1) : lineUtil.getDistance1(pointF1, pt1)) && thedis >= (lineUtil.getDistance1(pointF, pt1) >= lineUtil.getDistance1(pointF1, pt1) ? lineUtil.getDistance1(pointF1, pt1) : lineUtil.getDistance1(pointF, pt1))) {
                            deltas = thedis;
                            ChoosedName = trails.get(j).getName();
                        } else {
                            deltas = (lineUtil.getDistance1(pointF, pt1) >= lineUtil.getDistance1(pointF1, pt1) ? lineUtil.getDistance1(pointF1, pt1) : lineUtil.getDistance1(pointF, pt1));
                            ChoosedName = trails.get(j).getName();
                        }
                    } else {
                        double delta1 = lineUtil.getDistance(lineUtil.getLineNormalEquation(pointF.getX(), pointF.getY(), pointF1.getX(), pointF1.getY()), theTouchPt);
                        if (delta1 <= (lineUtil.getDistance1(pointF, pt1) >= lineUtil.getDistance1(pointF1, pt1) ? lineUtil.getDistance1(pointF, pt1) : lineUtil.getDistance1(pointF1, pt1)) && delta1 >= (lineUtil.getDistance1(pointF, pt1) >= lineUtil.getDistance1(pointF1, pt1) ? lineUtil.getDistance1(pointF1, pt1) : lineUtil.getDistance1(pointF, pt1))) {
                        } else
                            delta1 = (lineUtil.getDistance1(pointF, pt1) >= lineUtil.getDistance1(pointF1, pt1) ? lineUtil.getDistance1(pointF1, pt1) : lineUtil.getDistance1(pointF, pt1));
                        Log.w(TAG, "ParseWhiteblanklinesToMultilines: " + j + delta1 + ", " + deltas);
                        if (delta1 < deltas) {
                            deltas = delta1;
                            ChoosedName = trails.get(j).getName();
                        }
                    }
                }
            }
            if (deltas < 1000000) {
                return  ChoosedName;
            }
        }
        return "";
    }

    private String GetChoosedWhiteblankLine(List<whiteblank> whiteblanks, PointF pt1){
        String ChoosedObjectID = "";
        if (whiteblanks.size() > 0) {
            int linenum = whiteblanks.size();
            double deltas = 0;
            //显示线状要素
            for (int j = 0; j < linenum; ++j) {
                String line = GetMultiLineByWhiteblank(whiteblanks.get(j).getPts());
                    String[] strings = line.split(" ");
                    for (int cc = 0; cc < strings.length - 1; ++cc) {
                        String[] ptx1 = strings[cc].split(",");
                        String[] ptx2 = strings[cc + 1].split(",");
                        com.esri.arcgisruntime.demos.displaymap.Point pointF = new com.esri.arcgisruntime.demos.displaymap.Point(Double.valueOf(ptx1[0]), Double.valueOf(ptx1[1]));
                        com.esri.arcgisruntime.demos.displaymap.Point pointF1 = new com.esri.arcgisruntime.demos.displaymap.Point(Double.valueOf(ptx2[0]), Double.valueOf(ptx2[1]));
                        PointF theTouchPt = pt1;
                        if (deltas == 0) {
                            double thedis = lineUtil.getDistance(lineUtil.getLineNormalEquation(pointF.getX(), pointF.getY(), pointF1.getX(), pointF1.getY()), theTouchPt);
                            Log.w(TAG, "ParseWhiteblanklinesToMultilines: " + j + thedis + ", " + deltas);
                            if (thedis <= (lineUtil.getDistance1(pointF, pt1) >= lineUtil.getDistance1(pointF1, pt1) ? lineUtil.getDistance1(pointF, pt1) : lineUtil.getDistance1(pointF1, pt1)) && thedis >= (lineUtil.getDistance1(pointF, pt1) >= lineUtil.getDistance1(pointF1, pt1) ? lineUtil.getDistance1(pointF1, pt1) : lineUtil.getDistance1(pointF, pt1))) {
                                deltas = thedis;
                                ChoosedObjectID = whiteblanks.get(j).getObjectID();
                            } else {
                                deltas = (lineUtil.getDistance1(pointF, pt1) >= lineUtil.getDistance1(pointF1, pt1) ? lineUtil.getDistance1(pointF1, pt1) : lineUtil.getDistance1(pointF, pt1));
                                ChoosedObjectID = whiteblanks.get(j).getObjectID();
                            }
                        } else {
                            double delta1 = lineUtil.getDistance(lineUtil.getLineNormalEquation(pointF.getX(), pointF.getY(), pointF1.getX(), pointF1.getY()), theTouchPt);
                            if (delta1 <= (lineUtil.getDistance1(pointF, pt1) >= lineUtil.getDistance1(pointF1, pt1) ? lineUtil.getDistance1(pointF, pt1) : lineUtil.getDistance1(pointF1, pt1)) && delta1 >= (lineUtil.getDistance1(pointF, pt1) >= lineUtil.getDistance1(pointF1, pt1) ? lineUtil.getDistance1(pointF1, pt1) : lineUtil.getDistance1(pointF, pt1))) {
                            } else
                                delta1 = (lineUtil.getDistance1(pointF, pt1) >= lineUtil.getDistance1(pointF1, pt1) ? lineUtil.getDistance1(pointF1, pt1) : lineUtil.getDistance1(pointF, pt1));
                            Log.w(TAG, "ParseWhiteblanklinesToMultilines: " + j + delta1 + ", " + deltas);
                            if (delta1 < deltas) {
                                deltas = delta1;
                                ChoosedObjectID = whiteblanks.get(j).getObjectID();
                            }
                        }
                    }
            }
            if (deltas < 1000000) {
                return  ChoosedObjectID;
            }
        }
        return "";
    }

    private void ShowTextButton(){
        /*Button AddPOIBT = findViewById(R.id.AddPOIBT);
        AddPOIBT.setVisibility(View.VISIBLE);
        AddPOIBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddPOIFunction();
            }
        });
        Button DrawPolygonQueryBT = findViewById(R.id.drawPolygonQueryBT);
        DrawPolygonQueryBT.setVisibility(View.VISIBLE);
        DrawPolygonQueryBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NeedQueryFunction();
            }
        });
        Button DistanceMessureBT = findViewById(R.id.DistanceMessureBT);
        DistanceMessureBT.setVisibility(View.VISIBLE);
        DistanceMessureBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DistanceMessureFunction();
            }
        });
        Button AreaMessureBT = findViewById(R.id.AreaMessureBT);
        AreaMessureBT.setVisibility(View.VISIBLE);
        AreaMessureBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AreaMessureFunction();
            }
        });*/
        FloatingActionButton AddPOIFAB = findViewById(R.id.AddPOIFAB);
        AddPOIFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddPOIFunction();
            }
        });
        FloatingActionButton XZQQueryFAB = findViewById(R.id.XZQQueryFAB);
        XZQQueryFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RunningFunction = DisplayEnum.FUNC_ANA;
                RunningAnalyseFunction = DisplayEnum.ANA_XZQ;
                showPopueWindowForxzqTree();
            }
        });
        FloatingActionButton NeedQueryFAB = findViewById(R.id.NeedQueryFAB);
        NeedQueryFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NeedQueryFunction();
            }
        });
        FloatingActionButton DistanceMessureFAB = findViewById(R.id.DistanceMessureFAB);
        DistanceMessureFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DistanceMessureFunction();
            }
        });
        FloatingActionButton AreaMessureFAB = findViewById(R.id.AreaMessureFAB);
        AreaMessureFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AreaMessureFunction();
            }
        });
    }

    private void RemoveTextButton(){
        /*Button AddPOIBT = findViewById(R.id.AddPOIBT);
        AddPOIBT.setVisibility(View.INVISIBLE);
        Button DrawPolygonQueryBT = findViewById(R.id.drawPolygonQueryBT);
        DrawPolygonQueryBT.setVisibility(View.INVISIBLE);
        Button DistanceMessureBT = findViewById(R.id.DistanceMessureBT);
        DistanceMessureBT.setVisibility(View.INVISIBLE);
        Button AreaMessureBT = findViewById(R.id.AreaMessureBT);
        AreaMessureBT.setVisibility(View.INVISIBLE);*/
        FloatingActionButton AddPOIFAB = findViewById(R.id.AddPOIFAB);
        AddPOIFAB.setVisibility(View.INVISIBLE);
        FloatingActionButton XZQQueryFAB = findViewById(R.id.XZQQueryFAB);
        XZQQueryFAB.setVisibility(View.INVISIBLE);
        FloatingActionButton NeedQueryFAB = findViewById(R.id.NeedQueryFAB);
        NeedQueryFAB.setVisibility(View.INVISIBLE);
        FloatingActionButton DistanceMessureFAB = findViewById(R.id.DistanceMessureFAB);
        DistanceMessureFAB.setVisibility(View.INVISIBLE);
        FloatingActionButton AreaMessureFAB = findViewById(R.id.AreaMessureFAB);
        AreaMessureFAB.setVisibility(View.INVISIBLE);
    }

    private void AddPOIFunction(){
        RunningFunction = DisplayEnum.FUNC_ADDPOI;
        removeStandardWidget();
    }

    private void NeedQueryFunction(){
        // TODO DFASASDF
        if (DrawType == DisplayEnum.DRAW_NONE) {
            mCallout.dismiss();
            pieChartView.setVisibility(View.GONE);
            IsDrawedTuban = true;
            analyseFunction();
        }
        else {
            if (DrawType == DisplayEnum.DRAW_POLYGON && pointCollection.size() >= 3){
                final Polygon polygon = new Polygon(pointCollection);
                QueryParameters query = new QueryParameters();
                query.setGeometry(polygon);// 设置空间几何对象
                if (isFileExist(StaticVariableEnum.YSZRZYROOTPATH) && MapQuery) {
                    //FeatureLayer featureLayer=(FeatureLayer) mMapView.getMap().getOperationalLayers().get(10);
                    queryTaskForPolygon(query, polygon);
                } else
                    Toast.makeText(MainActivity.this, R.string.QueryError_2, Toast.LENGTH_SHORT).show();
                if (!inMap) mCallout.dismiss();

            }else if (DrawType == DisplayEnum.DRAW_POLYLINE && pointCollection.size() >= 2){
                Polyline polyline = new Polyline(pointCollection);
                GraphicsOverlay graphicsOverlay_1 = new GraphicsOverlay();
                SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.GREEN, 3);
                Graphic fillGraphic = new Graphic(polyline, lineSymbol);
                graphicsOverlay_1.getGraphics().add(fillGraphic);
                mMapView.getGraphicsOverlays().add(graphicsOverlay_1);
                Log.w(TAG, "onClick: " + pointCollection.size());
                Log.w(TAG, "onClick: " + GeometryEngine.lengthGeodetic(polyline, new LinearUnit(LinearUnitId.METERS), GeodeticCurveType.GEODESIC));
                DecimalFormat format = new DecimalFormat("0.00");
                Toast.makeText(MainActivity.this, format.format(GeometryEngine.lengthGeodetic(new Polyline(pointCollection, SpatialReference.create(4523)), new LinearUnit(LinearUnitId.METERS), GeodeticCurveType.GEODESIC)) + "米", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(MainActivity.this, "请构建面(至少三个点)后进行查询", Toast.LENGTH_SHORT).show();
            }
            DrawType = DisplayEnum.DRAW_NONE;
            mapQueryBtEvent();
        }
    }

    private void DistanceMessureFunction(){
        RunningFunction = DisplayEnum.FUNC_ANA;
        DrawType = DisplayEnum.DRAW_POLYLINE;
        QueryProcessType = DisplayEnum.INQUERY;
        pointCollection = new PointCollection(SpatialReference.create(4523));
        RunningAnalyseFunction = DisplayEnum.ANA_DISTANCE;
        showQueryWidget();
        removeStandardWidget();
    }

    private void AreaMessureFunction(){
        RunningFunction = DisplayEnum.FUNC_ANA;
        DrawType = DisplayEnum.DRAW_POLYGON;
        QueryProcessType = DisplayEnum.INQUERY;
        pointCollection = new PointCollection(SpatialReference.create(4523));
        RunningAnalyseFunction = DisplayEnum.ANA_AREA;
        showQueryWidget();
        removeStandardWidget();
    }

    private void showPopueWindowForMessure(){
        View popView = View.inflate(this,R.layout.popupwindow_drawfeature,null);
        Button bt_polygon = (Button) popView.findViewById(R.id.btn_pop_drawpolygon);
        final Button bt_xzq = (Button) popView.findViewById(R.id.btn_pop_xzq);
        final Button bt_distance = (Button) popView.findViewById(R.id.btn_pop_distance);
        final Button bt_area = (Button) popView.findViewById(R.id.btn_pop_area);
        final Button bt_addpoi = (Button) popView.findViewById(R.id.btn_pop_addpoi);
        //final Button bt_point = (Button) popView.findViewById(R.id.btn_pop_drawpoint);
        Button bt_cancle = (Button) popView.findViewById(R.id.btn_pop_cancel);
        //获取屏幕宽高
        int weight = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels * 2/3;

        final PopupWindow popupWindow = new PopupWindow(popView, weight, height);
        //popupWindow.setAnimationStyle(R.style.anim_popup_dir);
        popupWindow.setFocusable(true);
        //点击外部popueWindow消失
        popupWindow.setOutsideTouchable(true);
        bt_polygon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IsDrawedTuban = true;
                analyseFunction();
                popupWindow.dismiss();
            }
        });
        bt_xzq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent(MainActivity.this, chartshow.class);
                startActivity(intent);*/

                RunningFunction = DisplayEnum.FUNC_ANA;
                RunningAnalyseFunction = DisplayEnum.ANA_XZQ;
                showPopueWindowForxzqTree();
                popupWindow.dismiss();
            }
        });
        bt_distance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RunningFunction = DisplayEnum.FUNC_ANA;
                DrawType = DisplayEnum.DRAW_POLYLINE;
                QueryProcessType = DisplayEnum.INQUERY;
                pointCollection = new PointCollection(SpatialReference.create(4523));
                RunningAnalyseFunction = DisplayEnum.ANA_DISTANCE;
                showQueryWidget();
                removeStandardWidget();
                popupWindow.dismiss();
            }
        });
        bt_area.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RunningFunction = DisplayEnum.FUNC_ANA;
                DrawType = DisplayEnum.DRAW_POLYGON;
                QueryProcessType = DisplayEnum.INQUERY;
                pointCollection = new PointCollection(SpatialReference.create(4523));
                RunningAnalyseFunction = DisplayEnum.ANA_AREA;
                showQueryWidget();
                removeStandardWidget();
                popupWindow.dismiss();
            }
        });
        bt_addpoi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RunningFunction = DisplayEnum.FUNC_ADDPOI;
                removeStandardWidget();
                //showPopueWindowForPhoto();
                popupWindow.dismiss();
            }
        });

        /*bt_point.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawType = DRAW_POINT;
                popupWindow.dismiss();
            }
        });*/

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

    private void AddPhoto(final Uri uri, final float[] latandlong, final int num) {
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(MainActivity.this.getResources().getText(R.string.DateAndTime).toString());
        final Date date = new Date(System.currentTimeMillis());
        CreatePOI = true;
        POIType = num;
        final List<POI> POIs = LitePal.where("type = ?", strings[num]).find(POI.class);
        int size = POIs.size();
        if (size > 0) {
            float K = (float) 0.002;
            float delta = Math.abs(POIs.get(0).getX() - latandlong[0]) + Math.abs(POIs.get(0).getY() - latandlong[1]);
            for (int i = 0; i < size; ++i) {
                float theLat = POIs.get(i).getX();
                float theLong = POIs.get(i).getY();
                float delta1 = Math.abs(theLat - latandlong[0]) + Math.abs(theLong - latandlong[1]);
                if (delta1 < delta && delta1 < K) {
                    delta = delta1;
                    theNum = i;
                }
            }
            if (delta < K) {
                AlertDialog.Builder dialog1 = new AlertDialog.Builder(MainActivity.this);
                dialog1.setTitle("提示");
                dialog1.setMessage("你想怎样添加照片");
                dialog1.setCancelable(false);
                dialog1.setPositiveButton("合并到<" + POIs.get(theNum).getName() + ">点图集", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        POI poi = new POI();
                        poi.setPhotonum(POIs.get(theNum).getPhotonum() + 1);
                        DataUtil.addPhotoToDB(DataUtil.getRealPathFromUriForPhoto(MainActivity.this, uri), ic, POIs.get(theNum).getPoic(), simpleDateFormat.format(date));
                        getNormalBitmap();
                        updateMapPage(POIs.get(theNum).getPoic(), num);
                    }
                });
                dialog1.setNegativeButton("创建新兴趣点", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        long time = System.currentTimeMillis();
                        String poic = "POI" + String.valueOf(time);
                        DataUtil.addPOI(ic, poic, "图片POI" + String.valueOf(POIs.size() + 1), latandlong[0], latandlong[1], simpleDateFormat.format(date), num);
                        DataUtil.addPhotoToDB(DataUtil.getRealPathFromUriForPhoto(MainActivity.this, uri), ic, poic, simpleDateFormat.format(date));
                        getNormalBitmap();
                        updateMapPage(poic, num);
                    }
                });
                dialog1.show();
            } else {
                long time = System.currentTimeMillis();
                String poic = "POI" + String.valueOf(time);
                DataUtil.addPOI(ic, poic, "图片POI" + String.valueOf(POIs.size() + 1), latandlong[0], latandlong[1], simpleDateFormat.format(date), num);
                DataUtil.addPhotoToDB(DataUtil.getRealPathFromUriForPhoto(MainActivity.this, uri), ic, poic, simpleDateFormat.format(date));
                getNormalBitmap();
                updateMapPage(poic, num);
            }
        } else {
            long time = System.currentTimeMillis();
            String poic = "POI" + String.valueOf(time);
            DataUtil.addPOI(ic, poic, "图片POI" + String.valueOf(POIs.size() + 1), latandlong[0], latandlong[1], simpleDateFormat.format(date), num);
            DataUtil.addPhotoToDB(DataUtil.getRealPathFromUriForPhoto(MainActivity.this, uri), ic, poic, simpleDateFormat.format(date));
            getNormalBitmap();
            updateMapPage(poic, num);
        }
        POIType = -1;
        CreatePOI = false;
    }

    private void AddTape(final Uri uri, final int num) {
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(MainActivity.this.getResources().getText(R.string.DateAndTime).toString());
        final Date date = new Date(System.currentTimeMillis());
        theNum = 0;
        final long time = System.currentTimeMillis();
        final List<POI> POIs = LitePal.where("type = ?", strings[num]).find(POI.class);
        int size = POIs.size();
        if (size > 0) {
            float K = (float) 0.002;
            float delta = Math.abs(POIs.get(0).getX() - (float) m_lat) + Math.abs(POIs.get(0).getY() - (float) m_long);
            for (int i = 0; i < size; ++i) {
                float theLat = POIs.get(i).getX();
                float theLong = POIs.get(i).getY();
                float delta1 = Math.abs(theLat - (float) m_lat) + Math.abs(theLong - (float) m_long);
                if (delta1 < delta && delta1 < K) {
                    delta = delta1;
                    theNum = i;
                }
            }
            if (delta < K) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                dialog.setTitle("提示");
                dialog.setMessage("你想怎样添加音频");
                dialog.setCancelable(false);
                dialog.setPositiveButton("合并到<" + POIs.get(theNum).getName() + ">点音频集", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        POI poi = new POI();
                        poi.setTapenum(POIs.get(theNum).getTapenum() + 1);
                        poi.updateAll("poic = ?", POIs.get(theNum).getPoic());
                        DataUtil.addTapeToDB(DataUtil.getRealPathFromUriForAudio(MainActivity.this, uri), ic, POIs.get(theNum).getPoic(), simpleDateFormat.format(date));
                        updateMapPage(POIs.get(theNum).getPoic(), num);
                    }
                });
                dialog.setNegativeButton("创建新兴趣点", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String POIC = "POI" + String.valueOf(time);
                        //List<POI> POIs = LitePal.where("ic = ?", ic).find(POI.class);
                        //List<POI> POIs = LitePal.findAll(POI.class);
                        DataUtil.addPOI(ic, POIC, "录音POI" + String.valueOf(POIs.size() + 1), (float) m_lat, (float) m_long, simpleDateFormat.format(date), num);
                        DataUtil.addTapeToDB(DataUtil.getRealPathFromUriForAudio(MainActivity.this, uri), ic, POIC, simpleDateFormat.format(date));
                        updateMapPage(POIC, num);
                    }
                });
                dialog.show();
            } else {
                String POIC = "POI" + String.valueOf(time);
                //List<POI> POIs = LitePal.where("ic = ?", ic).find(POI.class);
                //List<POI> POIs = LitePal.findAll(POI.class);
                DataUtil.addPOI(ic, POIC, "录音POI" + String.valueOf(POIs.size() + 1), (float) m_lat, (float) m_long, simpleDateFormat.format(date), num);
                DataUtil.addTapeToDB(DataUtil.getRealPathFromUriForAudio(this, uri), ic, POIC, simpleDateFormat.format(date));
                updateMapPage(POIC, num);
            }
        } else {
            String POIC = "POI" + String.valueOf(time);
            //List<POI> POIs = LitePal.where("ic = ?", ic).find(POI.class);
            //List<POI> POIs = LitePal.findAll(POI.class);
            DataUtil.addPOI(ic, POIC, "录音POI" + String.valueOf(POIs.size() + 1), (float) m_lat, (float) m_long, simpleDateFormat.format(date), num);
            DataUtil.addTapeToDB(DataUtil.getRealPathFromUriForAudio(MainActivity.this, uri), ic, POIC, simpleDateFormat.format(date));
            updateMapPage(POIC, num);
        }
    }

    private void AddTakePhoto(final String imageuri, final float[] latandlong, final int num) {
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(MainActivity.this.getResources().getText(R.string.DateAndTime).toString());
        final Date date = new Date(System.currentTimeMillis());
        final long time = System.currentTimeMillis();
        final List<POI> POIs = LitePal.where("type = ?", strings[num]).find(POI.class);
        int size = POIs.size();
        if (size > 0) {
            float K = (float) 0.002;
            float delta = Math.abs(POIs.get(0).getX() - (float) m_lat) + Math.abs(POIs.get(0).getY() - (float) m_long);
            for (int i = 0; i < size; ++i) {
                float theLat = POIs.get(i).getX();
                float theLong = POIs.get(i).getY();
                float delta1 = Math.abs(theLat - (float) m_lat) + Math.abs(theLong - (float) m_long);
                if (delta1 < delta && delta1 < K) {
                    delta = delta1;
                    theNum = i;
                }
            }
            if (delta < K) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                dialog.setTitle("提示");
                dialog.setMessage("你想怎样添加照片");
                dialog.setCancelable(false);
                dialog.setPositiveButton("合并到<" + POIs.get(theNum).getName() + ">点图集", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        POI poi = new POI();
                        poi.setPhotonum(POIs.get(theNum).getPhotonum() + 1);
                        poi.updateAll("poic = ?", POIs.get(theNum).getPoic());
                        Date date = new Date(time);
                        DataUtil.addPhotoToDB(imageuri, ic, POIs.get(theNum).getPoic(), simpleDateFormat.format(date));
                        getNormalBitmap();
                        updateMapPage(POIs.get(theNum).getPoic(), num);
                    }
                });
                dialog.setNegativeButton("创建新兴趣点", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //long time = System.currentTimeMillis();
                        String poic = "POI" + String.valueOf(time);
                        if (latandlong[0] != 0 && latandlong[1] != 0) {
                            DataUtil.addPOI(ic, poic, "图片POI" + String.valueOf(POIs.size() + 1), latandlong[0], latandlong[1], simpleDateFormat.format(date), num);
                        } else {
                            DataUtil.addPOI(ic, poic, "图片POI" + String.valueOf(POIs.size() + 1), (float) m_lat, (float) m_long, simpleDateFormat.format(date), num);
                        }
                        DataUtil.addPhotoToDB(imageuri, ic, poic, simpleDateFormat.format(date));
                        getNormalBitmap();
                        updateMapPage(poic, num);
                    }
                });
                dialog.show();
            } else {
                //List<POI> POIs = LitePal.findAll(POI.class);
                //long time = System.currentTimeMillis();
                String poic = "POI" + String.valueOf(time);
                if (latandlong[0] != 0 && latandlong[1] != 0) {
                    DataUtil.addPOI(ic, poic, "图片POI" + String.valueOf(POIs.size() + 1), latandlong[0], latandlong[1], simpleDateFormat.format(date), num);
                } else {
                    DataUtil.addPOI(ic, poic, "图片POI" + String.valueOf(POIs.size() + 1), (float) m_lat, (float) m_long, simpleDateFormat.format(date), num);
                }
                DataUtil.addPhotoToDB(imageuri, ic, poic, simpleDateFormat.format(date));
                getNormalBitmap();
                updateMapPage(poic, num);
            }
        } else {
            String poic = "POI" + String.valueOf(time);
            if (latandlong[0] != 0 && latandlong[1] != 0) {
                DataUtil.addPOI(ic, poic, "图片POI" + String.valueOf(POIs.size() + 1), latandlong[0], latandlong[1], simpleDateFormat.format(date), num);
            } else {
                DataUtil.addPOI(ic, poic, "图片POI" + String.valueOf(POIs.size() + 1), (float) m_lat, (float) m_long, simpleDateFormat.format(date), num);
            }
            DataUtil.addPhotoToDB(imageuri, ic, poic, simpleDateFormat.format(date));
            getNormalBitmap();
            updateMapPage(poic, num);
        }
    }

    private void updateMapPage(String poic, int num) {
        /*Intent intent = new Intent(MainActivity.this, singlepoi.class);
        intent.putExtra("POIC", poic);
        startActivity(intent);*/
        Log.w(TAG, "updateMapPage: ");
        GoNormalSinglePOIPage(poic);
    }

    public void getNormalBitmap() {
        ////////////////////////缓存Bitmap//////////////////////////////
        bts = new ArrayList<bt>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                bts.clear();
                List<POI> pois = LitePal.findAll(POI.class);
                if (pois.size() > 0) {
                    for (POI poi : pois) {
                        List<MPHOTO> mphotos = LitePal.where("poic = ?", poi.getPoic()).find(MPHOTO.class);
                        //PointF pt2 = LatLng.getPixLocFromGeoL(new PointF(poi.getX(), poi.getY()));
                        //canvas.drawRect(new RectF(pt2.x - 5, pt2.y - 38, pt2.x + 5, pt2.y), paint2);
                        //locError(Boolean.toString(poi.getPath().isEmpty()));
                        //locError(Integer.toString(poi.getPath().length()));
                        //locError(poi.getPath());
                        if (poi.getPhotonum() != 0 && mphotos.size() != 0) {
                            String path = mphotos.get(0).getPath();
                            File file = new File(path);
                            if (file.exists()) {
                                Bitmap bitmap = DataUtil.getImageThumbnail(path, 100, 80);
                                if (mphotos.size() != 0) {
                                    int degree = DataUtil.getPicRotate(path);
                                    if (degree != 0) {
                                        Matrix m = new Matrix();
                                        m.setRotate(degree); // 旋转angle度
                                        Log.w(TAG, "showPopueWindowForPhoto: " + degree);
                                        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
                                    }
                                    Log.w(TAG, "imageUri: " + path);
                                    bt btt = new bt(bitmap, path);
                                    btt.setPoic(poi.getPoic());
                                    bts.add(btt);
                                }
                            } else {
                                Log.w(TAG, "imageUriWithWrongPath: " + path);
                                //Resources res = MyApplication.getContext().getResources();
                                //Bitmap bmp = BitmapFactory.decodeResource(res, R.drawable.ic_info_black);
                                Drawable drawable = MyApplication.getContext().getResources().getDrawable(R.drawable.imgerror);
                                BitmapDrawable bd = (BitmapDrawable) drawable;
                                Bitmap bitmap = Bitmap.createBitmap(bd.getBitmap(), 0, 0, bd.getBitmap().getWidth(), bd.getBitmap().getHeight());
                                bitmap = ThumbnailUtils.extractThumbnail(bitmap, 80, 120,
                                        ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
                                bt btt = new bt(bitmap, path);
                                btt.setPoic("11");
                                bts.add(btt);
                            }
                        } else {
                            POI poi1 = new POI();
                            if (mphotos.size() != 0) poi1.setPhotonum(mphotos.size());
                            else poi1.setToDefault("photonum");
                            poi1.updateAll("poic = ?", poi.getPoic());
                        }
                    }
                }
                isCreateBitmap = true;
                /*runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            pdfView.zoomWithAnimation(c_zoom);
                        }catch (Exception e){

                        }
                    }
                });*/
            }
        }).start();
        //////////////////////////////////////////////////////////////////
    }

    private void GoNormalSinglePOIPage(String poic) {
        Log.w(TAG, "updateMapPage: 0");
        if (!poic.isEmpty()) {
            Intent intent = new Intent(MainActivity.this, singlepoi.class);
            intent.putExtra("POIC", poic);
            intent.putExtra("type", 0);
            startActivity(intent);
        }
    }

    private void GoDMBZSinglePOIPage(String XH) {
        Log.w(TAG, "updateMapPage: 1");
        Intent intent = new Intent(MainActivity.this, singlepoi.class);
        intent.putExtra("DMBZ", XH);
        intent.putExtra("type", 1);
        startActivity(intent);
    }

    private void GoDMLSinglePOIPage(String MapId) {
        Log.w(TAG, "updateMapPage: 1");
        Intent intent = new Intent(MainActivity.this, singlepoi.class);
        intent.putExtra("DML", MapId);
        intent.putExtra("type", 2);
        startActivity(intent);
    }

    private void GoDMPSinglePOIPage(String MapId) {
        Log.w(TAG, "updateMapPage: 1");
        Intent intent = new Intent(MainActivity.this, singlepoi.class);
        intent.putExtra("DMP", MapId);
        intent.putExtra("type", 3);
        startActivity(intent);
    }

    private void queryTask(final QueryParameters query, final Polygon polygon){
        try {
            FeatureTable mTable = null;
            /*if (QueriedFeature == DisplayEnum.TDGHDL_FEATURE)
                mTable = TDGHDLFeatureLayer.getFeatureTable();//得到查询属性表
            else
                mTable = XZQFeatureLayer.getFeatureTable();//得到查询属性表*/
            switch (QueriedFeature){
                case TDGHDL_FEATURE:
                    mTable = TDGHDLFeatureLayer.getFeatureTable();//得到查询属性表
                    break;
                case XZQ_FEATURE:
                    mTable = XZCAreaFeatureLayer.getFeatureTable();//得到查询属性表
                    break;
                case PTB_FEATURE:
                    mTable = PTuBanFeatureLayer.getFeatureTable();//得到查询属性表
                    break;
                case DLTB09_FEATURE:
                    mTable = DLTB2009FeatureLayer.getFeatureTable();//得到查询属性表
                    break;
                case DLTB16_FEATURE:
                    mTable = DLTB2016FeatureLayer.getFeatureTable();//得到查询属性表
                    break;
                case DLTB17_FEATURE:
                    mTable = DLTB2017FeatureLayer.getFeatureTable();//得到查询属性表
                    break;
            }
            if (pointCollection.size() >= 3) {
                final ListenableFuture<FeatureQueryResult> featureQueryResult
                        = mTable.queryFeaturesAsync(query);
                featureQueryResult.addDoneListener(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            removeGraphicsOverlayers();
                            FeatureQueryResult featureResul = featureQueryResult.get();
                            Geometry geometry1 = polygon;
                            List<QueryTaskInfo> queryTaskInfos = new ArrayList<>();
                            for (Object element : featureResul) {
                                if (element instanceof Feature) {
                                    Feature mFeatureGrafic = (Feature) element;
                                    Geometry geometry = null;
                                    Polygon polygon1 = (Polygon) mFeatureGrafic.getGeometry();
                                    geometry = GeometryEngine.intersection(GeometryEngine.project(polygon1, SpatialReference.create(4523)), polygon);
                                    boolean isOK = false;
                                    QueryTaskInfo queryTaskInfo = new QueryTaskInfo(GeometryEngine.areaGeodetic(geometry, new AreaUnit(AreaUnitId.SQUARE_KILOMETERS), GeodeticCurveType.GEODESIC) * 1500);
                                    Log.w(TAG, "geometry2type: " + queryTaskInfo.getArea());
                                    //Log.w(TAG, "run: " + geometry1.getSpatialReference().getWkid());
                                    GraphicsOverlay graphicsOverlay_1 = new GraphicsOverlay();
                                    SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.BLUE, 3);
                                    Graphic fillGraphic = new Graphic(geometry, lineSymbol);
                                    graphicsOverlay_1.getGraphics().add(fillGraphic);
                                    mMapView.getGraphicsOverlays().add(graphicsOverlay_1);
                                    Map<String, Object> mQuerryString = mFeatureGrafic.getAttributes();
                                    //calloutContent.setSingleLine();
                                    // format coordinates to 4 decimal places
                                    //String str = "";
                                    //List<KeyAndValue> keyAndValues = new ArrayList<>();
                                    switch (QueriedFeature){
                                        case TDGHDL_FEATURE:
                                            for (String key : mQuerryString.keySet()) {
                                                //str = str + key + " : " + String.valueOf(mQuerryString.get(key)) + "\n";
                                                switch (key){
                                                    case "GHDLMC":
                                                        queryTaskInfo.setTypename(String.valueOf(mQuerryString.get(key)));
                                                        break;
                                                    case "GHDLBM":
                                                        queryTaskInfo.setType(String.valueOf(mQuerryString.get(key)));
                                                        break;
                                                    case "XZQMC":
                                                        queryTaskInfo.setXzq(String.valueOf(mQuerryString.get(key)));
                                                        break;
                                                    case "XZQDM":
                                                        queryTaskInfo.setXzqdm(String.valueOf(mQuerryString.get(key)));
                                                        break;
                                                }
                                                isOK = true;
                                            }
                                            break;
                                        case XZQ_FEATURE:
                                            for (String key : mQuerryString.keySet()) {
                                                //Log.w(TAG, "行政区: " + key);
                                                //str = str + key + " : " + String.valueOf(mQuerryString.get(key)) + "\n";
                                                if (key.equals("XZQDM")) {
                                                    //Log.w(TAG, "行政区界线: " + String.valueOf(mQuerryString.get(key)));
                                                    //Log.w(TAG, "行政区界线 面积: " + queryTaskInfo.getArea());
                                                    String str = String.valueOf(mQuerryString.get(key));
                                                    if (str.length() >= 9) {
                                                        queryTaskInfo.setXzqdm(str);
                                                        isOK = true;
                                                    } else
                                                        break;
                                                } else if (key.equals("XZQMC")) {
                                                    queryTaskInfo.setXzq(String.valueOf(mQuerryString.get(key)));
                                                }/*else {
                                                queryTaskInfo.setXzq(String.valueOf(mQuerryString.get(key)));
                                            }*/
                                            }
                                            break;
                                        case PTB_FEATURE:
                                            for (String key : mQuerryString.keySet()) {
                                                Log.w(TAG, "行政区: " + key);
                                                //str = str + key + " : " + String.valueOf(mQuerryString.get(key)) + "\n";
                                                if (key.equals("XJXZQHDM")) {
                                                    //Log.w(TAG, "行政区界线: " + String.valueOf(mQuerryString.get(key)));
                                                    //Log.w(TAG, "行政区界线 面积: " + queryTaskInfo.getArea());
                                                    String str = String.valueOf(mQuerryString.get(key));
                                                    queryTaskInfo.setXzqdm(str);
                                                    isOK = true;
                                                } else if (key.equals("BZPZWH")) {
                                                    queryTaskInfo.setXzq(String.valueOf(mQuerryString.get(key)));
                                                }
                                            }
                                            break;
                                        default:
                                            for (String key : mQuerryString.keySet()) {
                                                Log.w(TAG, "行政区: " + key);
                                                //str = str + key + " : " + String.valueOf(mQuerryString.get(key)) + "\n";
                                                if (key.equals("DLBM")) {
                                                    //Log.w(TAG, "行政区界线: " + String.valueOf(mQuerryString.get(key)));
                                                    //Log.w(TAG, "行政区界线 面积: " + queryTaskInfo.getArea());
                                                    String str = String.valueOf(mQuerryString.get(key));
                                                    queryTaskInfo.setXzqdm(str);
                                                    isOK = true;
                                                } else if (key.equals("DLMC")) {
                                                    queryTaskInfo.setXzq(String.valueOf(mQuerryString.get(key)));
                                                }
                                            }
                                            break;
                                    }
                                    if (isOK)
                                        queryTaskInfos.add(queryTaskInfo);

                                }
                            }
                            wholeArea = GeometryEngine.areaGeodetic(geometry1, new AreaUnit(AreaUnitId.SQUARE_KILOMETERS), GeodeticCurveType.GEODESIC) * 1500;
                            GraphicsOverlay graphicsOverlay_1 = new GraphicsOverlay();
                            SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.RED, 3);
                            Graphic fillGraphic = new Graphic(geometry1, lineSymbol);
                            graphicsOverlay_1.getGraphics().add(fillGraphic);
                            mMapView.getGraphicsOverlays().add(graphicsOverlay_1);
                            List<SliceValue> sliceValues = new ArrayList<>();
                            TextView calloutContent = new TextView(getApplicationContext());
                            calloutContent.setTextColor(Color.BLACK);
                            String str = "";
                            keyAndValues = new ArrayList<>();

                            if (QueriedFeature == DisplayEnum.TDGHDL_FEATURE) {
                                for (int i = 0; i < queryTaskInfos.size(); ++i) {
                                    if (i == 0 && queryTaskInfos.get(i).getArea() != 0)
                                        keyAndValues.add(new KeyAndValue(queryTaskInfos.get(i).getTypename(), Double.toString(queryTaskInfos.get(i).getArea())));
                                    else {
                                        boolean hasKey = false;
                                        for (int j = 0; j < keyAndValues.size(); ++j) {
                                            if (queryTaskInfos.get(i).getTypename().equals(keyAndValues.get(j).getName())) {
                                                hasKey = true;
                                                keyAndValues.get(j).setValue(Double.toString(Double.valueOf(keyAndValues.get(j).getValue()) + queryTaskInfos.get(i).getArea()));
                                                break;
                                            }
                                        }
                                        if (!hasKey && queryTaskInfos.get(i).getArea() != 0) {
                                            keyAndValues.add(new KeyAndValue(queryTaskInfos.get(i).getTypename(), Double.toString(queryTaskInfos.get(i).getArea())));
                                        }
                                    }
                                }
                            } else {
                                for (int i = 0; i < queryTaskInfos.size(); ++i) {
                                    if (i == 0 && queryTaskInfos.get(i).getArea() != 0)
                                        keyAndValues.add(new KeyAndValue(queryTaskInfos.get(i).getXzq(), Double.toString(queryTaskInfos.get(i).getArea())));
                                    else {
                                        boolean hasKey = false;
                                        for (int j = 0; j < keyAndValues.size(); ++j) {
                                            if (queryTaskInfos.get(i).getXzq().equals(keyAndValues.get(j).getName())) {
                                                hasKey = true;
                                                keyAndValues.get(j).setValue(Double.toString(Double.valueOf(keyAndValues.get(j).getValue()) + queryTaskInfos.get(i).getArea()));
                                                break;
                                            }
                                        }
                                        if (!hasKey && queryTaskInfos.get(i).getArea() != 0) {
                                            keyAndValues.add(new KeyAndValue(queryTaskInfos.get(i).getXzq(), Double.toString(queryTaskInfos.get(i).getArea())));
                                        }
                                    }
                                }
                            }

                            DecimalFormat decimalFormat = new DecimalFormat("0.00");
                            DecimalFormat decimalFormat1 = new DecimalFormat("0.0");
                            for (int j = 0; j < keyAndValues.size(); ++j) {
                                float value = Float.valueOf(keyAndValues.get(j).getValue()) / (float) wholeArea;
                                SliceValue sliceValue = new SliceValue(value, ChartUtils.pickColor());
                                sliceValue.setLabel(keyAndValues.get(j).getName() + ":" + decimalFormat1.format(value * 100) + "%");
                                sliceValues.add(sliceValue);
                                if (j < keyAndValues.size() - 1) {
                                    str = str + keyAndValues.get(j).getName() + ": " + decimalFormat1.format(Double.valueOf(keyAndValues.get(j).getValue())) + "亩";
                                    str = str + ",占比: " + decimalFormat.format(Double.valueOf(keyAndValues.get(j).getValue()) / wholeArea * 100) + "%" + "\n";
                                } else {
                                    str = str + keyAndValues.get(j).getName() + ": " + decimalFormat1.format(Double.valueOf(keyAndValues.get(j).getValue())) + "亩";
                                    str = str + ",占比: " + decimalFormat.format(Double.valueOf(keyAndValues.get(j).getValue()) / wholeArea * 100) + "%";
                                }
                            }
                            calloutContent.setText(str);
                            // get callout, set content and show
                            mCallout.setLocation(new Point(geometry1.getExtent().getCenter().getX(), geometry1.getExtent().getYMax(), SpatialReference.create(4523)));
                            mCallout.setContent(calloutContent);
                            Log.w(TAG, "run: callout" + mCallout.isShowing());
                            mCallout.show();
                            inMap = true;
                            PieChartData pieChartData = new PieChartData(sliceValues);
                            pieChartData.setHasLabels(true);
                            pieChartData.setHasLabelsOutside(false);
                            pieChartData.setHasLabelsOnlyForSelected(true);
                            pieChartView.setOnValueTouchListener(new ValueTouchListener());
                            pieChartView.setPieChartData(pieChartData);
                            pieChartView.setValueSelectionEnabled(true);
                            pieChartView.setVisibility(View.VISIBLE);
                            FloatingActionButton change = (FloatingActionButton) findViewById(R.id.ChangeQuery);
                            change.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    switch (QueriedFeature){
                                        case TDGHDL_FEATURE:
                                            QueriedFeature = DisplayEnum.XZQ_FEATURE;
                                            break;
                                        case XZQ_FEATURE:
                                            QueriedFeature = DisplayEnum.PTB_FEATURE;
                                            break;
                                        case PTB_FEATURE:
                                            QueriedFeature = DisplayEnum.DLTB09_FEATURE;
                                            break;
                                        case DLTB09_FEATURE:
                                            QueriedFeature = DisplayEnum.DLTB16_FEATURE;
                                            break;
                                        case DLTB16_FEATURE:
                                            QueriedFeature = DisplayEnum.DLTB17_FEATURE;
                                            break;
                                        case DLTB17_FEATURE:
                                            QueriedFeature = DisplayEnum.TDGHDL_FEATURE;
                                            break;
                                    }
                                    keyAndValues.clear();
                                    mCallout.dismiss();
                                    pieChartView.setVisibility(View.GONE);
                                    queryTask(query, polygon);
                                }
                            });
                            /*pieChartView.setOnLongClickListener(new View.OnLongClickListener() {
                                @Override
                                public boolean onLongClick(View v) {
                                    return true;
                                }
                            });*/
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }else Toast.makeText(MainActivity.this, "三个点以上才可进行多边形查询，请重试！", Toast.LENGTH_SHORT).show();
        } catch (ArcGISRuntimeException e) {
            Toast.makeText(MainActivity.this, e.getMessage() + e.getErrorCode(), Toast.LENGTH_SHORT).show();
        }
    }

    private void queryTaskFor20200904ForBasePolygon(final QueryParameters query, final Polygon polygon){
        try {
            if (QueriedLayerIndex != -1 && QueriedLayerIndex < BaseLayerFieldsSheetList.size()) {
                FeatureTable mTable = LayerFieldsSheetList.get(QueriedLayerIndex).getFeatureLayer().getFeatureTable();
            /*if (QueriedFeature == DisplayEnum.TDGHDL_FEATURE)
                mTable = TDGHDLFeatureLayer.getFeatureTable();//得到查询属性表
            else
                mTable = XZQFeatureLayer.getFeatureTable();//得到查询属性表*/

                //if (pointCollection.size() >= 3)
                    {
                    final ListenableFuture<FeatureQueryResult> featureQueryResult
                            = mTable.queryFeaturesAsync(query);
                    featureQueryResult.addDoneListener(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                removeGraphicsOverlayers();
                                FeatureQueryResult featureResul = featureQueryResult.get();
                                AnalysisUserGeometry = polygon;
                                Geometry geometry1 = polygon;
                                List<QueryTaskInfo> queryTaskInfos = new ArrayList<>();
                                HashMap<String, ChartQueryResult> hashMap = new HashMap<>();
                                //遍历被查询到的要素
                                for (Object element : featureResul) {
                                    if (element instanceof Feature) {
                                        Feature mFeatureGrafic = (Feature) element;
                                        Geometry geometry = null;
                                        Polygon polygon1 = (Polygon) mFeatureGrafic.getGeometry();
                                        geometry = GeometryEngine.intersection(GeometryEngine.project(polygon1, SpatialReference.create(4523)), polygon);
                                        boolean isOK = false;
                                        Double area = GeometryEngine.areaGeodetic(geometry, new AreaUnit(AreaUnitId.SQUARE_KILOMETERS), GeodeticCurveType.GEODESIC) * 1500;
                                        QueryTaskInfo queryTaskInfo = new QueryTaskInfo(area);
                                        Log.w(TAG, "geometry2type For BasePolygon: " + queryTaskInfo.getArea());
                                        //Log.w(TAG, "run: " + geometry1.getSpatialReference().getWkid());
                                        GraphicsOverlay graphicsOverlay_1 = new GraphicsOverlay();
                                        SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.BLUE, 3);
                                        Graphic fillGraphic = new Graphic(geometry, lineSymbol);
                                        graphicsOverlay_1.getGraphics().add(fillGraphic);
                                        mMapView.getGraphicsOverlays().add(graphicsOverlay_1);
                                        Map<String, Object> mQuerryString = mFeatureGrafic.getAttributes();
                                        if (QueriedLayerIndex == 5 || QueriedLayerIndex == 6 || QueriedLayerIndex == 2) {
                                            for (String key : mQuerryString.keySet()) {
                                                if (key.equals("DLMC")) {
                                                    String mkey = String.valueOf(mQuerryString.get(key));
                                                    if (hashMap.containsKey(mkey)) {
                                                        ChartQueryResult cqr = hashMap.get(mkey);
                                                        Polygon p = cqr.getPolygon();
                                                        cqr.setPolygon((Polygon) GeometryEngine.union(p, geometry));
                                                        cqr.setArea(cqr.getArea() + area);
                                                    } else {
                                                        if (area > 0)
                                                            hashMap.put(mkey, new ChartQueryResult(mkey, area, (Polygon) geometry));
                                                    }
                                                    break;
                                                }
                                            }
                                        } else if (QueriedLayerIndex == 7) {
                                            String mkey = "";
                                            for (String key : mQuerryString.keySet()) {
                                                if (key.equals("CBFMC")) {
                                                    mkey += String.valueOf(mQuerryString.get(key));
                                                }
                                                else if (key.equals("FBFMC")) {
                                                    mkey += ", " + String.valueOf(mQuerryString.get(key));
                                                }
                                            }
                                            if (hashMap.containsKey(mkey)) {
                                                ChartQueryResult cqr = hashMap.get(mkey);
                                                Polygon p = cqr.getPolygon();
                                                cqr.setPolygon((Polygon) GeometryEngine.union(p, geometry));
                                                cqr.setArea(cqr.getArea() + area);
                                            } else {
                                                if (area > 0)
                                                    hashMap.put(mkey, new ChartQueryResult(mkey, area, (Polygon) geometry));
                                            }
                                        } else if (QueriedLayerIndex == 8) {
                                            for (String key : mQuerryString.keySet()) {
                                                if (key.equals("ZLDWMC")) {
                                                    String mkey = String.valueOf(mQuerryString.get(key));
                                                    if (hashMap.containsKey(mkey)) {
                                                        ChartQueryResult cqr = hashMap.get(mkey);
                                                        Polygon p = cqr.getPolygon();
                                                        cqr.setPolygon((Polygon) GeometryEngine.union(p, geometry));
                                                        cqr.setArea(cqr.getArea() + area);
                                                    } else {
                                                        if (area > 0)
                                                            hashMap.put(mkey, new ChartQueryResult(mkey, area, (Polygon) geometry));
                                                    }
                                                    break;
                                                }
                                            }
                                        } else if (QueriedLayerIndex == 4) {
                                            for (String key : mQuerryString.keySet()) {
                                                if (key.equals("PDJB")) {
                                                    String mkey = String.valueOf(mQuerryString.get(key));
                                                    if (hashMap.containsKey(mkey)) {
                                                        ChartQueryResult cqr = hashMap.get(mkey);
                                                        Polygon p = cqr.getPolygon();
                                                        cqr.setPolygon((Polygon) GeometryEngine.union(p, geometry));
                                                        cqr.setArea(cqr.getArea() + area);
                                                    } else {
                                                        if (area > 0)
                                                            hashMap.put(mkey, new ChartQueryResult(mkey, area, (Polygon) geometry));
                                                    }
                                                    break;
                                                }
                                            }
                                        } else if (QueriedLayerIndex == 3) {
                                            for (String key : mQuerryString.keySet()) {
                                                if (key.equals("XZQMC")) {
                                                    String mkey = String.valueOf(mQuerryString.get(key));
                                                    if (hashMap.containsKey(mkey)) {
                                                        ChartQueryResult cqr = hashMap.get(mkey);
                                                        Polygon p = cqr.getPolygon();
                                                        cqr.setPolygon((Polygon) GeometryEngine.union(p, geometry));
                                                        cqr.setArea(cqr.getArea() + area);
                                                    } else {
                                                        if (area > 0)
                                                            hashMap.put(mkey, new ChartQueryResult(mkey, area, (Polygon) geometry));
                                                    }
                                                    break;
                                                }
                                            }
                                        } else if (QueriedLayerIndex == 1) {
                                            for (String key : mQuerryString.keySet()) {
                                                if (key.equals("CODE")) {
                                                    String mkey = String.valueOf(mQuerryString.get(key));
                                                    if (hashMap.containsKey(mkey)) {
                                                        ChartQueryResult cqr = hashMap.get(mkey);
                                                        Polygon p = cqr.getPolygon();
                                                        cqr.setPolygon((Polygon) GeometryEngine.union(p, geometry));
                                                        cqr.setArea(cqr.getArea() + area);
                                                    } else {
                                                        if (area > 0)
                                                            hashMap.put(mkey, new ChartQueryResult(mkey, area, (Polygon) geometry));
                                                    }
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }
                                Log.w(TAG, "run: 2020090417" + hashMap.size());
                                wholeArea = GeometryEngine.areaGeodetic(geometry1, new AreaUnit(AreaUnitId.SQUARE_KILOMETERS), GeodeticCurveType.GEODESIC) * 1500;
                                GraphicsOverlay graphicsOverlay_1 = new GraphicsOverlay();
                                SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.RED, 3);
                                Graphic fillGraphic = new Graphic(geometry1, lineSymbol);
                                graphicsOverlay_1.getGraphics().add(fillGraphic);
                                mMapView.getGraphicsOverlays().add(graphicsOverlay_1);
                                List<SliceValue> sliceValues = new ArrayList<>();
                                TextView calloutContent = new TextView(getApplicationContext());
                                calloutContent.setTextColor(Color.BLACK);
                                String str = "";

                                DecimalFormat decimalFormat = new DecimalFormat("0.00");
                                DecimalFormat decimalFormat1 = new DecimalFormat("0.0");
                                Iterator iter = hashMap.entrySet().iterator();
                                while (iter.hasNext()) {
                                    Map.Entry entry = (Map.Entry) iter.next();
                                    Object key = entry.getKey();
                                    Object val = entry.getValue();

                                    String name = (String) key;
                                    ChartQueryResult cqr = (ChartQueryResult) val;

                                    Log.w(TAG, "run: 2020090417" + name);

                                    float value = Float.valueOf(cqr.getArea().toString()) / (float) wholeArea;
                                    SliceValue sliceValue = new SliceValue(value, ChartUtils.pickColor());
                                    sliceValue.setLabel(cqr.getTypeName() + ":" + decimalFormat1.format(value * 100) + "%");
                                    sliceValues.add(sliceValue);
                                    if (str.length() == 0) {
                                        str += name + ": " + decimalFormat1.format(Double.valueOf(Float.valueOf(cqr.getArea().toString()))) + "亩";
                                        str += ",占比: " + decimalFormat.format(Double.valueOf(Float.valueOf(cqr.getArea().toString())) / wholeArea * 100) + "%";
                                    } else {
                                        str += "\n" + name + ": " + decimalFormat1.format(Double.valueOf(Float.valueOf(cqr.getArea().toString()))) + "亩";
                                        str += ",占比: " + decimalFormat.format(Double.valueOf(Float.valueOf(cqr.getArea().toString())) / wholeArea * 100) + "%";
                                    }
                                }


                                calloutContent.setText(str);
                                // get callout, set content and show
                                mCallout.setLocation(new Point(geometry1.getExtent().getCenter().getX(), geometry1.getExtent().getYMax(), SpatialReference.create(4523)));
                                mCallout.setContent(calloutContent);
                                Log.w(TAG, "run: callout" + mCallout.isShowing());
                                mCallout.show();
                                inMap = true;
                                PieChartData pieChartData = new PieChartData(sliceValues);
                                pieChartData.setHasLabels(true);
                                pieChartData.setHasLabelsOutside(false);
                                pieChartData.setHasLabelsOnlyForSelected(true);
                                pieChartView.setOnValueTouchListener(new ValueTouchListener(hashMap, geometry1));
                                pieChartView.setPieChartData(pieChartData);
                                pieChartView.setValueSelectionEnabled(true);
                                pieChartView.setVisibility(View.VISIBLE);



                            /*pieChartView.setOnLongClickListener(new View.OnLongClickListener() {
                                @Override
                                public boolean onLongClick(View v) {
                                    return true;
                                }
                            });*/
                            } catch (Exception e) {
                                Toast.makeText(MainActivity.this, "当前分析出错，请选择正确的面图层进行分析！", Toast.LENGTH_SHORT).show();
                                pieChartView.setVisibility(View.GONE);
                                mCallout.dismiss();
                            }
                        }
                    });
                }
            }
            else{
                removeGraphicsOverlayers();
                Toast.makeText(MainActivity.this, "当前分析出错，请选择正确的面图层进行分析！", Toast.LENGTH_SHORT).show();
                pieChartView.setVisibility(View.GONE);
                mCallout.dismiss();
            }
        } catch (ArcGISRuntimeException e) {
            Toast.makeText(MainActivity.this, "当前分析出错，请选择正确的面图层进行分析！", Toast.LENGTH_SHORT).show();
            pieChartView.setVisibility(View.GONE);
            mCallout.dismiss();
        }
    }

    String XZQLayerName = "";
    private void queryTaskFor20200904(final QueryParameters query, final Polygon polygon){
        try {
            if (QueriedLayerIndex != -1 && QueriedLayerIndex < BaseLayerFieldsSheetList.size()) {
                FeatureTable mTable = LayerFieldsSheetList.get(QueriedLayerIndex).getFeatureLayer().getFeatureTable();
                mCallout.dismiss();
            /*if (QueriedFeature == DisplayEnum.TDGHDL_FEATURE)
                mTable = TDGHDLFeatureLayer.getFeatureTable();//得到查询属性表
            else
                mTable = XZQFeatureLayer.getFeatureTable();//得到查询属性表*/

                if (pointCollection.size() >= 3) {
                    final ListenableFuture<FeatureQueryResult> featureQueryResult
                            = mTable.queryFeaturesAsync(query);
                    featureQueryResult.addDoneListener(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                removeGraphicsOverlayers();
                                FeatureQueryResult featureResul = featureQueryResult.get();
                                Geometry geometry1 = polygon;
                                List<QueryTaskInfo> queryTaskInfos = new ArrayList<>();
                                HashMap<String, ChartQueryResult> hashMap = new HashMap<>();
                                //遍历被查询到的要素
                                for (Object element : featureResul) {
                                    if (element instanceof Feature) {
                                        Feature mFeatureGrafic = (Feature) element;
                                        Geometry geometry = null;
                                        Polygon polygon1 = (Polygon) mFeatureGrafic.getGeometry();
                                        geometry = GeometryEngine.intersection(GeometryEngine.project(polygon1, SpatialReference.create(4523)), polygon);
                                        boolean isOK = false;
                                        Double area = GeometryEngine.areaGeodetic(geometry, new AreaUnit(AreaUnitId.SQUARE_KILOMETERS), GeodeticCurveType.GEODESIC) * 1500;
                                        QueryTaskInfo queryTaskInfo = new QueryTaskInfo(area);
                                        Log.w(TAG, "按需查询切换图层: " + queryTaskInfo.getArea());
                                        //Log.w(TAG, "run: " + geometry1.getSpatialReference().getWkid());
                                        GraphicsOverlay graphicsOverlay_1 = new GraphicsOverlay();
                                        SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.BLUE, 3);
                                        Graphic fillGraphic = new Graphic(geometry, lineSymbol);
                                        graphicsOverlay_1.getGraphics().add(fillGraphic);
                                        mMapView.getGraphicsOverlays().add(graphicsOverlay_1);
                                        Map<String, Object> mQuerryString = mFeatureGrafic.getAttributes();
                                        if (QueriedLayerIndex == 16 || QueriedLayerIndex == 15 || QueriedLayerIndex == 13 || QueriedLayerIndex == 4) {
                                            for (String key : mQuerryString.keySet()) {
                                                if (key.equals("DLMC")) {
                                                    String mkey = String.valueOf(mQuerryString.get(key));
                                                    if (hashMap.containsKey(mkey)) {
                                                        ChartQueryResult cqr = hashMap.get(mkey);
                                                        Polygon p = cqr.getPolygon();
                                                        cqr.setPolygon((Polygon) GeometryEngine.union(p, geometry));
                                                        cqr.setArea(cqr.getArea() + area);
                                                    } else {
                                                        if (area > 0)
                                                            hashMap.put(mkey, new ChartQueryResult(mkey, area, (Polygon) geometry));
                                                    }
                                                    break;
                                                }
                                            }
                                        } else if (QueriedLayerIndex == 14) {
                                            for (String key : mQuerryString.keySet()) {
                                                if (key.equals("GHDLMC")) {
                                                    String mkey = String.valueOf(mQuerryString.get(key));
                                                    if (hashMap.containsKey(mkey)) {
                                                        ChartQueryResult cqr = hashMap.get(mkey);
                                                        Polygon p = cqr.getPolygon();
                                                        cqr.setPolygon((Polygon) GeometryEngine.union(p, geometry));
                                                        cqr.setArea(cqr.getArea() + area);
                                                    } else {
                                                        if (area > 0)
                                                            hashMap.put(mkey, new ChartQueryResult(mkey, area, (Polygon) geometry));
                                                    }
                                                    break;
                                                }
                                            }
                                        } else if (QueriedLayerIndex == 11) {
                                            for (String key : mQuerryString.keySet()) {
                                                if (key.equals("ZLDWMC")) {
                                                    String mkey = String.valueOf(mQuerryString.get(key));
                                                    if (hashMap.containsKey(mkey)) {
                                                        ChartQueryResult cqr = hashMap.get(mkey);
                                                        Polygon p = cqr.getPolygon();
                                                        cqr.setPolygon((Polygon) GeometryEngine.union(p, geometry));
                                                        cqr.setArea(cqr.getArea() + area);
                                                    } else {
                                                        if (area > 0)
                                                            hashMap.put(mkey, new ChartQueryResult(mkey, area, (Polygon) geometry));
                                                    }
                                                    break;
                                                }
                                            }
                                        }else if (QueriedLayerIndex == 12) {
                                            for (String key : mQuerryString.keySet()) {
                                                if (key.equals("GHFQMC")) {
                                                    String mkey = String.valueOf(mQuerryString.get(key));
                                                    if (hashMap.containsKey(mkey)) {
                                                        ChartQueryResult cqr = hashMap.get(mkey);
                                                        Polygon p = cqr.getPolygon();
                                                        cqr.setPolygon((Polygon) GeometryEngine.union(p, geometry));
                                                        cqr.setArea(cqr.getArea() + area);
                                                    } else {
                                                        if (area > 0)
                                                            hashMap.put(mkey, new ChartQueryResult(mkey, area, (Polygon) geometry));
                                                    }
                                                    break;
                                                }
                                            }
                                        }else if (QueriedLayerIndex == 10 || QueriedLayerIndex == 2) {
                                            for (String key : mQuerryString.keySet()) {
                                                if (key.equals("XMMC")) {
                                                    String mkey = String.valueOf(mQuerryString.get(key));
                                                    if (hashMap.containsKey(mkey)) {
                                                        ChartQueryResult cqr = hashMap.get(mkey);
                                                        Polygon p = cqr.getPolygon();
                                                        cqr.setPolygon((Polygon) GeometryEngine.union(p, geometry));
                                                        cqr.setArea(cqr.getArea() + area);
                                                    } else {
                                                        if (area > 0)
                                                            hashMap.put(mkey, new ChartQueryResult(mkey, area, (Polygon) geometry));
                                                    }
                                                    break;
                                                }
                                            }
                                        }else if (QueriedLayerIndex == 9 || QueriedLayerIndex == 7) {
                                            for (String key : mQuerryString.keySet()) {
                                                if (key.equals("主导功能")) {
                                                    String mkey = String.valueOf(mQuerryString.get(key));
                                                    if (hashMap.containsKey(mkey)) {
                                                        ChartQueryResult cqr = hashMap.get(mkey);
                                                        Polygon p = cqr.getPolygon();
                                                        cqr.setPolygon((Polygon) GeometryEngine.union(p, geometry));
                                                        cqr.setArea(cqr.getArea() + area);
                                                    } else {
                                                        if (area > 0)
                                                            hashMap.put(mkey, new ChartQueryResult(mkey, area, (Polygon) geometry));
                                                    }
                                                    break;
                                                }
                                            }
                                        }else if (QueriedLayerIndex == 6 || QueriedLayerIndex == 8) {
                                            for (String key : mQuerryString.keySet()) {
                                                if (key.equals("ZRBHDMC")) {
                                                    String mkey = String.valueOf(mQuerryString.get(key));
                                                    if (hashMap.containsKey(mkey)) {
                                                        ChartQueryResult cqr = hashMap.get(mkey);
                                                        Polygon p = cqr.getPolygon();
                                                        cqr.setPolygon((Polygon) GeometryEngine.union(p, geometry));
                                                        cqr.setArea(cqr.getArea() + area);
                                                    } else {
                                                        if (area > 0)
                                                            hashMap.put(mkey, new ChartQueryResult(mkey, area, (Polygon) geometry));
                                                    }
                                                    break;
                                                }
                                            }
                                        }else if (QueriedLayerIndex == 1) {
                                            for (String key : mQuerryString.keySet()) {
                                                if (key.equals("JBXX_XMMC")) {
                                                    String mkey = String.valueOf(mQuerryString.get(key));
                                                    if (hashMap.containsKey(mkey)) {
                                                        ChartQueryResult cqr = hashMap.get(mkey);
                                                        Polygon p = cqr.getPolygon();
                                                        cqr.setPolygon((Polygon) GeometryEngine.union(p, geometry));
                                                        cqr.setArea(cqr.getArea() + area);
                                                    } else {
                                                        if (area > 0)
                                                            hashMap.put(mkey, new ChartQueryResult(mkey, area, (Polygon) geometry));
                                                    }
                                                    break;
                                                }
                                            }
                                        }else if (QueriedLayerIndex == 5) {
                                            for (String key : mQuerryString.keySet()) {
                                                if (key.equals("ZJRXM")) {
                                                    String mkey = String.valueOf(mQuerryString.get(key));
                                                    if (hashMap.containsKey(mkey)) {
                                                        ChartQueryResult cqr = hashMap.get(mkey);
                                                        Polygon p = cqr.getPolygon();
                                                        cqr.setPolygon((Polygon) GeometryEngine.union(p, geometry));
                                                        cqr.setArea(cqr.getArea() + area);
                                                    } else {
                                                        if (area > 0)
                                                            hashMap.put(mkey, new ChartQueryResult(mkey, area, (Polygon) geometry));
                                                    }
                                                    break;
                                                }
                                            }
                                        } else if (QueriedLayerIndex == 0) {
                                            for (String key : mQuerryString.keySet()) {
                                                if (key.equals("YDXZ")) {
                                                    String mkey = String.valueOf(mQuerryString.get(key));
                                                    if (hashMap.containsKey(mkey)) {
                                                        ChartQueryResult cqr = hashMap.get(mkey);
                                                        Polygon p = cqr.getPolygon();
                                                        cqr.setPolygon((Polygon) GeometryEngine.union(p, geometry));
                                                        cqr.setArea(cqr.getArea() + area);
                                                    } else {
                                                        if (area > 0)
                                                            hashMap.put(mkey, new ChartQueryResult(mkey, area, (Polygon) geometry));
                                                    }
                                                    break;
                                                }
                                            }
                                        }else if (QueriedLayerIndex == 3) {
                                            for (String key : mQuerryString.keySet()) {
                                                if (key.equals("PZWH")) {
                                                    String mkey = String.valueOf(mQuerryString.get(key));
                                                    if (hashMap.containsKey(mkey)) {
                                                        ChartQueryResult cqr = hashMap.get(mkey);
                                                        Polygon p = cqr.getPolygon();
                                                        cqr.setPolygon((Polygon) GeometryEngine.union(p, geometry));
                                                        cqr.setArea(cqr.getArea() + area);
                                                    } else {
                                                        if (area > 0)
                                                            hashMap.put(mkey, new ChartQueryResult(mkey, area, (Polygon) geometry));
                                                    }
                                                    break;
                                                }
                                            }
                                        } else if (QueriedLayerIndex == 17 || QueriedLayerIndex == 18) {
                                            for (String key : mQuerryString.keySet()) {
                                                if (key.equals("XZQMC")) {
                                                    String mkey = String.valueOf(mQuerryString.get(key));
                                                    if (hashMap.containsKey(mkey)) {
                                                        ChartQueryResult cqr = hashMap.get(mkey);
                                                        Polygon p = cqr.getPolygon();
                                                        cqr.setPolygon((Polygon) GeometryEngine.union(p, geometry));
                                                        cqr.setArea(cqr.getArea() + area);
                                                    } else {
                                                        if (area > 0)
                                                            hashMap.put(mkey, new ChartQueryResult(mkey, area, (Polygon) geometry));
                                                    }
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }
                                Log.w(TAG, "run: 2020090417" + hashMap.size());
                                wholeArea = GeometryEngine.areaGeodetic(geometry1, new AreaUnit(AreaUnitId.SQUARE_KILOMETERS), GeodeticCurveType.GEODESIC) * 1500;
                                GraphicsOverlay graphicsOverlay_1 = new GraphicsOverlay();
                                SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.RED, 3);
                                Graphic fillGraphic = new Graphic(geometry1, lineSymbol);
                                graphicsOverlay_1.getGraphics().add(fillGraphic);
                                mMapView.getGraphicsOverlays().add(graphicsOverlay_1);
                                List<SliceValue> sliceValues = new ArrayList<>();
                                TextView calloutContent = new TextView(getApplicationContext());
                                calloutContent.setTextColor(Color.BLACK);
                                String str = "";

                                DecimalFormat decimalFormat = new DecimalFormat("0.00");
                                DecimalFormat decimalFormat1 = new DecimalFormat("0.0");
                                Iterator iter = hashMap.entrySet().iterator();
                                while (iter.hasNext()) {
                                    Map.Entry entry = (Map.Entry) iter.next();
                                    Object key = entry.getKey();
                                    Object val = entry.getValue();

                                    String name = (String) key;
                                    ChartQueryResult cqr = (ChartQueryResult) val;

                                    Log.w(TAG, "run: 2020090417" + name);

                                    float value = Float.valueOf(cqr.getArea().toString()) / (float) wholeArea;
                                    SliceValue sliceValue = new SliceValue(value, ChartUtils.pickColor());
                                    sliceValue.setLabel(cqr.getTypeName() + ":" + decimalFormat1.format(value * 100) + "%");
                                    sliceValues.add(sliceValue);
                                    if (str.length() == 0) {
                                        str += name + ": " + decimalFormat1.format(Double.valueOf(Float.valueOf(cqr.getArea().toString()))) + "亩";
                                        str += ",占比: " + decimalFormat.format(Double.valueOf(Float.valueOf(cqr.getArea().toString())) / wholeArea * 100) + "%";
                                    } else {
                                        str += "\n" + name + ": " + decimalFormat1.format(Double.valueOf(Float.valueOf(cqr.getArea().toString()))) + "亩";
                                        str += ",占比: " + decimalFormat.format(Double.valueOf(Float.valueOf(cqr.getArea().toString())) / wholeArea * 100) + "%";
                                    }
                                }


                                calloutContent.setText(str);
                                mCallout.dismiss();
                                // get callout, set content and show
                                mCallout.setLocation(new Point(geometry1.getExtent().getCenter().getX(), geometry1.getExtent().getYMax(), SpatialReference.create(4523)));
                                mCallout.setContent(calloutContent);

                                // TODO 2020/12/9 切换查询图层时候的按需查询

                                Log.w(TAG, "run: 按需查询切换图层， Callout是否显示： " + mCallout.isShowing());
                                mCallout.show();
                                Log.w(TAG, "run: 按需查询切换图层， Callout是否显示： " + mCallout.isShowing());
                                inMap = true;
                                PieChartData pieChartData = new PieChartData(sliceValues);
                                pieChartData.setHasLabels(true);
                                pieChartData.setHasLabelsOutside(false);
                                pieChartData.setHasLabelsOnlyForSelected(true);
                                pieChartView.setOnValueTouchListener(new ValueTouchListener(hashMap, geometry1));
                                pieChartView.setPieChartData(pieChartData);
                                pieChartView.setValueSelectionEnabled(true);
                                pieChartView.setVisibility(View.VISIBLE);



                            /*pieChartView.setOnLongClickListener(new View.OnLongClickListener() {
                                @Override
                                public boolean onLongClick(View v) {
                                    return true;
                                }
                            });*/
                            } catch (Exception e) {
                                Toast.makeText(MainActivity.this, "当前分析出错，请选择正确的面图层进行分析！", Toast.LENGTH_SHORT).show();
                                pieChartView.setVisibility(View.GONE);
                                mCallout.dismiss();
                            }
                        }
                    });
                } else
                    Toast.makeText(MainActivity.this, "三个点以上才可进行多边形查询，请重试！", Toast.LENGTH_SHORT).show();
            }
            else{
                //removeGraphicsOverlayers();
                Toast.makeText(MainActivity.this, "当前分析出错，请选择正确的面图层进行分析！", Toast.LENGTH_SHORT).show();
                pieChartView.setVisibility(View.GONE);
                mCallout.dismiss();
            }
        } catch (ArcGISRuntimeException e) {
            Toast.makeText(MainActivity.this, "当前分析出错，请选择正确的面图层进行分析！", Toast.LENGTH_SHORT).show();
            pieChartView.setVisibility(View.GONE);
            mCallout.dismiss();
        }
    }

    private double jbntArea;
    private void querySingleTaskForPolygon(final QueryParameters query, final Polygon polygon, FeatureTable mTable, final String text){
        try {
            Log.w(TAG, "querySingleTaskForPolygon: " + text);
            final ListenableFuture<FeatureQueryResult> featureQueryResult
                    = mTable.queryFeaturesAsync(query);
            featureQueryResult.addDoneListener(new Runnable() {
                @Override
                public void run() {
                    try {
                        //removeGraphicsOverlayers();
                        FeatureQueryResult featureResul = featureQueryResult.get();
                        Geometry geometry1 = polygon;
                        List<QueryTaskInfo> queryTaskInfos = new ArrayList<>();
                        for (Object element : featureResul) {
                            if (element instanceof Feature) {
                                Feature mFeatureGrafic = (Feature) element;
                                Geometry geometry = null;
                                Polygon polygon1 = (Polygon) mFeatureGrafic.getGeometry();
                                geometry = GeometryEngine.intersection(GeometryEngine.project(polygon1, SpatialReference.create(4523)), polygon);
                                boolean isOK = false;
                                QueryTaskInfo queryTaskInfo = new QueryTaskInfo(GeometryEngine.areaGeodetic(geometry, new AreaUnit(AreaUnitId.SQUARE_KILOMETERS), GeodeticCurveType.GEODESIC) * 1500);
                                Log.w(TAG, "geometry2type: " + text + queryTaskInfo.getArea());
                                Map<String, Object> mQuerryString = mFeatureGrafic.getAttributes();
                                switch (text){
                                    case "土地规划地类":
                                        for (String key : mQuerryString.keySet()) {
                                            //str = str + key + " : " + String.valueOf(mQuerryString.get(key)) + "\n";
                                            switch (key){
                                                case "GHDLMC":
                                                    queryTaskInfo.setTypename(String.valueOf(mQuerryString.get(key)));
                                                    break;
                                                case "GHDLBM":
                                                    queryTaskInfo.setType(String.valueOf(mQuerryString.get(key)));
                                                    break;
                                                case "XZQMC":
                                                    queryTaskInfo.setXzq(String.valueOf(mQuerryString.get(key)));
                                                    break;
                                                case "XZQDM":
                                                    queryTaskInfo.setXzqdm(String.valueOf(mQuerryString.get(key)));
                                                    break;
                                            }
                                            isOK = true;
                                        }
                                        break;
                                    case "XZQ_FEATURE":
                                        for (String key : mQuerryString.keySet()) {
                                            //Log.w(TAG, "行政区: " + key);
                                            //str = str + key + " : " + String.valueOf(mQuerryString.get(key)) + "\n";
                                            if (key.equals("XZQDM")) {
                                                //Log.w(TAG, "行政区界线: " + String.valueOf(mQuerryString.get(key)));
                                                //Log.w(TAG, "行政区界线 面积: " + queryTaskInfo.getArea());
                                                String str = String.valueOf(mQuerryString.get(key));
                                                if (str.length() >= 9) {
                                                    queryTaskInfo.setXzqdm(str);
                                                    isOK = true;
                                                } else
                                                    break;
                                            } else if (key.equals("XZQMC")) {
                                                queryTaskInfo.setXzq(String.valueOf(mQuerryString.get(key)));
                                            }/*else {
                                                queryTaskInfo.setXzq(String.valueOf(mQuerryString.get(key)));
                                            }*/
                                        }
                                        break;
                                    case "PTB_FEATURE":
                                        for (String key : mQuerryString.keySet()) {
                                            Log.w(TAG, "行政区: " + key);
                                            //str = str + key + " : " + String.valueOf(mQuerryString.get(key)) + "\n";
                                            if (key.equals("XJXZQHDM")) {
                                                //Log.w(TAG, "行政区界线: " + String.valueOf(mQuerryString.get(key)));
                                                //Log.w(TAG, "行政区界线 面积: " + queryTaskInfo.getArea());
                                                String str = String.valueOf(mQuerryString.get(key));
                                                queryTaskInfo.setXzqdm(str);
                                                isOK = true;
                                            } else if (key.equals("BZPZWH")) {
                                                queryTaskInfo.setXzq(String.valueOf(mQuerryString.get(key)));
                                            }
                                        }
                                        break;
                                    case "JBNTBHQ":
                                        isOK = true;
                                        break;
                                    default:
                                        for (String key : mQuerryString.keySet()) {
                                            Log.w(TAG, "行政区: " + key);
                                            //str = str + key + " : " + String.valueOf(mQuerryString.get(key)) + "\n";
                                            if (key.equals("DLBM")) {
                                                //Log.w(TAG, "行政区界线: " + String.valueOf(mQuerryString.get(key)));
                                                //Log.w(TAG, "行政区界线 面积: " + queryTaskInfo.getArea());
                                                String str = String.valueOf(mQuerryString.get(key));
                                                queryTaskInfo.setXzqdm(str);
                                                isOK = true;
                                            } else if (key.equals("DLMC")) {
                                                queryTaskInfo.setXzq(String.valueOf(mQuerryString.get(key)));
                                            }
                                        }
                                        break;
                                }
                                if (isOK)
                                    queryTaskInfos.add(queryTaskInfo);

                            }
                        }
                        wholeArea = GeometryEngine.areaGeodetic(geometry1, new AreaUnit(AreaUnitId.SQUARE_KILOMETERS), GeodeticCurveType.GEODESIC) * 1500;
                        keyAndValues = new ArrayList<>();

                        if (text.equals("土地规划地类")) {
                            for (int i = 0; i < queryTaskInfos.size(); ++i) {
                                if (i == 0 && queryTaskInfos.get(i).getArea() != 0)
                                    keyAndValues.add(new KeyAndValue(queryTaskInfos.get(i).getTypename(), Double.toString(queryTaskInfos.get(i).getArea())));
                                else {
                                    boolean hasKey = false;
                                    for (int j = 0; j < keyAndValues.size(); ++j) {
                                        if (queryTaskInfos.get(i).getTypename().equals(keyAndValues.get(j).getName())) {
                                            hasKey = true;
                                            keyAndValues.get(j).setValue(Double.toString(Double.valueOf(keyAndValues.get(j).getValue()) + queryTaskInfos.get(i).getArea()));
                                            break;
                                        }
                                    }
                                    if (!hasKey && queryTaskInfos.get(i).getArea() != 0) {
                                        keyAndValues.add(new KeyAndValue(queryTaskInfos.get(i).getTypename(), Double.toString(queryTaskInfos.get(i).getArea())));
                                    }
                                }
                            }
                        }else if (text.equals("JBNTBHQ")){

                        }
                        else {
                            for (int i = 0; i < queryTaskInfos.size(); ++i) {
                                if (i == 0 && queryTaskInfos.get(i).getArea() != 0)
                                    keyAndValues.add(new KeyAndValue(queryTaskInfos.get(i).getXzq(), Double.toString(queryTaskInfos.get(i).getArea())));
                                else {
                                    boolean hasKey = false;
                                    for (int j = 0; j < keyAndValues.size(); ++j) {
                                        if (queryTaskInfos.get(i).getXzq().equals(keyAndValues.get(j).getName())) {
                                            hasKey = true;
                                            keyAndValues.get(j).setValue(Double.toString(Double.valueOf(keyAndValues.get(j).getValue()) + queryTaskInfos.get(i).getArea()));
                                            break;
                                        }
                                    }
                                    if (!hasKey && queryTaskInfos.get(i).getArea() != 0) {
                                        keyAndValues.add(new KeyAndValue(queryTaskInfos.get(i).getXzq(), Double.toString(queryTaskInfos.get(i).getArea())));
                                    }
                                }
                            }
                        }
                        DecimalFormat decimalFormat = new DecimalFormat("0.00");
                        DecimalFormat decimalFormat1 = new DecimalFormat("0.0");
                        String data = "";
                        for (int j = 0; j < keyAndValues.size(); ++j) {
                            //float value = Float.valueOf(keyAndValues.get(j).getValue()) / (float) wholeArea;
                            if (j < keyAndValues.size() - 1) {
                                data = data + keyAndValues.get(j).getName() + ":" + decimalFormat.format(Double.valueOf(keyAndValues.get(j).getValue())) + ",";
                            } else {
                                data = data + keyAndValues.get(j).getName() + ":" + decimalFormat.format(Double.valueOf(keyAndValues.get(j).getValue()));
                            }
                        }
                        inMap = true;
                        if (text.equals("土地规划地类")) {
                            isQueryTDGHDL = true;
                            data = "图层:土地规划地类," + data;
                        }
                        else if (text.equals("09地类图斑")) {
                            isQuery09DLTB = true;
                            data = "图层:09地类图斑," + data;
                        }
                        else if (text.equals("16地类图斑")) {
                            isQuery16DLTB = true;
                            data = "图层:16地类图斑," + data;
                        }
                        else if (text.equals("17地类图斑")) {
                            isQuery17DLTB = true;
                            data = "图层:17地类图斑," + data;
                        }else if (text.contains("JBNTBHQ")) {
                            isQueryJBNTBHQ = true;
                            jbntArea = 0;
                            for (int i = 0; i < queryTaskInfos.size(); ++i){
                                jbntArea += queryTaskInfos.get(i).getArea();
                                Log.w(TAG, "run: " + queryTaskInfos.get(i).getArea());
                            }
                            Log.w(TAG, "run: " + "lzy");
                            data = "图层:JBNTBHQ," + "基本农田保护区:" + decimalFormat.format(jbntArea);
                        }
                        Log.w(TAG, "onCreate: " + data);
                        isOkForPopWindow(data);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (ArcGISRuntimeException e) {
            Toast.makeText(MainActivity.this, e.getMessage() + e.getErrorCode(), Toast.LENGTH_SHORT).show();
        }
    }

    private void querySingleTaskForPolygon(final QueryParameters query, final Polygon polygon, LayerFieldsSheet lfs){
        try {
            final FeatureTable mTable = lfs.getFeatureLayer().getFeatureTable();
            final String text = lfs.getLayerShowName();
            final ListenableFuture<FeatureQueryResult> featureQueryResult
                    = mTable.queryFeaturesAsync(query);
            featureQueryResult.addDoneListener(new Runnable() {
                @Override
                public void run() {
                    try {
                        //removeGraphicsOverlayers();
                        FeatureQueryResult featureResul = featureQueryResult.get();
                        Geometry geometry1 = polygon;
                        List<QueryTaskInfo> queryTaskInfos = new ArrayList<>();
                        for (Object element : featureResul) {
                            if (element instanceof Feature) {
                                Feature mFeatureGrafic = (Feature) element;
                                Geometry geometry = null;
                                Polygon polygon1 = (Polygon) mFeatureGrafic.getGeometry();
                                geometry = GeometryEngine.intersection(GeometryEngine.project(polygon1, SpatialReference.create(4523)), polygon);
                                boolean isOK = false;
                                QueryTaskInfo queryTaskInfo = new QueryTaskInfo(GeometryEngine.areaGeodetic(geometry, new AreaUnit(AreaUnitId.SQUARE_KILOMETERS), GeodeticCurveType.GEODESIC) * 1500);
                                Map<String, Object> mQuerryString = mFeatureGrafic.getAttributes();
                                switch (text){
                                    case "农村土地承包经营权":
                                        for (String key : mQuerryString.keySet()) {
                                            if (key.equals("DKBM")) {
                                                String str = String.valueOf(mQuerryString.get(key));
                                                if (str.length() >= 9) {
                                                    queryTaskInfo.setXzqdm(str);
                                                    isOK = true;
                                                } else
                                                    break;
                                            } else if (key.equals("DKMC")) {
                                                queryTaskInfo.setXzq(String.valueOf(mQuerryString.get(key)));
                                            }
                                        }
                                        break;
                                    case "乡镇级行政区":
                                        for (String key : mQuerryString.keySet()) {
                                            if (key.equals("XZQDM")) {
                                                String str = String.valueOf(mQuerryString.get(key));
                                                if (str.length() >= 9) {
                                                    queryTaskInfo.setXzqdm(str);
                                                    isOK = true;
                                                } else
                                                    break;
                                            } else if (key.equals("XZQMC")) {
                                                queryTaskInfo.setXzq(String.valueOf(mQuerryString.get(key)));
                                            }
                                        }
                                        break;
                                    case "生态保护红线":
                                        for (String key : mQuerryString.keySet()) {
                                            Log.w(TAG, "行政区: " + key);
                                            //str = str + key + " : " + String.valueOf(mQuerryString.get(key)) + "\n";
                                            if (key.equals("CODE")) {
                                                //Log.w(TAG, "行政区界线: " + String.valueOf(mQuerryString.get(key)));
                                                //Log.w(TAG, "行政区界线 面积: " + queryTaskInfo.getArea());
                                                String str = String.valueOf(mQuerryString.get(key));
                                                queryTaskInfo.setXzqdm(str);
                                                isOK = true;
                                            } else if (key.equals("类型")) {
                                                queryTaskInfo.setXzq(String.valueOf(mQuerryString.get(key)));
                                            }
                                        }
                                        break;
                                    default:
                                        for (String key : mQuerryString.keySet()) {
                                            Log.w(TAG, "行政区: " + key);
                                            //str = str + key + " : " + String.valueOf(mQuerryString.get(key)) + "\n";
                                            if (key.equals("DLBM")) {
                                                //Log.w(TAG, "行政区界线: " + String.valueOf(mQuerryString.get(key)));
                                                //Log.w(TAG, "行政区界线 面积: " + queryTaskInfo.getArea());
                                                String str = String.valueOf(mQuerryString.get(key));
                                                queryTaskInfo.setXzqdm(str);
                                                isOK = true;
                                            } else if (key.equals("DLMC")) {
                                                queryTaskInfo.setXzq(String.valueOf(mQuerryString.get(key)));
                                            }
                                        }
                                        break;
                                }
                                if (isOK)
                                    queryTaskInfos.add(queryTaskInfo);

                            }
                        }
                        wholeArea = GeometryEngine.areaGeodetic(geometry1, new AreaUnit(AreaUnitId.SQUARE_KILOMETERS), GeodeticCurveType.GEODESIC) * 1500;
                        keyAndValues = new ArrayList<>();

                        if (text.equals("土地规划地类")) {
                            for (int i = 0; i < queryTaskInfos.size(); ++i) {
                                if (i == 0 && queryTaskInfos.get(i).getArea() != 0)
                                    keyAndValues.add(new KeyAndValue(queryTaskInfos.get(i).getTypename(), Double.toString(queryTaskInfos.get(i).getArea())));
                                else {
                                    boolean hasKey = false;
                                    for (int j = 0; j < keyAndValues.size(); ++j) {
                                        if (queryTaskInfos.get(i).getTypename().equals(keyAndValues.get(j).getName())) {
                                            hasKey = true;
                                            keyAndValues.get(j).setValue(Double.toString(Double.valueOf(keyAndValues.get(j).getValue()) + queryTaskInfos.get(i).getArea()));
                                            break;
                                        }
                                    }
                                    if (!hasKey && queryTaskInfos.get(i).getArea() != 0) {
                                        keyAndValues.add(new KeyAndValue(queryTaskInfos.get(i).getTypename(), Double.toString(queryTaskInfos.get(i).getArea())));
                                    }
                                }
                            }
                        }else if (text.equals("JBNTBHQ")){

                        }
                        else {
                            for (int i = 0; i < queryTaskInfos.size(); ++i) {
                                if (i == 0 && queryTaskInfos.get(i).getArea() != 0)
                                    keyAndValues.add(new KeyAndValue(queryTaskInfos.get(i).getXzq(), Double.toString(queryTaskInfos.get(i).getArea())));
                                else {
                                    boolean hasKey = false;
                                    for (int j = 0; j < keyAndValues.size(); ++j) {
                                        if (queryTaskInfos.get(i).getXzq().equals(keyAndValues.get(j).getName())) {
                                            hasKey = true;
                                            keyAndValues.get(j).setValue(Double.toString(Double.valueOf(keyAndValues.get(j).getValue()) + queryTaskInfos.get(i).getArea()));
                                            break;
                                        }
                                    }
                                    if (!hasKey && queryTaskInfos.get(i).getArea() != 0) {
                                        keyAndValues.add(new KeyAndValue(queryTaskInfos.get(i).getXzq(), Double.toString(queryTaskInfos.get(i).getArea())));
                                    }
                                }
                            }
                        }
                        DecimalFormat decimalFormat = new DecimalFormat("0.00");
                        DecimalFormat decimalFormat1 = new DecimalFormat("0.0");
                        String data = "";
                        for (int j = 0; j < keyAndValues.size(); ++j) {
                            //float value = Float.valueOf(keyAndValues.get(j).getValue()) / (float) wholeArea;
                            if (j < keyAndValues.size() - 1) {
                                data = data + keyAndValues.get(j).getName() + ":" + decimalFormat.format(Double.valueOf(keyAndValues.get(j).getValue())) + ",";
                            } else {
                                data = data + keyAndValues.get(j).getName() + ":" + decimalFormat.format(Double.valueOf(keyAndValues.get(j).getValue()));
                            }
                        }
                        inMap = true;
                        if (text.equals("三调地类图斑")) {
                            isQueryTDGHDL = true;
                            data = "图层:三调地类图斑," + data;
                        }
                        else if (text.equals("二调地类图斑")) {
                            isQuery09DLTB = true;
                            data = "图层:二调地类图斑," + data;
                        }
                        else if (text.equals("生态保护红线")) {
                            isQuery16DLTB = true;
                            data = "图层:生态保护红线," + data;
                        }
                        else if (text.equals("农村土地承包经营权")) {
                            isQuery17DLTB = true;
                            data = "图层:农村土地承包经营权," + data;
                        }else if (text.contains("永久基本农田")) {
                            isQueryJBNTBHQ = true;
                            jbntArea = 0;
                            for (int i = 0; i < queryTaskInfos.size(); ++i){
                                jbntArea += queryTaskInfos.get(i).getArea();
                                Log.w(TAG, "run: " + queryTaskInfos.get(i).getArea());
                            }
                            Log.w(TAG, "run: " + "lzy");
                            data = "图层:YJJBNT," + "永久基本农田:" + decimalFormat.format(jbntArea);
                        }
                        Log.w(TAG, "onCreate: " + data);
                        isOkForPopWindowFor20200904(data);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (ArcGISRuntimeException e) {
            Toast.makeText(MainActivity.this, e.getMessage() + e.getErrorCode(), Toast.LENGTH_SHORT).show();
        }
    }

    boolean isQueryTDGHDL;
    boolean isQuery09DLTB;
    boolean isQuery16DLTB;
    boolean isQuery17DLTB;
    boolean isQueryJBNTBHQ;
    boolean isQueryUserLayer;

    private void queryAllTaskForPolygon(final QueryParameters query, final Polygon polygon){
        try {
            //行政区层
            //querySingleTaskForPolygon(query, polygon, XZQFeatureLayer.getFeatureTable());
            //P图斑层
            //querySingleTaskForPolygon(query, polygon, PTuBanFeatureLayer.getFeatureTable());

            //基本农田保护区
            //querySingleTaskForPolygon(query, polygon, JBNTFeatureLayer.getFeatureTable(), "JBNTBHQ");
            /* 2017-2019使用的数据
            //土地规划地类层
            querySingleTaskForPolygon(query, polygon, TDGHDLFeatureLayer.getFeatureTable(), "土地规划地类");
            //09年土地规划地类层
            querySingleTaskForPolygon(query, polygon, DLTB2009FeatureLayer.getFeatureTable(), "09地类图斑");
            //16年土地规划地类层
            querySingleTaskForPolygon(query, polygon, DLTB2016FeatureLayer.getFeatureTable(), "16地类图斑");
            //17年土地规划地类层
            querySingleTaskForPolygon(query, polygon, DLTB2017FeatureLayer.getFeatureTable(), "17地类图斑");
            */

            // TODO 2020/9/2 新设置的待查寻内容
            //等高线层
            //querySingleTaskForPolygon(query, polygon, DLTB2017FeatureLayer.getFeatureTable(), "DGX");
            mCallout.dismiss();
            for (int i = 0; i < LayerFieldsSheetList.size(); i++) {
                FeatureLayer fl = LayerFieldsSheetList.get(i).getFeatureLayer();
                //querySingleTaskForPolygon(query, polygon, LayerFieldsSheetList.get(i));
            }
        } catch (final Exception e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //Toast.makeText(MainActivity.this, e.getMessage() + "\n" + "请重启app", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    List<String> PopWindowData;
    private void isOkForPopWindow(String data){
        Log.w(TAG, "isOkForPopWindow: " + isQueryTDGHDL + isQuery09DLTB + isQuery16DLTB + isQuery17DLTB + isQueryJBNTBHQ);
        PopWindowData.add(data);
        if (isQueryTDGHDL && isQuery09DLTB && isQuery16DLTB && isQuery17DLTB && isQueryJBNTBHQ){
            isQueryTDGHDL = isQuery09DLTB = isQuery16DLTB = isQuery17DLTB = isQueryJBNTBHQ = false;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showPopWindowForListShow();
                }
            });
        }
    }
    private void isOkForPopWindowFor20200904(String data){
        Log.w(TAG, "isOkForPopWindow for 20201209: " + isQueryTDGHDL + isQuery09DLTB + isQuery16DLTB + isQuery17DLTB + isQueryJBNTBHQ);
        PopWindowData.add(data);

        showPopWindowForListShow();
    }

    private String buildRasterPath() {
        // get sdcard resource name
        File extStorDir = Environment.getExternalStorageDirectory();
        // get the directory
        String extSDCardDirName = "临沧市基本农田/201912无人机影像";
        // get raster filename
        String filename = "DOM_CGCS";
        // create the full path to the raster file
        return extStorDir.getAbsolutePath()
                + File.separator
                + extSDCardDirName
                + File.separator
                + filename
                + ".tif";
    }

    private void loadRaster() {
        // create a raster from a local raster file
        Raster raster = new Raster(buildRasterPath());
        // create a raster layer
        final RasterLayer rasterLayer = new RasterLayer(raster);

        // create a Map with imagery basemap
        //ArcGISMap map = new ArcGISMap(Basemap.createImagery());
        // add the map to a map view
        //mMapView.setMap(map);
        // add the raster as an operational layer
        rasterLayer.setVisible(true);
        map.getOperationalLayers().add(rasterLayer);
        // set viewpoint on the raster
        rasterLayer.addDoneLoadingListener(new Runnable() {
            @Override
            public void run() {
                //mMapView.setViewpointGeometryAsync(rasterLayer.getFullExtent(), 50);
                Toast.makeText(MainActivity.this, "TIFF 已经显示", Toast.LENGTH_LONG).show();
                Log.w(TAG, "initLayerList: " + map.getOperationalLayers().size());
                initLayerList();
            }
        });
    }
    private void loadRaster(final String name, final String path) {
        // create a raster from a local raster file
        Raster raster = new Raster(path);
        // create a raster layer
        final RasterLayer rasterLayer = new RasterLayer(raster);
        rasterLayer.setVisible(UserLayerOpenStatus.get(path));
        map.getOperationalLayers().add(rasterLayer);
        // set viewpoint on the raster
        rasterLayer.addDoneLoadingListener(new Runnable() {
            @Override
            public void run() {
                if (rasterLayer.getFullExtent() != null)
                {
                    mMapView.setViewpointGeometryAsync(rasterLayer.getFullExtent(), 50);
                    /*List<FieldNameSheet> FieldNameSheetList = new ArrayList<>();
                    LayerFieldsSheetList.add(new LayerFieldsSheet(name, path, FieldNameSheetList));
                    setRecyclerViewForDynamicChooseFrame();*/
                    Toast.makeText(MainActivity.this, "TIFF 已经显示", Toast.LENGTH_LONG).show();
                    //initLayerList(path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf(".")));
                    initLayerList();
                }
                else
                {
                    Toast.makeText(MainActivity.this, "TIFF 出错", Toast.LENGTH_LONG).show();
                    LitePal.deleteAll(UserLayer.class, "path = ?", path);
                    for (int i = 0; i < LayerFieldsSheetList.size(); i++) {

                        String pathss = LayerFieldsSheetList.get(i).getLayerPath();
                        if (pathss != null && pathss.equals(path))
                        {
                            LayerFieldsSheetList.remove(i);
                            UserLayerOpenStatus.remove(path);
                            break;
                        }
                        setRecyclerViewForDynamicChooseFrame();
                    }
                }
            }
        });
    }

    private String CurrentTifPath = "";
    private void loadRaster(final String path) {
        // create a raster from a local raster file
        Raster raster = new Raster(path);
        // create a raster layer
        final RasterLayer rasterLayer = new RasterLayer(raster);

        // TODO 栅格设色
        /*List<Integer> colors = new ArrayList<>();
        Random r = new Random();
        for (int i = 0; i <= 255; i++) {
            if (i <= 50)
                colors.add(i, FeaturevalueAndColor.get(i).intValue());
            else
                colors.add(i, FeaturevalueAndColor.get(i).intValue());
        }
        // create a colormap renderer
        ColormapRenderer colormapRenderer = new ColormapRenderer(colors);
        rasterLayer.setRasterRenderer(colormapRenderer);
*/
        /*
        // Override the renderer of the feature layer with a new unique value renderer
        UniqueValueRenderer uniqueValueRenderer = new UniqueValueRenderer();
        // Set the field to use for the unique values
        uniqueValueRenderer.getFieldNames().add(
                "STATE_ABBR"); //You can add multiple fields to be used for the renderer in the form of a list, in this case
        // we are only adding a single field

        // Create the symbols to be used in the renderer
        SimpleFillSymbol defaultFillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.NULL, Color.BLACK,
                new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.GRAY, 2));
        SimpleFillSymbol californiaFillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, Color.RED,
                new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.RED, 2));
        SimpleFillSymbol arizonaFillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, Color.GREEN,
                new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.GREEN, 2));
        SimpleFillSymbol nevadaFillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, Color.BLUE,
                new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.BLUE, 2));

        // Set default symbol
        uniqueValueRenderer.setDefaultSymbol(defaultFillSymbol);
        uniqueValueRenderer.setDefaultLabel("Other");

        // Set value for california
        List<Object> californiaValue = new ArrayList<>();
        // You add values associated with fields set on the unique value renderer.
        // If there are multiple values, they should be set in the same order as the fields are set
        californiaValue.add("CA");
        uniqueValueRenderer.getUniqueValues().add(
                new UniqueValueRenderer.UniqueValue("California", "State of California", californiaFillSymbol,
                        californiaValue));

        // Set value for arizona
        List<Object> arizonaValue = new ArrayList<>();
        // You add values associated with fields set on the unique value renderer.
        // If there are multiple values, they should be set in the same order as the fields are set
        arizonaValue.add("AZ");
        uniqueValueRenderer.getUniqueValues()
                .add(new UniqueValueRenderer.UniqueValue("Arizona", "State of Arizona", arizonaFillSymbol, arizonaValue));

        // Set value for nevada
        List<Object> nevadaValue = new ArrayList<>();
        // You add values associated with fields set on the unique value renderer.
        // If there are multiple values, they should be set in the same order as the fields are set
        nevadaValue.add("NV");
        uniqueValueRenderer.getUniqueValues()
                .add(new UniqueValueRenderer.UniqueValue("Nevada", "State of Nevada", nevadaFillSymbol, nevadaValue));
        */

        // set the ColormapRenderer on the RasterLayer

        // create a Map with imagery basemap
        //ArcGISMap map = new ArcGISMap(Basemap.createImagery());
        // add the map to a map view
        //mMapView.setMap(map);
        // add the raster as an operational layer
        rasterLayer.setVisible(UserLayerOpenStatus.get(path));
        map.getOperationalLayers().add(rasterLayer);
        // set viewpoint on the raster
        rasterLayer.addDoneLoadingListener(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, "TIFF 已经显示", Toast.LENGTH_LONG).show();
                //initLayerList(path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf(".")));
                initLayerList();
            }
        });
    }

    private void featureLayerShapefile() {
        // load the shapefile with a local path
        final ShapefileFeatureTable shapefileFeatureTable = new ShapefileFeatureTable(
                Environment.getExternalStorageDirectory() + "/临沧市基本农田/1229基本农田保护区shp/基本农田保护区.shp");

        shapefileFeatureTable.loadAsync();
        shapefileFeatureTable.addDoneLoadingListener(new Runnable() {
            @Override
            public void run() {
                if (shapefileFeatureTable.getLoadStatus() == LoadStatus.LOADED) {

                    // create a feature layer to display the shapefile
                    FeatureLayer shapefileFeatureLayer = new FeatureLayer(shapefileFeatureTable);

                    shapefileFeatureLayer.setVisible(true);
                    // add the feature layer to the map
                    //mMapView.getMap().getOperationalLayers().add(shapefileFeatureLayer);
                    map.getOperationalLayers().add(shapefileFeatureLayer);
                    Log.w(TAG, "initLayerList: " + map.getOperationalLayers().size());
                    // zoom the map to the extent of the shapefile

                    initLayerList();
                    Toast.makeText(MainActivity.this, "Shapefile 已经显示", Toast.LENGTH_LONG).show();
                    //mMapView.setViewpointAsync(new Viewpoint(shapefileFeatureLayer.getFullExtent()));
                } else {
                    String error = "Shapefile feature table failed to load: " + shapefileFeatureTable.getLoadError().toString();
                    Toast.makeText(MainActivity.this, error, Toast.LENGTH_LONG).show();
                    Log.e(TAG, error);
                }
            }
        });
    }


    private HashMap<String, Boolean> UserLayerOpenStatus = new HashMap<String, Boolean>();
    private HashMap<Integer, Integer> FeaturevalueAndColor;
    private void featureLayerShapefile(final String path, final int color) {
        // load the shapefile with a local path
        final ShapefileFeatureTable shapefileFeatureTable = new ShapefileFeatureTable(path);


        shapefileFeatureTable.loadAsync();
        shapefileFeatureTable.addDoneLoadingListener(new Runnable() {
            @Override
            public void run() {
                if (shapefileFeatureTable.getLoadStatus() == LoadStatus.LOADED) {

                    // create a feature layer to display the shapefile
                    FeatureLayer shapefileFeatureLayer = new FeatureLayer(shapefileFeatureTable);


                    SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.BLACK, 1.0f);
                    SimpleFillSymbol fillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, color, lineSymbol);

                    // create the Renderer
                    SimpleRenderer renderer = new SimpleRenderer(fillSymbol);

                    // set the Renderer on the Layer
                    shapefileFeatureLayer.setRenderer(renderer);

                    /*
                    if (path.contains("云县")) {
                        // TODO 矢量要素设色
                        // Override the renderer of the feature layer with a new unique value renderer
                        UniqueValueRenderer uniqueValueRenderer = new UniqueValueRenderer();
                        // Set the field to use for the unique values

                        // 设置用于唯一值渲染的字段
                        uniqueValueRenderer.getFieldNames().add("Value");

                        for (int i = 0; i < uniqueValueRenderer.getFieldNames().size(); i++) {
                            Log.w(TAG, "20201126lzy: " + uniqueValueRenderer.getFieldNames().get(i));
                        }
                        //You can add multiple fields to be used for the renderer in the form of a list, in this case
                        // we are only adding a single field

                        // Create the symbols to be used in the renderer
                        Random r = new Random();

                        for (int i = 0; i <= 255; i++) {
                            SimpleFillSymbol fillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, FeaturevalueAndColor.get(i).intValue(),
                                    new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, FeaturevalueAndColor.get(i).intValue(), 2));
                            if (i == 0) {
                                // Set default symbol
                                uniqueValueRenderer.setDefaultSymbol(fillSymbol);
                                uniqueValueRenderer.setDefaultLabel("Other");
                            }

                            // Set value for california
                            List<Object> californiaValue = new ArrayList<>();
                            // You add values associated with fields set on the unique value renderer.
                            // If there are multiple values, they should be set in the same order as the fields are set
                            californiaValue.add(String.valueOf(i));
                            uniqueValueRenderer.getUniqueValues().add(new UniqueValueRenderer.UniqueValue(String.valueOf(i), String.valueOf(i), fillSymbol, californiaValue));
                        }
                        shapefileFeatureLayer.setRenderer(uniqueValueRenderer);
                    }*/
                    shapefileFeatureLayer.setVisible(UserLayerOpenStatus.get(path));
                    // add the feature layer to the map
                    //mMapView.getMap().getOperationalLayers().add(shapefileFeatureLayer);
                    map.getOperationalLayers().add(shapefileFeatureLayer);
                    Log.w(TAG, "initLayerList: " + map.getOperationalLayers().size());
                    // zoom the map to the extent of the shapefile

                    initLayerList();
                    Toast.makeText(MainActivity.this, "Shapefile 已经显示", Toast.LENGTH_LONG).show();
                    //mMapView.setViewpointAsync(new Viewpoint(shapefileFeatureLayer.getFullExtent()));
                } else {
                    String error = "Shapefile feature table failed to load: " + shapefileFeatureTable.getLoadError().toString();
                    Toast.makeText(MainActivity.this, error, Toast.LENGTH_LONG).show();
                    Log.e(TAG, error);
                }
            }
        });
    }

    private void changeFeatureLayer(final QueryParameters query, final Polygon polygon){
        switch (QueriedFeature){
            case TDGHDL_FEATURE:
                QueriedFeature = DisplayEnum.XZQ_FEATURE;
                setTitle("行政区查询");
                Toast.makeText(MainActivity.this, "行政区查询", Toast.LENGTH_LONG).show();
                break;
            case XZQ_FEATURE:
                QueriedFeature = DisplayEnum.PTB_FEATURE;
                setTitle("批图斑查询");
                Toast.makeText(MainActivity.this, "批图斑查询", Toast.LENGTH_LONG).show();
                break;
            case PTB_FEATURE:
                QueriedFeature = DisplayEnum.DLTB09_FEATURE;
                setTitle("09年地类图斑查询");
                Toast.makeText(MainActivity.this, "09年地类图斑查询", Toast.LENGTH_LONG).show();
                break;
            case DLTB09_FEATURE:
                QueriedFeature = DisplayEnum.DLTB16_FEATURE;
                setTitle("16年地类图斑查询");
                Toast.makeText(MainActivity.this, "16年地类图斑查询", Toast.LENGTH_LONG).show();
                break;
            case DLTB16_FEATURE:
                QueriedFeature = DisplayEnum.DLTB17_FEATURE;
                setTitle("17年地类图斑查询");
                Toast.makeText(MainActivity.this, "17年地类图斑查询", Toast.LENGTH_LONG).show();
                break;
            case DLTB17_FEATURE:
                QueriedFeature = DisplayEnum.TDGHDL_FEATURE;
                setTitle("土地规划地类查询");
                Toast.makeText(MainActivity.this, "土地规划地类查询", Toast.LENGTH_LONG).show();
                break;
        }
        keyAndValues.clear();
        mCallout.dismiss();
        pieChartView.setVisibility(View.GONE);
        queryTaskForPolygon(query, polygon);
    }

    private void queryTaskForPolygon(final QueryParameters query, final Polygon polygon){
        try {
            Log.w(TAG, "queryTaskForPolygon: " + "开始按需查询！");
            FeatureTable mTable = null;
            switch (QueriedFeature){
                case TDGHDL_FEATURE:
                    mTable = TDGHDLFeatureLayer.getFeatureTable();//得到查询属性表
                    break;
                case XZQ_FEATURE:
                    mTable = XZCAreaFeatureLayer.getFeatureTable();//得到查询属性表
                    break;
                case PTB_FEATURE:
                    mTable = PTuBanFeatureLayer.getFeatureTable();//得到查询属性表
                    break;
                case DLTB09_FEATURE:
                    mTable = DLTB2009FeatureLayer.getFeatureTable();//得到查询属性表
                    break;
                case DLTB16_FEATURE:
                    mTable = DLTB2016FeatureLayer.getFeatureTable();//得到查询属性表
                    break;
                case DLTB17_FEATURE:
                    mTable = DLTB2017FeatureLayer.getFeatureTable();//得到查询属性表
                    break;
            }final ListenableFuture<FeatureQueryResult> featureQueryResult
                    = mTable.queryFeaturesAsync(query);
            featureQueryResult.addDoneListener(new Runnable() {
                @Override
                public void run() {
                    try {
                        removeGraphicsOverlayers();
                        FeatureQueryResult featureResul = featureQueryResult.get();
                        Geometry geometry1 = polygon;
                        List<QueryTaskInfo> queryTaskInfos = new ArrayList<>();
                        for (Object element : featureResul) {
                            if (element instanceof Feature) {
                                Feature mFeatureGrafic = (Feature) element;
                                Geometry geometry = null;
                                Polygon polygon1 = (Polygon) mFeatureGrafic.getGeometry();
                                geometry = GeometryEngine.intersection(GeometryEngine.project(polygon1, SpatialReference.create(4523)), polygon);
                                boolean isOK = false;
                                QueryTaskInfo queryTaskInfo = new QueryTaskInfo(GeometryEngine.areaGeodetic(geometry, new AreaUnit(AreaUnitId.SQUARE_KILOMETERS), GeodeticCurveType.GEODESIC) * 1500);
                                Log.w(TAG, "geometry2type: " + queryTaskInfo.getArea());
                                //Log.w(TAG, "run: " + geometry1.getSpatialReference().getWkid());
                                GraphicsOverlay graphicsOverlay_1 = new GraphicsOverlay();
                                SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.BLUE, 3);
                                Graphic fillGraphic = new Graphic(geometry, lineSymbol);
                                graphicsOverlay_1.getGraphics().add(fillGraphic);
                                mMapView.getGraphicsOverlays().add(graphicsOverlay_1);
                                Map<String, Object> mQuerryString = mFeatureGrafic.getAttributes();
                                //calloutContent.setSingleLine();
                                // format coordinates to 4 decimal places
                                //String str = "";
                                //List<KeyAndValue> keyAndValues = new ArrayList<>();
                                switch (QueriedFeature){
                                    case TDGHDL_FEATURE:
                                        for (String key : mQuerryString.keySet()) {
                                            //str = str + key + " : " + String.valueOf(mQuerryString.get(key)) + "\n";
                                            switch (key){
                                                case "GHDLMC":
                                                    queryTaskInfo.setTypename(String.valueOf(mQuerryString.get(key)));
                                                    break;
                                                case "GHDLBM":
                                                    queryTaskInfo.setType(String.valueOf(mQuerryString.get(key)));
                                                    break;
                                                case "XZQMC":
                                                    queryTaskInfo.setXzq(String.valueOf(mQuerryString.get(key)));
                                                    break;
                                                case "XZQDM":
                                                    queryTaskInfo.setXzqdm(String.valueOf(mQuerryString.get(key)));
                                                    break;
                                            }
                                            isOK = true;
                                        }
                                        break;
                                    case XZQ_FEATURE:
                                        for (String key : mQuerryString.keySet()) {
                                            //Log.w(TAG, "行政区: " + key);
                                            //str = str + key + " : " + String.valueOf(mQuerryString.get(key)) + "\n";
                                            if (key.equals("XZQDM")) {
                                                //Log.w(TAG, "行政区界线: " + String.valueOf(mQuerryString.get(key)));
                                                //Log.w(TAG, "行政区界线 面积: " + queryTaskInfo.getArea());
                                                String str = String.valueOf(mQuerryString.get(key));
                                                if (str.length() >= 9) {
                                                    queryTaskInfo.setXzqdm(str);
                                                    isOK = true;
                                                } else
                                                    break;
                                            } else if (key.equals("XZQMC")) {
                                                queryTaskInfo.setXzq(String.valueOf(mQuerryString.get(key)));
                                            }/*else {
                                                queryTaskInfo.setXzq(String.valueOf(mQuerryString.get(key)));
                                            }*/
                                        }
                                        break;
                                    case PTB_FEATURE:
                                        for (String key : mQuerryString.keySet()) {
                                            Log.w(TAG, "行政区: " + key);
                                            //str = str + key + " : " + String.valueOf(mQuerryString.get(key)) + "\n";
                                            if (key.equals("XJXZQHDM")) {
                                                //Log.w(TAG, "行政区界线: " + String.valueOf(mQuerryString.get(key)));
                                                //Log.w(TAG, "行政区界线 面积: " + queryTaskInfo.getArea());
                                                String str = String.valueOf(mQuerryString.get(key));
                                                queryTaskInfo.setXzqdm(str);
                                                isOK = true;
                                            } else if (key.equals("BZPZWH")) {
                                                queryTaskInfo.setXzq(String.valueOf(mQuerryString.get(key)));
                                            }
                                        }
                                        break;
                                    default:
                                        for (String key : mQuerryString.keySet()) {
                                            Log.w(TAG, "行政区: " + key);
                                            //str = str + key + " : " + String.valueOf(mQuerryString.get(key)) + "\n";
                                            if (key.equals("DLBM")) {
                                                //Log.w(TAG, "行政区界线: " + String.valueOf(mQuerryString.get(key)));
                                                //Log.w(TAG, "行政区界线 面积: " + queryTaskInfo.getArea());
                                                String str = String.valueOf(mQuerryString.get(key));
                                                queryTaskInfo.setXzqdm(str);
                                                isOK = true;
                                            } else if (key.equals("DLMC")) {
                                                queryTaskInfo.setXzq(String.valueOf(mQuerryString.get(key)));
                                            }
                                        }
                                        break;
                                }
                                if (isOK)
                                    queryTaskInfos.add(queryTaskInfo);

                            }
                        }
                        wholeArea = GeometryEngine.areaGeodetic(geometry1, new AreaUnit(AreaUnitId.SQUARE_KILOMETERS), GeodeticCurveType.GEODESIC) * 1500;
                        GraphicsOverlay graphicsOverlay_1 = new GraphicsOverlay();
                        SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.RED, 3);
                        Graphic fillGraphic = new Graphic(geometry1, lineSymbol);
                        graphicsOverlay_1.getGraphics().add(fillGraphic);
                        mMapView.getGraphicsOverlays().add(graphicsOverlay_1);
                        List<SliceValue> sliceValues = new ArrayList<>();
                        TextView calloutContent = new TextView(getApplicationContext());
                        calloutContent.setTextColor(Color.BLACK);
                        String str = "";
                        keyAndValues = new ArrayList<>();

                        if (QueriedFeature == DisplayEnum.TDGHDL_FEATURE) {
                            for (int i = 0; i < queryTaskInfos.size(); ++i) {
                                if (i == 0 && queryTaskInfos.get(i).getArea() != 0)
                                    keyAndValues.add(new KeyAndValue(queryTaskInfos.get(i).getTypename(), Double.toString(queryTaskInfos.get(i).getArea())));
                                else {
                                    boolean hasKey = false;
                                    for (int j = 0; j < keyAndValues.size(); ++j) {
                                        if (queryTaskInfos.get(i).getTypename().equals(keyAndValues.get(j).getName())) {
                                            hasKey = true;
                                            keyAndValues.get(j).setValue(Double.toString(Double.valueOf(keyAndValues.get(j).getValue()) + queryTaskInfos.get(i).getArea()));
                                            break;
                                        }
                                    }
                                    if (!hasKey && queryTaskInfos.get(i).getArea() != 0) {
                                        keyAndValues.add(new KeyAndValue(queryTaskInfos.get(i).getTypename(), Double.toString(queryTaskInfos.get(i).getArea())));
                                    }
                                }
                            }
                        } else {
                            for (int i = 0; i < queryTaskInfos.size(); ++i) {
                                if (i == 0 && queryTaskInfos.get(i).getArea() != 0)
                                    keyAndValues.add(new KeyAndValue(queryTaskInfos.get(i).getXzq(), Double.toString(queryTaskInfos.get(i).getArea())));
                                else {
                                    boolean hasKey = false;
                                    for (int j = 0; j < keyAndValues.size(); ++j) {
                                        if (queryTaskInfos.get(i).getXzq().equals(keyAndValues.get(j).getName())) {
                                            hasKey = true;
                                            keyAndValues.get(j).setValue(Double.toString(Double.valueOf(keyAndValues.get(j).getValue()) + queryTaskInfos.get(i).getArea()));
                                            break;
                                        }
                                    }
                                    if (!hasKey && queryTaskInfos.get(i).getArea() != 0) {
                                        keyAndValues.add(new KeyAndValue(queryTaskInfos.get(i).getXzq(), Double.toString(queryTaskInfos.get(i).getArea())));
                                    }
                                }
                            }
                        }

                        DecimalFormat decimalFormat = new DecimalFormat("0.00");
                        DecimalFormat decimalFormat1 = new DecimalFormat("0.0");
                        for (int j = 0; j < keyAndValues.size(); ++j) {
                            float value = Float.valueOf(keyAndValues.get(j).getValue()) / (float) wholeArea;
                            SliceValue sliceValue = new SliceValue(value, ChartUtils.pickColor());
                            sliceValue.setLabel(keyAndValues.get(j).getName() + ":" + decimalFormat1.format(value * 100) + "%");
                            sliceValues.add(sliceValue);
                            if (j < keyAndValues.size() - 1) {
                                str = str + keyAndValues.get(j).getName() + ": " + decimalFormat1.format(Double.valueOf(keyAndValues.get(j).getValue())) + "亩";
                                str = str + ",占比: " + decimalFormat.format(Double.valueOf(keyAndValues.get(j).getValue()) / wholeArea * 100) + "%" + "\n";
                            } else {
                                str = str + keyAndValues.get(j).getName() + ": " + decimalFormat1.format(Double.valueOf(keyAndValues.get(j).getValue())) + "亩";
                                str = str + ",占比: " + decimalFormat.format(Double.valueOf(keyAndValues.get(j).getValue()) / wholeArea * 100) + "%";
                            }
                        }
                        //TODO 基本农田
                        if (str == null || str.isEmpty())
                            str =  "基本农田保护区: " + decimalFormat1.format(jbntArea) + "亩,占比: " + decimalFormat.format(jbntArea / wholeArea * 100) + "%";
                        else
                            str = str + "\n" + "基本农田保护区: " + decimalFormat1.format(jbntArea) + "亩,占比: " + decimalFormat.format(jbntArea / wholeArea * 100) + "%";

                        calloutContent.setText(str);
                        // get callout, set content and show
                        mCallout.setLocation(new Point(geometry1.getExtent().getCenter().getX(), geometry1.getExtent().getYMax(), SpatialReference.create(4523)));
                        mCallout.setContent(calloutContent);
                        Log.w(TAG, "run: callout" + mCallout.isShowing());
                        mCallout.show();
                        inMap = true;
                        PieChartData pieChartData = new PieChartData(sliceValues);
                        pieChartData.setHasLabels(true);
                        pieChartData.setHasLabelsOutside(false);
                        pieChartData.setHasLabelsOnlyForSelected(true);
                        pieChartView.setOnValueTouchListener(new ValueTouchListener());
                        pieChartView.setPieChartData(pieChartData);
                        pieChartView.setValueSelectionEnabled(true);
                        pieChartView.setVisibility(View.VISIBLE);
                        FloatingActionButton change = (FloatingActionButton) findViewById(R.id.ChangeQuery);
                        change.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                changeFeatureLayer(query, polygon);
                            }
                        });
                            /*pieChartView.setOnLongClickListener(new View.OnLongClickListener() {
                                @Override
                                public boolean onLongClick(View v) {
                                    return true;
                                }
                            });*/
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (NullPointerException e) {
            changeFeatureLayer(query, polygon);
        }catch (Exception e) {
            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void readOtherData(){
        //
        List<memoryxzqinfo> memoryxzqinfos = LitePal.findAll(memoryxzqinfo.class);
        /*for (int i = 0; i < memoryxzqinfos.size(); i++) {
            memoryxzqinfos.
        }*/
        if (memoryxzqinfos.size()<64)
        {
            LitePal.deleteAll(memoryxzqinfo.class);
            File file1 = new File(Environment.getExternalStorageDirectory().toString() + "/永善国土/永善县.txt");
            try {
                FileInputStream in = new FileInputStream(file1);
                InputStreamReader reader = new InputStreamReader(in, "utf-8");
                BufferedReader br = new BufferedReader(reader);
                String s = null;
                while ((s = br.readLine()) != null) {
                    s = s.replace("\n", "");
                    String[] strings = s.split(";");
                    double SumArea = 0;
                    memoryxzqinfo memoryxzqinfo = new memoryxzqinfo();
                    memoryxzqinfo.setLayername(strings[0]);
                    memoryxzqinfo.setName(strings[1]);
                    String keyAndValues = "";
                    for (int i = 2; i < strings.length; i++) {
                        double Area = Double.valueOf(strings[i].substring(strings[i].indexOf(",")+1))/666.67;
                        SumArea += Area;
                        if (strings.length-1 == i)
                            keyAndValues += strings[i].substring(0, strings[i].indexOf(",")) + ":" + Area + "," + SumArea;
                        else
                            keyAndValues += strings[i].substring(0, strings[i].indexOf(",")) + ":" + Area + ",";
                    }
                    Log.w(TAG, "readOtherData: " + strings[0] + ", " + strings[1] + ", " + keyAndValues);
                    memoryxzqinfo.setKeyAndValues(keyAndValues);
                    memoryxzqinfo.save();
                }
                reader.close();
                in.close();
            } catch (UnsupportedEncodingException | FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //List<xzq> xzqs = LitePal.findAll(xzq.class);
        }
        /*List<xzq> xzqList1 = LitePal.findAll(xzq.class);
        Log.w(TAG, "readXZQ: " + xzqList.size());
        for (int i = 0; i < xzqList1.size(); i++) {
            Log.w(TAG, "readXZQ: " + xzqList1.get(i).getXzqmc() + ", " + xzqList1.get(i).getGrade());
        }*/
    }

    private void readXZQ(){
        //LitePal.deleteAll(xzq.class);
        List<xzq> xzqList = LitePal.findAll(xzq.class);
        if (xzqList.size()==0)
        {
            File file1 = new File(Environment.getExternalStorageDirectory().toString() + "/永善国土/永善县行政区划树.txt");
            try {
                FileInputStream in = new FileInputStream(file1);
                InputStreamReader reader = new InputStreamReader(in, "utf-8");
                BufferedReader br = new BufferedReader(reader);
                String s = null;
                while ((s = br.readLine()) != null) {
                    s = s.replace("\n", "");
                    String[] strings = s.split(",");
                    xzq xzq = new xzq();
                    xzq.setXzqdm(strings[0]);
                    xzq.setXzqmc(strings[1]);
                    xzq.setSjxzq(strings[2]);
                    xzq.setType(strings[3]);
                    xzq.save();
                }
                reader.close();
                in.close();
            } catch (UnsupportedEncodingException | FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //List<xzq> xzqs = LitePal.findAll(xzq.class);
            DataUtil.xzqClassify(LitePal.findAll(xzq.class));
        }
        /*List<xzq> xzqList1 = LitePal.findAll(xzq.class);
        Log.w(TAG, "readXZQ: " + xzqList.size());
        for (int i = 0; i < xzqList1.size(); i++) {
            Log.w(TAG, "readXZQ: " + xzqList1.get(i).getXzqmc() + ", " + xzqList1.get(i).getGrade());
        }*/
    }

    private void StartMapQueryStatus(){
        if (Deleting){
            StopDeletingStatusForStartMapQuery();
        }
        else {
            StartMapQueryFunction();
        }
    }

    private void StartMapQueryFunction(){
        RunningFunction = DisplayEnum.FUNC_ANA;
        MapQuery = true;
        //ScaleShow.setText("云南省地图院数据分院研发 当前比例  1 : " + String.valueOf((int)mMapView.getMapScale()) + " (图面查询中)");
        ShowScreenBottomTipText();
        Toast.makeText(MainActivity.this, R.string.OpenMapQuery, Toast.LENGTH_LONG).show();
    }

    private void StopMapQueryStatus(){
        RunningFunction = DisplayEnum.FUNC_NONE;
        MapQuery = false;
        mCallout.dismiss();
        mMapView.getGraphicsOverlays().clear();
        drawGraphicsOverlayer();
        //ScaleShow.setText("云南省地图院数据分院研发 当前比例  1 : " + String.valueOf((int)mMapView.getMapScale()));
        ShowScreenBottomTipText();
        pieChartView.setVisibility(View.GONE);
        Toast.makeText(MainActivity.this, R.string.CloseMapQuery, Toast.LENGTH_LONG).show();
        RemoveFinishButton();
    }

    private void mapQueryBtEvent(){
        if (!MapQuery) {
            StartMapQueryStatus();
        }
        else {
            StopMapQueryStatus();
        }
    }

    // TODO 2021/1/22 完成模式文本标识模块
    private void ShowScreenBottomTipText(){
        String BaseString = "云南省地图院数据分院研发 当前比例  1 : " + String.valueOf((int)mMapView.getMapScale());
        if (MapQuery)
            BaseString += " 图面查询中";
        if (Deleting)
            BaseString += " 要素批量删除中";
        if (IsRecordingTrail)
            BaseString += " 正在记录轨迹";
        ScaleShow.setText(BaseString);
    }

    private void initWidgetAndVariable(){
        initWidget();
        initVariable();
    }

    private boolean checkWps(){
        Intent intent = getPackageManager().getLaunchIntentForPackage("cn.wps.moffice_eng");//WPS个人版的包名
        if (intent == null) {
            return false;
        } else {
            return true;
        }
    }

    // 回调接口
    public interface WpsInterface {
        void doRequest(String filePath);//filePath为文档的保存路径
        void doFinish();
    }
    /*
    // 广播接收器
    private class WpsCloseListener extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (intent.getAction().equals("cn.wps.moffice.file.save")) {
                    String fileSavePath = intent.getExtras().getString(Define.SAVE_PATH);
                    if(canWrite) {
                        wpsInterface.doRequest(fileSavePath);// 保存回调
                    }
                } else if (intent.getAction().equals("cn.wps.moffice.file.close")||
                        intent.getAction().equals("com.kingsoft.writer.back.key.down")) {
                    wpsInterface.doFinish();// 关闭,返回回调
                    mActivity.unregisterReceiver(wpsCloseListener);//注销广播
                }
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void initWpsCloseListener() {
        wpsCloseListener = new WpsCloseListener();
        IntentFilter filter = new IntentFilter(Define.OFFICE_SERVICE_ACTION);
        filter.addAction("com.kingsoft.writer.back.key.down");//按下返回键
        filter.addAction("com.kingsoft.writer.home.key.down");//按下home键
        filter.addAction("cn.wps.moffice.file.save");//保存
        filter.addAction("cn.wps.moffice.file.close");//关闭
        mActivity.registerReceiver(wpsCloseListener,filter);//注册广播
    }

    public void openDoc() {
        Bundle bundle = new Bundle();
        if (canWrite) {// 判断是否可以编辑文档
            bundle.putString("OpenMode", "Normal");// 一般模式
        } else {
            bundle.putString("OpenMode", "ReadOnly");// 只读模式
        }
        bundle.putBoolean("SendSaveBroad", true);// 关闭保存时是否发送广播
        bundle.putBoolean("SendCloseBroad", true);// 关闭文件时是否发送广播
        bundle.putBoolean("HomeKeyDown", true);// 按下Home键
        bundle.putBoolean("BackKeyDown", true);// 按下Back键
        bundle.putBoolean("IsShowView", false);// 是否显示wps界面
        bundle.putBoolean("AutoJump", true);// //第三方打开文件时是否自动跳转
        //设置广播
        bundle.putString("ThirdPackage", mActivity.getPackageName());
        //第三方应用的包名，用于对改应用合法性的验证
        //bundle.putBoolean(Define.CLEAR_FILE, true);
        //关闭后删除打开文件
        intent.setAction(android.content.Intent.ACTION_MAIN);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        intent.setData(Uri.parse(fileUrl));
        intent.putExtras(bundle);
        mActivity.startActivity(intent);
    }*/

    boolean openFile(String path) {
        try {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString(WpsModel.OPEN_MODE, WpsModel.OpenMode.NORMAL); // 打开模式
            //bundle.putBoolean(WpsModel.SEND_CLOSE_BROAD, true); // 关闭时是否发送广播
            //bundle.putString(WpsModel.THIRD_PACKAGE, getPackageName()); // 第三方应用的包名，用于对改应用合法性的验证
            //bundle.putBoolean(WpsModel.CLEAR_TRACE, true);// 清除打开记录
            // bundle.putBoolean(CLEAR_FILE, true); //关闭后删除打开文件
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setAction(android.content.Intent.ACTION_VIEW);
            intent.setClassName(WpsModel.PackageName.NORMAL, WpsModel.ClassName.NORMAL);

            File file = new File(path);
            if (file == null || !file.exists()) {
                System.out.println("文件为空或者不存在");
                return false;
            }


            Uri uri = null;
            if (Build.VERSION.SDK_INT >= 24) {
                //locError(Environment.getExternalStorageDirectory() + "/maphoto/" + Long.toString(timenow) + ".jpg");
                uri = FileProvider.getUriForFile(MainActivity.this, "com.android.displaymap.fileprovider", file);

            } else uri = Uri.fromFile(file);
            Log.w(TAG, "openFile: " + uri);
            intent.setData(uri);
            Log.w(TAG, "openFile: " + Uri.parse(path));
            //intent.setData(Uri.parse(path));
            intent.putExtras(bundle);
            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                System.out.println("打开wps异常：" + e.toString());
                e.printStackTrace();
                return false;
            }
            return true;
        }
        catch (Exception ioe){
            return false;
        }
    }

    private void initWidget(){
        recyclerViewForP = (RecyclerView) findViewById(R.id.RightRecyclerView);

        FloatingActionButton ReadPDFFAB = findViewById(R.id.ReadPDFFAB);
        ReadPDFFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkWps()){
                    Intent intent = new Intent(MainActivity.this, Activity_FileManage.class);
                    intent.putExtra("type", ".pdf");
                    //intent.putExtra("type", ".shp");
                    startActivityForResult(intent, EnumClass.GET_PDF_FILE);

                }
                else{
                    Toast.makeText(MainActivity.this, "请先安装WPS", Toast.LENGTH_LONG).show();
                }
            }
        });

        FloatingActionButton InputDataBt = findViewById(R.id.InputData);
        InputDataBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                builder1.setTitle("提示");
                builder1.setMessage("请选择你要添加的文件类型:" + "\n" + "（将在下次启动时生效）");
                builder1.setNegativeButton("SHP文件", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        InputDataForShp();
                    }
                });
                builder1.setPositiveButton("TIF文件", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        InputDataForTif();
                    }
                });
                builder1.show();
            }
        });
        /*Button InputData = findViewById(R.id.InputDataBT);
        InputData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                builder1.setTitle("提示");
                builder1.setMessage("请选择你要添加的文件类型:" + "\n" + "（将在下次启动时生效）");
                builder1.setNegativeButton("SHP文件", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        InputDataForShp();
                    }
                });
                builder1.setPositiveButton("TIF文件", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        InputDataForTif();
                    }
                });
                builder1.show();
            }
        });*/
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        pieChartView = (PieChartView) findViewById(R.id.chart);
        ShowTextButton();
        //按钮添加要素
        DrawFeature = (FloatingActionButton)findViewById(R.id.DrawFeature);
        DrawFeature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //showPopueWindowForMessure();
                if (DrawType == DisplayEnum.DRAW_NONE) {
                    mCallout.dismiss();
                    pieChartView.setVisibility(View.GONE);
                    showPopueWindowForMessure();
                }
                else {
                    if (DrawType == DisplayEnum.DRAW_POLYGON && pointCollection.size() >= 3){
                        final Polygon polygon = new Polygon(pointCollection);
                        QueryParameters query = new QueryParameters();
                        query.setGeometry(polygon);// 设置空间几何对象
                        if (isFileExist(StaticVariableEnum.GDBROOTPATH) && isFileExist(StaticVariableEnum.PGDBROOTPATH) && MapQuery) {
                            //FeatureLayer featureLayer=(FeatureLayer) mMapView.getMap().getOperationalLayers().get(10);
                            queryTaskForPolygon(query, polygon);
                        } else
                            Toast.makeText(MainActivity.this, R.string.QueryError_2, Toast.LENGTH_SHORT).show();
                        if (!inMap) mCallout.dismiss();

                    }else if (DrawType == DisplayEnum.DRAW_POLYLINE && pointCollection.size() >= 2){
                        Polyline polyline = new Polyline(pointCollection);
                        GraphicsOverlay graphicsOverlay_1 = new GraphicsOverlay();
                        SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.GREEN, 3);
                        Graphic fillGraphic = new Graphic(polyline, lineSymbol);
                        graphicsOverlay_1.getGraphics().add(fillGraphic);
                        mMapView.getGraphicsOverlays().add(graphicsOverlay_1);
                        Log.w(TAG, "onClick: " + pointCollection.size());
                        Log.w(TAG, "onClick: " + GeometryEngine.lengthGeodetic(polyline, new LinearUnit(LinearUnitId.METERS), GeodeticCurveType.GEODESIC));
                        DecimalFormat format = new DecimalFormat("0.00");
                        Toast.makeText(MainActivity.this, format.format(GeometryEngine.lengthGeodetic(new Polyline(pointCollection, SpatialReference.create(4523)), new LinearUnit(LinearUnitId.METERS), GeodeticCurveType.GEODESIC)) + "米", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(MainActivity.this, "请构建面(至少三个点)后进行查询", Toast.LENGTH_SHORT).show();
                    }
                    DrawType = DisplayEnum.DRAW_NONE;
                    mapQueryBtEvent();
                }
            }
        });
        North = (ImageView) findViewById(R.id.North);
        North.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //North.setRotation(0);
                if (mMapView.getMapRotation() != 0) {
                    isNorth = false;
                    mMapView.setViewpointRotationAsync(0);
                    //Log.w(TAG, "Exception: " + );
                }
                else isNorth = true;
            }
        });
        ResetBT = (FloatingActionButton) findViewById(R.id.Reset);
        ResetBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.w(TAG, "onClick: " + OriginLocation.getX() + " ; " + OriginLocation.getY());
                if (OriginLocation != null) {
                    mMapView.setViewpointCenterAsync(OriginLocation, 700000);
                    mMapView.setViewpointRotationAsync(0);
                }else {
                    ResetMapView();
                }
            }
        });
        ScaleShow = (TextView) findViewById(R.id.scale);
        MapQueryBT = (FloatingActionButton) findViewById(R.id.MapQuery);
        MapQueryBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapQueryBtEvent();
                if (MapQuery) {
                    if (LitePal.where("type = ?", Integer.toString(UserLayer.SHP_FILE)).find(UserLayer.class).size() > 0) {
                        /*AlertDialog.Builder dialog1 = new AlertDialog.Builder(MainActivity.this);
                        dialog1.setTitle("提示");
                        dialog1.setMessage("你想查询什么内容");
                        dialog1.setCancelable(false);
                        dialog1.setPositiveButton("UserLayer", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                isQueryUserLayer = true;
                            }
                        });
                        dialog1.setNegativeButton("普通查询", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                isQueryUserLayer = false;
                            }
                        });
                        dialog1.show();*/
                        Log.w(TAG, "onClick: 2020/9/7" + QueriedLayerIndex + ", " + BaseLayerFieldsSheetList.size());
                        if (QueriedLayerIndex == -1 || QueriedLayerIndex >= BaseLayerFieldsSheetList.size())
                            isQueryUserLayer = true;
                        else
                            isQueryUserLayer = false;
                    }
                    else
                        isQueryUserLayer = false;
                }
            }
        });
        whiteBlank_fab = (FloatingActionButton) findViewById(R.id.whiteBlank);
        whiteBlank_fab.setImageResource(R.drawable.ic_brush_black_24dp);
        whiteBlank_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isOpenWhiteBlank){
                    pieChartView.setVisibility(View.GONE);
                    isOpenWhiteBlank = true;
                    whiteBlank_fab.setImageResource(R.drawable.ic_cancel_black_24dp);
                    setTitle(R.string.WhiteBlankDrawing);
                    showPopueWindowForWhiteblank();
                    whiteBlank_fab.setVisibility(View.INVISIBLE);
                }
            }
        });

        final FloatingActionButton TrailBt = (FloatingActionButton) findViewById(R.id.StartTrail);
        TrailBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLocateEnd){
                    //没有开始轨迹记录
                    showTrailDialogForStart(TrailBt);
                }
                else{
                    //开始轨迹记录
                    showTrailDialogForStop(TrailBt);
                }
            }
        });

        InitDeletingBt();

        mMapView = findViewById(R.id.mapView);// = new ArcGISMap(Basemap.Type.TOPOGRAPHIC, 34.056295, -117.195800, 16);

        //去除水印
        mMapView.setAttributionTextVisible(false);

        mMapView.setOnTouchListener(new DefaultMapViewOnTouchListener(this, mMapView) {
            @Override
            public boolean  onSingleTapConfirmed(MotionEvent v) {

               // TODO 解决因未加载mmpk而单击屏幕造成的闪退
                OnTap(v);
                return true;
            }
        });

        mMapView.addMapRotationChangedListener(new MapRotationChangedListener() {
            @Override
            public void mapRotationChanged(MapRotationChangedEvent mapRotationChangedEvent) {
                North.setRotation(360 - (float) mapRotationChangedEvent.getSource().getMapRotation());
                Log.w(TAG, "mapRotationChanged: " + mapRotationChangedEvent.getSource().getMapRotation());
            }
        });
        mMapView.addMapScaleChangedListener(new MapScaleChangedListener() {
            @Override
            public void mapScaleChanged(MapScaleChangedEvent mapScaleChangedEvent) {
                /*if (!MapQuery) ScaleShow.setText("云南省地图院数据分院研发 当前比例  1 : " + String.valueOf((int)mMapView.getMapScale()));
                else ScaleShow.setText("云南省地图院数据分院研发 当前比例  1 : " + String.valueOf((int)mMapView.getMapScale()) + " (图面查询中)");*/
                ShowScreenBottomTipText();
                ShowOrRemoveLayer();
            }
        });
        LocHereBT = (FloatingActionButton) findViewById(R.id.LocHere);
        LocHereBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLocation != null) {
                    if (mLocation.getX() != 0) mMapView.setViewpointCenterAsync(mLocation, 3000);
                    locationDisplay.setAutoPanMode(LocationDisplay.AutoPanMode.COMPASS_NAVIGATION);
                    locationDisplay.startAsync();
                }else {
                    Toast.makeText(MainActivity.this, R.string.LocError_1, Toast.LENGTH_LONG).show();
                }
            }
        });

        FloatingActionButton outputbt = (FloatingActionButton) findViewById(R.id.OutputData);
        outputbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            OutputData();


                        }

                    }).start();
                }catch (Exception e)
                {
                    Log.w(TAG, "error: " + e.toString());
                }
                //setFAMVisible(false);
            }
        });
        /*Button OutputBT = findViewById(R.id.OutputDataBT);
        OutputBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            OutputData();


                        }

                    }).start();
                }catch (Exception e)
                {
                    Log.w(TAG, "error: " + e.toString());
                }
                //setFAMVisible(false);
            }
        });*/
    }

    private void ShowOrRemoveLayer(){
        // TODO 2021/7/26
        if ((int)mMapView.getMapScale()<50000) {
            /*
            for (int i = 0; i < layerList.size(); i++) {
                String Name = layerList.get(i).getName();
                if (Name.equals("地名点") || Name.equals("土地利用变更调查数据2020年") || Name.equals("二调地类图斑") || Name.equals("土地规划地类") || Name.equals("永善县稳定耕地") || Name.equals("土地承包经营权") || Name.equals("永久基本农田保护红线")) {
                    layerList.get(i).setStatus(true);
                    layers.get(i).getLayer().setVisible(true);
                }
            }*/
        }
        else{
            for (int i = 0; i < layerList.size(); i++) {
                String Name = layerList.get(i).getName();
                if (Name.equals("地名点") || Name.equals("土地利用变更调查数据2020年") || Name.equals("二调地类图斑") || Name.equals("土地规划地类") || Name.equals("永善县稳定耕地") || Name.equals("土地承包经营权") || Name.equals("永久基本农田保护红线")) {
                    layerList.get(i).setStatus(false);
                    layers.get(i).getLayer().setVisible(false);
                }
            }
        }
        setLeftRecyclerView();
    }

    private void OnTap(MotionEvent v){
        Log.d(TAG, "onSingleTapConfirmed: " + v.toString());
        // get the point that was clicked and convert it to a point in map coordinates
        final android.graphics.Point screenPoint = new android.graphics.Point(Math.round(v.getX()),
                Math.round(v.getY()));
        // create a map point from screen point
        final Point mapPoint = mMapView.screenToLocation(screenPoint);
        if (mapPoint!=null) {
            // convert to WGS84 for lat/lon format
            final Point wgs84Point = (Point) GeometryEngine.project(mapPoint, SpatialReference.create(4523));
                /*if (numx == 0) {
                    Point pt = mMapView.screenToLocation(new android.graphics.Point(Math.round(mMapView.getWidth() / 2), Math.round(((mMapView.getTop() + getStatusBarHeight(MainActivity.this) + getDaoHangHeight(MainActivity.this)) + (mMapView.getBottom() + getStatusBarHeight(MainActivity.this) + getDaoHangHeight(MainActivity.this))) / 2)));
                    //Log.w(TAG, "run: " + pt.getX() + "; " + pt.getY());
                    OriginLocation = (Point) GeometryEngine.project(pt, SpatialReference.create(4523));
                    numx++;
                }*/
            // create a textview for the callout
                /*TextView calloutContent = new TextView(getApplicationContext());
                calloutContent.setTextColor(Color.BLACK);
                calloutContent.setSingleLine();
                // format coordinates to 4 decimal places
                calloutContent.setText("Lat: " +  String.format("%.4f", wgs84Point.getY()) +
                        ", Lon: " + String.format("%.4f", wgs84Point.getX()));

                // get callout, set content and show
                Callout mCallout = mMapView.getCallout();
                mCallout.setLocation(mapPoint);
                mCallout.setContent(calloutContent);
                mCallout.show();*/

            final Point clickPoint = mMapView.screenToLocation(screenPoint);
            if (RunningFunction == DisplayEnum.FUNC_ANA) {
                if (QueryProcessType == DisplayEnum.NOQUERY && DrawType == DisplayEnum.DRAW_NONE) {
                    mCallout = mMapView.getCallout();
                    mCallout.dismiss();
                    pieChartView.setVisibility(View.GONE);
                    // center on tapped point
                    //mMapView.setViewpointCenterAsync(wgs84Point);
                    Log.w(TAG, "onSingleTapConfirmed: " + wgs84Point);
                    inMap = false;
                    Log.w(TAG, "onSingleTapConfirmed: ");

                    Log.w(TAG, "onSingleTapConfirmed: " + mapPoint);
                    if (!queryPoi(mMapView.locationToScreen(clickPoint)))
                        if (!inUserDrawedTuban(clickPoint)) {
                            // TODO 2020/9/8 重要图斑查询逻辑！！！
                            if (!isQueryUserLayer) {
                                // 原版
                                //queryTB(clickPoint, mapPoint);
                                // 新版
                                queryTBFor20200903(clickPoint, mapPoint);
                                //queryUserTB(StaticVariableEnum.DLLCROOTPATH, LCRAFeatureLayer, clickPoint, mapPoint);
                            } else {
                                List<UserLayer> userLayerList = LitePal.where("type = ?", Integer.toString(UserLayer.SHP_FILE)).find(UserLayer.class);
                                int size = userLayerList.size();
                                for (int i = 0; i < size; ++i) {
                                    String path = userLayerList.get(i).getPath();
                                    if (QueriedLayerIndex != -1 && path.equals(LayerFieldsSheetList.get(QueriedLayerIndex).getLayerPath())) {
                                        ShapefileFeatureTable shapefileFeatureTable = new ShapefileFeatureTable(path);
                                        pointCollection = null;
                                        if (queryUserTB(path, new FeatureLayer(shapefileFeatureTable), clickPoint, mapPoint)) {
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    //queryPTuBan(clickPoint, mapPoint);
                    //FeatureLayer featureLayer=(FeatureLayer) mMapView.getMap().getBasemap().getBaseLayers().get(0);
                } else if (DrawType == DisplayEnum.DRAW_POLYGON) {
                    pointCollection.add(wgs84Point);
                    if (pointCollection.size() == 1) {
                        removeGraphicsOverlayers();
                        GraphicsOverlay graphicsOverlay_1 = new GraphicsOverlay();
                        SimpleMarkerSymbol lineSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.GREEN, 3);
                        Graphic fillGraphic = new Graphic(new Point(pointCollection.get(0).getX(), pointCollection.get(0).getY(), SpatialReference.create(4523)), lineSymbol);
                        graphicsOverlay_1.getGraphics().add(fillGraphic);
                        mMapView.getGraphicsOverlays().add(graphicsOverlay_1);
                    } else if (pointCollection.size() == 2) {
                        removeGraphicsOverlayers();
                        GraphicsOverlay graphicsOverlay_1 = new GraphicsOverlay();
                        SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.GREEN, 3);
                        Graphic fillGraphic = new Graphic(new Polyline(pointCollection, SpatialReference.create(4523)), lineSymbol);
                        graphicsOverlay_1.getGraphics().add(fillGraphic);
                        mMapView.getGraphicsOverlays().add(graphicsOverlay_1);
                    } else if (pointCollection.size() > 2) {
                        removeGraphicsOverlayers();
                        GraphicsOverlay graphicsOverlay_1 = new GraphicsOverlay();
                        SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.GREEN, 3);
                        SimpleFillSymbol fillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.HORIZONTAL, Color.GREEN, lineSymbol);
                        switch (RunningAnalyseFunction) {
                            case ANA_NEED:
                                Graphic fillGraphic = new Graphic(new Polygon(pointCollection, SpatialReference.create(4523)), lineSymbol);
                                graphicsOverlay_1.getGraphics().add(fillGraphic);
                                mMapView.getGraphicsOverlays().add(graphicsOverlay_1);
                                break;
                            case ANA_AREA:
                                fillGraphic = new Graphic(new Polygon(pointCollection, SpatialReference.create(4523)), fillSymbol);
                                graphicsOverlay_1.getGraphics().add(fillGraphic);
                                mMapView.getGraphicsOverlays().add(graphicsOverlay_1);
                                break;
                        }
                    }
                } else if (DrawType == DisplayEnum.DRAW_POLYLINE) {
                    pointCollection.add(wgs84Point);
                    if (pointCollection.size() == 1) {
                        removeGraphicsOverlayers();
                        GraphicsOverlay graphicsOverlay_1 = new GraphicsOverlay();
                        SimpleMarkerSymbol lineSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.GREEN, 3);
                        Graphic fillGraphic = new Graphic(new Point(pointCollection.get(0).getX(), pointCollection.get(0).getY(), SpatialReference.create(4523)), lineSymbol);
                        graphicsOverlay_1.getGraphics().add(fillGraphic);
                        mMapView.getGraphicsOverlays().add(graphicsOverlay_1);
                    } else if (pointCollection.size() >= 2) {
                        removeGraphicsOverlayers();
                        GraphicsOverlay graphicsOverlay_1 = new GraphicsOverlay();
                        SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.GREEN, 3);
                        Graphic fillGraphic = new Graphic(new Polyline(pointCollection, SpatialReference.create(4523)), lineSymbol);
                        graphicsOverlay_1.getGraphics().add(fillGraphic);
                        mMapView.getGraphicsOverlays().add(graphicsOverlay_1);
                        DecimalFormat format = new DecimalFormat("0.00");
                        Toast.makeText(MainActivity.this, format.format(GeometryEngine.lengthGeodetic(new Polyline(pointCollection, SpatialReference.create(4523)), new LinearUnit(LinearUnitId.METERS), GeodeticCurveType.GEODESIC)) + "米", Toast.LENGTH_SHORT).show();
                    }
                } else if (DrawType == DisplayEnum.DRAW_POINT) {
                    GraphicsOverlay graphicsOverlay_1 = new GraphicsOverlay();
                    SimpleMarkerSymbol pointSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.DIAMOND, Color.RED, 10);
                    Graphic fillGraphic = new Graphic(wgs84Point, pointSymbol);
                    graphicsOverlay_1.getGraphics().add(fillGraphic);
                    mMapView.getGraphicsOverlays().add(graphicsOverlay_1);
                }
            } else if (RunningFunction == DisplayEnum.FUNC_ADDPOI) {
                Log.w(TAG, "onSingleTapConfirmed: " + wgs84Point.getX() + "; " + wgs84Point.getY());
                final Point wgs84Point1 = (Point) GeometryEngine.project(wgs84Point, SpatialReferences.getWgs84());
                Log.w(TAG, "onSingleTapConfirmed: " + wgs84Point1.getX() + "; " + wgs84Point1.getY());
                final PointF wgs84Point2 = new PointF((float) wgs84Point1.getY(), (float) wgs84Point1.getX());
                RunningFunction = DisplayEnum.FUNC_NONE;
                showStandardWidget();
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("提示");
                builder.setMessage("请选择你要添加的图层");
                builder.setNeutralButton(strings[0], new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        CreatePOI = true;
                        POIType = 0;
                        GoNormalSinglePOIPage(AddNormalPOI(wgs84Point2, 0));
                        POIType = -1;
                        CreatePOI = false;
                    }
                });
                builder.setNegativeButton(strings[1], new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        CreatePOI = true;
                        POIType = 1;

                        GoNormalSinglePOIPage(AddNormalPOI(wgs84Point2, 1));

                        POIType = -1;
                        CreatePOI = false;
                    }
                });
                builder.setPositiveButton(strings[2], new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        CreatePOI = true;
                        POIType = 2;
                        GoNormalSinglePOIPage(AddNormalPOI(wgs84Point2, 2));
                        POIType = -1;
                        CreatePOI = false;
                    }
                });
                builder.show();
            } else if (Deleting) {
                ChooseUserTuban(clickPoint);
                ChooseAndShowWhiteblankLine(new PointF(Float.valueOf(Double.toString(wgs84Point.getX())), Float.valueOf(Double.toString(wgs84Point.getY()))));
                queryPoiForDelete(mMapView.locationToScreen(clickPoint));
                ChoosedAndShowTrail(new PointF(Float.valueOf(Double.toString(wgs84Point.getX())), Float.valueOf(Double.toString(wgs84Point.getY()))));
            }
        }
    }

    private void InitDeletingBt(){
        //批量删除按钮
        final FloatingActionButton DeleteBt = (FloatingActionButton) findViewById(R.id.DeleteFeatures);
        DeleteBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Deleting)
                {
                    StopDeletingStatus();
                }
                else{
                    StartDeletingStatus();
                }
            }
        });
    }

    private void StartDeletingStatus(){
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("提示")
                .setMessage("确定开启批量删除模式吗？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FloatingActionButton DeleteBt = (FloatingActionButton) findViewById(R.id.DeleteFeatures);
                        StopMapQueryStatus();
                        Deleting = true;
                        DeleteBt.setImageResource(R.drawable.ic_delete_stop_24dp);
                        ShowScreenBottomTipText();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }

    private void StopDeletingStatusFunctionForConfirm(){
        FloatingActionButton DeleteBt = (FloatingActionButton) findViewById(R.id.DeleteFeatures);
        DeleteFeatures();
        ShowStandardLayers();
        Deleting = false;
        DeleteBt.setImageResource(R.drawable.ic_delete_start_24dp);
    }

    private void StopDeletingStatusFunctionForCancel(){
        FloatingActionButton DeleteBt = (FloatingActionButton) findViewById(R.id.DeleteFeatures);
        RemoveChoosedFeatures();
        ShowStandardLayers();
        Deleting = false;
        DeleteBt.setImageResource(R.drawable.ic_delete_start_24dp);
    }

    private void StopDeletingStatusForStartMapQuery(){
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("提示")
                .setMessage("确定要删除所选要素吗？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        StopDeletingStatusFunctionForConfirm();
                        ShowScreenBottomTipText();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        StopDeletingStatusFunctionForCancel();
                        ShowScreenBottomTipText();
                        StartMapQueryFunction();
                    }
                })
                .show();
    }

    private  void StopDeletingStatus(){
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("提示")
                .setMessage("确定要删除所选要素吗？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        StopDeletingStatusFunctionForConfirm();
                        ShowScreenBottomTipText();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }

    private void DeleteFeatures(){
        DeleteWhiteblankLines();
        DeletePois();
        DeleteTrails();
        DeleteMyTubans();
        RemoveChoosedFeatures();
    }

    private void DeleteWhiteblankLines(){
        for (int i = 0; i < ChoosedWhiteblankLines.size(); i++) {
            LitePal.deleteAll(whiteblank.class, "ObjectID = ?", ChoosedWhiteblankLines.get(i));
        }
    }

    private void DeletePois(){
        for (int i = 0; i < ChoosedPois.size(); i++) {
            LitePal.deleteAll(POI.class, "poic = ?", ChoosedPois.get(i));
        }
    }

    private void DeleteTrails(){
        for (int i = 0; i < ChoosedTrails.size(); i++) {
            LitePal.deleteAll(Trail.class, "name = ?", ChoosedTrails.get(i));
        }
    }

    private void DeleteMyTubans(){
        for (int i = 0; i < ChoosedMyTubans.size(); i++) {
            LitePal.deleteAll(my_tb.class, "name = ?", ChoosedMyTubans.get(i));
        }
    }

    private void RemoveChoosedFeatures(){
        ChoosedWhiteblankLines = new ArrayList<>();
        ChoosedMyTubans = new ArrayList<>();
        ChoosedPois = new ArrayList<>();
        ChoosedTrails = new ArrayList<>();
    }

    private void ShowStandardLayers(){
        drawGraphicsOverlayer();
    }

    private void showWhiteBlankDialog(){
        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(MainActivity.this);
        normalDialog.setTitle("提示");
        normalDialog.setMessage("确定要完全清除当前白板吗？");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        CurrentWhiteBlank.clear();
                        //LitePal.deleteAll(whiteblank.class);
                        ShowStandardLayers();
                        Toast.makeText(MainActivity.this, R.string.EraseFinish, Toast.LENGTH_SHORT).show();
                    }
                });
        normalDialog.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        // 显示
        normalDialog.show();
    }

    private void showTrailDialogForStop(final ImageButton TrailBt){
        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(MainActivity.this);
        normalDialog.setTitle("提示");
        normalDialog.setMessage("确定要停止记录吗？");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        StopRecordingTrail(TrailBt);
                    }
                });
        normalDialog.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        // 显示
        normalDialog.show();
    }

    public static MainActivity instance = null;
    public static final int UPDATE_TEXT = 1;
    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_TEXT:
                    Log.w(TAG, "handleMessage: " );
                    Log.w(TAG, "handleMessage: " + "当前有" + LitePal.findAll(Trail.class).size() + "条轨迹");
                    drawGraphicsOverlayer();
                    ShowScreenBottomTipText();
            }
        }
    };

    private void showTrailDialogForStart(final ImageButton TrailBt){
        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(MainActivity.this);
        normalDialog.setTitle("提示");
        normalDialog.setMessage("确定要开始记录轨迹吗？");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getLocation();
                        if (location != null)
                        {
                            StartRecordingTrail(TrailBt);
                            Log.w(TAG, "onClick: " + "当前有" + LitePal.findAll(Trail.class).size() + "条轨迹");
                        }
                    }
                });
        normalDialog.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        // 显示
        normalDialog.show();
    }

    private void StopRecordingTrail(final ImageButton TrailBt){
        IsRecordingTrail = false;
        TrailBt.setImageResource(R.drawable.ic_subway);
        isLocateEnd = true;
        recordTrail(last_x, last_y);
        locError(m_cTrail);
        invalidateOptionsMenu();
        Intent stop_mService = new Intent(MainActivity.this, RecordTrail.class);
        stopService(stop_mService);
    }

    private void StartRecordingTrail(final ImageButton TrailBt){
        IsRecordingTrail = true;
        TrailBt.setImageResource(R.drawable.ic_stop_24dp);
        isLocateEnd = false;
        m_cTrail = "";
        isLocate = 0;
        initTrail();
        invalidateOptionsMenu();
        Intent start_mService = new Intent(MainActivity.this, RecordTrail.class);
        start_mService.putExtra("ic", ic);
        startService(start_mService);
        ShowScreenBottomTipText();
    }

    private void recordTrail(Location location) {
        isLocate++;
        if (location != null) {
            if (isLocateEnd || isLocate == 1) {
                m_cTrail = m_cTrail + Double.toString(m_lat) + " " + Double.toString(m_long);
                //isLocateEnd = true;
            } else
                m_cTrail = m_cTrail + " " + Double.toString(m_lat) + " " + Double.toString(m_long) + " " + Double.toString(m_lat) + " " + Double.toString(m_long);
            //setHereLocation();
            //locError(Double.toString(m_lat) + "," + Double.toString(m_long) + "Come here");

        } else {

        }
    }

    //记录轨迹
    private void recordTrail(float x, float y) {
        isLocate++;
        last_x = x;
        last_y = y;
        locError(Integer.toString(isLocate));
        //if (location != null) {
        if (isLocateEnd || isLocate == 1) {
            if (!m_cTrail.isEmpty()) {
                if (isLocate > 2) {
                    int num = DataUtil.appearNumber(m_cTrail, " ");
                    String str = m_cTrail;
                    for (int i = 0; i <= num - 2; ++i) {
                        str = str.substring(str.indexOf(" ") + 1);
                    }
                    m_cTrail = m_cTrail.substring(0, m_cTrail.length() - str.length());
                } else m_cTrail = m_cTrail + " " + Float.toString(x) + " " + Float.toString(y);
            } else m_cTrail = m_cTrail + Float.toString(x) + " " + Float.toString(y);
        } else
            m_cTrail = m_cTrail + " " + Float.toString(x) + " " + Float.toString(y) + " " + Float.toString(x) + " " + Float.toString(y);
        //setHereLocation();
        //locError(Integer.toString(m_lat) + "," + Double.toString(m_long) + "Come here");

        //} else {

        //}
    }

    //坐标监听器
    protected final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            //Log.d(TAG, "Location changed to: " + getLocationInfo(location));
            updateView(location);
            if (!isLocateEnd) {
                if(location != null) {
                    recordTrail((float) location.getLatitude(), (float) location.getLongitude());
                    locError(m_cTrail);
                }
                else{
                    Toast.makeText(MainActivity.this, "当前没有GPS信号，无法正常记录轨迹", Toast.LENGTH_LONG).show();
                    final FloatingActionButton TrailBt = (FloatingActionButton) findViewById(R.id.StartTrail);
                    StopRecordingTrail(TrailBt);
                }
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.d(TAG, "onStatusChanged() called with " + "provider = [" + provider + "], status = [" + status + "], extras = [" + extras + "]");
            switch (status) {
                case LocationProvider.AVAILABLE:
                    Log.i(TAG, "AVAILABLE");
                    break;
                case LocationProvider.OUT_OF_SERVICE:
                    Log.i(TAG, "OUT_OF_SERVICE");
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Log.i(TAG, "TEMPORARILY_UNAVAILABLE");
                    break;
            }
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.d(TAG, "onProviderEnabled() called with " + "provider = [" + provider + "]");
            try {
                Location location = locationManager.getLastKnownLocation(provider);
                Log.d(TAG, "onProviderDisabled.location = " + location);
                updateView(location);
            } catch (SecurityException e) {

            }
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.d(TAG, "onProviderDisabled() called with " + "provider = [" + provider + "]");
        }
    };

    //更新坐标信息
    private void updateView(Location location) {
        /*
        locError("isFullLocation : " + Boolean.toString(isFullLocation));
        locError("location : " + location.toString());
        if(isFullLocation && location != null){
        Geocoder gc = new Geocoder(MainInterface.this);
        List<Address> addresses = null;
        String msg = "";
        Log.d(TAG, "updateView.location = " + location);
            try {
                addresses = gc.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                //Log.d(TAG, "updateView.addresses = " + Integer.toString(addresses.size()));
                if (addresses.size() > 0) Toast.makeText(MainInterface.this, "当前位置: " + addresses.get(0).getAddressLine(0), Toast.LENGTH_LONG).show();
                else Toast.makeText(this, "你当前没有连接网络, 无法进行详细地址查询", Toast.LENGTH_LONG).show();
                Log.d(TAG, "updateView.addresses = " + addresses);
                if (addresses.size() > 0) {
                    msg += addresses.get(0).getAdminArea().substring(0,2);
                    msg += " " + addresses.get(0).getLocality().substring(0,2);
                    Log.d(TAG, "updateView.addresses = " + msg);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/
        if (location != null) {
            m_lat = location.getLatitude();
            m_long = location.getLongitude();
            locError("wgs2000: " + Double.toString(m_lat) + "&&" + Double.toString(m_long) + "Come here");
            com.esri.arcgisruntime.geometry.Point wgs84Point = new com.esri.arcgisruntime.geometry.Point(m_long, m_lat, SpatialReferences.getWgs84());
            locError("wgs2000: " + Double.toString(wgs84Point.getX()) + "&&" + Double.toString(wgs84Point.getY()) + "Come here");
            com.esri.arcgisruntime.geometry.Point wgs2000Point = (com.esri.arcgisruntime.geometry.Point) GeometryEngine.project(wgs84Point, SpatialReference.create(4490));
            locError("wgs2000: " + Double.toString(wgs2000Point.getX()) + "&&" + Double.toString(wgs2000Point.getY()) + "Come here");
            m_lat = wgs2000Point.getY();
            m_long = wgs2000Point.getX();
            //verx = (float) ((max_lat - m_lat) / (max_lat - min_lat));
            //setHereLocation();

        }
        else{
            Toast.makeText(MainActivity.this, "请打开GPS组件或者移动到有GPS信号的地方", Toast.LENGTH_LONG).show();
        }
    }

    public void locError(String str) {
        Log.e(TAG, "debug: " + str);
    }

    //获取当前坐标位置
    private void getLocation() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (!(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))) {
            Toast.makeText(this, R.string.LocError, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(intent, 0);
            return;
        }

        try {

            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location == null) {
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
            updateView(location);

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, locationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5, locationListener);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    //开始记录轨迹
    private void initTrail() {
        if (isGPSEnabled()) {
            locError("开始绘制轨迹");
        } else locError("请打开GPS功能");
    }

    //判断GPS功能是否处于开启状态
    private boolean isGPSEnabled() {
        //textView = (TextView) findViewById(R.id.txt);
        //得到系统的位置服务，判断GPS是否激活
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean ok = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (ok) {
            //textView.setText("GPS已经开启");
            //Toast.makeText(this, "GPS已经开启", Toast.LENGTH_LONG).show();
            return true;
        } else {
            Toast.makeText(this, R.string.LocError, Toast.LENGTH_SHORT).show();
            //textView.setText("GPS没有开启");
            return false;
        }
    }



    private void OutputData(){
        String SubFolder = Long.toString(System.currentTimeMillis());
        List<POI> pois = LitePal.findAll(POI.class);
        List<String> types = new ArrayList<>();
        Log.w(TAG, "runlzy: " + pois.size());
        for (int i = 0; i < pois.size(); ++i){
            String temp = pois.get(i).getType();
            Log.w(TAG, "runlzy: " + temp);
            if (temp != null) {
                if (!temp.isEmpty()) {
                    if (types.size() > 0) {
                        for (int j = 0; j < types.size(); ++j) {
                            if (temp.equals(types.get(j))) break;
                            else {
                                if (j == types.size() - 1) types.add(temp);
                                else continue;
                            }
                        }
                    }else types.add(temp);
                }
            }
        }
        DataUtil.makeKML(MainActivity.this.getResources().getText(R.string.save_folder_name1).toString(), SubFolder);
        Log.w(TAG, "runlzy: " + types.size());
        if (types.size() > 0) {
            for (int i = 0; i < types.size(); ++i) {
                DataUtil.makeTxt(types.get(i), MainActivity.this.getResources().getText(R.string.save_folder_name1).toString(), SubFolder);
            }
        }else DataUtil.makeTxt("", MainActivity.this.getResources().getText(R.string.save_folder_name1).toString(), SubFolder);
        DataUtil.makeTxt1(MainActivity.this.getResources().getText(R.string.save_folder_name1).toString(), SubFolder);
        DataUtil.makeWhiteBlankKML(MainActivity.this.getResources().getText(R.string.save_folder_name1).toString(), SubFolder);
        List<File> files = new ArrayList<File>();
        StringBuffer sb = new StringBuffer();
        int size_POI = pois.size();
        sb = sb.append("<POI>").append("\n");
        for (int i = 0; i < size_POI; ++i){
            sb.append("<id>").append(pois.get(i).getId()).append("</id>").append("\n");
            sb.append("<ic>").append(pois.get(i).getIc()).append("</ic>").append("\n");
            sb.append("<name>").append(pois.get(i).getName()).append("</name>").append("\n");
            sb.append("<POIC>").append(pois.get(i).getPoic()).append("</POIC>").append("\n");
            sb.append("<type>").append(pois.get(i).getType()).append("</type>").append("\n");
            sb.append("<photonum>").append(pois.get(i).getPhotonum()).append("</photonum>").append("\n");
            sb.append("<description>").append(pois.get(i).getDescription()).append("</description>").append("\n");
            sb.append("<tapenum>").append(pois.get(i).getTapenum()).append("</tapenum>").append("\n");
            sb.append("<x>").append(pois.get(i).getX()).append("</x>").append("\n");
            sb.append("<y>").append(pois.get(i).getY()).append("</y>").append("\n");
            sb.append("<time>").append(pois.get(i).getTime()).append("</time>").append("\n");
        }
        sb.append("</POI>").append("\n");


        List<Trail> trails = LitePal.findAll(Trail.class);
        int size_trail = trails.size();
        sb = sb.append("<Trail>").append("\n");
        for (int i = 0; i < size_trail; ++i){
            sb.append("<id>").append(trails.get(i).getId()).append("</id>").append("\n");
            sb.append("<ic>").append(trails.get(i).getIc()).append("</ic>").append("\n");
            sb.append("<name>").append(trails.get(i).getName()).append("</name>").append("\n");
            sb.append("<path>").append(trails.get(i).getPath()).append("</path>").append("\n");
            sb.append("<starttime>").append(trails.get(i).getStarttime()).append("</starttime>").append("\n");
            sb.append("<endtime>").append(trails.get(i).getEndtime()).append("</endtime>").append("\n");
        }
        sb.append("</Trail>").append("\n");

        List<MPHOTO> mphotos = LitePal.findAll(MPHOTO.class);
        int size_mphoto = mphotos.size();
        sb = sb.append("<MPHOTO>").append("\n");
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


        List<MVEDIO> mvedios = LitePal.findAll(MVEDIO.class);
        int size_mvideo = mvedios.size();
        sb = sb.append("<MVIDEO>").append("\n");
        for (int i = 0; i < size_mvideo; ++i){
            sb.append("<id>").append(mvedios.get(i).getId()).append("</id>").append("\n");
            sb.append("<pdfic>").append(mvedios.get(i).getPdfic()).append("</pdfic>").append("\n");
            sb.append("<POIC>").append(mvedios.get(i).getPoic()).append("</POIC>").append("\n");
            String path1 = mvedios.get(i).getThumbnailImg();
            sb.append("<Thumbnailpath>").append(path1).append("</Thumbnailpath>").append("\n");
            try {
                files.add(new File(path1));
            }
            catch (Exception e){

            }
            String path = mvedios.get(i).getPath();
            sb.append("<path>").append(path).append("</path>").append("\n");
            try {
                files.add(new File(path));
            }
            catch (Exception e){

            }
            sb.append("<time>").append(mvedios.get(i).getTime()).append("</time>").append("\n");
        }
        sb.append("</MVIDEO>").append("\n");

        List<MTAPE> mtapes = LitePal.findAll(MTAPE.class);
        int size_mtape = mtapes.size();
        sb = sb.append("<MTAPE>").append("\n");
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
        List<whiteblank> lines_whiteBlanks = LitePal.findAll(whiteblank.class);
        int size_lines_whiteBlank = lines_whiteBlanks.size();
        sb = sb.append("<Lines_WhiteBlank>").append("\n");
        for (int i = 0; i < size_lines_whiteBlank; ++i){
            sb.append("<m_objectid>").append(lines_whiteBlanks.get(i).getObjectID()).append("</m_objectid>").append("\n");
            sb.append("<m_lines>").append(lines_whiteBlanks.get(i).getPts()).append("</m_lines>").append("\n");
            sb.append("<m_color>").append(lines_whiteBlanks.get(i).getColor()).append("</m_color>").append("\n");
        }
        sb.append("</Lines_WhiteBlank>").append("\n");
        File file = new File(Environment.getExternalStorageDirectory() + "/" + MainActivity.this.getResources().getText(R.string.save_folder_name1) + "/Output" + "/" + SubFolder);
        if (!file.exists() && !file.isDirectory()){
            file.mkdirs();
        }
        final String outputPath = Long.toString(System.currentTimeMillis());
        File file1 = new File(Environment.getExternalStorageDirectory() + "/" + MainActivity.this.getResources().getText(R.string.save_folder_name1) + "/Output" + "/" + SubFolder,  outputPath + ".dtdb");
        try {
            FileOutputStream of = new FileOutputStream(file1);
            of.write(sb.toString().getBytes());
            of.close();
            files.add(file1);
        }catch (IOException e){
            Toast.makeText(MainActivity.this, MainActivity.this.getResources().getText(R.string.OpenFileError) + "_2", Toast.LENGTH_SHORT).show();
        }
        try {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, MainActivity.this.getResources().getText(R.string.PackingData).toString() + R.string.QSH, Toast.LENGTH_LONG).show();
                    //toolbar.setTitle("数据打包中");
                }
            });
            File zipFile = new File(Environment.getExternalStorageDirectory() + "/" + MainActivity.this.getResources().getText(R.string.save_folder_name1) + "/Output" + "/" + SubFolder,  outputPath + ".zip");
            //InputStream inputStream = null;
            ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipFile));
            zipOut.setComment("test");
            int size = files.size();
            Log.w(TAG, "run: " + size);
            for (int i = 0; i < size; ++i){
                Log.w(TAG, "run: " + i);
                Log.w(TAG, "run: " + files.get(i).getPath());
                boolean isOK = false;
                for (int k = 0; k < i; ++k) {
                    if (files.get(i).getPath().equals(files.get(k).getPath())) break;
                    if ((k == i - 1 & !files.get(i).getPath().equals(files.get(k).getPath()) & files.get(i).exists())) isOK = true;
                }
                Log.w(TAG, "aa");
                if (i == 0 & files.get(i).exists()) isOK = true;
                if (isOK){
                    Log.w(TAG, "aa");
                    InputStream inputStream = new FileInputStream(files.get(i));
                    Log.w(TAG, "aa");
                    zipOut.putNextEntry(new ZipEntry(files.get(i).getName()));
                    Log.w(TAG, "aa");
                    //int temp = 0;
                    //while ((temp = inputStream.read()) != -1){
                    //    zipOut.write(temp);
                    //}
                    byte buffer[] = new byte[4096];
                    int realLength;
                    while ((realLength = inputStream.read(buffer)) > 0) {
                        zipOut.write(buffer, 0, realLength);
                    }
                    inputStream.close();
                }
            }
            zipOut.close();
            file1.delete();
            files.clear();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, MainActivity.this.getResources().getText(R.string.PackingOk), Toast.LENGTH_LONG).show();
                    //toolbar.setTitle(select_page.this.getResources().getText(R.string.MapList));
                }
            });
        }catch (IOException e){
            Toast.makeText(MainActivity.this, MainActivity.this.getResources().getText(R.string.OpenFileError) + "_2", Toast.LENGTH_SHORT).show();
            Log.w(TAG, "run: " + e.toString());
            Log.w(TAG, "run: " + e.getMessage());
        }
    }

    private boolean queryPoi(android.graphics.Point point){
        Log.w(TAG, "queryPoi: " + point.x + "; " + point.y);
        List<mPOIobj> pois = new ArrayList<>();
        Cursor cursor = LitePal.findBySQL("select * from POI");
        if (cursor.moveToFirst()) {
            do {
                String POIC = cursor.getString(cursor.getColumnIndex("poic"));
                String time = cursor.getString(cursor.getColumnIndex("time"));
                String name = cursor.getString(cursor.getColumnIndex("name"));
                String description = cursor.getString(cursor.getColumnIndex("description"));
                int tapenum = cursor.getInt(cursor.getColumnIndex("tapenum"));
                int photonum = cursor.getInt(cursor.getColumnIndex("photonum"));
                float x = cursor.getFloat(cursor.getColumnIndex("x"));
                float y = cursor.getFloat(cursor.getColumnIndex("y"));
                mPOIobj mPOIobj = new mPOIobj(POIC, x, y, time, tapenum, photonum, name, description);
                pois.add(mPOIobj);
            } while (cursor.moveToNext());
        }
        cursor.close();
        int n = 0;
        int num = 0;
        if (pois.size() > 0) {
            mPOIobj poii = pois.get(0);

            //PointF pointF1 = new PointF(poii.getM_X(), poii.getM_Y());
            //pointF1 = LatLng.getPixLocFromGeoL(pointF1, current_pagewidth, current_pageheight, w, h, min_long, min_lat);
            Point mpt0 = (Point) GeometryEngine.project(new Point((double)poii.getM_Y(), (double)poii.getM_X(), SpatialReferences.getWgs84()), SpatialReference.create(4523));
            android.graphics.Point pt0 = mMapView.locationToScreen(mpt0);
            Log.w(TAG, "queryPoi0: " + pt0.x + "; " + pt0.y);
            //pointF1 = new PointF(pointF1.x, pointF1.y);
            //pointF = getGeoLocFromPixL(pointF);
            //final PointF pt9 = LatLng.getPixLocFromGeoL(pt1, current_pagewidth, current_pageheight, w, h, min_long, min_lat);
            float mdelta = Math.abs(pt0.x - point.x) + Math.abs(pt0.y - point.y);
            for (mPOIobj poi : pois) {
                /*PointF mpointF1 = new PointF(poi.getM_X(), poi.getM_Y());
                Log.w(TAG, "mpointF1 queried: " + mpointF1.x + ";" + mpointF1.y);
                mpointF1 = LatLng.getPixLocFromGeoL(mpointF1, current_pagewidth, current_pageheight, w, h, min_long, min_lat);
                mpointF1 = new PointF(mpointF1.x, mpointF1.y);*/
                Point mpt1 = (Point) GeometryEngine.project(new Point((double)poi.getM_Y(), (double)poi.getM_X(), SpatialReferences.getWgs84()), SpatialReference.create(4523));
                android.graphics.Point pt1 = mMapView.locationToScreen(mpt1);
                Log.w(TAG, "queryPoi: " + n);
                Log.w(TAG, "queryPoi: " + pt1.x + "; " + pt1.y);
                if (Math.abs(pt1.x - point.x) + Math.abs(pt1.y - point.y) < mdelta && Math.abs(pt1.x - point.x) + Math.abs(pt1.y - point.y) < 35) {
                    //locError("mpointF : " + mpointF1.toString());
                    mdelta = Math.abs(pt1.x - point.x) + Math.abs(pt1.y - point.y);
                    num = n;
                }
                n++;
            }
            if (mdelta < 35 || num != 0) {
                //return pois.get(num).getM_POIC();
                GoNormalSinglePOIPage(pois.get(num).getM_POIC());
                return true;
                //locError(Integer.toString(pois.get(num).getPhotonum()));
            } else {
                return false;
            }
        } else return false;
    }

    private boolean queryPoiForDelete(android.graphics.Point point){
        Log.w(TAG, "queryPoi: " + point.x + "; " + point.y);
        List<mPOIobj> pois = new ArrayList<>();
        Cursor cursor = LitePal.findBySQL("select * from POI");
        if (cursor.moveToFirst()) {
            do {
                String POIC = cursor.getString(cursor.getColumnIndex("poic"));
                String time = cursor.getString(cursor.getColumnIndex("time"));
                String name = cursor.getString(cursor.getColumnIndex("name"));
                String description = cursor.getString(cursor.getColumnIndex("description"));
                int tapenum = cursor.getInt(cursor.getColumnIndex("tapenum"));
                int photonum = cursor.getInt(cursor.getColumnIndex("photonum"));
                float x = cursor.getFloat(cursor.getColumnIndex("x"));
                float y = cursor.getFloat(cursor.getColumnIndex("y"));
                mPOIobj mPOIobj = new mPOIobj(POIC, x, y, time, tapenum, photonum, name, description);
                pois.add(mPOIobj);
            } while (cursor.moveToNext());
        }
        cursor.close();
        int n = 0;
        int num = 0;
        if (pois.size() > 0) {
            mPOIobj poii = pois.get(0);

            //PointF pointF1 = new PointF(poii.getM_X(), poii.getM_Y());
            //pointF1 = LatLng.getPixLocFromGeoL(pointF1, current_pagewidth, current_pageheight, w, h, min_long, min_lat);
            Point mpt0 = (Point) GeometryEngine.project(new Point((double)poii.getM_Y(), (double)poii.getM_X(), SpatialReferences.getWgs84()), SpatialReference.create(4523));
            android.graphics.Point pt0 = mMapView.locationToScreen(mpt0);
            Log.w(TAG, "queryPoi0: " + pt0.x + "; " + pt0.y);
            //pointF1 = new PointF(pointF1.x, pointF1.y);
            //pointF = getGeoLocFromPixL(pointF);
            //final PointF pt9 = LatLng.getPixLocFromGeoL(pt1, current_pagewidth, current_pageheight, w, h, min_long, min_lat);
            float mdelta = Math.abs(pt0.x - point.x) + Math.abs(pt0.y - point.y);
            for (mPOIobj poi : pois) {
                /*PointF mpointF1 = new PointF(poi.getM_X(), poi.getM_Y());
                Log.w(TAG, "mpointF1 queried: " + mpointF1.x + ";" + mpointF1.y);
                mpointF1 = LatLng.getPixLocFromGeoL(mpointF1, current_pagewidth, current_pageheight, w, h, min_long, min_lat);
                mpointF1 = new PointF(mpointF1.x, mpointF1.y);*/
                Point mpt1 = (Point) GeometryEngine.project(new Point((double)poi.getM_Y(), (double)poi.getM_X(), SpatialReferences.getWgs84()), SpatialReference.create(4523));
                android.graphics.Point pt1 = mMapView.locationToScreen(mpt1);
                Log.w(TAG, "queryPoi: " + n);
                Log.w(TAG, "queryPoi: " + pt1.x + "; " + pt1.y);
                if (Math.abs(pt1.x - point.x) + Math.abs(pt1.y - point.y) < mdelta && Math.abs(pt1.x - point.x) + Math.abs(pt1.y - point.y) < 35) {
                    //locError("mpointF : " + mpointF1.toString());
                    mdelta = Math.abs(pt1.x - point.x) + Math.abs(pt1.y - point.y);
                    num = n;
                }
                n++;
            }
            if (mdelta < 35 || num != 0) {
                //return pois.get(num).getM_POIC();
                AddPoisByPoic(pois.get(num).getM_POIC());
                Log.w(TAG, "queryPoiForDelete: " + pois.get(num).getM_POIC());

                initialiseGraphics();
                if (ShowPoi)
                    updatePoi();
                if (ShowTrail)
                    ParseAndUpdateTrails();
                if (ShowMyTuban)
                    parseAndUpdateMyTuban();
                if (ShowWhiteBlank)
                    updateWhiteBlank();
                updateGraphicsOverlayer();
                //GoNormalSinglePOIPage(pois.get(num).getM_POIC());
                return true;
                //locError(Integer.toString(pois.get(num).getPhotonum()));
            } else {
                return false;
            }
        } else return false;
    }

    private boolean queryUserTB(String path, FeatureLayer featureLayer, final Point clickPoint, final Point mapPoint){
        QueryParameters query = new QueryParameters();
        query.setGeometry(clickPoint);
        /*Log.w(TAG, "queryUserTB: " + featureLayer.getFeatureTable().getGeometryType());
        if (featureLayer.getFeatureTable().getGeometryType() == GeometryType.POLYGON)
        {
            Log.w(TAG, "queryUserTB: " + "这是个面！");
            query.setSpatialRelationship(QueryParameters.SpatialRelationship.WITHIN);// 设置空间几何对象
        }
        else if (featureLayer.getFeatureTable().getGeometryType() == GeometryType.POLYLINE)
        {
            Log.w(TAG, "queryUserTB: " + "这是根线！");
            query.setSpatialRelationship(QueryParameters.SpatialRelationship.INTERSECTS);// 设置空间几何对象
        }*/
        query.setSpatialRelationship(QueryParameters.SpatialRelationship.WITHIN);
        if (isFileExist(path) & MapQuery) {
            //FeatureLayer featureLayer=(FeatureLayer) mMapView.getMap().getOperationalLayers().get(10);
            try {
                FeatureTable mTable = featureLayer.getFeatureTable();//得到查询属性表
                final ListenableFuture<FeatureQueryResult> featureQueryResult
                        = mTable.queryFeaturesAsync(query);
                featureQueryResult.addDoneListener(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            removeGraphicsOverlayers();
                            drawGraphicsOverlayer();
                            FeatureQueryResult featureResul = featureQueryResult.get();
                            for (Object element : featureResul) {
                                if (element instanceof Feature) {
                                    Feature mFeatureGrafic = (Feature) element;
                                    final Geometry geometry = mFeatureGrafic.getGeometry();
                                    // TODO 用户添加图层的分析模块
                                    //analyseFunction((Polygon) geometry);
                                    //ChoosedPolygonForUserLayer = (Polygon)geometry;
                                    //AnalyseGeometry(geometry);
                                    GraphicsOverlay graphicsOverlay_1 = new GraphicsOverlay();
                                    SimpleMarkerSymbol pointSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.DIAMOND, Color.RED, 5);
                                    SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.GREEN, 3);
                                    Graphic pointGraphic = new Graphic(clickPoint, pointSymbol);
                                    Graphic fillGraphic = new Graphic(geometry, lineSymbol);
                                    graphicsOverlay_1.getGraphics().add(pointGraphic);
                                    graphicsOverlay_1.getGraphics().add(fillGraphic);
                                    mMapView.getGraphicsOverlays().add(graphicsOverlay_1);
                                    Map<String, Object> mQuerryString = mFeatureGrafic.getAttributes();
                                    TextView calloutContent = new TextView(getApplicationContext());
                                    calloutContent.setTextColor(Color.BLACK);
                                    //calloutContent.setSingleLine();
                                    // format coordinates to 4 decimal places
                                    String str = "";
                                    List<KeyAndValue> keyAndValues = new ArrayList<>();
                                    for (String key : mQuerryString.keySet()) {
                                        //str = str + key + " : " + String.valueOf(mQuerryString.get(key)) + "\n";
                                        //if (key.contains("GHDLMC") || key.contains("GHDLBM") || key.contains("GHDLMJ") || key.contains("PDJB") || key.contains("XZQMC") || key.contains("XZQDM")) {
                                            keyAndValues.add(new KeyAndValue(key, String.valueOf(mQuerryString.get(key))));
                                        //}
                                    }
                                    //keyAndValues = KeyAndValue.parseList(keyAndValues);
                                    KeyAndValue.parseList(keyAndValues);
                                    for (int i = 0; i < keyAndValues.size(); ++i) {
                                        if (i == keyAndValues.size() - 1){
                                            str = str + keyAndValues.get(i).getName() + " : " + keyAndValues.get(i).getValue();
                                        }else
                                            str = str + keyAndValues.get(i).getName() + " : " + keyAndValues.get(i).getValue() + "\n";
                                    }
                                    calloutContent.setText(str);
                                    // get callout, set content and show
                                    mCallout.setLocation(mapPoint);
                                    mCallout.setContent(calloutContent);
                                    mCallout.show();
                                    Log.w(TAG, "run: callout" + mCallout.isShowing());
                                    inMap = true;
                                    break;
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                return inMap;
            } catch (ArcGISRuntimeException e) {
                Toast.makeText(MainActivity.this, e.getMessage() + e.getErrorCode(), Toast.LENGTH_SHORT).show();
                return false;
            }
        } else{
            Toast.makeText(MainActivity.this, R.string.QueryError_2, Toast.LENGTH_SHORT).show();
        }
        if (!inMap) mCallout.dismiss();
        return false;
    }

    private Polygon AnalysisUserGeometry;
    private void AnalyseGeometry(final Geometry geometry){
        final Polygon polygon = (Polygon)geometry;
        final FloatingActionButton finish = (FloatingActionButton) findViewById(R.id.FinishQuery);
        finish.setVisibility(View.VISIBLE);
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //analyseFunctionForBasePolygon((Polygon) geometry);
                AnalysisUserGeometry = (Polygon) geometry;
                NeedQueryForBasePolygon((Polygon) geometry);

                /*try {
                    //PartCollection parts = new PartCollection(polygon.getParts(), SpatialReference.create(4523));
                    PartCollection parts = new PartCollection(polygon.getParts());
                    pointCollection = new PointCollection(parts.getPartsAsPoints());
                }
                catch (Exception e){
                    Log.w(TAG, "onClick: 2020/9/7:" + e.toString());
                }*/
            }
        });
    }

    int queryedFeatureLayerCount = 0;
    private void updataCalloutInfo() {
        queryedFeatureLayerCount++;
        if(queryedFeatureLayerCount == BaseLayerFieldsSheetList.size()) {

        }
    }

    private int QueriedLayerIndex = -1;
    private void queryTBFor20200903(final Point clickPoint, final Point mapPoint){
        Geometry GrossGeometry = GeometryEngine.buffer(clickPoint, 10);
        final QueryParameters query = new QueryParameters();
        query.setGeometry(GrossGeometry);// 设置空间几何对象
        query.setSpatialRelationship(QueryParameters.SpatialRelationship.INTERSECTS);
        if (isFileExist(StaticVariableEnum.YSZRZYROOTPATH) & MapQuery) {
            //FeatureLayer featureLayer=(FeatureLayer) mMapView.getMap().getOperationalLayers().get(10);
            try {
                if (QueriedLayerIndex != -1 && QueriedLayerIndex < BaseLayerFieldsSheetList.size()) {
                    LayerFieldsSheet lfs = LayerFieldsSheetList.get(QueriedLayerIndex);
                    final FeatureLayer fl = lfs.getFeatureLayer();
                    final List<FieldNameSheet> Fields = lfs.getFieldNameSheetList();

                    FeatureTable mTable = fl.getFeatureTable();//得到查询属性表
                    //mTable.qu
                    // TODO 2020/9/3 图到属性查询
                    final ListenableFuture<FeatureQueryResult> featureQueryResult
                            = mTable.queryFeaturesAsync(query);
                    Log.w(TAG, "通过点选查询底图图斑: " + featureQueryResult);

                    featureQueryResult.addDoneListener(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                removeGraphicsOverlayers();
                                drawGraphicsOverlayer();
                                FeatureQueryResult featureResul = featureQueryResult.get();
                                for (Object element : featureResul) {
                                    if (element instanceof Feature) {
                                        Feature mFeatureGrafic = (Feature) element;
                                        Geometry geometry = mFeatureGrafic.getGeometry();
                                        // TODO 2020/9/8 利用底图面图斑进行查询
                                        if (geometry instanceof Polygon)
                                            //AnalyseGeometry(geometry);
                                            ;
                                        else
                                            //RemoveFinishButton();
                                        ;
                                        GraphicsOverlay graphicsOverlay_1 = new GraphicsOverlay();
                                        SimpleMarkerSymbol pointSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.DIAMOND, Color.RED, 5);
                                        SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.GREEN, 3);
                                        Graphic pointGraphic = new Graphic(clickPoint, pointSymbol);
                                        Graphic fillGraphic = new Graphic(geometry, lineSymbol);
                                        graphicsOverlay_1.getGraphics().add(pointGraphic);
                                        graphicsOverlay_1.getGraphics().add(fillGraphic);
                                        mMapView.getGraphicsOverlays().add(graphicsOverlay_1);
                                        Map<String, Object> mQuerryString = mFeatureGrafic.getAttributes();
                                        TextView calloutContent = new TextView(getApplicationContext());
                                        calloutContent.setTextColor(Color.BLACK);
                                        //calloutContent.setSingleLine();
                                        // format coordinates to 4 decimal places
                                        String str = "";
                                        List<KeyAndValue> keyAndValues = new ArrayList<>();
                                        for (String key : mQuerryString.keySet()) {
                                            //str = str + key + " : " + String.valueOf(mQuerryString.get(key)) + "\n";
                                            //if (key.contains("GHDLMC") || key.contains("GHDLBM") || key.contains("GHDLMJ") || key.contains("PDJB") || key.contains("XZQMC") || key.contains("XZQDM"))
                                            {
                                                keyAndValues.add(new KeyAndValue(key, String.valueOf(mQuerryString.get(key))));
                                            }
                                        }
                                        //keyAndValues = KeyAndValue.parseList(keyAndValues);
                                        /*KeyAndValue.parseList(keyAndValues);
                                        for (int i = 0; i < keyAndValues.size(); ++i) {
                                            if (i == keyAndValues.size() - 1){
                                                str = str + keyAndValues.get(i).getNickname() + " : " + keyAndValues.get(i).getValue();
                                            }else
                                                str = str + keyAndValues.get(i).getNickname() + " : " + keyAndValues.get(i).getValue() + "\n";
                                        }*/

                                        if (Fields.size() > 0) {
                                            for (int i = 0; i < keyAndValues.size(); i++) {
                                                for (int j = 0; j < Fields.size(); j++) {
                                                    if (keyAndValues.get(i).getName().equals(Fields.get(j).getRealName())) {
                                                        /*if (i == keyAndValues.size() - 1){
                                                            str = str + Fields.get(j).getShowName() + " : " + keyAndValues.get(i).getValue();
                                                        }else
                                                            str = str + Fields.get(j).getShowName() + " : " + keyAndValues.get(i).getValue();// + "\n";*/
                                                        if (str.length() == 0)
                                                            str = Fields.get(j).getShowName() + " : " + keyAndValues.get(i).getValue();
                                                        else
                                                            str += "\n" + Fields.get(j).getShowName() + " : " + keyAndValues.get(i).getValue();
                                                        break;
                                                    }
                                                }
                                            }
                                        }

                                        calloutContent.setText(str);
                                        // get callout, set content and show
                                        mCallout.setLocation(mapPoint);
                                        mCallout.setContent(calloutContent);
                                        mCallout.show();
                                        Log.w(TAG, "通过点选查询底图图斑: callout" + mCallout.isShowing());
                                        inMap = true;
                                    }
                                    break;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
                else{
                    Toast.makeText(MainActivity.this, "请选择待查寻图层", Toast.LENGTH_LONG).show();
                }
            } catch (ArcGISRuntimeException e) {
                Toast.makeText(MainActivity.this, e.getMessage() + e.getErrorCode(), Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            Toast.makeText(MainActivity.this, R.string.QueryError_2, Toast.LENGTH_SHORT).show();
        }
        if (!inMap) {
            Log.w(TAG, "通过点选查询底图图斑: 我关闭了 callout" );
            mCallout.dismiss();
        }
    }

    private void RemoveFinishButton(){
        final FloatingActionButton finish = (FloatingActionButton) findViewById(R.id.FinishQuery);
        finish.setVisibility(View.GONE);
    }

    private void queryTB(final Point clickPoint, final Point mapPoint){
        final QueryParameters query = new QueryParameters();
        query.setGeometry(clickPoint);// 设置空间几何对象
        // TODO
        query.setSpatialRelationship(QueryParameters.SpatialRelationship.WITHIN);
        if (isFileExist(StaticVariableEnum.GDBROOTPATH) & MapQuery) {
            //FeatureLayer featureLayer=(FeatureLayer) mMapView.getMap().getOperationalLayers().get(10);
            try {
                FeatureTable mTable = TDGHDLFeatureLayer.getFeatureTable();//得到查询属性表
                final ListenableFuture<FeatureQueryResult> featureQueryResult
                        = mTable.queryFeaturesAsync(query);
                featureQueryResult.addDoneListener(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            removeGraphicsOverlayers();
                            FeatureQueryResult featureResul = featureQueryResult.get();
                            for (Object element : featureResul) {
                                if (element instanceof Feature) {
                                    Feature mFeatureGrafic = (Feature) element;
                                    Geometry geometry = mFeatureGrafic.getGeometry();
                                    GraphicsOverlay graphicsOverlay_1 = new GraphicsOverlay();
                                    SimpleMarkerSymbol pointSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.DIAMOND, Color.RED, 5);
                                    SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.GREEN, 3);
                                    Graphic pointGraphic = new Graphic(clickPoint, pointSymbol);
                                    Graphic fillGraphic = new Graphic(geometry, lineSymbol);
                                    graphicsOverlay_1.getGraphics().add(pointGraphic);
                                    graphicsOverlay_1.getGraphics().add(fillGraphic);
                                    mMapView.getGraphicsOverlays().add(graphicsOverlay_1);
                                    Map<String, Object> mQuerryString = mFeatureGrafic.getAttributes();
                                    TextView calloutContent = new TextView(getApplicationContext());
                                    calloutContent.setTextColor(Color.BLACK);
                                    //calloutContent.setSingleLine();
                                    // format coordinates to 4 decimal places
                                    String str = "";
                                    List<KeyAndValue> keyAndValues = new ArrayList<>();
                                    for (String key : mQuerryString.keySet()) {
                                        //str = str + key + " : " + String.valueOf(mQuerryString.get(key)) + "\n";
                                        if (key.contains("GHDLMC") || key.contains("GHDLBM") || key.contains("GHDLMJ") || key.contains("PDJB") || key.contains("XZQMC") || key.contains("XZQDM")) {
                                            keyAndValues.add(new KeyAndValue(key, String.valueOf(mQuerryString.get(key))));
                                        }
                                    }
                                    //keyAndValues = KeyAndValue.parseList(keyAndValues);
                                    KeyAndValue.parseList(keyAndValues);
                                    for (int i = 0; i < keyAndValues.size(); ++i) {
                                        if (i == keyAndValues.size() - 1){
                                            str = str + keyAndValues.get(i).getNickname() + " : " + keyAndValues.get(i).getValue();
                                        }else
                                            str = str + keyAndValues.get(i).getNickname() + " : " + keyAndValues.get(i).getValue() + "\n";
                                    }
                                    calloutContent.setText(str);
                                    // get callout, set content and show
                                    mCallout.setLocation(mapPoint);
                                    mCallout.setContent(calloutContent);
                                    mCallout.show();
                                    Log.w(TAG, "run: callout" + mCallout.isShowing());
                                    inMap = true;
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (ArcGISRuntimeException e) {
                Toast.makeText(MainActivity.this, e.getMessage() + e.getErrorCode(), Toast.LENGTH_SHORT).show();
            }
        }
        else if(isFileExist(StaticVariableEnum.DLLCROOTPATH) & MapQuery){
            try {
                FeatureTable mTable = LCRAFeatureLayer.getFeatureTable();//得到查询属性表
                final ListenableFuture<FeatureQueryResult> featureQueryResult
                        = mTable.queryFeaturesAsync(query);
                featureQueryResult.addDoneListener(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            removeGraphicsOverlayers();
                            FeatureQueryResult featureResul = featureQueryResult.get();
                            for (Object element : featureResul) {
                                if (element instanceof Feature) {
                                    Feature mFeatureGrafic = (Feature) element;
                                    Geometry geometry = mFeatureGrafic.getGeometry();

                                    //if(GeometryEngine.within(mapPoint, geometry))
                                    {
                                        GraphicsOverlay graphicsOverlay_1 = new GraphicsOverlay();
                                        SimpleMarkerSymbol pointSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.DIAMOND, Color.RED, 5);
                                        SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.GREEN, 3);
                                        Graphic pointGraphic = new Graphic(clickPoint, pointSymbol);
                                        Graphic fillGraphic = new Graphic(geometry, lineSymbol);
                                        graphicsOverlay_1.getGraphics().add(pointGraphic);
                                        graphicsOverlay_1.getGraphics().add(fillGraphic);
                                        mMapView.getGraphicsOverlays().add(graphicsOverlay_1);
                                        Map<String, Object> mQuerryString = mFeatureGrafic.getAttributes();
                                        TextView calloutContent = new TextView(getApplicationContext());
                                        calloutContent.setTextColor(Color.BLACK);
                                        //calloutContent.setSingleLine();
                                        // format coordinates to 4 decimal places
                                        String str = "";
                                        List<KeyAndValue> keyAndValues = new ArrayList<>();
                                        for (String key : mQuerryString.keySet()) {
                                            //str = str + key + " : " + String.valueOf(mQuerryString.get(key)) + "\n";
                                            //if (key.contains("GHDLMC") || key.contains("GHDLBM") || key.contains("GHDLMJ") || key.contains("PDJB") || key.contains("XZQMC") || key.contains("XZQDM")) {
                                            if (key.contains("NAME")) {
                                                keyAndValues.add(new KeyAndValue(key, String.valueOf(mQuerryString.get(key))));
                                            }
                                        }
                                        //keyAndValues = KeyAndValue.parseList(keyAndValues);
                                        KeyAndValue.parseList(keyAndValues);
                                        for (int i = 0; i < keyAndValues.size(); ++i) {
                                            if (i == keyAndValues.size() - 1) {
                                                str = str + keyAndValues.get(i).getNickname() + " : " + keyAndValues.get(i).getValue();
                                            } else
                                                str = str + keyAndValues.get(i).getNickname() + " : " + keyAndValues.get(i).getValue() + "\n";
                                        }
                                        calloutContent.setText(str);
                                        // get callout, set content and show
                                        mCallout.setLocation(mapPoint);
                                        mCallout.setContent(calloutContent);
                                        mCallout.show();
                                        Log.w(TAG, "run: callout" + mCallout.isShowing());
                                        inMap = true;
                                        //Toast.makeText(MainActivity.this, mapPoint.getSpatialReference().getWkid() + geometry.getSpatialReference().getWkid(), Toast.LENGTH_SHORT).show();
                                        //Toast.makeText(MainActivity.this, GeometryEngine.within(mapPoint, GeometryEngine.project(geometry, SpatialReference.create(4523))) + "", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                break;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "lzy" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } catch (ArcGISRuntimeException e) {
                Toast.makeText(MainActivity.this, e.getMessage() + e.getErrorCode(), Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            Toast.makeText(MainActivity.this, R.string.QueryError_2, Toast.LENGTH_SHORT).show();
        }
        if (!inMap) mCallout.dismiss();
    }

    private void queryPTuBan(final Point clickPoint, final Point mapPoint){
        QueryParameters query = new QueryParameters();
        query.setGeometry(clickPoint);// 设置空间几何对象
        if (isFileExist(StaticVariableEnum.PGDBROOTPATH) & MapQuery) {
            //FeatureLayer featureLayer=(FeatureLayer) mMapView.getMap().getOperationalLayers().get(10);
            try {
                FeatureTable mTable = PTuBanFeatureLayer.getFeatureTable();//得到查询属性表
                final ListenableFuture<FeatureQueryResult> featureQueryResult
                        = mTable.queryFeaturesAsync(query);
                featureQueryResult.addDoneListener(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            boolean isOK = true;
                            removeGraphicsOverlayers();
                            FeatureQueryResult featureResul = featureQueryResult.get();
                            for (Object element : featureResul) {
                                if (element instanceof Feature) {
                                    Feature mFeatureGrafic = (Feature) element;
                                    Geometry geometry = mFeatureGrafic.getGeometry();
                                    GraphicsOverlay graphicsOverlay_1 = new GraphicsOverlay();
                                    SimpleMarkerSymbol pointSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.DIAMOND, Color.RED, 5);
                                    SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.GREEN, 3);
                                    Graphic pointGraphic = new Graphic(clickPoint, pointSymbol);
                                    Graphic fillGraphic = new Graphic(geometry, lineSymbol);
                                    graphicsOverlay_1.getGraphics().add(pointGraphic);
                                    graphicsOverlay_1.getGraphics().add(fillGraphic);
                                    mMapView.getGraphicsOverlays().add(graphicsOverlay_1);
                                    Map<String, Object> mQuerryString = mFeatureGrafic.getAttributes();
                                    TextView calloutContent = new TextView(getApplicationContext());
                                    calloutContent.setTextColor(Color.BLACK);
                                    //calloutContent.setSingleLine();
                                    // format coordinates to 4 decimal places
                                    String str = "";
                                    List<KeyAndValue> keyAndValues = new ArrayList<>();
                                    for (String key : mQuerryString.keySet()) {
                                        //str = str + key + " : " + String.valueOf(mQuerryString.get(key)) + "\n";
                                        //if (key.contains("GHDLMC") || key.contains("GHDLBM") || key.contains("GHDLMJ") || key.contains("PDJB") || key.contains("XZQMC") || key.contains("XZQDM")) {
                                        keyAndValues.add(new KeyAndValue(key, String.valueOf(mQuerryString.get(key))));
                                        //}
                                    }
                                    //keyAndValues = KeyAndValue.parseList(keyAndValues);
                                    KeyAndValue.parseList(keyAndValues);
                                    for (int i = 0; i < keyAndValues.size(); ++i) {
                                        if (i == keyAndValues.size() - 1){
                                            str = str + keyAndValues.get(i).getNickname() + " : " + keyAndValues.get(i).getValue();
                                        }else
                                            str = str + keyAndValues.get(i).getNickname() + " : " + keyAndValues.get(i).getValue() + "\n";
                                    }
                                    calloutContent.setText(str);
                                    // get callout, set content and show
                                    mCallout.setLocation(mapPoint);
                                    mCallout.setContent(calloutContent);
                                    mCallout.show();
                                    Log.w(TAG, "run: callout" + mCallout.isShowing());
                                    inMap = true;
                                    isOK = false;
                                }
                            }
                            if (isOK)
                                queryTB(clickPoint, mapPoint);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (ArcGISRuntimeException e) {
                Toast.makeText(MainActivity.this, e.getMessage() + e.getErrorCode(), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(MainActivity.this, R.string.QueryError_2, Toast.LENGTH_SHORT).show();
        }
        if (!inMap) mCallout.dismiss();
    }

    private void removeGraphicsOverlayers(){
        while (mMapView.getGraphicsOverlays().size() != 0) {
            for (int i = 0; i < mMapView.getGraphicsOverlays().size(); ++i) {
                mMapView.getGraphicsOverlays().remove(i);
            }
        }
    }

    private void doSpecificOperation(){
        //开始内存占用监测
        Sampler.getInstance().init(MainActivity.this, 100);
        Sampler.getInstance().start();
    }

    private String AddNormalPOI(final PointF pt1, final int num) {
        List<POI> POIs = LitePal.findAll(POI.class);
        POI poi = new POI();
        poi.setName("POI" + String.valueOf(POIs.size() + 1));
        poi.setIc(ic);
        //if (showMode == TuzhiEnum.NOCENTERMODE) {
            poi.setX(pt1.x);
            poi.setY(pt1.y);
        /*} else {
            poi.setX(centerPointLoc.x);
            poi.setY(centerPointLoc.y);
        }*/
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(MainActivity.this.getResources().getText(R.string.DateAndTime).toString());
        Date date = new Date(System.currentTimeMillis());
        poi.setTime(simpleDateFormat.format(date));
        poi.setPhotonum(0);
        String mpoic = "POI" + String.valueOf(System.currentTimeMillis());
        poi.setPoic(mpoic);
        poi.setType(strings[num]);
        poi.save();
        return mpoic;
    }

    private void initLayerList(){
        int size = map.getOperationalLayers().size();
        layers.clear();
        layerList.clear();

        Log.w(TAG, "size: " + size);
        /*List<UserLayer> tifList = LitePal.where("type = ?", Integer.toString(UserLayer.TIF_FILE)).find(UserLayer.class);
        for (int i = 0; i < tifList.size(); i++) {
            Log.w(TAG, "onClick: 2020/12/7 For Update: " + tifList.get(i).getPath());
            layerList.add(new layer(tifList.get(i).getName(), tifList.get(i).getPath(), map.getOperationalLayers().get(i).isVisible()));
        }*/
        List<UserLayer> tifList = LitePal.where("type = ?", Integer.toString(UserLayer.TIF_FILE)).find(UserLayer.class);
        for (int i = size - 1, j = tifList.size()-1; i > -1; i--){
            Log.w(TAG, "initLayerList: " + map.getOperationalLayers().get(i).getName());
            if (!map.getOperationalLayers().get(i).getName().contains(".tpk")) {
                //当此图层不是切片图层时

                //在layers中添加一个layer
                layers.add(new layer1(map.getOperationalLayers().get(i), i));


                if (map.getOperationalLayers().get(i).getName().isEmpty()){
                    //图层名为空
                    try {
                        layerList.add(new layer(tifList.get(j).getName(), tifList.get(j).getPath(), map.getOperationalLayers().get(i).isVisible()));
                    }
                    catch (Exception e){

                    }
                    j--;
                }
                else
                {
                    //图层名不是空值
                    if (map.getOperationalLayers().get(i) instanceof FeatureLayer) {
                        //要素图层
                        FeatureLayer fl = (FeatureLayer) (map.getOperationalLayers().get(i));
                        FeatureTable ft = fl.getFeatureTable();
                        if (ft instanceof ShapefileFeatureTable) {
                            //用户添加的Shp图层
                            ShapefileFeatureTable sfft = (ShapefileFeatureTable) ft;
                            layerList.add(new layer(map.getOperationalLayers().get(i).getName(), sfft.getPath(), map.getOperationalLayers().get(i).isVisible()));
                        } else
                        {
                            //底图；要素图层
                            layerList.add(new layer(map.getOperationalLayers().get(i).getName(), "", map.getOperationalLayers().get(i).isVisible()));
                        }
                    }
                    else{
                        //当前图层不是要素图层时
                        layerList.add(new layer(map.getOperationalLayers().get(i).getName(), "", map.getOperationalLayers().get(i).isVisible()));
                    }
                }
                if (map.getOperationalLayers().get(i).getName().contains("土地规划地类")) {
                    //ArcGISVectorTiledLayer mVectorTiledLayer = (ArcGISVectorTiledLayer) map.getOperationalLayers().get(i);
                    //Log.w(TAG, "initLayerList: " + "omg");
                }
            }else {
                //当此图层是切片图层时
                hasTPK = true;
                if (!map.getOperationalLayers().get(i).getName().equals("临沧市中心城区影像"))
                {
                    //底图；mmpk；影像图层
                    TPKlayers.add(new layer1(map.getOperationalLayers().get(i), i));
                }
                else{
                    //底图；额外特殊；影像图层
                    layers.add(new layer1(map.getOperationalLayers().get(i), i));
                    layerList.add(new layer(map.getOperationalLayers().get(i).getName(), "", map.getOperationalLayers().get(i).isVisible()));
                }

            }
        }
        layerList.add(new layer("影像", "", true));
        AddStandardLayer();
        isOK1 = true;
        setLeftRecyclerView();

        Log.w(TAG, "size: " + "done");
    }

    private void AddStandardLayer(){
        layerList.add(new layer("白板层", "", true));
        layerList.add(new layer("轨迹线层", "", true));
        layerList.add(new layer("兴趣点层", "", true));
        layerList.add(new layer("按需查询层", "", true));
    }

    /*private void initLayerList(String name){
        int size = map.getOperationalLayers().size();
        layers.clear();
        layerList.clear();
        Log.w(TAG, "size: " + size);
        for (int i = size - 1; i > -1; i--){
            Log.w(TAG, "initLayerList: " + map.getOperationalLayers().get(i).getName());
            if (!map.getOperationalLayers().get(i).getName().contains(".tpk")) {
                layers.add(new layer1(map.getOperationalLayers().get(i), i));
                if (map.getOperationalLayers().get(i).getName().isEmpty())
                    layerList.add(new layer(name, "", map.getOperationalLayers().get(i).isVisible()));
                else
                    layerList.add(new layer(map.getOperationalLayers().get(i).getName(), "", map.getOperationalLayers().get(i).isVisible()));
                if (map.getOperationalLayers().get(i).getName().contains("土地规划地类")) {
                    //ArcGISVectorTiledLayer mVectorTiledLayer = (ArcGISVectorTiledLayer) map.getOperationalLayers().get(i);
                    //Log.w(TAG, "initLayerList: " + "omg");
                }
            }else {
                //Log.w(TAG, "initLayerList: " + map.getOperationalLayers().get(i).getName());
                hasTPK = true;
                if (!map.getOperationalLayers().get(i).getName().equals("临沧市中心城区影像"))
                    TPKlayers.add(new layer1(map.getOperationalLayers().get(i), i));
                else{
                    layers.add(new layer1(map.getOperationalLayers().get(i), i));
                    layerList.add(new layer(map.getOperationalLayers().get(i).getName(), "", map.getOperationalLayers().get(i).isVisible()));
                }

            }
        }
        layerList.add(new layer("影像", "", false));
        isOK1 = true;
        setRecyclerView();
    }*/

    private void initSurfaceCenterPoint(final MobileMapPackage mainMobileMapPackage){
        OriginScale = mMapView.getMapScale();
        /*if (mainMobileMapPackage.getPath().toString().contains("临沧")) {
            Log.w(TAG, "run: " + mainMobileMapPackage.getPath().toString());
            // TODO 初始比例尺
            //OriginLocation = new Point(99.626302, 23.928384, 0, SpatialReference.create(4326));
            //新版本
            //OriginLocation = new Point(99.9478, 24.9860, 0, SpatialReference.create(4326));
            //新版本2020/9/2
            OriginLocation = new Point(99.6178, 23.9106, 0, SpatialReference.create(4326));
            mMapView.setViewpointCenterAsync(OriginLocation, 1500000);
        }else mMapView.setViewpointScaleAsync(100000);*/
        OriginLocation = new Point(103.645, 27.98, SpatialReference.create(4326));
        mMapView.setViewpointCenterAsync(OriginLocation, 700000);
    }

    private List<Layer> BaseMMPKLayer;
    private void readMMPKData(){
        BaseMMPKLayer = new ArrayList<>();
        File file = new File(StaticVariableEnum.YSMMPKROOTPATH);
        Log.w(TAG, "readMMPKData: " + StaticVariableEnum.YSMMPKROOTPATH + ", 文件存在： " + file.exists());
        final MobileMapPackage mainMobileMapPackage = new MobileMapPackage(StaticVariableEnum.YSMMPKROOTPATH);
        mainMobileMapPackage.loadAsync();
        mainMobileMapPackage.addDoneLoadingListener(new Runnable() {
            @Override
            public void run() {
                LoadStatus mainLoadStatus = mainMobileMapPackage.getLoadStatus();
                if (mainLoadStatus == LoadStatus.LOADED) {
                    List<ArcGISMap> mainArcGISMapL = mainMobileMapPackage.getMaps();
                    Log.w(TAG, "readMMPKData: " + mainArcGISMapL.size());
                    map = mainArcGISMapL.get(0);
                    //map.setMaxScale(2000);
                    mMapView.setMap(map);
                    initSurfaceCenterPoint(mainMobileMapPackage);
                    drawGraphicsOverlayer();
                    for (int i = 0; i < map.getOperationalLayers().size(); i++) {
                        BaseLayerForMMPK.add(layerAdapter.getAliasName(map.getOperationalLayers().get(i).getName()));
                        BaseMMPKLayer.add(map.getOperationalLayers().get(i));
                    }
                    initLayerList();
                    useUserLayer();

                    //setRecyclerView();
                    /*loadRaster();
                    featureLayerShapefile();*/
                    //Log.w(TAG, "run: " + mMapView.getWidth() + "; " + (mMapView.getTop() + getStatusBarHeight(MainActivity.this) + getDaoHangHeight(MainActivity.this)) + "; " + (mMapView.getBottom() + getStatusBarHeight(MainActivity.this) + getDaoHangHeight(MainActivity.this)));

                }else {
                    Log.w(TAG, "readMMPKData: " + mainMobileMapPackage.getLoadError());
                    mainMobileMapPackage.loadAsync();
                }
            }
        });
    }

    private void initVariable(){
        //获取传感器管理器系统服务
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        strings = getResources().getStringArray(R.array.Type);
        graphicsOverlay_66 = new GraphicsOverlay();
        color_Whiteblank = Color.RED;
        map = new ArcGISMap();
        mCallout = mMapView.getCallout();

        //在地图中显示当前位置
        locationDisplay = mMapView.getLocationDisplay();
        Log.w(TAG, "initVariable: " + locationDisplay.getLocation().getPosition());
        locationDisplay.setShowLocation(true);
        locationDisplay.setAutoPanMode(LocationDisplay.AutoPanMode.COMPASS_NAVIGATION);
        locationDisplay.startAsync();
        if (locationDisplay.isShowPingAnimation()) {
            final LocationDataSource.Location location = locationDisplay.getLocation();
            mLocation = location.getPosition();
            locationDisplay.addLocationChangedListener(new LocationDisplay.LocationChangedListener() {
                @Override
                public void onLocationChanged(LocationDisplay.LocationChangedEvent locationChangedEvent) {
                    LocationDataSource.Location location = locationChangedEvent.getLocation();
                    mLocation = location.getPosition();
                    //Log.w(TAG, "initVariable: " + mLocation.getX() + "; "+ mLocation.getY());
                    if (mLocation != null) {
                        m_lat = mLocation.getY();
                        m_long = mLocation.getX();
                    }
                }
            });
        }
        //readDLLCGDBForShape();

        /* //2020/5/ 所使用的DLLC数据
        readDLLCGDBForGdb();*/

        /*
        // 2017-2019 所使用的老图层
        readJBNTGDB();
        readGDB();
        readPGDB();
        readXZCAreaGDB();*/

        // 2020/9/2 读取地矿院使用的新数据
        //readLCOutsideWorkDataForGDB();

        //读取永善县Geodatabase数据
        readYSGeodatabaseDataForGDB();
        readYSSimpleQueryGeodatabaseDataForGDB();
    }

    private void readDLLCGDBForShape(){
        try {
            if (isFileExist(StaticVariableEnum.DLLCROOTPATH)) {
                final ShapefileFeatureTable shapefileFeatureTable = new ShapefileFeatureTable(StaticVariableEnum.DLLCROOTPATH);
                shapefileFeatureTable.loadAsync();
                shapefileFeatureTable.addDoneLoadingListener(new Runnable() {
                    @Override
                    public void run() {
                        if (shapefileFeatureTable.getLoadStatus() == LoadStatus.LOADED) {
                            //XZCAreaFeatureLayer = new FeatureLayer(shapefileFeatureTable);
                            //Toast.makeText(MainActivity.this, "可以正常打开Geodatabase " + XZCAreaFeatureLayer.getFeatureTable().getField("NAME").getAlias(), Toast.LENGTH_LONG).show();
                            Toast.makeText(MainActivity.this, "可以正常打开Geodatabase ", Toast.LENGTH_LONG).show();
                        } else {
                            String error = "无法使用ShapeFile方式读取： " + shapefileFeatureTable.getLoadError().toString();
                            Toast.makeText(MainActivity.this, error, Toast.LENGTH_LONG).show();
                            Log.e(TAG, error);
                        }
                    }
                });
            } else
                Toast.makeText(MainActivity.this, R.string.QueryError_1, Toast.LENGTH_SHORT).show();
        }
        catch (Exception e) {
            Toast.makeText(MainActivity.this, "无法使用ShapeFile方式读取： " + e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    //记录LCRA层的FeatureLayer
    FeatureLayer LCRAFeatureLayer;
    //读取LCRA层
    private void readDLLCGDBForGdb(){
        if (isFileExist(StaticVariableEnum.DLLCROOTPATH)) {
            final Geodatabase localGdb = new Geodatabase(StaticVariableEnum.DLLCROOTPATH);
            localGdb.loadAsync();
            localGdb.addDoneLoadingListener(new Runnable() {
                @Override
                public void run() {

                     for(int i = 0; i < localGdb.getGeodatabaseFeatureTables().size(); ++i){
                        Log.w(TAG, "exception: " + localGdb.getGeodatabaseFeatureTables().get(i).getTableName());
                         LCRAFeatureLayer = new FeatureLayer(localGdb.getGeodatabaseFeatureTables().get(i));
                    }
                    //FeatureLayer featureLayer = new FeatureLayer(localGdb.getGeodatabaseFeatureTable("P图斑"));
                   // Log.w(TAG, "run: " + featureLayer.getFeatureTable().);
                    /*PTuBanFeatureLayer = new FeatureLayer(localGdb.getGeodatabaseFeatureTable("P图斑"));
                    queryPTB();
                    DLTB2009FeatureLayer = new FeatureLayer(localGdb.getGeodatabaseFeatureTable("DLTB2009"));
                    DLTB2016FeatureLayer = new FeatureLayer(localGdb.getGeodatabaseFeatureTable("DLTB2016"));
                    DLTB2017FeatureLayer = new FeatureLayer(localGdb.getGeodatabaseFeatureTable("DLTB2017"));*/
                }
            });
            Log.w(TAG, "run: " + localGdb.getLoadStatus().toString());
        } else Toast.makeText(MainActivity.this, R.string.QueryError_1, Toast.LENGTH_SHORT).show();
    }

    FeatureLayer PTuBanFeatureLayer;
    FeatureLayer DLTB2009FeatureLayer;
    FeatureLayer DLTB2016FeatureLayer;
    FeatureLayer DLTB2017FeatureLayer;
    FeatureLayer XZCAreaFeatureLayer;

    private void readXZCAreaGDB(){
        if (isFileExist(StaticVariableEnum.XZCAreaROOTPATH)) {
            final ShapefileFeatureTable shapefileFeatureTable = new ShapefileFeatureTable(StaticVariableEnum.XZCAreaROOTPATH);
            shapefileFeatureTable.loadAsync();
            shapefileFeatureTable.addDoneLoadingListener(new Runnable() {
                @Override
                public void run() {
                    if (shapefileFeatureTable.getLoadStatus() == LoadStatus.LOADED) {
                        XZCAreaFeatureLayer = new FeatureLayer(shapefileFeatureTable);
                    } else {
                        String error = "Shapefile feature table failed to load: " + shapefileFeatureTable.getLoadError().toString();
                        Toast.makeText(MainActivity.this, error, Toast.LENGTH_LONG).show();
                        Log.e(TAG, error);
                    }
                }
            });
        } else Toast.makeText(MainActivity.this, R.string.QueryError_1, Toast.LENGTH_SHORT).show();
    }

    private void readLCOutsideWorkDataForShp(){
        if (isFileExist(Environment.getExternalStorageDirectory().toString() + "/临沧市基本农田/临沧市级外业踏勘数据.geodatabase")) {
            final ShapefileFeatureTable shapefileFeatureTable = new ShapefileFeatureTable(Environment.getExternalStorageDirectory().toString() + "/临沧市基本农田/临沧市级外业踏勘数据.geodatabase");
            shapefileFeatureTable.loadAsync();
            shapefileFeatureTable.addDoneLoadingListener(new Runnable() {
                @Override
                public void run() {
                    if (shapefileFeatureTable.getLoadStatus() == LoadStatus.LOADED) {
                        //XZCAreaFeatureLayer = new FeatureLayer(shapefileFeatureTable);
                    } else {
                        String error = "Shapefile feature table failed to load: " + shapefileFeatureTable.getLoadError().toString();
                        Toast.makeText(MainActivity.this, error, Toast.LENGTH_LONG).show();
                        Log.e(TAG, error);
                    }
                }
            });
        } else Toast.makeText(MainActivity.this, R.string.QueryError_1, Toast.LENGTH_SHORT).show();
    }

    //List<FeatureLayer> featureLayerList;
    // TODO 2020/9/3 读取地矿院GDB方法
    List<LayerFieldsSheet> BaseLayerFieldsSheetList = null;
    List<LayerFieldsSheet> LayerFieldsSheetList = null;

    List<FeatureLayer> SimpleFeatureLayers = new ArrayList<>();

    private void readLCOutsideWorkDataForGDB(){
        if (isFileExist(StaticVariableEnum.LCDKYROOTPATH)) {
            final Geodatabase localGdb = new Geodatabase(StaticVariableEnum.LCDKYROOTPATH);
            localGdb.loadAsync();
            localGdb.addDoneLoadingListener(new Runnable() {
                @Override
                public void run() {
                    BaseLayerFieldsSheetList = new ArrayList<>();
                    LayerFieldsSheetList = new ArrayList<>();
                    //featureLayerList = new ArrayList<>();
                    for(int i = 0; i < localGdb.getGeodatabaseFeatureTables().size(); ++i){
                        Log.w(TAG, "run 2020/9/2: " + localGdb.getGeodatabaseFeatureTables().get(i).getTableName());
                        String TableName = localGdb.getGeodatabaseFeatureTables().get(i).getTableName();
                        List<FieldNameSheet> FieldNameSheetList = new ArrayList<>();
                        switch (TableName) {
                            case "DGX":
                                FieldNameSheet fns = new FieldNameSheet("BSGC", "高程值");
                                FieldNameSheetList.add(fns);
                                break;
                            case "STBHHX":
                                //FieldNameSheetList.add(null);
                                break;
                            case "YJJBNT":
                                //FieldNameSheetList.add(null);
                                break;
                            case "XZQ":
                                FieldNameSheet fns1 = new FieldNameSheet("XZQMC", "行政区名称");
                                FieldNameSheetList.add(fns1);
                                break;
                            case "PDT":
                                FieldNameSheet fns2 = new FieldNameSheet("PDJB", "坡度级别");
                                FieldNameSheetList.add(fns2);
                                break;
                            case "DLTB_3diao":
                                FieldNameSheet fns3 = new FieldNameSheet("QSDWMC", "权属单位名称");
                                FieldNameSheet fns4 = new FieldNameSheet("QSXZ", "权属性质");
                                FieldNameSheet fns5 = new FieldNameSheet("DLMC", "地类名称");
                                FieldNameSheet fns6 = new FieldNameSheet("DLBM", "地类编码");
                                FieldNameSheet fns7 = new FieldNameSheet("TBDLMJ", "图斑地类面积");
                                FieldNameSheet fns8 = new FieldNameSheet("KCMJ", "扣除面积");
                                FieldNameSheetList.add(fns3);
                                FieldNameSheetList.add(fns4);
                                FieldNameSheetList.add(fns5);
                                FieldNameSheetList.add(fns6);
                                FieldNameSheetList.add(fns7);
                                FieldNameSheetList.add(fns8);
                                break;
                            case "DLTB_2diao":
                                FieldNameSheet fns9 = new FieldNameSheet("QSDWMC", "权属单位名称");
                                FieldNameSheet fns10 = new FieldNameSheet("DLMC", "地类名称");
                                FieldNameSheet fns11 = new FieldNameSheet("DLBM", "地类编码");
                                FieldNameSheet fns12 = new FieldNameSheet("TBMJ", "图斑面积");
                                FieldNameSheetList.add(fns9);
                                FieldNameSheetList.add(fns10);
                                FieldNameSheetList.add(fns11);
                                FieldNameSheetList.add(fns12);
                                break;
                            case "DK":
                                FieldNameSheet fns13 = new FieldNameSheet("FBFMC", "发包方名称");
                                FieldNameSheet fns14 = new FieldNameSheet("CBFMC", "承包方名称");
                                FieldNameSheetList.add(fns13);
                                FieldNameSheetList.add(fns14);
                                break;
                            case "CJDCQ":
                                FieldNameSheet fns15 = new FieldNameSheet("ZLDWMC", "坐落单位名称");
                                FieldNameSheetList.add(fns15);
                                break;
                        }
                        FeatureLayer fl = new FeatureLayer(localGdb.getGeodatabaseFeatureTable(localGdb.getGeodatabaseFeatureTables().get(i).getTableName()));
                        Log.w(TAG, "run 2020/9/2: " + fl.getName() + ", " + fl.getFeatureTable().getGeometryType());
                        BaseLayerFieldsSheetList.add(new LayerFieldsSheet(fl, "", FieldNameSheetList));
                        LayerFieldsSheetList.add(new LayerFieldsSheet(fl, "", FieldNameSheetList));
                        //featureLayerList.add(new FeatureLayer(localGdb.getGeodatabaseFeatureTable(localGdb.getGeodatabaseFeatureTables().get(i).getTableName())));
                    }

                    /*for (int i = 0; i < BaseLayerFieldsSheetList.size(); i++) {
                        FeatureLayer fl = BaseLayerFieldsSheetList.get(i).getFeatureLayer();
                        List<Field> filedList = fl.getFeatureTable().getFields();
                        for (int j = 0; j < filedList.size(); j++) {
                            Log.w(TAG, "run 2020/9/2: " + fl.getName() + ", " + filedList.get(j).getName());
                        }
                    }*/
                    /*PTuBanFeatureLayer = new FeatureLayer(localGdb.getGeodatabaseFeatureTable("P图斑"));
                    queryPTB();
                    DLTB2009FeatureLayer = new FeatureLayer(localGdb.getGeodatabaseFeatureTable("DLTB2009"));
                    DLTB2016FeatureLayer = new FeatureLayer(localGdb.getGeodatabaseFeatureTable("DLTB2016"));
                    DLTB2017FeatureLayer = new FeatureLayer(localGdb.getGeodatabaseFeatureTable("DLTB2017"));*/
                    Log.w(TAG, "run: 20200904: " + LayerFieldsSheetList.size());
                    setRecyclerViewForDynamicChooseFrame();
                    //useUserLayer();
                }
            });
            Log.w(TAG, "run: " + localGdb.getLoadStatus().toString());
        } else Toast.makeText(MainActivity.this, R.string.QueryError_1, Toast.LENGTH_SHORT).show();
    }

    private void readYSSimpleQueryGeodatabaseDataForGDB(){
        if (isFileExist(StaticVariableEnum.YSSIMPLEZRZYROOTPATH)) {
            final Geodatabase localGdb = new Geodatabase(StaticVariableEnum.YSSIMPLEZRZYROOTPATH);
            localGdb.loadAsync();
            localGdb.addDoneLoadingListener(new Runnable() {
                @Override
                public void run() {
                    for(int i = 0; i < localGdb.getGeodatabaseFeatureTables().size(); ++i){
                        FeatureLayer fl = new FeatureLayer(localGdb.getGeodatabaseFeatureTables().get(i));
                        String name =fl.getName();
                        if (name.equals("行政区面"))
                            XZQAreaFeatureLayer = fl;
                        SimpleFeatureLayers.add(fl);
                    }
                }
            });
            Log.w(TAG, "run: " + localGdb.getLoadStatus().toString());
        } else Toast.makeText(MainActivity.this, R.string.QueryError_1, Toast.LENGTH_SHORT).show();
    }

    public static FeatureLayer XZQAreaFeatureLayer = null;

    private void readYSGeodatabaseDataForGDB(){
        if (isFileExist(StaticVariableEnum.YSZRZYROOTPATH)) {
            final Geodatabase localGdb = new Geodatabase(StaticVariableEnum.YSZRZYROOTPATH);
            localGdb.loadAsync();
            localGdb.addDoneLoadingListener(new Runnable() {
                @Override
                public void run() {
                    BaseLayerFieldsSheetList = new ArrayList<>();
                    LayerFieldsSheetList = new ArrayList<>();
                    //featureLayerList = new ArrayList<>();
                    for(int i = 0; i < localGdb.getGeodatabaseFeatureTables().size(); ++i){
                        Log.w(TAG, "run 2020/9/2: " + localGdb.getGeodatabaseFeatureTables().get(i).getTableName());
                        String TableName = localGdb.getGeodatabaseFeatureTables().get(i).getTableName();
                        List<FieldNameSheet> FieldNameSheetList = new ArrayList<>();
                        switch (TableName) {
                            /*case "DGX":
                                FieldNameSheet fns = new FieldNameSheet("BSGC", "高程值");
                                FieldNameSheetList.add(fns);
                                break;
                            case "STBHHX":
                                //FieldNameSheetList.add(null);
                                break;
                            case "YJJBNT":
                                //FieldNameSheetList.add(null);
                                break;
                            case "XZQ":
                                FieldNameSheet fns1 = new FieldNameSheet("XZQMC", "行政区名称");
                                FieldNameSheetList.add(fns1);
                                break;
                            case "PDT":
                                FieldNameSheet fns2 = new FieldNameSheet("PDJB", "坡度级别");
                                FieldNameSheetList.add(fns2);
                                break;
                            case "DLTB_3diao":
                                FieldNameSheet fns3 = new FieldNameSheet("QSDWMC", "权属单位名称");
                                FieldNameSheet fns4 = new FieldNameSheet("QSXZ", "权属性质");
                                FieldNameSheet fns5 = new FieldNameSheet("DLMC", "地类名称");
                                FieldNameSheet fns6 = new FieldNameSheet("DLBM", "地类编码");
                                FieldNameSheet fns7 = new FieldNameSheet("TBDLMJ", "图斑地类面积");
                                FieldNameSheet fns8 = new FieldNameSheet("KCMJ", "扣除面积");
                                FieldNameSheetList.add(fns3);
                                FieldNameSheetList.add(fns4);
                                FieldNameSheetList.add(fns5);
                                FieldNameSheetList.add(fns6);
                                FieldNameSheetList.add(fns7);
                                FieldNameSheetList.add(fns8);
                                break;
                            case "DLTB_2diao":
                                FieldNameSheet fns9 = new FieldNameSheet("QSDWMC", "权属单位名称");
                                FieldNameSheet fns10 = new FieldNameSheet("DLMC", "地类名称");
                                FieldNameSheet fns11 = new FieldNameSheet("DLBM", "地类编码");
                                FieldNameSheet fns12 = new FieldNameSheet("TBMJ", "图斑面积");
                                FieldNameSheetList.add(fns9);
                                FieldNameSheetList.add(fns10);
                                FieldNameSheetList.add(fns11);
                                FieldNameSheetList.add(fns12);
                                break;
                            case "DK":
                                FieldNameSheet fns13 = new FieldNameSheet("FBFMC", "发包方名称");
                                FieldNameSheet fns14 = new FieldNameSheet("CBFMC", "承包方名称");
                                FieldNameSheetList.add(fns13);
                                FieldNameSheetList.add(fns14);
                                break;
                            case "CJDCQ":
                                FieldNameSheet fns15 = new FieldNameSheet("ZLDWMC", "坐落单位名称");
                                FieldNameSheetList.add(fns15);
                                break;*/
                            case "地名点":
                                FieldNameSheet fns = new FieldNameSheet("标准名称", "名称");
                                FieldNameSheetList.add(fns);
                                break;
                            case "土地利用变更调查数据2020年":
                                FieldNameSheet fns16 = new FieldNameSheet("QSDWMC", "权属单位名称");
                                FieldNameSheet fns18 = new FieldNameSheet("DLMC", "地类名称");
                                FieldNameSheet fns19 = new FieldNameSheet("DLBM", "地类编码");
                                FieldNameSheet fns17 = new FieldNameSheet("TBMJ", "图斑面积");
                                FieldNameSheetList.add(fns16);
                                FieldNameSheetList.add(fns17);
                                FieldNameSheetList.add(fns18);
                                FieldNameSheetList.add(fns19);
                                break;
                            case "二调地类图斑":
                                FieldNameSheet fns20 = new FieldNameSheet("QSDWMC", "权属单位名称");
                                FieldNameSheet fns22 = new FieldNameSheet("DLMC", "地类名称");
                                FieldNameSheet fns23 = new FieldNameSheet("DLBM", "地类编码");
                                FieldNameSheet fns21 = new FieldNameSheet("TBMJ", "图斑面积");
                                FieldNameSheetList.add(fns20);
                                FieldNameSheetList.add(fns21);
                                FieldNameSheetList.add(fns22);
                                FieldNameSheetList.add(fns23);
                                break;
                            case "土地规划地类":
                                FieldNameSheet fns1 = new FieldNameSheet("GHDLMC", "规划地类名称");
                                FieldNameSheetList.add(fns1);
                                FieldNameSheet fns24 = new FieldNameSheet("GHDLMJ", "规划地类面积");
                                FieldNameSheetList.add(fns24);
                                FieldNameSheet fns25 = new FieldNameSheet("TBMJ", "图斑面积");
                                FieldNameSheetList.add(fns25);
                                break;
                            case "村级调查区":
                                FieldNameSheet fns2 = new FieldNameSheet("ZLDWMC", "坐落单位名称");
                                FieldNameSheetList.add(fns2);
                                break;
                            case "城镇开发边界试划":
                                FieldNameSheet fns3 = new FieldNameSheet("GHFQMC", "规划分区名称");
                                FieldNameSheet fns4 = new FieldNameSheet("MJ", "面积");
                                FieldNameSheetList.add(fns3);
                                FieldNameSheetList.add(fns4);

                                break;
                            case "批地数据":
                                FieldNameSheet fns9 = new FieldNameSheet("XMMC", "项目名称");
                                FieldNameSheet fns10 = new FieldNameSheet("PZWH", "批准文号");
                                FieldNameSheetList.add(fns9);
                                FieldNameSheetList.add(fns10);

                                break;
                            case "永善县稳定耕地":
                                FieldNameSheet fns13 = new FieldNameSheet("QSDWMC", "权属单位名称");
                                FieldNameSheet fns14 = new FieldNameSheet("DLMC", "地类名称");
                                FieldNameSheetList.add(fns13);
                                FieldNameSheetList.add(fns14);
                                FieldNameSheet fns11 = new FieldNameSheet("DLBM", "地类编码");
                                FieldNameSheet fns12 = new FieldNameSheet("TBMJ", "图斑面积");
                                FieldNameSheetList.add(fns11);
                                FieldNameSheetList.add(fns12);
                                break;
                            case "土地承包经营权":
                                FieldNameSheet fns15 = new FieldNameSheet("ZJRXM", "指界人姓名");
                                FieldNameSheetList.add(fns15);
                                break;
                            case "用地规划":
                                FieldNameSheet fns5 = new FieldNameSheet("YDXZ", "用地性质");
                                FieldNameSheetList.add(fns5);
                                break;
                            case "生态保护红线2021年7月15日":
                                FieldNameSheet fns6 = new FieldNameSheet("ZRBHDMC", "自然保护地名称");
                                FieldNameSheetList.add(fns6);
                                break;
                            case "生态保护红线调整后202103012207改":
                                FieldNameSheet fns7 = new FieldNameSheet("ZRBHDMC", "自然保护地名称");
                                FieldNameSheetList.add(fns7);
                                break;
                            case "永善县2017年至2019年县规划区内供地红线":
                                FieldNameSheet fns8 = new FieldNameSheet("PZWH", "批准文号");
                                FieldNameSheetList.add(fns8);
                                FieldNameSheet fns26 = new FieldNameSheet("GYFS", "供应方式");
                                FieldNameSheetList.add(fns26);
                                FieldNameSheet fns27 = new FieldNameSheet("TDYT", "土地用途");
                                FieldNameSheetList.add(fns27);
                                FieldNameSheet fns28 = new FieldNameSheet("SRR", "受让人");
                                FieldNameSheetList.add(fns28);
                                break;
                            case "永善县农转用项目图斑2010至2020":
                                FieldNameSheet fns29 = new FieldNameSheet("PZWH", "批准文号");
                                FieldNameSheetList.add(fns29);
                                FieldNameSheet fns30 = new FieldNameSheet("XMMC", "项目名称");
                                FieldNameSheetList.add(fns30);
                                break;
                            case "永善县土地整治验收项目图斑2019及以前":
                                FieldNameSheet fns31 = new FieldNameSheet("JBXX_XMMC", "项目名称");
                                FieldNameSheetList.add(fns31);
                                FieldNameSheet fns32 = new FieldNameSheet("YS_YSWH", "验收文号");
                                FieldNameSheetList.add(fns32);
                                break;
                            case "乡级行政区":
                                FieldNameSheet fns33 = new FieldNameSheet("XZQMC", "行政区名称");
                                FieldNameSheetList.add(fns33);
                                FieldNameSheet fns34 = new FieldNameSheet("XZQDM", "行政区代码");
                                FieldNameSheetList.add(fns34);
                                break;
                            case "县级行政区面":
                                FieldNameSheet fns35 = new FieldNameSheet("XZQMC", "行政区名称");
                                FieldNameSheetList.add(fns35);
                                FieldNameSheet fns36 = new FieldNameSheet("XZQDM", "行政区代码");
                                FieldNameSheetList.add(fns36);
                                break;
                            case "行政区面":
                                FieldNameSheet fns37 = new FieldNameSheet("XZQMC", "行政区名称");
                                FieldNameSheetList.add(fns37);
                                FieldNameSheet fns38 = new FieldNameSheet("XZQDM", "行政区代码");
                                FieldNameSheetList.add(fns38);
                                break;
                        }
                        FeatureLayer fl = new FeatureLayer(localGdb.getGeodatabaseFeatureTable(localGdb.getGeodatabaseFeatureTables().get(i).getTableName()));
                        Log.w(TAG, "run 2020/9/2: " + fl.getName() + ", " + fl.getFeatureTable().getGeometryType());
                        BaseLayerFieldsSheetList.add(new LayerFieldsSheet(fl, "", FieldNameSheetList));
                        LayerFieldsSheetList.add(new LayerFieldsSheet(fl, "", FieldNameSheetList));
                    }
                    Log.w(TAG, "run: 20200904: " + LayerFieldsSheetList.size());
                    setRecyclerViewForDynamicChooseFrame();
                }
            });
            Log.w(TAG, "run: " + localGdb.getLoadStatus().toString());
        } else Toast.makeText(MainActivity.this, R.string.QueryError_1, Toast.LENGTH_SHORT).show();
    }

    private void readPGDB(){
        if (isFileExist(StaticVariableEnum.PGDBROOTPATH)) {
            final Geodatabase localGdb = new Geodatabase(StaticVariableEnum.PGDBROOTPATH);
            localGdb.loadAsync();
            localGdb.addDoneLoadingListener(new Runnable() {
                @Override
                public void run() {
                    /* (int i = 0; i < localGdb.getGeodatabaseFeatureTables().size(); ++i){
                        Log.w(TAG, "run: " + localGdb.getGeodatabaseFeatureTables().get(i).getTableName());
                    }
                    FeatureLayer featureLayer = new FeatureLayer(localGdb.getGeodatabaseFeatureTable("P图斑"));
                    Log.w(TAG, "run: " + featureLayer.getFeatureTable().);*/
                    PTuBanFeatureLayer = new FeatureLayer(localGdb.getGeodatabaseFeatureTable("P图斑"));
                    queryPTB();
                    DLTB2009FeatureLayer = new FeatureLayer(localGdb.getGeodatabaseFeatureTable("DLTB2009"));
                    DLTB2016FeatureLayer = new FeatureLayer(localGdb.getGeodatabaseFeatureTable("DLTB2016"));
                    DLTB2017FeatureLayer = new FeatureLayer(localGdb.getGeodatabaseFeatureTable("DLTB2017"));
                }
            });
            Log.w(TAG, "run: " + localGdb.getLoadStatus().toString());
        } else Toast.makeText(MainActivity.this, R.string.QueryError_1, Toast.LENGTH_SHORT).show();
    }

    private void readGDB(){
        if (isFileExist(StaticVariableEnum.GDBROOTPATH)) {
            final Geodatabase localGdb = new Geodatabase(StaticVariableEnum.GDBROOTPATH);
            Log.w(TAG, "run: " + localGdb.getLoadStatus().toString());
            Log.w(TAG, "run: " + localGdb.getPath());
            localGdb.loadAsync();
            localGdb.addDoneLoadingListener(new Runnable() {
                @Override
                public void run() {
                    TDGHDLFeatureLayer = new FeatureLayer(localGdb.getGeodatabaseFeatureTable("土地规划地类"));
                    //TDGHDLFeatureLayer = new FeatureLayer(localGdb.getGeodatabaseFeatureTable("基本农田保护区"));
                    /*for (int i = 0; i < localGdb.getGeodatabaseFeatureTables().size(); ++i){
                        Log.w(TAG, "run: " + localGdb.getGeodatabaseFeatureTables().get(i).getTableName());
                    }*/
                    XZQFeatureLayer = new FeatureLayer(localGdb.getGeodatabaseFeatureTable("行政区"));
                    DMPointFeatureLayer = new FeatureLayer(localGdb.getGeodatabaseFeatureTable("地名点"));
                    Log.w(TAG, "run 2020/9/2: " + DMPointFeatureLayer.getFeatureTable().getGeometryType());
                    //mMapView.setViewpointCenterAsync();
                }
            });
            Log.w(TAG, "run: " + localGdb.getLoadStatus().toString());
        } else Toast.makeText(MainActivity.this, R.string.QueryError_1, Toast.LENGTH_SHORT).show();
    }

    private void readKSTest(){
        if (isFileExist(StaticVariableEnum.KSTestROOTPATH)) {
            final Geodatabase localGdb = new Geodatabase(StaticVariableEnum.KSTestROOTPATH);
            localGdb.loadAsync();
            localGdb.addDoneLoadingListener(new Runnable() {
                @Override
                public void run() {
                    TDGHDLFeatureLayer = new FeatureLayer(localGdb.getGeodatabaseFeatureTable("土地规划地类"));
                    XZQFeatureLayer = new FeatureLayer(localGdb.getGeodatabaseFeatureTable("行政区"));
                    DMPointFeatureLayer = new FeatureLayer(localGdb.getGeodatabaseFeatureTable("地名点"));
                }
            });
            Log.w(TAG, "run: " + localGdb.getLoadStatus().toString());
        } else Toast.makeText(MainActivity.this, R.string.QueryError_1, Toast.LENGTH_SHORT).show();
    }

    FeatureLayer JBNTFeatureLayer;
    private void readJBNTGDB(){
        if (isFileExist(StaticVariableEnum.JBNTGDBROOTPATH)) {
            final Geodatabase localGdb = new Geodatabase(StaticVariableEnum.JBNTGDBROOTPATH);
            Log.w(TAG, "run: " + localGdb.getLoadStatus().toString());
            Log.w(TAG, "run: " + localGdb.getPath());
            localGdb.loadAsync();
            localGdb.addDoneLoadingListener(new Runnable() {
                @Override
                public void run() {
                    JBNTFeatureLayer = new FeatureLayer(localGdb.getGeodatabaseFeatureTable("基本农田保护区"));
                }
            });
            Log.w(TAG, "run: " + localGdb.getPath() + localGdb.getLoadStatus().toString());
        } else Toast.makeText(MainActivity.this, R.string.QueryError_1, Toast.LENGTH_SHORT).show();
    }

    private boolean isFileExist(String path){
        try {
            File file = new File(path);
            if (file.exists()) return true;
            else return false;
        }catch (Exception e){
            Toast.makeText(MainActivity.this, "文件地址存在性检测错误，请核实文件地址!", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private boolean isFileExist(URI path){
        try {
            File file = new File(path);
            if (file.exists()) return true;
            else return false;
        }catch (Exception e){
            Toast.makeText(MainActivity.this, "文件地址存在性检测错误，请核实文件地址!", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        instance = this;
        //去除水印
        //ArcGISRuntimeEnvironment.setLicense("runtimelite,1000,rud4449636536,none,NKMFA0PL4S0DRJE15166");

        readOtherData();
        readXZQ();
        RandomColorCreator();
        // TODO 暂时删除
        queriedMyTuban = new my_tb();
        requestAuthority();
        ic = "自然资源监管系统";
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_list_black_24dp);
        }

        /*Toast.makeText(MainActivity.this, DeviceUtil.GetAndroidID(this), Toast.LENGTH_LONG).show();
        Log.w(TAG, "onCreate: " + DeviceUtil.GetAndroidID(this));*/
    }

    private void RandomColorCreator(){
        Random r = new Random();

        FeaturevalueAndColor = new HashMap<>();
        for (int i = 0; i < 2000; i++) {
            FeaturevalueAndColor.put(i, Color.argb(128, r.nextInt(255), r.nextInt(255), r.nextInt(255)));
        }
    }

    private double[] GetCoordinate(String QueryString){
        String[] strings = QueryString.split(";");
        if (strings.length == 2){
            String longitude = strings[0];
            String latitude = strings[1];
            Boolean longitudeIsNumeric = longitude.matches("-?[0-9]+.?[0-9]*");
            Boolean latitudeIsNumeric = latitude.matches("-?[0-9]+.?[0-9]*");
            if (longitudeIsNumeric && latitudeIsNumeric)
            {
                double[] coordinate = {Double.valueOf(longitude), Double.valueOf(latitude)};
                return coordinate;
            }
            else{
                try{
                    double mLongitude = Double.valueOf(longitude.substring(0, longitude.indexOf("°"))) + Double.valueOf(longitude.substring(longitude.indexOf("°")+1, longitude.indexOf("′")))/60 + Double.valueOf(longitude.substring(longitude.indexOf("′")+1, longitude.indexOf("″")))/3600;
                    double mLatitude = Double.valueOf(latitude.substring(0, latitude.indexOf("°"))) + Double.valueOf(latitude.substring(latitude.indexOf("°")+1, latitude.indexOf("′")))/60 + Double.valueOf(latitude.substring(latitude.indexOf("′")+1, latitude.indexOf("″")))/3600;

                    double[] coordinate = {Double.valueOf(mLongitude), Double.valueOf(mLatitude)};
                    return coordinate;
                }
                catch (Exception e){
                    return null;
                }
            }
        }
        else
            return null;
    }

    private void InputDataForTif(){
        Intent intent = new Intent(MainActivity.this, Activity_FileManage.class);
        intent.putExtra("type", ".tif");
        //intent.putExtra("type", ".shp");
        startActivityForResult(intent, EnumClass.GET_TIF_FILE);
    }

    private void InputDataForShp(){
        Intent intent = new Intent(MainActivity.this, Activity_FileManage.class);
        intent.putExtra("type", ".shp");
        //intent.putExtra("type", ".shp");
        startActivityForResult(intent, EnumClass.GET_SHP_FILE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case StaticVariableEnum.PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    doSpecificOperation();
                    initWidgetAndVariable();
                    readMMPKData();
                }else {
                    Toast.makeText(MainActivity.this, "请通过所有申请的权限", Toast.LENGTH_LONG).show();
                }
                break;
            case StaticVariableEnum.PERMISSION_CODE_1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    doSpecificOperation();
                    initWidgetAndVariable();
                    readMMPKData();
                }else {
                    Toast.makeText(MainActivity.this, "请通过文件读取权限", Toast.LENGTH_LONG).show();
                }
                break;
            case StaticVariableEnum.PERMISSION_CODE_2:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    doSpecificOperation();
                    initWidgetAndVariable();
                    readMMPKData();
                }else {
                    Toast.makeText(MainActivity.this, "请通过定位权限", Toast.LENGTH_LONG).show();
                }
                break;
                default:
        }
    }

    private void showStandardWidget(){
        whiteBlank_fab.setVisibility(View.VISIBLE);
        MapQueryBT.setVisibility(View.VISIBLE);
        ShowTextButton();
        //DrawFeature.setVisibility(View.VISIBLE);
        LocHereBT.setVisibility(View.VISIBLE);
        ResetBT.setVisibility(View.VISIBLE);
        FloatingActionButton DeleteBt = (FloatingActionButton) findViewById(R.id.DeleteFeatures);
        DeleteBt.setVisibility(View.VISIBLE);
        FloatingActionButton TrailBt = (FloatingActionButton) findViewById(R.id.StartTrail);
        TrailBt.setVisibility(View.VISIBLE);
        FloatingActionButton outputbt = (FloatingActionButton) findViewById(R.id.OutputData);
        outputbt.setVisibility(View.VISIBLE);
        FloatingActionButton InputDataBt = findViewById(R.id.InputData);
        InputDataBt.setVisibility(View.VISIBLE);
        FloatingActionButton AddPOIFAB = (FloatingActionButton) findViewById(R.id.AddPOIFAB);
        AddPOIFAB.setVisibility(View.VISIBLE);
        FloatingActionButton XZQQueryFAB = findViewById(R.id.XZQQueryFAB);
        XZQQueryFAB.setVisibility(View.VISIBLE);
        FloatingActionButton NeedQueryFAB = findViewById(R.id.NeedQueryFAB);
        NeedQueryFAB.setVisibility(View.VISIBLE);
        FloatingActionButton DistanceMessureFAB = (FloatingActionButton) findViewById(R.id.DistanceMessureFAB);
        DistanceMessureFAB.setVisibility(View.VISIBLE);
        FloatingActionButton AreaMessureFAB = findViewById(R.id.AreaMessureFAB);
        AreaMessureFAB.setVisibility(View.VISIBLE);
        FloatingActionButton ReadPDFFAB = findViewById(R.id.ReadPDFFAB);
        ReadPDFFAB.setVisibility(View.VISIBLE);
        /*Button OutputBT = findViewById(R.id.OutputDataBT);
        OutputBT.setVisibility(View.VISIBLE);
        Button InputBT = findViewById(R.id.InputDataBT);
        InputBT.setVisibility(View.VISIBLE);*/
    }

    private void removeStandardWidget(){
        // TODO 2020/9/4 各区域占比
        whiteBlank_fab.setVisibility(View.GONE);
        MapQueryBT.setVisibility(View.GONE);
        DrawFeature.setVisibility(View.GONE);
        RemoveTextButton();
        LocHereBT.setVisibility(View.GONE);
        ResetBT.setVisibility(View.GONE);

        FloatingActionButton DeleteBt = (FloatingActionButton) findViewById(R.id.DeleteFeatures);
        DeleteBt.setVisibility(View.GONE);

        FloatingActionButton TrailBt = (FloatingActionButton) findViewById(R.id.StartTrail);
        TrailBt.setVisibility(View.GONE);

        FloatingActionButton outputbt = (FloatingActionButton) findViewById(R.id.OutputData);
        outputbt.setVisibility(View.GONE);

        FloatingActionButton InputDataBt = findViewById(R.id.InputData);
        InputDataBt.setVisibility(View.GONE);

        FloatingActionButton ReadPDFFAB = findViewById(R.id.ReadPDFFAB);
        ReadPDFFAB.setVisibility(View.GONE);
        /*Button OutputBT = findViewById(R.id.OutputDataBT);
        OutputBT.setVisibility(View.GONE);
        Button InputBT = findViewById(R.id.InputDataBT);
        InputBT.setVisibility(View.GONE);*/
    }

    private DisplayEnum RunningAnalyseFunction = DisplayEnum.ANA_NONE;

    private void cancelBtFunction(){
        queriedMyTuban = new my_tb();
        QueryProcessType = DisplayEnum.NOQUERY;
        removeQueryWidget();
        showStandardWidget();
        DrawType = DisplayEnum.DRAW_NONE;
        if (RunningAnalyseFunction == DisplayEnum.ANA_NEED)
            mapQueryBtEvent();
        else {
            mMapView.getGraphicsOverlays().clear();
            drawGraphicsOverlayer();
        }
        setTitle(R.string.app_name);
        RunningAnalyseFunction = DisplayEnum.ANA_NONE;
    }

    private void showQueryWidget(){
        FloatingActionButton cancel = (FloatingActionButton) findViewById(R.id.CancelQuery);
        cancel.setVisibility(View.VISIBLE);
        FloatingActionButton clear = (FloatingActionButton) findViewById(R.id.ClearQuery);
        clear.setVisibility(View.VISIBLE);
        FloatingActionButton back = (FloatingActionButton) findViewById(R.id.BackQuery);
        back.setVisibility(View.VISIBLE);
        final FloatingActionButton finish = (FloatingActionButton) findViewById(R.id.FinishQuery);
        finish.setVisibility(View.VISIBLE);
        final FloatingActionButton change = (FloatingActionButton) findViewById(R.id.ChangeQuery);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (queriedMyTuban.getName() != null && pointCollection.size() < 3){
                    //TODO 删除图斑逻辑
                    AlertDialog.Builder dialog1 = new AlertDialog.Builder(MainActivity.this);
                    dialog1.setTitle("提示");
                    dialog1.setMessage("你确定要删除该图斑吗？");
                    dialog1.setCancelable(false);
                    dialog1.setPositiveButton("重置该图斑", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(MainActivity.this, "该图斑已恢复", Toast.LENGTH_LONG).show();
                            cancelBtFunction();
                        }
                    });
                    dialog1.setNegativeButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(MainActivity.this, queriedMyTuban.getName() + "已删除", Toast.LENGTH_LONG).show();
                            LitePal.deleteAll(my_tb.class, "name = ?", queriedMyTuban.getName());
                            cancelBtFunction();
                        }
                    });
                    dialog1.show();
                }else {
                    cancelBtFunction();
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeGraphicsOverlayers();
                if (pointCollection.size() >= 1)
                    pointCollection.remove(pointCollection.size() - 1);
                if (pointCollection.size() == 1){
                    GraphicsOverlay graphicsOverlay_1 = new GraphicsOverlay();
                    SimpleMarkerSymbol lineSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.GREEN, 3);
                    Graphic fillGraphic = new Graphic(new Point(pointCollection.get(0).getX(), pointCollection.get(0).getY(), SpatialReference.create(4523)), lineSymbol);
                    graphicsOverlay_1.getGraphics().add(fillGraphic);
                    mMapView.getGraphicsOverlays().add(graphicsOverlay_1);
                }else if (pointCollection.size() == 2){
                    GraphicsOverlay graphicsOverlay_1 = new GraphicsOverlay();
                    SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.GREEN, 3);
                    Graphic fillGraphic = new Graphic(new Polyline(pointCollection, SpatialReference.create(4523)), lineSymbol);
                    graphicsOverlay_1.getGraphics().add(fillGraphic);
                    mMapView.getGraphicsOverlays().add(graphicsOverlay_1);
                }else if (pointCollection.size() > 2){
                    GraphicsOverlay graphicsOverlay_1 = new GraphicsOverlay();
                    SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.GREEN, 3);
                    SimpleFillSymbol fillSymbol;
                    Graphic fillGraphic;
                    switch (RunningAnalyseFunction){
                        case ANA_DISTANCE:
                            fillGraphic = new Graphic(new Polyline(pointCollection, SpatialReference.create(4523)), lineSymbol);
                            graphicsOverlay_1.getGraphics().add(fillGraphic);
                            mMapView.getGraphicsOverlays().add(graphicsOverlay_1);
                            break;
                        case ANA_AREA:
                            fillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.HORIZONTAL, Color.GREEN, lineSymbol);
                            fillGraphic = new Graphic(new Polygon(pointCollection, SpatialReference.create(4523)), fillSymbol);
                            graphicsOverlay_1.getGraphics().add(fillGraphic);
                            mMapView.getGraphicsOverlays().add(graphicsOverlay_1);
                            break;
                        case ANA_NEED:
                            fillGraphic = new Graphic(new Polygon(pointCollection, SpatialReference.create(4523)), lineSymbol);
                            graphicsOverlay_1.getGraphics().add(fillGraphic);
                            mMapView.getGraphicsOverlays().add(graphicsOverlay_1);
                            break;
                    }
                }
            }
        });
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "已清空", Toast.LENGTH_SHORT).show();
                pointCollection.clear();
                removeGraphicsOverlayers();
            }
        });
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (RunningAnalyseFunction){
                    case ANA_NEED:
                        NeedQuery(change);
                        break;
                    case ANA_AREA:
                        if (QueryProcessType == DisplayEnum.INQUERY) {
                            if (DrawType == DisplayEnum.DRAW_POLYGON && pointCollection.size() >= 3) {
                                DrawType = DisplayEnum.DRAW_NONE;
                                final Polygon polygon = new Polygon(pointCollection);
                                DecimalFormat df = new DecimalFormat("0.0");
                                double area = GeometryEngine.areaGeodetic(polygon, new AreaUnit(AreaUnitId.SQUARE_KILOMETERS), GeodeticCurveType.GEODESIC);
                                String str = df.format(area);
                                TextView calloutContent = new TextView(getApplicationContext());
                                calloutContent.setTextColor(Color.BLACK);
                                if (area < 0.1)
                                {
                                    area = GeometryEngine.areaGeodetic(polygon, new AreaUnit(AreaUnitId.SQUARE_METERS), GeodeticCurveType.GEODESIC);
                                    str = df.format(area);
                                    double s = GeometryEngine.lengthGeodetic(polygon, new LinearUnit(LinearUnitId.METERS), GeodeticCurveType.GEODESIC);
                                    calloutContent.setText("面积：" + str + "平方米" + "\n" + "周长：" + df.format(s) + "米");
                                }
                                else {
                                    double s = GeometryEngine.lengthGeodetic(polygon, new LinearUnit(LinearUnitId.KILOMETERS), GeodeticCurveType.GEODESIC);
                                    calloutContent.setText("面积：" + str + getResources().getString(R.string.SQUARE_KILOMETERS) + "\n" + "周长：" + df.format(s) + "公里");
                                }
                                //calloutContent.setText(str);
                                // get callout, set content and show
                                mCallout.setLocation(new Point(polygon.getExtent().getCenter().getX(), polygon.getExtent().getYMax(), SpatialReference.create(4523)));
                                mCallout.setContent(calloutContent);
                                mCallout.show();
                                //if (!inMap) mCallout.dismiss();
                                removeQueryWidgetFinish();
                                QueryProcessType = DisplayEnum.FINISHQUERY;

                                //change.setVisibility(View.VISIBLE);
                            } else
                                Toast.makeText(MainActivity.this, "请构建面(至少三个点)", Toast.LENGTH_SHORT).show();
                        }else if (QueryProcessType == DisplayEnum.FINISHQUERY) {
                            QueryProcessType = DisplayEnum.NOQUERY;
                            removeQueryWidgetFinishLater();
                            showStandardWidget();
                            DrawType = DisplayEnum.DRAW_NONE;
                            mCallout.dismiss();
                            mMapView.getGraphicsOverlays().clear();
                            drawGraphicsOverlayer();
                        }
                        break;
                    case ANA_DISTANCE:
                        if (QueryProcessType == DisplayEnum.INQUERY) {
                            if (DrawType == DisplayEnum.DRAW_POLYLINE && pointCollection.size() >= 2) {
                                DrawType = DisplayEnum.DRAW_NONE;
                                final Polyline polyline = new Polyline(pointCollection);
                                DecimalFormat df = new DecimalFormat("0.0");
                                double length = GeometryEngine.lengthGeodetic(polyline, new LinearUnit(LinearUnitId.KILOMETERS), GeodeticCurveType.GEODESIC);
                                TextView calloutContent = new TextView(getApplicationContext());
                                if (length < 10){
                                    length = GeometryEngine.lengthGeodetic(polyline, new LinearUnit(LinearUnitId.METERS), GeodeticCurveType.GEODESIC);
                                    String str = df.format(length);
                                    Log.w(TAG, "onClick: " + str);
                                    calloutContent.setTextColor(Color.BLACK);
                                    calloutContent.setText(str + "米");
                                }
                                else{
                                    String str = df.format(length);
                                    Log.w(TAG, "onClick: " + str);
                                    calloutContent.setTextColor(Color.BLACK);
                                    calloutContent.setText(str + getResources().getString(R.string.KILOMETERS));
                                }
                                //calloutContent.setText(str + R.string.SQUARE_KILOMETERS);
                                // get callout, set content and show
                                mCallout.setLocation(new Point(polyline.getExtent().getCenter().getX(), polyline.getExtent().getYMax(), SpatialReference.create(4523)));
                                mCallout.setContent(calloutContent);
                                mCallout.show();
                                //if (!inMap) mCallout.dismiss();
                                removeQueryWidgetFinish();
                                QueryProcessType = DisplayEnum.FINISHQUERY;
                                //change.setVisibility(View.VISIBLE);
                            } else
                                Toast.makeText(MainActivity.this, "请构建线(至少两个点)", Toast.LENGTH_SHORT).show();
                        }else if (QueryProcessType == DisplayEnum.FINISHQUERY) {
                            QueryProcessType = DisplayEnum.NOQUERY;
                            removeQueryWidgetFinishLater();
                            showStandardWidget();
                            DrawType = DisplayEnum.DRAW_NONE;
                            mCallout.dismiss();
                            mMapView.getGraphicsOverlays().clear();
                            drawGraphicsOverlayer();
                            }
                        break;
                }
            }
        });
    }

    private void NeedQueryForBasePolygon(final Polygon polygon){
        final FloatingActionButton finish = (FloatingActionButton) findViewById(R.id.FinishQuery);
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnalysisUserGeometry = null;

                queriedMyTuban = new my_tb();
                QueryProcessType = DisplayEnum.NOQUERY;
                removeQueryWidgetFinishLater();
                showStandardWidget();
                DrawType = DisplayEnum.DRAW_NONE;
                mapQueryBtEvent();
                setTitle(R.string.app_name);
                removePopWindowForListShow();
            }
        });
        final QueryParameters query = new QueryParameters();
        query.setGeometry(polygon);// 设置空间几何对象
        PopWindowData = new ArrayList<>();
        try {
            //基本农田保护区 老版本
            //querySingleTaskForPolygon(query, polygon, JBNTFeatureLayer.getFeatureTable(), "JBNTBHQ");

            if(QueriedLayerIndex != -1)
            {
                queryTaskFor20200904ForBasePolygon(query, polygon);
                SaveUserDrawedTB();
            }
            // TODO 2020/9/4 完成新的按需查询内容
            //querySingleTaskForPolygon(query, polygon, LayerFieldsSheetList.get(1));
        }catch (ArcGISRuntimeException e){
            Toast.makeText(MainActivity.this, e.getMessage() + e.getErrorCode(), Toast.LENGTH_SHORT).show();
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                //查询所有的图层内容 老版
                queryAllTaskForPolygon(query, polygon);
            }
        }).start();
        if (!inMap) mCallout.dismiss();
        QueryProcessType = DisplayEnum.FINISHQUERY;
        RunningAnalyseFunction = DisplayEnum.ANA_NEED;
    }

    private void NeedQuery(FloatingActionButton change){
        if (QueryProcessType == DisplayEnum.INQUERY) {
            if (DrawType == DisplayEnum.DRAW_POLYGON && pointCollection.size() >= 3) {
                DrawType = DisplayEnum.DRAW_NONE;
                final Polygon polygon = new Polygon(pointCollection);
                final QueryParameters query = new QueryParameters();
                query.setGeometry(polygon);// 设置空间几何对象
                PopWindowData = new ArrayList<>();
                try {
                    //基本农田保护区 老版本
                    //querySingleTaskForPolygon(query, polygon, JBNTFeatureLayer.getFeatureTable(), "JBNTBHQ");

                    if(QueriedLayerIndex != -1)
                    {
                        queryTaskFor20200904(query, polygon);
                        SaveUserDrawedTB();
                    }
                    // TODO 2020/9/4 完成新的按需查询内容
                    //querySingleTaskForPolygon(query, polygon, LayerFieldsSheetList.get(1));
                }catch (ArcGISRuntimeException e){
                    Toast.makeText(MainActivity.this, e.getMessage() + e.getErrorCode(), Toast.LENGTH_SHORT).show();
                }

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //查询所有的图层内容 老版
                        queryAllTaskForPolygon(query, polygon);
                    }
                }).start();
                if (!inMap) mCallout.dismiss();
                QueryProcessType = DisplayEnum.FINISHQUERY;
                removeQueryWidgetFinish();
                //change.setVisibility(View.VISIBLE);
            } else
                Toast.makeText(MainActivity.this, "请构建面(至少三个点)", Toast.LENGTH_SHORT).show();
        } else if (QueryProcessType == DisplayEnum.FINISHQUERY) {
            IsDrawedTuban = false;
            queriedMyTuban = new my_tb();
            QueryProcessType = DisplayEnum.NOQUERY;
            removeQueryWidgetFinishLater();
            showStandardWidget();
            DrawType = DisplayEnum.DRAW_NONE;
            mapQueryBtEvent();
            setTitle(R.string.app_name);
            removePopWindowForListShow();
        }
    }

    private void SaveUserDrawedTB(){
        //if (IsDrawedTuban)
        {
            final FloatingActionButton popListShow = (FloatingActionButton) findViewById(R.id.PopWindow);
            popListShow.setVisibility(View.VISIBLE);
            popListShow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // TODO 2020/12/10 解决不弹出分析窗口的问题

                    String data = "";
                    String mStrPointCollection = "";
                    if (pointCollection != null) {
                        for (int i = 0; i < pointCollection.size(); ++i) {
                            Log.w(TAG, "onClick: " + pointCollection.get(i).getX() + ";" + pointCollection.get(i).getY());
                            mStrPointCollection = mStrPointCollection + pointCollection.get(i).getX() + "," + pointCollection.get(i).getY();
                            if (i != pointCollection.size() - 1)
                                mStrPointCollection = mStrPointCollection + ";";
                        }
                    }
                /*
                Intent intent = new Intent(MainActivity.this, PopWindowForListShow.class);
                intent.putExtra("data", data);
                intent.putExtra("pointCollection", mStrPointCollection);
                intent.putExtra("name", queriedMyTuban.getName());
                Log.w(TAG, "onClick: " + pointCollection.getSpatialReference().toString());
                startActivity(intent);*/

                    if (IsDrawedTuban)
                        isSaveOrUpdate(queriedMyTuban.getName(), mStrPointCollection, data);
                }
            });
        }
    }

    private void showPopWindowForListShow(){
        final FloatingActionButton popListShow = (FloatingActionButton) findViewById(R.id.PopWindow);
        popListShow.setVisibility(View.VISIBLE);
        popListShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // TODO 2020/12/10 解决不弹出分析窗口的问题

                String data = "";
                for (int i = 0; i < PopWindowData.size(); ++i){
                    if (i > 0)
                        data = data + ";" + PopWindowData.get(i);
                    else
                        data = PopWindowData.get(i);
                }
                String mStrPointCollection = "";
                for (int i = 0; i < pointCollection.size(); ++i){
                    Log.w(TAG, "onClick: " + pointCollection.get(i).getX() + ";" + pointCollection.get(i).getY());
                    mStrPointCollection = mStrPointCollection + pointCollection.get(i).getX() + "," + pointCollection.get(i).getY();
                    if (i != pointCollection.size() - 1)
                        mStrPointCollection = mStrPointCollection + ";";
                }
                /*
                Intent intent = new Intent(MainActivity.this, PopWindowForListShow.class);
                intent.putExtra("data", data);
                intent.putExtra("pointCollection", mStrPointCollection);
                intent.putExtra("name", queriedMyTuban.getName());
                Log.w(TAG, "onClick: " + pointCollection.getSpatialReference().toString());
                startActivity(intent);*/

                if (IsDrawedTuban)
                    isSaveOrUpdate(queriedMyTuban.getName(), mStrPointCollection, data);
            }
        });
    }


    private void isSaveOrUpdate(String name, String PointCollection, String data){
        if (name == null)
        {
            saveTbData(PointCollection, data);
        }
        else
        {
            updateTbData(name, PointCollection, data);
        }
    }

    //Boolean isThisUserDrawedTBSaved = false;
    private void saveTbData(String PointCollection, String data){
        String name = "图斑" + System.currentTimeMillis();
        my_tb my_tb = new my_tb();
        my_tb.setPointCollection(PointCollection);
        my_tb.setMdata(data);
        my_tb.setName(name);
        my_tb.save();
        Toast.makeText(MainActivity.this, name + "已经保存", Toast.LENGTH_SHORT).show();
        // isThisUserDrawedTBSaved = true;
    }

    private void updateTbData(String name, String PointCollection, String data){
        //String name = "图斑" + LitePal.findAll(my_tb.class).size();
        my_tb my_tb = new my_tb();
        my_tb.setPointCollection(PointCollection);
        my_tb.setMdata(data);
        my_tb.setName(name);
        my_tb.updateAll("name = ?", name);
        Toast.makeText(MainActivity.this, name + "已经更新", Toast.LENGTH_SHORT).show();
    }

    private void removePopWindowForListShow(){
        FloatingActionButton popListShow = (FloatingActionButton) findViewById(R.id.PopWindow);
        popListShow.setVisibility(View.GONE);
    }

    private void showQueryWidgetForP(){
        FloatingActionButton cancel = (FloatingActionButton) findViewById(R.id.CancelQuery);
        cancel.setVisibility(View.VISIBLE);
        FloatingActionButton clear = (FloatingActionButton) findViewById(R.id.ClearQuery);
        clear.setVisibility(View.GONE);
        FloatingActionButton back = (FloatingActionButton) findViewById(R.id.BackQuery);
        back.setVisibility(View.GONE);
        final FloatingActionButton finish = (FloatingActionButton) findViewById(R.id.FinishQuery);
        finish.setVisibility(View.VISIBLE);
        final FloatingActionButton change = (FloatingActionButton) findViewById(R.id.ChangeQuery);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QueryProcessType = DisplayEnum.NOQUERY;
                removeQueryWidget();
                showStandardWidget();
                DrawType = DisplayEnum.DRAW_NONE;
                if (RunningAnalyseFunction == DisplayEnum.ANA_NEED)
                    mapQueryBtEvent();
                else {
                    mMapView.getGraphicsOverlays().clear();
                    drawGraphicsOverlayer();
                }
                setTitle(R.string.app_name);
                RunningAnalyseFunction = DisplayEnum.ANA_NONE;
                pieChartView.setVisibility(View.INVISIBLE);
                removeGraphicsOverlayers();
            }
        });
        //change.setVisibility(View.VISIBLE);
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (QueryProcessType == DisplayEnum.INQUERY) {
                    //final Polygon polygon = new Polygon(pointCollection);
                    final QueryParameters query = new QueryParameters();
                    query.setGeometry(mPolygon);// 设置空间几何对象

                    PopWindowData = new ArrayList<>();

                    try {
                        //基本农田保护区

                        querySingleTaskForPolygon(query, (Polygon) mPolygon, JBNTFeatureLayer.getFeatureTable(), "JBNTBHQ");
                        SaveUserDrawedTB();
                    }catch (ArcGISRuntimeException e){
                        Toast.makeText(MainActivity.this, e.getMessage() + e.getErrorCode(), Toast.LENGTH_SHORT).show();
                    }


                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            //查询所有的图层内容
                            queryAllTaskForPolygon(query, (Polygon) mPolygon);
                        }
                    }).start();

                    if (isFileExist(StaticVariableEnum.PGDBROOTPATH) & MapQuery) {
                        //FeatureLayer featureLayer=(FeatureLayer) mMapView.getMap().getOperationalLayers().get(10);
                        DrawType = DisplayEnum.DRAW_NONE;
                        setTitle("土地规划地类查询");
                        Toast.makeText(MainActivity.this, "土地规划地类查询", Toast.LENGTH_LONG).show();
                        queryTaskForPolygon(query, (Polygon) mPolygon);
                    } else
                        Toast.makeText(MainActivity.this, R.string.QueryError_2, Toast.LENGTH_SHORT).show();
                    if (!inMap) mCallout.dismiss();
                    QueryProcessType = DisplayEnum.FINISHQUERY;
                    removeQueryWidgetFinish();
                    change.setVisibility(View.VISIBLE);
                        /*RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) finish.getLayoutParams();
                        lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
                        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                        finish.setLayoutParams(lp);*/
                } else if (QueryProcessType == DisplayEnum.FINISHQUERY) {
                    setTitle(R.string.app_name);
                    QueryProcessType = DisplayEnum.NOQUERY;
                    removeQueryWidgetFinishLater();
                    showStandardWidget();
                    DrawType = DisplayEnum.DRAW_NONE;
                    mapQueryBtEvent();
                    removePopWindowForListShow();
                }
            }
        });
    }

    private void removeQueryWidgetFinishLater(){
        FloatingActionButton finish = (FloatingActionButton) findViewById(R.id.FinishQuery);
        finish.setVisibility(View.GONE);
        FloatingActionButton change = (FloatingActionButton) findViewById(R.id.ChangeQuery);
        change.setVisibility(View.GONE);
    }

    private void removeQueryWidgetFinish(){
        FloatingActionButton cancel = (FloatingActionButton) findViewById(R.id.CancelQuery);
        cancel.setVisibility(View.GONE);
        FloatingActionButton clear = (FloatingActionButton) findViewById(R.id.ClearQuery);
        clear.setVisibility(View.GONE);
        FloatingActionButton back = (FloatingActionButton) findViewById(R.id.BackQuery);
        back.setVisibility(View.GONE);
    }

    private void removeQueryWidget(){
        FloatingActionButton cancel = (FloatingActionButton) findViewById(R.id.CancelQuery);
        cancel.setVisibility(View.GONE);
        FloatingActionButton clear = (FloatingActionButton) findViewById(R.id.ClearQuery);
        clear.setVisibility(View.GONE);
        FloatingActionButton back = (FloatingActionButton) findViewById(R.id.BackQuery);
        back.setVisibility(View.GONE);
        FloatingActionButton finish = (FloatingActionButton) findViewById(R.id.FinishQuery);
        finish.setVisibility(View.GONE);
    }

    private boolean showMTuban;

    private boolean hasMyTuban(){
        return LitePal.findAll(my_tb.class).size() >= 1;
    }

    private void updateMyTuban(){
        SimpleLineSymbol lineSymbol = null;
        for (int i = 0; i < currentMyTuban.size(); ++i){
            Boolean ChoosedMyTuban = false;
            for (int j = 0; j < ChoosedMyTubans.size(); j++) {
                if(currentMyTuban.get(i).getName().equals(ChoosedMyTubans.get(j))){
                    ChoosedMyTuban = true;
                    break;
                }
            }

            if (ChoosedMyTuban){
                lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.rgb(0, 255, 255), 3);
                Polygon polygon = new Polygon(currentMyTuban.get(i).getPoints());
                Graphic g = new Graphic(polygon, lineSymbol);
                graphics.add(g);
            }
            else {
                lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.RED, 3);
                Polygon polygon = new Polygon(currentMyTuban.get(i).getPoints());
                Graphic g = new Graphic(polygon, lineSymbol);
                graphics.add(g);
            }
        }
    }

    private void removeMyTuban(){
        SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.RED, 3);
        for (int i = 0; i < currentMyTuban.size(); ++i){
            Polygon polygon = new Polygon(currentMyTuban.get(i).getPoints());
            Graphic g = new Graphic(polygon, lineSymbol);
            graphics.remove(g);
        }
    }

    List<my_tb> currentMyTuban = new ArrayList<>();

    private void parseAndUpdateMyTuban(){
        queryMyTuban();
        updateMyTuban();
    }

    private my_tb queriedMyTuban;

    private void queryMyTuban(){
        //LitePal.deleteAll(my_tb.class);
        List<my_tb> myTbs = LitePal.findAll(my_tb.class);
        currentMyTuban = myTbs;
        for (int i = 0; i < myTbs.size(); ++i) {
            String mpointCollection = myTbs.get(i).getPointCollection();
            PointCollection pointCollection = new PointCollection(SpatialReference.create(4523));
            Log.w(TAG, "queryMyTuban: " + mpointCollection);
            String[] mPoint = mpointCollection.split(";");
            for (int j = 0; j < mPoint.length; ++j) {
                String[] mPoint1 = mPoint[j].split(",");
                try {
                    pointCollection.add(new Point(Double.valueOf(mPoint1[0]), Double.valueOf(mPoint1[1])));
                }
                catch (Exception e){
                    LitePal.deleteAll(my_tb.class, "name = ?", myTbs.get(i).getName());
                    myTbs.remove(i--);
                    currentMyTuban = myTbs;
                }
            }
            //pointCollectionList.add(pointCollection);
            currentMyTuban.get(i).setPoints(pointCollection);
        }
    }

    private void analyseFunction(){
        RunningFunction = DisplayEnum.FUNC_ANA;
        if (!MapQuery)
            mapQueryBtEvent();
        DrawType = DisplayEnum.DRAW_POLYGON;
        pointCollection = new PointCollection(SpatialReference.create(4523));
        RunningAnalyseFunction = DisplayEnum.ANA_NEED;
        showQueryWidget();
        removeStandardWidget();
        QueryProcessType = DisplayEnum.INQUERY;
    }

    private void analyseFunction(PointCollection pc){
        removeGraphicsOverlayers();
        RunningFunction = DisplayEnum.FUNC_ANA;
        if (!MapQuery)
            mapQueryBtEvent();
        DrawType = DisplayEnum.DRAW_POLYGON;
        pointCollection = pc;
        drawGeometry(new Polygon(pointCollection, SpatialReference.create(4523)));
        RunningAnalyseFunction = DisplayEnum.ANA_NEED;
        showQueryWidget();
        removeStandardWidget();
        QueryProcessType = DisplayEnum.INQUERY;
    }

    private void analyseFunctionForBasePolygon(Polygon polygon){
        removeGraphicsOverlayers();
        RunningFunction = DisplayEnum.FUNC_ANA;
        if (!MapQuery)
            mapQueryBtEvent();
        DrawType = DisplayEnum.DRAW_POLYGON;
        PartCollection parts = new PartCollection(polygon.getParts());
        pointCollection = new PointCollection(parts.getPartsAsPoints());
        drawGeometry(polygon);
        RunningAnalyseFunction = DisplayEnum.ANA_NEED;
        showQueryWidget();
        removeStandardWidget();
        QueryProcessType = DisplayEnum.INQUERY;
    }


    private void analyseFunction(Polygon polygon){
        removeGraphicsOverlayers();
        RunningFunction = DisplayEnum.FUNC_ANA;
        if (!MapQuery)
            mapQueryBtEvent();
        DrawType = DisplayEnum.DRAW_POLYGON;
        PartCollection parts = new PartCollection(polygon.getParts(), SpatialReference.create(4523));
        pointCollection = new PointCollection(parts.getPartsAsPoints());
        drawGeometry(polygon);
        RunningAnalyseFunction = DisplayEnum.ANA_NEED;
        showQueryWidget();
        removeStandardWidget();
        QueryProcessType = DisplayEnum.INQUERY;
    }

    private void drawGeometry(Geometry geometry){
        GraphicsOverlay graphicsOverlay_1 = new GraphicsOverlay();
        SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.GREEN, 3);
        Graphic fillGraphic;
        fillGraphic = new Graphic(geometry, lineSymbol);
        graphicsOverlay_1.getGraphics().add(fillGraphic);
        mMapView.getGraphicsOverlays().add(graphicsOverlay_1);
    }

    private void ChooseUserTuban(Point pt){
        for (int i = 0; i < currentMyTuban.size(); ++i){
            Polygon polygon = new Polygon(currentMyTuban.get(i).getPoints());
            if (GeometryEngine.within(pt, polygon)){
                Log.w(TAG, "ChooseUserTuban: " + currentMyTuban.get(i).getName());
                AddMyTubansByName(currentMyTuban.get(i).getName());
                drawGraphicsOverlayer();
            }
        }
    }

    private Boolean IsDrawedTuban = false;
    private boolean inUserDrawedTuban(Point pt){
        for (int i = 0; i < currentMyTuban.size(); ++i){
            Polygon polygon = new Polygon(currentMyTuban.get(i).getPoints());
            if (GeometryEngine.within(pt, polygon)){
                queriedMyTuban = currentMyTuban.get(i);
                analyseFunction(queriedMyTuban.getPoints());
                IsDrawedTuban = true;
                return true;
            }
        }
        return false;
    }

    public void drawGraphicsOverlayer(){
        initialiseGraphics();
        if (ShowPoi)
            updatePoi();
        if (ShowWhiteBlank)
            updateWhiteBlank();
        if (ShowMyTuban)
            parseAndUpdateMyTuban();
        if(ShowTrail)
            ParseAndUpdateTrails();
        updateGraphicsOverlayer();
    }

    public void initialiseGraphics(){
        for (int i = 0; i < graphics.size(); ++i){
            graphicsOverlay_10.getGraphics().remove(graphics.get(i));
        }
        graphics.clear();
    }

    private void updatePoi(){
        List<POI> pois = LitePal.findAll(POI.class);
        int psize = pois.size();
        if (psize > 0) {
            updatepoi(pois, psize);
            updateChoosedPoi();
        }
    }

    private void updateChoosedPoi(){
        for (int i = 0; i < ChoosedPois.size(); i++) {
            String poic = ChoosedPois.get(i);
            List<POI> pois = LitePal.findAll(POI.class);
            for (int j = 0; j < pois.size(); j++) {
                if (poic.equals(pois.get(j).getPoic())){
                    SimpleMarkerSymbol makerSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.rgb(0, 255, 255), 20);
                    Point wgs84Point = (Point) GeometryEngine.project(new Point(Double.valueOf(pois.get(j).getY()), Double.valueOf(pois.get(j).getX()), SpatialReferences.getWgs84()), SpatialReference.create(4523));
                    Log.w(TAG, "updateChoosedPoi, POI: " + pois.get(j).getY() + ", " + pois.get(j).getX());
                    Graphic g = new Graphic(wgs84Point, makerSymbol);
                    graphics.add(g);
                    break;
                }
            }
        }
    }

    private void updateWhiteBlank(){
        List<whiteblank> whiteblanks = LitePal.findAll(whiteblank.class);
        int wbsize = whiteblanks.size();
        if (wbsize > 0) {
            updateWhiteBlank(whiteblanks, wbsize);
            updateChoosedWhiteblank();
        }
    }

    private void updateChoosedWhiteblank(){
        List<whiteblank> whiteblanks = LitePal.findAll(whiteblank.class);
        for (int i = 0; i < ChoosedWhiteblankLines.size(); i++) {
            PointCollection points = new PointCollection(SpatialReference.create(4523));;
            for (int j = 0; j < whiteblanks.size(); j++) {
                if (ChoosedWhiteblankLines.get(i).equals(whiteblanks.get(j).getObjectID()))
                {
                    String[] strings = whiteblanks.get(j).getPts().split("lzy");
                    Log.w(TAG, "drawWhiteBlank1: " + strings.length);
                    for (int kk = 0; kk < strings.length; ++kk) {
                        String[] strings1 = strings[kk].split(",");
                        if (strings1.length == 2) {
                            Log.w(TAG, "drawWhiteBlank2: " + strings1[0] + "; " + strings1[1]);
                            Point wgs84Point = (Point) GeometryEngine.project(new Point(Double.valueOf(strings1[0]), Double.valueOf(strings1[1])), SpatialReference.create(4523));
                            points.add(wgs84Point);
                        }
                    }
                    SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.rgb(0, 255, 255), 3);
                    Polyline polyline = new Polyline(points);
                    Graphic g = new Graphic(polyline, lineSymbol);
                    graphics.add(g);
                    break;
                }
            }
        }
    }

    /*public void updatePoiAndWhiteBlank(){
        List<whiteblank> whiteblanks = LitePal.findAll(whiteblank.class);
        List<POI> pois = LitePal.findAll(POI.class);
        int wbsize = whiteblanks.size();
        int psize = pois.size();
        int size = wbsize + psize;

        if (size > 0){
            if (wbsize > 0) updateWhiteBlank(whiteblanks, wbsize);
            if (psize > 0) updatepoi(pois, psize);
        }
    }*/

    public void updateGraphicsOverlayer(){
        graphicsOverlay_10 = new GraphicsOverlay(GraphicsOverlay.RenderingMode.DYNAMIC);
        if (graphics.size() > 0) {
            for (int j = 0; j < graphics.size(); ++j){
                try {
                    graphicsOverlay_10.getGraphics().add(graphics.get(j));
                }catch (ArcGISRuntimeException e){
                    Log.w(TAG, "drawGraphicsOverlayer: " + e.toString());
                }
            }
        }
        if (mMapView.getGraphicsOverlays().size() != 0) mMapView.getGraphicsOverlays().remove(graphicsOverlay_10);
        mMapView.getGraphicsOverlays().add(graphicsOverlay_10);
    }

    public void updateWhiteBlank(List<whiteblank> whiteblanks, int size){
        for (int i = 0; i < size; ++i){
            points.clear();
            //geometry_WhiteBlank geometryWhiteBlank = new geometry_WhiteBlank(whiteblanks.get(i).getLineSymbol(), whiteblanks.get(i).getPolyline());
            String[] strings = whiteblanks.get(i).getPts().split("lzy");
            Log.w(TAG, "drawWhiteBlank1: " + strings.length);
            for (int kk = 0; kk < strings.length; ++kk){
                String[] strings1 = strings[kk].split(",");
                if (strings1.length == 2) {
                    Log.w(TAG, "drawWhiteBlank2: " + strings1[0] + "; " + strings1[1]);
                    Point wgs84Point = (Point) GeometryEngine.project(new Point(Double.valueOf(strings1[0]), Double.valueOf(strings1[1])), SpatialReference.create(4523));
                    points.add(wgs84Point);
                }
            }
            SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, whiteblanks.get(i).getColor(), 3);
            Polyline polyline = new Polyline(points);
            Graphic g = new Graphic(polyline, lineSymbol);
            graphics.add(g);
        }
    }

    public void drawWhiteBlank(){
        graphics.clear();
        List<whiteblank> whiteblanks = LitePal.findAll(whiteblank.class);
        int size = whiteblanks.size();
        if (size == 0) graphicsOverlay_10 = new GraphicsOverlay(GraphicsOverlay.RenderingMode.DYNAMIC);
        else {
            graphicsOverlay_10 = new GraphicsOverlay(GraphicsOverlay.RenderingMode.DYNAMIC);
            for (int i = 0; i < size; ++i){
                points.clear();
                //geometry_WhiteBlank geometryWhiteBlank = new geometry_WhiteBlank(whiteblanks.get(i).getLineSymbol(), whiteblanks.get(i).getPolyline());
                String[] strings = whiteblanks.get(i).getPts().split("lzy");
                Log.w(TAG, "drawWhiteBlank1: " + strings.length);
                for (int kk = 0; kk < strings.length; ++kk){
                    String[] strings1 = strings[kk].split(",");
                    if (strings1.length == 2) {
                        Log.w(TAG, "drawWhiteBlank2: " + strings1.length);
                        Point wgs84Point = (Point) GeometryEngine.project(new Point(Double.valueOf(strings1[0]), Double.valueOf(strings1[1])), SpatialReference.create(4523));
                        points.add(wgs84Point);
                    }
                }
                SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, whiteblanks.get(i).getColor(), 3);
                Polyline polyline = new Polyline(points);
                Graphic g = new Graphic(polyline, lineSymbol);
                graphics.add(g);
            }
            if (graphics.size() > 0) {
                for (int j = 0; j < graphics.size(); ++j){
                    try {
                        graphicsOverlay_10.getGraphics().add(graphics.get(j));
                    }catch (ArcGISRuntimeException e){
                        Log.w(TAG, "drawWhiteBlank: " + e.toString());
                    }
                }
            }
            if (mMapView.getGraphicsOverlays().size() != 0) mMapView.getGraphicsOverlays().remove(graphicsOverlay_10);
            mMapView.getGraphicsOverlays().add(graphicsOverlay_10);
        }
    }

    /*public void drawpoi(){
        List<POI> pois = LitePal.findAll(POI.class);
        int size = pois.size();
        if (size == 0) graphicsOverlay_10 = new GraphicsOverlay(GraphicsOverlay.RenderingMode.DYNAMIC);
        else {
            graphicsOverlay_10 = new GraphicsOverlay(GraphicsOverlay.RenderingMode.DYNAMIC);
            for (int i = 0; i < size; ++i){

                SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, pois.get(i).getColor(), 3);
                Polyline polyline = new Polyline(points);
                Graphic g = new Graphic(polyline, lineSymbol);
                graphics.add(g);
            }

            if (graphics.size() > 0) {
                for (int j = 0; j < graphics.size(); ++j){
                    try {
                        graphicsOverlay_10.getGraphics().add(graphics.get(j));
                    }catch (ArcGISRuntimeException e){
                        Log.w(TAG, "drawpoi: " + e.toString());
                    }
                }
            }
            if (mMapView.getGraphicsOverlays().size() != 0) mMapView.getGraphicsOverlays().remove(graphicsOverlay_10);
            mMapView.getGraphicsOverlays().add(graphicsOverlay_10);
        }
    }*/

    public void updatepoi(List<POI> pois, int size){
        Log.w(TAG, "drawpoi: " + size);
        for (int i = 0; i < size; ++i){
            Boolean hasSamePoi = false;
            for (int j = 0; j < ChoosedPois.size(); j++) {
                if (ChoosedPois.get(j).equals(pois.get(i).getPoic())){
                    hasSamePoi = true;
                    break;
                }
            }
            if (!hasSamePoi) {
                SimpleMarkerSymbol makerSymbol;
                int tapenum = pois.get(i).getTapenum();
                int photonum = pois.get(i).getPhotonum();
                if (tapenum == 0 && photonum == 0) {
                    makerSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.RED, 20);
                } else if (tapenum > 0 && photonum > 0) {
                    makerSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.GREEN, 20);
                } else {
                    makerSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.YELLOW, 20);
                }
                Log.w(TAG, "drawWhiteBlank2: " + pois.get(i).getX() + "; " + pois.get(i).getY());
                Point wgs84Point = (Point) GeometryEngine.project(new Point(Double.valueOf(pois.get(i).getY()), Double.valueOf(pois.get(i).getX()), SpatialReferences.getWgs84()), SpatialReference.create(4523));
                Log.w(TAG, "updateChoosedPoi, POI: " + pois.get(i).getY() + ", " + pois.get(i).getX());
                Log.w(TAG, "drawWhiteBlank2: " + wgs84Point.getX() + "; " + wgs84Point.getY());
                Graphic g = new Graphic(wgs84Point, makerSymbol);
                graphics.add(g);
            }
        }
    }

    public void searchForState(final String searchString) {

        // clear any previous selections

        DMPointFeatureLayer.clearSelection();

        // create objects required to do a selection with a query
        QueryParameters query = new QueryParameters();
        //make search case insensitive
        query.setWhereClause("upper(图上名称) LIKE '%" + searchString.toUpperCase() + "%'");
        Log.w(TAG, "searchForState: " );
        // call select features
        if (mMapView.getMap().getOperationalLayers().size() != 0) {

            //Log.w(TAG, "searchForState: getAttribution" + mMapView.getMap().getOperationalLayers().get(3).getAttribution());
            //Log.w(TAG, "searchForState: getDescription" + mMapView.getMap().getOperationalLayers().get(3).getDescription());
            //DMPointFeatureLayer = (FeatureLayer) mMapView.getMap().getOperationalLayers().get(11);
            try {
                FeatureTable mTable = DMPointFeatureLayer.getFeatureTable();//得到查询属性表
                //Log.w(TAG, "searchForState: " + mTable.getFields().get(0) );
                final ListenableFuture<FeatureQueryResult> featureQueryResult
                        = mTable.queryFeaturesAsync(query);
                featureQueryResult.addDoneListener(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            while (mMapView.getGraphicsOverlays().size() != 0){
                                for (int i = 0; i < mMapView.getGraphicsOverlays().size(); ++i){
                                    mMapView.getGraphicsOverlays().remove(i);
                                }
                            }
                            // call get on the future to get the result
                            FeatureQueryResult result = featureQueryResult.get();
                            // check there are some results
                            if (result.iterator().hasNext()) {
                            /*Iterator<Feature> features = result.iterator();
                            features.*/
                                // get the extend of the first feature in the result to zoom to
                                Feature feature = result.iterator().next();
                                Envelope envelope = feature.getGeometry().getExtent();
                                mMapView.setViewpointGeometryAsync(envelope, 200);

                                //Select the feature
                                DMPointFeatureLayer.selectFeature(feature);
                                Log.w(TAG, "run: " + mMapView.getMapScale());
                                mMapView.setViewpointScaleAsync(3000);

                            } else {
                                Toast.makeText(MainActivity.this, "No states found with name: " + searchString, Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(MainActivity.this, "Feature search failed for: " + searchString + ". Error=" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.e(getResources().getString(R.string.app_name), "Feature search failed for: " + searchString + ". Error=" + e.getMessage());
                        }
                    }
                });
            }catch (ArcGISRuntimeException e){
                Toast.makeText(MainActivity.this, e.getMessage() + e.getErrorCode(), Toast.LENGTH_SHORT).show();
            }
        }
        // add done loading listener to fire when the selection returns
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // TODO 暂时删除
        switch (isQurey){
            case I_QUREY:
                pieChartView.setVisibility(View.GONE);
                menu.findItem(R.id.search).setVisible(false);
                menu.findItem(R.id.cancel).setVisible(true);
                // Get the SearchView and set the searchable configuration
                SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
                final SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
                // Assumes current activity is the searchable activity
                searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
                searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
                searchView.setSubmitButtonEnabled(true);
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        //searchForState(query);
                        pieChartView.setVisibility(View.GONE);
                        mCallout.dismiss();
                        //if (isFileExist(StaticVariableEnum.GDBROOTPATH))
                        {
                            //showListPopupWindow(searchView, query);
                            //showListPopupWindowforListView(searchView, query);

                            queryInfos.clear();
                            /*showListPopupWindowforListViewForXZ(searchView, query);
                            showListPopupWindowforListViewForCXZ(searchView, query);*/
                            double[] coordinate = GetCoordinate(query);
                            if (coordinate == null)
                            {
                                if (query.equals("lzyswds"))
                                {
                                    LitePal.deleteAll(memoryxzqinfo.class);
                                    readOtherData();
                                    Toast.makeText(MainActivity.this, "预设数据读取完毕", Toast.LENGTH_LONG).show();
                                }
                                else
                                    showListPopupWindowforListViewFor20200903(searchView, query);
                            }
                            else{
                                if (coordinate[0]>180)
                                    ResetMapViewForNow(new Point(coordinate[0], coordinate[1], 0, SpatialReference.create(4523)), 2000, 0);
                                else
                                    ResetMapViewForNow(new Point(coordinate[0], coordinate[1], 0, SpatialReference.create(4326)), 2000, 0);
                            }

                        }
                        //else Toast.makeText(MainActivity.this, R.string.QueryError_1, Toast.LENGTH_SHORT).show();


                        /* // 原版本

                        File file1 = new File(Environment.getExternalStorageDirectory().toString() + "/临沧市行政区.txt");
                        if (query.equals("kqlcsxzq") && file1.exists()){
                            //行政区数据入库
                            LitePal.deleteAll(xzq.class);
                            readXZQ();
                        }else
                            Toast.makeText(MainActivity.this, "缺少必要文件，无法打开行政区查询", Toast.LENGTH_LONG).show();*/
                        return true;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        return true;
                    }
                });
                //menu.findItem(R.id.action_search).setVisible(true);
                break;
            case I_NOQUREY:
                pieChartView.setVisibility(View.GONE);
                // TODO 2020/9/3 界面顶部MENU更新
                //老版本
                menu.findItem(R.id.search).setVisible(true);
                menu.findItem(R.id.cancel).setVisible(false);
                menu.findItem(R.id.action_search).setVisible(false);
                /*
                //新版本
                menu.findItem(R.id.search).setVisible(false);
                menu.findItem(R.id.cancel).setVisible(false);
                menu.findItem(R.id.action_search).setVisible(false);*/
                break;
        }
        return super.onPrepareOptionsMenu(menu);
        //return false;
    }

    public void showListPopupWindow(View view, String searchString) {
        queryInfos.clear();
        final ListPopupWindow listPopupWindow = new ListPopupWindow(this);
        searchString = searchString.trim();
        DMPointFeatureLayer.clearSelection();

        // create objects required to do a selection with a query
        QueryParameters query = new QueryParameters();
        //make search case insensitive
        query.setWhereClause("upper(图上名称) LIKE '%" + searchString.toUpperCase() + "%'");
        Log.w(TAG, "searchForState: " );
        // call select features
        if (mMapView.getMap().getOperationalLayers().size() != 0) {

            //Log.w(TAG, "searchForState: getAttribution" + mMapView.getMap().getOperationalLayers().get(3).getAttribution());
            //Log.w(TAG, "searchForState: getDescription" + mMapView.getMap().getOperationalLayers().get(3).getDescription());
            //DMPointFeatureLayer = (FeatureLayer) mMapView.getMap().getOperationalLayers().get(11);
            try {
                final String string = searchString;
                FeatureTable mTable = DMPointFeatureLayer.getFeatureTable();//得到查询属性表
                //Log.w(TAG, "searchForState: " + mTable.getFields().get(0) );
                final ListenableFuture<FeatureQueryResult> featureQueryResult
                        = mTable.queryFeaturesAsync(query);
                featureQueryResult.addDoneListener(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            /*while (mMapView.getGraphicsOverlays().size() != 0){
                                for (int i = 0; i < mMapView.getGraphicsOverlays().size(); ++i){
                                    mMapView.getGraphicsOverlays().remove(i);
                                }
                            }
                            // call get on the future to get the result
                            FeatureQueryResult result = featureQueryResult.get();
                            // check there are some results
                            int num = -1;
                            if (result.iterator().hasNext() ) {
                                num++;
                                //Log.w(TAG, "run: " + features.next().getFeatureTable().getTableName().toString());
                                // get the extend of the first feature in the result to zoom to
                                Feature feature = result.iterator().next();
                                Log.w(TAG, "run: " + feature.getAttributes().get("图上名称"));
                                Envelope envelope = feature.getGeometry().getExtent();
                                String name = feature.getAttributes().get("图上名称").toString();
                                boolean hasSame = false;
                                for (int i = 0; i < queryInfos.size(); ++i){
                                    if (name.equals(queryInfos.get(i).getName())) hasSame = true;
                                }
                                if (!hasSame) {
                                    Log.w(TAG, "run: " + "hasSame");
                                    QueryInfo queryInfo = new QueryInfo(name, feature, envelope);
                                    queryInfos.add(queryInfo);
                                }
                            }
                            Log.w(TAG, "showListPopupWindow: " + queryInfos.size());
                            items = new String[queryInfos.size()];
                            for (int i = 0; i < queryInfos.size(); ++i){
                                items[i] = queryInfos.get(i).getName();
                            }
                            // ListView适配器
                            listPopupWindow.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, items));*/

                            FeatureCollectionTable featureCollectionTable = new FeatureCollectionTable(featureQueryResult.get());
                            Iterator<Feature> featureIterator = featureCollectionTable.iterator();
                            while (featureIterator.hasNext()) {
                                //Log.w(TAG, "run: " + featureIterator.next().getAttributes().get("图上名称"));
                                Feature feature = featureIterator.next();
                                Log.w(TAG, "run: " + feature.getAttributes().get("图上名称"));
                                Envelope envelope = feature.getGeometry().getExtent();
                                String name = feature.getAttributes().get("图上名称").toString();
                                boolean hasSame = false;
                                for (int i = 0; i < queryInfos.size(); ++i){
                                    if (name.equals(queryInfos.get(i).getName())) hasSame = true;
                                }
                                if (!hasSame) {
                                    Log.w(TAG, "run: " + "hasSame");
                                    QueryInfo queryInfo = new QueryInfo(name, feature, envelope);
                                    queryInfos.add(queryInfo);
                                }
                            }
                            Log.w(TAG, "showListPopupWindow: " + queryInfos.size());
                            items = new String[queryInfos.size()];
                            for (int i = 0; i < queryInfos.size(); ++i){
                                items[i] = queryInfos.get(i).getName();
                            }
                            // ListView适配器
                            ArrayAdapter arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, items);
                            listPopupWindow.setAdapter(arrayAdapter);

                            //Log.w(TAG, "run: " + featureCollectionTable.iterator().next().getAttribut es().get("图上名称").toString());
                            //Log.w(TAG, "run: " + featureCollectionTable.iterator().next().getAttributes().get("图上名称").toString());
                            //Log.w(TAG, "run: " + featureCollectionTable.iterator().next().getAttributes().get("图上名称").toString());
                            //create a feature collection from the above feature collection table
                            /*FeatureCollection featureCollection = new FeatureCollection();
                            featureCollection.getTables().add(featureCollectionTable);
                            Log.w(TAG, "run: " + featureCollection.toString());
                            Log.w(TAG, "run: " + featureCollection.getTables().size());
                            //create a feature collection layer
                            FeatureCollectionLayer featureCollectionLayer = new FeatureCollectionLayer(featureCollection);
                            Log.w(TAG, "run: " + featureCollectionLayer.getAttribution());
                            //add the layer to the operational layers array
                            mMapView.getMap().getOperationalLayers().add(featureCollectionLayer);*/
                        } catch (Exception e) {
                            Toast.makeText(MainActivity.this, "Feature search failed for: " + string + ". Error=" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.e(getResources().getString(R.string.app_name), "Feature search failed for: " + string + ". Error=" + e.getMessage());
                        }
                    }
                });
            }catch (ArcGISRuntimeException e){
                Toast.makeText(MainActivity.this, e.getMessage() + e.getErrorCode(), Toast.LENGTH_SHORT).show();
            }
        }
        //final List<QueryInfo> pois = new ArrayList<>();




        // ListView适配器
        //listPopupWindow.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, items));

        // 选择item的监听事件
        listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mMapView.setViewpointGeometryAsync(queryInfos.get(position).getEnvelope(), 200);

                //Select the feature
                DMPointFeatureLayer.selectFeature(queryInfos.get(position).getFeature());
                Log.w(TAG, "run: " + mMapView.getMapScale());
                mMapView.setViewpointScaleAsync(3000);
                listPopupWindow.dismiss();
            }
        });

        // 对话框的宽高
        listPopupWindow.setWidth(600);
        listPopupWindow.setHeight(600);

        // ListPopupWindow的锚,弹出框的位置是相对当前View的位置
        listPopupWindow.setAnchorView(view);

        // ListPopupWindow 距锚view的距离
        listPopupWindow.setHorizontalOffset(50);
        listPopupWindow.setVerticalOffset(100);

        listPopupWindow.setModal(false);

        listPopupWindow.show();
    }

    private void ClearBasedFeatureLayerSelection(){
        for (int i = 0; i < BaseLayerFieldsSheetList.size(); i++) {
            FeatureLayer mfl = BaseLayerFieldsSheetList.get(i).getFeatureLayer();
            mfl.clearSelection();
        }
    }



    private FeatureLayer AttributeQueryFeatureLayer = null;
    public void showListPopupWindowforListView(View view, String searchString) {
        final View popView = View.inflate(this, R.layout.popupwindow_listview,null);
        searchString = searchString.trim();
        //原版本
        DMPointFeatureLayer.clearSelection();

        //ClearBasedFeatureLayerSelection();

        popView.setX(view.getX());
        popView.setY(view.getY() + view.getHeight());
        final PopupWindow popupWindow = new PopupWindow(popView, view.getWidth(), 800);

        //popupWindow.setAnimationStyle(R.style.anim_popup_dir);
        popupWindow.setFocusable(true);
        //点击外部popueWindow消失
        popupWindow.setOutsideTouchable(true);
        // create objects required to do a selection with a query
        QueryParameters query = new QueryParameters();
        //make search case insensitive
        query.setWhereClause("upper(图上名称) LIKE '%" + searchString.toUpperCase() + "%'");
        Log.w(TAG, "searchForState: " );
        // call select features
        if (mMapView.getMap().getOperationalLayers().size() != 0) {

            //Log.w(TAG, "searchForState: getAttribution" + mMapView.getMap().getOperationalLayers().get(3).getAttribution());
            //Log.w(TAG, "searchForState: getDescription" + mMapView.getMap().getOperationalLayers().get(3).getDescription());
            //DMPointFeatureLayer = (FeatureLayer) mMapView.getMap().getOperationalLayers().get(11);
            try {
                final String string = searchString;
                /*AttributeQueryFeatureLayer = null;
                //FeatureLayer fl;
                for (int i = 0; i < BaseLayerFieldsSheetList.size(); i++) {
                    FeatureLayer mfl = BaseLayerFieldsSheetList.get(i).getFeatureLayer();
                    if (mfl.getName().equals("XZQ"))
                    {
                        AttributeQueryFeatureLayer = mfl;
                        break;
                    }
                }*/
                //if (AttributeQueryFeatureLayer != null) {

                    //原版
                    FeatureTable mTable = DMPointFeatureLayer.getFeatureTable();//得到查询属性表
                    //FeatureTable mTable = AttributeQueryFeatureLayer.getFeatureTable();
                    //Log.w(TAG, "searchForState: " + mTable.getFields().get(0) );
                    final ListenableFuture<FeatureQueryResult> featureQueryResult
                            = mTable.queryFeaturesAsync(query);
                    featureQueryResult.addDoneListener(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                FeatureCollectionTable featureCollectionTable = new FeatureCollectionTable(featureQueryResult.get());
                                Iterator<Feature> featureIterator = featureCollectionTable.iterator();
                                while (featureIterator.hasNext()) {
                                    Feature feature = featureIterator.next();
                                    //Log.w(TAG, "run: " + feature.getAttributes().get("图上名称"));
                                    Envelope envelope = feature.getGeometry().getExtent();
                                    //String name = feature.getAttributes().get("图上名称").toString();
                                    String name = feature.getAttributes().get("图上名称").toString();
                                    boolean hasSame = false;
                                    for (int i = 0; i < queryInfos.size(); ++i) {
                                        if (name.equals(queryInfos.get(i).getName()))
                                            hasSame = true;
                                    }
                                    if (!hasSame) {
                                        Log.w(TAG, "run: " + "hasSame");
                                        QueryInfo queryInfo = new QueryInfo(name, feature, envelope);
                                        queryInfos.add(queryInfo);
                                    }
                                }
                                Log.w(TAG, "showListPopupWindow: " + queryInfos.size());
                                items = new String[queryInfos.size()];
                                for (int i = 0; i < queryInfos.size(); ++i) {
                                    items[i] = queryInfos.get(i).getName();
                                }

                                RecyclerView recyclerView1 = (RecyclerView) popView.findViewById(R.id.listview_recycler_view);
                                //GridLayoutManager layoutManager1 = new GridLayoutManager(popView.getContext(),1);
                                LinearLayoutManager layoutManager1 = new LinearLayoutManager(popView.getContext());
                                recyclerView1.setLayoutManager(layoutManager1);
                                listviewAdapter adapter1 = new listviewAdapter(queryInfos);
                                adapter1.setOnItemClickListener(new listviewAdapter.OnRecyclerItemClickListener() {
                                    @Override
                                    public void onItemClick(View view, String xzqdm, int position) {
                                        //mMapView.setViewpointGeometryAsync(queryInfos.get(position).getEnvelope(), 200);
                                        mMapView.setViewpointGeometryAsync(queryInfos.get(position).getEnvelope());

                                        //Select the feature
                                        DMPointFeatureLayer.selectFeature(queryInfos.get(position).getFeature());
                                        //AttributeQueryFeatureLayer.selectFeature(queryInfos.get(position).getFeature());
                                        Log.w(TAG, "run: " + mMapView.getMapScale());
                                        //mMapView.setViewpointScaleAsync(200000);
                                        popupWindow.dismiss();
                                    }
                                });
                                recyclerView1.setAdapter(adapter1);
                            } catch (Exception e) {
                                Toast.makeText(MainActivity.this, "Feature search failed for: " + string + ". Error=" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                Log.e(getResources().getString(R.string.app_name), "Feature search failed for: " + string + ". Error=" + e.getMessage());
                            }
                        }
                    });

            }catch (ArcGISRuntimeException e){
                Toast.makeText(MainActivity.this, e.getMessage() + e.getErrorCode(), Toast.LENGTH_SHORT).show();
            }
        }
        //final List<QueryInfo> pois = new ArrayList<>();



        /*listviewAdapter adapter1 = new listviewAdapter(queryInfos);
        adapter1.setOnItemClickListener(new listviewAdapter.OnRecyclerItemClickListener() {
            @Override
            public void onItemClick(View view, String xzqdm, int position) {
                mMapView.setViewpointGeometryAsync(queryInfos.get(position).getEnvelope(), 200);

                //Select the feature
                DMPointFeatureLayer.selectFeature(queryInfos.get(position).getFeature());
                Log.w(TAG, "run: " + mMapView.getMapScale());
                mMapView.setViewpointScaleAsync(3000);
                popupWindow.dismiss();
            }
        });
        recyclerView1.setAdapter(adapter1);*/

        // ListView适配器
        //listPopupWindow.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, items));

        // 选择item的监听事件

        // 对话框的宽高

        // ListPopupWindow的锚,弹出框的位置是相对当前View的位置

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
        popupWindow.showAtLocation(popView, Gravity.TOP, 0, 50);
    }

    public void showListPopupWindowforListViewForXZ(View view, String searchString) {
        final View popView = View.inflate(this, R.layout.popupwindow_listview,null);
        searchString = searchString.trim();
        //原版本
        //DMPointFeatureLayer.clearSelection();

        ClearBasedFeatureLayerSelection();

        popView.setX(view.getX());
        popView.setY(view.getY() + view.getHeight());
        final PopupWindow popupWindow = new PopupWindow(popView, view.getWidth(), 800);

        //popupWindow.setAnimationStyle(R.style.anim_popup_dir);
        popupWindow.setFocusable(true);
        //点击外部popueWindow消失
        popupWindow.setOutsideTouchable(true);
        // create objects required to do a selection with a query
        QueryParameters query = new QueryParameters();
        //make search case insensitive
        query.setWhereClause("upper(XZQMC) LIKE '%" + searchString.toUpperCase() + "%'");
        Log.w(TAG, "searchForState: " );
        // call select features
        if (mMapView.getMap().getOperationalLayers().size() != 0) {

            //Log.w(TAG, "searchForState: getAttribution" + mMapView.getMap().getOperationalLayers().get(3).getAttribution());
            //Log.w(TAG, "searchForState: getDescription" + mMapView.getMap().getOperationalLayers().get(3).getDescription());
            //DMPointFeatureLayer = (FeatureLayer) mMapView.getMap().getOperationalLayers().get(11);
            try {
                final String string = searchString;
                AttributeQueryFeatureLayer = null;
                //FeatureLayer fl;
                for (int i = 0; i < LayerFieldsSheetList.size(); i++) {
                    FeatureLayer mfl = LayerFieldsSheetList.get(i).getFeatureLayer();
                    if (mfl.getName().equals("XZQ"))
                    {
                        AttributeQueryFeatureLayer = mfl;
                        break;
                    }
                }
                if (AttributeQueryFeatureLayer != null) {
                    /*
                    //原版
                    FeatureTable mTable = DMPointFeatureLayer.getFeatureTable();//得到查询属性表*/
                    FeatureTable mTable = AttributeQueryFeatureLayer.getFeatureTable();
                    //Log.w(TAG, "searchForState: " + mTable.getFields().get(0) );
                    final ListenableFuture<FeatureQueryResult> featureQueryResult
                            = mTable.queryFeaturesAsync(query);
                    featureQueryResult.addDoneListener(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                FeatureCollectionTable featureCollectionTable = new FeatureCollectionTable(featureQueryResult.get());
                                Iterator<Feature> featureIterator = featureCollectionTable.iterator();
                                while (featureIterator.hasNext()) {
                                    Feature feature = featureIterator.next();
                                    //Log.w(TAG, "run: " + feature.getAttributes().get("图上名称"));
                                    Envelope envelope = feature.getGeometry().getExtent();
                                    //String name = feature.getAttributes().get("图上名称").toString();
                                    String name = feature.getAttributes().get("XZQMC").toString();
                                    boolean hasSame = false;
                                    for (int i = 0; i < queryInfos.size(); ++i) {
                                        if (name.equals(queryInfos.get(i).getName()))
                                        {
                                            hasSame = true;
                                            break;
                                        }
                                    }
                                    if (!hasSame) {
                                        Log.w(TAG, "run: " + "hasSame");
                                        QueryInfo queryInfo = new QueryInfo(name, feature, envelope);
                                        queryInfos.add(queryInfo);
                                    }
                                }
                                Log.w(TAG, "showListPopupWindow: " + queryInfos.size());
                                items = new String[queryInfos.size()];
                                for (int i = 0; i < queryInfos.size(); ++i) {
                                    items[i] = queryInfos.get(i).getName();
                                }

                                RecyclerView recyclerView1 = (RecyclerView) popView.findViewById(R.id.listview_recycler_view);
                                //GridLayoutManager layoutManager1 = new GridLayoutManager(popView.getContext(),1);
                                LinearLayoutManager layoutManager1 = new LinearLayoutManager(popView.getContext());
                                recyclerView1.setLayoutManager(layoutManager1);
                                listviewAdapter adapter1 = new listviewAdapter(queryInfos);
                                adapter1.setOnItemClickListener(new listviewAdapter.OnRecyclerItemClickListener() {
                                    @Override
                                    public void onItemClick(View view, String xzqdm, int position) {
                                        //mMapView.setViewpointGeometryAsync(queryInfos.get(position).getEnvelope(), 200);
                                        mMapView.setViewpointGeometryAsync(queryInfos.get(position).getEnvelope());

                                        //Select the feature
                                        //DMPointFeatureLayer.selectFeature(queryInfos.get(position).getFeature());
                                        for (int i = 0; i < LayerFieldsSheetList.size(); i++) {
                                            FeatureLayer mfl = LayerFieldsSheetList.get(i).getFeatureLayer();
                                            if (mfl.getName().equals("XZQ"))
                                            {
                                                AttributeQueryFeatureLayer = mfl;
                                                break;
                                            }
                                        }
                                        AttributeQueryFeatureLayer.selectFeature(queryInfos.get(position).getFeature());
                                        Log.w(TAG, "run: " + mMapView.getMapScale());
                                        //mMapView.setViewpointScaleAsync(200000);
                                        popupWindow.dismiss();
                                    }
                                });
                                recyclerView1.setAdapter(adapter1);
                            } catch (Exception e) {
                                Toast.makeText(MainActivity.this, "Feature search failed for: " + string + ". Error=" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                Log.e(getResources().getString(R.string.app_name), "Feature search failed for: " + string + ". Error=" + e.getMessage());
                            }
                        }
                    });
                }
            }catch (ArcGISRuntimeException e){
                Toast.makeText(MainActivity.this, e.getMessage() + e.getErrorCode(), Toast.LENGTH_SHORT).show();
            }
        }
        //final List<QueryInfo> pois = new ArrayList<>();



        /*listviewAdapter adapter1 = new listviewAdapter(queryInfos);
        adapter1.setOnItemClickListener(new listviewAdapter.OnRecyclerItemClickListener() {
            @Override
            public void onItemClick(View view, String xzqdm, int position) {
                mMapView.setViewpointGeometryAsync(queryInfos.get(position).getEnvelope(), 200);

                //Select the feature
                DMPointFeatureLayer.selectFeature(queryInfos.get(position).getFeature());
                Log.w(TAG, "run: " + mMapView.getMapScale());
                mMapView.setViewpointScaleAsync(3000);
                popupWindow.dismiss();
            }
        });
        recyclerView1.setAdapter(adapter1);*/

        // ListView适配器
        //listPopupWindow.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, items));

        // 选择item的监听事件

        // 对话框的宽高

        // ListPopupWindow的锚,弹出框的位置是相对当前View的位置

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
        popupWindow.showAtLocation(popView, Gravity.TOP, 0, 50);
    }

    public void showListPopupWindowforListViewForCXZ(View view, String searchString) {
        final View popView = View.inflate(this, R.layout.popupwindow_listview,null);
        searchString = searchString.trim();
        //原版本
        //DMPointFeatureLayer.clearSelection();

        ClearBasedFeatureLayerSelection();

        popView.setX(view.getX());
        popView.setY(view.getY() + view.getHeight());
        final PopupWindow popupWindow = new PopupWindow(popView, view.getWidth(), 800);

        //popupWindow.setAnimationStyle(R.style.anim_popup_dir);
        popupWindow.setFocusable(true);
        //点击外部popueWindow消失
        popupWindow.setOutsideTouchable(true);
        // create objects required to do a selection with a query
        QueryParameters query = new QueryParameters();
        //make search case insensitive
        query.setWhereClause("upper(ZLDWMC) LIKE '%" + searchString.toUpperCase() + "%'");
        Log.w(TAG, "searchForState: " );
        // call select features
        if (mMapView.getMap().getOperationalLayers().size() != 0) {

            //Log.w(TAG, "searchForState: getAttribution" + mMapView.getMap().getOperationalLayers().get(3).getAttribution());
            //Log.w(TAG, "searchForState: getDescription" + mMapView.getMap().getOperationalLayers().get(3).getDescription());
            //DMPointFeatureLayer = (FeatureLayer) mMapView.getMap().getOperationalLayers().get(11);
            try {
                final String string = searchString;
                AttributeQueryFeatureLayer = null;
                //FeatureLayer fl;
                for (int i = 0; i < LayerFieldsSheetList.size(); i++) {
                    FeatureLayer mfl = LayerFieldsSheetList.get(i).getFeatureLayer();
                    if (mfl.getName().equals("CJDCQ"))
                    {
                        AttributeQueryFeatureLayer = mfl;
                        break;
                    }
                }
                if (AttributeQueryFeatureLayer != null) {
                    /*
                    //原版
                    FeatureTable mTable = DMPointFeatureLayer.getFeatureTable();//得到查询属性表*/
                    FeatureTable mTable = AttributeQueryFeatureLayer.getFeatureTable();
                    //Log.w(TAG, "searchForState: " + mTable.getFields().get(0) );
                    final ListenableFuture<FeatureQueryResult> featureQueryResult
                            = mTable.queryFeaturesAsync(query);
                    featureQueryResult.addDoneListener(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                FeatureCollectionTable featureCollectionTable = new FeatureCollectionTable(featureQueryResult.get());
                                Iterator<Feature> featureIterator = featureCollectionTable.iterator();
                                while (featureIterator.hasNext()) {
                                    Feature feature = featureIterator.next();
                                    //Log.w(TAG, "run: " + feature.getAttributes().get("图上名称"));
                                    Envelope envelope = feature.getGeometry().getExtent();
                                    //String name = feature.getAttributes().get("图上名称").toString();
                                    String name = feature.getAttributes().get("ZLDWMC").toString();
                                    boolean hasSame = false;
                                    for (int i = 0; i < queryInfos.size(); ++i) {
                                        if (name.equals(queryInfos.get(i).getName()))
                                            hasSame = true;
                                    }
                                    if (!hasSame) {
                                        Log.w(TAG, "run: " + "hasSame");
                                        QueryInfo queryInfo = new QueryInfo(name, feature, envelope);
                                        queryInfos.add(queryInfo);
                                    }
                                }
                                Log.w(TAG, "showListPopupWindow: " + queryInfos.size());
                                items = new String[queryInfos.size()];
                                for (int i = 0; i < queryInfos.size(); ++i) {
                                    items[i] = queryInfos.get(i).getName();
                                }

                                RecyclerView recyclerView1 = (RecyclerView) popView.findViewById(R.id.listview_recycler_view);
                                //GridLayoutManager layoutManager1 = new GridLayoutManager(popView.getContext(),1);
                                LinearLayoutManager layoutManager1 = new LinearLayoutManager(popView.getContext());
                                recyclerView1.setLayoutManager(layoutManager1);
                                listviewAdapter adapter1 = new listviewAdapter(queryInfos);
                                adapter1.setOnItemClickListener(new listviewAdapter.OnRecyclerItemClickListener() {
                                    @Override
                                    public void onItemClick(View view, String xzqdm, int position) {
                                        //mMapView.setViewpointGeometryAsync(queryInfos.get(position).getEnvelope(), 200);
                                        mMapView.setViewpointGeometryAsync(queryInfos.get(position).getEnvelope());

                                        //Select the feature
                                        //DMPointFeatureLayer.selectFeature(queryInfos.get(position).getFeature());
                                        for (int i = 0; i < LayerFieldsSheetList.size(); i++) {
                                            FeatureLayer mfl = LayerFieldsSheetList.get(i).getFeatureLayer();
                                            if (mfl.getName().equals("CJDCQ"))
                                            {
                                                AttributeQueryFeatureLayer = mfl;
                                                break;
                                            }
                                        }
                                        AttributeQueryFeatureLayer.selectFeature(queryInfos.get(position).getFeature());
                                        Log.w(TAG, "run: " + mMapView.getMapScale());
                                        //mMapView.setViewpointScaleAsync(200000);
                                        popupWindow.dismiss();
                                    }
                                });
                                recyclerView1.setAdapter(adapter1);
                            } catch (Exception e) {
                                Toast.makeText(MainActivity.this, "Feature search failed for: " + string + ". Error=" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                Log.e(getResources().getString(R.string.app_name), "Feature search failed for: " + string + ". Error=" + e.getMessage());
                            }
                        }
                    });
                }
            }catch (ArcGISRuntimeException e){
                Toast.makeText(MainActivity.this, e.getMessage() + e.getErrorCode(), Toast.LENGTH_SHORT).show();
            }
        }
        //final List<QueryInfo> pois = new ArrayList<>();



        /*listviewAdapter adapter1 = new listviewAdapter(queryInfos);
        adapter1.setOnItemClickListener(new listviewAdapter.OnRecyclerItemClickListener() {
            @Override
            public void onItemClick(View view, String xzqdm, int position) {
                mMapView.setViewpointGeometryAsync(queryInfos.get(position).getEnvelope(), 200);

                //Select the feature
                DMPointFeatureLayer.selectFeature(queryInfos.get(position).getFeature());
                Log.w(TAG, "run: " + mMapView.getMapScale());
                mMapView.setViewpointScaleAsync(3000);
                popupWindow.dismiss();
            }
        });
        recyclerView1.setAdapter(adapter1);*/

        // ListView适配器
        //listPopupWindow.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, items));

        // 选择item的监听事件

        // 对话框的宽高

        // ListPopupWindow的锚,弹出框的位置是相对当前View的位置

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
        popupWindow.showAtLocation(popView, Gravity.TOP, 0, 50);
    }
    //TODO 简单查询
    public void showListPopupWindowforListViewFor20200903(View view, String searchString) {
        final View popView = View.inflate(this, R.layout.popupwindow_listview,null);
        searchString = searchString.trim();
        //原版本
        //DMPointFeatureLayer.clearSelection();

        ClearBasedFeatureLayerSelection();

        popView.setX(view.getX());
        popView.setY(view.getY() + view.getHeight());
        final PopupWindow popupWindow = new PopupWindow(popView, view.getWidth(), 800);

        //popupWindow.setAnimationStyle(R.style.anim_popup_dir);
        popupWindow.setFocusable(true);
        //点击外部popueWindow消失
        popupWindow.setOutsideTouchable(true);
        // create objects required to do a selection with a query

        QueryParameters query = new QueryParameters();
        //make search case insensitive
        query.setWhereClause("upper(XZQMC) LIKE '%" + searchString.toUpperCase() + "%'");
        Log.w(TAG, "searchForState: "  + searchString);
        // call select features
        if (mMapView.getMap().getOperationalLayers().size() != 0) {

            //Log.w(TAG, "searchForState: getAttribution" + mMapView.getMap().getOperationalLayers().get(3).getAttribution());
            //Log.w(TAG, "searchForState: getDescription" + mMapView.getMap().getOperationalLayers().get(3).getDescription());
            //DMPointFeatureLayer = (FeatureLayer) mMapView.getMap().getOperationalLayers().get(11);
            try {
                final String string = searchString;
                AttributeQueryFeatureLayer = null;
                //FeatureLayer fl;
                for (int i = 0; i < SimpleFeatureLayers.size(); i++) {
                    FeatureLayer mfl = SimpleFeatureLayers.get(i);
                    Log.w(TAG, "searchForState: " + mfl.getName());
                    if (mfl.getName().equals("行政区面"))
                    {
                        AttributeQueryFeatureLayer = mfl;
                        break;
                    }
                }
                if (AttributeQueryFeatureLayer != null) {
                    /*
                    //原版
                    FeatureTable mTable = DMPointFeatureLayer.getFeatureTable();//得到查询属性表*/
                    FeatureTable mTable = AttributeQueryFeatureLayer.getFeatureTable();
                    //Log.w(TAG, "searchForState: " + mTable.getFields().get(0) );
                    final ListenableFuture<FeatureQueryResult> featureQueryResult
                            = mTable.queryFeaturesAsync(query);
                    featureQueryResult.addDoneListener(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                FeatureCollectionTable featureCollectionTable = new FeatureCollectionTable(featureQueryResult.get());
                                Iterator<Feature> featureIterator = featureCollectionTable.iterator();
                                while (featureIterator.hasNext()) {
                                    Feature feature = featureIterator.next();
                                    //Log.w(TAG, "run: " + feature.getAttributes().get("图上名称"));
                                    Envelope envelope = feature.getGeometry().getExtent();
                                    //String name = feature.getAttributes().get("图上名称").toString();
                                    String name = feature.getAttributes().get("XZQMC").toString();
                                    /*boolean hasSame = false;
                                    for (int i = 0; i < queryInfos.size(); ++i) {
                                        if (name.equals(queryInfos.get(i).getName()))
                                        {
                                            hasSame = true;
                                            break;
                                        }
                                    }
                                    if (!hasSame) {
                                        Log.w(TAG, "run: " + "hasSame");
                                        QueryInfo queryInfo = new QueryInfo(name, feature, envelope);
                                        queryInfos.add(queryInfo);
                                    }*/
                                    QueryInfo queryInfo = new QueryInfo(name, feature, envelope);
                                    queryInfos.add(queryInfo);
                                }
                                Log.w(TAG, "showListPopupWindow: " + queryInfos.size());
                                items = new String[queryInfos.size()];
                                for (int i = 0; i < queryInfos.size(); ++i) {
                                    items[i] = queryInfos.get(i).getName();
                                }
                            } catch (Exception e) {
                                Toast.makeText(MainActivity.this, "Feature search failed for: " + string + ". Error=" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                Log.e(getResources().getString(R.string.app_name), "Feature search failed for: " + string + ". Error=" + e.getMessage());
                            }
                        }
                    });
                }
            }catch (ArcGISRuntimeException e){
                Toast.makeText(MainActivity.this, e.getMessage() + e.getErrorCode(), Toast.LENGTH_SHORT).show();
            }
        }

        QueryParameters query1 = new QueryParameters();
        //make search case insensitive
        query1.setWhereClause("upper(标准名称) LIKE '%" + searchString.toUpperCase() + "%'");
        Log.w(TAG, "searchForState: " );
        // call select features
        if (mMapView.getMap().getOperationalLayers().size() != 0) {

            //Log.w(TAG, "searchForState: getAttribution" + mMapView.getMap().getOperationalLayers().get(3).getAttribution());
            //Log.w(TAG, "searchForState: getDescription" + mMapView.getMap().getOperationalLayers().get(3).getDescription());
            //DMPointFeatureLayer = (FeatureLayer) mMapView.getMap().getOperationalLayers().get(11);
            try {
                final String string = searchString;
                AttributeQueryFeatureLayer = null;
                //FeatureLayer fl;
                for (int i = 0; i < SimpleFeatureLayers.size(); i++) {
                    FeatureLayer mfl = SimpleFeatureLayers.get(i);
                    if (mfl.getName().equals("地名点"))
                    {
                        AttributeQueryFeatureLayer = mfl;
                        break;
                    }
                }
                if (AttributeQueryFeatureLayer != null) {
                    /*
                    //原版
                    FeatureTable mTable = DMPointFeatureLayer.getFeatureTable();//得到查询属性表*/
                    FeatureTable mTable = AttributeQueryFeatureLayer.getFeatureTable();
                    //Log.w(TAG, "searchForState: " + mTable.getFields().get(0) );
                    final ListenableFuture<FeatureQueryResult> featureQueryResult
                            = mTable.queryFeaturesAsync(query1);
                    featureQueryResult.addDoneListener(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                FeatureCollectionTable featureCollectionTable = new FeatureCollectionTable(featureQueryResult.get());
                                Iterator<Feature> featureIterator = featureCollectionTable.iterator();
                                while (featureIterator.hasNext()) {
                                    Feature feature = featureIterator.next();
                                    //Log.w(TAG, "run: " + feature.getAttributes().get("图上名称"));
                                    Envelope envelope = feature.getGeometry().getExtent();
                                    //String name = feature.getAttributes().get("图上名称").toString();
                                    String name = feature.getAttributes().get("标准名称").toString();
                                    String xzq = feature.getAttributes().get("ZLDWMC").toString();
                                    String type = feature.getAttributes().get("类别").toString();

                                    QueryInfo queryInfo = new QueryInfo(name + ", " + type + ", " + xzq, feature, envelope);
                                    queryInfos.add(queryInfo);
                                    /*boolean hasSame = false;
                                    for (int i = 0; i < queryInfos.size(); ++i) {
                                        if (name.equals(queryInfos.get(i).getName()))
                                            hasSame = true;
                                    }
                                    if (!hasSame) {
                                        Log.w(TAG, "run: " + "hasSame");
                                        QueryInfo queryInfo = new QueryInfo(name, feature, envelope);
                                        queryInfos.add(queryInfo);
                                    }*/
                                }
                                Log.w(TAG, "showListPopupWindow: " + queryInfos.size());
                                items = new String[queryInfos.size()];
                                for (int i = 0; i < queryInfos.size(); ++i) {
                                    items[i] = queryInfos.get(i).getName();
                                }

                                RecyclerView recyclerView1 = (RecyclerView) popView.findViewById(R.id.listview_recycler_view);
                                //GridLayoutManager layoutManager1 = new GridLayoutManager(popView.getContext(),1);
                                LinearLayoutManager layoutManager1 = new LinearLayoutManager(popView.getContext());
                                recyclerView1.setLayoutManager(layoutManager1);
                                listviewAdapter adapter1 = new listviewAdapter(queryInfos);
                                adapter1.setOnItemClickListener(new listviewAdapter.OnRecyclerItemClickListener() {
                                    @Override
                                    public void onItemClick(View view, String xzqdm, int position) {
                                        //mMapView.setViewpointGeometryAsync(queryInfos.get(position).getEnvelope(), 200);
                                        switch (queryInfos.get(position).getFeature().getGeometry().getGeometryType())
                                        {
                                            case POINT:
                                                mMapView.setViewpointCenterAsync((Point)queryInfos.get(position).getFeature().getGeometry(), 2000);
                                                mMapView.setViewpointRotationAsync(0);
                                                break;
                                                default:
                                                mMapView.setViewpointGeometryAsync(queryInfos.get(position).getEnvelope());
                                                break;
                                        }
                                        ShowQueriedFeature(queryInfos.get(position).getFeature());
                                        //Select the feature
                                        //DMPointFeatureLayer.selectFeature(queryInfos.get(position).getFeature());
                /*for (int i = 0; i < BaseLayerFieldsSheetList.size(); i++) {
                    FeatureLayer mfl = BaseLayerFieldsSheetList.get(i).getFeatureLayer();
                    if (mfl.getName().equals("CJDCQ"))
                    {
                        AttributeQueryFeatureLayer = mfl;
                        break;
                    }
                }
                AttributeQueryFeatureLayer.selectFeature(queryInfos.get(position).getFeature());*/
                                        Log.w(TAG, "run: " + mMapView.getMapScale());
                                        //mMapView.setViewpointScaleAsync(200000);
                                        popupWindow.dismiss();
                                    }
                                });
                                recyclerView1.setAdapter(adapter1);
                            } catch (Exception e) {
                                Toast.makeText(MainActivity.this, "Feature search failed for: " + string + ". Error=" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                Log.e(getResources().getString(R.string.app_name), "Feature search failed for: " + string + ". Error=" + e.getMessage());
                            }
                        }
                    });
                }
            }catch (ArcGISRuntimeException e){
                Toast.makeText(MainActivity.this, e.getMessage() + e.getErrorCode(), Toast.LENGTH_SHORT).show();
            }
        }
        //final List<QueryInfo> pois = new ArrayList<>();



        /*listviewAdapter adapter1 = new listviewAdapter(queryInfos);
        adapter1.setOnItemClickListener(new listviewAdapter.OnRecyclerItemClickListener() {
            @Override
            public void onItemClick(View view, String xzqdm, int position) {
                mMapView.setViewpointGeometryAsync(queryInfos.get(position).getEnvelope(), 200);

                //Select the feature
                DMPointFeatureLayer.selectFeature(queryInfos.get(position).getFeature());
                Log.w(TAG, "run: " + mMapView.getMapScale());
                mMapView.setViewpointScaleAsync(3000);
                popupWindow.dismiss();
            }
        });
        recyclerView1.setAdapter(adapter1);*/

        // ListView适配器
        //listPopupWindow.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, items));

        // 选择item的监听事件

        // 对话框的宽高

        // ListPopupWindow的锚,弹出框的位置是相对当前View的位置

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
        popupWindow.showAtLocation(popView, Gravity.TOP, 0, 50);
    }

    private void ShowQueriedFeature(Feature feature){

        QueriedFeatureGraphic = null;
        if (graphicsOverlay_QueriedFeatureLayer != null) {
            graphicsOverlay_QueriedFeatureLayer.getGraphics().clear();
            //if (graphicsOverlay_QueriedFeatureLayer != null)
            mMapView.getGraphicsOverlays().remove(graphicsOverlay_QueriedFeatureLayer);
            graphicsOverlay_QueriedFeatureLayer = null;

        }
        Log.w(TAG, "ShowQueriedFeature: " + feature.getGeometry().getGeometryType());
        switch (feature.getGeometry().getGeometryType()){
            case POINT:
                SimpleMarkerSymbol makerSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.rgb(255, 0, 255), 20);
                QueriedFeatureGraphic = new Graphic(feature.getGeometry(), makerSymbol);
                break;
            case POLYGON:
                SimpleLineSymbol QueriedlineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.BLACK, 3);
                SimpleFillSymbol QueriedfillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.VERTICAL, Color.rgb(255, 0, 255), QueriedlineSymbol);
                QueriedFeatureGraphic = new Graphic(feature.getGeometry(), QueriedfillSymbol);
                break;
            case POLYLINE:
                SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.rgb(255, 0, 255), 3);
                QueriedFeatureGraphic = new Graphic(feature.getGeometry(), lineSymbol);
                break;
        }

        graphicsOverlay_QueriedFeatureLayer = new GraphicsOverlay(GraphicsOverlay.RenderingMode.DYNAMIC);
        try {
            graphicsOverlay_QueriedFeatureLayer.getGraphics().clear();
            graphicsOverlay_QueriedFeatureLayer.getGraphics().add(QueriedFeatureGraphic);
        }catch (ArcGISRuntimeException e){
            Log.w(TAG, "drawGraphicsOverlayer: " + e.toString());
        }
        //if (graphicsOverlay_QueriedFeatureLayer != null)
            mMapView.getGraphicsOverlays().remove(graphicsOverlay_QueriedFeatureLayer);
        mMapView.getGraphicsOverlays().add(graphicsOverlay_QueriedFeatureLayer);

        /*SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.rgb(0, 255, 255), 3);
        Polyline polyline = new Polyline(points);
        Graphic g = new Graphic(polyline, lineSymbol);
        graphics.add(g);*/
    }
    GraphicsOverlay graphicsOverlay_QueriedFeatureLayer;
    Graphic QueriedFeatureGraphic;

    public void showListPopupWindowforListViewForUserLayer(View view, String searchString) {
        queryInfos.clear();
        final View popView = View.inflate(this, R.layout.popupwindow_listview,null);
        searchString = searchString.trim();
        DMPointFeatureLayer.clearSelection();
        popView.setX(view.getX());
        popView.setY(view.getY() + view.getHeight());
        final PopupWindow popupWindow = new PopupWindow(popView, view.getWidth(), 800);

        //popupWindow.setAnimationStyle(R.style.anim_popup_dir);
        popupWindow.setFocusable(true);
        //点击外部popueWindow消失
        popupWindow.setOutsideTouchable(true);
        // create objects required to do a selection with a query
        QueryParameters query = new QueryParameters();
        //make search case insensitive
        query.setWhereClause("upper(图上名称) LIKE '%" + searchString.toUpperCase() + "%'");
        Log.w(TAG, "searchForState: " );
        // call select features
        if (mMapView.getMap().getOperationalLayers().size() != 0) {

            //Log.w(TAG, "searchForState: getAttribution" + mMapView.getMap().getOperationalLayers().get(3).getAttribution());
            //Log.w(TAG, "searchForState: getDescription" + mMapView.getMap().getOperationalLayers().get(3).getDescription());
            //DMPointFeatureLayer = (FeatureLayer) mMapView.getMap().getOperationalLayers().get(11);
            try {
                final String string = searchString;
                FeatureTable mTable = DMPointFeatureLayer.getFeatureTable();//得到查询属性表
                //Log.w(TAG, "searchForState: " + mTable.getFields().get(0) );
                final ListenableFuture<FeatureQueryResult> featureQueryResult
                        = mTable.queryFeaturesAsync(query);
                featureQueryResult.addDoneListener(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            FeatureCollectionTable featureCollectionTable = new FeatureCollectionTable(featureQueryResult.get());
                            Iterator<Feature> featureIterator = featureCollectionTable.iterator();
                            while (featureIterator.hasNext()) {
                                Feature feature = featureIterator.next();
                                Log.w(TAG, "run: " + feature.getAttributes().get("图上名称"));
                                Envelope envelope = feature.getGeometry().getExtent();
                                String name = feature.getAttributes().get("图上名称").toString();
                                boolean hasSame = false;
                                for (int i = 0; i < queryInfos.size(); ++i){
                                    if (name.equals(queryInfos.get(i).getName())) hasSame = true;
                                }
                                if (!hasSame) {
                                    Log.w(TAG, "run: " + "hasSame");
                                    QueryInfo queryInfo = new QueryInfo(name, feature, envelope);
                                    queryInfos.add(queryInfo);
                                }
                            }
                            Log.w(TAG, "showListPopupWindow: " + queryInfos.size());
                            items = new String[queryInfos.size()];
                            for (int i = 0; i < queryInfos.size(); ++i){
                                items[i] = queryInfos.get(i).getName();
                            }

                            RecyclerView recyclerView1 = (RecyclerView) popView.findViewById(R.id.listview_recycler_view);
                            //GridLayoutManager layoutManager1 = new GridLayoutManager(popView.getContext(),1);
                            LinearLayoutManager layoutManager1 = new LinearLayoutManager(popView.getContext());
                            recyclerView1.setLayoutManager(layoutManager1);
                            listviewAdapter adapter1 = new listviewAdapter(queryInfos);
                            adapter1.setOnItemClickListener(new listviewAdapter.OnRecyclerItemClickListener() {
                                @Override
                                public void onItemClick(View view, String xzqdm, int position) {
                                    mMapView.setViewpointGeometryAsync(queryInfos.get(position).getEnvelope(), 200);

                                    //Select the feature
                                    DMPointFeatureLayer.selectFeature(queryInfos.get(position).getFeature());
                                    Log.w(TAG, "run: " + mMapView.getMapScale());
                                    mMapView.setViewpointScaleAsync(3000);
                                    popupWindow.dismiss();
                                }
                            });
                            recyclerView1.setAdapter(adapter1);
                        } catch (Exception e) {
                            Toast.makeText(MainActivity.this, "Feature search failed for: " + string + ". Error=" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.e(getResources().getString(R.string.app_name), "Feature search failed for: " + string + ". Error=" + e.getMessage());
                        }
                    }
                });
            }catch (ArcGISRuntimeException e){
                Toast.makeText(MainActivity.this, e.getMessage() + e.getErrorCode(), Toast.LENGTH_SHORT).show();
            }
        }
        //final List<QueryInfo> pois = new ArrayList<>();



        /*listviewAdapter adapter1 = new listviewAdapter(queryInfos);
        adapter1.setOnItemClickListener(new listviewAdapter.OnRecyclerItemClickListener() {
            @Override
            public void onItemClick(View view, String xzqdm, int position) {
                mMapView.setViewpointGeometryAsync(queryInfos.get(position).getEnvelope(), 200);

                //Select the feature
                DMPointFeatureLayer.selectFeature(queryInfos.get(position).getFeature());
                Log.w(TAG, "run: " + mMapView.getMapScale());
                mMapView.setViewpointScaleAsync(3000);
                popupWindow.dismiss();
            }
        });
        recyclerView1.setAdapter(adapter1);*/

        // ListView适配器
        //listPopupWindow.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, items));

        // 选择item的监听事件

        // 对话框的宽高

        // ListPopupWindow的锚,弹出框的位置是相对当前View的位置

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
        popupWindow.showAtLocation(popView, Gravity.TOP, 0, 50);
    }

    private void queryPTB(){
        queryInfos.clear();
        QueryParameters query = new QueryParameters();
        //make search case insensitive
        query.setWhereClause("upper(BZPZWH) LIKE '%云国土%'");
        try {
            FeatureTable mTable = PTuBanFeatureLayer.getFeatureTable();//得到查询属性表
            //Log.w(TAG, "searchForState: " + mTable.getFields().get(0) );
            final ListenableFuture<FeatureQueryResult> featureQueryResult
                    = mTable.queryFeaturesAsync(query);
            featureQueryResult.addDoneListener(new Runnable() {
                @Override
                public void run() {
                    try {
                        FeatureCollectionTable featureCollectionTable = new FeatureCollectionTable(featureQueryResult.get());
                        Iterator<Feature> featureIterator = featureCollectionTable.iterator();
                        while (featureIterator.hasNext()) {
                            Feature feature = featureIterator.next();
                            Log.w(TAG, "run: " + feature.getAttributes().get("BZPZWH"));
                            Envelope envelope = feature.getGeometry().getExtent();
                            String name = feature.getAttributes().get("BZPZWH").toString();
                            boolean hasSame = false;
                            for (int i = 0; i < queryInfos.size(); ++i){
                                if (name.equals(queryInfos.get(i).getName())) hasSame = true;
                            }
                            if (!hasSame) {
                                Log.w(TAG, "run: " + "hasSame");
                                QueryInfo queryInfo = new QueryInfo(name, feature, envelope, 2, Integer.valueOf(name.substring(name.indexOf("[") + 1, name.indexOf("]"))));
                                queryInfos.add(queryInfo);
                                //Log.w(TAG, "setRecyclerViewForP: " + name.substring(name.indexOf("[") + 1, name.indexOf("]")));
                            }
                        }
                        Log.w(TAG, "showListPopupWindow: " + queryInfos.size());
                        SharedPreferences.Editor editor = getSharedPreferences("ptb", MODE_PRIVATE).edit();
                        editor.putString("year", "");
                        editor.apply();
                        setRightRecyclerView();
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, "Feature search failed for: " + ". Error=" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e(getResources().getString(R.string.app_name), "Feature search failed for: " + ". Error=" + e.getMessage());
                    }
                }
            });
        }catch (ArcGISRuntimeException e){
            Toast.makeText(MainActivity.this, e.getMessage() + e.getErrorCode(), Toast.LENGTH_SHORT).show();
        }
    }

    Geometry mPolygon;
    private void queryPTB(String name){
        //queryInfos.clear();
        pieChartView.setVisibility(View.INVISIBLE);
        removeGraphicsOverlayers();
        QueryParameters query = new QueryParameters();
        //make search case insensitive
        query.setWhereClause("upper(BZPZWH) LIKE '%" + name + "%'");
        try {
            FeatureTable mTable = PTuBanFeatureLayer.getFeatureTable();//得到查询属性表
            final ListenableFuture<FeatureQueryResult> featureQueryResult
                    = mTable.queryFeaturesAsync(query);
            featureQueryResult.addDoneListener(new Runnable() {
                @Override
                public void run() {
                    try {
                        FeatureQueryResult featureResul = featureQueryResult.get();
                        mPolygon = null;
                        for (Object element : featureResul) {
                            if (element instanceof Feature) {
                                Feature mFeatureGrafic = (Feature) element;
                                Polygon polygon1 = (Polygon) mFeatureGrafic.getGeometry();
                                if (mPolygon == null)
                                    mPolygon = GeometryEngine.project(polygon1, SpatialReference.create(4523));
                                else
                                    mPolygon = GeometryEngine.union(GeometryEngine.project(polygon1, SpatialReference.create(4523)), mPolygon);
                                removeGraphicsOverlayers();
                            }
                        }
                        GraphicsOverlay graphicsOverlay_1 = new GraphicsOverlay();
                        SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.BLUE, 3);
                        Graphic fillGraphic = new Graphic(mPolygon, lineSymbol);
                        graphicsOverlay_1.getGraphics().add(fillGraphic);
                        mMapView.getGraphicsOverlays().add(graphicsOverlay_1);
                        Envelope envelope = mPolygon.getExtent();
                        Log.w(TAG, "lllllzzzyyy: " + envelope.toString());
                        mMapView.setViewpointGeometryAsync(envelope);
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, "Feature search failed for: " + ". Error=" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e(getResources().getString(R.string.app_name), "Feature search failed for: " + ". Error=" + e.getMessage());
                    }
                }
            });
        }catch (ArcGISRuntimeException e){
            Toast.makeText(MainActivity.this, e.getMessage() + e.getErrorCode(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.menu_main, menu);
        // Get the SearchView and set the searchable configuration
        /*SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
*/
        return true;
    }

    private Boolean ShowWhiteBlank = true;
    private Boolean ShowTrail = true;
    private Boolean ShowPoi = true;
    private Boolean ShowMyTuban = true;

    private void setLeftRecyclerView(){
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.LeftRecyclerView);
        GridLayoutManager layoutManager = new GridLayoutManager(this,1);
        recyclerView.setLayoutManager(layoutManager);
        Log.w(TAG, "setRecyclerView: " + layerList.size() );
        final layerAdapter adapter = new layerAdapter(layerList, map);
        adapter.setOnItemCheckListener(new layerAdapter.OnRecyclerItemCheckListener() {
            @Override
            public void onItemCheckClick(layerAdapter.ViewHolder holder, String name, int position) {
                Log.w(TAG, "onItemCheckClick: 200902" + name);
                if (!holder.checkBox.isChecked()){
                    holder.checkBox.setChecked(false);
                    if (!name.equals("影像")) {

                        if(name.equals("白板层")){
                            ShowWhiteBlank = false;
                            drawGraphicsOverlayer();
                        }
                        else if(name.equals("轨迹线层")){
                            ShowTrail = false;
                            drawGraphicsOverlayer();
                        }
                        else if(name.equals("兴趣点层")){
                            ShowPoi = false;
                            drawGraphicsOverlayer();
                        }
                        else if(name.equals("按需查询层")){
                            ShowMyTuban = false;
                            drawGraphicsOverlayer();
                        }
                        else {
                            map.getOperationalLayers().get(layers.get(position).getNum()).setVisible(false);
                            for (int i = 0; i < layerList.size(); ++i){
                                if (name.equals(layerList.get(i).getName())) {
                                    layerList.get(i).setStatus(false);
                                    UserLayerOpenStatus.remove(layerList.get(i).getPath());
                                    UserLayerOpenStatus.put(layerList.get(i).getPath(), false);
                                }
                            }
                        }

                    }
                    else {
                        for (int kk = 0; kk < TPKlayers.size(); ++kk){
                            map.getOperationalLayers().get(TPKlayers.get(kk).getNum()).setVisible(false);
                        }
                        for (int i = 0; i < layerList.size(); ++i){
                            if (name.equals(layerList.get(i).getName())) layerList.get(i).setStatus(false);
                        }
                    }
                    mMapView.setMap(map);
                }else {
                    holder.checkBox.setChecked(true);
                    if (!name.equals("影像")) {

                        if(name.equals("白板层")){
                            ShowWhiteBlank = true;
                            drawGraphicsOverlayer();
                        }
                        else if(name.equals("轨迹线层")){
                            ShowTrail = true;
                            drawGraphicsOverlayer();
                        }
                        else if(name.equals("兴趣点层")){
                            ShowPoi = true;
                            drawGraphicsOverlayer();
                        }
                        else if(name.equals("按需查询层")){
                            ShowMyTuban = true;
                            drawGraphicsOverlayer();
                        }
                        else {
                            if (name.equals("土地利用变更调查数据2020年") || name.equals("二调地类图斑") || name.equals("土地规划地类") || name.equals("永善县稳定耕地") || name.equals("土地承包经营权") || name.equals("永久基本农田保护红线"))
                            {
                                if (mMapView.getMapScale() < 50000) {
                                    map.getOperationalLayers().get(layers.get(position).getNum()).setVisible(true);
                                    for (int i = 0; i < layerList.size(); ++i) {
                                        if (name.equals(layerList.get(i).getName()))
                                        {
                                            layerList.get(i).setStatus(true);
                                            for (int j = 0; j < LayerFieldsSheetList.size(); j++) {
                                                LayerFieldsSheet layerFieldsSheet = LayerFieldsSheetList.get(j);
                                                if (j < BaseLayerFieldsSheetList.size()) {
                                                    if (layerList.get(i).getName().equals(layerFieldsSheet.getLayerShowName())) {
                                                        XZQLayerName = LayerFieldsSheetList.get(j).getLayerShowName();
                                                        DynamicCheckFunction(j);
                                                        break;
                                                    }
                                                }
                                                else{
                                                    if (layerList.get(i).getPath().equals(layerFieldsSheet.getLayerPath())) {
                                                        XZQLayerName = LayerFieldsSheetList.get(j).getLayerShowName();
                                                        DynamicCheckFunction(j);
                                                        break;
                                                    }
                                                }
                                            }
                                            break;
                                        }
                                    }
                                }
                                else
                                {
                                    holder.checkBox.setChecked(false);
                                    Toast.makeText(MainActivity.this, "该数据层要素过多，易导致程序异常，请在1：50000以上比例尺再打开",Toast.LENGTH_LONG).show();
                                }
                            }
                            else
                            {
                                map.getOperationalLayers().get(layers.get(position).getNum()).setVisible(true);
                                for (int i = 0; i < layerList.size(); ++i) {
                                    if (name.equals(layerList.get(i).getName()))
                                    {
                                        layerList.get(i).setStatus(true);
                                        UserLayerOpenStatus.remove(layerList.get(i).getPath());
                                        UserLayerOpenStatus.put(layerList.get(i).getPath(), true);

                                        for (int j = 0; j < LayerFieldsSheetList.size(); j++) {
                                            LayerFieldsSheet layerFieldsSheet = LayerFieldsSheetList.get(j);
                                            if (j < BaseLayerFieldsSheetList.size()) {
                                                if (layerList.get(i).getName().equals(layerFieldsSheet.getLayerShowName())) {
                                                    XZQLayerName = LayerFieldsSheetList.get(j).getLayerShowName();
                                                    DynamicCheckFunction(j);
                                                    break;
                                                }
                                            }
                                            else{
                                                if (layerList.get(i).getPath().equals(layerFieldsSheet.getLayerPath())) {
                                                    XZQLayerName = LayerFieldsSheetList.get(j).getLayerShowName();
                                                    DynamicCheckFunction(j);
                                                    break;
                                                }
                                            }
                                        }
                                        break;
                                    }
                                }
                            }

                        }
                    }
                    else {
                        for (int kk = 0; kk < TPKlayers.size(); ++kk){
                            map.getOperationalLayers().get(TPKlayers.get(kk).getNum()).setVisible(true);
                        }
                        for (int i = 0; i < layerList.size(); ++i){
                            if (name.equals(layerList.get(i).getName())) layerList.get(i).setStatus(true);
                        }
                    }
                    mMapView.setMap(map);
                }
            }
        });
        adapter.setOnItemLongClickListener(new layerAdapter.OnRecyclerItemLongListener() {
            @Override
            public void onItemLongClick(layerAdapter.ViewHolder holder, final String name, final String path) {
                // TODO 2020/12/7
                //DynamicLongClickFunction(adapter, holder, name, path);
                Log.w(TAG, "onClick: 2020/12/7 For LongClick: " + path);
                LayerAdapterLongClickFunction(holder, name, path);
                recyclerView.setAdapter(adapter);
            }
        });
        recyclerView.setAdapter(adapter);
        isLoc = true;
    }

    private void LayerAdapterLongClickFunction(layerAdapter.ViewHolder holder, final String name, final String path){
        Boolean isBaseLayer = IsBasedLayerForMMPK(name);
        if (!isBaseLayer) {
            {
                holder.cardView.setCardBackgroundColor(Color.RED);
                if (path.contains(".SHP") || path.contains(".shp")) {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("提示")
                            .setMessage("想对该用户要素图层进行什么操作？")
                            .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Log.w(TAG, "onClick: 2020/12/7 For Left: " + name + "; " + path);

                                    RemoveUserLayer(name, path);
                                    setRecyclerViewForDynamicChooseFrame();
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .setNeutralButton("样式管理器", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // TODO 2021/1/18 样式管理器

                                    Log.w(TAG, "onClick: " + "取样器已经打开");

                                    new ColorPickerPopup.Builder(MainActivity.this)
                                            .initialColor(Color.RED) // Set initial color
                                            .enableBrightness(true) // Enable brightness slider or not
                                            .enableAlpha(true) // Enable alpha slider or not
                                            .okTitle("确定")
                                            .cancelTitle("取消")
                                            .showIndicator(true)
                                            .showValue(true)
                                            .build()
                                            .show(new ColorPickerPopup.ColorPickerObserver() {
                                                @Override
                                                public void onColorPicked(int color) {
                                                    WindowManager wm = (WindowManager) MainActivity.this
                                                            .getSystemService(Context.WINDOW_SERVICE);
                                                    DisplayMetrics dm = new DisplayMetrics();
                                                    wm.getDefaultDisplay().getMetrics(dm);
                                                    int width = dm.widthPixels;
                                                    int height = dm.heightPixels;
                                                    android.graphics.Point screenPoint = new android.graphics.Point(Math.round(width/2),
                                                            Math.round(height/2 - getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop()));
                                                    Point mapPoint = mMapView.screenToLocation(screenPoint);
                                                    Point wgs84Point = (Point) GeometryEngine.project(mapPoint, SpatialReference.create(4523));

                                                    double MapScale = mMapView.getMapScale();
                                                    double Rotation = mMapView.getMapRotation();

                                                    RemoveUserLayer(name, path);
                                                    setRecyclerViewForDynamicChooseFrame();


                                                    UserLayer userLayer = new UserLayer(path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf(".")), path, UserLayer.SHP_FILE, color);

                                                    //userLayer.setShpColor(selectedColor);

                                                    userLayer.save();
                                                    //setRecyclerViewForDynamicChooseFrame(LayerFieldsSheetList.size()-1);
                                                    Log.w(TAG, "useUserLayer: " + LitePal.findAll(UserLayer.class).size());
                                                    //useUserLayer();
                                                    showUserLayer(userLayer);
                                                    ResetMapViewForNow(wgs84Point, MapScale, Rotation);
                                                }
                                            });
                                }
                            })
                            .show();
                }
                else{
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("提示")
                            .setMessage("删除该用户图层吗？")
                            .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Log.w(TAG, "onClick: 2020/12/7 For Left: " + name + "; " + path);

                                    RemoveUserLayer(name, path);
                                    //setRecyclerViewForDynamicChooseFrame();
                                }
                            })
                            .setNegativeButton("否", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .show();
                }
            }

        }
    }

    private RecyclerView recyclerViewForP;
    private void setRecyclerViewForDynamicChooseFrame(){
        //final RecyclerView recyclerViewForP = (RecyclerView) findViewById(R.id.recyclerViewForP);
        GridLayoutManager layoutManager = new GridLayoutManager(this,1);
        recyclerViewForP.setLayoutManager(layoutManager);

        final DynamicChooseFrameAdapter adapter = new DynamicChooseFrameAdapter(LayerFieldsSheetList);
        adapter.setOnItemCheckListener(new DynamicChooseFrameAdapter.OnRecyclerItemCheckListener() {
            @Override
            public void onItemCheckClick(DynamicChooseFrameAdapter.ViewHolder holder, String name, int position) {
                XZQLayerName = LayerFieldsSheetList.get(position).getLayerShowName();
                DynamicCheckFunction(position);
            }
        });
        adapter.setOnItemLongClickListener(new DynamicChooseFrameAdapter.OnRecyclerItemLongListener() {
            @Override
            public void onItemLongClick(DynamicChooseFrameAdapter.ViewHolder holder, final String name, final String path) {
                DynamicLongClickFunction(adapter, holder, name, path);
            }
        });
        recyclerViewForP.setAdapter(adapter);
    }

    private void DynamicCheckFunction(int position){
        Log.w(TAG, "onClick: 2020/9/7 : " + position);
        QueriedLayerIndex = position;
        if (QueriedLayerIndex >= BaseLayerFieldsSheetList.size())
            isQueryUserLayer = true;
        else
            isQueryUserLayer = false;
        setRecyclerViewForDynamicChooseFrame(position);
        if (QueryProcessType == DisplayEnum.FINISHQUERY){
            final Polygon polygon = new Polygon(pointCollection);
            final QueryParameters query = new QueryParameters();
            query.setGeometry(polygon);// 设置空间几何对象
            PopWindowData = new ArrayList<>();
            try {
                //基本农田保护区 老版本
                //querySingleTaskForPolygon(query, polygon, JBNTFeatureLayer.getFeatureTable(), "JBNTBHQ");

                if (QueriedLayerIndex != -1)
                {
                    queryTaskFor20200904(query, polygon);
                    SaveUserDrawedTB();
                }
                // TODO 2020/9/4 完成新的按需查询内容
                //querySingleTaskForPolygon(query, polygon, BaseLayerFieldsSheetList.get(1));
            } catch (ArcGISRuntimeException e) {
                Toast.makeText(MainActivity.this, e.getMessage() + e.getErrorCode(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private Boolean IsBasedLayerForGeodatabase(String name){
        Boolean isBasedLayer = false;
        for (int i = 0; i < BaseLayerFieldsSheetList.size(); i++) {
            if (name.equals(BaseLayerFieldsSheetList.get(i).getLayerShowName())) {
                isBasedLayer = true;
                break;
            }
        }
        return isBasedLayer;
    }

    List<String> BaseLayerForMMPK = new ArrayList<>();
    private Boolean IsBasedLayerForMMPK(String name){
        Boolean isBasedLayer = false;
        for (int i = 0; i < BaseLayerForMMPK.size(); i++) {
            if (name.equals(BaseLayerForMMPK.get(i))) {
                isBasedLayer = true;
                break;
            }
        }
        return isBasedLayer;
    }

    private void RemoveUserLayer(String pName, String path){
        try {
            Log.w(TAG, "size: " + path);
            if(path.substring(path.length()-4).contains("tif") || path.substring(path.length()-4).contains("TIF")) {
                List<UserLayer> list = LitePal.where("type = ?", Integer.toString(UserLayer.TIF_FILE)).find(UserLayer.class);
                Log.w(TAG, "run 2020/9/2: " + list.get(0).getName());
                ;
                for (int i = 0; i < list.size(); ++i) {
                    String mpath = list.get(i).getPath();
                    String name = list.get(i).getName();


                    if (mpath.equals(path)) {
                        LitePal.deleteAll(UserLayer.class, "path = ?", mpath);
                        list.remove(i);
                        map.getOperationalLayers().clear();
                        for (int k = 0; k < BaseMMPKLayer.size(); k++) {
                            map.getOperationalLayers().add(BaseMMPKLayer.get(k));
                        }
                        initLayerList();
                        break;
                    }
                }
            }
            else {
                List<UserLayer> list = LitePal.where("type = ?", Integer.toString(UserLayer.SHP_FILE)).find(UserLayer.class);
                ;
                for (int i = 0; i < list.size(); ++i) {
                    String mpath = list.get(i).getPath();
                    String name = list.get(i).getName();


                    if (mpath.equals(path)) {
                        LitePal.deleteAll(UserLayer.class, "path = ?", mpath);
                        list.remove(i);
                        --i;
                        for (int j = 0; j < LayerFieldsSheetList.size(); j++) {
                            if (path.equals(LayerFieldsSheetList.get(j).getLayerPath())) {
                                LayerFieldsSheetList.remove(j);
                                Log.w(TAG, "RemoveUserLayer: " + map.getOperationalLayers().size());
                                map.getOperationalLayers().clear();
                                for (int k = 0; k < BaseMMPKLayer.size(); k++) {
                                    map.getOperationalLayers().add(BaseMMPKLayer.get(k));
                                }
                                initLayerList();
                            }
                        }
                        break;
                    }
                }
            }
            useUserLayer();
            ResetMapView();
        }
        catch (Exception e){
            Toast.makeText(MainActivity.this, "可能该用户图层和底图图层重名，无法使用该方式移除图层" + "\n" + "请在文件管理器中删除需要移除的图层内容！", Toast.LENGTH_LONG).show();
        }
    }

    private void setRecyclerViewForDynamicChooseFrame(int index){
        //RecyclerView recyclerViewForP = (RecyclerView) findViewById(R.id.recyclerViewForP);
        GridLayoutManager layoutManager = new GridLayoutManager(this,1);
        recyclerViewForP.setLayoutManager(layoutManager);

        final DynamicChooseFrameAdapter adapter = new DynamicChooseFrameAdapter(LayerFieldsSheetList, index);
        adapter.setOnItemCheckListener(new DynamicChooseFrameAdapter.OnRecyclerItemCheckListener() {
            @Override
            public void onItemCheckClick(DynamicChooseFrameAdapter.ViewHolder holder, String name, int position) {
                XZQLayerName = LayerFieldsSheetList.get(position).getLayerShowName();
                DynamicCheckFunction1(position);
            }
        });
        adapter.setOnItemLongClickListener(new DynamicChooseFrameAdapter.OnRecyclerItemLongListener() {
            @Override
            public void onItemLongClick(DynamicChooseFrameAdapter.ViewHolder holder, final String name, final String path) {
                DynamicLongClickFunction(adapter, holder, name, path);
            }
        });
        recyclerViewForP.setAdapter(adapter);
    }

    private void DynamicLongClickFunction(final DynamicChooseFrameAdapter adapter, DynamicChooseFrameAdapter.ViewHolder holder, final String name, final String path){
        holder.cardView.setCardBackgroundColor(Color.RED);
        Toast.makeText(MainActivity.this, name, Toast.LENGTH_SHORT).show();
        Boolean isBasedLayer = IsBasedLayerForGeodatabase(name);
        if (isBasedLayer) {
            recyclerViewForP.setAdapter(adapter);
        } else {
            //Toast.makeText(MainActivity.this, "长按了", Toast.LENGTH_SHORT).show();
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("提示")
                    .setMessage("删除该用户图层吗？")
                    .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.w(TAG, "onClick: 2020/12/7 For Right: " + name + "; " + path);
                            RemoveUserLayer(name, path);
                            recyclerViewForP.setAdapter(adapter);
                        }
                    })
                    .setNegativeButton("否", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            recyclerViewForP.setAdapter(adapter);
                        }
                    })
                    .show();
        }
    }

    private void DynamicCheckFunction1(int position){
        Log.w(TAG, "onClick: 2020/9/7 : " + position);
        QueriedLayerIndex = position;
        XZQLayerName = LayerFieldsSheetList.get(QueriedLayerIndex).getLayerShowName();
        if (QueriedLayerIndex >= BaseLayerFieldsSheetList.size())
            isQueryUserLayer = true;
        else
            isQueryUserLayer = false;
        setRecyclerViewForDynamicChooseFrame(position);
        if (QueryProcessType == DisplayEnum.FINISHQUERY){
            if(pointCollection != null){
                final Polygon polygon = new Polygon(pointCollection);
                final QueryParameters query = new QueryParameters();
                query.setGeometry(polygon);// 设置空间几何对象
                PopWindowData = new ArrayList<>();
                try {
                    //基本农田保护区 老版本
                    //querySingleTaskForPolygon(query, polygon, JBNTFeatureLayer.getFeatureTable(), "JBNTBHQ");

                    if (QueriedLayerIndex != -1)
                    {
                        queryTaskFor20200904(query, polygon);
                        SaveUserDrawedTB();
                    }
                    // TODO 2020/9/4 完成新的按需查询内容
                    //querySingleTaskForPolygon(query, polygon, BaseLayerFieldsSheetList.get(1));
                } catch (ArcGISRuntimeException e) {
                    Toast.makeText(MainActivity.this, e.getMessage() + e.getErrorCode(), Toast.LENGTH_SHORT).show();
                }
            }
            else{
                pieChartView.setVisibility(View.GONE);
                mCallout.dismiss();
                NeedQueryForBasePolygon(AnalysisUserGeometry);
            }
        }
    }

    private void setRightRecyclerView(){
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.RightRecyclerView);
        GridLayoutManager layoutManager = new GridLayoutManager(this,1);
        recyclerView.setLayoutManager(layoutManager);
        List<QueryInfo> queryInfos2 = new ArrayList<>();
        queryInfos2.addAll(QueryInfo.sort(queryInfos));

        QueryInfoAdapter adapter = new QueryInfoAdapter(queryInfos2, queryInfos);
        adapter.setOnItemClickListener(new QueryInfoAdapter.OnRecyclerItemClickListener() {
            @Override
            public void onItemClick(View view, String path, int position) {
                Log.w(TAG, "onItemClick: " + path);
                if (path.contains("号")){
                    queryPTB(path);
                    RunningFunction = DisplayEnum.FUNC_ANA;
                    if (!MapQuery)
                        mapQueryBtEvent();
                    showQueryWidgetForP();
                    removeStandardWidget();
                    QueryProcessType = DisplayEnum.INQUERY;
                }else{
                    /*SharedPreferences.Editor editor = getSharedPreferences("ptb", MODE_PRIVATE).edit();
                    editor.putString("year", path);
                    editor.apply();*/


                }
            }
        });
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onPause() {
        mMapView.pause();
        sensorManager.unregisterListener(listener);
        super.onPause();
    }

    /*public String queryNormalPoi(final PointF pt1) {
        List<mPOIobj> pois = new ArrayList<>();
        Cursor cursor = LitePal.findBySQL("select * from POI where x >= ? and x <= ? and y >= ? and y <= ?", String.valueOf(min_lat), String.valueOf(max_lat), String.valueOf(min_long), String.valueOf(max_long));
        if (cursor.moveToFirst()) {
            do {
                String POIC = cursor.getString(cursor.getColumnIndex("poic"));
                String time = cursor.getString(cursor.getColumnIndex("time"));
                String name = cursor.getString(cursor.getColumnIndex("name"));
                String description = cursor.getString(cursor.getColumnIndex("description"));
                int tapenum = cursor.getInt(cursor.getColumnIndex("tapenum"));
                int photonum = cursor.getInt(cursor.getColumnIndex("photonum"));
                float x = cursor.getFloat(cursor.getColumnIndex("x"));
                float y = cursor.getFloat(cursor.getColumnIndex("y"));
                mPOIobj mPOIobj = new mPOIobj(POIC, x, y, time, tapenum, photonum, name, description);
                pois.add(mPOIobj);
            } while (cursor.moveToNext());
        }
        cursor.close();
        int n = 0;
        int num = 0;
        if (pois.size() > 0) {
            mPOIobj poii = pois.get(0);
            PointF pointF1 = new PointF(poii.getM_X(), poii.getM_Y());
            pointF1 = LatLng.getPixLocFromGeoL(pointF1, current_pagewidth, current_pageheight, w, h, min_long, min_lat);
            pointF1 = new PointF(pointF1.x, pointF1.y - 70);
            //pointF = getGeoLocFromPixL(pointF);
            final PointF pt9 = LatLng.getPixLocFromGeoL(pt1, current_pagewidth, current_pageheight, w, h, min_long, min_lat);
            float mdelta = Math.abs(pointF1.x - pt9.x) + Math.abs(pointF1.y - pt9.y);
            for (mPOIobj poi : pois) {
                PointF mpointF1 = new PointF(poi.getM_X(), poi.getM_Y());
                Log.w(TAG, "mpointF1 queried: " + mpointF1.x + ";" + mpointF1.y);
                mpointF1 = LatLng.getPixLocFromGeoL(mpointF1, current_pagewidth, current_pageheight, w, h, min_long, min_lat);
                mpointF1 = new PointF(mpointF1.x, mpointF1.y - 70);
                if (Math.abs(mpointF1.x - pt9.x) + Math.abs(mpointF1.y - pt9.y) < mdelta && Math.abs(mpointF1.x - pt9.x) + Math.abs(mpointF1.y - pt9.y) < 35) {
                    //locError("mpointF : " + mpointF1.toString());
                    mdelta = Math.abs(pointF1.x - pt9.x) + Math.abs(pointF1.y - pt9.y);
                    num = n;
                }
                n++;
            }
            if (mdelta < 35 || num != 0) {
                return pois.get(num).getM_POIC();
                //locError(Integer.toString(pois.get(num).getPhotonum()));
            } else {
                return "";
            }
        } else return "";
    }*/

    @Override
    protected void onResume() {
        super.onResume();
        // TODO 暂时删除
        mMapView.resume();

        //drawGraphicsOverlayer();
        //setRecyclerView();
        //setRecyclerViewForP();
        // TODO 暂时删除
        //注册传感器监听器
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_UI);
        //Log.w(TAG, "getMapScale: " + mMapView.getMapScale());
        //Log.w(TAG, "getVisibleArea: " + mMapView.getVisibleArea().getExtent().getCenter());
        //hasMTuban = hasMyTuban();
        // TODO 暂时删除
        drawOperationalLayer();
    }

    private void drawOperationalLayer(){
        drawGraphicsOverlayer();
        /*if (hasMyTuban() && hasMyTuban())
            parseAndUpdateMyTuban();*/
    }

    @Override
    protected void onDestroy() {
        SharedPreferences.Editor editor = getSharedPreferences("xzq", MODE_PRIVATE).edit();
        editor.putString("name", "");
        editor.apply();
        super.onDestroy();
        getCacheDir().delete();
        getFilesDir().delete();
        mMapView.dispose();
    }

    private class ValueTouchListener implements PieChartOnValueSelectListener {

        private HashMap<String, ChartQueryResult> hashMap;
        private Geometry geo;

        public ValueTouchListener(){
        }

        public ValueTouchListener(HashMap<String, ChartQueryResult> hashMap, Geometry geo){
            this.hashMap = hashMap;
            this.geo = geo;
        }

        @Override
        public void onValueSelected(int arcIndex, SliceValue value) {
            DecimalFormat decimalFormat = new DecimalFormat("0.00");
            DecimalFormat decimalFormat1 = new DecimalFormat("0.000000");

            int index = -1;
            removeGraphicsOverlayers();
            Geometry QueriedGeo = null;
            Iterator iter = hashMap.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                Object key = entry.getKey();
                Object val = entry.getValue();
                String name = (String)key;
                ChartQueryResult cqr = (ChartQueryResult)val;
                index++;
                if (index == arcIndex) {
                    QueriedGeo = cqr.getPolygon();
                }
                else{
                    GraphicsOverlay graphicsOverlay_1 = new GraphicsOverlay();
                    SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.BLUE, 3);
                    Graphic fillGraphic = new Graphic(cqr.getPolygon(), lineSymbol);
                    graphicsOverlay_1.getGraphics().add(fillGraphic);
                    mMapView.getGraphicsOverlays().add(graphicsOverlay_1);
                }
            }

            GraphicsOverlay graphicsOverlay_1 = new GraphicsOverlay();
            SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.RED, 3);
            Graphic fillGraphic = new Graphic(geo, lineSymbol);
            graphicsOverlay_1.getGraphics().add(fillGraphic);
            mMapView.getGraphicsOverlays().add(graphicsOverlay_1);


            GraphicsOverlay QueriedGraphicsOverlay = new GraphicsOverlay();
            SimpleLineSymbol QueriedlineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.YELLOW, 3);
            SimpleFillSymbol QueriedfillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.VERTICAL, Color.YELLOW, QueriedlineSymbol);
            Graphic QueriedfillGraphic = new Graphic(QueriedGeo, QueriedfillSymbol);
            QueriedGraphicsOverlay.getGraphics().add(QueriedfillGraphic);
            mMapView.getGraphicsOverlays().add(QueriedGraphicsOverlay);
            Log.w(TAG, "run: 2020090417" + arcIndex);
            /*for (int i = 0; i < keyAndValues.size(); ++i){
                Log.w(TAG, "onValueSelected: " + (float)(Float.valueOf(keyAndValues.get(i).getValue()) / wholeArea) + ": " + value.getValue() + ": " + arcIndex);
                if (decimalFormat1.format((float)(Float.valueOf(keyAndValues.get(i).getValue()) / wholeArea)).equals(decimalFormat1.format(value.getValue()))) {
                    //Toast.makeText(MainActivity.this, keyAndValues.get(i).getName() + "占比: " + decimalFormat.format(value.getValue() * 100) + "%", Toast.LENGTH_SHORT).show();
                    Toast.makeText(MainActivity.this, keyAndValues.get(i).getName() + ": " + decimalFormat.format(Double.valueOf(keyAndValues.get(i).getValue())) + "亩", Toast.LENGTH_SHORT).show();
                    break;
                }
            }*/
        }

        @Override
        public void onValueDeselected() {

        }

    }

    //获取文件管理器的返回信息
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK){
            switch (requestCode){
                case EnumClass.REQUEST_CODE_PHOTO:
                    theNum = 0;
                    final Uri uri = data.getData();
                    Log.w(TAG, "onActivityResult: " + DataUtil.getRealPathFromUriForPhoto(MainActivity.this, uri));
                    final float[] latandlong = new float[2];
                    try {
                        ExifInterface exifInterface = new ExifInterface(DataUtil.getRealPathFromUriForPhoto(this, uri));
                        exifInterface.getLatLong(latandlong);
                        if (latandlong[0] != 0) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setTitle("提示");
                            builder.setMessage("请选择你要添加的图层");
                            builder.setNeutralButton(strings[0], new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    AddPhoto(uri, latandlong, 0);
                                }
                            });
                            builder.setNegativeButton(strings[1], new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    AddPhoto(uri, latandlong, 1);
                            /*else {
                                dmbzList = LitePal.findAll(DMBZ.class);
                                int size = dmbzList.size();
                                DMBZ dmbz = new DMBZ();
                                dmbz.setIMGPATH(DataUtil.getRealPathFromUriForPhoto(MainActivity.this, uri));
                                dmbz.setLat(latandlong[0]);
                                dmbz.setLng(latandlong[1]);
                                dmbz.setXH(String.valueOf(size + 1));
                                dmbz.setTime(simpleDateFormat1.format(new Date(System.currentTimeMillis())));
                                dmbz.save();
                                getDMBZBitmap();
                            }*/
                                }
                            });
                            builder.setPositiveButton(strings[2], new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    AddPhoto(uri, latandlong, 2);
                                }
                            });
                            builder.show();
                        } else
                            Toast.makeText(MainActivity.this, R.string.LocError1, Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case EnumClass.REQUEST_CODE_TAPE:
                    final Uri uri1 = data.getData();
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("提示");
                    builder.setMessage("请选择你要添加的图层");
                    builder.setNeutralButton(strings[0], new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            AddTape(uri1, 0);
                        }
                    });
                    builder.setNegativeButton(strings[1], new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            AddTape(uri1, 1);
                    /*else {
                        dmbzList = LitePal.findAll(DMBZ.class);
                        int size = dmbzList.size();
                        DMBZ dmbz = new DMBZ();
                        dmbz.setTAPEPATH(DataUtil.getRealPathFromUriForPhoto(MainActivity.this, uri));
                        dmbz.setLat((float) m_lat);
                        dmbz.setLng((float) m_long);
                        dmbz.setXH(String.valueOf(size + 1));
                        dmbz.setTime(simpleDateFormat1.format(new Date(System.currentTimeMillis())));
                        dmbz.save();
                        getDMBZBitmap();
                    }*/
                        }
                    });
                    builder.setPositiveButton(strings[2], new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            AddTape(uri1, 2);
                        }
                    });
                    builder.show();
                    break;
                case EnumClass.TAKE_PHOTO:
                    theNum = 0;
                    final String imageuri;
                    if (Build.VERSION.SDK_INT >= 24) {
                        imageuri = DataUtil.getRealPath(imageUri.toString());
                    } else {
                        imageuri = imageUri.toString().substring(7);
                    }
                    File file = new File(imageuri);
                    if (file.exists()) {
                        final float[] latandlong1 = new float[2];
                        try {
                            MediaStore.Images.Media.insertImage(getContentResolver(), imageuri, "title", "description");
                            // 最后通知图库更新
                            MainActivity.this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + imageuri)));
                            ExifInterface exifInterface = new ExifInterface(imageuri);
                            exifInterface.getLatLong(latandlong1);
                            //List<POI> POIs = LitePal.where("ic = ?", ic).find(POI.class);

                            AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                            builder1.setTitle("提示");
                            builder1.setMessage("请选择你要添加的图层");
                            builder1.setNeutralButton(strings[0], new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    AddTakePhoto(imageuri, latandlong1, 0);
                                }
                            });
                            builder1.setNegativeButton(strings[1], new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    AddTakePhoto(imageuri, latandlong1, 1);
                            /*else {
                                dmbzList = LitePal.findAll(DMBZ.class);
                                int size = dmbzList.size();
                                DMBZ dmbz = new DMBZ();
                                dmbz.setLat(latandlong[0]);
                                dmbz.setLng(latandlong[1]);
                                dmbz.setIMGPATH(imageuri);
                                dmbz.setXH(String.valueOf(size + 1));
                                dmbz.setTime(simpleDateFormat1.format(new Date(System.currentTimeMillis())));
                                dmbz.save();
                                getDMBZBitmap();
                            }*/
                                }
                            });
                            builder1.setPositiveButton(strings[2], new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    AddTakePhoto(imageuri, latandlong1, 2);
                                }
                            });
                            builder1.show();

                        } catch (Exception e) {
                            Log.w(TAG, e.toString());
                        }
                    } else {
                        file.delete();
                        Toast.makeText(MainActivity.this, R.string.TakePhotoError, Toast.LENGTH_LONG).show();
                    }
                    break;
                case EnumClass.GET_SHP_FILE:
                    //TODO 处理文件管理器获取SHP文件
                    final String shp_path = data.getStringExtra("filePath");

                    List<UserLayer> list = LitePal.where("type = ?", Integer.toString(UserLayer.SHP_FILE)).find(UserLayer.class);
                    Boolean hasSameFile = false;
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).getPath().equals(shp_path))
                        {
                            hasSameFile = true;
                            break;
                        }
                    }
                    if (!hasSameFile) {
                        Log.w(TAG, "onClick: " + "取样器已经打开");
                        new ColorPickerPopup.Builder(MainActivity.this)
                                .initialColor(Color.RED) // Set initial color
                                .enableBrightness(true) // Enable brightness slider or not
                                .enableAlpha(true) // Enable alpha slider or not
                                .okTitle("确定")
                                .cancelTitle("取消")
                                .showIndicator(true)
                                .showValue(true)
                                .build()
                                .show(new ColorPickerPopup.ColorPickerObserver() {
                                    @Override
                                    public void onColorPicked(int color) {
                                        UserLayer userLayer = new UserLayer(shp_path.substring(shp_path.lastIndexOf("/") + 1, shp_path.lastIndexOf(".")), shp_path, UserLayer.SHP_FILE, color);
                                        userLayer.save();
                                        Log.w(TAG, "useUserLayer: " + LitePal.findAll(UserLayer.class).size());
                                        //useUserLayer();
                                        showUserLayer(userLayer);
                                        Toast.makeText(MainActivity.this, shp_path, Toast.LENGTH_LONG).show();
                                        //ResetMapView();
                                    }
                                });
                    }
                    else
                        Toast.makeText(MainActivity.this, "不能重复添加图层文件！", Toast.LENGTH_LONG).show();
                    //Toast.makeText(MainActivity.this, shp_path.substring(shp_path.lastIndexOf("/") + 1), Toast.LENGTH_LONG).show();
                    break;
                case EnumClass.GET_TIF_FILE:
                    //TODO 处理文件管理器获取TIF文件
                    final String tif_path = data.getStringExtra("filePath");
                    List<UserLayer> list1 = LitePal.where("type = ?", Integer.toString(UserLayer.TIF_FILE)).find(UserLayer.class);
                    Boolean hasSameFile1 = false;
                    for (int i = 0; i < list1.size(); i++) {
                        if (list1.get(i).getPath().equals(tif_path))
                        {
                            hasSameFile1 = true;
                            break;
                        }
                    }
                    if (!hasSameFile1) {
                        CurrentTifPath = tif_path;
                        Log.w(TAG, "onClick: 2020/12/7 For Input: " + tif_path);
                        UserLayer userLayer1 = new UserLayer(tif_path.substring(tif_path.lastIndexOf("/") + 1, tif_path.lastIndexOf(".")), tif_path, UserLayer.TIF_FILE);
                        userLayer1.save();
                        showUserLayer(userLayer1);
                        //useUserLayer();
                        Toast.makeText(MainActivity.this, tif_path, Toast.LENGTH_LONG).show();
                        //TODO tiffffff
                        //ResetMapView();
                    }
                    else
                        Toast.makeText(MainActivity.this, "不能重复添加图层文件！", Toast.LENGTH_LONG).show();
                    break;
                case EnumClass.GET_PDF_FILE:
                    //TODO 处理文件管理器获取PDF文件
                    final String pdf_path = data.getStringExtra("filePath");
                    //String stringPath = Environment.getExternalStorageDirectory() + "/TuZhi/1.pdf";
                    boolean flag = openFile(pdf_path);
                    if (flag == true) {

                        Toast.makeText(MainActivity.this," 打开文件成功", Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(MainActivity.this, "打开文件失败", Toast.LENGTH_LONG).show();
                    }
                    break;
            }
        }
        /*if (resultCode == RESULT_OK && requestCode == EnumClass.REQUEST_CODE_PHOTO) {

        }else if (resultCode == RESULT_OK && requestCode == EnumClass.REQUEST_CODE_TAPE) {

        }else if (resultCode == RESULT_OK && requestCode == EnumClass.TAKE_PHOTO) {

            //String imageuri = getRealPathFromUriForPhoto(this, imageUri);

        }else if (resultCode == RESULT_OK && requestCode == EnumClass.GET_FILE){

        }*/
    }

    private void useUserLayer(){
        List<UserLayer> userLayerList = LitePal.findAll(UserLayer.class);
        Log.w(TAG, "useUserLayer: " + userLayerList.size());;
        checkUserLayerFile(userLayerList);
        showUserLayer(userLayerList);
    }

    private void checkUserLayerFile(List<UserLayer> userLayerList){
        try {
            UserLayerOpenStatus.clear();
            int size = userLayerList.size();
            for (int i = 0 ; i < size; ++i){
                String path = userLayerList.get(i).getPath();
                File file = new File(path);
                if (!file.exists()) {
                    LitePal.deleteAll(UserLayer.class, "path = ?", path);
                    userLayerList.remove(i);
                    --i;
                }
                else{
                    UserLayerOpenStatus.put(path, false);
                }
            }
        }catch (Exception e){
            Log.w(TAG, "checkUserLayer: " + e.toString());
        }
    }

    private void showUserLayer(List<UserLayer> userLayerList){
        try {
            int size = userLayerList.size();
            LayerFieldsSheetList.clear();
            LayerFieldsSheetList.addAll(BaseLayerFieldsSheetList);
            for (int i = 0 ; i < size; ++i){
                String path = userLayerList.get(i).getPath();

                switch (userLayerList.get(i).getType()){
                    case UserLayer.TIF_FILE:
                        loadRaster(path);
                        break;
                    case UserLayer.SHP_FILE:
                        featureLayerShapefile(path, userLayerList.get(i).getShpColor());
                        List<FieldNameSheet> FieldNameSheetList = new ArrayList<>();
                        LayerFieldsSheetList.add(new LayerFieldsSheet(userLayerList.get(i).getName(), userLayerList.get(i).getPath(), FieldNameSheetList));
                        setRecyclerViewForDynamicChooseFrame();
                        break;
                }
            }
        }catch (Exception e){
            Log.w(TAG, "checkUserLayer: " + e.toString());
        }
    }

    private void ResetMapView() {
        mMapView.setViewpointCenterAsync(OriginLocation, 700000);
        mMapView.setViewpointRotationAsync(0);
    }

    private void ResetMapViewForNow(Point wgs84Point, double MapScale, double Rotation) {
        mMapView.setViewpointCenterAsync(wgs84Point, MapScale);
        mMapView.setViewpointRotationAsync(Rotation);
    }

    private void showUserLayer(UserLayer userLayer){
        try {
            UserLayerOpenStatus.put(userLayer.getPath(), true);
            /*LayerFieldsSheetList.clear();
            LayerFieldsSheetList.addAll(BaseLayerFieldsSheetList);*/
                String path = userLayer.getPath();
                //TODO 完成用户图层使用逻辑
                switch (userLayer.getType()){
                    case UserLayer.TIF_FILE:
                        loadRaster(userLayer.getName(), path);

                        break;
                    case UserLayer.SHP_FILE:
                        featureLayerShapefile(path, userLayer.getShpColor());
                        List<FieldNameSheet> FieldNameSheetList = new ArrayList<>();
                        LayerFieldsSheetList.add(new LayerFieldsSheet(userLayer.getName(), userLayer.getPath(), FieldNameSheetList));
                        //setRecyclerViewForDynamicChooseFrame();
                        XZQLayerName = LayerFieldsSheetList.get(LayerFieldsSheetList.size()-1).getLayerShowName();
                        DynamicCheckFunction(LayerFieldsSheetList.size()-1);
                        break;
                }
        }catch (Exception e){
            Log.w(TAG, "checkUserLayer: " + e.toString());
        }
    }

    private void takePhoto() {
        File file2 = new File(Environment.getExternalStorageDirectory() + "/TuZhi/photo");
        if (!file2.exists() && !file2.isDirectory()) {
            file2.mkdirs();
        }
        long timenow = System.currentTimeMillis();
        File outputImage = new File(Environment.getExternalStorageDirectory() + "/TuZhi/photo", Long.toString(timenow) + ".jpg");
        try {
            if (outputImage.exists()) {
                outputImage.delete();
            }
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            if (Build.VERSION.SDK_INT >= 24) {
                //locError(Environment.getExternalStorageDirectory() + "/maphoto/" + Long.toString(timenow) + ".jpg");
                imageUri = FileProvider.getUriForFile(MainActivity.this, "com.android.displaymap.fileprovider", outputImage);

            } else imageUri = Uri.fromFile(outputImage);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(intent, EnumClass.TAKE_PHOTO);
        } catch (Exception e) {
            Log.w(TAG, e.toString());
        }

    }

    private void showPopueWindowForPhoto() {
        View popView = View.inflate(this, R.layout.popupwindow_camera_need, null);
        Button bt_album = (Button) popView.findViewById(R.id.btn_pop_album);
        Button bt_camera = (Button) popView.findViewById(R.id.btn_pop_camera);
        Button bt_cancle = (Button) popView.findViewById(R.id.btn_pop_cancel);
        //获取屏幕宽高
        int weight = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels * 1 / 3;

        final PopupWindow popupWindow = new PopupWindow(popView, weight, height);
        //popupWindow.setAnimationStyle(R.style.anim_popup_dir);
        popupWindow.setFocusable(true);
        //点击外部popueWindow消失
        popupWindow.setOutsideTouchable(true);

        bt_album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickFile();
                popupWindow.dismiss();

            }
        });
        bt_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (m_lat != 0) takePhoto();
                else
                    Toast.makeText(MainActivity.this, R.string.LocError, Toast.LENGTH_LONG).show();
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
        popupWindow.showAtLocation(popView, Gravity.BOTTOM, 0, 50);

    }

    //获取文件读取权限
    void pickFile() {
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                EnumClass.READ_EXTERNAL_STORAGE);
        int permissionCheck1 = ContextCompat.checkSelfPermission(this,
                EnumClass.WRITE_EXTERNAL_STORAGE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED || permissionCheck1 != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            EnumClass.READ_EXTERNAL_STORAGE, EnumClass.WRITE_EXTERNAL_STORAGE},
                    EnumClass.PERMISSION_CODE
            );

            return;
        }

        launchPicker();
    }

    //打开图片的文件管理器
    void launchPicker() {
        //Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        //intent.setData(Uri.parse(DEF_DIR));
        //intent.addCategory(Intent.CATEGORY_OPENABLE);
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        try {
            startActivityForResult(intent, EnumClass.REQUEST_CODE_PHOTO);
        } catch (ActivityNotFoundException e) {
            //alert user that file manager not working
            Toast.makeText(this, R.string.toast_pick_file_error, Toast.LENGTH_SHORT).show();
        }
    }
}
