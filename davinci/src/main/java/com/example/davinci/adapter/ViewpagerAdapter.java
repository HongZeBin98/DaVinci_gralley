package com.example.davinci.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.davinci.R;
import com.example.davinci.util.ImageLoader;
import java.util.List;

public class ViewpagerAdapter extends PagerAdapter {

    private Context mContext;
    private List<String> mPicturePathList;

    public ViewpagerAdapter(Context context, List<String> picturePathList) {
        mContext = context;
        mPicturePathList = picturePathList;
    }

    @Override
    public int getCount() {
        return mPicturePathList.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = View.inflate(mContext, R.layout.viewpager_item, null);
        ImageView imageView = view.findViewById(R.id.id_viewpager_imageView);
        ImageLoader.getInstance(ImageLoader.Type.LIFO).loadImage(mPicturePathList.get(position), imageView, true);
        container.addView(view);
        return view;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

}
