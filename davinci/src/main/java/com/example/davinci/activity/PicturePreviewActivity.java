package com.example.davinci.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.davinci.R;
import com.example.davinci.adapter.ViewpagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class PicturePreviewActivity extends AppCompatActivity {

    private List<String> mPicturePathList;
    private ViewPager mViewPager;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_preview);
        getPicturePathList();
        initView();
        initData();
        initEvent();
    }
    //获取由缩略图列表activity传入的所选择图片路径列表
    private void getPicturePathList() {
        Intent intent = getIntent();
        mPicturePathList = intent.getStringArrayListExtra("picture_path_list");
    }

    private void initView() {
        mToolbar = findViewById(R.id.id_preview_toolbar);
        mViewPager = findViewById(R.id.id_preview_viewPager);
    }

    private void initData() {
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
    }

    public static void actionStart(Context context, List<String> picturePathList){
        Intent intent = new Intent(context, PicturePreviewActivity.class);
        intent.putStringArrayListExtra("picture_path_list", (ArrayList<String>) picturePathList);
        context.startActivity(intent);
    }
}
