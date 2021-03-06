package com.example.davinci.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.davinci.SelectionSpec;
import com.example.davinci.bean.FolderBean;
import com.example.davinci.R;
import com.example.davinci.engine.ImageEngine;
import com.example.davinci.util.ImageLoader;

import java.util.List;

public class AlbumListAdapter extends RecyclerView.Adapter<AlbumListAdapter.ViewHolder> {

    private ImageEngine mEngine;
    private LayoutInflater mInflater;
    private List<FolderBean> mData;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onClick(int position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView albumName;
        TextView pictureNumber;

        ViewHolder(View itemView) {
            super(itemView);
            albumName = itemView.findViewById(R.id.id_popup_album_name);
            imageView = itemView.findViewById(R.id.id_popup_picture);
            pictureNumber = itemView.findViewById(R.id.id_popup_picture_number);
        }
    }

    public AlbumListAdapter(Context context, List<FolderBean> objects) {
        mInflater = LayoutInflater.from(context);
        mData = objects;
        mEngine = SelectionSpec.getInstance().imageEngine;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.popup_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        FolderBean bean = mData.get(position);
        holder.albumName.setText(bean.getName());
        holder.pictureNumber.setText(bean.getCount() + "张");
        holder.imageView.setImageResource(R.drawable.black_background);
        mEngine.loadThumbnail(ImageLoader.Type.LIFO, bean.getFirstImgPath(), holder.imageView, 100);
        if (mOnItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnItemClickListener.onClick(position);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }
}
