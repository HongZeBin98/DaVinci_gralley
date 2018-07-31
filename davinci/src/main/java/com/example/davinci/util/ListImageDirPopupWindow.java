package com.example.davinci.util;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.example.davinci.adapter.AlbumListAdapter;
import com.example.davinci.bean.FolderBean;
import com.example.davinci.R;

import java.util.List;

public class ListImageDirPopupWindow extends PopupWindow {

    private int mWidth;
    private int mHeight;
    private View mConvertView;
    private RecyclerView mRecyclerView;
    private List<FolderBean> mData;

    public ListImageDirPopupWindow(Context context, List<FolderBean> data) {
        setWidthAndHeight(context);

        mConvertView = LayoutInflater.from(context).inflate(R.layout.popup_main, null);
        mData = data;
        //设置popupWindow的宽度和高度
        setContentView(mConvertView);
        setWidth(mWidth);
        setHeight(mHeight);

        setFocusable(true);
        setTouchable(true);
        setOutsideTouchable(true);
        setBackgroundDrawable(new BitmapDrawable());

        setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_OUTSIDE){
                    dismiss();
                    return true;
                }
                return false;
            }
        });

        initViews(context);
    }

    private void initViews(Context context) {
        mRecyclerView = mConvertView.findViewById(R.id.id_popup_recyclerView);
        LinearLayoutManager manager = new LinearLayoutManager(context);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(new AlbumListAdapter(context, mData));
    }

    /**
     * 计算popupWindow的宽和高
     */
    private void setWidthAndHeight(Context context) {
        //获取屏幕的长和宽
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        //设置popupWindow宽是屏幕宽度
        mWidth = outMetrics.widthPixels;
        //设置popupWindow高度是屏幕高度的70%
        mHeight = (int) (outMetrics.heightPixels * 0.7);
    }
}
