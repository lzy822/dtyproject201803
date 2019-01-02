package com.esri.arcgisruntime.demos.displaymap;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Activity_FileManage extends AppCompatActivity {

    private static final String TAG = "Activity_FileManage";
    private List<FileManage> fileManages = new ArrayList<>();
    private FileManageAdapter adapter;
    private RecyclerView recyclerView;
    private GridLayoutManager layoutManager;
    private final String DeviceRootPath = Environment.getExternalStorageDirectory().toString();
    String type = "";
    //声明Toolbar
    Toolbar toolbar;

    FileManage fileManage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__file_manage);
        Log.w(TAG, "aaa" + Environment.getExternalStorageDirectory().toString());

        Intent intent = getIntent();
        type = intent.getStringExtra("type");
        toolbar = (Toolbar) findViewById(R.id.toolbar_filemanage);
        toolbar.setTitle(R.string.FileManage);
        toolbar.inflateMenu(R.menu.filemanagemenu);
        toolbar.setBackgroundColor(getResources().getColor(R.color.Black));
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch(item.getItemId()) {
                    case R.id.back_filemanage:
                        if (!fileManage.getRootPath().equals(Environment.getExternalStorageDirectory().toString())) {
                            fileManages.clear();
                            fileManage = fileManage.SelectLast();
                            String RootPath = fileManage.getRootPath();
                            String[] strings = fileManage.getFileSubset(FileManage.MERGERSORT);
                            for (int i = 0; i < strings.length; i++) {
                                if (!strings[i].contains(type))
                                    fileManages.add(new FileManage(RootPath + "/" + strings[i], RootPath + "/", type));
                                else {
                                    fileManages.add(new FileManage(RootPath + "/" + strings[i], RootPath + "/", 1));
                                }
                            }
                            toolbar.setTitle(RootPath.replace(DeviceRootPath, "文件管理器"));
                            refreshRecycler();
                        }else {
                            Activity_FileManage.this.finish();
                        }
                }
                return true;
            }
        });
        String filepath = "";
        if (type.equals(".shp")) {
            SharedPreferences prf1 = getSharedPreferences("filepath", MODE_PRIVATE);
            filepath = prf1.getString("shp_inputpath", "");
            Log.w(TAG, "onCreate: " + filepath);
        }else if (type.equals(".zip")){
            SharedPreferences prf1 = getSharedPreferences("filepath", MODE_PRIVATE);
            filepath = prf1.getString("inputpath", "");
            Log.w(TAG, "onCreate: " + filepath);
        }else if (type.equals(".tif")){
            SharedPreferences prf1 = getSharedPreferences("filepath", MODE_PRIVATE);
            filepath = prf1.getString("tif_inputpath", "");
            Log.w(TAG, "onCreate: " + filepath);
        }
        if (filepath.isEmpty()) {
            fileManage = new FileManage(type);
            String[] strings = fileManage.getFileSubset(FileManage.MERGERSORT);
            for (int i = 0; i < strings.length; i++) {
                if (!strings[i].contains(type))
                    fileManages.add(new FileManage(Environment.getExternalStorageDirectory().toString() + "/" + strings[i], Environment.getExternalStorageDirectory().toString() + "/", type));
                else {
                    fileManages.add(new FileManage(Environment.getExternalStorageDirectory().toString() + "/" + strings[i], Environment.getExternalStorageDirectory().toString() + "/", 1));
                }
            }
            toolbar.setTitle(fileManage.getRootPath().replace(DeviceRootPath, "文件管理器"));
        } else {
            File file = new File(filepath);
            if (file.exists() && file.isDirectory()) {
                fileManage = new FileManage(filepath, filepath.substring(0, filepath.lastIndexOf("/")), type);
                String[] strings = fileManage.getFileSubset(FileManage.MERGERSORT);
                for (int i = 0; i < strings.length; i++) {
                    if (!strings[i].contains(type))
                        fileManages.add(new FileManage(filepath + "/" + strings[i], filepath + "/", type));
                    else {
                        fileManages.add(new FileManage(filepath + "/" + strings[i], filepath + "/", 1));
                    }
                }
            } else {
                fileManage = new FileManage(type);
                String[] strings = fileManage.getFileSubset(FileManage.MERGERSORT);
                for (int i = 0; i < strings.length; i++) {
                    if (!strings[i].contains(type))
                        fileManages.add(new FileManage(Environment.getExternalStorageDirectory().toString() + "/" + strings[i], Environment.getExternalStorageDirectory().toString() + "/", type));
                    else {
                        fileManages.add(new FileManage(Environment.getExternalStorageDirectory().toString() + "/" + strings[i], Environment.getExternalStorageDirectory().toString() + "/", 1));
                    }
                }
            }
            toolbar.setTitle(fileManage.getRootPath().replace(DeviceRootPath, "文件管理器"));
        }
        refreshRecycler();
    }

    //重新刷新Recycler
    public void refreshRecycler(){
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_filemanage);
        layoutManager = new GridLayoutManager(this,1);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new FileManageAdapter(fileManages);
        adapter.setOnItemClickListener(new FileManageAdapter.OnRecyclerItemClickListener() {
            @Override
            public void onItemClick(View view, String RootPath, int position) {
                if (!RootPath.contains(type)) {
                    fileManages.clear();
                    fileManage = new FileManage(RootPath, RootPath.substring(0, RootPath.lastIndexOf("/")), type);
                    toolbar.setTitle(RootPath.replace(DeviceRootPath, "文件管理器"));
                    String[] strings = fileManage.getFileSubset(FileManage.MERGERSORT);
                    for (int i = 0; i < strings.length; i++) {
                        if (!strings[i].contains(type))
                            fileManages.add(new FileManage(RootPath + "/" + strings[i], RootPath + "/", type));
                        else {
                            fileManages.add(new FileManage(RootPath + "/" + strings[i], RootPath + "/", 1));
                        }
                    }
                    refreshRecycler();
                }else {
                    Intent i = new Intent();
                    i.putExtra("filePath", RootPath);
                    setResult(RESULT_OK, i);
                    SharedPreferences.Editor editor = getSharedPreferences("filepath", MODE_PRIVATE).edit();
                    String RootPathForTrue = RootPath.substring(0, RootPath.lastIndexOf("/"));
                    if (type.equals(".shp")) {
                        editor.putString("shp_inputpath", RootPathForTrue);
                        editor.apply();
                    }else if (type.equals(".zip")){
                        editor.putString("inputpath", RootPathForTrue);
                        editor.apply();
                    }else if (type.equals(".tif")){
                        editor.putString("tif_inputpath", RootPathForTrue);
                        editor.apply();
                    }
                    Activity_FileManage.this.finish();
                }
            }
        });
        recyclerView.setAdapter(adapter);
    }
}
