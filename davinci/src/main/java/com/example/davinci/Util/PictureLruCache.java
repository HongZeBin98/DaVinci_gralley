package com.example.davinci.Util;

import android.graphics.Bitmap;
import android.util.LruCache;

/**
 * 对图片进行内存缓存的操作
 * Created By Mr.Bean
 */
public class PictureLruCache {

    private LruCache<String, Bitmap> mLruCache;

    public PictureLruCache(){
        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        int cacheSize = maxMemory / 8;
        mLruCache = new LruCache<String, Bitmap>(cacheSize){
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getRowBytes() * bitmap.getHeight() / 1024;
            }
        };
    }

    /**
     * 获取缓存容器
     * @return 获取缓存容器
     */
    public LruCache<String, Bitmap> getLruCache(){
        return mLruCache;
    }

    /**
     * 得到缓存中的图片
     * @param path 图片路径
     * @return 缓存中的图片
     */
    public Bitmap getBitmapFromLruCache(String path){
        return mLruCache.get(path);
    }

    /**
     * 将图片和地址加入缓存
     * @param path
     * @param bitmap
     */
    public void addBitmapToLruCache(String path, Bitmap bitmap){
        if (getBitmapFromLruCache(path) == null){
            if(bitmap != null){
                mLruCache.put(path, bitmap);
            }
        }
    }
}
