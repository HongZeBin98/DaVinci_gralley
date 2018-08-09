package com.example.davinci.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.davinci.R;
import com.example.davinci.util.ImageLoader;

import java.util.List;
import java.util.Map;

/**
 * 预览界面缩略图的横向RecyclerView适配器
 */
public class PreviewThumbnailAdapter extends RecyclerView.Adapter<PreviewThumbnailAdapter.ViewHolder> {

    private int mNewPosition;
    private int mLastPosition;
    private ImageView mImageView;
    private List<String> mSelectedImg;
    private PreviewThumbnailAdapter.OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener{
        void onClick(int position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        View thumbnailFrame;

        ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.id_preview_thumbnail);
            thumbnailFrame = itemView.findViewById(R.id.id_preview_thumbnail_frame);
        }
    }

    public PreviewThumbnailAdapter(List<String> imgList) {
        mSelectedImg = imgList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        @SuppressLint("InflateParams") View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.preview_recyclerview_item, null, false);
        return new ViewHolder(view);
    }

    @SuppressLint("ResourceType")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        String path = mSelectedImg.get(position);
        ImageView iv = holder.imageView;
        ImageLoader.getInstance(ImageLoader.Type.LIFO).loadImage(path, iv, false, 60);
        if (position == mNewPosition) {
            holder.thumbnailFrame.setVisibility(View.VISIBLE);
            mImageView = holder.imageView;
        } else {
            holder.thumbnailFrame.setVisibility(View.GONE);
        }
        if(mOnItemClickListener != null){
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnItemClickListener.onClick(position );
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mSelectedImg.size();
    }

    /**
     * 传入当前页面的position
     * @param position 当前页的位置
     */
    public void setCurrentPosition(int position) {
        Log.e("position", ""+position );
        mNewPosition = position;
        notifyItemChanged(mNewPosition);
        notifyItemChanged(mLastPosition);
        mLastPosition = mNewPosition;
    }

    /**
     * 返回当前缩略图实例
     * @return  当前缩略图实例
     */
    public ImageView getImageView(){
        return mImageView;
    }

    /**
     * 获取接口的实例化
     * @param onItemClickListener 接口的实例化
     */
    public void setOnItemClickListener(PreviewThumbnailAdapter.OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }
}
