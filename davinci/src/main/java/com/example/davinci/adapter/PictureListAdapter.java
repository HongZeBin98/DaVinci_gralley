package com.example.davinci.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.davinci.R;
import com.example.davinci.SelectionSpec;
import com.example.davinci.activity.AmplificationActivity;
import com.example.davinci.engine.ImageEngine;
import com.example.davinci.util.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import static com.example.davinci.util.Constants.MAX_SELECTION_COUNT;

/**
 * recyclerView适配器
 * Created By Mr.Bean
 */
public class PictureListAdapter extends RecyclerView.Adapter<PictureListAdapter.ViewHolder> {
    //记录被选择的图片
    private List<String> mSelectedImg = new ArrayList<>();
    private ImageEngine mEngine;
    private int mPictureCount = 0;
    private Context mContext;
    private List<String> mImgList;
    private LocalBroadcastManager mLocalBroadcastManager;

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ImageButton selection;

        ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.id_item_picture);
            selection = itemView.findViewById(R.id.id_item_select);
        }
    }

    public PictureListAdapter(List<String> imgList, Context context, LocalBroadcastManager localBroadcastManager) {
        mImgList = imgList;
        mContext = context;
        mLocalBroadcastManager = localBroadcastManager;
        mEngine = SelectionSpec.getInstance().imageEngine;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        @SuppressLint("InflateParams") View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item, null, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final PictureListAdapter.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final String path = mImgList.get(position);
        holder.imageView.setImageResource(R.drawable.black_background);
        holder.imageView.setColorFilter(null);
        mEngine.loadThumbnail(ImageLoader.Type.LIFO, path, holder.imageView, 100);
        if (mSelectedImg.contains(path)) {
            selectTrue(holder);
        } else {
            selectFalse(holder);
        }
        holder.selection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               selectionOnClick(path, holder);
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AmplificationActivity.actionStart(mContext, mImgList, mSelectedImg ,position);
            }
        });
    }

    /**
     * 图片选择框被点击
     * @param path 被选中图片的路径
     * @param holder ViewHolder
     */
    private void selectionOnClick(String path, ViewHolder holder){
        Intent intent = new Intent();
        //该图片已经被选择
        if (mSelectedImg.contains(path)) {
            mSelectedImg.remove(path);
            selectFalse(holder);
            mPictureCount--;
            //设置一条图片选择数减少的广播
            intent.setAction("com.example.davinci.REDUCE_SELECTION");
        }
        //该图片没有被选择
        else {
            if (mPictureCount < MAX_SELECTION_COUNT) {
                mSelectedImg.add(path);
                selectTrue(holder);
                mPictureCount++;
                //设置一条图片选择数增加的广播
                intent.setAction("com.example.davinci.ADD_SELECTION");
            } else {
                Toast.makeText(mContext, "你最多只能选择" + MAX_SELECTION_COUNT + "张图片！", Toast.LENGTH_SHORT).show();
            }
        }
        //发送广播通知主Activity，更新被选择的图片数
        mLocalBroadcastManager.sendBroadcast(intent);
    }

    /**
     * 被选择状态
     *
     * @param holder viewHolder
     */
    private void selectTrue(PictureListAdapter.ViewHolder holder) {
        holder.selection.setImageResource(R.drawable.yes_selection);
        holder.imageView.setColorFilter(Color.parseColor("#77000000"));
    }

    /**
     * 不被选择状态
     *
     * @param holder viewHolder
     */
    private void selectFalse(PictureListAdapter.ViewHolder holder) {
        holder.selection.setImageResource(R.drawable.no_selection);
        holder.imageView.setColorFilter(null);
    }

    @Override
    public int getItemCount() {
        return mImgList.size();
    }

    public List<String> getSelectedImg() {
        return mSelectedImg;
    }

    public void setSelectedImg(List<String> list) {
        mSelectedImg = list;
    }
}
