package com.example.hrv.smarthrv;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class CircleProgressView extends View {
	//���
    private int mMaxProgress = 100;
    //��С
    private int mProgress = 30;
    //���ο��
    private final int mCircleLineStrokeWidth = 8;
    //tex ���

    // ��Բ���ڵľ�������
    private final RectF mRectF;
    //�滭
    private final Paint mPaint;

    @SuppressWarnings("unused")
	private final Context mContext;

    private String mTxtHint1;

    private String mTxtHint2;

    public CircleProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mContext = context;
        mRectF = new RectF();
        mPaint = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = this.getWidth();
        int height = this.getHeight();

        if (width != height) {
            int min = Math.min(width, height);
            width = min;
            height = min;
        }

        // ���û����������
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.rgb(0xe9, 0xe9, 0xe9));
        canvas.drawColor(Color.TRANSPARENT);
        mPaint.setStrokeWidth(mCircleLineStrokeWidth);
        mPaint.setStyle(Style.STROKE);
        // λ��
        mRectF.left = mCircleLineStrokeWidth / 2; // ���Ͻ�x
        mRectF.top = mCircleLineStrokeWidth / 2; // ���Ͻ�y
        mRectF.right = width - mCircleLineStrokeWidth / 2; // ���½�x
        mRectF.bottom = height - mCircleLineStrokeWidth / 2; // ���½�y

        // ����ԲȦ������������
        canvas.drawArc(mRectF, -90, 360, false, mPaint);
        mPaint.setColor(Color.rgb(0xf8, 0x60, 0x30));
        canvas.drawArc(mRectF, -90, ((float) mProgress / mMaxProgress) * 360, false, mPaint);

        
    }

    public int getMaxProgress() {
        return mMaxProgress;
    }

    public void setMaxProgress(int maxProgress) {
        this.mMaxProgress = maxProgress;
    }

    public void setProgress(int progress) {
        this.mProgress = progress;
        this.invalidate();
    }

    public void setProgressNotInUiThread(int progress) {
        this.mProgress = progress;
        this.postInvalidate();
    }

    public String getmTxtHint1() {
        return mTxtHint1;
    }

    public void setmTxtHint1(String mTxtHint1) {
        this.mTxtHint1 = mTxtHint1;
    }

    public String getmTxtHint2() {
        return mTxtHint2;
    }

    public void setmTxtHint2(String mTxtHint2) {
        this.mTxtHint2 = mTxtHint2;
    }
}
