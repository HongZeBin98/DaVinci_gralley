package com.example.davinci.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * 对图片进行压缩
 * Created by Mr.Bean
 */
public class ImageResizer {

    /**
     * 获取图片的缩略图
     * @param path  图片路径
     * @param reqWidth  期望的宽度
     * @param reqHeight 期望的高度
     * @return  缩略图
     */
    public Bitmap decodeSampleBitmapFromResource(String path, int reqWidth, int reqHeight){
        //获取图片的宽和高，并不加载到内存中
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        //获取适当的采样值
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }

    /**
     * 获取适当的采样值
     * @param options 图片的Option
     * @param reqWidth  期望的宽度
     * @param reqHeight 期望的高度
     * @return  适当的采样值
     */
    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight){
        if(reqHeight == 0 || reqWidth == 0){
            return 1;
        }
        //获取图片原本高度和宽度
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        //判断是否采样值能更加大
        if(height > reqHeight || width > reqWidth){
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while((halfHeight /  inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth){
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

}
