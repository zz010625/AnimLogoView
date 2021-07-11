package com.example.animlogoview;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.annotation.Nullable;

public class AnimLogoView extends View {
    private Context mContext;
    private ValueAnimator mMoveAnimator;
    private OnAnimFinish mOnAnimFinish;
    private Paint mTextPaint;
    private Paint mPicPaint;
    private SparseArray<PointF> mQuietPoints; // 最终合成logo后的坐标
    private SparseArray<PointF> mRadonPoints;// logo被随机打散的坐标
    private PointF mPicEndCoordinate;// logo图片的最终坐标
    private PointF mPicStartCoordinate;// logo图片的起始坐标
    private int mWidth;
    private int mHeight;
    private int mCenterX;
    private int mCenterY;
    private String mLogoTexts = "Logo";
    private int mTextSize = 25;//文字大小 dp
    private char[] mTexts = mLogoTexts.toCharArray();//logo字符数组
    private float mTextWidth;
    private float mOffSet;//文字垂直偏移量
    private int mTextPadding = 0;//文字间距 px
    private Bitmap mPic;
    private int mPicPath = -1;//图片地址
    private int mPicWidth = -1;//未设置默认按照传入图片本身大小 px
    private int mPicHeight = -1;//未设置默认按照传入图片本身大小 px
    private int mPicPaddingBottom = 0;//图片与文字的上下间距 px
    private long mAnimDuration = 1500;//动画执行时间
    private long mFinishDelayed=500;//动画结束后延时时间
    private float mAnimProgress;//动画执行时的进度0->1

    public AnimLogoView(Context context) {
        this(context, null);
    }

    public AnimLogoView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AnimLogoView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        mQuietPoints = new SparseArray<>();
        mRadonPoints = new SparseArray<>();
        initPaint();
        initAnim();
    }

    private void initPaint() {
        mTextPaint = new Paint();
        mPicPaint = new Paint();
        //设置文字大小
        mTextPaint.setTextSize(dip2px(mContext, mTextSize));
        //设置图片画笔
        mPicPaint.setAntiAlias(true);
        mPicPaint.setStyle(Paint.Style.STROKE);

    }

    public void setOnAnimFinish(OnAnimFinish mOnAnimFinish) {
        this.mOnAnimFinish = mOnAnimFinish;
    }

    public void setLogoTexts(String mLogoTexts) {
        this.mLogoTexts = mLogoTexts;
        //转为字符数组
        mTexts = mLogoTexts.toCharArray();
    }

    public void setTextSize(int mTextSize) {
        this.mTextSize = mTextSize;
        //设置文字大小
        mTextPaint.setTextSize(dip2px(mContext, mTextSize));
    }
    public void setAnimDuration(long mAnimDuration) {
        this.mAnimDuration = mAnimDuration;
    }

    public void setFinishDelayed(int mFinishDelayed) {
        this.mFinishDelayed = mFinishDelayed;
    }
    public void setTextPadding(int mTextPaddingDp) {
        this.mTextPadding = dip2px(mContext, mTextPaddingDp);
    }

    public void setPicPaddingBottom(int mPicPaddingBottomDp) {
        this.mPicPaddingBottom = dip2px(mContext, mPicPaddingBottomDp);
    }

    public void setPicWidthAndHeight(int mPicWidthDp, int mPicHeightDp) {
        this.mPicWidth = dip2px(mContext, mPicWidthDp);
        this.mPicHeight = dip2px(mContext, mPicHeightDp);
        if (mPic != null) {
          changePicSize();
        }
    }

    public void setPicPath(int mPicPath) {
        this.mPicPath = mPicPath;
        initPic();
    }

    private void initPic() {
        mPic = BitmapFactory.decodeResource(getResources(), mPicPath);
        if (mPicWidth!=-1&&mPicHeight!=1){
          changePicSize();
        }
    }

    private void initTextCoordinate() {
        /**
         * 初始化字符坐标
         */
       //清除原数据
        mRadonPoints.clear();
        mQuietPoints.clear();
        //获取字符相关数据
        Rect bounds = new Rect();
        mTextPaint.getTextBounds(String.valueOf(mTexts[0]), 0, 1, bounds);
        mOffSet = (bounds.top + bounds.bottom) / 2;
        //初始化logo最终位置
        int textHalfLength = mLogoTexts.length() / 2;//字符数/2取模
        for (int i = 0; i < mLogoTexts.length(); i++) {
            PointF pointF = new PointF();
            mTextWidth = mTextPaint.measureText(String.valueOf(mTexts[i]));
            //保证所有字符排列后 居中
            if (mLogoTexts.length() % 2 == 0) {
                if (i <= textHalfLength - 1) {
                    pointF.x = mCenterX - (textHalfLength - i) * mTextWidth - (textHalfLength - i - 0.5f) * mTextPadding;
                } else {
                    pointF.x = mCenterX + (i - textHalfLength) * mTextWidth + (i - textHalfLength + 0.5f) * mTextPadding;
                }
            } else {
                if (i <= textHalfLength - 1) {
                    pointF.x = mCenterX - (textHalfLength - i + 0.5f) * mTextWidth - (textHalfLength - i) * mTextPadding;
                } else {
                    if (i == textHalfLength / 2) {
                        pointF.x = mCenterX - 0.5f * mTextWidth;
                    } else {
                        pointF.x = mCenterX + (i - textHalfLength - 0.5f) * mTextWidth + (i - textHalfLength) * mTextPadding;
                    }
                }
            }
            //设置文字的Y坐标 保证图片于文字整体垂直居中
            if (mPic != null) {
                pointF.y = mCenterY - mOffSet + mPic.getHeight() / 2f;
            } else {
                pointF.y = mCenterY - mOffSet;
            }
            mQuietPoints.put(i, pointF);
        }
        // 构建随机初始坐标
        for (int i = 0; i < mLogoTexts.length(); i++) {
            mRadonPoints.put(i, new PointF((float) Math.random() * mWidth, (float) Math.random() * mHeight));
        }

    }

    private void initPicCoordinate() {
        /**
         * 初始化图片坐标
         */
        mPicStartCoordinate = new PointF();
        mPicEndCoordinate = new PointF();
        Paint.FontMetrics forFontMetrics = mTextPaint.getFontMetrics();
        float textHeight = forFontMetrics.descent - forFontMetrics.ascent;

        mPicStartCoordinate.x = (mWidth - mPic.getWidth()) / 2;
        mPicStartCoordinate.y = mQuietPoints.get(0).y - textHeight - mPic.getHeight() - mPicPaddingBottom + 100;
        mPicEndCoordinate.x = (mWidth - mPic.getWidth()) / 2;
        mPicEndCoordinate.y = mQuietPoints.get(0).y - textHeight - mPic.getHeight() - mPicPaddingBottom;
    }

    private void initAnim() {
        mMoveAnimator = ValueAnimator.ofFloat(0, 1);
        mMoveAnimator.setDuration(mAnimDuration);
        mMoveAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        mMoveAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (mQuietPoints.size() <= 0 || mRadonPoints.size() <= 0) {
                    return;
                }
                mAnimProgress = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        mMoveAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                try {
                    Thread.sleep(mFinishDelayed);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (mOnAnimFinish != null) {
                    mOnAnimFinish.startOtherActivity();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    public void startAnim() {
        mMoveAnimator.start();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //得到View宽高
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
        //得到 X Y 中间值
        mCenterX = mWidth / 2;
        mCenterY = mHeight / 2;
        //初始化logo文字起始坐标 最终坐标(View正中间)
        initTextCoordinate();
        //初始化logo图片起始坐标 最终坐标(View正中间)
        if (mPic!=null){
            initPicCoordinate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制文字
        for (int i = 0; i < mLogoTexts.length(); i++) {
            PointF quietP = mQuietPoints.get(i);
            PointF radonP = mRadonPoints.get(i);
            float x = radonP.x + (quietP.x - radonP.x) * mAnimProgress;
            float y = radonP.y + (quietP.y - radonP.y) * mAnimProgress;
            canvas.drawText(String.valueOf(mTexts[i]), x, y, mTextPaint);
        }
        //绘制Logo图标
        if (mPic != null) {
            PointF endP = mPicEndCoordinate;
            PointF startP = mPicStartCoordinate;
            float y = startP.y + (endP.y - startP.y) * mAnimProgress;
            mPicPaint.setAlpha((int) (255*mAnimProgress));
            canvas.drawBitmap(mPic, endP.x, y, mPicPaint);
        }
    }

    //dp转px
    private int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
    //图片的大小缩放
    private void changePicSize(){
        int width = mPic.getWidth();
        int height = mPic.getHeight();
        // 设置想要的大小
        int newWidth = mPicWidth;
        int newHeight = mPicHeight;
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到缩放后的图片
        mPic = Bitmap.createBitmap(mPic, 0, 0, width, height, matrix, true);
    }
    /**
     * 对外接口 当动画结束时回调
     */
    public interface OnAnimFinish {
        void startOtherActivity();
    }
}
