package com.esri.arcgisruntime.demos.displaymap;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PointF;
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
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.data.FeatureTable;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.Part;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.PointCollection;
import com.esri.arcgisruntime.geometry.Polyline;
import com.esri.arcgisruntime.geometry.PolylineBuilder;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.ArcGISMapImageLayer;
import com.esri.arcgisruntime.layers.ArcGISMapImageSublayer;
import com.esri.arcgisruntime.layers.ArcGISTiledLayer;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.layers.Layer;
import com.esri.arcgisruntime.layers.SublayerList;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.MobileMapPackage;
import com.esri.arcgisruntime.mapping.popup.Popup;
import com.esri.arcgisruntime.mapping.view.Callout;
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.IdentifyLayerResult;
import com.esri.arcgisruntime.mapping.view.LayerViewStateChangedEvent;
import com.esri.arcgisruntime.mapping.view.LayerViewStateChangedListener;
import com.esri.arcgisruntime.mapping.view.MapView;
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
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    private MapView mMapView;
    private static final String TAG = "MainActivity";
    private static final String rootPath = Environment.getExternalStorageDirectory().toString() + "/weizhi_test.mmpk";
    private List<layer> layerList = new ArrayList<>();
    private List<Layer> layers = new ArrayList<>();
    private layerAdapter adapter;
    private RecyclerView recyclerView;
    private GridLayoutManager layoutManager;
    private ImageButton recyclerViewButton;
    FeatureLayer mFeaturelayer;
    private boolean isClick = false;
    ArcGISMap map;
    public static final String READ_EXTERNAL_STORAGE = "android.permission.READ_EXTERNAL_STORAGE";
    public static final String WRITE_EXTERNAL_STORAGE = "android.permission.WRITE_EXTERNAL_STORAGE";
    public static final int PERMISSION_CODE = 42042;
    com.github.clans.fab.FloatingActionButton whiteBlank_fab;
    //记录画笔颜色
    private int color_Whiteblank;
    Location location;

    private LocationManager locationManager;
    GraphicsOverlay graphicsOverlay_66;

    //获取当前坐标位置
    private void getLocation() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (!(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))) {
            Toast.makeText(this, this.getResources().getText(R.string.LocError), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(intent, 0);
            return;
        }

        try {

            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(location == null){
                Log.d(TAG, "onCreate.location = null");
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
            Log.d(TAG, "onCreate.location = " + location);
            updateView(location);

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, locationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5, locationListener);
        }catch (SecurityException  e){
            e.printStackTrace();
        }
    }

    //坐标监听器
    protected final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            //Log.d(TAG, "Location changed to: " + getLocationInfo(location));
            updateView(location);
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
            }catch (SecurityException e){

            }
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.d(TAG, "onProviderDisabled() called with " + "provider = [" + provider + "]");
        }
    };
    double m_lat = 0, m_long = 0;

    //更新坐标信息
    private void updateView(Location location) {
        if (location != null){
            m_lat = location.getLatitude();
            m_long = location.getLongitude();
            SharedPreferences.Editor editor = getSharedPreferences("latlong", MODE_PRIVATE).edit();
            editor.clear().commit();
            editor.putString("mlatlong", Double.toString(m_lat) + "," + Double.toString(m_long));
            editor.apply();
            if (isLoc) {
                Point xpt = new Point(m_long, m_lat, SpatialReferences.getWgs84());
                Log.w(TAG, "setRecyclerView: " + xpt.toString());
                mMapView.setViewpointCenterAsync(xpt);
                SimpleMarkerSymbol pointSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.DIAMOND, Color.RED, 10);
                Graphic pointGraphic = new Graphic(xpt, pointSymbol);
                graphicsOverlay_66 = new GraphicsOverlay();
                graphicsOverlay_66.getGraphics().add(pointGraphic);
                mMapView.getGraphicsOverlays().remove(graphicsOverlay_66);
                mMapView.getGraphicsOverlays().add(graphicsOverlay_66);
            }
        }
    }
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
    PointCollection points = new PointCollection(SpatialReferences.getWgs84());;
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
                List<whiteblank> whiteblanks = DataSupport.findAll(whiteblank.class);
                DataSupport.delete(whiteblank.class, whiteblanks.size());
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
                DataSupport.deleteAll(whiteblank.class);
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
                        geometryWhiteBlank.setLineSymbol(color_Whiteblank);
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
                            SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, color_Whiteblank, 10);
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

    //记录是否开启白板功能
    private boolean isOpenWhiteBlank = false;
    Callout mCallout;
    boolean inMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        graphicsOverlay_66 = new GraphicsOverlay();
        //获取定位信息
        getLocation();
        color_Whiteblank = Color.RED;
        //DataSupport.deleteAll(whiteblank.class);
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
                                                                Log.w(TAG, "" + Integer.toString(mainArcGISMapL.size()) );
                                                                //ArcGISMap mainArcrun: GISMapMMPK = mainArcGISMapL.get(0);
                                                                map = mainArcGISMapL.get(0);
                                                                int size = map.getOperationalLayers().size();
                                                                for (int i = 0; i < size; i++){
                                                                    layers.add(map.getOperationalLayers().get(i));
                                                                    layerList.add(new layer(map.getOperationalLayers().get(i).getName()));
                                                                }
                                                                mMapView.setMap(map);
                                                            }
                                                        }
                                                    });
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
                final Point wgs84Point = (Point) GeometryEngine.project(mapPoint, SpatialReferences.getWgs84());
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

                // center on tapped point
                mMapView.setViewpointCenterAsync(wgs84Point);
                Log.w(TAG, "onSingleTapConfirmed: " + wgs84Point);
                inMap = false;
                Log.w(TAG, "onSingleTapConfirmed: " );
                //final android.graphics.Point screenPoint=new android.graphics.Point(Math.round(v.getX()), Math.round(v.getY()));
                final Point clickPoint = mMapView.screenToLocation(screenPoint);
                Log.w(TAG, "onSingleTapConfirmed: " + mapPoint);
                QueryParameters query = new QueryParameters();
                query.setGeometry(clickPoint);// 设置空间几何对象
                /*
                if (mMapView.getMap().getOperationalLayers().size() != 0){
                    FeatureLayer featureLayer=(FeatureLayer) mMapView.getMap().getOperationalLayers().get(3);
                    FeatureTable mTable = featureLayer.getFeatureTable();//得到查询属性表
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
                                FeatureQueryResult featureResul = featureQueryResult.get();
                                for (Object element : featureResul) {
                                    if (element instanceof Feature) {
                                        Feature mFeatureGrafic = (Feature) element;
                                        Geometry geometry = mFeatureGrafic.getGeometry();
                                        GraphicsOverlay graphicsOverlay_1 = new GraphicsOverlay();
                                        SimpleMarkerSymbol pointSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.DIAMOND, Color.RED, 10);
                                        SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.GREEN,3);
                                        Graphic pointGraphic = new Graphic(clickPoint,pointSymbol);
                                        Graphic fillGraphic = new Graphic(geometry,lineSymbol);
                                        graphicsOverlay_1.getGraphics().add(pointGraphic);
                                        graphicsOverlay_1.getGraphics().add(fillGraphic);
                                        mMapView.getGraphicsOverlays().add(graphicsOverlay_1);
                                        Map<String, Object> mQuerryString = mFeatureGrafic.getAttributes();
                                        TextView calloutContent = new TextView(getApplicationContext());
                                        calloutContent.setTextColor(Color.BLACK);
                                        //calloutContent.setSingleLine();
                                        // format coordinates to 4 decimal places
                                        String str = "";
                                        for(String key : mQuerryString.keySet()){
                                            str = str + key + " : " + String.valueOf(mQuerryString.get(key)) + "\n";
                                        }
                                        calloutContent.setText(str);
                                        // get callout, set content and show
                                        mCallout.setLocation(mapPoint);
                                        mCallout.setContent(calloutContent);
                                        mCallout.show();
                                        inMap = true;
                                    }
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    });
                }
                */
                if (!inMap) mCallout.dismiss();
                //FeatureLayer featureLayer=(FeatureLayer) mMapView.getMap().getBasemap().getBaseLayers().get(0);
                return true;
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
                                    SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.DASH,Color.GREEN,10);
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

        List<whiteblank> whiteblanks = DataSupport.findAll(whiteblank.class);
        int size = whiteblanks.size();
        Log.w(TAG, "onCreate: " + size);
        if (size == 0) graphicsOverlay_10 = new GraphicsOverlay(GraphicsOverlay.RenderingMode.DYNAMIC);
        else {
            graphicsOverlay_10 = new GraphicsOverlay(GraphicsOverlay.RenderingMode.DYNAMIC);
            for (int i = 0; i < size; i++){
                points.clear();
                //geometry_WhiteBlank geometryWhiteBlank = new geometry_WhiteBlank(whiteblanks.get(i).getLineSymbol(), whiteblanks.get(i).getPolyline());
                String[] strings = whiteblanks.get(i).getPts().split("lzy");
                for (int kk = 0; kk < strings.length; kk++){
                    String[] strings1 = strings[kk].split(",");
                    Point wgs84Point = (Point) GeometryEngine.project(new Point(Double.valueOf(strings1[0]), Double.valueOf(strings1[1])), SpatialReferences.getWgs84());
                    points.add(wgs84Point);
                }
                SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, whiteblanks.get(i).getColor(), 10);
                Polyline polyline = new Polyline(points);
                Graphic g = new Graphic(polyline, lineSymbol);
                graphics.add(g);
            }
            if (graphics.size() > 0) {
                for (int j = 0; j < graphics.size(); j++){
                    graphicsOverlay_10.getGraphics().add(graphics.get(j));
                }
            }
            if (mMapView.getGraphicsOverlays().size() != 0) mMapView.getGraphicsOverlays().remove(graphicsOverlay_10);
            mMapView.getGraphicsOverlays().add(graphicsOverlay_10);
            }

        mCallout = mMapView.getCallout();
    }

    private void initMap(){
        //tiledLayer = new ArcGISMapImageLayer("http://services.arcgisonline.com/arcgis/rest/services/World_Imagery/MapServer");
        //censusLayer = new ArcGISMapImageLayer("http://sampleserver6.arcgisonline.com/arcgis/rest/services/Census/MapServer");
        ArcGISMapImageLayer layer2 = new ArcGISMapImageLayer("http://services.arcgisonline.com/arcgis/rest/services/World_Imagery/MapServer");
        //map.getOperationalLayers().add(tiledLayer);
        layers.add(layer2);
        layer layer1 = new layer("ArcGISMapImageLayer", "http://sampleserver6.arcgisonline.com/arcgis/rest/services/Census/MapServer", "censusLayer", true);
        layerList.add(layer1);
        layer2 = new ArcGISMapImageLayer("http://sampleserver6.arcgisonline.com/arcgis/rest/services/Census/MapServer");
        layers.add(layer2);
        Log.w(TAG, "size: " + Integer.toString(layers.size()));
        int size = layers.size();
        for (int i = 0; i < size;i++){
            map.getOperationalLayers().add(layers.get(i));
        }
        ServiceFeatureTable serviceFeatureTable = new ServiceFeatureTable("http://sampleserver6.arcgisonline.com/arcgis/rest/services/PoolPermits/FeatureServer/0");
        FeatureLayer featureLayer = new FeatureLayer(serviceFeatureTable);
        map.getOperationalLayers().add(featureLayer);

        //map.getOperationalLayers().add(censusLayer);
        layer1 = new layer("ArcGISMapImageLayer", "http://services.arcgisonline.com/arcgis/rest/services/World_Imagery/MapServer", "tiledLayer", true);
        layerList.add(layer1);
    }

    public void searchForState(final String searchString) {

        // clear any previous selections

        //mFeaturelayer.clearSelection();

        // create objects required to do a selection with a query
        QueryParameters query = new QueryParameters();
        //make search case insensitive
        query.setWhereClause("upper(BQMC) LIKE '%" + searchString.toUpperCase() + "%'");
        Log.w(TAG, "searchForState: " );
        // call select features
        if (mMapView.getMap().getOperationalLayers().size() != 0) {

            Log.w(TAG, "searchForState: getAttribution" + mMapView.getMap().getOperationalLayers().get(3).getAttribution());
            Log.w(TAG, "searchForState: getDescription" + mMapView.getMap().getOperationalLayers().get(3).getDescription());
            mFeaturelayer = (FeatureLayer) mMapView.getMap().getOperationalLayers().get(3);
            FeatureTable mTable = mFeaturelayer.getFeatureTable();//得到查询属性表
            Log.w(TAG, "searchForState: " + mTable.getFields().get(0) );
            final ListenableFuture<FeatureQueryResult> featureQueryResult
                    = mTable.queryFeaturesAsync(query);
            featureQueryResult.addDoneListener(new Runnable() {
                @Override
                public void run() {
                    try {
                        // call get on the future to get the result
                        FeatureQueryResult result = featureQueryResult.get();

                        // check there are some results
                        if (result.iterator().hasNext()) {

                            // get the extend of the first feature in the result to zoom to
                            Feature feature = result.iterator().next();
                            Envelope envelope = feature.getGeometry().getExtent();
                            mMapView.setViewpointGeometryAsync(envelope, 200);

                            //Select the feature
                            mFeaturelayer.selectFeature(feature);

                        } else {
                            Toast.makeText(MainActivity.this, "No states found with name: " + searchString, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, "Feature search failed for: " + searchString + ". Error=" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e(getResources().getString(R.string.app_name), "Feature search failed for: " + searchString + ". Error=" + e.getMessage());
                    }
                }
            });
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
                        searchForState(query);
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
                        map.getOperationalLayers().remove(layers.get(position));
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
                        map.getOperationalLayers().add(layers.get(position));
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
        Point xpt = new Point(m_long, m_lat, SpatialReferences.getWgs84());
        Log.w(TAG, "setRecyclerView: " + xpt.toString());
        mMapView.setViewpointCenterAsync(xpt);
        SimpleMarkerSymbol pointSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.DIAMOND, Color.RED, 10);
        Graphic pointGraphic = new Graphic(xpt, pointSymbol);
        graphicsOverlay_66 = new GraphicsOverlay();
        graphicsOverlay_66.getGraphics().add(pointGraphic);
        try {
            mMapView.getGraphicsOverlays().add(graphicsOverlay_66);
        }catch (IllegalArgumentException e){
            mMapView.getGraphicsOverlays().remove(graphicsOverlay_66);
            mMapView.getGraphicsOverlays().add(graphicsOverlay_66);
        }
        isLoc = true;
    }
boolean isLoc = false;
    @Override
    protected void onPause() {
        mMapView.pause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.resume();

        setRecyclerView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.dispose();
    }
}
