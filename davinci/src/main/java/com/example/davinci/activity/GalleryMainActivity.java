package com.example.davinci.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.davinci.adapter.PictureListAdapter;
import com.example.davinci.bean.FolderBean;
import com.example.davinci.R;
import com.example.davinci.model.PictureModel;
import com.example.davinci.util.ListImageDirPopupWindow;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.example.davinci.util.Constants.MAX_SELECTION_COUNT;
import static com.example.davinci.util.Constants.SCAN_FINISH;

/**
 * 图库的主界面
 * Created By Mr.Bean
 */
public class GalleryMainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RelativeLayout mRelativeLayout;
    private TextView mPreviewer;
    private TextView mAlbumSelection;
    private TextView mSender;
    private List<FolderBean> mFolderBeans;
    private ListImageDirPopupWindow mPopupWindow;
    private List<String> mImg;
    private PictureListAdapter mPictureListAdapter;
    private List<String> mAllPictureList;
    private Toolbar mToolbar;
    private LocalSelectionCountReceiver mLocalReceiver;
    private LocalBroadcastManager mLocalBroadcastManager;
    private int mSelectionCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
        initEvent();
    }

    /**
     * 配置recyclerVew
     */
    protected void dataView() {
        //设置网格布局
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mImg.addAll(mAllPictureList);
        mPictureListAdapter = new PictureListAdapter(mImg, this, mLocalBroadcastManager);
        mRecyclerView.setAdapter(mPictureListAdapter);
    }

    protected void initPopupWindow() {
        mPopupWindow = new ListImageDirPopupWindow(this, mFolderBeans);
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                lightOn();
            }
        });
        mPopupWindow.setOnDirSelectedListener(new ListImageDirPopupWindow.OnDirSelectedListener() {
            @Override
            public void onSelected(FolderBean folderBean) {
                File currentDir;
                mImg.clear();
                if (folderBean.getName().equals("所有图片")) {
                    mImg.addAll(mAllPictureList);
                } else {
                    currentDir = new File(folderBean.getDir());
                    List<String> list = Arrays.asList(currentDir.list(new FilenameFilter() {
                        @Override
                        public boolean accept(File file, String s) {
                            return s.endsWith(".jpg") || s.endsWith(".jpeg") || s.endsWith(".png");
                        }
                    }));
                    String dirPath = currentDir.getAbsolutePath();
                    for (String x : list) {
                        mImg.add(dirPath + "/" + x);
                    }
                    Collections.reverse(mImg);
                }
                mAlbumSelection.setText(folderBean.getName());
                mPictureListAdapter.notifyDataSetChanged();
                mPopupWindow.dismiss();
            }
        });
    }

    /**
     * 内容区变亮
     */
    private void lightOn() {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 1.0f;
        getWindow().setAttributes(lp);
    }

    /**
     * 内容区域变暗
     */
    private void lightOff() {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.3f;
        getWindow().setAttributes(lp);
    }

    private void initEvent() {
        //底部栏点击监听弹出相册选择列表
        mRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPopupWindow.showAsDropDown(mRelativeLayout, 0, 0);
                lightOff();
            }
        });
        //标题栏返回键点击监听
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GalleryMainActivity.this.finish();
            }
        });
        //标题栏发送键点击监听
        mSender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(mPictureListAdapter.getSelectedImg());
                finish();
            }
        });
        //底部栏预览按键点击监听
        mPreviewer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PicturePreviewActivity.actionStart(GalleryMainActivity.this, mPictureListAdapter.getSelectedImg());
            }
        });
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.davinci.REDUCE_SELECTION");
        intentFilter.addAction("com.example.davinci.ADD_SELECTION");
        mLocalBroadcastManager.registerReceiver(mLocalReceiver, intentFilter);
    }

    /**
     * 实例化
     */
    private void initView() {
        mImg = new ArrayList<>();
        mLocalReceiver = new LocalSelectionCountReceiver();
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
        mPreviewer = findViewById(R.id.id_bottom_preview);
        mToolbar = findViewById(R.id.toolbar);
        mSender = findViewById(R.id.id_Toolbar_sender);
        mRecyclerView = findViewById(R.id.recyclerView);
        mAlbumSelection = findViewById(R.id.id_bottom_album_selection);
        mRelativeLayout = findViewById(R.id.id_bottom_relativeLayout);
    }

    /**
     * 利用ContentProvider扫描手机中所有图片
     */
    private void initData() {
        //判断储存卡是否可用
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, "当前储存卡不可用！", Toast.LENGTH_SHORT).show();
            return;
        }
        //设置ToolBar
        setSupportActionBar(mToolbar);
        //扫描所有图片
        PictureModel.scanPicture(this, new PictureModel.ScanPictureCallBack() {
            @Override
            public void onFinish(List<FolderBean> folderBeanList, List<String> allPictureList) {
                mFolderBeans = folderBeanList;
                mAllPictureList = allPictureList;
                //通知Handler扫描图片完成
                AdapterHandler adapterHandler = new AdapterHandler(getMainLooper(), GalleryMainActivity.this);
                adapterHandler.sendEmptyMessage(SCAN_FINISH);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //取消注册广播接收器
        mLocalBroadcastManager.unregisterReceiver(mLocalReceiver);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                List<String> returnList = data.getStringArrayListExtra("data_return");
                //把返回的数据再返回给调用module接口的activity
                if (resultCode == RESULT_OK) {
                    setResult(returnList);
                    finish();
                }
                //把得到的新的选择图片路径列表更新到界面上
                else {
                    mSelectionCount = returnList.size();
                    mPictureListAdapter.setSelectedImg(returnList);
                    mPictureListAdapter.notifyDataSetChanged();
                    changeTextViewCount(this);
                }
                break;
            default:
        }
    }

    /**
     * 设置要返回给上一个activity的内容
     */
    private void setResult(List<String> list) {
        Intent intent = new Intent();
        intent.putStringArrayListExtra("data_return", (ArrayList<String>) list);
        setResult(RESULT_OK, intent);
    }


    /**
     * 改变预览和发送textView上的被选择数
     * @param context 上下文
     */
    private void changeTextViewCount(Context context){
        int color;
        String senderStr;
        String previewerSrt;
        if (mSelectionCount != 0) {
            senderStr = "发送(" + mSelectionCount + "/" + MAX_SELECTION_COUNT + ")";
            previewerSrt = "预览(" + mSelectionCount + ")";
            color = context.getResources().getColor(R.color.colorWhite);
        } else {
            senderStr = "";
            previewerSrt = "";
            color = context.getResources().getColor(R.color.colorDefaultSender);
        }
        mPreviewer.setText(previewerSrt);
        mSender.setTextColor(color);
        mSender.setText(senderStr);
    }

    private class AdapterHandler extends Handler {
        private final WeakReference<GalleryMainActivity> mMainActivity;

        AdapterHandler(Looper looper, GalleryMainActivity mainActivity) {
            super(looper);
            mMainActivity = new WeakReference<>(mainActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            GalleryMainActivity imageLoader = mMainActivity.get();
            if (imageLoader == null) {
                return;
            }
            super.handleMessage(msg);
            if (msg.what == SCAN_FINISH) {
                dataView();
                initPopupWindow();
            }
        }
    }

    /**
     * 发送选择数有增加或者减少的广播来获取当前被选择数
     */
    public class LocalSelectionCountReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null) {
                if (action.equals("com.example.davinci.ADD_SELECTION")) {
                    mSelectionCount++;
                } else if (action.equals("com.example.davinci.REDUCE_SELECTION")) {
                    mSelectionCount--;
                }
                changeTextViewCount(GalleryMainActivity.this);
            }
        }
    }
}
