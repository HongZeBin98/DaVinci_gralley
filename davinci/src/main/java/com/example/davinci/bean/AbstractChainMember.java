package com.example.davinci.bean;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.example.davinci.util.ImageLoader;
import com.example.davinci.util.PictureLruCache;

import java.util.concurrent.Semaphore;

/**
 * 责任链中抽象获取图片
 * Created By Mr.Bean
 */
public abstract class AbstractChainMember {
    //下一个节点
    protected AbstractChainMember nextHandler;

    public void setNextHandler(AbstractChainMember nextHandler){
        this.nextHandler = nextHandler;
    }

    /**
     * 抽象方法获取图片
     * @param path 图片路径
     * @param imageView 设置图片的容器
     * @param reqFigure 图片压缩期望尺寸
     * @return 获取到的图片
     */
    public abstract Bitmap getBitmap(ImageLoader imageLoader, String path, int reqFigure
            , ImageView imageView, Semaphore taskSemaphore, PictureLruCache pictureLruCache);
}
