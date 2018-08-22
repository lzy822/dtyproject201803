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

    private OnRecyclerItemLongListener mOnItemLong;

    private OnRecyclerItemClickListener mOnItemClick;

    static class ViewHolder extends RecyclerView.ViewHolder {
        //private OnRecyclerItemLongListener mOnItemLong = null;
        CardView cardView;
        TextView MapName;

        public ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;
            MapName = (TextView) view.findViewById(R.id.xzq_name);



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
                xzq map = xzqs.get(position);
                mOnItemClick.onItemClick(v, map.getXzqdm(), position);
                /*
                Intent intent = new Intent(mContext, MainInterface.class);
                intent.putExtra("num", map.getM_num());
                mContext.startActivity(intent);*/
            }
        });
        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mOnItemLong != null){
                    int position = holder.getAdapterPosition();
                    xzq map = xzqs.get(position);
                    mOnItemLong.onItemLongClick(v, map.getXzqdm(), position);
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
        xzq xzq = xzqs.get(position);
        if (xzq.getGrade() == 1){
            holder.MapName.setTextColor(Color.RED);
            holder.MapName.setText(xzq.getXzqmc());
        }
        else if (xzq.getGrade() == 2) {
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
    public void setOnItemLongClickListener(OnRecyclerItemLongListener listener){
        this.mOnItemLong =  listener;
    }

    public interface OnRecyclerItemClickListener{
        void onItemClick(View view, String xzqdm, int position);
    }
    public void setOnItemClickListener(OnRecyclerItemClickListener listener){
        this.mOnItemClick =  listener;
    }
}
