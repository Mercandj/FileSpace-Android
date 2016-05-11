package com.mercandalli.android.apps.files.main;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.mercandalli.android.apps.files.BuildConfig;
import com.mercandalli.android.apps.files.R;
import com.mercandalli.android.apps.files.file.FileUtils;
import com.mercandalli.android.apps.files.user.UserModel;
import com.mercandalli.android.library.base.precondition.Preconditions;

/**
 * The nav drawer header.
 */
/* package */
class NavDrawerHeaderView extends FrameLayout {

    /* Views */
    private TextView mTitleTextView;
    private TextView mSubtitleTextView;
    private ImageView mIconImageView;
    private ImageView mStorageImageView;
    private TextView mVersionTextView;

    public NavDrawerHeaderView(Context context) {
        super(context);
        initView(context);
    }

    public NavDrawerHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public NavDrawerHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    /**
     * Set the current {@link UserModel}.
     *
     * @param userModel The {@link UserModel}.
     */
    /* package */ void setUser(final UserModel userModel) {
        Preconditions.checkNotNull(userModel);

        mTitleTextView.setText(userModel.username);

        mIconImageView.setVisibility(View.VISIBLE);
        mStorageImageView.setVisibility(View.GONE);

        if (userModel.mPictureUrl != null) {
            Glide.with(getContext())
                    .load(new GlideUrl(userModel.mPictureUrl, new LazyHeaders.Builder()
                            .addHeader("Authorization", "Basic " + Config.getUserToken())
                            .build()))
                    .into(mIconImageView);
        }
    }

    private void initView(@NonNull Context context) {
        inflate(context, R.layout.view_nav_drawer_header, this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            setFitsSystemWindows(true);
        }

        findViews();

        if (!isInEditMode()) {
            final StatFs statFs = new StatFs(Environment.getExternalStorageDirectory().getAbsolutePath());
            final long blockSize = statFs.getBlockSize();
            final long totalSize = statFs.getBlockCount() * blockSize;
            final long availableSize = statFs.getAvailableBlocks() * blockSize;

            mIconImageView.setVisibility(View.GONE);
            mStorageImageView.setVisibility(View.VISIBLE);
            mTitleTextView.setText(context.getString(R.string.tab_header_title, (int) ((totalSize - availableSize) * 100.0 / totalSize)));
            mSubtitleTextView.setText(context.getString(R.string.tab_header_subtitle, FileUtils.humanReadableByteCount(totalSize - availableSize), FileUtils.humanReadableByteCount(totalSize)));

            mVersionTextView.setText(String.format("v%s", BuildConfig.VERSION_NAME));

            setPadding(0, getStatusBarHeight(), 0, 0);
        }
    }

    private void findViews() {
        mVersionTextView = (TextView) findViewById(R.id.view_nav_drawer_header_version);
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
