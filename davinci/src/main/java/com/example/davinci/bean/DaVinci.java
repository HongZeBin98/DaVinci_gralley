package com.example.davinci.bean;

import android.app.Activity;
import android.support.v4.app.Fragment;

import java.lang.ref.WeakReference;

public class DaVinci {
    private final WeakReference<Activity> mContext;
    private WeakReference<Fragment> mFragment;

    private DaVinci(Activity activity){
        this.mContext = new WeakReference<>(activity);
        mFragment = null;
    }

    public static DaVinci from(Activity activity){
        return new DaVinci(activity);
    }

    public Activity getActivity(){
        return mContext.get();
    }

    public Fragment getFragment(){
        if(mFragment == null){
            return null;
        }else{
            return mFragment.get();
        }
    }

    public SelectionCreator choose(){
        return new SelectionCreator(this);
    }
}
