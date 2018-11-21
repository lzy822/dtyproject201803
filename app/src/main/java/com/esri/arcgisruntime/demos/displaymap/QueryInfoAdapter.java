package com.esri.arcgisruntime.demos.displaymap;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class QueryInfoAdapter extends RecyclerView.Adapter<com.esri.arcgisruntime.demos.displaymap.QueryInfoAdapter.ViewHolder>{
        private static final String TAG = "QueryInfoAdapter";
        private Context mContext;

        private List<QueryInfo> queryInfoList;

        private com.esri.arcgisruntime.demos.displaymap.QueryInfoAdapter.OnRecyclerItemLongListener mOnItemLong;

        private com.esri.arcgisruntime.demos.displaymap.QueryInfoAdapter.OnRecyclerItemClickListener mOnItemClick;

        private com.esri.arcgisruntime.demos.displaymap.QueryInfoAdapter.OnRecyclerItemCheckListener mOnItemCheck;

        static class ViewHolder extends RecyclerView.ViewHolder {
            //private OnRecyclerItemLongListener mOnItemLong = null;
            CardView cardView;
            TextView textView;

            public ViewHolder(View view) {
                super(view);
                cardView = (CardView) view;
                textView = (TextView) view.findViewById(R.id.query_name);


            }
        }
        public QueryInfoAdapter(List<QueryInfo> queryInfoList) {
            this.queryInfoList = queryInfoList;
        }

        @Override
        public com.esri.arcgisruntime.demos.displaymap.QueryInfoAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            if(mContext == null) {
                mContext = parent.getContext();
            }
            View view = LayoutInflater.from(mContext).inflate(R.layout.queryinfo_item, parent, false);
            final com.esri.arcgisruntime.demos.displaymap.QueryInfoAdapter.ViewHolder holder = new com.esri.arcgisruntime.demos.displaymap.QueryInfoAdapter.ViewHolder(view);
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getAdapterPosition();
                    QueryInfo queryInfo = queryInfoList.get(position);
                    try {
                        mOnItemClick.onItemClick(v, queryInfo.getName(), position);
                    }catch (NullPointerException e){

                    }
                }
            });
            holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (mOnItemLong != null){
                        int position = holder.getAdapterPosition();
                        QueryInfo queryInfo = queryInfoList.get(position);
                        mOnItemLong.onItemLongClick(v, queryInfo.getName());
                        holder.cardView.setCardBackgroundColor(Color.GRAY);
                    }
                    return true;
                }
            });
            return holder;
        }

        @Override
        public void onBindViewHolder(final com.esri.arcgisruntime.demos.displaymap.QueryInfoAdapter.ViewHolder holder, int position) {
            QueryInfo queryInfo = queryInfoList.get(position);
            holder.textView.setText(queryInfo.getName());
        }


        @Override
        public int getItemCount() {
            return queryInfoList.size();
        }



        public interface OnRecyclerItemLongListener{
            void onItemLongClick(View view, String path);
        }
        public void setOnItemLongClickListener(com.esri.arcgisruntime.demos.displaymap.QueryInfoAdapter.OnRecyclerItemLongListener listener){
            //Log.w(TAG, "setOnItemLongClickListener: " );
            this.mOnItemLong =  listener;
        }
        public interface OnRecyclerItemCheckListener{
            void onItemCheckClick(com.esri.arcgisruntime.demos.displaymap.QueryInfoAdapter.ViewHolder holder, String name, int position);
        }
        public void setOnItemCheckListener(com.esri.arcgisruntime.demos.displaymap.QueryInfoAdapter.OnRecyclerItemCheckListener listener){
            //Log.w(TAG, "setOnItemLongClickListener: " );
            this.mOnItemCheck =  listener;
        }
        public interface OnRecyclerItemClickListener{
            void onItemClick(View view, String path, int position);
        }
        public void setOnItemClickListener(com.esri.arcgisruntime.demos.displaymap.QueryInfoAdapter.OnRecyclerItemClickListener listener) {
            this.mOnItemClick = listener;
        }
}
