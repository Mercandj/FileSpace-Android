package com.mercandalli.android.filespace.main;

import android.content.Context;
import android.graphics.Bitmap;
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

/**
 * The nav drawer header.
 */
public class NavDrawerHeaderView extends FrameLayout {

    /* Views */
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

    /**
     * Set the current {@link ModelUser}.
     *
     * @param modelUser      The {@link ModelUser}.
     * @param profilePicture The user profile picture.
     */
    /* package */ void setUser(final ModelUser modelUser, final Bitmap profilePicture) {
        Preconditions.checkNotNull(modelUser);

        mTitleTextView.setText(modelUser.username);

        mIconImageView.setVisibility(View.VISIBLE);
        mStorageImageView.setVisibility(View.GONE);

        if (profilePicture != null) {
            mIconImageView.setImageBitmap(profilePicture);
        }
    }

    private void initView(@NonNull Context context, @Nullable AttributeSet attrs) {
        inflate(context, R.layout.view_nav_drawer_header, this);

        findViews();

        if (!isInEditMode()) {
            FontUtils.applyFont(context, mTitleTextView, "fonts/Roboto-Medium.ttf");
            FontUtils.applyFont(context, mSubtitleTextView, "fonts/Roboto-Light.ttf");

            StatFs statFs = new StatFs(Environment.getExternalStorageDirectory().getAbsolutePath());
            long blockSize = statFs.getBlockSize();
            long totalSize = statFs.getBlockCount() * blockSize;
            long availableSize = statFs.getAvailableBlocks() * blockSize;
            long freeSize = statFs.getFreeBlocks() * blockSize;

            mIconImageView.setVisibility(View.GONE);
            mStorageImageView.setVisibility(View.VISIBLE);
            mSubtitleTextView.setText(String.format("Using %s of %s", FileUtils.humanReadableByteCount(totalSize - availableSize), FileUtils.humanReadableByteCount(totalSize)));
            mTitleTextView.setText(String.format("%d%% Full", (int) ((totalSize - availableSize) * 100.0 / totalSize)));

            setPadding(0, getStatusBarHeight(), 0, 0);
        }
    }

    private void findViews() {
        mTitleTextView = (TextView) findViewById(R.id.view_nav_drawer_header_title);
        mSubtitleTextView = (TextView) findViewById(R.id.view_nav_drawer_header_subtitle);
        mIconImageView = (ImageView) findViewById(R.id.view_nav_drawer_header_icon);
        mStorageImageView = (ImageView) findViewById(R.id.view_nav_drawer_header_image_storage);
    }

    /**
     * Get the height of the top status bar.
     *
     * @return The status bar height (px).
     */
    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
