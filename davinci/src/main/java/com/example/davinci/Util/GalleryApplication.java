package com.example.davinci.Util;

import android.content.Context;

public class GalleryApplication  extends android.app.Application {
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }

    //获取context
    public static Context getmContext() {
        return mContext;
    }
}
