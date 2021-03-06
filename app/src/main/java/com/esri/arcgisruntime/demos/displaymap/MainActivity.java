package com.esri.arcgisruntime.demos.displaymap;

import android.app.ActionBar;
import android.app.DownloadManager;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
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
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.esri.arcgisruntime.ArcGISRuntimeException;
import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureCollection;
import com.esri.arcgisruntime.data.FeatureCollectionTable;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.data.FeatureTable;
import com.esri.arcgisruntime.data.Field;
import com.esri.arcgisruntime.data.Geodatabase;
import com.esri.arcgisruntime.data.GeodatabaseFeatureTable;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.data.ShapefileFeatureTable;
import com.esri.arcgisruntime.geometry.AreaUnit;
import com.esri.arcgisruntime.geometry.AreaUnitId;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.GeodeticCurveType;
import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.GeometryBuilder;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.LinearUnit;
import com.esri.arcgisruntime.geometry.LinearUnitId;
import com.esri.arcgisruntime.geometry.Part;
import com.esri.arcgisruntime.geometry.PartCollection;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.PointCollection;
import com.esri.arcgisruntime.geometry.Polygon;
import com.esri.arcgisruntime.geometry.Polyline;
import com.esri.arcgisruntime.geometry.PolylineBuilder;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.ArcGISMapImageLayer;
import com.esri.arcgisruntime.layers.ArcGISMapImageSublayer;
import com.esri.arcgisruntime.layers.ArcGISTiledLayer;
import com.esri.arcgisruntime.layers.ArcGISVectorTiledLayer;
import com.esri.arcgisruntime.layers.FeatureCollectionLayer;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.layers.Layer;
import com.esri.arcgisruntime.layers.RasterLayer;
import com.esri.arcgisruntime.layers.SublayerList;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.location.LocationDataSource;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.LayerList;
import com.esri.arcgisruntime.mapping.MobileMapPackage;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.popup.Popup;
import com.esri.arcgisruntime.mapping.view.Callout;
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.IdentifyLayerResult;
import com.esri.arcgisruntime.mapping.view.LayerViewStateChangedEvent;
import com.esri.arcgisruntime.mapping.view.LayerViewStateChangedListener;
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
import com.esri.arcgisruntime.util.ListChangedEvent;
import com.esri.arcgisruntime.util.ListChangedListener;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.github.clans.fab.FloatingActionButton;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

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
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import lecho.lib.hellocharts.listener.PieChartOnValueSelectListener;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.PieChartView;

public class MainActivity extends AppCompatActivity {
    private MapView mMapView;
    private static final String TAG = "MainActivity";
    private List<layer> layerList = new ArrayList<>();
    private List<layer1> layers = new ArrayList<>();
    private DrawerLayout mDrawerLayout;

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
    List<Point> whiteBlankPts;
    GraphicsOverlay graphicsOverlay_9;
    GraphicsOverlay graphicsOverlay_10;
    PointCollection points = new PointCollection(SpatialReference.create(4521));;
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
    boolean MapQuery = false;
    private DisplayEnum QueryProcessType = DisplayEnum.NOQUERY;

    //FeatureLayer QueriedFeatureLayer;

    //private static final int INQUERY = -1;
    //private static final int FINISHQUERY = -2;
    //private static final int NOQUERY = -3;

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
        final xzqTreeAdapter adapter1 = new xzqTreeAdapter(xzqs);
        adapter1.setOnItemClickListener(new xzqTreeAdapter.OnRecyclerItemClickListener() {
            @Override
            public void onItemClick(View view, String xzqdm, final int position) {
                Intent intent = new Intent(MainActivity.this, chartshow.class);
                intent.putExtra("xzqdm", xzqdm);
                startActivity(intent);
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
        lp.alpha = 1f;
        getWindow().setAttributes(lp);
        popupWindow.showAtLocation(popView, Gravity.NO_GRAVITY,0,0);
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

    private void showPopueWindowForWhiteblank(){
        final View popView = View.inflate(this, R.layout.popupwindow_whiteblank,null);
        isWhiteBlank = true;
        whiteBlankPts = new ArrayList<>();
        FloatingActionButton back_pop = (FloatingActionButton) popView.findViewById(R.id.back_pop) ;
        back_pop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < graphics.size(); ++i){
                    graphicsOverlay_10.getGraphics().remove(graphics.get(i));
                }
                if (graphics.size() != 0) graphics.remove(graphics.size() - 1);
                graphicsOverlay_10.getGraphics().clear();
                for (int i = 0; i < graphics.size(); ++i){
                    graphicsOverlay_10.getGraphics().add(graphics.get(i));
                }
                mMapView.getGraphicsOverlays().remove(graphicsOverlay_10);
                mMapView.getGraphicsOverlays().add(graphicsOverlay_10);
                List<whiteblank> whiteblanks = LitePal.findAll(whiteblank.class);
                LitePal.delete(whiteblank.class, whiteblanks.size());
            }
        });
        FloatingActionButton fff = (FloatingActionButton) popView.findViewById(R.id.colorSeeker_pop);
        fff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorPickerDialogBuilder
                        .with(MainActivity.this)
                        .setTitle(R.string.ChooseColor)
                        .initialColor(Color.RED)
                        .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                        .density(12)
                        .setOnColorSelectedListener(new OnColorSelectedListener() {
                            @Override
                            public void onColorSelected(int selectedColor) {
                            }
                        })
                        .setPositiveButton(R.string.Confirm, new ColorPickerClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                                //changeBackgroundColor(selectedColor);
                                color_Whiteblank = selectedColor;
                            }
                        })
                        .setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .build()
                        .show();
            }
        });

        FloatingActionButton eraseContent = (FloatingActionButton) popView.findViewById(R.id.eraseContent);
        eraseContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //try {
                    //mMapView.getGraphicsOverlays().remove(graphicsOverlay_10);
                Log.w(TAG, "onClick: " + mMapView.getGraphicsOverlays().size());
                /*points.clear();
                while (mMapView.getGraphicsOverlays().size() != 0){
                    for (int i = 0; i < mMapView.getGraphicsOverlays().size(); ++i){
                        mMapView.getGraphicsOverlays().remove(i);
                    }
                }*/


                /*graphics.clear();
                graphicsOverlay_10.getGraphics().clear();
                mMapView.getGraphicsOverlays().remove(graphicsOverlay_10);
                mMapView.getGraphicsOverlays().add(graphicsOverlay_10);*/
                LitePal.deleteAll(whiteblank.class);
                drawGraphicsOverlayer();
                Toast.makeText(MainActivity.this, R.string.EraseFinish, Toast.LENGTH_SHORT).show();
                /*}catch (Exception e){
                    Toast.makeText(MainActivity.this, "已经清空白板", Toast.LENGTH_SHORT).show();
                    Log.w(TAG, "onClick: " + e.toString());
                }*/
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
                //PointCollection points = new PointCollection(SpatialReference.create(4521));
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
                        wb.setPts(pts);
                        wb.setColor(color_Whiteblank);
                        wb.save();
                        points.clear();
                        //whiteBlankPts.clear();
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
                        Point wgs84Point = (Point) GeometryEngine.project(mapPoint, SpatialReference.create(4521));
                        //whiteBlankPts.add(wgs84Point);
                        //int size = whiteBlankPts.size();
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
                pointCollection = new PointCollection(SpatialReference.create(4521));
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
                pointCollection = new PointCollection(SpatialReference.create(4521));
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
                                    geometry = GeometryEngine.intersection(GeometryEngine.project(polygon1, SpatialReference.create(4521)), polygon);
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
                            mCallout.setLocation(new Point(geometry1.getExtent().getCenter().getX(), geometry1.getExtent().getYMax(), SpatialReference.create(4521)));
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
                                Geometry geometry1 = polygon;
                                List<QueryTaskInfo> queryTaskInfos = new ArrayList<>();
                                HashMap<String, ChartQueryResult> hashMap = new HashMap<>();
                                //遍历被查询到的要素
                                for (Object element : featureResul) {
                                    if (element instanceof Feature) {
                                        Feature mFeatureGrafic = (Feature) element;
                                        Geometry geometry = null;
                                        Polygon polygon1 = (Polygon) mFeatureGrafic.getGeometry();
                                        geometry = GeometryEngine.intersection(GeometryEngine.project(polygon1, SpatialReference.create(4521)), polygon);
                                        boolean isOK = false;
                                        Double area = GeometryEngine.areaGeodetic(geometry, new AreaUnit(AreaUnitId.SQUARE_KILOMETERS), GeodeticCurveType.GEODESIC) * 1500;
                                        QueryTaskInfo queryTaskInfo = new QueryTaskInfo(area);
                                        Log.w(TAG, "geometry2type: " + queryTaskInfo.getArea());
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
                                mCallout.setLocation(new Point(geometry1.getExtent().getCenter().getX(), geometry1.getExtent().getYMax(), SpatialReference.create(4521)));
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

    private void queryTaskFor20200904(final QueryParameters query, final Polygon polygon){
        try {
            if (QueriedLayerIndex != -1 && QueriedLayerIndex < BaseLayerFieldsSheetList.size()) {
                FeatureTable mTable = LayerFieldsSheetList.get(QueriedLayerIndex).getFeatureLayer().getFeatureTable();
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
                                        geometry = GeometryEngine.intersection(GeometryEngine.project(polygon1, SpatialReference.create(4521)), polygon);
                                        boolean isOK = false;
                                        Double area = GeometryEngine.areaGeodetic(geometry, new AreaUnit(AreaUnitId.SQUARE_KILOMETERS), GeodeticCurveType.GEODESIC) * 1500;
                                        QueryTaskInfo queryTaskInfo = new QueryTaskInfo(area);
                                        Log.w(TAG, "geometry2type: " + queryTaskInfo.getArea());
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
                                mCallout.setLocation(new Point(geometry1.getExtent().getCenter().getX(), geometry1.getExtent().getYMax(), SpatialReference.create(4521)));
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
                } else
                    Toast.makeText(MainActivity.this, "三个点以上才可进行多边形查询，请重试！", Toast.LENGTH_SHORT).show();
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
                                geometry = GeometryEngine.intersection(GeometryEngine.project(polygon1, SpatialReference.create(4521)), polygon);
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
                                geometry = GeometryEngine.intersection(GeometryEngine.project(polygon1, SpatialReference.create(4521)), polygon);
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
                            data = "图层:土地规划地类," + data;
                        }
                        else if (text.equals("二调地类图斑")) {
                            isQuery09DLTB = true;
                            data = "图层:09地类图斑," + data;
                        }
                        else if (text.equals("生态保护红线")) {
                            isQuery16DLTB = true;
                            data = "图层:16地类图斑," + data;
                        }
                        else if (text.equals("农村土地承包经营权")) {
                            isQuery17DLTB = true;
                            data = "图层:17地类图斑," + data;
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
            for (int i = 0; i < LayerFieldsSheetList.size(); i++) {
                FeatureLayer fl = LayerFieldsSheetList.get(i).getFeatureLayer();
                querySingleTaskForPolygon(query, polygon, LayerFieldsSheetList.get(i));
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
        Log.w(TAG, "isOkForPopWindow: " + isQueryTDGHDL + isQuery09DLTB + isQuery16DLTB + isQuery17DLTB + isQueryJBNTBHQ);
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

    private void loadRaster(final String path) {
        // create a raster from a local raster file
        Raster raster = new Raster(path);
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
                initLayerList(path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf(".")));
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

    private void featureLayerShapefile(String path) {
        // load the shapefile with a local path
        final ShapefileFeatureTable shapefileFeatureTable = new ShapefileFeatureTable(path);


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
                                geometry = GeometryEngine.intersection(GeometryEngine.project(polygon1, SpatialReference.create(4521)), polygon);
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
                        mCallout.setLocation(new Point(geometry1.getExtent().getCenter().getX(), geometry1.getExtent().getYMax(), SpatialReference.create(4521)));
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

    private void readXZQ(){
        File file1 = new File(Environment.getExternalStorageDirectory().toString() + "/临沧市行政区.txt");
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

    private void mapQueryBtEvent(){
        if (!MapQuery) {
            RunningFunction = DisplayEnum.FUNC_ANA;
            MapQuery = true;
            ScaleShow.setText("云南省地图院数据分院研发 当前比例  1 : " + String.valueOf((int)mMapView.getMapScale()) + " (图面查询中)");
            Toast.makeText(MainActivity.this, R.string.OpenMapQuery, Toast.LENGTH_LONG).show();
        }
        else {
            RunningFunction = DisplayEnum.FUNC_NONE;
            MapQuery = false;
            mCallout.dismiss();
            mMapView.getGraphicsOverlays().clear();
            drawGraphicsOverlayer();
            ScaleShow.setText("云南省地图院数据分院研发 当前比例  1 : " + String.valueOf((int)mMapView.getMapScale()));
            pieChartView.setVisibility(View.GONE);
            Toast.makeText(MainActivity.this, R.string.CloseMapQuery, Toast.LENGTH_LONG).show();
            RemoveFinishButton();
        }
    }

    private void initWidgetAndVariable(){
        initWidget();
        initVariable();
    }

    private void initWidget(){
        recyclerViewForP = (RecyclerView) findViewById(R.id.recyclerViewForP);

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
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        pieChartView = (PieChartView) findViewById(R.id.chart);
        //按钮添加要素
        DrawFeature = (FloatingActionButton)findViewById(R.id.DrawFeature);
        DrawFeature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                        Toast.makeText(MainActivity.this, format.format(GeometryEngine.lengthGeodetic(new Polyline(pointCollection, SpatialReference.create(4521)), new LinearUnit(LinearUnitId.METERS), GeodeticCurveType.GEODESIC)) + "米", Toast.LENGTH_SHORT).show();
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
                    //mMapView.setViewpointCenterAsync(OriginLocation, 1200000);
                    mMapView.setViewpointCenterAsync(OriginLocation, 1500000);
                    //mMapView.setViewpointScaleAsync(1200000);
                    //North.setRotation(0);
                    mMapView.setViewpointRotationAsync(0);
                }else {
                    //mMapView.setViewpointCenterAsync(new Point(102.715507, 25.038112, 0.000000, SpatialReference.create(4326)), 2000000);
                    mMapView.setViewpointCenterAsync(new Point(99.6178, 23.9106, 0, SpatialReference.create(4326)), 1500000);
                    // TODO
                    //North.setRotation(0);
                    mMapView.setViewpointRotationAsync(0);
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
        /*recyclerViewButton = (ImageButton) findViewById(R.id.openRecyclerView);
        recyclerViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isClick){
                    recyclerView.setVisibility(View.GONE);
                    recyclerViewButton.setBackgroundResource(R.drawable.ic_expand_more_black_24dp);
                    recyclerViewButton.setY(recyclerView.getY());
                    isClick = false;
                    //recyclerViewButton.setX(recyclerView.getX());
                }else {
                    //setRecyclerView();
                    recyclerView.setVisibility(View.VISIBLE);
                    recyclerViewButton.setVisibility(View.GONE);
                    TimerTask task = new TimerTask() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    recyclerViewButton.setY(recyclerView.getY() + recyclerView.getHeight() + 20);
                                    recyclerViewButton.setBackgroundResource(R.drawable.ic_expand_less_black_24dp);
                                    recyclerViewButton.setVisibility(View.VISIBLE);
                                }
                            });
                        }
                    };
                    Timer timer = new Timer();
                    timer.schedule(task, 1000);
                    //recyclerViewButton1.setVisibility(View.VISIBLE);
                    //recyclerViewButton.setVisibility(View.GONE);
                    isClick = true;
                }
                //Log.w(TAG, "onClick: " + recyclerViewButton.getBackground().toString() );
            }
        });*/
        mMapView = findViewById(R.id.mapView);// = new ArcGISMap(Basemap.Type.TOPOGRAPHIC, 34.056295, -117.195800, 16);
        mMapView.setOnTouchListener(new DefaultMapViewOnTouchListener(this, mMapView) {
            @Override
            public boolean  onSingleTapConfirmed(MotionEvent v) {
                Log.d(TAG, "onSingleTapConfirmed: " + v.toString());
                // get the point that was clicked and convert it to a point in map coordinates
                final android.graphics.Point screenPoint = new android.graphics.Point(Math.round(v.getX()),
                        Math.round(v.getY()));
                // create a map point from screen point
                final Point mapPoint = mMapView.screenToLocation(screenPoint);
                // convert to WGS84 for lat/lon format
                final Point wgs84Point = (Point) GeometryEngine.project(mapPoint, SpatialReference.create(4521));
                /*if (numx == 0) {
                    Point pt = mMapView.screenToLocation(new android.graphics.Point(Math.round(mMapView.getWidth() / 2), Math.round(((mMapView.getTop() + getStatusBarHeight(MainActivity.this) + getDaoHangHeight(MainActivity.this)) + (mMapView.getBottom() + getStatusBarHeight(MainActivity.this) + getDaoHangHeight(MainActivity.this))) / 2)));
                    //Log.w(TAG, "run: " + pt.getX() + "; " + pt.getY());
                    OriginLocation = (Point) GeometryEngine.project(pt, SpatialReference.create(4521));
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
                        mCallout.dismiss();
                        pieChartView.setVisibility(View.GONE);
                        // center on tapped point
                        //mMapView.setViewpointCenterAsync(wgs84Point);
                        Log.w(TAG, "onSingleTapConfirmed: " + wgs84Point);
                        inMap = false;
                        Log.w(TAG, "onSingleTapConfirmed: ");

                        Log.w(TAG, "onSingleTapConfirmed: " + mapPoint);
                        if (!queryPoi(mMapView.locationToScreen(clickPoint)))
                            if (!inTuban(clickPoint)) {
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
                            Graphic fillGraphic = new Graphic(new Point(pointCollection.get(0).getX(), pointCollection.get(0).getY(), SpatialReference.create(4521)), lineSymbol);
                            graphicsOverlay_1.getGraphics().add(fillGraphic);
                            mMapView.getGraphicsOverlays().add(graphicsOverlay_1);
                        } else if (pointCollection.size() == 2) {
                            removeGraphicsOverlayers();
                            GraphicsOverlay graphicsOverlay_1 = new GraphicsOverlay();
                            SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.GREEN, 3);
                            Graphic fillGraphic = new Graphic(new Polyline(pointCollection, SpatialReference.create(4521)), lineSymbol);
                            graphicsOverlay_1.getGraphics().add(fillGraphic);
                            mMapView.getGraphicsOverlays().add(graphicsOverlay_1);
                        } else if (pointCollection.size() > 2) {
                            removeGraphicsOverlayers();
                            GraphicsOverlay graphicsOverlay_1 = new GraphicsOverlay();
                            SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.GREEN, 3);
                            SimpleFillSymbol fillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.HORIZONTAL, Color.GREEN, lineSymbol);
                            switch (RunningAnalyseFunction) {
                                case ANA_NEED:
                                    Graphic fillGraphic = new Graphic(new Polygon(pointCollection, SpatialReference.create(4521)), lineSymbol);
                                    graphicsOverlay_1.getGraphics().add(fillGraphic);
                                    mMapView.getGraphicsOverlays().add(graphicsOverlay_1);
                                    break;
                                case ANA_AREA:
                                    fillGraphic = new Graphic(new Polygon(pointCollection, SpatialReference.create(4521)), fillSymbol);
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
                            Graphic fillGraphic = new Graphic(new Point(pointCollection.get(0).getX(), pointCollection.get(0).getY(), SpatialReference.create(4521)), lineSymbol);
                            graphicsOverlay_1.getGraphics().add(fillGraphic);
                            mMapView.getGraphicsOverlays().add(graphicsOverlay_1);
                        } else if (pointCollection.size() >= 2) {
                            removeGraphicsOverlayers();
                            GraphicsOverlay graphicsOverlay_1 = new GraphicsOverlay();
                            SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.GREEN, 3);
                            Graphic fillGraphic = new Graphic(new Polyline(pointCollection, SpatialReference.create(4521)), lineSymbol);
                            graphicsOverlay_1.getGraphics().add(fillGraphic);
                            mMapView.getGraphicsOverlays().add(graphicsOverlay_1);
                            DecimalFormat format = new DecimalFormat("0.00");
                            Toast.makeText(MainActivity.this, format.format(GeometryEngine.lengthGeodetic(new Polyline(pointCollection, SpatialReference.create(4521)), new LinearUnit(LinearUnitId.METERS), GeodeticCurveType.GEODESIC)) + "米", Toast.LENGTH_SHORT).show();
                        }
                    } else if (DrawType == DisplayEnum.DRAW_POINT) {
                        GraphicsOverlay graphicsOverlay_1 = new GraphicsOverlay();
                        SimpleMarkerSymbol pointSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.DIAMOND, Color.RED, 10);
                        Graphic fillGraphic = new Graphic(wgs84Point, pointSymbol);
                        graphicsOverlay_1.getGraphics().add(fillGraphic);
                        mMapView.getGraphicsOverlays().add(graphicsOverlay_1);
                    }
                }else if (RunningFunction == DisplayEnum.FUNC_ADDPOI){
                    Log.w(TAG, "onSingleTapConfirmed: " + wgs84Point.getX() + "; " + wgs84Point.getY());
                    final Point wgs84Point1 = (Point) GeometryEngine.project(wgs84Point, SpatialReferences.getWgs84());
                    Log.w(TAG, "onSingleTapConfirmed: " + wgs84Point1.getX() + "; " + wgs84Point1.getY());
                    final PointF wgs84Point2 = new PointF((float) wgs84Point1.getY(), (float)wgs84Point1.getX());
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
                }
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
                if (!MapQuery) ScaleShow.setText("云南省地图院数据分院研发 当前比例  1 : " + String.valueOf((int)mMapView.getMapScale()));
                else ScaleShow.setText("云南省地图院数据分院研发 当前比例  1 : " + String.valueOf((int)mMapView.getMapScale()) + " (图面查询中)");
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
                            /*List<POI> pois = LitePal.findAll(POI.class);
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
                            DataUtil.makeKML();
                            Log.w(TAG, "runlzy: " + types.size());
                            if (types.size() > 0) {
                                for (int i = 0; i < types.size(); ++i) {
                                    DataUtil.makeTxt(types.get(i));
                                }
                            }else DataUtil.makeTxt("");
                            DataUtil.makeTxt1();
                            //DataUtil.makeWhiteBlankKML();
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
                            File file = new File(Environment.getExternalStorageDirectory() + "/TuZhi/" + "/Output");
                            if (!file.exists() && !file.isDirectory()){
                                file.mkdirs();
                            }
                            final String outputPath = Long.toString(System.currentTimeMillis());
                            File file1 = new File(Environment.getExternalStorageDirectory() + "/TuZhi/" + "/Output",  outputPath + ".dtdb");
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
                                File zipFile = new File(Environment.getExternalStorageDirectory() + "/TuZhi/" + "/Output",  outputPath + ".zip");
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
                                        //toolbar.setTitle(MainActivity.this.getResources().getText(R.string.MapList));
                                    }
                                });
                            }catch (IOException e){
                                Toast.makeText(MainActivity.this, MainActivity.this.getResources().getText(R.string.OpenFileError) + "_2", Toast.LENGTH_SHORT).show();
                                Log.w(TAG, "run: " + e.toString());
                                Log.w(TAG, "run: " + e.getMessage());
                            }*/


                        }

                    }).start();
                }catch (Exception e)
                {
                    Log.w(TAG, "error: " + e.toString());
                }
                //setFAMVisible(false);
            }
        });
    }



    private void OutputData(){
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
        DataUtil.makeKML();
        Log.w(TAG, "runlzy: " + types.size());
        if (types.size() > 0) {
            for (int i = 0; i < types.size(); ++i) {
                DataUtil.makeTxt(types.get(i));
            }
        }else DataUtil.makeTxt("");
        DataUtil.makeTxt1();
        DataUtil.makeWhiteBlankKML();
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
            //sb.append("<m_ic>").append(lines_whiteBlanks.get(i).getIc()).append("</m_ic>").append("\n");
            sb.append("<m_lines>").append(lines_whiteBlanks.get(i).getPts()).append("</m_lines>").append("\n");
            sb.append("<m_color>").append(lines_whiteBlanks.get(i).getColor()).append("</m_color>").append("\n");
        }
        sb.append("</Lines_WhiteBlank>").append("\n");
        File file = new File(Environment.getExternalStorageDirectory() + "/TuZhi/" + "/Output");
        if (!file.exists() && !file.isDirectory()){
            file.mkdirs();
        }
        final String outputPath = Long.toString(System.currentTimeMillis());
        File file1 = new File(Environment.getExternalStorageDirectory() + "/TuZhi/" + "/Output",  outputPath + ".dtdb");
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
            File zipFile = new File(Environment.getExternalStorageDirectory() + "/TuZhi/" + "/Output",  outputPath + ".zip");
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
            Point mpt0 = (Point) GeometryEngine.project(new Point((double)poii.getM_Y(), (double)poii.getM_X(), SpatialReferences.getWgs84()), SpatialReference.create(4521));
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
                Point mpt1 = (Point) GeometryEngine.project(new Point((double)poi.getM_Y(), (double)poi.getM_X(), SpatialReferences.getWgs84()), SpatialReference.create(4521));
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

    private boolean queryUserTB(String path, FeatureLayer featureLayer, final Point clickPoint, final Point mapPoint){
        QueryParameters query = new QueryParameters();
        query.setGeometry(clickPoint);// 设置空间几何对象
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
                                    AnalyseGeometry(geometry);
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

    private void AnalyseGeometry(final Geometry geometry){
        final Polygon polygon = (Polygon)geometry;
        final FloatingActionButton finish = (FloatingActionButton) findViewById(R.id.FinishQuery);
        finish.setVisibility(View.VISIBLE);
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                analyseFunctionForBasePolygon((Polygon) geometry);
                //NeedQueryForBasePolygon((Polygon) geometry);

                /*try {
                    //PartCollection parts = new PartCollection(polygon.getParts(), SpatialReference.create(4521));
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
        if (isFileExist(StaticVariableEnum.LCDKYROOTPATH) & MapQuery) {
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
                    Log.w(TAG, "queryTBFor20200903: " + featureQueryResult);

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
                                        Log.w(TAG, "run: callout" + mCallout.isShowing());
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
        if (!inMap) mCallout.dismiss();
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
                                        //Toast.makeText(MainActivity.this, GeometryEngine.within(mapPoint, GeometryEngine.project(geometry, SpatialReference.create(4521))) + "", Toast.LENGTH_SHORT).show();
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
        for (int i = size - 1, j = 0; i > -1; i--){
            Log.w(TAG, "initLayerList: " + map.getOperationalLayers().get(i).getName());
            if (!map.getOperationalLayers().get(i).getName().contains(".tpk")) {
                layers.add(new layer1(map.getOperationalLayers().get(i), i));
                if (map.getOperationalLayers().get(i).getName().isEmpty()){
                    List<UserLayer> tifList = LitePal.where("type = ?", Integer.toString(UserLayer.TIF_FILE)).find(UserLayer.class);
                    layerList.add(new layer(tifList.get(j).getName(), tifList.get(j).getPath(), map.getOperationalLayers().get(i).isVisible()));
                    ++j;
                }
                else
                {
                    if (map.getOperationalLayers().get(i) instanceof FeatureLayer) {
                        FeatureLayer fl = (FeatureLayer) (map.getOperationalLayers().get(i));
                        FeatureTable ft = fl.getFeatureTable();
                        if (ft instanceof ShapefileFeatureTable) {
                            ShapefileFeatureTable sfft = (ShapefileFeatureTable) ft;
                            layerList.add(new layer(map.getOperationalLayers().get(i).getName(), sfft.getPath(), map.getOperationalLayers().get(i).isVisible()));
                        } else
                            layerList.add(new layer(map.getOperationalLayers().get(i).getName(), "", map.getOperationalLayers().get(i).isVisible()));
                    }
                    else
                        layerList.add(new layer(map.getOperationalLayers().get(i).getName(), "", map.getOperationalLayers().get(i).isVisible()));
                }
                if (map.getOperationalLayers().get(i).getName().contains("土地规划地类")) {
                    //ArcGISVectorTiledLayer mVectorTiledLayer = (ArcGISVectorTiledLayer) map.getOperationalLayers().get(i);
                    //Log.w(TAG, "initLayerList: " + "omg");
                }
            }else {
                //Log.w(TAG, "initLayerList: " + map.getOperationalLayers().get(i).getName());
                hasTPK = true;
                if (!map.getOperationalLayers().get(i).getName().contains("临沧市中心城区影像"))
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

    }

    private void initLayerList(String name){
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
                if (!map.getOperationalLayers().get(i).getName().contains("临沧市中心城区影像"))
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
    }

    private void initSurfaceCenterPoint(final MobileMapPackage mainMobileMapPackage){
        OriginScale = mMapView.getMapScale();
        if (mainMobileMapPackage.getPath().toString().contains("临沧")) {
            Log.w(TAG, "run: " + mainMobileMapPackage.getPath().toString());
            // TODO 初始比例尺
            //OriginLocation = new Point(99.626302, 23.928384, 0, SpatialReference.create(4326));
            //新版本
            //OriginLocation = new Point(99.9478, 24.9860, 0, SpatialReference.create(4326));
            //新版本2020/9/2
            OriginLocation = new Point(99.6178, 23.9106, 0, SpatialReference.create(4326));

            //mMapView.setViewpointCenterAsync(OriginLocation);
            //mMapView.setViewpointScaleAsync(1200000);
            //mMapView.setViewpointCenterAsync(OriginLocation, 1200000);
            mMapView.setViewpointCenterAsync(OriginLocation, 1500000);
        }else mMapView.setViewpointScaleAsync(100000);
    }

    private List<Layer> BaseMMPKLayer;
    private void readMMPKData(){
        BaseMMPKLayer = new ArrayList<>();
        Log.w(TAG, "readMMPKData: " + StaticVariableEnum.MMPKROOTPATH );
        final MobileMapPackage mainMobileMapPackage = new MobileMapPackage(StaticVariableEnum.MMPKROOTPATH);
        mainMobileMapPackage.loadAsync();
        mainMobileMapPackage.addDoneLoadingListener(new Runnable() {
            @Override
            public void run() {
                LoadStatus mainLoadStatus = mainMobileMapPackage.getLoadStatus();
                if (mainLoadStatus == LoadStatus.LOADED) {
                    List<ArcGISMap> mainArcGISMapL = mainMobileMapPackage.getMaps();
                    map = mainArcGISMapL.get(0);
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

                }else mainMobileMapPackage.loadAsync();
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

        // TODO 2020/9/2 读取地矿院使用的新数据
        readLCOutsideWorkDataForGDB();
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
        DrawFeature.setVisibility(View.VISIBLE);
        LocHereBT.setVisibility(View.VISIBLE);
        ResetBT.setVisibility(View.VISIBLE);
        FloatingActionButton outputbt = (FloatingActionButton) findViewById(R.id.OutputData);
        outputbt.setVisibility(View.VISIBLE);
        FloatingActionButton InputDataBt = findViewById(R.id.InputData);
        InputDataBt.setVisibility(View.VISIBLE);
    }

    private void removeStandardWidget(){
        // TODO 2020/9/4 各区域占比
        whiteBlank_fab.setVisibility(View.GONE);
        MapQueryBT.setVisibility(View.GONE);
        DrawFeature.setVisibility(View.GONE);
        LocHereBT.setVisibility(View.GONE);
        ResetBT.setVisibility(View.GONE);

        FloatingActionButton outputbt = (FloatingActionButton) findViewById(R.id.OutputData);
        outputbt.setVisibility(View.GONE);

        FloatingActionButton InputDataBt = findViewById(R.id.InputData);
        InputDataBt.setVisibility(View.GONE);
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
                    Graphic fillGraphic = new Graphic(new Point(pointCollection.get(0).getX(), pointCollection.get(0).getY(), SpatialReference.create(4521)), lineSymbol);
                    graphicsOverlay_1.getGraphics().add(fillGraphic);
                    mMapView.getGraphicsOverlays().add(graphicsOverlay_1);
                }else if (pointCollection.size() == 2){
                    GraphicsOverlay graphicsOverlay_1 = new GraphicsOverlay();
                    SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.GREEN, 3);
                    Graphic fillGraphic = new Graphic(new Polyline(pointCollection, SpatialReference.create(4521)), lineSymbol);
                    graphicsOverlay_1.getGraphics().add(fillGraphic);
                    mMapView.getGraphicsOverlays().add(graphicsOverlay_1);
                }else if (pointCollection.size() > 2){
                    GraphicsOverlay graphicsOverlay_1 = new GraphicsOverlay();
                    SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.GREEN, 3);
                    SimpleFillSymbol fillSymbol;
                    Graphic fillGraphic;
                    switch (RunningAnalyseFunction){
                        case ANA_DISTANCE:
                            fillGraphic = new Graphic(new Polyline(pointCollection, SpatialReference.create(4521)), lineSymbol);
                            graphicsOverlay_1.getGraphics().add(fillGraphic);
                            mMapView.getGraphicsOverlays().add(graphicsOverlay_1);
                            break;
                        case ANA_AREA:
                            fillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.HORIZONTAL, Color.GREEN, lineSymbol);
                            fillGraphic = new Graphic(new Polygon(pointCollection, SpatialReference.create(4521)), fillSymbol);
                            graphicsOverlay_1.getGraphics().add(fillGraphic);
                            mMapView.getGraphicsOverlays().add(graphicsOverlay_1);
                            break;
                        case ANA_NEED:
                            fillGraphic = new Graphic(new Polygon(pointCollection, SpatialReference.create(4521)), lineSymbol);
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
                                    calloutContent.setText(str + "平方米");
                                }
                                else {
                                    calloutContent.setText(str + getResources().getString(R.string.SQUARE_KILOMETERS));
                                }
                                //calloutContent.setText(str);
                                // get callout, set content and show
                                mCallout.setLocation(new Point(polygon.getExtent().getCenter().getX(), polygon.getExtent().getYMax(), SpatialReference.create(4521)));
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
                                String str = df.format(length);
                                Log.w(TAG, "onClick: " + str);
                                TextView calloutContent = new TextView(getApplicationContext());
                                calloutContent.setTextColor(Color.BLACK);
                                calloutContent.setText(str + getResources().getString(R.string.KILOMETERS));
                                //calloutContent.setText(str + R.string.SQUARE_KILOMETERS);
                                // get callout, set content and show
                                mCallout.setLocation(new Point(polyline.getExtent().getCenter().getX(), polyline.getExtent().getYMax(), SpatialReference.create(4521)));
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
                queryTaskFor20200904ForBasePolygon(query, polygon);
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
                        queryTaskFor20200904(query, polygon);
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

    private void showPopWindowForListShow(){
        final FloatingActionButton popListShow = (FloatingActionButton) findViewById(R.id.PopWindow);
        popListShow.setVisibility(View.VISIBLE);
        popListShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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

    private void saveTbData(String PointCollection, String data){
        String name = "图斑" + LitePal.findAll(my_tb.class).size();
        my_tb my_tb = new my_tb();
        my_tb.setPointCollection(PointCollection);
        my_tb.setMdata(data);
        my_tb.setName(name);
        my_tb.save();
        Toast.makeText(MainActivity.this, name + "已经保存", Toast.LENGTH_SHORT).show();
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
        SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.RED, 3);
        for (int i = 0; i < currentMyTuban.size(); ++i){
            Polygon polygon = new Polygon(currentMyTuban.get(i).getPoints());
            Graphic g = new Graphic(polygon, lineSymbol);
            graphics.add(g);
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
        for (int i = 0; i < myTbs.size(); ++i){
            String mpointCollection = myTbs.get(i).getPointCollection();
            PointCollection pointCollection = new PointCollection(SpatialReference.create(4521));
            Log.w(TAG, "queryMyTuban: " + mpointCollection);
            String[] mPoint = mpointCollection.split(";");
            for (int j = 0; j < mPoint.length; ++j){
                String[] mPoint1 = mPoint[j].split(",");
                pointCollection.add(new Point(Double.valueOf(mPoint1[0]), Double.valueOf(mPoint1[1])));
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
        pointCollection = new PointCollection(SpatialReference.create(4521));
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
        drawGeometry(new Polygon(pointCollection, SpatialReference.create(4521)));
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
        PartCollection parts = new PartCollection(polygon.getParts(), SpatialReference.create(4521));
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

    private boolean inTuban(Point pt){
        for (int i = 0; i < currentMyTuban.size(); ++i){
            Polygon polygon = new Polygon(currentMyTuban.get(i).getPoints());
            if (GeometryEngine.within(pt, polygon)){
                queriedMyTuban = currentMyTuban.get(i);
                analyseFunction(queriedMyTuban.getPoints());
                return true;
            }
        }
        return false;
    }

    public void drawGraphicsOverlayer(){
        initialiseGraphics();
        updatePoiAndWhiteBlank();
        parseAndUpdateMyTuban();
        updateGraphicsOverlayer();
    }

    public void initialiseGraphics(){
        for (int i = 0; i < graphics.size(); ++i){
            graphicsOverlay_10.getGraphics().remove(graphics.get(i));
        }
        graphics.clear();
    }

    public void updatePoiAndWhiteBlank(){
        List<whiteblank> whiteblanks = LitePal.findAll(whiteblank.class);
        List<POI> pois = LitePal.findAll(POI.class);
        int wbsize = whiteblanks.size();
        int psize = pois.size();
        int size = wbsize + psize;

        if (size > 0){
            if (wbsize > 0) updateWhiteBlank(whiteblanks, wbsize);
            if (psize > 0) updatepoi(pois, psize);
        }
    }

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
                    Point wgs84Point = (Point) GeometryEngine.project(new Point(Double.valueOf(strings1[0]), Double.valueOf(strings1[1])), SpatialReference.create(4521));
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
                        Point wgs84Point = (Point) GeometryEngine.project(new Point(Double.valueOf(strings1[0]), Double.valueOf(strings1[1])), SpatialReference.create(4521));
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
            SimpleMarkerSymbol makerSymbol;
            int tapenum = pois.get(i).getTapenum();
            int photonum = pois.get(i).getPhotonum();
            /*if (photonum == 0){
                if (tapenum == 0){

                }else {

                }
            }else {
                if (tapenum == 0){

                }else {

                }
            }*/
            if (tapenum == 0 && photonum == 0){
                makerSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.RED, 20);
            }else if (tapenum > 0 && photonum > 0){
                makerSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE,  Color.GREEN, 20);
            }else {
                makerSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.YELLOW, 20);
            }
            Log.w(TAG, "drawWhiteBlank2: " + pois.get(i).getX() + "; " + pois.get(i).getY());
            Point wgs84Point = (Point) GeometryEngine.project(new Point(Double.valueOf(pois.get(i).getY()), Double.valueOf(pois.get(i).getX()), SpatialReferences.getWgs84()), SpatialReference.create(4521));
            Log.w(TAG, "drawWhiteBlank2: " + wgs84Point.getX() + "; " + wgs84Point.getY());
            Graphic g = new Graphic(wgs84Point, makerSymbol);
            graphics.add(g);
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
                            showListPopupWindowforListViewFor20200903(searchView, query);

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
        query1.setWhereClause("upper(ZLDWMC) LIKE '%" + searchString.toUpperCase() + "%'");
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
                        setRecyclerViewForP();
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
                                    mPolygon = GeometryEngine.project(polygon1, SpatialReference.create(4521));
                                else
                                    mPolygon = GeometryEngine.union(GeometryEngine.project(polygon1, SpatialReference.create(4521)), mPolygon);
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

    private void setRecyclerView(){
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
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
                        map.getOperationalLayers().get(layers.get(position).getNum()).setVisible(false);
                        for (int i = 0; i < layerList.size(); ++i){
                            if (name.equals(layerList.get(i).getName())) layerList.get(i).setStatus(false);
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
                        map.getOperationalLayers().get(layers.get(position).getNum()).setVisible(true);
                        for (int i = 0; i < layerList.size(); ++i){
                            if (name.equals(layerList.get(i).getName())) layerList.get(i).setStatus(true);
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
            Log.w(TAG, "onClick: 2020/9/7: " + name);
            holder.cardView.setCardBackgroundColor(Color.RED);
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("提示")
                    .setMessage("删除该用户图层吗？")
                    .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            RemoveUserLayer(name, path);
                            setRecyclerViewForDynamicChooseFrame();
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

    private RecyclerView recyclerViewForP;
    private void setRecyclerViewForDynamicChooseFrame(){
        //final RecyclerView recyclerViewForP = (RecyclerView) findViewById(R.id.recyclerViewForP);
        GridLayoutManager layoutManager = new GridLayoutManager(this,1);
        recyclerViewForP.setLayoutManager(layoutManager);

        final DynamicChooseFrameAdapter adapter = new DynamicChooseFrameAdapter(LayerFieldsSheetList);
        adapter.setOnItemCheckListener(new DynamicChooseFrameAdapter.OnRecyclerItemCheckListener() {
            @Override
            public void onItemCheckClick(DynamicChooseFrameAdapter.ViewHolder holder, String name, int position) {
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
                    queryTaskFor20200904(query, polygon);
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
            Boolean isDeleted = false;
            List<UserLayer> list = LitePal.where("type = ?", Integer.toString(UserLayer.SHP_FILE)).find(UserLayer.class);
            ;
            for (int i = 0; i < list.size(); ++i) {
                String mpath = list.get(i).getPath();
                String name = list.get(i).getName();
                Log.w(TAG, "RemoveUserLayer: " + mpath + ", " + name + ", " + pName + ", " + path);


                if (mpath.equals(path)) {
                    LitePal.deleteAll(UserLayer.class, "path = ?", mpath);
                    list.remove(i);
                    --i;
                    isDeleted = true;
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

            if (!isDeleted) {
                list = LitePal.where("type = ?", Integer.toString(UserLayer.TIF_FILE)).find(UserLayer.class);
                Log.w(TAG, "run 2020/9/2: " + list.get(0).getName());
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
                    queryTaskFor20200904(query, polygon);
                // TODO 2020/9/4 完成新的按需查询内容
                //querySingleTaskForPolygon(query, polygon, BaseLayerFieldsSheetList.get(1));
            } catch (ArcGISRuntimeException e) {
                Toast.makeText(MainActivity.this, e.getMessage() + e.getErrorCode(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setRecyclerViewForP(){
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerViewForP);
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
                        UserLayer userLayer = new UserLayer(shp_path.substring(shp_path.lastIndexOf("/") + 1, shp_path.lastIndexOf(".")), shp_path, UserLayer.SHP_FILE);

                        userLayer.save();
                        //useUserLayer();
                        showUserLayer(userLayer);
                        Toast.makeText(MainActivity.this, shp_path, Toast.LENGTH_LONG).show();
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
                        UserLayer userLayer1 = new UserLayer(tif_path.substring(tif_path.lastIndexOf("/") + 1, tif_path.lastIndexOf(".")), tif_path, UserLayer.TIF_FILE);
                        userLayer1.save();
                        showUserLayer(userLayer1);
                        //useUserLayer();
                        Toast.makeText(MainActivity.this, tif_path, Toast.LENGTH_LONG).show();
                    }
                    else
                        Toast.makeText(MainActivity.this, "不能重复添加图层文件！", Toast.LENGTH_LONG).show();
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
        checkUserLayerFile(userLayerList);
        showUserLayer(userLayerList);
    }

    private void checkUserLayerFile(List<UserLayer> userLayerList){
        try {
            int size = userLayerList.size();
            for (int i = 0 ; i < size; ++i){
                String path = userLayerList.get(i).getPath();
                File file = new File(path);
                if (!file.exists()) {
                    LitePal.deleteAll(UserLayer.class, "path = ?", path);
                    userLayerList.remove(i);
                    --i;
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
                //TODO 完成用户图层使用逻辑dasfasdf as
                switch (userLayerList.get(i).getType()){
                    case UserLayer.TIF_FILE:
                        loadRaster(path);
                        break;
                    case UserLayer.SHP_FILE:
                        featureLayerShapefile(path);
                        break;
                }
                List<FieldNameSheet> FieldNameSheetList = new ArrayList<>();
                LayerFieldsSheetList.add(new LayerFieldsSheet(userLayerList.get(i).getName(), userLayerList.get(i).getPath(), FieldNameSheetList));
                setRecyclerViewForDynamicChooseFrame();
            }
        }catch (Exception e){
            Log.w(TAG, "checkUserLayer: " + e.toString());
        }
    }

    private void showUserLayer(UserLayer userLayer){
        try {
                String path = userLayer.getPath();
                //TODO 完成用户图层使用逻辑
                switch (userLayer.getType()){
                    case UserLayer.TIF_FILE:
                        loadRaster(path);
                        break;
                    case UserLayer.SHP_FILE:
                        featureLayerShapefile(path);
                        break;
                }
            List<FieldNameSheet> FieldNameSheetList = new ArrayList<>();
            LayerFieldsSheetList.add(new LayerFieldsSheet(userLayer.getName(), userLayer.getPath(), FieldNameSheetList));
            setRecyclerViewForDynamicChooseFrame();
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
