package com.esri.arcgisruntime.demos.displaymap;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.esri.arcgisruntime.mapping.ArcGISMap;

import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by 54286 on 2018/3/20.
 */

public class layerAdapter extends RecyclerView.Adapter<layerAdapter.ViewHolder>{
    private static final String TAG = "layerAdapter";
    private Context mContext;

    public void setLayerList(List<layer> layerList) {
        this.layerList = layerList;
    }

    private List<layer> layerList;

    private ArcGISMap map;//用于判断某个图层当前的可视状态

    private layerAdapter.OnRecyclerItemLongListener mOnItemLong;

    private layerAdapter.OnRecyclerItemClickListener mOnItemClick;

    private layerAdapter.OnRecyclerItemCheckListener mOnItemCheck;

    static class ViewHolder extends RecyclerView.ViewHolder {
        //private OnRecyclerItemLongListener mOnItemLong = null;
        CardView cardView;
        CheckBox checkBox;

        public ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;
            checkBox = (CheckBox) view.findViewById(R.id.checkbox);



        }
    }
    public layerAdapter(List<layer> layerList, ArcGISMap map) {
        this.layerList = layerList;
        this.map = map;
    }

    @Override
    public layerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if(mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.layer_item, parent, false);
        final layerAdapter.ViewHolder holder = new layerAdapter.ViewHolder(view);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                layer layer = layerList.get(position);
                try {
                    mOnItemClick.onItemClick(v, layer.getName(), position);
                }catch (NullPointerException e){

                }
            }
        });
        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mOnItemLong != null){
                    int position = holder.getAdapterPosition();
                    layer layer = layerList.get(position);
                    mOnItemLong.onItemLongClick(holder, layer.getName(), layer.getPath());
                }
                return true;
            }
        });
        holder.checkBox.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mOnItemLong != null){
                    int position = holder.getAdapterPosition();
                    layer layer = layerList.get(position);
                    mOnItemLong.onItemLongClick(holder, layer.getName(), layer.getPath());
                }
                return true;
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(layerAdapter.ViewHolder holder, int position, List<Object> payloads) {
        //notifyItemChanged(position);
        Log.w(TAG, "find here" + payloads.toString() );
        if (payloads.isEmpty()){
            onBindViewHolder(holder, position);
        }else {
            notifyItemChanged(position);
            //Toast.makeText(mContext, Integer.toString(position), Toast.LENGTH_SHORT).show();
            Log.w(TAG, "find here" + payloads.toString() );
        }
    }

    @Override
    public void onBindViewHolder(final layerAdapter.ViewHolder holder, int position) {
        Log.w(TAG, "正在重载左侧滑块 " + position);
        layer mlayer = layerList.get(position);
        if (mlayer.isStatus())
            holder.checkBox.setChecked(true);
        else
            holder.checkBox.setChecked(false);

        /*for (int i = 0; i < layerList.size(); i++) {
            Log.w(TAG, "onBindViewHolder: 200902" + map.getOperationalLayers().get(layerList.get(i).getNum()).getName() + ",visible: " + map.getOperationalLayers().get(mlayer.getNum()).isVisible());
        }
        if (map.getOperationalLayers().get(mlayer.getNum()).isVisible())
            holder.checkBox.setChecked(true);
        else
            holder.checkBox.setChecked(false);*/
        holder.checkBox.setText(getAliasName(mlayer.getName()));
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int position = holder.getAdapterPosition();
                    if (layerList.size() != 0) {
                        layer layer = layerList.get(position);
                        mOnItemCheck.onItemCheckClick(holder, layer.getName(), layer.getPath(), position);
                    }
                    //holder.cardView.setCardBackgroundColor(Color.GRAY);

            }
        });
    }


    @Override
    public int getItemCount() {
        return layerList.size();
    }

    public static String getAliasName(String name) {
        switch (name){
            case "DGX":
                name = "等高线";
                break;
            case "STBHHX":
                name = "生态保护红线";
                break;
            case "YJJBNT":
                name = "永久基本农田";
                break;
            case "XZQ":
                name = "乡镇级行政区";
                break;
            case "PDT":
                name = "坡度图";
                break;
            case "DLTB_3diao":
                name = "三调地类图斑";
                break;
            case "DLTB_2diao":
                name = "二调地类图斑";
                break;
            case "DK":
                name = "农村土地承包经营权";
                break;
            case "CJDCQ":
                name = "村级调查区";
                break;
        }
        return name;
    }



    public interface OnRecyclerItemLongListener{
        void onItemLongClick(layerAdapter.ViewHolder holder, String name, String path);
    }
    public void setOnItemLongClickListener(layerAdapter.OnRecyclerItemLongListener listener){
        //Log.w(TAG, "setOnItemLongClickListener: " );
        this.mOnItemLong =  listener;
    }
    public interface OnRecyclerItemCheckListener{
        void onItemCheckClick(layerAdapter.ViewHolder holder, String name, String path,  int position);
    }
    public void setOnItemCheckListener(layerAdapter.OnRecyclerItemCheckListener listener){
        //Log.w(TAG, "setOnItemLongClickListener: " );
        this.mOnItemCheck =  listener;
    }
    public interface OnRecyclerItemClickListener{
        void onItemClick(View view, String path, int position);
    }
    public void setOnItemClickListener(OnRecyclerItemClickListener listener){
        this.mOnItemClick =  listener;
    }
}
