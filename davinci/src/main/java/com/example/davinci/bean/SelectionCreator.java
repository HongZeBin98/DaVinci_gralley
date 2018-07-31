package com.example.davinci.bean;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.example.davinci.activity.GalleryMainActivity;

public class SelectionCreator {

    private DaVinci mDaVinci;

    SelectionCreator(DaVinci daVinci){
        mDaVinci = daVinci;
    }

    public void forResult(int requestCode) {
        Activity activity = mDaVinci.getActivity();
        if (activity != null) {
            Intent intent = new Intent(activity, GalleryMainActivity.class);
            Fragment fragment = mDaVinci.getFragment();
            if(fragment != null){
                fragment.startActivityForResult(intent, requestCode);
            }else{
                activity.startActivity(intent);
            }
        }
    }
}
