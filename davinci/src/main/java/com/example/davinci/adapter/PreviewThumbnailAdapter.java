package com.example.davinci.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.davinci.R;
import com.example.davinci.util.ImageLoader;

import java.util.List;

public class PreviewThumbnailAdapter extends RecyclerView.Adapter<PreviewThumbnailAdapter.ViewHolder> {

    private List<String> mSelectedImg;

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.id_preview_thumbnail);
        }
    }

    public PreviewThumbnailAdapter(List<String> imgList){
        mSelectedImg = imgList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.preview_recyclerview_item, null, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String path = mSelectedImg.get(position);
        ImageLoader.getInstance(ImageLoader.Type.LIFO).loadImage(path, holder.imageView, false, 60);
    }

    @Override
    public int getItemCount() {
        return mSelectedImg.size();
    }
}
