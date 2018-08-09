package com.example.davinci.activity;

import android.annotation.SuppressLint;
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
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.davinci.R;
import com.example.davinci.SelectionSpec;
import com.example.davinci.adapter.PreviewThumbnailAdapter;
import com.example.davinci.adapter.viewpagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class AmplificationActivity extends AppCompatActivity {

    private int mFolderPictureCount;
    private int mPagerPosition;
    private int mSelectedPosition;
    private int mMaxSelectionCount;
    private TextView mSender;
    private Toolbar mToolbar;
    private CheckBox mCheckBox;
    private ViewPager mViewPager;
    private RecyclerView mRecyclerView;
    private List<Integer> mPositionList;    //储存选中的文件夹图片在缩略图中的位置
    private List<String> mFolderPicturePathList;
    private List<String> mChangedPicturePathList;   //在预览中被更改的图片路径list
    private PreviewThumbnailAdapter mThumbnailAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amplification);
        initView();
        initData();
        initEvent();
    }

    /**
     * 获取由缩略图列表activity传入的所选择图片路径列表
     */
    private void getPicturePathList() {
        Intent intent = getIntent();
        mSelectedPosition = intent.getIntExtra("selection_position", -1);
        mChangedPicturePathList = intent.getStringArrayListExtra("selection_picture_path_list");
        mFolderPicturePathList = intent.getStringArrayListExtra("folder_picture_path_list");
    }

    private void initView(){
        mPositionList = new ArrayList<>();
        mChangedPicturePathList = new ArrayList<>();
        mSender = findViewById(R.id.id_amplification_sender);
        mToolbar = findViewById(R.id.id_amplification_toolbar);
        mViewPager = findViewById(R.id.id_amplification_viewPager);
        mCheckBox = findViewById(R.id.id_bottom_amplification_checkBox);
        mRecyclerView = findViewById(R.id.id_amplification_recyclerView);
    }

    @SuppressLint("SetTextI18n")
    private void initData() {
        //得到上一个activity传递过来的数据
        getPicturePathList();
        mPagerPosition = mSelectedPosition;
        mMaxSelectionCount = SelectionSpec.getInstance().maxSelectable;
        //把选中的图片在文件夹中的位置保存
        for(String x: mChangedPicturePathList){
            mPositionList.add(mFolderPicturePathList.indexOf(x));
        }
        //设置默认没有被选择
        mCheckBox.setChecked(false);
        int selectedPictureCount = mChangedPicturePathList.size();
        mFolderPictureCount = mFolderPicturePathList.size();
        //设置标题默认值
        mToolbar.setTitle((mSelectedPosition + 1) + "/" + mFolderPictureCount);
        //设置发送键默认状态
        mSender.setText("发送(" + selectedPictureCount + "/" + mMaxSelectionCount + ")");
        mSender.setTextColor(getResources().getColor(R.color.colorWhite));
        //设置ToolBar
        setSupportActionBar(mToolbar);
        //给ViewPager提供数据
        viewpagerAdapter viewpagerAdapter = new viewpagerAdapter(this, mFolderPicturePathList);
        mViewPager.setAdapter(viewpagerAdapter);
        mViewPager.setCurrentItem(mSelectedPosition, false);
        //给recyclerView提供数据
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(manager);
        ((SimpleItemAnimator) mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        mThumbnailAdapter = new PreviewThumbnailAdapter(mChangedPicturePathList);
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
                mPagerPosition = position;
                if (mChangedPicturePathList.contains(mFolderPicturePathList.get(position))) {
                    mCheckBox.setChecked(true);
                } else {
                    mCheckBox.setChecked(false);
                }
                mToolbar.setTitle((position + 1) + "/" + mFolderPictureCount);
                int thumbnailPosition = mPositionList.indexOf(position);
                if(thumbnailPosition != -1){
                    mThumbnailAdapter.setCurrentPosition(thumbnailPosition);
                }
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
                String picturePath = mFolderPicturePathList.get(mPagerPosition);
                //通过点击对图片选择列表进行增加或者减少
                if (mChangedPicturePathList.contains(picturePath)) {
                    mChangedPicturePathList.remove(picturePath);
                    mPositionList.remove((Integer) mPagerPosition);
                } else {
                    mChangedPicturePathList.add(picturePath);
                    mPositionList.add(mPagerPosition);
                }
                //对ToolBar上发送进行状态的改变
                changedPictureCount = mChangedPicturePathList.size();
                if (changedPictureCount > 0) {
                    senderStr = "发送(" + changedPictureCount + "/" + mMaxSelectionCount + ")";
                    color = getResources().getColor(R.color.colorWhite);
                } else {
                    senderStr = "";
                    color = getResources().getColor(R.color.colorDefaultSender);
                }
                mSender.setTextColor(color);
                mSender.setText(senderStr);
                mThumbnailAdapter.notifyDataSetChanged();
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
                mViewPager.setCurrentItem(mPositionList.get(position), false);
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
     *
     * @param context                 上一个activity
     * @param selectedPicturePathList 需要传入该activity的被选择图片路径
     * @param allPicturePathList      需要传入所有图片的路径
     */
    public static void actionStart(Context context, List<String> allPicturePathList, List<String> selectedPicturePathList, int position) {
        Intent intent = new Intent(context, AmplificationActivity.class);
        intent.putExtra("selection_position", position);
        intent.putStringArrayListExtra("folder_picture_path_list", (ArrayList<String>) allPicturePathList);
        intent.putStringArrayListExtra("selection_picture_path_list", (ArrayList<String>) selectedPicturePathList);
        ((Activity) context).startActivityForResult(intent, 1);
    }
}
