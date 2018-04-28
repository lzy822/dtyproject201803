package com.esri.arcgisruntime.demos.displaymap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.ThumbnailUtils;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by 54286 on 2018/3/20.
 */

public class layerAdapter extends RecyclerView.Adapter<layerAdapter.ViewHolder>{
    private static final String TAG = "layerAdapter";
    private Context mContext;

    private List<layer> layerList;

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
    public layerAdapter(List<layer> layerList) {
        this.layerList = layerList;
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
                mOnItemClick.onItemClick(v, layer.getName(), position);
            }
        });
        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mOnItemLong != null){
                    int position = holder.getAdapterPosition();
                    layer layer = layerList.get(position);
                    mOnItemLong.onItemLongClick(v, layer.getName());
                    holder.cardView.setCardBackgroundColor(Color.GRAY);
                }
                return true;
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(final layerAdapter.ViewHolder holder, int position) {
        layer mlayer = layerList.get(position);
        holder.checkBox.setChecked(true);
        holder.checkBox.setText(mlayer.getName());
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int position = holder.getAdapterPosition();
                    if (layerList.size() != 0) {
                        layer layer = layerList.get(position);
                        mOnItemCheck.onItemCheckClick(holder, layer.getName(), position);
                    }
                    //holder.cardView.setCardBackgroundColor(Color.GRAY);

            }
        });
    }


    @Override
    public int getItemCount() {
        return layerList.size();
    }



    public interface OnRecyclerItemLongListener{
        void onItemLongClick(View view, String path);
    }
    public void setOnItemLongClickListener(layerAdapter.OnRecyclerItemLongListener listener){
        //Log.w(TAG, "setOnItemLongClickListener: " );
        this.mOnItemLong =  listener;
    }
    public interface OnRecyclerItemCheckListener{
        void onItemCheckClick(layerAdapter.ViewHolder holder, String name, int position);
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
