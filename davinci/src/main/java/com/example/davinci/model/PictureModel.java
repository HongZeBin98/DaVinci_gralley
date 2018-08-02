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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PictureModel {

    public interface ScanPictureCallBack {
        void onFinish(List<FolderBean> folderBeanList, List<String> allPictureList);
    }

    public static void scanPicture(final Context context, final ScanPictureCallBack callback) {
        new Thread() {
            @Override
            public void run() {
                List<FolderBean> folderBeanList = new ArrayList<>();
                List<String> allPicturePath = new ArrayList<>();
                //使用ContentProvide获取所有图片的路径
                Uri mImgUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ContentResolver cr = context.getContentResolver();
                String queryArgs = MediaStore.Images.Media.MIME_TYPE + " = ? or " + MediaStore.Images.Media.MIME_TYPE + " = ?";
                Cursor cursor = cr.query(mImgUri, null, queryArgs, new String[]{"image/jpeg", "image/png"}, MediaStore.Images.Media.DATE_MODIFIED);
                Set<String> mDirPaths = new HashSet<>();
                FolderBean folderBeanForAllPicture = new FolderBean();
                folderBeanForAllPicture.setName();
                //确保所有图片的文件夹的第一张图片为最新的
                boolean flag = true;
                if (cursor != null) {
                    cursor.moveToLast();
                    do {
                        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                        if (flag) {
                            folderBeanForAllPicture.setFirstImgPath(path);
                            flag = false;
                        }
                        allPicturePath.add(path);
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
                    } while (cursor.moveToPrevious());
                    cursor.close();
                }
                int allPictureCount = 0;
                for (FolderBean x : folderBeanList) {
                    allPictureCount += x.getCount();
                }
                folderBeanForAllPicture.setCount(allPictureCount);
                folderBeanList.add(folderBeanForAllPicture);
                Collections.reverse(folderBeanList);
                callback.onFinish(folderBeanList, allPicturePath);
            }
        }.start();
    }
}
