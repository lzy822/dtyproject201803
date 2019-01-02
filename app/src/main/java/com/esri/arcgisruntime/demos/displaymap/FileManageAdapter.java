package com.esri.arcgisruntime.demos.displaymap;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by 54286 on 2018/3/20.
 */

public class FileManageAdapter extends RecyclerView.Adapter<FileManageAdapter.ViewHolder>{
    private Context mContext;

    private List<FileManage> mFileManageList;

    private FileManageAdapter.OnRecyclerItemLongListener mOnItemLong;

    private FileManageAdapter.OnRecyclerItemClickListener mOnItemClick;

    static class ViewHolder extends RecyclerView.ViewHolder {
        //private OnRecyclerItemLongListener mOnItemLong = null;
        CardView cardView;
        ImageView FileImage;
        TextView FileName;

        public ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;
            FileImage = (ImageView) view.findViewById(R.id.File_image_11);
            FileName = (TextView) view.findViewById(R.id.File_txt_11);



        }
    }
    public FileManageAdapter(List<FileManage> mFileList) {
        this.mFileManageList = mFileList;
    }

    @Override
    public FileManageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if(mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.filemanage_item, parent, false);
        final FileManageAdapter.ViewHolder holder = new FileManageAdapter.ViewHolder(view);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                FileManage fileManage = mFileManageList.get(position);
                mOnItemClick.onItemClick(v, fileManage.getRootPath(), position);
                /*Intent intent = new Intent(mContext, singlepoi.class);
                intent.putExtra("POIC", poi.getM_POIC());
                mContext.startActivity(intent);*/
            }
        });
        /*holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mOnItemLong != null){
                    int position = holder.getAdapterPosition();
                    FileManage fileManage = mFileManageList.get(position);
                    mOnItemLong.onItemLongClick(v, fileManage.getFileSubset());
                    holder.cardView.setCardBackgroundColor(Color.GRAY);
                }
                return true;
            }
        });*/
        return holder;
    }

    @Override
    public void onBindViewHolder(FileManageAdapter.ViewHolder holder, int position) {
        FileManage fileManage = mFileManageList.get(position);
        String data = fileManage.getRootPath();
        data = data.substring(data.lastIndexOf("/") + 1);
        holder.FileName.setText(data);
        if (data.contains(".dt"))
            holder.FileImage.setImageResource(R.drawable.ic_map_black_24dp);
        else if (data.contains(".zip"))
            holder.FileImage.setImageResource(R.drawable.ic_work_black_24dp);
        else if (data.contains(".shp")) {
            holder.FileImage.setImageResource(R.drawable.ic_landscape_blue_24dp);
        }
        else if (data.contains(".tif")) {
            holder.FileImage.setImageResource(R.drawable.ic_landscape_black_24dp);
        }
        else holder.FileImage.setImageResource(R.drawable.ic_folder_black_24dp);
    }


    @Override
    public int getItemCount() {
        return mFileManageList.size();
    }



    public interface OnRecyclerItemLongListener{
        void onItemLongClick(View view, String[] FileSubset);
    }
    public void setOnItemLongClickListener(FileManageAdapter.OnRecyclerItemLongListener listener){
        //Log.w(TAG, "setOnItemLongClickListener: " );
        this.mOnItemLong =  listener;
    }
    public interface OnRecyclerItemClickListener{
            void onItemClick(View view, String RootPath, int position);
    }
    public void setOnItemClickListener(OnRecyclerItemClickListener listener){
        this.mOnItemClick =  listener;
    }
}
