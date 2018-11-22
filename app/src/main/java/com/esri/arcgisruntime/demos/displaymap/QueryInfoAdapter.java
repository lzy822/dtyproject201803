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

import com.esri.arcgisruntime.geometry.Envelope;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class QueryInfoAdapter extends RecyclerView.Adapter<com.esri.arcgisruntime.demos.displaymap.QueryInfoAdapter.ViewHolder>{
        private static final String TAG = "QueryInfoAdapter";
        private Context mContext;

        private List<QueryInfo> queryInfoList;

    private List<QueryInfo> queryInfoList1;

    private List<QueryInfo> queryInfoList2;

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
        public QueryInfoAdapter(List<QueryInfo> queryInfoList, List<QueryInfo> queryInfoList1) {
            this.queryInfoList = queryInfoList;
            this.queryInfoList1 = queryInfoList1;
            queryInfoList2 = queryInfoList;
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
                        if (!queryInfo.getName().contains("[")) {
                            boolean isOK = false;
                            for (int j = 0; j < queryInfoList.size(); ++j){
                                if (queryInfoList.get(j).getStatus() == 2){
                                    if (!Integer.toString(queryInfoList.get(j).getFnode()).contains(queryInfo.getName())){
                                        isOK = true;
                                        break;
                                    }
                                }
                            }
                            if (queryInfoList.size() == queryInfoList2.size() || isOK) {
                                List<QueryInfo> queryInfos1 = new ArrayList<>();
                                for (int i = 0; i < queryInfoList1.size(); ++i) {
                                    if (queryInfoList1.get(i).getFnode() == Integer.valueOf(queryInfo.getName()))
                                        queryInfos1.add(queryInfoList1.get(i));
                                }
                                queryInfos1.addAll(queryInfoList2);
                                QueryInfo.sort1(queryInfos1);
                                queryInfoList = queryInfos1;
                            }else {
                                List<QueryInfo> queryInfos1 = new ArrayList<>();
                                queryInfos1.addAll(queryInfoList2);
                                queryInfoList = queryInfos1;
                            }
                            notifyDataSetChanged();
                        }
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
            /*
            if (name.substring(name.indexOf("["), name.indexOf("]")) == "2017")*/
            //String name = queryInfo.getName();

            /*SharedPreferences pref1 = mContext.getSharedPreferences("ptb", MODE_PRIVATE);
            String year = (String) pref1.getString("year", "");*/
            if (queryInfo.getStatus() == 1)
            {
                holder.textView.setTextColor(Color.RED);
                holder.textView.setText(queryInfo.getName() + "年");
            }
            else
            {
                holder.textView.setTextColor(Color.BLACK);
                holder.textView.setText(queryInfo.getName());
            }
            /*if (queryInfo.getStatus() == 2) {
                holder.textView.setText(queryInfo.getName());
                //holder.textView.setText("onBindViewHolder");
            }else {
                holder.cardView.setVisibility(View.INVISIBLE);
            }*/
            //holder.textView.setText(queryInfo.getName());
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
