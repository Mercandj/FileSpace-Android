package com.mercandalli.android.apps.files.common.animation;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

/**
 * {@link RecyclerView} adapter with animation.
 */
public class ScaleAnimationAdapter extends RecyclerView.Adapter<ViewHolder> implements Animator.AnimatorListener {

    /**
     * The {@link RecyclerView.Adapter} with animated {@link ViewHolder}.
     */
    private RecyclerView.Adapter mAdapter;

    /**
     * The {@link RecyclerView} with animated {@link ViewHolder}.
     */
    private RecyclerView mRecyclerView;

    /**
     * The animation duration for each {@link RecyclerView}.
     */
    private int mDuration = 300;

    /**
     * The animation offset duration between each {@link RecyclerView}.
     */
    private int mOffsetDuration = 100;

    /**
     * The animation {@link Interpolator}.
     */
    private Interpolator mInterpolator = new AccelerateDecelerateInterpolator();

    /**
     * The number of {@link ViewHolder} items per line.
     */
    private int mViewHolderPerLine;

    private int mLastPosition = -1;
    private boolean isFirstOnly = false;
    private int mCounter;
    private boolean mAnimsInitialized;
    private final float mFrom;

    public ScaleAnimationAdapter(RecyclerView recyclerView, RecyclerView.Adapter adapter) {
        this(recyclerView, adapter, 1, 0.0F);
    }

    public ScaleAnimationAdapter(RecyclerView recyclerView, RecyclerView.Adapter adapter, int cardPerLine) {
        this(recyclerView, adapter, cardPerLine, 0.0F);
    }

    public ScaleAnimationAdapter(RecyclerView recyclerView, RecyclerView.Adapter adapter, int cardPerLine, float from) {
        mRecyclerView = recyclerView;
        mAdapter = adapter;
        mViewHolderPerLine = cardPerLine;
        this.mFrom = from;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return this.mAdapter.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        this.mAdapter.onBindViewHolder(holder, position);
        if (this.isFirstOnly && position <= this.mLastPosition) {
            clear(holder.itemView);
        } else {
            Animator[] animators = this.getAnimators(holder.itemView);

            LinearLayoutManager layoutManager = ((LinearLayoutManager) mRecyclerView.getLayoutManager());
            int firstVisiblePosition = layoutManager.findFirstVisibleItemPosition();

            if (!mAnimsInitialized && firstVisiblePosition <= 1)
                for (Animator anim : animators) {
                    anim.setDuration((long) this.mDuration);
                    anim.setInterpolator(this.mInterpolator);
                    increaseCounter();

                    if (mViewHolderPerLine > 1 && position >= mViewHolderPerLine) {
                        anim.setStartDelay(mOffsetDuration * (position - mViewHolderPerLine / 2));
                    } else {
                        anim.setStartDelay(mOffsetDuration * position);
                    }
                    anim.addListener(this);
                    anim.start();
                }
            else {
                holder.itemView.setScaleX(1);
                holder.itemView.setScaleY(1);
            }

            this.mLastPosition = position;
        }
    }

    @Override
    public void onAnimationStart(Animator animation) {

    }

    @Override
    public void onAnimationEnd(Animator animation) {
        if (decreaseCounter())
            mAnimsInitialized = true;
    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }

    public synchronized int getCounter() {
        return mCounter;
    }

    public synchronized void increaseCounter() {
        mCounter++;
    }

    public int getItemCount() {
        return this.mAdapter.getItemCount();
    }

    public void setDuration(int duration) {
        this.mDuration = duration;
    }

    public void setOffsetDuration(int mOffsetDuration) {
        this.mOffsetDuration = mOffsetDuration;
    }

    public void setInterpolator(Interpolator interpolator) {
        this.mInterpolator = interpolator;
    }

    public void setStartPosition(int start) {
        this.mLastPosition = start;
    }

    private RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        this.mRecyclerView = recyclerView;
    }

    public int getItemViewType(int position) {
        return this.mAdapter.getItemViewType(position);
    }

    private synchronized boolean decreaseCounter() {
        mCounter--;
        return mCounter == 0;
    }

    private Animator[] getAnimators(View view) {
        view.setScaleX(0);
        view.setScaleY(0);
        return new Animator[]{ObjectAnimator.ofFloat(view, "scaleX", this.mFrom, 1.0F), ObjectAnimator.ofFloat(view, "scaleY", this.mFrom, 1.0F)};
    }

    private static void clear(View v) {
        ViewCompat.setAlpha(v, 1.0F);
        ViewCompat.setScaleY(v, 1.0F);
        ViewCompat.setScaleX(v, 1.0F);
        v.setPivotY((float) (v.getMeasuredHeight() / 2));
        ViewCompat.setPivotX(v, (float) (v.getMeasuredWidth() / 2));
        ViewCompat.animate(v).setInterpolator(null);
    }
}
