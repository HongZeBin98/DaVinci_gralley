package com.example.davinci.model;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.example.davinci.bean.FolderBean;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PictureModel {

    public interface ScanPictureCallBack{
        void onFinish(File currentDir, int maxCount, List<FolderBean> folderBeanList);
    }

    public static void scanPicture(final Context context, final ScanPictureCallBack callback){
        new Thread() {
            @Override
            public void run() {
                List<FolderBean> folderBeanList = new ArrayList<>();
                int maxCount = 0;
                File currentDir =null;
                Uri mImgUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ContentResolver cr = context.getContentResolver();
                String queryArgs = MediaStore.Images.Media.MIME_TYPE + " = ? or " + MediaStore.Images.Media.MIME_TYPE + " = ?";
                Cursor cursor = cr.query(mImgUri, null, queryArgs, new String[]{"image/jpeg", "image/png"}, MediaStore.Images.Media.DATE_MODIFIED);
                Set<String> mDirPaths = new HashSet<>();

                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                        File parentFile = new File(path).getParentFile();
                        if (parentFile == null) {
                            continue;
                        }
                        String dirPath = parentFile.getAbsolutePath();
                        FolderBean folderBean;

                        if (mDirPaths.contains(dirPath)) {
                            continue;
                        } else {
                            mDirPaths.add(dirPath);
                            folderBean = new FolderBean();
                            folderBean.setDir(dirPath);
                            folderBean.setFirstImgPath(path);
                        }
                        if (parentFile.list() == null) {
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
                        folderBeanList.add(folderBean);

                        if (picSize > maxCount) {
                            currentDir = parentFile;
                            maxCount = picSize;
                        }
                    }
                    cursor.close();
                }
                callback.onFinish(currentDir, maxCount, folderBeanList);
                //通知Handler扫描图片完成
//                mAdapterHandler.sendEmptyMessage(SCAN_FINISH);
            }
        }.start();
    }
}
