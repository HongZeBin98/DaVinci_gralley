package com.example.davinci.Util;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.ImageView;

import com.example.davinci.Bean.ImageBeanHolder;

import java.lang.ref.WeakReference;

/**
 * 该获取到通知后，从通知中获取数据，把图片放入图片容器中
 *  Created By Mr.Bean
 */
public class PictureHandler extends Handler {
    private final WeakReference<ImageLoader> mImageLoader;

    public PictureHandler(Looper looper, ImageLoader imageLoader){
        super(looper);
        mImageLoader = new WeakReference<>(imageLoader);
    }



    @Override
    public void handleMessage(Message msg) {
        ImageLoader imageLoader = mImageLoader.get();
        if (imageLoader == null){
            return;
        }
        super.handleMessage(msg);
        ImageBeanHolder imageBeanHolder = (ImageBeanHolder) msg.obj;
        Bitmap bm = imageBeanHolder.getBitmap();
        ImageView imageView= imageBeanHolder.getImageView();
        String path = imageBeanHolder.getPath();
        //如果path和之前设置的标签匹配则进行加载
        if(imageView.getTag() == path){
            imageView.setImageBitmap(bm);
        }
    }
}

