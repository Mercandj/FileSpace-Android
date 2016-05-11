package com.mercandalli.android.apps.files.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.ProgressBar;

import com.mercandalli.android.apps.files.R;

import static com.mercandalli.android.library.base.view.ViewUtils.dpToPx;

public class StaticProgressBar extends ProgressBar {

    private int mMeasuredWidth;
    private int mMeasuredHeight;
    private int mCircleRadius;

    private Paint mPaint;
    private int mStrokeWidth;

    public StaticProgressBar(Context context) {
        super(context);
        init(context);
    }

    public StaticProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public StaticProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int dw = 0;
        int dh = 0;

        final Drawable d = getIndeterminateDrawable();
        if (d != null) {
            dw = d.getIntrinsicWidth();
            dh = d.getIntrinsicHeight();
        }
        dw += getPaddingLeft() + getPaddingRight();
        dh += getPaddingTop() + getPaddingBottom();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            mMeasuredWidth = resolveSizeAndState(dw, widthMeasureSpec, 0);
            mMeasuredHeight = resolveSizeAndState(dh, heightMeasureSpec, 0);
        } else {
            mMeasuredWidth = 200;
            mMeasuredHeight = 200;
        }
        setMeasuredDimension(mMeasuredWidth, mMeasuredHeight);
        mCircleRadius = Math.min(mMeasuredHeight, mMeasuredWidth) / 2 - mStrokeWidth;
    }

    @Override
    public synchronized void setIndeterminate(boolean indeterminate) {
        super.setIndeterminate(false);
        setProgress((int) (0.7f * getMax()));
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        canvas.drawCircle(mMeasuredWidth / 2, mMeasuredHeight / 2, mCircleRadius, mPaint);
    }

    private void init(final Context context) {
        mStrokeWidth = (int) dpToPx(context, 4);
        mPaint = new Paint();
        mPaint.setColor(ContextCompat.getColor(context, R.color.accent));
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mStrokeWidth);
    }
}
