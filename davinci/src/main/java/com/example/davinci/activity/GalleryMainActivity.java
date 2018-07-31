package com.example.davinci.activity;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.example.davinci.util.Constants.SCAN_FINISH;

/**
 * 图库的主界面
 * Created By Mr.Bean
 */
public class GalleryMainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private TextView mAlbumSelection;
    private List<FolderBean> mFolderBeans;
    private ListImageDirPopupWindow mPopupWindow;
    private File mCurrentDir;
    private AdapterHandler mAdapterHandler;
    private int mMaxCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapterHandler = new AdapterHandler(getMainLooper(), this);
        setContentView(R.layout.activity_main);
        initView();
        initData();
        initEvent();
    }

    /**
     * 配置recyclerVew
     */
    protected void dataView() {
        List<String> img = Arrays.asList(mCurrentDir.list());
        //反转列表，使最新的图片在最上面
        Collections.reverse(img);
        //网格布局
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        PictureListAdapter pictureAdapter = new PictureListAdapter(img, mCurrentDir.getAbsolutePath(), this);
        mRecyclerView.setAdapter(pictureAdapter);
    }

    protected void initPopupWindow() {
        mPopupWindow = new ListImageDirPopupWindow(this, mFolderBeans);
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                lightOn();
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
        mAlbumSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPopupWindow.showAsDropDown(mAlbumSelection, 0, 0);
                lightOff();
            }
        });
    }

    /**
     * 实例化
     */
    private void initView() {
        mRecyclerView = findViewById(R.id.recyclerView);
        mAlbumSelection = findViewById(R.id.id_bottom_album_selection);
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
        PictureModel.scanPicture(this, new PictureModel.ScanPictureCallBack() {
            @Override
            public void onFinish(File currentDir, int maxCount, List<FolderBean> folderBeanList) {
                mCurrentDir = currentDir;
                mMaxCount = maxCount;
                mFolderBeans = folderBeanList;
                //通知Handler扫描图片完成
                mAdapterHandler.sendEmptyMessage(SCAN_FINISH);
            }
        });
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

}
