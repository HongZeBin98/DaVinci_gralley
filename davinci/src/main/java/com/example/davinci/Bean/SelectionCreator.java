package com.example.davinci.Bean;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.example.davinci.Activity.GalleryMainActivity;

public class SelectionCreator {

    private DaVinci mDaVinci;

    public SelectionCreator(DaVinci daVinci){
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
