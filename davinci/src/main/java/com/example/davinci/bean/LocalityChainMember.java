package com.example.davinci.bean;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.example.davinci.util.ImageLoader;
import com.example.davinci.util.ImageResizer;
import com.example.davinci.util.PictureLruCache;

import java.util.concurrent.Semaphore;

/**
 * 从本地获取图片
 * Created By Mr.Bean
 */
public class LocalityChainMember extends AbstractChainMember {

    @Override
    public Bitmap getBitmap(ImageLoader imageLoader, String path, int reqFigure, ImageView imageView
            , Semaphore taskSemaphore, PictureLruCache pictureLruCache) {
        Bitmap bitmap = new ImageResizer().decodeSampleBitmapFromResource(path, reqFigure, reqFigure);
        if (bitmap != null){
            return bitmap;
        }else{
            if (nextHandler != null){
                return nextHandler.getBitmap(imageLoader, path, reqFigure, imageView, taskSemaphore, pictureLruCache);
            }else {
                return null;
            }
        }
    }
}
