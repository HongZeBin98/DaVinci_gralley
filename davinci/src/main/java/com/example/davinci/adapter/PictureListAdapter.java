package com.example.davinci.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.davinci.R;
import com.example.davinci.util.ImageLoader;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * recyclerView适配器
 * Created By Mr.Bean
 */
public class PictureListAdapter extends RecyclerView.Adapter<PictureListAdapter.ViewHolder> {
    //记录被选择的图片
    private static Set<String> mSelectedImg = new HashSet<>();
    private static int mPictureNumber = 0;
    private Context mContext;
    private List<String> mImgList;
    private String mDirPath;

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ImageButton select;

        ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.id_item_picture);
            select = itemView.findViewById(R.id.id_item_select);
        }
    }

    public PictureListAdapter(List<String> imgList, String dirPath, Context context) {
        mImgList = imgList;
        mDirPath = dirPath;
        mContext = context;
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
                //该图片已经被选择
                if (mSelectedImg.contains(filePath)) {
                    mSelectedImg.remove(filePath);
                    selectFalse(holder);
                    mPictureNumber--;
                }
                //该图片没有被选择
                else {
                    if (mPictureNumber < 9) {
                        mSelectedImg.add(filePath);
                        selectTrue(holder);
                        mPictureNumber++;
                    } else {
                        Toast.makeText(mContext, "你最多只能选择9张图片！", Toast.LENGTH_SHORT).show();
                    }
                }
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
}
