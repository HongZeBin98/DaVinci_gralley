package com.example.davinci.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;

import com.example.davinci.R;
import com.example.davinci.adapter.ViewpagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class PicturePreviewActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private Toolbar mToolbar;
    private CheckBox mCheckBox;
    private int mPosition;
    private List<String> mPicturePathList;
    //在预览中被更改的图片路径list
    private List<String> mChangedPicturePathList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_preview);
        initView();
        initData();
        initEvent();
    }
    //获取由缩略图列表activity传入的所选择图片路径列表
    private void getPicturePathList() {
        Intent intent = getIntent();
        mPicturePathList = intent.getStringArrayListExtra("picture_path_list");
        mChangedPicturePathList.addAll(mPicturePathList);
    }

    private void initView() {
        mChangedPicturePathList = new ArrayList<>();
        mToolbar = findViewById(R.id.id_preview_toolbar);
        mViewPager = findViewById(R.id.id_preview_viewPager);
        mCheckBox = findViewById(R.id.id_bottom_preview_checkBox);
    }

    private void initData() {
        //得到缩略图activity返回的选中图片的路径list
        getPicturePathList();
        //设置ToolBar
        setSupportActionBar(mToolbar);
        //给ViewPager提供数据
        ViewpagerAdapter viewpagerAdapter = new ViewpagerAdapter(this, mPicturePathList);
        mViewPager.setAdapter(viewpagerAdapter);
    }


    private void initEvent() {
        //标题栏返回键点击监听
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PicturePreviewActivity.this.finish();
            }
        });
        //设置判断viewPager滚动到每一页时checkBox是否被选中
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                mPosition = position;
                if(mChangedPicturePathList.contains(mPicturePathList.get(position))){
                    mCheckBox.setChecked(true);
                }else {
                    mCheckBox.setChecked(false);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        //对checkBox进行点击监听
        mCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String picturePath = mPicturePathList.get(mPosition);
                if (mChangedPicturePathList.contains(picturePath)) {
                    mChangedPicturePathList.remove(picturePath);
                } else {
                    mChangedPicturePathList.add(picturePath);
                }
            }
        });
    }

    public static void actionStart(Context context, List<String> picturePathList){
        Intent intent = new Intent(context, PicturePreviewActivity.class);
        intent.putStringArrayListExtra("picture_path_list", (ArrayList<String>) picturePathList);
        context.startActivity(intent);
    }
}
