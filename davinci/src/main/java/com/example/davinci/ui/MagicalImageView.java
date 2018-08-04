package com.example.davinci.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.ViewTreeObserver;

public class MagicalImageView extends android.support.v7.widget.AppCompatImageView implements ViewTreeObserver.OnGlobalLayoutListener {
    //设置初始加载图片只获取一次
    private boolean mOnce = false;
    private Bitmap mBitmap;


    public MagicalImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

    }



    @Override
    public void onGlobalLayout() {
        if (!mOnce) {
            //得到自定义控件的宽和高
            final int width = getWidth();
            final int height = getHeight();
            //获取图片的宽和高
            if (mBitmap == null) {
                return;
            }
            final int bmHeight = mBitmap.getHeight();
            final int bmWidth = mBitmap.getWidth();
            //获取移动到ImageView中央的距离
            int moveX = width / 2 - bmWidth / 2;
            int moveY = height / 2 - bmHeight / 2;
            //获取缩放的比例
            float scale;
            if(bmWidth / bmHeight >=1){
                scale = width / bmWidth;
            }else {
                scale = height / bmHeight;
            }

            Matrix matrix = new Matrix();
            matrix.postTranslate(moveX, moveY);
            matrix.postScale(scale, scale);

            setImageMatrix(matrix);

            mOnce = true;
        }
    }

    /**
     * ImageView出现的时候调用这个方法
     */
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        //注册OnGlobalLayoutListener
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    /**
     * ImageView从屏幕上消失的时候调用这个方法
     */
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        //注销OnGlobalLayoutListener
        getViewTreeObserver().removeOnGlobalLayoutListener(this);
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        mBitmap = bm;
    }

}
