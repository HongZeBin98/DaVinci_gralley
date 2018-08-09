package com.example.davinci.bean;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.example.davinci.util.ImageLoader;
import com.example.davinci.util.ImageResizer;
import com.example.davinci.util.PictureLruCache;

import java.util.concurrent.Semaphore;

/**
 * 从缓存中获取图片
 * Created By Mr.Bean
 */
public class LruCacheChainMember extends AbstractChainMember {

    @Override
    public Bitmap getBitmap(final ImageLoader imageLoader, final String path, final int reqFigure
            , final ImageView imageView, final Semaphore taskSemaphore, final PictureLruCache pictureLruCache) {
        Bitmap bitmap = pictureLruCache.getBitmapFromLruCache(path);
        if (bitmap != null) {
            imageLoader.sendUIMessage(bitmap, imageView, path);
        } else {
            imageLoader.addTaskIntoQueue(new Runnable() {
                @Override
                public void run() {
                    Bitmap bm;
                    //获取缩略图
                    bm = nextHandler.getBitmap(imageLoader, path, reqFigure, imageView, taskSemaphore, pictureLruCache);
//                    bm = new ImageResizer().decodeSampleBitmapFromResource(path, reqFigure, reqFigure);
                    //把缩略图放入缓存
                    pictureLruCache.addBitmapToLruCache(path, bm);
                    //释放信号量
                    taskSemaphore.release();
                    imageLoader.sendUIMessage(bm, imageView, path);
                }
            });
        }
        return null;
    }
}
