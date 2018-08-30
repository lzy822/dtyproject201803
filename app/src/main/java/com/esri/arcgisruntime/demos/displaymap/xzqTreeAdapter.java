package com.esri.arcgisruntime.demos.displaymap;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.litepal.LitePal;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;


/**
 * Created by 54286 on 2018/3/6.
 */

public class xzqTreeAdapter extends RecyclerView.Adapter<xzqTreeAdapter.ViewHolder> {
    private Context mContext;

    private List<xzq> xzqs;

    private OnRecyclerItemClickListener mOnItemClick;

    static class ViewHolder extends RecyclerView.ViewHolder {
        //private OnRecyclerItemLongListener mOnItemLong = null;
        CardView cardView;
        TextView MapName;
        ImageView xzqIcon;

        public ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;
            MapName = (TextView) view.findViewById(R.id.xzq_name);
            xzqIcon = (ImageView) view.findViewById(R.id.xzq_icon);



        }
    }
    public xzqTreeAdapter(List<xzq> xzqs) {
        this.xzqs = xzqs;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if(mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.xzqtree_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                xzq xzq = xzqs.get(position);
                mOnItemClick.onItemClick(v, xzq.getXzqdm(), position);
                /*
                Intent intent = new Intent(mContext, MainInterface.class);
                intent.putExtra("num", map.getM_num());
                mContext.startActivity(intent);*/
            }
        });
        holder.xzqIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                xzq xzq = xzqs.get(position);
                Log.w(TAG, "onClick: " + xzq.getXzqmc());
                SharedPreferences pref1 = mContext.getSharedPreferences("xzq", MODE_PRIVATE);
                xzqs = DataUtil.bubbleSort(LitePal.where("grade = ?", Integer.toString(1)).find(xzq.class));
                if (pref1.getString("name", "").equals(xzq.getXzqmc())){
                    SharedPreferences.Editor editor = mContext.getSharedPreferences("xzq", MODE_PRIVATE).edit();
                    editor.putString("name", "");
                    editor.apply();
                }else {
                    SharedPreferences.Editor editor = mContext.getSharedPreferences("xzq", MODE_PRIVATE).edit();
                    editor.putString("name", xzq.getXzqmc());
                    editor.apply();
                    xzqs.addAll(LitePal.where("sjxzq = ? and grade = ?", xzq.getXzqmc(), Integer.toString(2)).find(xzq.class));
                    xzqs = DataUtil.bubbleSort(xzqs);
                }
                //notifyItemChanged(position);
                //notifyItemRangeChanged(0, xzqs.size());
                notifyDataSetChanged();
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
        xzq xzq = xzqs.get(position);
        if (xzq.getGrade() == 1){
            SharedPreferences pref1 = mContext.getSharedPreferences("xzq", MODE_PRIVATE);
            if (xzq.getXzqmc().equals(pref1.getString("name", ""))) {
                holder.xzqIcon.setImageResource(R.drawable.ic_remove_circle_outline_black_24dp);
            }
            else
                holder.xzqIcon.setImageResource(R.drawable.ic_add_circle_outline_black_24dp);
            holder.xzqIcon.setVisibility(View.VISIBLE);
            holder.MapName.setTextColor(Color.RED);
            holder.MapName.setText(xzq.getXzqmc());
        }
        else if (xzq.getGrade() == 2) {
            holder.xzqIcon.setVisibility(View.INVISIBLE);
            holder.MapName.setTextColor(Color.BLACK);
            holder.MapName.setText("\t" + "-" + xzq.getXzqmc());
        }
    }

    @Override
    public int getItemCount() {
        return xzqs.size();
    }



    public interface OnRecyclerItemLongListener{
        void onItemLongClick(View view, String xzqdm, int position);
    }

    public interface OnRecyclerItemClickListener{
        void onItemClick(View view, String xzqdm, final int position);
    }
    public void setOnItemClickListener(OnRecyclerItemClickListener listener){
        this.mOnItemClick =  listener;
    }
}
