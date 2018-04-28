package com.esri.arcgisruntime.demos.displaymap;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

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
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
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
    private List<layer> layerList = new ArrayList<>();
    private List<ArcGISMapImageLayer> layers = new ArrayList<>();
    private layerAdapter adapter;
    private RecyclerView recyclerView;
    private GridLayoutManager layoutManager;
    ArcGISMapImageLayer tiledLayer;
    ArcGISMapImageLayer censusLayer;
    ArcGISMap map;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMapView = findViewById(R.id.mapView);// = new ArcGISMap(Basemap.Type.TOPOGRAPHIC, 34.056295, -117.195800, 16);
        map = new ArcGISMap();
        initMap();
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
        Log.w(TAG, "onCreate: "  );
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
        adapter = new layerAdapter(layerList);
        adapter.setOnItemCheckListener(new layerAdapter.OnRecyclerItemCheckListener() {
            @Override
            public void onItemCheckClick(layerAdapter.ViewHolder holder, String name, int position) {
                if (!holder.checkBox.isChecked()){
                    holder.checkBox.setChecked(false);
                    layerList.get(position).setStatus(false);
                    if (layerList.get(position).getName().equals("tiledLayer")) {
                        map.getOperationalLayers().remove(layers.get(position));
                        Log.w(TAG, "取消选中后" + Integer.toString(map.getOperationalLayers().size()));
                    }else if (layerList.get(position).getName().equals("censusLayer")) {
                        map.getOperationalLayers().remove(layers.get(position));
                        Log.w(TAG, "取消选中后" + Integer.toString(map.getOperationalLayers().size()));
                    }
                    Log.w(TAG, "取消选中后" + Integer.toString(map.getOperationalLayers().size()));
                    mMapView.setMap(map);
                }else {
                    holder.checkBox.setChecked(true);
                    layerList.get(position).setStatus(true);
                    if (layerList.get(position).getName().equals("tiledLayer")) {
                        tiledLayer = new ArcGISMapImageLayer(layerList.get(position).getPath());
                        map.getOperationalLayers().add(layers.get(position));
                        Log.w(TAG, "选中后" + Integer.toString(map.getOperationalLayers().size()));
                    }else if (layerList.get(position).getName().equals("censusLayer")) {
                        censusLayer = new ArcGISMapImageLayer(layerList.get(position).getPath());
                        map.getOperationalLayers().add(layers.get(position));
                        Log.w(TAG, "选中后" + Integer.toString(map.getOperationalLayers().size()));
                    }
                    Log.w(TAG, "选中后" + Integer.toString(map.getOperationalLayers().size()));
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.dispose();
    }
}
