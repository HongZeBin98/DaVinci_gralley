package com.example.davinci.engine;

import android.widget.ImageView;

import com.example.davinci.util.ImageLoader;

public class InnerEngine implements ImageEngine{

    private static class Singleton {
        private static InnerEngine instance = new InnerEngine();
    }

    public static InnerEngine getInstance() {
        return InnerEngine.Singleton.instance;
    }

    @Override
    public void loadThumbnail(ImageLoader.Type type, String path, ImageView imageView, int reqFigure) {
        ImageLoader.getInstance(type).loadImage(path, imageView, false, reqFigure);
    }

    @Override
    public void loadSamplePicture(ImageLoader.Type type, String path, ImageView imageView, int reqFigure) {
        ImageLoader.getInstance(type).loadImage(path, imageView, true, reqFigure);
    }
}
