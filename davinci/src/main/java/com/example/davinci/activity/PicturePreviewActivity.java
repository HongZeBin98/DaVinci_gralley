package com.example.davinci.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.davinci.R;
import com.example.davinci.adapter.AlbumListAdapter;
import com.example.davinci.adapter.PreviewThumbnailAdapter;
import com.example.davinci.adapter.ViewpagerAdapter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static android.view.View.INVISIBLE;

/**
 * 图片预览
 * Created By Mr.Bean
 */
public class PicturePreviewActivity extends AppCompatActivity {

    private int mPosition;
    private int mPictureCount;
    private TextView mSender;
    private Toolbar mToolbar;
    private CheckBox mCheckBox;
    private ViewPager mViewPager;
    private RecyclerView mRecyclerView;
    private List<String> mPicturePathList;
    private ViewpagerAdapter mViewpagerAdapter;
    private List<String> mChangedPicturePathList;   //在预览中被更改的图片路径list
    private PreviewThumbnailAdapter mThumbnailAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_preview);
        initView();
        initData();
        initEvent();
    }

    /**
     * 获取由缩略图列表activity传入的所选择图片路径列表
     */
    private void getPicturePathList() {
        Intent intent = getIntent();
        mPicturePathList = intent.getStringArrayListExtra("picture_path_list");
        mChangedPicturePathList.addAll(mPicturePathList);
    }

    private void initView() {
        mChangedPicturePathList = new ArrayList<>();
        mSender = findViewById(R.id.id_preview_sender);
        mToolbar = findViewById(R.id.id_preview_toolbar);
        mViewPager = findViewById(R.id.id_preview_viewPager);
        mCheckBox = findViewById(R.id.id_bottom_preview_checkBox);
        mRecyclerView= findViewById(R.id.id_preview_recyclerView);
    }

    private void initData() {
        //得到缩略图activity返回的选中图片的路径list
        getPicturePathList();
        mPictureCount = mPicturePathList.size();
        //设置标题默认值
        mToolbar.setTitle(1 + "/" + mPictureCount);
        //设置发送键默认状态
        mSender.setText("发送(" + mPictureCount + "/" + mPictureCount + ")");
        mSender.setTextColor(getResources().getColor(R.color.colorWhite));
        //设置ToolBar
        setSupportActionBar(mToolbar);
        //给ViewPager提供数据
        mViewpagerAdapter = new ViewpagerAdapter(this, mPicturePathList);
        mViewPager.setAdapter(mViewpagerAdapter);
        //给recyclerView提供数据
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(manager);
        ((SimpleItemAnimator)mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        mThumbnailAdapter = new PreviewThumbnailAdapter(mPicturePathList);
        mRecyclerView.setAdapter(mThumbnailAdapter);
    }


    private void initEvent() {
        //标题栏返回键点击监听
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(mChangedPicturePathList, RESULT_CANCELED);
                finish();
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
                if (mChangedPicturePathList.contains(mPicturePathList.get(position))) {
                    mCheckBox.setChecked(true);
                } else {
                    mCheckBox.setChecked(false);
                }
                mToolbar.setTitle((position + 1) + "/" + mPictureCount);
                mThumbnailAdapter.setCurrentPosition(position);
                mRecyclerView.scrollToPosition(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        //对checkBox进行点击监听
        mCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int color;
                String senderStr;
                int changedPictureCount;
                String picturePath = mPicturePathList.get(mPosition);
                //通过点击对图片选择列表进行增加或者减少
                if (mChangedPicturePathList.contains(picturePath)) {
                    mChangedPicturePathList.remove(picturePath);
                } else {
                    mChangedPicturePathList.add(picturePath);
                }
                //对ToolBar上发送进行状态的改变
                changedPictureCount = mChangedPicturePathList.size();
                if (changedPictureCount > 0) {
                    senderStr = "发送(" + changedPictureCount + "/" + mPictureCount + ")";
                    color = getResources().getColor(R.color.colorWhite);
                } else {
                    senderStr = "";
                    color = getResources().getColor(R.color.colorDefaultSender);
                }
                mSender.setTextColor(color);
                mSender.setText(senderStr);
            }
        });
        mSender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(mChangedPicturePathList, RESULT_OK);
                finish();
            }
        });
        mThumbnailAdapter.setOnItemClickListener(new PreviewThumbnailAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                mViewPager.setCurrentItem(position, false);
            }
        });
    }

    @Override
    public void onBackPressed() {
        setResult(mChangedPicturePathList, RESULT_CANCELED);
        super.onBackPressed();

    }

    /**
     * 设置要返回给上一个activity的内容
     */
    private void setResult(List<String> list, int sign) {
        Intent intent = new Intent();
        intent.putStringArrayListExtra("data_return", (ArrayList<String>) list);
        setResult(sign, intent);
    }



    /**
     * 启动该activity
     * @param context         上一个activity
     * @param picturePathList 需要传入该activity的被选择图片路径
     */
    public static void actionStart(Context context, List<String> picturePathList) {
        Intent intent = new Intent(context, PicturePreviewActivity.class);
        intent.putStringArrayListExtra("picture_path_list", (ArrayList<String>) picturePathList);
        ((Activity)context).startActivityForResult(intent, 1);
    }
}
