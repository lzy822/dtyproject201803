package com.esri.arcgisruntime.demos.displaymap;

import android.content.Context;
import android.content.SharedPreferences;
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
import static android.content.Context.MODE_PRIVATE;


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
        TextView kvnum;
        TextView kvzb;

        public ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;
            kvname = (TextView) view.findViewById(R.id.kv_name);
            kvvalue = (TextView) view.findViewById(R.id.kv_value);
            kvnum = (TextView) view.findViewById(R.id.kv_num);
            kvzb = (TextView) view.findViewById(R.id.kv_zb);



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
                if (mOnItemClick != null) {
                    int position = holder.getAdapterPosition();
                    KeyAndValue keyAndValue = keyAndValues.get(position);
                    holder.cardView.setCardBackgroundColor(Color.RED);
                    mOnItemClick.onItemClick(v, keyAndValue.getName(), position);
                }
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
        SharedPreferences prf1 = mContext.getSharedPreferences("queryType", MODE_PRIVATE);
        String string = prf1.getString("tdghdl", "");
        KeyAndValue keyAndValue = keyAndValues.get(position);
        holder.kvnum.setText(Integer.toString(position + 1));
        holder.kvname.setText(keyAndValue.getName());
        if (!string.isEmpty() && keyAndValue.getName().equals(string)) holder.cardView.setCardBackgroundColor(Color.RED);
        else holder.cardView.setCardBackgroundColor(Color.WHITE);
        DecimalFormat decimalFormat = new DecimalFormat("0.0");
        DecimalFormat decimalFormat1 = new DecimalFormat("0.00");
        holder.kvvalue.setText(decimalFormat.format(Double.valueOf(keyAndValue.getValue())) + "亩");
        holder.kvzb.setText(decimalFormat1.format(Double.valueOf(keyAndValue.getValue()) / Double.valueOf(keyAndValue.getNickname()) * 100) + "%");
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
        void onItemClick(View view, String name, int position);
    }
    public void setOnItemClickListener(OnRecyclerItemClickListener listener){
        this.mOnItemClick =  listener;
    }
}
