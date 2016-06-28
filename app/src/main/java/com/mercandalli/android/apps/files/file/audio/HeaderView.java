package com.mercandalli.android.apps.files.file.audio;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.mercandalli.android.apps.files.R;
import com.mercandalli.android.apps.files.file.FileModelCardAdapter;
import com.mercandalli.android.apps.files.file.FileModelCardHeaderItem;

import java.util.ArrayList;
import java.util.List;

/* package */ class HeaderView extends FrameLayout implements View.OnClickListener {

    @NonNull
    private final List<FileModelCardAdapter.OnHeaderClickListener> mOnHeaderClickListeners = new ArrayList<>();
    @ColorInt
    private int mPrimaryColor;

    public HeaderView(final Context context) {
        super(context);
        initView(context);
    }

    public HeaderView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public HeaderView(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        updateView();
    }

    private void initView(@NonNull final Context context) {
        inflate(context, R.layout.view_file_header_audio, this);
        mPrimaryColor = ContextCompat.getColor(context, R.color.primary);
        updateView();
    }

    /* package */ void addOnHeaderClickListener(@NonNull final FileModelCardAdapter.OnHeaderClickListener onHeaderClickListener) {
        mOnHeaderClickListeners.add(onHeaderClickListener);
    }

    /* package */ void removeOnHeaderClickListener(@NonNull final FileModelCardAdapter.OnHeaderClickListener onHeaderClickListener) {
        mOnHeaderClickListeners.remove(onHeaderClickListener);
    }

    private void updateView() {
        final List<FileModelCardHeaderItem> headerIds = FileAudioHeaderManager.getInstance().getHeaderIds();
        for (FileModelCardHeaderItem f : headerIds) {
            final TextView tv = (TextView) findViewById(f.getId());
            tv.setOnClickListener(this);
            if (f.isSelected()) {
                tv.setTextColor(mPrimaryColor);
                tv.setBackgroundResource(R.drawable.file_local_audio_rounded_bg_selected);
            } else {
                tv.setTextColor(Color.WHITE);
                tv.setBackgroundResource(R.drawable.file_local_audio_rounded_bg);
            }
        }
    }

    @Override
    public void onClick(final View v) {
        final int viewId = v.getId();
        final List<FileModelCardHeaderItem> headerIds = FileAudioHeaderManager.getInstance().getHeaderIds();
        boolean isElementAlreadySelected = false;
        for (FileModelCardHeaderItem f : headerIds) {
            if (f.getId() == viewId && f.isSelected()) {
                isElementAlreadySelected = true;
                break;
            }
        }
        if (isElementAlreadySelected) {
            return;
        }
        for (FileModelCardHeaderItem f : headerIds) {
            f.setSelected(f.getId() == viewId);
        }
        FileAudioHeaderManager.getInstance().setHeaderIds(headerIds);
        notifyOnHeaderClickListeners(v, headerIds);
        updateView();
    }

    private void notifyOnHeaderClickListeners(View v, List<FileModelCardHeaderItem> fileModelCardHeaderItems) {
        for (final FileModelCardAdapter.OnHeaderClickListener onHeaderClickListener : mOnHeaderClickListeners) {
            onHeaderClickListener.onHeaderClick(v, fileModelCardHeaderItems);
        }
    }
}
