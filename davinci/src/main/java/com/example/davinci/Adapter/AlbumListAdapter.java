package com.example.davinci.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.davinci.Bean.FolderBean;
import com.example.davinci.R;
import com.example.davinci.Util.ImageLoader;

import java.util.List;

public class AlbumListAdapter extends RecyclerView.Adapter<AlbumListAdapter.ViewHolder> {

    private LayoutInflater mInflater;
    private List<FolderBean> mData;

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
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.popup_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FolderBean bean = mData.get(position);
        holder.pictureNumber.setText(bean.getCount() + "");
        holder.albumName.setText(bean.getName());
        holder.imageView.setImageResource(R.drawable.black_background);
        ImageLoader.getInstance(ImageLoader.Type.LIFO).loadImage(bean.getFirstImgPath(), holder.imageView);

    }


    @Override
    public int getItemCount() {
        return mData.size();
    }
}
