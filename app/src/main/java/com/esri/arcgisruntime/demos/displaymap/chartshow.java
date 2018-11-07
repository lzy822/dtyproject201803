package com.esri.arcgisruntime.demos.displaymap;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.esri.arcgisruntime.ArcGISRuntimeException;
import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.data.FeatureTable;
import com.esri.arcgisruntime.data.Field;
import com.esri.arcgisruntime.data.Geodatabase;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.geometry.AreaUnit;
import com.esri.arcgisruntime.geometry.AreaUnitId;
import com.esri.arcgisruntime.geometry.GeodeticCurveType;
import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;

import org.litepal.LitePal;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lecho.lib.hellocharts.listener.ColumnChartOnValueSelectListener;
import lecho.lib.hellocharts.listener.PieChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.ColumnChartView;
import lecho.lib.hellocharts.view.PieChartView;

public class chartshow extends AppCompatActivity {
    private static final String TAG = "chartshow";
    private static final String rootPath1 = Environment.getExternalStorageDirectory().toString() + "/临沧市基本农田/临沧市5309省标准乡级土地利用总体规划及基本农田数据库2000.geodatabase";

    ProgressBar progressBar;
    TextView chartdata;
    PieChartView pieChartView;
    ColumnChartView columnChartView;
    private String selectedItem = "";
    List<KeyAndValue> mkeyAndValueList;


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

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.search).setVisible(false);
        menu.findItem(R.id.cancel).setVisible(true);
        menu.findItem(R.id.action_search).setVisible(false);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.cancel) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    int mposition = -1;
    private void refreshRecycler(final List<KeyAndValue> keyAndValues){
        RecyclerView recyclerView1 = (RecyclerView) findViewById(R.id.chart_recycler_view);
        recyclerView1.setVisibility(View.VISIBLE);
        GridLayoutManager layoutManager1 = new GridLayoutManager(chartshow.this,2);
        recyclerView1.setLayoutManager(layoutManager1);
        final KVAdapter adapter1 = new KVAdapter(keyAndValues);
        adapter1.setOnItemClickListener(new KVAdapter.OnRecyclerItemClickListener() {
            @Override
            public void onItemClick(View view, String name, int position) {
                selectedItem = name;
                /*SharedPreferences.Editor editor = getSharedPreferences("queryType", MODE_PRIVATE).edit();
                editor.putString("tdghdl", name);
                editor.apply();
                refreshRecycler(keyAndValues);*/
                if (mposition != -1) {
                    adapter1.notifyItemChanged(mposition);
                }
                showKV(keyAndValues);
                mposition = position;
            }
        });
        recyclerView1.setAdapter(adapter1);
    }

    private void refreshRecyclerAndChart(){
            selectedItem = "";
            refreshRecycler(mkeyAndValueList);
            showKV(mkeyAndValueList);
    }

    private void showKV(final List<KeyAndValue> keyAndValues){
        mkeyAndValueList = keyAndValues;
        List<SliceValue> sliceValues = new ArrayList<>();
        String data = "";
        DecimalFormat decimalFormat = new DecimalFormat("0.0");
        DecimalFormat decimalFormat1 = new DecimalFormat("0.00");
        List<Column> columns = new ArrayList<Column>();
        if (selectedItem.isEmpty()) {
            Log.w(TAG, "showKV: " + "empty");
            for (int i = 0; i < keyAndValues.size(); ++i) {
                int color = ChartUtils.pickColor();
                float value = Float.valueOf(keyAndValues.get(i).getValue()) / (float) wholeArea;
                SliceValue sliceValue = new SliceValue(Float.valueOf(keyAndValues.get(i).getValue()) / (float) wholeArea, color);
                sliceValue.setLabel(keyAndValues.get(i).getName() + "占比:" + decimalFormat1.format(value * 100) + "%");
                sliceValues.add(sliceValue);
                Log.w(TAG, "run: " + keyAndValues.get(i).getName() + ": " + keyAndValues.get(i).getValue());
                data = data + keyAndValues.get(i).getName() + ": " + decimalFormat.format(Double.valueOf(keyAndValues.get(i).getValue())) + "亩" + "\n";
                List<SubcolumnValue> values = new ArrayList<>();
                //values.add(new SubcolumnValue(Float.valueOf(keyAndValues.get(i).getValue()) / (float) wholeArea, ChartUtils.pickColor()));
                values.add(new SubcolumnValue(Float.valueOf(keyAndValues.get(i).getValue()), color));
                Column column = new Column(values);
                column.setHasLabels(true);
                column.setHasLabelsOnlyForSelected(true);
                columns.add(column);
            }
        }else {
            Log.w(TAG, "showKV: " + "full");
            for (int i = 0; i < keyAndValues.size(); ++i) {
                data = data + keyAndValues.get(i).getName() + ": " + decimalFormat.format(Double.valueOf(keyAndValues.get(i).getValue())) + "亩" + "\n";
                List<SubcolumnValue> values = new ArrayList<>();
                Column column = new Column(values);
                column.setHasLabels(true);
                //column.setHasLabelsOnlyForSelected(true);
                if (keyAndValues.get(i).getName().equals(selectedItem)) {
                    sliceValues.add( new SliceValue( Float.valueOf(keyAndValues.get(i).getValue()) / (float) wholeArea, ChartUtils.COLOR_RED));
                    values.add(new SubcolumnValue(Float.valueOf(keyAndValues.get(i).getValue()), ChartUtils.COLOR_RED));
                    column.setHasLabelsOnlyForSelected(false);
                }else {
                    sliceValues.add( new SliceValue( Float.valueOf(keyAndValues.get(i).getValue()) / (float) wholeArea, ChartUtils.COLOR_GREEN));
                    values.add(new SubcolumnValue(Float.valueOf(keyAndValues.get(i).getValue()), ChartUtils.COLOR_GREEN));
                    column.setHasLabelsOnlyForSelected(true);
                }
                columns.add(column);

            }
        }
        //chartdata.setGravity(Gravity.TOP);
        //chartdata.setText(data);
        //List<Column> columns = new
        TextView textView = (TextView) findViewById(R.id.chart_textshow);
        textView.setVisibility(View.GONE);
        ColumnChartData columnChartData = new ColumnChartData(columns);
        Axis axisX = new Axis();
        List<AxisValue> xAxisValues = new ArrayList<>();
        for (int i = 0; i < keyAndValues.size(); ++i){
            xAxisValues.add(new AxisValue(i, Integer.toString(i + 1).toCharArray()));
        }
        axisX.setValues(xAxisValues);
        axisX.setName("土地规划地类");
        Axis axisY = new Axis().setHasLines(true);
        axisY.setName("面积");
        columnChartData.setAxisXBottom(axisX);
        columnChartData.setAxisYLeft(axisY);
        columnChartView.setColumnChartData(columnChartData);
        columnChartView.setValueSelectionEnabled(true);
        final List<KeyAndValue> kv = keyAndValues;
        columnChartView.setOnValueTouchListener(new ColumnChartOnValueSelectListener() {
            @Override
            public void onValueSelected(int i, int i1, SubcolumnValue subcolumnValue) {
                if (!selectedItem.isEmpty()) {
                    SharedPreferences.Editor editor = getSharedPreferences("queryType", MODE_PRIVATE).edit();
                    editor.putString("tdghdl", "");
                    editor.apply();
                    refreshRecyclerAndChart();
                }
                Log.w(TAG, "onValueSelected: " + subcolumnValue.getValue());
                DecimalFormat decimalFormat = new DecimalFormat("0.0");
                DecimalFormat decimalFormat1 = new DecimalFormat("0.000000");
                for (int j = 0; j < kv.size(); ++j){
                    if (decimalFormat1.format((float)(Float.valueOf(kv.get(j).getValue()))).equals(decimalFormat1.format(subcolumnValue.getValue()))) {
                        Toast.makeText(chartshow.this, kv.get(j).getName() + "占地: " + decimalFormat.format(subcolumnValue.getValue()) + "亩", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
            }

            @Override
            public void onValueDeselected() {

            }
        });

        //columnChartView.setY(chartdata.getBottom());
        columnChartView.setVisibility(View.VISIBLE);
        PieChartData pieChartData = new PieChartData(sliceValues);
        if (!selectedItem.isEmpty()){
            for (int i = 0; i < keyAndValues.size(); ++i){
                if (keyAndValues.get(i).getName().equals(selectedItem)) {
                    pieChartData.setCenterText1(selectedItem);
                    pieChartData.setCenterText1FontSize(21);
                    pieChartData.setCenterText2(decimalFormat1.format(Double.valueOf(keyAndValues.get(i).getValue()) / wholeArea * 100) + "%");
                    pieChartData.setCenterText2Color(ChartUtils.COLOR_RED);
                    break;
                }
            }
        }else pieChartData.setCenterText1("各类占比");
        pieChartData.setHasCenterCircle(true);
        pieChartData.setHasLabels(true);
        pieChartData.setHasLabelsOutside(false);
        pieChartData.setHasLabelsOnlyForSelected(true);
        pieChartView.setValueSelectionEnabled(true);
        pieChartView.setPieChartData(pieChartData);
        pieChartView.setOnValueTouchListener(new PieChartOnValueSelectListener() {
            @Override
            public void onValueSelected(int i, SliceValue sliceValue) {
                if (!selectedItem.isEmpty()) {
                    SharedPreferences.Editor editor = getSharedPreferences("queryType", MODE_PRIVATE).edit();
                    editor.putString("tdghdl", "");
                    editor.apply();
                    refreshRecyclerAndChart();
                }
                Log.w(TAG, "onValueSelected: " + sliceValue.getValue());
                DecimalFormat decimalFormat = new DecimalFormat("0.00");
                DecimalFormat decimalFormat1 = new DecimalFormat("0.000000");
                for (int j = 0; j < kv.size(); ++j){
                    if (decimalFormat1.format((float)(Float.valueOf(kv.get(j).getValue()) / wholeArea)).equals(decimalFormat1.format(sliceValue.getValue()))) {
                        Toast.makeText(chartshow.this, kv.get(j).getName() + "占比: " + decimalFormat.format(sliceValue.getValue() * 100) + "%", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
            }

            @Override
            public void onValueDeselected() {
            }
        });

        //pieChartView.setY(chartdata.getBottom());
        pieChartView.setVisibility(View.VISIBLE);
    }

    private String classifyKV(final List<KeyAndValue> keyAndValues){
        String kvString = "";
        for (int i = 0; i < keyAndValues.size(); ++i){
            if (i != keyAndValues.size() - 1)
                kvString = kvString + keyAndValues.get(i).getName() + ":" + keyAndValues.get(i).getValue() + ",";
            else kvString = kvString + keyAndValues.get(i).getNickname();
        }
        return kvString;
    }

    private List<KeyAndValue> parseKV(final String keyAndValues){
        List<KeyAndValue> keyAndValues1 = new ArrayList<>();
        String[] strings1 = keyAndValues.split(",");
        for (int i = 0; i < strings1.length; ++i){
            if (strings1.length - 1 != i) {
                String[] strings = strings1[i].split(":");
                keyAndValues1.add(new KeyAndValue(strings1[strings1.length - 1], strings[0], strings[1]));
            }else wholeArea = Double.valueOf(strings1[i]);
        }
        return keyAndValues1;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chartshow);
        pieChartView = (PieChartView) findViewById(R.id.pie);
        columnChartView = (ColumnChartView) findViewById(R.id.column);
        progressBar = (ProgressBar) findViewById(R.id.progressBarLarge);
        //chartdata = (TextView) findViewById(R.id.chartdata);
        Intent intent = getIntent();
        String xzqdm = intent.getStringExtra("xzqdm");
        List<xzq> xzqs = LitePal.where("xzqdm = ?", xzqdm).find(xzq.class);
        Log.w(TAG, "onCreate: " + xzqs.size() + "; " + xzqs.get(0).getXzqmc());
        setTitle(xzqs.get(0).getXzqmc());
        List<memoryxzqinfo> memoryxzqinfoList = LitePal.where("name = ?", xzqdm).find(memoryxzqinfo.class);
        if (memoryxzqinfoList.size() == 0)
            queryFunction(xzqdm);
        else{
            progressBar.setVisibility(View.GONE);
            List<KeyAndValue> keyAndValues = parseKV(memoryxzqinfoList.get(0).getKeyAndValues());
            refreshRecycler(keyAndValues);
            showKV(keyAndValues);
        }
        //LitePal.deleteAll(xzq.class, "xzqmc = ? and type = ?", "凤庆县", "乡级");
        //Log.w(TAG, "onCreate: " + DataUtil.xzqClassify(LitePal.findAll(xzq.class)));
        /*long[] nums = DataUtil.xzqCalGrade(LitePal.findAll(xzq.class));
        Log.w(TAG, "onCreate: " + nums[0] + "; " + nums[1]);
        List<xzq> xzqs = LitePal.where("xzqmc = ?", "凤庆县").find(xzq.class);
        for (xzq xzq: xzqs){
            Log.w(TAG, "onCreate: " + xzq.getType() + "; " + xzq.getGrade());
        }*/
        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "确定要退出吗?", Snackbar.LENGTH_LONG)
                        .setAction("确定", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                chartshow.this.finish();
                            }
                        }).show();
            }
        });*/
    }
    double wholeArea;
    Geodatabase localGdb;
    FeatureLayer featureLayer777;
    FeatureLayer featureLayer778;
    private void queryFunction(final String xzqdm){
        File file = new File(rootPath1);
        if (file.exists()){
            localGdb = new Geodatabase(rootPath1);
            Log.w(TAG, "run: " + localGdb.getLoadStatus().toString());
            Log.w(TAG, "run: " + localGdb.getPath());
            localGdb.loadAsync();
            localGdb.addDoneLoadingListener(new Runnable() {
                @Override
                public void run() {
                    featureLayer777 = new FeatureLayer(localGdb.getGeodatabaseFeatureTable("土地规划地类"));
                    featureLayer778 = new FeatureLayer(localGdb.getGeodatabaseFeatureTable("行政区"));
                    /*FeatureTable featureTable = featureLayer778.getFeatureTable();
                    List<Field> list = featureTable.getFields();
                    for (int i = 0; i < list.size(); ++i){
                        Log.w(TAG, "run: " + list.get(i).getName());
                    }*/
                    QueryParameters query = new QueryParameters();
                    query.setWhereClause("XZQDM = " + xzqdm);
                    try {
                        FeatureTable mTable = featureLayer778.getFeatureTable();//得到查询属性表
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
                                            QueryParameters query1 = new QueryParameters();
                                            query1.setGeometry(geometry);
                                            wholeArea = 0;
                                            //final double wholeArea = GeometryEngine.areaGeodetic(geometry, new AreaUnit(AreaUnitId.SQUARE_KILOMETERS), GeodeticCurveType.GEODESIC) * 1500;
                                            FeatureTable mTable = featureLayer777.getFeatureTable();//得到查询属性表
                                            final ListenableFuture<FeatureQueryResult> featureQueryResult1
                                                    = mTable.queryFeaturesAsync(query1);
                                            featureQueryResult1.addDoneListener(new Runnable() {
                                                @Override
                                                public void run() {
                                                    try {
                                                        FeatureQueryResult featureResul = featureQueryResult1.get();
                                                        List<QueryTaskInfo> queryTaskInfos = new ArrayList<>();
                                                        List<KeyAndValue> keyAndValues = new ArrayList<>();
                                                        for (Object element : featureResul) {
                                                            if (element instanceof Feature) {
                                                                Feature mFeatureGrafic = (Feature) element;
                                                                Geometry geometry = mFeatureGrafic.getGeometry();
                                                                //Log.w(TAG, "run: " + GeometryEngine.areaGeodetic(geometry, new AreaUnit(AreaUnitId.SQUARE_KILOMETERS), GeodeticCurveType.GEODESIC) * 1500 + "亩");
                                                                wholeArea = wholeArea + GeometryEngine.areaGeodetic(geometry, new AreaUnit(AreaUnitId.SQUARE_KILOMETERS), GeodeticCurveType.GEODESIC) * 1500;
                                                                Map<String, Object> mQuerryString = mFeatureGrafic.getAttributes();
                                                                //calloutContent.setSingleLine();
                                                                // format coordinates to 4 decimal places
                                                                QueryTaskInfo queryTaskInfo = new QueryTaskInfo(GeometryEngine.areaGeodetic(geometry, new AreaUnit(AreaUnitId.SQUARE_KILOMETERS), GeodeticCurveType.GEODESIC) * 1500);
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
                                                        for (int i = 0; i < queryTaskInfos.size(); ++i) {
                                                            if (i == 0 && queryTaskInfos.get(i).getArea() != 0)
                                                                keyAndValues.add(new KeyAndValue(Double.toString(wholeArea), queryTaskInfos.get(i).getTypename(), Double.toString(queryTaskInfos.get(i).getArea())));
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
                                                                    keyAndValues.add(new KeyAndValue(Double.toString(wholeArea), queryTaskInfos.get(i).getTypename(), Double.toString(queryTaskInfos.get(i).getArea())));
                                                                }
                                                            }
                                                        }
                                                        progressBar.setVisibility(View.GONE);
                                                        memoryxzqinfo memoryxzqinfo = new memoryxzqinfo();
                                                        memoryxzqinfo.setName(xzqdm);
                                                        memoryxzqinfo.setKeyAndValues(classifyKV(keyAndValues));
                                                        memoryxzqinfo.save();
                                                        refreshRecycler(keyAndValues);
                                                        showKV(keyAndValues);


                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            });
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    } catch (ArcGISRuntimeException e) {
                        Toast.makeText(chartshow.this, e.getMessage() + e.getErrorCode(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
            Log.w(TAG, "run: " + localGdb.getLoadStatus().toString());
        } else Toast.makeText(chartshow.this, R.string.QueryError_1, Toast.LENGTH_SHORT).show();


    }

    @Override
    protected void onDestroy() {
        SharedPreferences.Editor editor = getSharedPreferences("queryType", MODE_PRIVATE).edit();
        editor.putString("tdghdl", "");
        editor.apply();
        super.onDestroy();
    }
}
