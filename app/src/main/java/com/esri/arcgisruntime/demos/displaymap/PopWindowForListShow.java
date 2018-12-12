package com.esri.arcgisruntime.demos.displaymap;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.bin.david.form.core.SmartTable;
import com.bin.david.form.core.TableConfig;
import com.bin.david.form.data.CellInfo;
import com.bin.david.form.data.column.Column;
import com.bin.david.form.data.format.bg.BaseCellBackgroundFormat;
import com.bin.david.form.data.format.draw.ImageResDrawFormat;
import com.bin.david.form.data.style.FontStyle;
import com.bin.david.form.data.table.TableData;
import com.bin.david.form.listener.OnColumnItemClickListener;
import com.bin.david.form.utils.DensityUtils;

import java.util.ArrayList;
import java.util.List;

public class PopWindowForListShow extends AppCompatActivity {
    private static final String TAG = "PopWindowForListShow";
    private SmartTable<User> table;
    Column<String> name;
    Column<Integer> age;
    Column<Boolean> operation;
    List<String> name_selected = new ArrayList<String>();

    /**
     * 收集所有被勾选的姓名记录到 name_selected 集合中，并实时更新
     * @param position  被选择记录的行数，根据行数用来找到其他列中该行记录对应的信息
     * @param selectedState   当前的操作状态：选中 || 取消选中
     */
    public void showName(int position, boolean selectedState){
        List<String> rotorIdList = name.getDatas();
        if(position >-1){
            String rotorTemp = rotorIdList.get(position);
            if(selectedState && !name_selected.contains(rotorTemp)){            //当前操作是选中，并且“所有选中的姓名的集合”中没有该记录，则添加进去
                name_selected.add(rotorTemp);
            }else if(!selectedState && name_selected.contains(rotorTemp)){     // 当前操作是取消选中，并且“所有选中姓名的集合”总含有该记录，则删除该记录
                name_selected.remove(rotorTemp);
            }
        }
        for(String s:name_selected){
            System.out.print(s + " -- ");
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_window_for_list_show);
        String data = getIntent().getStringExtra("data");
        String[] str1 = data.split(";");
        List<KeyAndValue> keyAndValueList = new ArrayList<>();
        List<KeyAndValue> keyAndValueList1 = new ArrayList<>();
        List<KeyAndValue> keyAndValueList2 = new ArrayList<>();
        List<KeyAndValue> keyAndValueList3 = new ArrayList<>();
        String[] keyAndValue = str1[0].split(",");
        for (int j = 0; j < keyAndValue.length; ++j){
            String[] keyAndValue1 = keyAndValue[j].split(":");
            keyAndValueList.add(new KeyAndValue(keyAndValue1[0], keyAndValue1[1]));
        }
        keyAndValue = str1[1].split(",");
        for (int j = 0; j < keyAndValue.length; ++j){
            String[] keyAndValue1 = keyAndValue[j].split(":");
            keyAndValueList1.add(new KeyAndValue(keyAndValue1[0], keyAndValue1[1]));
        }
        keyAndValue = str1[2].split(",");
        for (int j = 0; j < keyAndValue.length; ++j){
            String[] keyAndValue1 = keyAndValue[j].split(":");
            keyAndValueList2.add(new KeyAndValue(keyAndValue1[0], keyAndValue1[1]));
        }
        keyAndValue = str1[3].split(",");
        for (int j = 0; j < keyAndValue.length; ++j){
            String[] keyAndValue1 = keyAndValue[j].split(":");
            keyAndValueList3.add(new KeyAndValue(keyAndValue1[0], keyAndValue1[1]));
        }
        Log.w(TAG, "onCreate: " + data);
        table = (SmartTable<User>)findViewById(R.id.table);
        List<User> codeList = new ArrayList<User>();
        codeList.add(new User("user_01",20,false));
        codeList.add(new User("user_02",21,false));
        codeList.add(new User("user_03",22,false));
        codeList.add(new User("user_04",22,false));
        codeList.add(new User("user_05",21,false));
        codeList.add(new User("user_06",21,false));
        codeList.add(new User("user_07",23,false));
        codeList.add(new User("user_08",20,false));

        name = new Column<>("姓名", "name");

        age = new Column<>("年龄", "age");

        int size = DensityUtils.dp2px(this,30);

        operation = new Column<>("勾选", "operation", new ImageResDrawFormat<Boolean>(size,size) {    //设置"操作"这一列以图标显示 true、false 的状态
            @Override
            protected Context getContext() {
                return PopWindowForListShow.this;
            }
            @Override
            protected int getResourceID(Boolean isCheck, String value, int position) {
                if(isCheck){
                    return R.mipmap.ic_launcher;      //将图标提前放入 app/res/mipmap 目录下
                }
                return R.mipmap.lincanglogo;
            }
        });
        operation.setComputeWidth(40);

        final TableData<User> tableData = new TableData<>("测试标题",codeList, operation, name, age);
        table.getConfig().setShowTableTitle(false);

        table.setTableData(tableData);
//        table.getConfig().setContentStyle(new FontStyle(50, Color.BLUE));
        table.getConfig().setMinTableWidth(1024);       //设置表格最小宽度
        FontStyle style = new FontStyle();
        style.setTextSize(30);
        table.getConfig().setContentStyle(style);       //设置表格主题字体样式
        table.getConfig().setColumnTitleStyle(style);   //设置表格标题字体样式
        table.getConfig().setContentCellBackgroundFormat(new BaseCellBackgroundFormat<CellInfo>() {     //设置隔行变色
            @Override
            public int getBackGroundColor(CellInfo cellInfo) {
                if(cellInfo.row%2 ==1) {
                    return ContextCompat.getColor(PopWindowForListShow.this, R.color.cardview_dark_background);      //需要在 app/res/values 中添加 <color name="tableBackground">#d4d4d4</color>
                }else{
                    return TableConfig.INVALID_COLOR;
                }
            }
        });
        table.getConfig().setMinTableWidth(1024);   //设置最小宽度
    }
}
