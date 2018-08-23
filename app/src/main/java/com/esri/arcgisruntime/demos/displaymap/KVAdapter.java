package com.esri.arcgisruntime.demos.displaymap;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import static android.content.ContentValues.TAG;


/**
 * Created by 54286 on 2018/3/6.
 */

public class KVAdapter extends RecyclerView.Adapter<KVAdapter.ViewHolder> {
    private Context mContext;

    private List<KeyAndValue> keyAndValues;

    private OnRecyclerItemLongListener mOnItemLong;

    private OnRecyclerItemClickListener mOnItemClick;

    static class ViewHolder extends RecyclerView.ViewHolder {
        //private OnRecyclerItemLongListener mOnItemLong = null;
        CardView cardView;
        TextView kvname;
        TextView kvvalue;

        public ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;
            kvname = (TextView) view.findViewById(R.id.kv_name);
            kvvalue = (TextView) view.findViewById(R.id.kv_value);



        }
    }
    public KVAdapter(List<KeyAndValue> keyAndValues) {
        this.keyAndValues = keyAndValues;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if(mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.kv_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                KeyAndValue keyAndValue = keyAndValues.get(position);
                mOnItemClick.onItemClick(v, keyAndValue.getValue(), position);
                /*
                Intent intent = new Intent(mContext, MainInterface.class);
                intent.putExtra("num", keyAndValue.getM_num());
                mContext.startActivity(intent);*/
            }
        });
        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mOnItemLong != null){
                    int position = holder.getAdapterPosition();
                    KeyAndValue keyAndValue = keyAndValues.get(position);
                    mOnItemLong.onItemLongClick(v, keyAndValue.getValue(), position);
                    holder.cardView.setCardBackgroundColor(Color.GRAY);
               }
                return true;
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position, List<Object> payloads) {
        //notifyItemChanged(position);
        if (payloads.isEmpty()){
            onBindViewHolder(holder, position);
        }else {
            notifyItemChanged(position);
            //Toast.makeText(mContext, Integer.toString(position), Toast.LENGTH_SHORT).show();
            Log.w(TAG, "find here" + payloads.toString() );
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        KeyAndValue keyAndValue = keyAndValues.get(position);
        holder.kvname.setText(keyAndValue.getName());
        DecimalFormat decimalFormat = new DecimalFormat("0.0");
        holder.kvvalue.setText(decimalFormat.format(Double.valueOf(keyAndValue.getValue())) + "亩");
    }

    @Override
    public int getItemCount() {
        return keyAndValues.size();
    }



    public interface OnRecyclerItemLongListener{
        void onItemLongClick(View view, String value, int position);
    }
    public void setOnItemLongClickListener(OnRecyclerItemLongListener listener){
        this.mOnItemLong =  listener;
    }

    public interface OnRecyclerItemClickListener{
        void onItemClick(View view, String value, int position);
    }
    public void setOnItemClickListener(OnRecyclerItemClickListener listener){
        this.mOnItemClick =  listener;
    }
}
