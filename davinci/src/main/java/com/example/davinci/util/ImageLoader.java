package com.example.davinci.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.widget.ImageView;

import com.example.davinci.bean.ImageBeanHolder;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import static com.example.davinci.util.Constants.CPU_CORE_NUMBER;
import static com.example.davinci.util.Constants.EXECUTE_TASK;

/**
 * 图片加载类
 * Created by Mr.Bean
 */
public class ImageLoader {

    private static ImageLoader mInstance;
    private PictureLruCache mPictureLruCache;
    private ExecutorService mThreadPool;
    private Type mType = Type.LIFO;
    private volatile List<Runnable> mTaskQueue;
    private Semaphore mTaskSemaphore;
    private PictureHandler mPictureHandler;
    private ThreadHandler mThreadHandler;

    /**
     * 枚举类型设置滚动载入的方式
     */
    public enum Type {
        FIFO, LIFO
    }

    /**
     * 私有化构造器，使外界无法访问，运用单例
     */
    private ImageLoader(Type type) {
        init(type);
    }


    /**
     * 单例模式获取ImageLoader实例
     *
     * @return 获取到的ImageLoader实例
     */
    public static ImageLoader getInstance(Type type) {
        if (mInstance == null) {
            synchronized (ImageLoader.class) {
                if (mInstance == null) {
                    mInstance = new ImageLoader(type);
                }
            }
        }
        return mInstance;
    }

    /**
     * 初始化变量
     *
     * @param type 滚动获取类型
     */
    private void init(Type type) {
        //初始化后台轮循线程
        HandlerThread handlerThread = new HandlerThread("wheel");
        //启动轮循线程
        handlerThread.start();
        //初始化后台的handler
        mThreadHandler = new ThreadHandler(handlerThread.getLooper(), this);
        //创建线程池
        mThreadPool = Executors.newFixedThreadPool(CPU_CORE_NUMBER + 1);
        //创建任务队列
        mTaskQueue = Collections.synchronizedList(new LinkedList<Runnable>());
        //初始化信号量
        mTaskSemaphore = new Semaphore(CPU_CORE_NUMBER + 1);
        //初始化滚动类型
        mType = type;
        //初始化内存容器
        mPictureLruCache = new PictureLruCache();
    }

    /**
     * 通过路径获取图片
     *
     * @param path      图片路径
     * @param imageView 放图片的容器
     */
    public void loadImage(final String path, final ImageView imageView, final boolean originalPictureSwitch) {
        //防止出现图片闪烁现象，给imageView设置一个标签
        imageView.setTag(path);
        if (mPictureHandler == null) {
            mPictureHandler = new PictureHandler(Looper.getMainLooper(), this);
        }
        if (originalPictureSwitch) {
            addTaskIntoQueue(new Runnable() {
                @Override
                public void run() {
                    Bitmap bm;
                    bm = new ImageResizer().decodeSampleBitmapFromResource(path, 1080, 1080);
                    //释放信号量
                    mTaskSemaphore.release();
                    sendUIMessage(bm, imageView, path);
                }
            });
        } else {
            //从缓存中获取图片
            Bitmap bitmap = mPictureLruCache.getBitmapFromLruCache(path);
            if (bitmap != null) {
                sendUIMessage(bitmap, imageView, path);
            } else {
                addTaskIntoQueue(new Runnable() {
                    @Override
                    public void run() {
                        Bitmap bm;
                        //获取缩略图
                        bm = new ImageResizer().decodeSampleBitmapFromResource(path, 100, 100);
                        //把缩略图放入缓存
                        mPictureLruCache.addBitmapToLruCache(path, bm);
                        //释放信号量
                        mTaskSemaphore.release();
                        sendUIMessage(bm, imageView, path);
                    }
                });
            }
        }
    }

    /**
     * 发送携带图片相关的给主线程handler
     *
     * @param bitmap    图片
     * @param imageView 图片容器
     * @param path      图片地址
     */
    private void sendUIMessage(Bitmap bitmap, ImageView imageView, String path) {
        Message message = Message.obtain();
        message.obj = new ImageBeanHolder(bitmap, imageView, path);
        mPictureHandler.sendMessage(message);
    }


    /**
     * 把任务添加到任务队列
     */
    private void addTaskIntoQueue(Runnable action) {
        mTaskQueue.add(action);
        mThreadHandler.sendEmptyMessage(EXECUTE_TASK);
    }

    /**
     * 从任务队列中获取任务
     *
     * @return 获取到的任务
     */
    private Runnable getTask() {
        if (mType == Type.LIFO) {
            return mTaskQueue.remove(mTaskQueue.size() - 1);
        } else if (mType == Type.FIFO) {
            return mTaskQueue.remove(0);
        } else {
            throw new IllegalStateException("not exact method of scheduling");
        }
    }

    /**
     * 执行加载任务
     */
    private void executeTask() {
        Runnable action = getTask();
        if (action == null) {
            return;
        }
        mThreadPool.execute(action);
        try {
            mTaskSemaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static class ThreadHandler extends Handler {
        private final WeakReference<ImageLoader> mImageLoader;

        ThreadHandler(Looper looper, ImageLoader imageLoader) {
            super(looper);
            mImageLoader = new WeakReference<>(imageLoader);
        }

        @Override
        public void handleMessage(Message msg) {
            ImageLoader imageLoader = mImageLoader.get();
            if (imageLoader == null) {
                return;
            }
            super.handleMessage(msg);
            switch (msg.what) {
                case EXECUTE_TASK:
                    //添加进队列执行加载任务
                    imageLoader.executeTask();
                    break;
            }
        }
    }
}
