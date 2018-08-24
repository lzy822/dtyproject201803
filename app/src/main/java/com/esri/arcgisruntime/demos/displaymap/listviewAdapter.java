package com.esri.arcgisruntime.demos.displaymap;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import static android.content.ContentValues.TAG;


/**
 * Created by 54286 on 2018/3/6.
 */

public class listviewAdapter extends RecyclerView.Adapter<listviewAdapter.ViewHolder> {
    private Context mContext;

    private List<QueryInfo> queryInfos;

    private OnRecyclerItemClickListener mOnItemClick;

    static class ViewHolder extends RecyclerView.ViewHolder {
        //private OnRecyclerItemLongListener mOnItemLong = null;
        TextView lvName;
        CardView cardView;

        public ViewHolder(View view) {
            super(view);
            lvName = (TextView) view.findViewById(R.id.listview_name);
            cardView = (CardView) view;



        }
    }
    public listviewAdapter(List<QueryInfo> queryInfos) {
        this.queryInfos = queryInfos;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if(mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.listview_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                QueryInfo queryInfo = queryInfos.get(position);
                mOnItemClick.onItemClick(v, queryInfo.getName(), position);
                /*
                Intent intent = new Intent(mContext, MainInterface.class);
                intent.putExtra("num", map.getM_num());
                mContext.startActivity(intent);*/
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
        QueryInfo queryInfo = queryInfos.get(position);
        holder.lvName.setText(queryInfo.getName());
    }

    @Override
    public int getItemCount() {
        return queryInfos.size();
    }





    public interface OnRecyclerItemClickListener{
        void onItemClick(View view, String xzqdm, int position);
    }
    public void setOnItemClickListener(OnRecyclerItemClickListener listener){
        this.mOnItemClick =  listener;
    }
}
