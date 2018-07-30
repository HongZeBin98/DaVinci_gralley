package com.example.davinci.Adapter;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.davinci.R;
import com.example.davinci.Util.ImageLoader;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * recyclerView适配器
 * Created By Mr.Bean
 */
public class PictureListAdapter extends RecyclerView.Adapter<PictureListAdapter.ViewHolder> {

    private static Set<String> mSelectedImg = new HashSet<String>();
    private List<String> mImgList;
    private String mDirPath;

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ImageButton select;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.id_item_picture);
            select = itemView.findViewById(R.id.id_item_select);
        }
    }

    public PictureListAdapter(List<String> imgList, String dirPath) {
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
    public void onBindViewHolder(final PictureListAdapter.ViewHolder holder, int position) {
        final String path = mImgList.get(position);
        holder.imageView.setImageResource(R.drawable.black_background);
        holder.imageView.setColorFilter(null);
        ImageLoader.getInstance(ImageLoader.Type.LIFO).loadImage(mDirPath + "/" + path, holder.imageView);
        final String filePath = mDirPath + "/" + path;
        if (mSelectedImg.contains(filePath)) {
            selectTrue(holder);
        } else {
            selectFalse(holder);
        }
        holder.select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //已经被选择
                if (mSelectedImg.contains(filePath)) {
                    mSelectedImg.remove(filePath);
                        selectFalse(holder);
                } else {
                    mSelectedImg.add(filePath);
                    selectTrue(holder);
                }
            }
        });
    }

    /**
     * 被选择状态
     * @param holder
     */
    private void selectTrue(PictureListAdapter.ViewHolder holder){
        holder.select.setImageResource(R.drawable.yes_selection);
        holder.imageView.setColorFilter(Color.parseColor("#77000000"));
    }

    /**
     * 不被选择状态
     * @param holder
     */
    private void selectFalse(PictureListAdapter.ViewHolder holder){
        holder.select.setImageResource(R.drawable.no_selection);
        holder.imageView.setColorFilter(null);
    }

    @Override
    public int getItemCount() {
        return mImgList.size();
    }
}
