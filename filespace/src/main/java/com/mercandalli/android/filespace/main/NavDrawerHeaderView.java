package com.mercandalli.android.filespace.main;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.mercandalli.android.filespace.R;
import com.mercandalli.android.filespace.common.Preconditions;
import com.mercandalli.android.filespace.common.util.FileUtils;
import com.mercandalli.android.filespace.common.util.FontUtils;
import com.mercandalli.android.filespace.user.ModelUser;

public class NavDrawerHeaderView extends FrameLayout implements View.OnClickListener {

    private TextView mTitleTextView;
    private TextView mSubtitleTextView;
    private ImageView mIconImageView;
    private ImageView mStorageImageView;

    public NavDrawerHeaderView(Context context) {
        super(context);
        initView(context, null);
    }

    public NavDrawerHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public NavDrawerHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    @Override
    public void onClick(View v) {


    }

    private void initView(@NonNull Context context, @Nullable AttributeSet attrs) {
        inflate(context, R.layout.view_nav_drawer_header, this);

        findViews();


        FontUtils.applyFont(context, mTitleTextView, "fonts/Roboto-Medium.ttf");

        mSubtitleTextView.setText("Edit your profile");
        mIconImageView.setVisibility(View.INVISIBLE);

        StatFs statFs = new StatFs(Environment.getExternalStorageDirectory().getAbsolutePath());
        long blockSize = statFs.getBlockSize();
        long totalSize = statFs.getBlockCount() * blockSize;
        long availableSize = statFs.getAvailableBlocks() * blockSize;
        long freeSize = statFs.getFreeBlocks() * blockSize;

        mStorageImageView.setVisibility(View.GONE);
        mSubtitleTextView.setText(String.format("Using %s of %s", FileUtils.humanReadableByteCount(totalSize - availableSize), FileUtils.humanReadableByteCount(totalSize)));

    }

    private void findViews() {
        mTitleTextView = (TextView) findViewById(R.id.view_nav_drawer_header_title);
        mSubtitleTextView = (TextView) findViewById(R.id.view_nav_drawer_header_subtitle);
        mIconImageView = (ImageView) findViewById(R.id.view_nav_drawer_header_icon);
        mStorageImageView = (ImageView) findViewById(R.id.view_nav_drawer_header_image_storage);
    }


    /* package */ void setUser(final ModelUser modelUser) {
        Preconditions.checkNotNull(modelUser);

        mTitleTextView.setText(modelUser.username);

        mIconImageView.setVisibility(View.INVISIBLE);
        mStorageImageView.setVisibility(View.GONE);
    }
}
