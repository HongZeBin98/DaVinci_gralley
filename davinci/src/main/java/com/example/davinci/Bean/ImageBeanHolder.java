package com.example.davinci.Bean;

import android.graphics.Bitmap;
import android.widget.ImageView;

public class ImageBeanHolder {

    private ImageView imageView;
    private String path;
    private Bitmap bitmap;

    public ImageBeanHolder(Bitmap bitmap, ImageView imageView, String path) {
        this.bitmap = bitmap;
        this.imageView = imageView;
        this.path = path;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public String getPath() {
        return path;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }
}
