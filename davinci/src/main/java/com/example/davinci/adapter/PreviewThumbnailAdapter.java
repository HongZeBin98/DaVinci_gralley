package com.example.davinci.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.davinci.R;
import com.example.davinci.util.ImageLoader;

import java.util.List;

/**
 * 预览界面缩略图的横向RecyclerView适配器
 */
public class PreviewThumbnailAdapter extends RecyclerView.Adapter<PreviewThumbnailAdapter.ViewHolder> {

    private List<String> mSelectedImg;
    private int mPosition;

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
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String path = mSelectedImg.get(position);
        ImageLoader.getInstance(ImageLoader.Type.LIFO).loadImage(path, holder.imageView, false, 60);
        if (position == mPosition) {
            holder.thumbnailFrame.setVisibility(View.VISIBLE);
        } else {
            holder.thumbnailFrame.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mSelectedImg.size();
    }

    //传入当前页面的position
    public void setCurrentPosition(int position) {
        mPosition = position;
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
