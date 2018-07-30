package com.example.davinci.Adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.davinci.R;
import com.example.davinci.Util.ImageLoader;

import java.util.List;

/**
 * recyclerView适配器
 * Created By Mr.Bean
 */
public class PictureListAdapter extends RecyclerView.Adapter<PictureListAdapter.ViewHolder>{

    private List<String> mImgList;
    private String mDirPath;

    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.picture);
        }
    }

    public PictureListAdapter(List<String> imgList, String dirPath){
        mImgList = imgList;
        mDirPath = dirPath;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview, null, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(PictureListAdapter.ViewHolder holder, int position) {
        String path = mImgList.get(position);
        holder.imageView.setImageResource(R.drawable.black_background);
        ImageLoader.getInstance(ImageLoader.Type.LIFO).loadImage(mDirPath + "/" + path, holder.imageView);
    }

    @Override
    public int getItemCount() {
        return mImgList.size();
    }
}
