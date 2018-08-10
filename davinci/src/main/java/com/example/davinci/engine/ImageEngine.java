package com.example.davinci.engine;

import android.widget.ImageView;

import com.example.davinci.util.ImageLoader;

public interface ImageEngine {

    void loadThumbnail(ImageLoader.Type type, String path, ImageView imageView, int reqFigure);

    void loadSamplePicture(ImageLoader.Type type, String path, ImageView imageView, int reqFigure);
}
