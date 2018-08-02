package com.example.davinci;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.example.davinci.activity.GalleryMainActivity;
import com.example.davinci.bean.DaVinci;

import java.util.ArrayList;
import java.util.List;

public class SelectionCreator {

    private final DaVinci mDaVinci;
    private SelectionSpec mSelectionSpec;

    public SelectionCreator(DaVinci daVinci){
        mDaVinci = daVinci;
        mSelectionSpec = SelectionSpec.getInstance();
    }

    public void forResult(int requestCode) {
        Activity activity = mDaVinci.getActivity();
        if (activity != null) {
            Intent intent = new Intent(activity, GalleryMainActivity.class);
            Fragment fragment = mDaVinci.getFragment();
            if(fragment != null){
                fragment.startActivityForResult(intent, requestCode);
            }else{
                activity.startActivityForResult(intent,requestCode);
            }
        }
    }

    public SelectionCreator maxSelectable(int maxSelectable) {
        if (maxSelectable < 1)
            throw new IllegalArgumentException("maxSelectable must be greater than or equal to one");
        mSelectionSpec.maxSelectable = maxSelectable;
        return this;
    }
}
