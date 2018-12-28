package com.esri.arcgisruntime.demos.displaymap;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bin.david.form.core.SmartTable;
import com.bin.david.form.core.TableConfig;
import com.bin.david.form.data.CellInfo;
import com.bin.david.form.data.column.Column;
import com.bin.david.form.data.format.bg.BaseBackgroundFormat;
import com.bin.david.form.data.format.bg.BaseCellBackgroundFormat;
import com.bin.david.form.data.format.bg.IBackgroundFormat;
import com.bin.david.form.data.format.draw.ImageResDrawFormat;
import com.bin.david.form.data.style.FontStyle;
import com.bin.david.form.data.table.TableData;
import com.bin.david.form.listener.OnColumnItemClickListener;
import com.bin.david.form.utils.DensityUtils;
import com.github.clans.fab.FloatingActionButton;

import org.litepal.LitePal;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PopWindowForListShow extends AppCompatActivity {
    private static final String TAG = "PopWindowForListShow";
    private SmartTable<PopWindowList> table;
    List<PopWindowList> codeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_window_for_list_show);

        resumeTable();
        FloatingActionButton outputButton = (FloatingActionButton) findViewById(R.id.outputData);
        outputButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickFile();
                //outputData();
            }
        });

        FloatingActionButton saveButton = (FloatingActionButton) findViewById(R.id.saveData);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO 存储图斑信息
                saveTbData();
            }
        });
    }

    private void saveTbData(){
        String name = "图斑" + LitePal.findAll(my_tb.class).size();
        my_tb my_tb = new my_tb();
        my_tb.setPointCollection(getIntent().getStringExtra("pointCollection"));
        my_tb.setMdata(getIntent().getStringExtra("data"));
        my_tb.setName(name);
        my_tb.save();
        Toast.makeText(PopWindowForListShow.this, name + "已经保存", Toast.LENGTH_SHORT).show();
    }

    private boolean hasRepeatedTuban(){

        return false;
    }

    private void resumeTable(){
        String data = getIntent().getStringExtra("data");
        String[] str1 = data.split(";");
        Log.w(TAG, "onCreate: " + data);
        table = (SmartTable<PopWindowList>)findViewById(R.id.table);
        codeList = new ArrayList<PopWindowList>();
        String jbntStr = "";
        int mnum = 0;
        for (int i = 0; i < str1.length; ++i){
            if (str1[i].contains("基本农田保护区")){
                String[] mstr = str1[i].split(",");
                jbntStr = mstr[1];
                mnum = i;
                break;
            }
        }
        for (int i = 0; i < str1.length; ++i){
            if (i != mnum)
                codeList.add(new PopWindowList(str1[i] + "," + jbntStr));
        }

        Column<String> name = new Column<>("图层名", "name");
        Column<Double> jbnt = new Column<>("基本农田（亩）", "jbnt");
        Column<Double> hd = new Column<>("旱地（亩）", "hd");
        Column<Double> zld = new Column<>("总林地（亩）", "ld");
        Column<Double> yld = new Column<>("有林地（亩）", "yld");
        Column<Double> qtld = new Column<>("其他林地（亩）", "qtld");
        Column<Double> gmld = new Column<>("灌木林地（亩）", "gmld");
        Column<Double> ncjmdyd = new Column<>("农村居民点用地（亩）", "ncjmdyd");
        Column<Double> zrbld = new Column<>("自然保留地（亩）", "zrbld");
        Column<Double> yd = new Column<>("园地（亩）", "yd");
        Column<Double> st = new Column<>("水田（亩）", "st");
        Column<Double> ktsm = new Column<>("坑塘水面（亩）", "ktsm");
        Column<Double> tt = new Column<>("滩涂（亩）", "tt");
        Column<Double> ssnyd = new Column<>("设施农用地（亩）", "ssnyd");
        Column<Double> ckyd = new Column<>("采矿用地（亩）", "ckyd");
        Column<Double> tsyd = new Column<>("特殊用地（亩）", "tsyd");
        Column<Double> czyd = new Column<>("城镇用地（亩）", "czyd");
        Column<Double> sgjzyd = new Column<>("水工建筑用地（亩）", "sgjzyd");
        Column<Double> glyd = new Column<>("公路用地（亩）", "glyd");
        Column<Double> sjd = new Column<>("水浇地（亩）", "sjd");
        Column<Double> hlsm = new Column<>("河流水面（亩）", "hlsm");
        Column<Double> qtdljsyd = new Column<>("其他独立建设用地（亩）", "qtdljsyd");
        Column<Double> fjmsssyd = new Column<>("风景名胜设施用地（亩）", "fjmsssyd");
        Column<Double> sksm = new Column<>("水库水面（亩）", "sksm");
        Column<Double> ncdl = new Column<>("农村道路（亩）", "ncdl");
        Column<Double> mcd = new Column<>("牧草地（亩）", "mcd");
        Column<Double> myjcyd = new Column<>("民用机场用地（亩）", "myjcyd");
        Column<Double> gkmtyd = new Column<>("港口码头用地（亩）", "gkmtyd");
        Column<Double> hpsm = new Column<>("湖泊水面（亩）", "hpsm");

        //组合列
        Column ld = new Column("林地",zld,yld,qtld,gmld);

        final TableData<PopWindowList> tableData = new TableData<>("测试标题",codeList, name, jbnt, hd, ld, ncjmdyd, zrbld, yd, st
                , ktsm, tt, ssnyd, ckyd, tsyd, czyd, sgjzyd, glyd, sjd, hlsm, qtdljsyd, fjmsssyd, sksm, ncdl, mcd, myjcyd, gkmtyd, hpsm);
        table.getConfig().setShowTableTitle(false);
        table.getConfig().setColumnTitleBackground(new BaseBackgroundFormat(getResources().getColor(R.color.colorAccent)));
        table.setTableData(tableData);
        table.setZoom(true);
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
        }else {
            outputData();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case StaticVariableEnum.PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    outputData();
                }else {
                    Toast.makeText(PopWindowForListShow.this, "请通过所有申请的权限", Toast.LENGTH_LONG).show();
                }
                break;
            default:
        }
    }

    public void outputData(){
        StringBuffer sb = new StringBuffer();
        sb.append(PopWindowList.titleToString()).append("\n");
        for (int i = 0; i < codeList.size(); ++i){
            sb.append(codeList.get(i).toString()).append("\n");
        }
        String outputPath = Long.toString(System.currentTimeMillis());

        try {
            File file0 = new File(Environment.getExternalStorageDirectory() + "/ZRZY");
            if (!file0.exists() && !file0.isDirectory()){
                file0.mkdirs();
            }
            File file = new File(Environment.getExternalStorageDirectory() + "/ZRZY/" + "/Output");
            if (!file.exists() && !file.isDirectory()){
                file.mkdirs();
            }
            File file1 = new File(Environment.getExternalStorageDirectory() + "/ZRZY/" + "/Output",  "图表数据" + outputPath + ".txt");
            Log.w(TAG, "exception: " + file1);
            if (!file1.exists()) {
                file1.getParentFile().mkdirs();
                file1.createNewFile();
            }
            FileOutputStream of = new FileOutputStream(file1);
            of.write(sb.toString().getBytes());
            of.close();
            Toast.makeText(PopWindowForListShow.this, "导出成功，文件位于" + "\n" + Environment.getExternalStorageDirectory() + "/ZRZY/" + "/Output/" + "图表数据" + outputPath + ".txt" + "\n" + "请返回上一界面，自行进行图斑截图", Toast.LENGTH_LONG).show();
        }catch (IOException e){
            Log.w(TAG, e.toString());
        }
    }
}
