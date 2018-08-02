package com.example.davinci.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.davinci.R;
import com.example.davinci.util.ImageLoader;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.example.davinci.util.Constants.MAX_SELECTION_COUNT;

/**
 * recyclerView适配器
 * Created By Mr.Bean
 */
public class PictureListAdapter extends RecyclerView.Adapter<PictureListAdapter.ViewHolder> {
    //记录被选择的图片
    private List<String> mSelectedImg = new ArrayList<>();
    private int mPictureCount = 0;
    private Context mContext;
    private List<String> mImgList;
    private LocalBroadcastManager mLocalBroadcastManager;

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ImageButton select;

        ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.id_item_picture);
            select = itemView.findViewById(R.id.id_item_select);
        }
    }

    public PictureListAdapter(List<String> imgList, Context context, LocalBroadcastManager localBroadcastManager) {
        mImgList = imgList;
        mContext = context;
        mLocalBroadcastManager = localBroadcastManager;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview, null, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final PictureListAdapter.ViewHolder holder, int position) {
        final String path = mImgList.get(position);
        holder.imageView.setImageResource(R.drawable.black_background);
        holder.imageView.setColorFilter(null);
        final String filePath = path;
        ImageLoader.getInstance(ImageLoader.Type.LIFO).loadImage(filePath, holder.imageView);
        if (mSelectedImg.contains(filePath)) {
            selectTrue(holder);
        } else {
            selectFalse(holder);
        }
        holder.select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                //该图片已经被选择
                if (mSelectedImg.contains(filePath)) {
                    mSelectedImg.remove(filePath);
                    selectFalse(holder);
                    mPictureCount--;
                    intent.setAction("com.example.davinci.REDUCE_SELECTION");
                }
                //该图片没有被选择
                else {
                    if (mPictureCount < MAX_SELECTION_COUNT) {
                        mSelectedImg.add(filePath);
                        selectTrue(holder);
                        mPictureCount++;
                        intent.setAction("com.example.davinci.ADD_SELECTION");
                    } else {
                        Toast.makeText(mContext, "你最多只能选择" + MAX_SELECTION_COUNT + "张图片！", Toast.LENGTH_SHORT).show();
                    }
                }
                mLocalBroadcastManager.sendBroadcast(intent);
            }
        });
    }

    /**
     * 被选择状态
     *
     * @param holder viewHolder
     */
    private void selectTrue(PictureListAdapter.ViewHolder holder) {
        holder.select.setImageResource(R.drawable.yes_selection);
        holder.imageView.setColorFilter(Color.parseColor("#77000000"));
    }

    /**
     * 不被选择状态
     *
     * @param holder viewHolder
     */
    private void selectFalse(PictureListAdapter.ViewHolder holder) {
        holder.select.setImageResource(R.drawable.no_selection);
        holder.imageView.setColorFilter(null);
    }

    @Override
    public int getItemCount() {
        return mImgList.size();
    }

    public List<String> getmSelectedImg(){
        return mSelectedImg;
    }
}
