package com.esri.arcgisruntime.demos.displaymap;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PointF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.esri.arcgisruntime.ArcGISRuntimeException;
import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureCollection;
import com.esri.arcgisruntime.data.FeatureCollectionTable;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.data.FeatureTable;
import com.esri.arcgisruntime.data.Geodatabase;
import com.esri.arcgisruntime.data.GeodatabaseFeatureTable;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
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
import com.esri.arcgisruntime.layers.FeatureCollectionLayer;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.layers.Layer;
import com.esri.arcgisruntime.layers.SublayerList;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.location.LocationDataSource;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.LayerList;
import com.esri.arcgisruntime.mapping.MobileMapPackage;
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

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;

public class MainActivity extends AppCompatActivity {
    private MapView mMapView;
    private static final String TAG = "MainActivity";
    private static final String rootPath = Environment.getExternalStorageDirectory().toString() + "/临沧市基本农田/临沧市土地利用规划和基本农田数据.mmpk";
    private static final String rootPath2 = Environment.getExternalStorageDirectory().toString() + "/昆明.mmpk";
    private static final String rootPath1 = Environment.getExternalStorageDirectory().toString() + "/临沧市基本农田/临沧市5309省标准乡级土地利用总体规划及基本农田数据库2000.geodatabase";
    private List<layer> layerList = new ArrayList<>();
    private List<layer1> layers = new ArrayList<>();
    private layerAdapter adapter;
    private RecyclerView recyclerView;
    private GridLayoutManager layoutManager;
    private ImageButton recyclerViewButton;
    FeatureLayer mFeaturelayer;
    private boolean isClick = false;
    private TextView ScaleShow;
    ArcGISMap map;
    public static final String READ_EXTERNAL_STORAGE = "android.permission.READ_EXTERNAL_STORAGE";
    public static final String WRITE_EXTERNAL_STORAGE = "android.permission.WRITE_EXTERNAL_STORAGE";
    public static final int PERMISSION_CODE = 42042;
    com.github.clans.fab.FloatingActionButton whiteBlank_fab;
    //记录画笔颜色
    private int color_Whiteblank;
    GraphicsOverlay graphicsOverlay_66;
    ImageView North;
    FloatingActionButton ResetBT;

    private int DrawType;
    public static final int DRAW_POLYGON = -1;
    public static final int DRAW_POLYLINE = -2;
    public static final int DRAW_POINT = -3;
    public static final int DRAW_NONE = 0;



    double m_lat = 0, m_long = 0;


    //获取文件读取权限
    void pickFile() {
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                READ_EXTERNAL_STORAGE);
        int permissionCheck1 = ContextCompat.checkSelfPermission(this,
                WRITE_EXTERNAL_STORAGE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED || permissionCheck1 != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE},
                    PERMISSION_CODE
            );

            return;
        }

    }


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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.cancel:
                isQurey = NOQUREY;
                invalidateOptionsMenu();
                break;
            case R.id.search:
                isQurey = QUREY;
                invalidateOptionsMenu();
                break;
            default:

        }
        return true;
    }

    //记录是否处于白板画图状态
    private boolean isWhiteBlank = false;
    List<Point> whiteBlankPts;
    GraphicsOverlay graphicsOverlay_9;
    GraphicsOverlay graphicsOverlay_10;
    PointCollection points = new PointCollection(SpatialReference.create(4521));;
    List<Graphic> graphics = new ArrayList<>();
    boolean isOk = false;
    private void showPopueWindowForWhiteblank(){
        final View popView = View.inflate(this,R.layout.popupwindow_whiteblank,null);
        isWhiteBlank = true;
        whiteBlankPts = new ArrayList<>();
        FloatingActionButton back_pop = (FloatingActionButton) popView.findViewById(R.id.back_pop) ;
        back_pop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < graphics.size(); i++){
                    graphicsOverlay_10.getGraphics().remove(graphics.get(i));
                }
                if (graphics.size() != 0) graphics.remove(graphics.size() - 1);
                graphicsOverlay_10.getGraphics().clear();
                for (int i = 0; i < graphics.size(); i++){
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
                    for (int i = 0; i < mMapView.getGraphicsOverlays().size(); i++){
                        mMapView.getGraphicsOverlays().remove(i);
                    }
                }*/

                for (int i = 0; i < graphics.size(); i++){
                    graphicsOverlay_10.getGraphics().remove(graphics.get(i));
                }
                graphics.clear();
                graphicsOverlay_10.getGraphics().clear();
                mMapView.getGraphicsOverlays().remove(graphicsOverlay_10);
                mMapView.getGraphicsOverlays().add(graphicsOverlay_10);
                LitePal.deleteAll(whiteblank.class);
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
        //popupWindow OnTouchListener
        frameLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //PointCollection points = new PointCollection(SpatialReferences.getWgs84());
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
                            for (int i = 0; i < mMapView.getGraphicsOverlays().size(); i++){
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
                        for (int i = 0; i < points.size(); i++){
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
                                Math.round(event.getY() - getStatusBarHeight(MainActivity.this) - getDaoHangHeight(MainActivity.this)));
                        //Log.w(TAG, "onTouch: " + event.getX() + " ; " + event.getY());
                        // create a map point from screen point
                        Point mapPoint = mMapView.screenToLocation(screenPoint);
                        // convert to WGS84 for lat/lon format
                        Point wgs84Point = (Point) GeometryEngine.project(mapPoint, SpatialReferences.getWgs84());
                        //whiteBlankPts.add(wgs84Point);
                        //int size = whiteBlankPts.size();
                        //for (int i = 0; i < size; i++){
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

    private void showMap(){
        map = new ArcGISMap();
        if (isOK1 & isOK2) {
            map.getOperationalLayers().clear();
            int size = layers.size();
            Log.w(TAG, "showMap: " + map.getOperationalLayers().size());
            for (int i = 0; i < size; i++){
                Log.w(TAG, "showMap i : " + i);
                //if (!map.getOperationalLayers().contains(layers.get(i)))
                map.getOperationalLayers().add(layers.get(i).getLayer());
            }
            mMapView.setMap(map);
        }
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
    boolean isOK1 = false;
    boolean isOK2 = false;
    Point OriginLocation;

    //记录是否开启白板功能
    private boolean isOpenWhiteBlank = false;
    Callout mCallout;
    boolean inMap;
    boolean isNorth = false;

    private void showPopueWindowForMessure(){
        View popView = View.inflate(this,R.layout.popupwindow_drawfeature,null);
        Button bt_polygon = (Button) popView.findViewById(R.id.btn_pop_drawpolygon);
        final Button bt_polyline = (Button) popView.findViewById(R.id.btn_pop_drawpolyline);
        final Button bt_point = (Button) popView.findViewById(R.id.btn_pop_drawpoint);
        Button bt_cancle = (Button) popView.findViewById(R.id.btn_pop_cancel);
        //获取屏幕宽高
        int weight = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels * 1/3;

        final PopupWindow popupWindow = new PopupWindow(popView, weight, height);
        //popupWindow.setAnimationStyle(R.style.anim_popup_dir);
        popupWindow.setFocusable(true);
        //点击外部popueWindow消失
        popupWindow.setOutsideTouchable(true);
        bt_polygon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawType = DRAW_POLYGON;
                pointCollection = new PointCollection(SpatialReference.create(4521));
                popupWindow.dismiss();
            }
        });
        bt_polyline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawType = DRAW_POLYLINE;
                pointCollection = new PointCollection(SpatialReference.create(4521));
                popupWindow.dismiss();
            }
        });
        bt_point.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawType = DRAW_POINT;
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

    PointCollection pointCollection;
    PointCollection pointCollection1 = new PointCollection(SpatialReference.create(4521));
    int num = 0;
    Point ppp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //按钮添加要素
        FloatingActionButton DrawFeature = (FloatingActionButton)findViewById(R.id.DrawFeature);
        DrawFeature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DrawType == DRAW_NONE)
                    showPopueWindowForMessure();
                else {
                    if (DrawType == DRAW_POLYGON){
                        //pointCollection.add(ppp.getX(), ppp.getY());
                        //if (num == 0) {
                            final Polygon polygon = new Polygon(pointCollection);
                            /*GraphicsOverlay graphicsOverlay_1 = new GraphicsOverlay();
                            SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.GREEN, 3);
                            SimpleFillSymbol fillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.NULL, Color.WHITE, lineSymbol);
                            Graphic fillGraphic = new Graphic(polygon, fillSymbol);
                            graphicsOverlay_1.getGraphics().add(fillGraphic);
                            mMapView.getGraphicsOverlays().add(graphicsOverlay_1);
                            num++;*/

                            Log.w(TAG, "onClick: " + pointCollection.size());
                            Log.w(TAG, "onClick: " + GeometryEngine.area(polygon));
                            Log.w(TAG, "onClick: " + GeometryEngine.areaGeodetic(polygon, new AreaUnit(AreaUnitId.SQUARE_KILOMETERS), GeodeticCurveType.GEODESIC));
                            Log.w(TAG, "onClick: " + GeometryEngine.lengthGeodetic(polygon.toPolyline(), new LinearUnit(LinearUnitId.KILOMETERS), GeodeticCurveType.GEODESIC));

                        //}
                        /*else {
                            final Polygon polygon = new Polygon(pointCollection1);
                            GraphicsOverlay graphicsOverlay_1 = new GraphicsOverlay();
                            SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.GREEN, 3);
                            SimpleFillSymbol fillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.NULL, Color.WHITE, lineSymbol);
                            Graphic fillGraphic = new Graphic(polygon, fillSymbol);
                            graphicsOverlay_1.getGraphics().add(fillGraphic);
                            mMapView.getGraphicsOverlays().add(graphicsOverlay_1);
                            num++;

                            Geometry geometry = GeometryEngine.intersection(polygon, new Polygon(pointCollection));
                            GraphicsOverlay graphicsOverlay_11 = new GraphicsOverlay();
                            SimpleLineSymbol lineSymbol1 = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.RED, 3);
                            SimpleFillSymbol fillSymbol1 = new SimpleFillSymbol(SimpleFillSymbol.Style.NULL, Color.WHITE, lineSymbol1);
                            Graphic fillGraphic1 = new Graphic(geometry, fillSymbol1);
                            graphicsOverlay_1.getGraphics().add(fillGraphic1);
                            mMapView.getGraphicsOverlays().add(graphicsOverlay_11);
                            Log.w(TAG, "onClick: " + pointCollection1.size());
                            Log.w(TAG, "onClick: " + GeometryEngine.area(polygon));
                            Log.w(TAG, "onClick: " + GeometryEngine.areaGeodetic(polygon, new AreaUnit(AreaUnitId.SQUARE_KILOMETERS), GeodeticCurveType.GEODESIC));
                            Log.w(TAG, "onClick: " + GeometryEngine.lengthGeodetic(polygon.toPolyline(), new LinearUnit(LinearUnitId.KILOMETERS), GeodeticCurveType.GEODESIC));
                        }*/
                        QueryParameters query = new QueryParameters();
                        query.setGeometry(polygon);// 设置空间几何对象
                        File file = new File(rootPath);
                        if (file.exists() & MapQuery) {
                            //FeatureLayer featureLayer=(FeatureLayer) mMapView.getMap().getOperationalLayers().get(10);
                            try {
                                FeatureTable mTable = featureLayer777.getFeatureTable();//得到查询属性表
                                final ListenableFuture<FeatureQueryResult> featureQueryResult
                                        = mTable.queryFeaturesAsync(query);
                                featureQueryResult.addDoneListener(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            while (mMapView.getGraphicsOverlays().size() != 0) {
                                                for (int i = 0; i < mMapView.getGraphicsOverlays().size(); i++) {
                                                    mMapView.getGraphicsOverlays().remove(i);
                                                }
                                            }
                                            FeatureQueryResult featureResul = featureQueryResult.get();
                                            Geometry geometry1 = polygon;
                                            List<QueryTaskInfo> queryTaskInfos = new ArrayList<>();
                                            for (Object element : featureResul) {
                                                if (element instanceof Feature) {
                                                    Feature mFeatureGrafic = (Feature) element;
                                                    Polygon geometry = (Polygon) mFeatureGrafic.getGeometry();
                                                    //geometry1 = GeometryEngine.intersection(geometry1, GeometryEngine.project(geometry, SpatialReference.create(4521)));
                                                    Geometry geometry2 = GeometryEngine.intersection(polygon, GeometryEngine.project(geometry, SpatialReference.create(4521)));
                                                    QueryTaskInfo queryTaskInfo = new QueryTaskInfo(GeometryEngine.areaGeodetic(geometry2, new AreaUnit(AreaUnitId.SQUARE_KILOMETERS), GeodeticCurveType.GEODESIC));
                                                    //Log.w(TAG, "run: " + geometry.getSpatialReference().getWkid());
                                                    //Log.w(TAG, "run: " + geometry1.getSpatialReference().getWkid());
                                                    GraphicsOverlay graphicsOverlay_1 = new GraphicsOverlay();
                                                    SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.BLUE, 3);
                                                    Graphic fillGraphic = new Graphic(geometry2, lineSymbol);
                                                    graphicsOverlay_1.getGraphics().add(fillGraphic);
                                                    mMapView.getGraphicsOverlays().add(graphicsOverlay_1);
                                                    Map<String, Object> mQuerryString = mFeatureGrafic.getAttributes();
                                                    //calloutContent.setSingleLine();
                                                    // format coordinates to 4 decimal places
                                                    //String str = "";
                                                    //List<KeyAndValue> keyAndValues = new ArrayList<>();
                                                    for (String key : mQuerryString.keySet()) {
                                                        //str = str + key + " : " + String.valueOf(mQuerryString.get(key)) + "\n";
                                                        if (key.equals("GHDLMC"))
                                                            queryTaskInfo.setTypename(String.valueOf(mQuerryString.get(key)));
                                                        else if (key.equals("GHDLBM"))
                                                            queryTaskInfo.setType(String.valueOf(mQuerryString.get(key)));
                                                        else if (key.equals("XZQMC"))
                                                            queryTaskInfo.setXzq(String.valueOf(mQuerryString.get(key)));
                                                        else if (key.equals("XZQDM"))
                                                            queryTaskInfo.setXzqdm(String.valueOf(mQuerryString.get(key)));
                                                    }
                                                    queryTaskInfos.add(queryTaskInfo);

                                                }
                                            }
                                            double wholeArea = GeometryEngine.areaGeodetic(geometry1, new AreaUnit(AreaUnitId.SQUARE_KILOMETERS), GeodeticCurveType.GEODESIC);
                                            List<SliceValue> sliceValues = new ArrayList<>();
                                            TextView calloutContent = new TextView(getApplicationContext());
                                            calloutContent.setTextColor(Color.BLACK);
                                            String str = "";
                                            List<KeyAndValue> keyAndValues = new ArrayList<>();
                                            for (int i = 0; i < queryTaskInfos.size(); i++){
                                                if (i == 0 && queryTaskInfos.get(i).getArea() != 0) keyAndValues.add(new KeyAndValue(queryTaskInfos.get(i).getTypename(), Double.toString(queryTaskInfos.get(i).getArea())));
                                                else {
                                                    boolean hasKey = false;
                                                    for (int j = 0; j < keyAndValues.size(); j++){
                                                        if (queryTaskInfos.get(i).getTypename().equals(keyAndValues.get(j).getName())) {
                                                            hasKey = true;
                                                            keyAndValues.get(j).setValue(Double.toString(Double.valueOf(keyAndValues.get(j).getValue()) + queryTaskInfos.get(i).getArea()));
                                                            break;
                                                        }
                                                    }
                                                    if (!hasKey && queryTaskInfos.get(i).getArea() != 0){
                                                        keyAndValues.add(new KeyAndValue(queryTaskInfos.get(i).getTypename(), Double.toString(queryTaskInfos.get(i).getArea())));
                                                    }
                                                }
                                            }
                                            DecimalFormat decimalFormat = new DecimalFormat("0.00");
                                            DecimalFormat decimalFormat1 = new DecimalFormat("0.0000");
                                            for (int j = 0; j < keyAndValues.size(); j++){
                                                if (j < keyAndValues.size() - 1) {
                                                    sliceValues.add(new SliceValue(Float.valueOf(keyAndValues.get(j).getValue())));
                                                    str = str + keyAndValues.get(j).getName() + ": " + decimalFormat1.format(Double.valueOf(keyAndValues.get(j).getValue())) + "平方公里" + "\n";
                                                    str = str + "占比: " + decimalFormat.format(Double.valueOf(keyAndValues.get(j).getValue()) / wholeArea  * 100) + "%" + "\n";
                                                }
                                                else {
                                                    sliceValues.add(new SliceValue(Float.valueOf(keyAndValues.get(j).getValue())));
                                                    str = str + keyAndValues.get(j).getName() + ": " + decimalFormat1.format(Double.valueOf(keyAndValues.get(j).getValue())) + "平方公里" + "\n";
                                                    str = str + "占比: " + decimalFormat.format(Double.valueOf(keyAndValues.get(j).getValue()) / wholeArea  * 100) + "%";
                                                }
                                            }
                                            calloutContent.setText(str);
                                            // get callout, set content and show
                                            mCallout.setLocation(new Point(geometry1.getExtent().getCenter().getX(), geometry1.getExtent().getYMax(), SpatialReference.create(4521)));
                                            mCallout.setContent(calloutContent);
                                            mCallout.show();
                                            inMap = true;
                                            PieChartData pieChartData = new PieChartData(sliceValues);
                                            PieChartView pieChartView = (PieChartView) findViewById(R.id.chart);
                                            pieChartView.setPieChartData(pieChartData);
                                            pieChartView.setVisibility(View.VISIBLE);
                                            GraphicsOverlay graphicsOverlay_1 = new GraphicsOverlay();
                                            SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.RED, 3);
                                            Graphic fillGraphic = new Graphic(geometry1, lineSymbol);
                                            graphicsOverlay_1.getGraphics().add(fillGraphic);
                                            mMapView.getGraphicsOverlays().add(graphicsOverlay_1);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            } catch (ArcGISRuntimeException e) {
                                Toast.makeText(MainActivity.this, e.getMessage() + e.getErrorCode(), Toast.LENGTH_SHORT).show();
                            }
                        } else
                            Toast.makeText(MainActivity.this, R.string.QueryError_2, Toast.LENGTH_SHORT).show();
                        if (!inMap) mCallout.dismiss();

                    }else if (DrawType == DRAW_POLYLINE){
                        Polyline polyline = new Polyline(pointCollection);
                        GraphicsOverlay graphicsOverlay_1 = new GraphicsOverlay();
                        SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.GREEN, 3);
                        Graphic fillGraphic = new Graphic(polyline, lineSymbol);
                        graphicsOverlay_1.getGraphics().add(fillGraphic);
                        mMapView.getGraphicsOverlays().add(graphicsOverlay_1);
                        Log.w(TAG, "onClick: " + pointCollection.size());
                        Log.w(TAG, "onClick: " + GeometryEngine.lengthGeodetic(polyline, new LinearUnit(LinearUnitId.METERS), GeodeticCurveType.GEODESIC));
                    }
                    DrawType = DRAW_NONE;
                }
            }
        });
        //获取传感器管理器系统服务
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        North = (ImageView) findViewById(R.id.North);
        North.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //North.setRotation(0);
                if (mMapView.getMapRotation() != 0) {
                    isNorth = false;
                    mMapView.setViewpointRotationAsync(0);
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
                    mMapView.setViewpointCenterAsync(OriginLocation, 1200000);
                    //mMapView.setViewpointScaleAsync(1200000);
                    //North.setRotation(0);
                    mMapView.setViewpointRotationAsync(0);
                }else {
                    mMapView.setViewpointCenterAsync(new Point(102.715507, 25.038112, 0.000000, SpatialReference.create(4326)), 65000);
                    //North.setRotation(0);
                    mMapView.setViewpointRotationAsync(0);
                }
            }
        });
        ScaleShow = (TextView) findViewById(R.id.scale);
        FloatingActionButton MapQueryBT = (FloatingActionButton) findViewById(R.id.MapQuery);
        MapQueryBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!MapQuery) {
                    MapQuery = true;
                    ScaleShow.setText("当前比例  1 : " + String.valueOf((int)mMapView.getMapScale()) + " (图面查询中)");
                    Toast.makeText(MainActivity.this, R.string.OpenMapQuery, Toast.LENGTH_LONG).show();
                }
                else {
                    MapQuery = false;
                    mCallout.dismiss();
                    mMapView.getGraphicsOverlays().clear();
                    drawWhiteBlank();
                    ScaleShow.setText("当前比例  1 : " + String.valueOf((int)mMapView.getMapScale()));
                    Toast.makeText(MainActivity.this, R.string.CloseMapQuery, Toast.LENGTH_LONG).show();
                }
            }
        });
        graphicsOverlay_66 = new GraphicsOverlay();
        color_Whiteblank = Color.RED;
        //LitePal.deleteAll(whiteblank.class);
        //whiteBlankPts = new ArrayList<Point>();
        whiteBlank_fab = (FloatingActionButton) findViewById(R.id.whiteBlank);
        whiteBlank_fab.setImageResource(R.drawable.ic_brush_black_24dp);
        whiteBlank_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isOpenWhiteBlank){
                    isOpenWhiteBlank = true;
                    whiteBlank_fab.setImageResource(R.drawable.ic_cancel_black_24dp);
                    setTitle(R.string.WhiteBlankDrawing);
                    showPopueWindowForWhiteblank();
                    whiteBlank_fab.setVisibility(View.INVISIBLE);
                }
            }
        });
        recyclerViewButton = (ImageButton) findViewById(R.id.openRecyclerView);
        recyclerViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isClick == true){
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
        });
        /*recyclerViewButton1 = (ImageButton) findViewById(R.id.openRecyclerView1);
        recyclerViewButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isClick == true){
                    recyclerView.setVisibility(View.GONE);
                    recyclerViewButton.setVisibility(View.VISIBLE);
                    recyclerViewButton1.setVisibility(View.GONE);
                    isClick = false;
                    //recyclerViewButton.setBackgroundResource(R.drawable.ic_expand_less_black_24dp);
                    //recyclerViewButton.setX(recyclerView.getX() + recyclerView.getWidth() + 50);
                }else {
                    //setRecyclerView();
                    recyclerView.setVisibility(View.VISIBLE);
                }
                //Log.w(TAG, "onClick: " + recyclerViewButton.getBackground().toString() );
            }
        });*/
        mMapView = findViewById(R.id.mapView);// = new ArcGISMap(Basemap.Type.TOPOGRAPHIC, 34.056295, -117.195800, 16);
        map = new ArcGISMap();
        pickFile();
        Log.w(TAG, "onCreate: " + rootPath );
        final MobileMapPackage mainMobileMapPackage = new MobileMapPackage(rootPath);
        mainMobileMapPackage.loadAsync();
        mainMobileMapPackage.addDoneLoadingListener(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            LoadStatus mainLoadStatus = mainMobileMapPackage.getLoadStatus();
                                                            if (mainLoadStatus == LoadStatus.LOADED) {
                                                                List<ArcGISMap> mainArcGISMapL = mainMobileMapPackage.getMaps();
                                                                map = mainArcGISMapL.get(0);
                                                                int size = map.getOperationalLayers().size();
                                                                Log.w(TAG, "size: " + size);
                                                                for (int i = size - 1; i > -1; i--){
                                                                    if (!map.getOperationalLayers().get(i).getName().contains(".tpk")) {
                                                                        layers.add(new layer1(map.getOperationalLayers().get(i), i));
                                                                        layerList.add(new layer(map.getOperationalLayers().get(i).getName(), true));
                                                                    }else {
                                                                        hasTPK = true;
                                                                        TPKlayers.add(new layer1(map.getOperationalLayers().get(i), i));
                                                                    }
                                                                }
                                                                layerList.add(new layer("影像", true));
                                                                isOK1 = true;

                                                               /* Log.w(TAG, "getFullExtent: " + map.getOperationalLayers().size());
                                                                for (int i = 0; i < map.getOperationalLayers().size(); i++){
                                                                    Log.w(TAG, "getFullExtent: " + i);
                                                                    Log.w(TAG, "getFullExtent: " + map.getOperationalLayers().get(i).getFullExtent());
                                                                }*/
                                                                //showMap();
                                                                mMapView.setMap(map);
                                                                OriginScale = mMapView.getMapScale();
                                                                //Log.w(TAG, "getMapScale: " + mMapView.getMapScale());
                                                                //Log.w(TAG, "getVisibleArea: " + mMapView.getVisibleArea().getExtent().getCenter());
                                                                if (mainMobileMapPackage.getPath().toString().contains("临沧")) {
                                                                    Log.w(TAG, "run: " + mainMobileMapPackage.getPath().toString());
                                                                    OriginLocation = new Point(99.626302, 23.928384, 0, SpatialReference.create(4326));
                                                                    //mMapView.setViewpointCenterAsync(OriginLocation);
                                                                    //mMapView.setViewpointScaleAsync(1200000);
                                                                    mMapView.setViewpointCenterAsync(OriginLocation, 1200000);
                                                                }else mMapView.setViewpointScaleAsync(100000);
                                                                drawWhiteBlank();
                                                                //Log.w(TAG, "run: " + mMapView.getWidth() + "; " + (mMapView.getTop() + getStatusBarHeight(MainActivity.this) + getDaoHangHeight(MainActivity.this)) + "; " + (mMapView.getBottom() + getStatusBarHeight(MainActivity.this) + getDaoHangHeight(MainActivity.this)));

                                                            }else mainMobileMapPackage.loadAsync();
                                                        }
                                                    });
        /*final MobileMapPackage mainMobileMapPackage1 = new MobileMapPackage(rootPath1);
        mainMobileMapPackage1.loadAsync();
        mainMobileMapPackage1.addDoneLoadingListener(new Runnable() {
            @Override
            public void run() {
                LoadStatus mainLoadStatus = mainMobileMapPackage1.getLoadStatus();
                if (mainLoadStatus == LoadStatus.LOADED) {
                    List<ArcGISMap> mainArcGISMapL = mainMobileMapPackage1.getMaps();
                    Log.w(TAG, "" + Integer.toString(mainArcGISMapL.size()) );
                    //ArcGISMap mainArcrun: GISMapMMPK = mainArcGISMapL.get(0);
                    Log.w(TAG, "mainArcGISMapL.size: " + mainArcGISMapL.size());
                    ArcGISMap map1 = mainArcGISMapL.get(0);
                    int size = map1.getOperationalLayers().size();
                    Log.w(TAG, "size: " + size);
                    for (int i = 0; i < size; i++){
                        layers.add(map1.getOperationalLayers().get(i));
                        layerList.add(new layer(map1.getOperationalLayers().get(i).getName()));
                        //try {
                        Log.w(TAG, "run: " + map.getOperationalLayers().size());
                        //if (map.getOperationalLayers().size() <= 6)
                        //map.getOperationalLayers().add(map1.getOperationalLayers().get(i));
                        //}catch (ArcGISRuntimeException e){

                        // }
                    }
                    isOK2 = true;
                    showMap();
                    //mMapView.setMap(map);
                }
            }
        });*/
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
                    OriginLocation = (Point) GeometryEngine.project(pt, SpatialReferences.getWgs84());
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
                if (DrawType == DRAW_NONE) {
                    // center on tapped point
                    mMapView.setViewpointCenterAsync(wgs84Point);
                    Log.w(TAG, "onSingleTapConfirmed: " + wgs84Point);
                    inMap = false;
                    Log.w(TAG, "onSingleTapConfirmed: ");
                    //final android.graphics.Point screenPoint=new android.graphics.Point(Math.round(v.getX()), Math.round(v.getY()));

                    Log.w(TAG, "onSingleTapConfirmed: " + mapPoint);
                    QueryParameters query = new QueryParameters();
                    query.setGeometry(clickPoint);// 设置空间几何对象
                    File file = new File(rootPath);
                    if (file.exists() & MapQuery) {
                        //FeatureLayer featureLayer=(FeatureLayer) mMapView.getMap().getOperationalLayers().get(10);
                        try {
                            FeatureTable mTable = featureLayer777.getFeatureTable();//得到查询属性表
                            final ListenableFuture<FeatureQueryResult> featureQueryResult
                                    = mTable.queryFeaturesAsync(query);
                            featureQueryResult.addDoneListener(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        while (mMapView.getGraphicsOverlays().size() != 0) {
                                            for (int i = 0; i < mMapView.getGraphicsOverlays().size(); i++) {
                                                mMapView.getGraphicsOverlays().remove(i);
                                            }
                                        }
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
                                                    if (key.equals("GHDLMC"))
                                                        keyAndValues.add(new KeyAndValue(key, String.valueOf(mQuerryString.get(key))));
                                                    else if (key.equals("GHDLBM"))
                                                        keyAndValues.add(new KeyAndValue(key, String.valueOf(mQuerryString.get(key))));
                                                    else if (key.equals("GHDLMJ"))
                                                        keyAndValues.add(new KeyAndValue(key, String.valueOf(mQuerryString.get(key))));
                                                    else if (key.equals("PDJB"))
                                                        keyAndValues.add(new KeyAndValue(key, String.valueOf(mQuerryString.get(key))));
                                                    else if (key.equals("XZQMC"))
                                                        keyAndValues.add(new KeyAndValue(key, String.valueOf(mQuerryString.get(key))));
                                                    else if (key.equals("XZQDM"))
                                                        keyAndValues.add(new KeyAndValue(key, String.valueOf(mQuerryString.get(key))));
                                                }
                                                keyAndValues = KeyAndValue.parseList(keyAndValues);
                                                for (int i = 0; i < keyAndValues.size(); i++) {
                                                    str = str + keyAndValues.get(i).getNickname() + " : " + keyAndValues.get(i).getValue() + "\n";
                                                }
                                                calloutContent.setText(str);
                                                // get callout, set content and show
                                                mCallout.setLocation(mapPoint);
                                                mCallout.setContent(calloutContent);
                                                mCallout.show();
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
                    } else
                        Toast.makeText(MainActivity.this, R.string.QueryError_2, Toast.LENGTH_SHORT).show();
                    if (!inMap) mCallout.dismiss();
                    //FeatureLayer featureLayer=(FeatureLayer) mMapView.getMap().getBasemap().getBaseLayers().get(0);
                }else if (DrawType == DRAW_POLYGON){
                    ppp = wgs84Point;
                    pointCollection.add(wgs84Point);
                    if (pointCollection.size() == 2){
                        while (mMapView.getGraphicsOverlays().size() != 0) {
                            for (int i = 0; i < mMapView.getGraphicsOverlays().size(); i++) {
                                mMapView.getGraphicsOverlays().remove(i);
                            }
                        }
                        GraphicsOverlay graphicsOverlay_1 = new GraphicsOverlay();
                        SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.GREEN, 3);
                        Graphic fillGraphic = new Graphic(new Polyline(pointCollection, SpatialReference.create(4521)), lineSymbol);
                        graphicsOverlay_1.getGraphics().add(fillGraphic);
                        mMapView.getGraphicsOverlays().add(graphicsOverlay_1);
                    }else if (pointCollection.size() > 2){
                        while (mMapView.getGraphicsOverlays().size() != 0) {
                            for (int i = 0; i < mMapView.getGraphicsOverlays().size(); i++) {
                                mMapView.getGraphicsOverlays().remove(i);
                            }
                        }
                        GraphicsOverlay graphicsOverlay_1 = new GraphicsOverlay();
                        SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.GREEN, 3);
                        Graphic fillGraphic = new Graphic(new Polygon(pointCollection, SpatialReference.create(4521)), lineSymbol);
                        graphicsOverlay_1.getGraphics().add(fillGraphic);
                        mMapView.getGraphicsOverlays().add(graphicsOverlay_1);
                    }
                }else if (DrawType == DRAW_POLYLINE){
                    pointCollection.add(wgs84Point);
                    if (pointCollection.size() >= 2){
                        while (mMapView.getGraphicsOverlays().size() != 0) {
                            for (int i = 0; i < mMapView.getGraphicsOverlays().size(); i++) {
                                mMapView.getGraphicsOverlays().remove(i);
                            }
                        }
                        GraphicsOverlay graphicsOverlay_1 = new GraphicsOverlay();
                        SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.GREEN, 3);
                        Graphic fillGraphic = new Graphic(new Polyline(pointCollection, SpatialReference.create(4521)), lineSymbol);
                        graphicsOverlay_1.getGraphics().add(fillGraphic);
                        mMapView.getGraphicsOverlays().add(graphicsOverlay_1);
                    }
                }else if (DrawType == DRAW_POINT){
                    GraphicsOverlay graphicsOverlay_1 = new GraphicsOverlay();
                    SimpleMarkerSymbol pointSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.DIAMOND, Color.RED, 10);
                    Graphic fillGraphic = new Graphic(wgs84Point, pointSymbol);
                    graphicsOverlay_1.getGraphics().add(fillGraphic);
                    mMapView.getGraphicsOverlays().add(graphicsOverlay_1);
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

        /*initMap();
        Log.w(TAG, "初始化后" + Integer.toString(map.getOperationalLayers().size()));
        mMapView.setOnTouchListener(new DefaultMapViewOnTouchListener(this, mMapView) {
            @Override
            public boolean  onSingleTapConfirmed(MotionEvent v) {
                android.graphics.Point screenPoint=new android.graphics.Point(Math.round(v.getX()), Math.round(v.getY()));
                final Point clickPoint = mMapView.screenToLocation(screenPoint);
                QueryParameters query = new QueryParameters();
                query.setGeometry(clickPoint);// 设置空间几何对象
                FeatureLayer featureLayer=(FeatureLayer) mMapView.getMap().getOperationalLayers().get(2);
                FeatureTable mTable = featureLayer.getFeatureTable();//得到查询属性表
                final ListenableFuture<FeatureQueryResult> featureQueryResult
                        = mTable.queryFeaturesAsync(query);
                featureQueryResult.addDoneListener(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            FeatureQueryResult featureResul = featureQueryResult.get();
                            for (Object element : featureResul) {
                                if (element instanceof Feature) {
                                    Feature mFeatureGrafic = (Feature) element;
                                    Geometry geometry=mFeatureGrafic.getGeometry();
                                    GraphicsOverlay graphicsOverlay_1=new GraphicsOverlay();
                                    SimpleMarkerSymbol pointSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.DIAMOND, Color.RED, 10);
                                    SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.DASH,Color.GREEN,3);
                                    Graphic pointGraphic = new Graphic(clickPoint,pointSymbol);
                                    Graphic fillGraphic = new Graphic(geometry,lineSymbol);
                                    graphicsOverlay_1.getGraphics().add(pointGraphic);
                                    graphicsOverlay_1.getGraphics().add(fillGraphic);
                                    mMapView.getGraphicsOverlays().add(graphicsOverlay_1);
                                    Map<String, Object> mQuerryString = mFeatureGrafic.getAttributes();
                                    for(String key : mQuerryString.keySet()){
                                        Log.i("==============="+key,String.valueOf(mQuerryString.get(key)));
                                    }
                                }
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });
                return true;
            }
        });
        mMapView.setMap(map);
        setRecyclerView();
        Log.w(TAG, "onCreate: "  );*/



        mCallout = mMapView.getCallout();
        //Log.w(TAG, "onCreate: " + mMapView.getMap().getOperationalLayers().get(10).getFullExtent().getCenter().getX() + "; " + mMapView.getMap().getOperationalLayers().get(10).getFullExtent().getCenter().getY());
        locationDisplay = mMapView.getLocationDisplay();
        Log.w(TAG, "onCreate: " + locationDisplay.getLocation().getPosition());
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
                }
            });
        }

        File file = new File(rootPath1);
        if (file.exists()) {
            localGdb = new Geodatabase(rootPath1);
            Log.w(TAG, "run: " + localGdb.getLoadStatus().toString());
            Log.w(TAG, "run: " + localGdb.getPath());
            localGdb.loadAsync();
            localGdb.addDoneLoadingListener(new Runnable() {
                @Override
                public void run() {
                    featureLayer777 = new FeatureLayer(localGdb.getGeodatabaseFeatureTable("土地规划地类"));
                    mFeaturelayer = new FeatureLayer(localGdb.getGeodatabaseFeatureTable("地名点"));
                    //mMapView.setViewpointCenterAsync();
                }
            });
            Log.w(TAG, "run: " + localGdb.getLoadStatus().toString());
        } else Toast.makeText(MainActivity.this, R.string.QueryError_1, Toast.LENGTH_SHORT).show();

        mMapView.addMapScaleChangedListener(new MapScaleChangedListener() {
            @Override
            public void mapScaleChanged(MapScaleChangedEvent mapScaleChangedEvent) {
                if (!MapQuery) ScaleShow.setText("当前比例  1 : " + String.valueOf((int)mMapView.getMapScale()));
                else ScaleShow.setText("当前比例  1 : " + String.valueOf((int)mMapView.getMapScale()) + " (图面查询中)");
            }
        });
        FloatingActionButton LocHereBT = (FloatingActionButton) findViewById(R.id.LocHere);
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
    }

    int numx = 0;
    Point mLocation;
    Geodatabase localGdb;
    LocationDisplay locationDisplay;
    FeatureLayer featureLayer777 = null;
    boolean hasTPK = false;
    boolean MapQuery = false;

    public void drawWhiteBlank(){
        graphics.clear();
        List<whiteblank> whiteblanks = LitePal.findAll(whiteblank.class);
        int size = whiteblanks.size();
        Log.w(TAG, "onCreate: " + size);
        if (size == 0) graphicsOverlay_10 = new GraphicsOverlay(GraphicsOverlay.RenderingMode.DYNAMIC);
        else {
            graphicsOverlay_10 = new GraphicsOverlay(GraphicsOverlay.RenderingMode.DYNAMIC);
            for (int i = 0; i < size; i++){
                points.clear();
                //geometry_WhiteBlank geometryWhiteBlank = new geometry_WhiteBlank(whiteblanks.get(i).getLineSymbol(), whiteblanks.get(i).getPolyline());
                String[] strings = whiteblanks.get(i).getPts().split("lzy");
                Log.w(TAG, "drawWhiteBlank1: " + strings.length);
                for (int kk = 0; kk < strings.length; kk++){
                    String[] strings1 = strings[kk].split(",");
                    if (strings1.length == 2) {
                        Log.w(TAG, "drawWhiteBlank2: " + strings1.length);
                        Point wgs84Point = (Point) GeometryEngine.project(new Point(Double.valueOf(strings1[0]), Double.valueOf(strings1[1])), SpatialReferences.getWgs84());
                        points.add(wgs84Point);
                    }
                }
                SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, whiteblanks.get(i).getColor(), 3);
                Polyline polyline = new Polyline(points);
                Graphic g = new Graphic(polyline, lineSymbol);
                graphics.add(g);
            }
            if (graphics.size() > 0) {
                for (int j = 0; j < graphics.size(); j++){
                    try {
                        graphicsOverlay_10.getGraphics().add(graphics.get(j));
                    }catch (ArcGISRuntimeException e){

                    }
                }
            }
            if (mMapView.getGraphicsOverlays().size() != 0) mMapView.getGraphicsOverlays().remove(graphicsOverlay_10);
            mMapView.getGraphicsOverlays().add(graphicsOverlay_10);
        }
    }

    public void searchForState(final String searchString) {

        // clear any previous selections

        mFeaturelayer.clearSelection();

        // create objects required to do a selection with a query
        QueryParameters query = new QueryParameters();
        //make search case insensitive
        query.setWhereClause("upper(图上名称) LIKE '%" + searchString.toUpperCase() + "%'");
        Log.w(TAG, "searchForState: " );
        // call select features
        if (mMapView.getMap().getOperationalLayers().size() != 0) {

            //Log.w(TAG, "searchForState: getAttribution" + mMapView.getMap().getOperationalLayers().get(3).getAttribution());
            //Log.w(TAG, "searchForState: getDescription" + mMapView.getMap().getOperationalLayers().get(3).getDescription());
            //mFeaturelayer = (FeatureLayer) mMapView.getMap().getOperationalLayers().get(11);
            try {
                FeatureTable mTable = mFeaturelayer.getFeatureTable();//得到查询属性表
                //Log.w(TAG, "searchForState: " + mTable.getFields().get(0) );
                final ListenableFuture<FeatureQueryResult> featureQueryResult
                        = mTable.queryFeaturesAsync(query);
                featureQueryResult.addDoneListener(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            while (mMapView.getGraphicsOverlays().size() != 0){
                                for (int i = 0; i < mMapView.getGraphicsOverlays().size(); i++){
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
                                mFeaturelayer.selectFeature(feature);
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


    private int isQurey = NOQUREY;
    private static final int QUREY = 0;
    private static final int NOQUREY = 1;
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        switch (isQurey){
            case QUREY:
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
                        mCallout.dismiss();
                        File file = new File(rootPath1);
                        if (file.exists()) showListPopupWindow(searchView, query);
                        else Toast.makeText(MainActivity.this, R.string.QueryError_1, Toast.LENGTH_SHORT).show();
                        return true;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        return true;
                    }
                });
                //menu.findItem(R.id.action_search).setVisible(true);
                break;
            case NOQUREY:
                menu.findItem(R.id.search).setVisible(true);
                menu.findItem(R.id.cancel).setVisible(false);
                menu.findItem(R.id.action_search).setVisible(false);
                break;
        }
        return super.onPrepareOptionsMenu(menu);
    }

    String[] items;
    public void showListPopupWindow(View view, String searchString) {
        queryInfos.clear();
        final ListPopupWindow listPopupWindow = new ListPopupWindow(this);
        searchString = searchString.trim();
        mFeaturelayer.clearSelection();

        // create objects required to do a selection with a query
        QueryParameters query = new QueryParameters();
        //make search case insensitive
        query.setWhereClause("upper(图上名称) LIKE '%" + searchString.toUpperCase() + "%'");
        Log.w(TAG, "searchForState: " );
        // call select features
        if (mMapView.getMap().getOperationalLayers().size() != 0) {

            //Log.w(TAG, "searchForState: getAttribution" + mMapView.getMap().getOperationalLayers().get(3).getAttribution());
            //Log.w(TAG, "searchForState: getDescription" + mMapView.getMap().getOperationalLayers().get(3).getDescription());
            //mFeaturelayer = (FeatureLayer) mMapView.getMap().getOperationalLayers().get(11);
            try {
                final String string = searchString;
                FeatureTable mTable = mFeaturelayer.getFeatureTable();//得到查询属性表
                //Log.w(TAG, "searchForState: " + mTable.getFields().get(0) );
                final ListenableFuture<FeatureQueryResult> featureQueryResult
                        = mTable.queryFeaturesAsync(query);
                featureQueryResult.addDoneListener(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            /*while (mMapView.getGraphicsOverlays().size() != 0){
                                for (int i = 0; i < mMapView.getGraphicsOverlays().size(); i++){
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
                                for (int i = 0; i < queryInfos.size(); i++){
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
                            for (int i = 0; i < queryInfos.size(); i++){
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
                                for (int i = 0; i < queryInfos.size(); i++){
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
                            for (int i = 0; i < queryInfos.size(); i++){
                                items[i] = queryInfos.get(i).getName();
                            }
                            // ListView适配器
                            listPopupWindow.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, items));

                            //Log.w(TAG, "run: " + featureCollectionTable.iterator().next().getAttributes().get("图上名称").toString());
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
                mFeaturelayer.selectFeature(queryInfos.get(position).getFeature());
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
    List<QueryInfo> queryInfos = new ArrayList<>();

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
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        layoutManager = new GridLayoutManager(this,1);
        recyclerView.setLayoutManager(layoutManager);
        Log.w(TAG, "setRecyclerView: " + layerList.size() );
        adapter = new layerAdapter(layerList);
        adapter.setOnItemCheckListener(new layerAdapter.OnRecyclerItemCheckListener() {
            @Override
            public void onItemCheckClick(layerAdapter.ViewHolder holder, String name, int position) {
                if (!holder.checkBox.isChecked()){
                    holder.checkBox.setChecked(false);
                    //layerList.get(position).setStatus(false);
                    //if (layerList.get(position).getName().equals("tiledLayer")) {
                    if (!name.equals("影像")) {
                        //map.getOperationalLayers().remove(layers.get(position).getLayer());
                        map.getOperationalLayers().get(layers.get(position).getNum()).setVisible(false);
                        for (int i = 0; i < layerList.size(); i++){
                            if (name.equals(layerList.get(i).getName())) layerList.get(i).setStatus(false);
                        }
                    }
                    else {
                        for (int kk = 0; kk < TPKlayers.size(); kk++){
                            //map.getOperationalLayers().remove(TPKlayers.get(kk).getLayer());
                            map.getOperationalLayers().get(TPKlayers.get(kk).getNum()).setVisible(false);
                        }
                        for (int i = 0; i < layerList.size(); i++){
                            if (name.equals(layerList.get(i).getName())) layerList.get(i).setStatus(false);
                        }
                    }
                        //map.getOperationalLayers().get(position).setVisible(false);
                        Log.w(TAG, "取消选中后" + Integer.toString(map.getOperationalLayers().size()));
                    //}else if (layerList.get(position).getName().equals("censusLayer")) {
                        //map.getOperationalLayers().remove(layers.get(position));
                        //Log.w(TAG, "取消选中后" + Integer.toString(map.getOperationalLayers().size()));
                    //}
                    //Log.w(TAG, "取消选中后" + Integer.toString(map.getOperationalLayers().size()));
                    mMapView.setMap(map);
                }else {
                    holder.checkBox.setChecked(true);
                    //layerList.get(position).setStatus(true);
                    //if (layerList.get(position).getName().equals("tiledLayer")) {
                        //tiledLayer = new ArcGISMapImageLayer(layerList.get(position).getPath());
                    if (!name.equals("影像")) {
                        //map.getOperationalLayers().add(layers.get(position).getLayer());
                        map.getOperationalLayers().get(layers.get(position).getNum()).setVisible(true);
                        for (int i = 0; i < layerList.size(); i++){
                            if (name.equals(layerList.get(i).getName())) layerList.get(i).setStatus(true);
                        }
                    }
                    else {
                        for (int kk = 0; kk < TPKlayers.size(); kk++){
                            //map.getOperationalLayers().add(TPKlayers.get(kk).getLayer());
                            map.getOperationalLayers().get(TPKlayers.get(kk).getNum()).setVisible(true);
                        }
                        for (int i = 0; i < layerList.size(); i++){
                            if (name.equals(layerList.get(i).getName())) layerList.get(i).setStatus(true);
                        }
                    }
                    //map.getOperationalLayers().get(position).setVisible(true);
                        Log.w(TAG, "选中后" + Integer.toString(map.getOperationalLayers().size()));
                    //}else if (layerList.get(position).getName().equals("censusLayer")) {
                        //censusLayer = new ArcGISMapImageLayer(layerList.get(position).getPath());
                        //map.getOperationalLayers().add(layers.get(position));
                        //Log.w(TAG, "选中后" + Integer.toString(map.getOperationalLayers().size()));
                    //}
                   // Log.w(TAG, "选中后" + Integer.toString(map.getOperationalLayers().size()));
                    mMapView.setMap(map);
                    //Log.w(TAG, "add: " + Integer.toString(size) );
                }
            }
        });
        //adapter.getItemSelected();
        recyclerView.setAdapter(adapter);
        /*Point xpt = new Point(m_long, m_lat, SpatialReferences.getWgs84());
        Log.w(TAG, "setRecyclerView: " + xpt.toString());
        //mMapView.setViewpointCenterAsync(xpt);
        SimpleMarkerSymbol pointSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.DIAMOND, Color.RED, 10);
        Graphic pointGraphic = new Graphic(xpt, pointSymbol);
        graphicsOverlay_66 = new GraphicsOverlay();
        graphicsOverlay_66.getGraphics().add(pointGraphic);
        try {
            mMapView.getGraphicsOverlays().add(graphicsOverlay_66);
        }catch (IllegalArgumentException e){
            mMapView.getGraphicsOverlays().remove(graphicsOverlay_66);
            mMapView.getGraphicsOverlays().add(graphicsOverlay_66);
        }*/
        isLoc = true;
    }
    boolean isLoc = false;
    List<layer1> TPKlayers = new ArrayList<>();
    //初始化传感器管理器
    private SensorManager sensorManager;
    @Override
    protected void onPause() {
        mMapView.pause();
        super.onPause();
        sensorManager.unregisterListener(listener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.resume();
        setRecyclerView();
        //注册传感器监听器
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_UI);
        //Log.w(TAG, "getMapScale: " + mMapView.getMapScale());
        //Log.w(TAG, "getVisibleArea: " + mMapView.getVisibleArea().getExtent().getCenter());
    }
    double OriginScale;
    @Override
    protected void onDestroy() {
        super.onDestroy();
        getCacheDir().delete();
        getFilesDir().delete();
        mMapView.dispose();
    }
}
