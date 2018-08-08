package com.example.davinci.ui;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;

import static com.example.davinci.util.Constants.MODE_FLING;
import static com.example.davinci.util.Constants.MODE_FREE;
import static com.example.davinci.util.Constants.MODE_SCALE;

/**
 * 自定义ImageView使该ImageView能够自动把整个图片放在正中央，还能进行一些手势动作
 * Created By Mr.Bean
 */
public class MagicalImageView extends android.support.v7.widget.AppCompatImageView implements ViewTreeObserver.OnGlobalLayoutListener, View.OnTouchListener {



    private Bitmap mBitmap;
    //设置初始加载图片只获取一次
    private boolean mOnce = false;
    // 最大缩放比
    private float mMaxScaleValue = 4F;
    // 上一次移动的点(双指下是中点)
    private PointF mLastMovePoint = new PointF();
    // 缩放中心点
    private PointF mScaleCenter = new PointF();
    // 初始缩放比例，乘上两指距离即为要缩放的比例
    private float mScaleBase = 0;
    // 当前模式，缩放/移动/静止
    private int mCurrentMode = MODE_FREE;
    //滑动产生的惯性动画
    private FlingAnimator mFlingAnimator;
    private GestureDetector mGestureDetector;
    private Matrix mOuterMatrix = new Matrix();
    private ScaleAnimator mScaleAnimator;

    public MagicalImageView(Context context) {
        this(context, null);
    }

    public MagicalImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MagicalImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        setScaleType(ScaleType.MATRIX);
        setOnTouchListener(this);
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                if (!(mScaleAnimator != null && mScaleAnimator.isRunning())) {
                    doubleTap(e);
                }
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                //只有在单指模式结 束之后才允许执行fling
                if (mCurrentMode == MODE_FREE && !(mScaleAnimator != null && mScaleAnimator.isRunning())) {
                    fling(velocityX, velocityY);
                }
                return true;
            }
        });
    }

    /**
     * 执行惯性动画`
     * 动画在遇到不能移动就停止.
     * 动画速度衰减到很小就停止.
     * 其中参数速度单位为 像素/秒
     *
     * @param vX x方向速度
     * @param vY y方向速度
     */
    private void fling(float vX, float vY) {
        if (!isReady()) {
            return;
        }
        //清理当前可能正在执行的动画
        cancelAllAnimator();
        //创建惯性动画
        //FlingAnimator单位为 像素/帧,一秒60帧
        mFlingAnimator = new FlingAnimator(vX / 60f, vY / 60f);
        mFlingAnimator.start();
    }

    /**
     *  解决与viewpager滑动冲突
     * @param direction 方向（横向）
     * @return 是否可以滑动
     */
    @Override
    public boolean canScrollHorizontally(int direction) {
        if (mCurrentMode == MODE_SCALE) {
            // 如果是缩放模式，可以
            return true;
        }
        RectF bound = getCurrentRect();
        if (bound == null) {
            return false;
        }
        if (bound.isEmpty()) {
            return false;
        }
        if (direction > 0) {
            // 如果方向为左且右边没有到边界,则可以
            return bound.right > getWidth();
        } else {
            // 如果方向为右且左边没有到边界,则可以
            // 如果方向为右且左边没有到边界,则可以
            return bound.left < 0;
        }
    }

    /**
     * 获取带有缩放后尺寸信息的RectF
     *
     * @return 缩放后的尺寸信息
     */
    private RectF getCurrentRect() {
        RectF rectF = new RectF();
        if (isReady()) {
            // 获取当前缩放后的matrix
            Matrix matrix = getCurrentMatrix();
            // 应用于RectF后，得到当前的矩阵
            rectF.set(0, 0, getDrawable().getIntrinsicWidth(), getDrawable().getIntrinsicHeight());
            matrix.mapRect(rectF);
            return rectF;
        } else {
            return rectF;
        }
    }


    /**
     * 当双击后进行的处理
     *
     * @param e 动作事件，通过该变量可以获得点击的坐标
     */
    private void doubleTap(MotionEvent e) {
        if (!isReady()) {
            return;
        }
        //获取点击的坐标
        float doubleX = e.getX();
        float doubleY = e.getY();
        // 控件的大小
        float width = getWidth();
        float height = getHeight();
        //获取第一层变换矩阵
        Matrix innerMatrix = getInnerMatrix();
        //当前总的缩放比例
        float innerScale = calculateMatrixScale(innerMatrix);
        float outerScale = calculateMatrixScale(mOuterMatrix);
        //当前缩放比例
        float currentScale = innerScale * outerScale;
        //开始计算缩放动画的结果矩阵
        Matrix animEnd = new Matrix(mOuterMatrix);
        //将要放大的大小
        float nextScale = calculateNextScale(innerScale, outerScale);
        //缩放
        animEnd.postScale(nextScale / currentScale, nextScale / currentScale, doubleX, doubleY);
        // 将图片平移到中心
        animEnd.postTranslate(width / 2F - doubleX, height / 2F - doubleY);
        // 结合缩放
        Matrix finalMatrix = new Matrix(innerMatrix);
        finalMatrix.postConcat(animEnd);
        // 获取边界
        RectF bound = new RectF(0, 0, getDrawable().getIntrinsicWidth(), getDrawable().getIntrinsicHeight());
        finalMatrix.mapRect(bound);
        // 修正位置
        float postX = 0;
        float postY = 0;
        if (bound.right - bound.left < width) {
            postX = width / 2f - (bound.right + bound.left) / 2f;
        } else if (bound.left > 0) {
            postX = -bound.left;
        } else if (bound.right < width) {
            postX = width - bound.right;
        }
        if (bound.bottom - bound.top < height) {
            postY = height / 2f - (bound.bottom + bound.top) / 2f;
        } else if (bound.top > 0) {
            postY = -bound.top;
        } else if (bound.bottom < height) {
            postY = height - bound.bottom;
        }
        // 修正
        animEnd.postTranslate(postX, postY);
        // 结束动画
        cancelAllAnimator();
        //启动矩阵动画
        mScaleAnimator = new ScaleAnimator(mOuterMatrix, animEnd);
        mScaleAnimator.start();
    }

    /**
     * 获取图片总变换矩阵.
     * <p>
     * 总变换矩阵为内部变换矩阵x外部变换矩阵,决定了原图到所见最终状态的变换
     * 当尚未布局或者原图不存在时,其值无意义.所以在调用前需要确保前置条件有效,否则将影响计算结果.
     *
     * @return 如果传了matrix参数则将matrix填充后返回, 否则new一个填充返回
     */
    public Matrix getCurrentMatrix() {
        //获取内部变换矩阵
        Matrix matrix = getInnerMatrix();
        //乘上外部变换矩阵
        matrix.postConcat(mOuterMatrix);
        return matrix;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //在绘制前设置变换矩阵
        if (isReady()) {
            setImageMatrix(getCurrentMatrix());
        }
        super.onDraw(canvas);
    }

    /**
     * 获取内部变换矩阵.
     * 内部变换矩阵是原图到fit center状态的变换,当原图尺寸变化或者控件大小变化都会发生改变
     * 当尚未布局或者原图不存在时,其值无意义.所以在调用前需要确保前置条件有效,否则将影响计算结果.
     *
     * @return 如果传了matrix参数则将matrix填充后返回, 否则new一个填充返回
     */
    public Matrix getInnerMatrix() {
        Matrix matrix = new Matrix();
        //原图大小
        RectF tempSrc = new RectF(0, 0, mBitmap.getWidth(), mBitmap.getHeight());
        //控件大小
        RectF tempDst = new RectF(0, 0, getWidth(), getHeight());
        //计算fit center矩阵
        matrix.setRectToRect(tempSrc, tempDst, Matrix.ScaleToFit.CENTER);
        return matrix;
    }


    /**
     * 判断当前情况是否能执行手势相关计算
     * 包括:是否有图片,图片是否有尺寸,控件是否有尺寸.
     *
     * @return 是否能执行手势相关计算
     */
    private boolean isReady() {
        return getDrawable() != null && getDrawable().getIntrinsicWidth() > 0 && getDrawable().getIntrinsicHeight() > 0
                && getWidth() > 0 && getHeight() > 0;
    }

    /**
     * 该方法在有效调用requestLayout方法后会调用，注意：该方法可能会被多次调用因此需要设置判断只用执行一次
     */
    @Override
    public void onGlobalLayout() {
        if (!mOnce) {
            if (mBitmap == null) {
                return;
            }
            Matrix matrix = getInnerMatrix();
            setImageMatrix(matrix);
            mOnce = true;
        }
    }

    /**
     * ImageView出现的时候调用这个方法
     */
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        //注册OnGlobalLayoutListener
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    /**
     * ImageView从屏幕上消失的时候调用这个方法
     */
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        //注销OnGlobalLayoutListener
        getViewTreeObserver().removeOnGlobalLayoutListener(this);
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        mBitmap = bm;
    }

    /**
     * 在onTouch()方法中，我们调用GestureDetector的onTouchEvent()方法，将捕捉到的MotionEvent交给GestureDetector
     * ，来分析是否有合适的callback函数来处理用户的手势
     *
     * @param view  view
     * @param event 触摸事件
     * @return 返回值为true，表示这个touch事件被onTouch方法处理完毕，不会把touch事件再传递给Activity，
     * 也就是说onTouchEvent方法不会被调用。返回值是false，表示这个touch事件没有被vew完全处理，onTouch返回
     * 以后，touch事件被传递给Activity，onTouchEvent方法被调用。
     */
    @Override
    public boolean onTouch(View view, MotionEvent event) {
        // 处理多点触控事件
        int action = event.getAction() & MotionEvent.ACTION_MASK;

        if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
            //如果之前是缩放模式,还需要触发一下缩放结束动画
            if (mCurrentMode == MODE_SCALE) {
                scaleEnd();
            }
            mCurrentMode = MODE_FREE;
        } else if (action == MotionEvent.ACTION_POINTER_UP) {
            //多个手指情况下抬起一个手指,此时需要是缩放模式才触发
            if (mCurrentMode == MODE_SCALE) {
                //抬起的点如果大于2，那么缩放模式还有效，但是有可能初始点变了，重新测量初始点
                if (event.getPointerCount() > 2) {
                    //如果还没结束缩放模式，但是第一个点抬起了，那么让第二个点和第三个点作为缩放控制点
                    if (event.getAction() >> 8 == 0) {
                        saveScaleStatus(new PointF(event.getX(1), event.getY(1)),
                                new PointF(event.getX(2), event.getY(2)));
                        //如果还没结束缩放模式，但是第二个点抬起了，那么让第一个点和第三个点作为缩放控制点
                    } else if (event.getAction() >> 8 == 1) {
                        saveScaleStatus(new PointF(event.getX(0), event.getY(0)),
                                new PointF(event.getX(2), event.getY(2)));
                    }
                }
                //如果抬起的点等于2,那么此时只剩下一个点,也不允许进入单指模式,因为此时可能图片没有在正确的位置上
            }
            //第一个点按下，开启滚动模式，记录开始滚动的点
        } else if (action == MotionEvent.ACTION_DOWN) {
            //在矩阵动画过程中不允许启动滚动模式
            if (!(mScaleAnimator != null && mScaleAnimator.isRunning())) {
                //停止所有动画
                cancelAllAnimator();
                //切换到滚动模式
                mCurrentMode = MODE_FLING;
                //保存触发点用于move计算差值
                mLastMovePoint.set(event.getX(), event.getY());
            }
            //非第一个点按下，关闭滚动模式，开启缩放模式，记录缩放模式的一些初始数据
        } else if (action == MotionEvent.ACTION_POINTER_DOWN) {
            //停止所有动画
            cancelAllAnimator();
            //切换到缩放模式
            mCurrentMode = MODE_SCALE;
            //保存缩放的两个手指
            saveScaleStatus(new PointF(event.getX(0), event.getY(0)),
                    new PointF(event.getX(1), event.getY(1)));
        } else if (action == MotionEvent.ACTION_MOVE) {
            if (!(mScaleAnimator != null && mScaleAnimator.isRunning())) {
                //在滚动模式下移动
                if (mCurrentMode == MODE_FLING) {
                    //每次移动产生一个差值累积到图片位置上
                    scrollBy(event.getX() - mLastMovePoint.x, event.getY() - mLastMovePoint.y);
                    //记录新的移动点
                    mLastMovePoint.set(event.getX(), event.getY());
                    //在缩放模式下移动
                } else if (mCurrentMode == MODE_SCALE && event.getPointerCount() > 1) {
                    //两个缩放点间的距离
                    float distance = calculateDistance(new PointF(event.getX(0), event.getY(0)),
                            new PointF(event.getX(1), event.getY(1)));;
                    //保存缩放点中点
                    mLastMovePoint = calculateCenterPoint(new PointF(event.getX(0), event.getY(0)),
                            new PointF(event.getX(1), event.getY(1)));
                    //处理缩放
                    scale(mScaleCenter, mScaleBase, distance, mLastMovePoint);
                }
            }
        }

        //无论如何都处理各种外部手势
        mGestureDetector.onTouchEvent(event);
        return true;
    }

    /**
     * 让图片移动一段距离
     * 不能移动超过可移动范围,超过了就到可移动范围边界为止.
     *
     * @param xDiff 移动距离
     * @param yDiff 移动距离
     * @return 是否改变了位置
     */
    private boolean scrollBy(float xDiff, float yDiff) {
        if (!isReady()) {
            return false;
        }
        //原图方框
        RectF imageBound = getImageBound();
        //控件大小
        float displayWidth = getWidth();
        float displayHeight = getHeight();
        //如果当前图片宽度小于控件宽度，则无需移动
        if (imageBound.right - imageBound.left < displayWidth) {
            xDiff = 0;
            //如果图片左边在移动后超出控件左边
        } else if (imageBound.left + xDiff > 0) {
            //如果在移动之前是没超出的，计算应该移动的距离
            if (imageBound.left < 0) {
                xDiff = -imageBound.left;
                //否则无法移动
            } else {
                xDiff = 0;
            }
            //如果图片右边在移动后超出控件右边
        } else if (imageBound.right + xDiff < displayWidth) {
            //如果在移动之前是没超出的，计算应该移动的距离
            if (imageBound.right > displayWidth) {
                xDiff = displayWidth - imageBound.right;
                //否则无法移动
            } else {
                xDiff = 0;
            }
        }
        //以下同理
        if (imageBound.bottom - imageBound.top < displayHeight) {
            yDiff = 0;
        } else if (imageBound.top + yDiff > 0) {
            if (imageBound.top < 0) {
                yDiff = -imageBound.top;
            } else {
                yDiff = 0;
            }
        } else if (imageBound.bottom + yDiff < displayHeight) {
            if (imageBound.bottom > displayHeight) {
                yDiff = displayHeight - imageBound.bottom;
            } else {
                yDiff = 0;
            }
        }
        //应用移动变换
        mOuterMatrix.postTranslate(xDiff, yDiff);
        //触发重绘
        invalidate();
        //检查是否有变化
        return xDiff != 0 || yDiff != 0;
    }

    /**
     * 当缩放操作结束动画
     * <p>
     * 如果图片超过边界,找到最近的位置动画恢复.
     * 如果图片缩放尺寸超过最大值或者最小值,找到最近的值动画恢复.
     */
    private void scaleEnd() {
        // 是否改变位置
        boolean changed = false;
        // 获取当前变换矩阵
        Matrix currentMatrix = getCurrentMatrix();
        // 计算当前缩放比例
        float currentScale = calculateMatrixScale(currentMatrix);
        // 计算外部矩阵缩放比例
        float outerScale = calculateMatrixScale(mOuterMatrix);
        // 控件的大小
        float width = getWidth();
        float height = getHeight();
        // 最大缩放比例
        float maxScale = mMaxScaleValue;
        // 修正比例
        float postScale = 1F;
        // 修正位置
        float postX = 0F;
        float postY = 0F;
        // 如果比例大于最大比例，缩放修正
        if (currentScale > maxScale) {
            postScale = maxScale / currentScale;
        }
        // 如果修正导致了图片未填满，重新修正
        if (outerScale * postScale < 1.0F) {
            postScale = 1.0F / outerScale;
        }
        // 缩放修正不为1，则进行了修正
        if (postScale != 1.0F) {
            changed = true;
        }
        // 进行缩放修正
        Matrix postMatrix = new Matrix(currentMatrix);
        // 以移动中心缩放
        postMatrix.postScale(postScale, postScale, mLastMovePoint.x, mLastMovePoint.y);
        // 获取缩放后rect
        RectF rect = new RectF(0, 0, getDrawable().getIntrinsicWidth(), getDrawable().getIntrinsicHeight());
        postMatrix.mapRect(rect);
        // 如果出界，进行位置修正
        if (rect.right - rect.left < width) {
            postX = width / 2.0F - (rect.right + rect.left) / 2.0F;
        } else if (rect.left > 0) {
            postX = -rect.left;
        } else if (rect.right < width) {
            postX = width - rect.right;
        }
        if (rect.bottom - rect.top < height) {
            postY = height / 2.0F - (rect.bottom + rect.top) / 2.0F;
        } else if (rect.top > 0) {
            postY = -rect.top;
        } else if (rect.bottom < height) {
            postY = height - rect.bottom;
        }
        // 如果位置修正不为0，说明进行了修正
        if (postX != 0 || postY != 0) {
            changed = true;
        }
        if (changed) {
            // 改变了，则执行修正动画
            // 计算结束后的矩阵
            Matrix finalMatrix = new Matrix(mOuterMatrix);
            finalMatrix.postScale(postScale, postScale, mLastMovePoint.x, mLastMovePoint.y);
            finalMatrix.postTranslate(postX, postY);
            // 结束当前正在执行的动画
            cancelAllAnimator();
            // 执行矩阵动画
            mScaleAnimator = new ScaleAnimator(mOuterMatrix, finalMatrix);
            mScaleAnimator.start();
        }
    }

    /**
     * 停止所有手势动画
     */
    private void cancelAllAnimator() {
        if (mScaleAnimator != null) {
            mScaleAnimator.cancel();
            mScaleAnimator = null;
        }
        if (mFlingAnimator != null) {
            mFlingAnimator.cancel();
            mFlingAnimator = null;
        }
    }

    /**
     * 获取带有缩放后尺寸信息的RectF
     *
     * @return 缩放后的尺寸信息
     */
    private RectF getImageBound() {
        RectF rectF = new RectF();
        if (!isReady()) {
            return rectF;
        } else {
            //获取当前总变换矩阵
            Matrix matrix = getCurrentMatrix();
            //对原图矩形进行变换得到当前显示矩形
            rectF.set(0, 0, getDrawable().getIntrinsicWidth(), getDrawable().getIntrinsicHeight());
            matrix.mapRect(rectF);
            return rectF;
        }
    }

    /**
     * 计算矩阵的缩放比例
     */
    private float calculateMatrixScale(Matrix matrix) {
        float[] value = new float[9];
        matrix.getValues(value);
        return value[0];
    }

    /**
     * 计算两点之间的距离
     *
     * @param point1 点1
     * @param point2 点2
     * @return 两点的距离
     */
    private float calculateDistance(PointF point1, PointF point2) {
        float dx = point1.x - point2.x;
        float dy = point1.y - point2.y;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * 下一个缩放比例
     *
     * @param innerScale 内部缩放率
     * @param outerScale 外部缩放率
     * @return 下一个缩放率
     */
    private float calculateNextScale(float innerScale, float outerScale) {
        float tempScale = innerScale * outerScale;
        if (tempScale < mMaxScaleValue / 2) {
            // 如果是需要放大，返回缩放后大小
            return mMaxScaleValue / 2;
        } else {
            // 如果是需要缩小,返回初始大小
            return innerScale;
        }
    }

    /**
     * 计算中心点
     *
     * @param point1 点1
     * @param point2 点2
     * @return 包含中心点信息的PointF
     */
    private PointF calculateCenterPoint(PointF point1, PointF point2) {
        return new PointF((point1.x + point2.x) / 2.0F, (point1.y + point2.y) / 2.0F);
    }

    /**
     * 保存缩放状态，以便超界时恢复
     */
    private void saveScaleStatus(PointF point1, PointF point2) {
        // 根据图像变换部分知识，矩阵的左上和左中的值决定了缩放的x和y方向
        // 由于我们是等比缩放，所以只用获取一个方向即可
        // 保存基础缩放比例
        mScaleBase = calculateMatrixScale(mOuterMatrix) / calculateDistance(point1, point2);
        // 保存不缩放状态下的缩放中心点
        mScaleCenter = inverseMatrixPoint(calculateCenterPoint(point1, point2), mOuterMatrix);
    }


    /**
     * 对图片进行缩放
     *
     * @param scaleCenter    缩放中心
     * @param scaleBaseValue 缩放初始值
     * @param distance       两指距离
     * @param lineCenter     两指中点
     */
    private void scale(PointF scaleCenter, float scaleBaseValue, float distance, PointF lineCenter) {
        if (!isReady()) {
            return;
        }
        // 计算图片从不缩放的状态到目标状态的缩放比例
        float scale = scaleBaseValue * distance;
        Matrix matrix = new Matrix();
        // 从缩放中心缩放s
        matrix.postScale(scale, scale, scaleCenter.x, scaleCenter.y);
        // 跟随手指中心移动
        matrix.postTranslate(lineCenter.x - scaleCenter.x, lineCenter.y - scaleCenter.y);
        // 为外部矩阵应用变换
        mOuterMatrix.set(matrix);
        // 根据外部矩阵的值重绘
        invalidate();
    }

    /**
     * 计算映射到原来状态的中点(不做放大缩小变换的)
     */
    private PointF inverseMatrixPoint(PointF pointF, Matrix outerMatrix) {
        // 计算逆矩阵
        Matrix inverseMatrix = new Matrix();
        // invert方法:求矩阵的逆矩阵，简而言之就是计算与之前相反的矩阵
        // 如果之前是平移200px，则求的矩阵为反向平移200px
        // 如果之前是缩小到0.5f，则结果是放大到2倍
        outerMatrix.invert(inverseMatrix);
        float[] srcPoint = {pointF.x, pointF.y};
        float[] dstPoint = new float[2];
        // mapPoints方法：计算一组点基于当前Matrix变换后的位置
        inverseMatrix.mapPoints(dstPoint, srcPoint);
        return new PointF(dstPoint[0], dstPoint[1]);
    }



    /**
     * 缩放动画
     */
    class ScaleAnimator extends ValueAnimator implements ValueAnimator.AnimatorUpdateListener {
        // 初始矩阵
        private float[] mStartMatrix = new float[9];
        // 结束矩阵
        private float[] mEndMatrix = new float[9];

        ScaleAnimator(Matrix srcMatrix, Matrix dstMatrix) {
            super();
            // 设置数值更新事件
            addUpdateListener(this);
            //设置动画进度变化范围
            setFloatValues(0, 1.0F);
            //设置动画时间
            setDuration(500);
            // 起始矩阵值数组
            srcMatrix.getValues(mStartMatrix);
            // 结束矩阵值数组
            dstMatrix.getValues(mEndMatrix);
        }

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            // 当前动画进度
            float currentValue = (float) animation.getAnimatedValue();
            //中间结果矩阵
            float[] resultMatrix = new float[9];
            // 插值动画计算出减速效果的算法
            for (int i = 0; i < 9; i++) {
                // 改变矩阵的每一个值
                resultMatrix[i] = mStartMatrix[i] + (mEndMatrix[i] - mStartMatrix[i]) * currentValue;
            }
            // 为外部矩阵设定数值
            mOuterMatrix.setValues(resultMatrix);
            // 根据外部矩阵数值重绘
            invalidate();
        }
    }

    /**
     * 滑动惯性动画
     */
    class FlingAnimator extends ValueAnimator implements ValueAnimator.AnimatorUpdateListener {

        private float[] mVector;    // 速度向量

        FlingAnimator(float vectorX, float vectorY) {
            super();
            setFloatValues(0, 1f);
            setDuration(1000000);
            addUpdateListener(this);
            mVector = new float[]{vectorX, vectorY};
        }

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            // 移动图像并给出结果
            boolean result = scrollBy(mVector[0], mVector[1]);
            // 每次衰减9/10
            mVector[0] *= 0.9F;
            mVector[1] *= 0.9F;
            //速度太小或者不能移动了则结束
            if (!result || calculateDistance(new PointF(0, 0), new PointF(mVector[0], mVector[1])) < 1.0F) {
                animation.cancel();
            }
        }
    }
}
