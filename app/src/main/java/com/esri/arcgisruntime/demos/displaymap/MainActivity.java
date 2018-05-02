package com.esri.arcgisruntime.demos.displaymap;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.data.FeatureTable;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.Point;
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
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.LayerViewStateChangedEvent;
import com.esri.arcgisruntime.mapping.view.LayerViewStateChangedListener;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.esri.arcgisruntime.util.ListChangedEvent;
import com.esri.arcgisruntime.util.ListChangedListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private MapView mMapView;
    private static final String TAG = "MainActivity";
    private static final String rootPath = Environment.getExternalStorageDirectory().toString() + "/SanFrancisco.mmpk";
    private List<layer> layerList = new ArrayList<>();
    private List<Layer> layers = new ArrayList<>();
    private layerAdapter adapter;
    private RecyclerView recyclerView;
    private GridLayoutManager layoutManager;
    private ImageButton recyclerViewButton;
    private ImageButton recyclerViewButton1;
    private boolean isClick = false;
    ArcGISMapImageLayer tiledLayer;
    ArcGISMapImageLayer censusLayer;
    ArcGISMap map;
    public static final String READ_EXTERNAL_STORAGE = "android.permission.READ_EXTERNAL_STORAGE";
    public static final String WRITE_EXTERNAL_STORAGE = "android.permission.WRITE_EXTERNAL_STORAGE";
    public static final int PERMISSION_CODE = 42042;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerViewButton = (ImageButton) findViewById(R.id.openRecyclerView);
        recyclerViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isClick == true){
                    recyclerView.setVisibility(View.GONE);
                    recyclerViewButton.setBackgroundResource(R.drawable.ic_expand_less_black_24dp);
                    recyclerViewButton.setX(recyclerView.getX() + recyclerView.getWidth() + 50);
                }else {
                    //setRecyclerView();
                    recyclerView.setVisibility(View.VISIBLE);
                    recyclerViewButton1.setVisibility(View.VISIBLE);
                    recyclerViewButton.setVisibility(View.GONE);
                    isClick = true;
                }
                //Log.w(TAG, "onClick: " + recyclerViewButton.getBackground().toString() );
            }
        });
        recyclerViewButton1 = (ImageButton) findViewById(R.id.openRecyclerView1);
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
        });
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
                                                                Log.w(TAG, "run: " + Integer.toString(mainArcGISMapL.size()) );
                                                                //ArcGISMap mainArcGISMapMMPK = mainArcGISMapL.get(0);
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
                Log.w(TAG, "onSingleTapConfirmed: " );
                android.graphics.Point screenPoint=new android.graphics.Point(Math.round(v.getX()), Math.round(v.getY()));
                final Point clickPoint = mMapView.screenToLocation(screenPoint);
                QueryParameters query = new QueryParameters();
                query.setGeometry(clickPoint);// 设置空间几何对象
                FeatureLayer featureLayer=(FeatureLayer) mMapView.getMap().getOperationalLayers().get(0);
                //FeatureLayer featureLayer=(FeatureLayer) mMapView.getMap().getBasemap().getBaseLayers().get(0);
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
                                    Geometry geometry = mFeatureGrafic.getGeometry();
                                    GraphicsOverlay graphicsOverlay_1 = new GraphicsOverlay();
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
    }

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
