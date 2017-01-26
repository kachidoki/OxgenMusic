package com.kachidoki.oxgenmusic.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.kachidoki.oxgenmusic.R;

/**
 * Created by mayiwei on 16/11/23.
 */
public class CDview extends View {
    private static final int MSG_RUN = 0x00000100;
    private static final int TIME_UPDATE = 50;

//    private Bitmap mCircleBitmap;
    private Bitmap mClipBitmap; // cd图片

    private float mRotation = 0.0f;

    private Matrix mMatrix;

    private volatile boolean isRunning;

    public CDview(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CDview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
//        mCircleBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.cd);
        mMatrix = new Matrix();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        isRunning = false;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mClipBitmap == null) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }

        int width = 0;
        int height = 0;

        // mode of width
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        // mode of height
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        // size of width
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        // size of height
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        // parent assign the size
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            // child compute the size of itself
            width = mClipBitmap.getWidth();
            // parent assign the max size, child should <= widthSize
            if (widthMode == MeasureSpec.AT_MOST) {
                width = Math.min(width, widthSize);
            }
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            height = mClipBitmap.getHeight();
            if (heightMode == MeasureSpec.AT_MOST) {
                height = Math.min(height, heightSize);
            }
        }
        setMeasuredDimension(width, height);
//        mCircleBitmap = resizeBitmap(width/10,mCircleBitmap);
        mClipBitmap = resizeBitmap(width,mClipBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mClipBitmap == null)
            return;
        canvas.save();
        mMatrix.setRotate(mRotation, getMeasuredWidth() / 2, getMeasuredHeight() / 2);
        mMatrix.preTranslate((getMeasuredWidth() - mClipBitmap.getWidth()) / 2,(getMeasuredWidth() - mClipBitmap.getWidth()) / 2);

        canvas.drawBitmap(mClipBitmap, mMatrix, null);
//        canvas.drawBitmap(mCircleBitmap, (getMeasuredWidth() - mCircleBitmap.getWidth()) / 2, (getMeasuredHeight() - mCircleBitmap.getHeight()) / 2, null);
        canvas.restore();
    }

    /**
     * 创建圆形剪切图
     * @param src
     * @return
     */
    private Bitmap createCircleBitmap(Bitmap src) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setARGB(255, 241, 239, 229);

        Bitmap target = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(target);

        canvas.drawCircle(getMeasuredWidth() / 2, getMeasuredWidth() / 2, getMeasuredWidth() / 2, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        canvas.drawBitmap(src, 0, 0, paint);

        return target;
    }


    private Bitmap resizeBitmap(int dstWidth, Bitmap srcBitmap) {
//        缩放系数
        float scale = 1.0f * dstWidth / srcBitmap.getWidth();

//        缩放矩阵
        Matrix matrix = new Matrix();
        matrix.setScale(scale, scale);
        return Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(), srcBitmap.getHeight(), matrix, true);
    }



    /**
     * 设置cd图片
     * @param bmp
     */
    public void setImage(Bitmap bmp) {

        int widthSize = bmp.getWidth();
        int heightSize = bmp.getHeight();
        int widthSpec = MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY);
        int heightSpec = MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY);

        measure(widthSpec, heightSpec);
        mClipBitmap = createCircleBitmap(bmp);
        requestLayout();
        invalidate();
    }

    /**
     * 开始旋转
     */
    public void start() {
        if (isRunning)
            return;
        isRunning = true;
        mHandler.sendEmptyMessageDelayed(MSG_RUN, TIME_UPDATE);
    }

    /**
     * 暂停旋转
     */
    public void pause() {
        if (!isRunning)
            return;
        isRunning = false;
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == MSG_RUN) {
                if (isRunning) {
                    mRotation += 0.3f;
                    if (mRotation >= 360)
                        mRotation = 0;
                    invalidate();
                    sendEmptyMessageDelayed(MSG_RUN, TIME_UPDATE);
                }
            }
        }
    };
}
