package com.esri.arcgisruntime.demos.displaymap;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import java.util.List;

import static android.content.ContentValues.TAG;

public class DynamicChooseFrameAdapter extends RecyclerView.Adapter<DynamicChooseFrameAdapter.ViewHolder> {
    private static final String TAG = "DynamicChooseFrameAdapter";
    private Context mContext;

    //private List<layer> layerList;

    private List<LayerFieldsSheet> list;

    public int CheckedIndex = -1;

    private DynamicChooseFrameAdapter.OnRecyclerItemLongListener mOnItemLong;

    private DynamicChooseFrameAdapter.OnRecyclerItemClickListener mOnItemClick;

    private DynamicChooseFrameAdapter.OnRecyclerItemCheckListener mOnItemCheck;

    static class ViewHolder extends RecyclerView.ViewHolder {
        //private OnRecyclerItemLongListener mOnItemLong = null;
        CardView cardView;
        CheckBox checkBox;

        public ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;
            checkBox = (CheckBox) view.findViewById(R.id.dcfcheckbox);



        }
    }
    public DynamicChooseFrameAdapter(List<LayerFieldsSheet> list) {
        this.list = list;
    }
    public DynamicChooseFrameAdapter(List<LayerFieldsSheet> list, int index) {
        this.list = list;
        CheckedIndex = index;
    }

    @Override
    public DynamicChooseFrameAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if(mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.dynamic_item, parent, false);
        final DynamicChooseFrameAdapter.ViewHolder holder = new DynamicChooseFrameAdapter.ViewHolder(view);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                LayerFieldsSheet lfs = list.get(position);
                try {
                    mOnItemClick.onItemClick(v, lfs.getLayerShowName(), position);
                }catch (NullPointerException e){

                }
            }
        });
        holder.checkBox.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mOnItemLong != null){
                    int position = holder.getAdapterPosition();
                    LayerFieldsSheet lfs = list.get(position);
                    mOnItemLong.onItemLongClick(holder, lfs.getLayerShowName(), lfs.getLayerPath());
                    holder.cardView.setCardBackgroundColor(Color.RED);
                }
                return true;
            }
        });
        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mOnItemLong != null){
                    int position = holder.getAdapterPosition();
                    LayerFieldsSheet lfs = list.get(position);
                    mOnItemLong.onItemLongClick(holder, lfs.getLayerShowName(), lfs.getLayerPath());
                    holder.cardView.setCardBackgroundColor(Color.RED);
                }
                return true;
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(DynamicChooseFrameAdapter.ViewHolder holder, int position, List<Object> payloads) {
        //notifyItemChanged(position);
        if (payloads.isEmpty()){
            onBindViewHolder(holder, position);
        }else {
            notifyItemChanged(position);
            //Toast.makeText(mContext, Integer.toString(position), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBindViewHolder(final DynamicChooseFrameAdapter.ViewHolder holder, int position) {
        LayerFieldsSheet lfs = list.get(position);
        if (position == CheckedIndex)
            holder.checkBox.setChecked(true);
        else
            holder.checkBox.setChecked(false);
        holder.checkBox.setText(lfs.getLayerShowName());
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int position = holder.getAdapterPosition();
                if (list.size() != 0) {
                    LayerFieldsSheet dd = list.get(position);
                    mOnItemCheck.onItemCheckClick(holder, dd.getLayerShowName(), position);
                    CheckedIndex = position;
                    //notifyDataSetChanged();
                    //mOnItemCheck.onItemCheckClick(holder, "", position);
                }
                //holder.cardView.setCardBackgroundColor(Color.GRAY);

            }
        });
        //holder.checkBox.setText(lfs.getFeatureLayer().getName());
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface OnRecyclerItemLongListener{
        void onItemLongClick(DynamicChooseFrameAdapter.ViewHolder holder, String name, String path);
    }
    public void setOnItemLongClickListener(DynamicChooseFrameAdapter.OnRecyclerItemLongListener listener){
        //Log.w(TAG, "setOnItemLongClickListener: " );
        this.mOnItemLong =  listener;
    }
    public interface OnRecyclerItemCheckListener{
        void onItemCheckClick(DynamicChooseFrameAdapter.ViewHolder holder, String name, int position);
    }
    public void setOnItemCheckListener(DynamicChooseFrameAdapter.OnRecyclerItemCheckListener listener){
        //Log.w(TAG, "setOnItemLongClickListener: " );
        this.mOnItemCheck =  listener;
    }
    public interface OnRecyclerItemClickListener{
        void onItemClick(View view, String path, int position);
    }
    public void setOnItemClickListener(DynamicChooseFrameAdapter.OnRecyclerItemClickListener listener){
        this.mOnItemClick =  listener;
    }
}
