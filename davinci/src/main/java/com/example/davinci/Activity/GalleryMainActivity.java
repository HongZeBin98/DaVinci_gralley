package com.example.davinci.Activity;

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
import android.widget.Toast;

import com.example.davinci.Adapter.PictureListAdapter;
import com.example.davinci.Bean.FolderBean;
import com.example.davinci.R;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.example.davinci.Util.Constants.SCAN_FINISH;

/**
 * 图库的主界面
 * Created By Mr.Bean
 */
public class GalleryMainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
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
    }

    /**
     * 配置recyclerVew
     */
    protected void dataView(){
        List<String> img = Arrays.asList(mCurrentDir.list());
        //反转列表，使最新的图片在最上面
        Collections.reverse(img);
        //网格布局
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        PictureListAdapter pictureAdapter = new PictureListAdapter(img, mCurrentDir.getAbsolutePath());
        mRecyclerView.setAdapter(pictureAdapter);
    }

    /**
     * 实例化
     */
    private void initView(){
        mRecyclerView = findViewById(R.id.recyclerView);
    }

    /**
     * 利用ContentProvider扫描手机中所有图片
     */
    private void initData(){
        //判断储存卡是否可用
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            Toast.makeText(this, "当前储存卡不可用！", Toast.LENGTH_SHORT).show();
            return;
        }
        //启动线程扫描所有图片
        new Thread(){
            @Override
            public void run() {
                List<FolderBean> folderBeans = new ArrayList<>();
                Uri mImgUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ContentResolver cr = GalleryMainActivity.this.getContentResolver();
                String queryArgs = MediaStore.Images.Media.MIME_TYPE + " = ? or " + MediaStore.Images.Media.MIME_TYPE + " = ?";
                Cursor cursor = cr.query(mImgUri, null, queryArgs, new String[]{"image/jpeg", "image/png"}, MediaStore.Images.Media.DATE_MODIFIED);
                Set<String> mDirPaths = new HashSet<>();

                if (cursor != null){
                    while(cursor.moveToNext()){
                        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                        File parentFile = new File(path).getParentFile();
                        if (parentFile == null){
                            continue;
                        }
                        String dirPath = parentFile.getAbsolutePath();
                        FolderBean folderBean;

                        if (mDirPaths.contains(dirPath)){
                            continue;
                        }else {
                            mDirPaths.add(dirPath);
                            folderBean = new FolderBean();
                            folderBean.setDir(dirPath);
                            folderBean.setFirstImgPath(path);
                        }
                        if (parentFile.list() == null){
                            continue;
                        }
                        //对文件夹中的数据进行过滤,获取图片的数量
                        int picSize = parentFile.list(new FilenameFilter() {
                            @Override
                            public boolean accept(File file, String filename) {
                                return filename.endsWith(".jpg") || filename.endsWith(".jpeg") || filename.endsWith(".png");
                            }
                        }).length;
                        folderBean.setCount(picSize);
                        folderBeans.add(folderBean);

                        if(picSize > mMaxCount){
                            mCurrentDir = parentFile;
                            mMaxCount = picSize;
                        }
                    }
                    cursor.close();
                }
                //通知Handler扫描图片完成
                mAdapterHandler.sendEmptyMessage(SCAN_FINISH);
            }
        }.start();
    }

    private class AdapterHandler extends Handler{
        private final WeakReference<GalleryMainActivity> mMainActivity;

        AdapterHandler(Looper looper, GalleryMainActivity mainActivity) {
            super(looper);
            mMainActivity = new WeakReference<>(mainActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            GalleryMainActivity imageLoader = mMainActivity.get();
            if (imageLoader == null){
                return;
            }
            super.handleMessage(msg);
            if (msg.what == SCAN_FINISH){
                dataView();
            }
        }

    }

}
