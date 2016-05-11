package com.mercandalli.android.apps.files.storage;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mercandalli.android.apps.files.R;

import static com.mercandalli.android.library.base.view.ViewUtils.spToPx;


public class StorageProgressBarWrapper extends FrameLayout {

    private static final String SAVED_PROGRESS_BAR_MAX = "StorageProgressBarWrapper.Saved.SAVED_PROGRESS_BAR_MAX";

    /**
     * The real {@link #mProgressBarMax} max.
     * See {@link {@link #setProgress(int)}}.
     */
    private static final int PROGRESS_BAR_VIEW_MAX = 1_000;

    /**
     * The current progress.
     * Attention: mProgressBarMax != {@link #mProgressBar#getProgress()}.
     * See {@link {@link #setProgress(int)}}.
     */
    private int mProgressBarMax = 100;

    private int mProgress;

    private ProgressBar mProgressBar;
    private TextView mTextView;
    private int mDuration = 300;

    /**
     * Default selector label size (SP).
     */
    private static final int DEFAULT_SELECTOR_LABEL_SIZE = 30;

    private int mTextViewSize;

    public StorageProgressBarWrapper(Context context) {
        super(context);
        init(context, null);
    }

    public StorageProgressBarWrapper(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public StorageProgressBarWrapper(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(@NonNull final Context context, @Nullable final AttributeSet attrs) {
        final View rootView = inflate(context, R.layout.view_storage_progress_bar_wrapper, this);

        mTextView = (TextView) rootView.findViewById(R.id.view_storage_progress_bar_wrapper_text_view);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.view_storage_progress_bar_wrapper_progress_bar);
        mTextView.setText(String.format("%d %%", 0));

        if (!isInEditMode()) {
            extractAttrs(context, attrs);
            mTextView.setTextSize(mTextViewSize);
            mProgressBar.setMax(PROGRESS_BAR_VIEW_MAX);
        }
    }

    /**
     * <p>Set the current progress to the specified value.</p>
     *
     * @param progress the new progress, between 0 and {@link #getMax()}
     */
    public void setProgress(final int progress) {
        if (mProgress != progress) {
            int viewProgress = (int) ((1.0 * progress / mProgressBarMax) * PROGRESS_BAR_VIEW_MAX);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
                ObjectAnimator animation = ObjectAnimator.ofInt(this, "progressInternal", viewProgress);
                animation.setDuration(mDuration); //in milliseconds
                animation.setInterpolator(new AccelerateDecelerateInterpolator());
                animation.start();
            } else {
                setProgressInternal(viewProgress);
            }
        }
        mProgress = progress;
    }

    public void setDuration(final int duration) {
        mDuration = duration;
    }

    /**
     * <p>Set the range of the progress bar to 0...<tt>max</tt>.</p>
     *
     * @param max the upper range of this progress bar
     */
    public void setMax(int max) {
        mProgressBarMax = max;
    }

    public int getMax() {
        return mProgressBarMax;
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(SAVED_PROGRESS_BAR_MAX, mProgressBarMax);
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        mProgressBarMax = savedInstanceState.getInt(SAVED_PROGRESS_BAR_MAX, 100);
    }

    public void setProgressInternal(final int progress) {
        mTextView.setText(String.format("%d %%", progress / 10));
        mProgressBar.setProgress(progress);
    }

    protected void extractAttrs(@NonNull final Context context, @Nullable final AttributeSet attrs) {
        if (attrs == null) {
            return;
        }
        final DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        final TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs, R.styleable.StorageProgressBarWrapper, 0, 0);
        try {
            mTextViewSize = a.getDimensionPixelSize(
                    R.styleable.StorageProgressBarWrapper_spb_textLabelSize,
                    (int) spToPx(displayMetrics, DEFAULT_SELECTOR_LABEL_SIZE));
        } finally {
            a.recycle();
        }
    }
}

