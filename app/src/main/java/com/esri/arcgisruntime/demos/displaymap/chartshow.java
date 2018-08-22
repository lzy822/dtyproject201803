package com.esri.arcgisruntime.demos.displaymap;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import org.litepal.LitePal;

import java.util.List;

public class chartshow extends AppCompatActivity {
    private static final String TAG = "chartshow";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chartshow);
        Intent intent = getIntent();
        String xzqdm = intent.getStringExtra("xzqdm");
        List<xzq> xzqs = LitePal.where("xzqdm = ?", xzqdm).find(xzq.class);
        Log.w(TAG, "onCreate: " + xzqs.size() + "; " + xzqs.get(0).getXzqmc());
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //LitePal.deleteAll(xzq.class, "xzqmc = ? and type = ?", "凤庆县", "乡级");
        //Log.w(TAG, "onCreate: " + DataUtil.xzqClassify(LitePal.findAll(xzq.class)));
        /*long[] nums = DataUtil.xzqCalGrade(LitePal.findAll(xzq.class));
        Log.w(TAG, "onCreate: " + nums[0] + "; " + nums[1]);
        List<xzq> xzqs = LitePal.where("xzqmc = ?", "凤庆县").find(xzq.class);
        for (xzq xzq: xzqs){
            Log.w(TAG, "onCreate: " + xzq.getType() + "; " + xzq.getGrade());
        }*/
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
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
        });
    }

}
