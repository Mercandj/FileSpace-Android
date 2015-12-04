package com.mercandalli.android.apps.files.common.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Jonathan on 18/09/2015.
 */
public class NonSwipeableViewPager extends ViewPager {

    private int[] nonSwipeableItem;

    public NonSwipeableViewPager(Context context) {
        super(context);
    }

    public NonSwipeableViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        // Never allow swiping to switch between pages, return false
        if (nonSwipeableItem == null)
            return super.onInterceptTouchEvent(event);
        for (Integer i : nonSwipeableItem) {
            if (i == getCurrentItem())
                return false;
        }
        return super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Never allow swiping to switch between pages, return false
        if (nonSwipeableItem == null)
            return super.onTouchEvent(event);
        for (Integer i : nonSwipeableItem) {
            if (i == getCurrentItem())
                return false;
        }
        return super.onTouchEvent(event);
    }

    public void setNonSwipeableItem(int[] nonSwipeableItem) {
        this.nonSwipeableItem = nonSwipeableItem;
    }

    public void setNonSwipeableItem(int nonSwipeableItem) {
        this.nonSwipeableItem = new int[]{nonSwipeableItem};
    }
}
