package com.mercandalli.android.apps.files.main;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.mercandalli.android.apps.files.R;
import com.mercandalli.android.apps.files.user.UserModel;
import com.mercandalli.android.library.base.precondition.Preconditions;
import com.mercandalli.android.library.base.view.ViewUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * The all NavDrawer {@link FrameLayout}.
 */
/* package */
class NavDrawerView extends FrameLayout implements View.OnClickListener {

    private static final String TAG = "NavDrawerView";
    private NavDrawerRow mLastClicked;

    private NavDrawerHeaderView mNavDrawerHeaderView;

    private OnNavDrawerClickCallback mOnNavDrawerClickCallback;

    private final Map<Integer, View> mChildrenViews = new HashMap<>();
    private final Map<Integer, TextView> mTextViews = new HashMap<>();
    private final Map<Integer, ImageView> mImageViews = new HashMap<>();

    public NavDrawerView(Context context) {
        super(context);
        initView(context);
    }

    public NavDrawerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public NavDrawerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    @Override
    public void onClick(View v) {
        final NavDrawerRow navDrawerRow = setSelectedRow(getContext(), getNavDrawerRowById(v.getId()));
        if (mOnNavDrawerClickCallback != null) {
            mOnNavDrawerClickCallback.onNavDrawerClicked(navDrawerRow, v);
        }
    }

    /* package */ NavDrawerRow setSelectedRow(final Context context, @NonNull final NavDrawerRow navDrawerRow) {
        Preconditions.checkNotNull(context);
        Preconditions.checkNotNull(navDrawerRow);

        if (!navDrawerRow.mIsSelectable) {
            return navDrawerRow;
        }

        if (mLastClicked != null && mLastClicked.mNavDrawerSimpleRow != null) {
            applyFont(mTextViews.get(mLastClicked.mId), mLastClicked, false);
            mImageViews.get(mLastClicked.mId).setImageResource(
                    mLastClicked.mNavDrawerSimpleRow.mDrawableId);
        }

        if (mOnNavDrawerClickCallback != null) {
            if (navDrawerRow.mNavDrawerSimpleRow != null) {
                applyFont(mTextViews.get(navDrawerRow.mId), navDrawerRow, true);
                if (!isOtherSection(navDrawerRow)) {
                    mImageViews.get(navDrawerRow.mId)
                            .setImageResource(navDrawerRow.mNavDrawerSimpleRow.mDrawablePressedId);
                }
            }
            mLastClicked = navDrawerRow;
        }
        return mLastClicked;
    }

    @Nullable
    /* package */ NavDrawerRow getLastClicked() {
        return mLastClicked;
    }

    /* package */ void setOnNavDrawerClickCallback(OnNavDrawerClickCallback onNavDrawerClickCallback) {
        mOnNavDrawerClickCallback = onNavDrawerClickCallback;
    }

    /* package */ void setConnected(boolean connected) {
        for (final NavDrawerRow navDrawerRow : NavDrawerRow.values()) {
            if (!navDrawerRow.mVisibleIfDisconnected) {
                mChildrenViews.get(navDrawerRow.mId).setVisibility(connected ? VISIBLE : GONE);
            }
        }
    }

    /* package */ void setUser(final UserModel userModel) {
        final boolean isAdmin = userModel.isAdmin();
        for (final NavDrawerRow navDrawerRow : NavDrawerRow.values()) {
            if (!navDrawerRow.mVisibleIfNotAdmin) {
                mChildrenViews.get(navDrawerRow.mId).setVisibility(isAdmin ? VISIBLE : GONE);
            }
        }
        mNavDrawerHeaderView.setUser(userModel);
    }

    private void initView(final @NonNull Context context) {
        inflate(context, R.layout.view_nav_drawer, this);
        setBackgroundColor(Color.WHITE);

        mNavDrawerHeaderView = (NavDrawerHeaderView) findViewById(R.id.view_nav_drawer_header);

        FileApp.logPerformance(TAG, "initView - Start");

        ViewUtils.applyFont((TextView) findViewById(R.id.view_nav_drawer_other), "fonts/MYRIADAB.TTF");

        for (final NavDrawerRow navDrawerRow : NavDrawerRow.values()) {
            final View view = findViewById(navDrawerRow.mId);
            mChildrenViews.put(navDrawerRow.mId, view);
            view.setOnClickListener(this);
            if (navDrawerRow.mNavDrawerSimpleRow != null) {
                final TextView textView = (TextView) findViewById(navDrawerRow.mNavDrawerSimpleRow.mTitleId);
                applyFont(textView, navDrawerRow, false);
                mTextViews.put(navDrawerRow.mId, textView);
                mImageViews.put(navDrawerRow.mId, (ImageView) findViewById(navDrawerRow.mNavDrawerSimpleRow.mIconId));
            }
        }

        FileApp.logPerformance(TAG, "initView - End");
    }

    private void applyFont(TextView textView, NavDrawerRow navDrawerRow, boolean selected) {
        if (navDrawerRow.mNavDrawerSimpleRow == null) {
            return;
        }
        if (selected) {
            textView.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
        } else {
            textView.setTypeface(Typeface.SANS_SERIF, Typeface.NORMAL);
        }
    }

    private boolean isOtherSection(NavDrawerRow navDrawerRow) {
        return navDrawerRow.mNavDrawerSimpleRow != null && navDrawerRow.mNavDrawerSimpleRow.mDrawablePressedId == null;
    }

    @NonNull
    private NavDrawerRow getNavDrawerRowById(final int viewId) {
        for (NavDrawerRow navDrawerRow : NavDrawerRow.values()) {
            if (navDrawerRow.mId == viewId) {
                return navDrawerRow;
            }
        }
        throw new IllegalArgumentException("Wrong id. No NavDrawerRow getNavDrawerRowById.");
    }

    /**
     * The nav drawer items.
     */
    /* package */ enum NavDrawerRow {
        HEADER(R.id.view_nav_drawer_header, null, true, true, false),
        FILES(R.id.view_nav_drawer_files, NavDrawerSimpleRow.FILES, true, true),
        CLOUD(R.id.view_nav_drawer_cloud, NavDrawerSimpleRow.CLOUD, false, true),
        WORKSPACE(R.id.view_nav_drawer_workspace, NavDrawerSimpleRow.NOTES, true, true),
        COMMUNITY(R.id.view_nav_drawer_community, NavDrawerSimpleRow.COMMUNITY, false, true),
        ADMIN(R.id.view_nav_drawer_admin, NavDrawerSimpleRow.ADMIN, false, false),
        SETTINGS(R.id.view_nav_drawer_settings, NavDrawerSimpleRow.SETTINGS, true, true),
        LOYALTY(R.id.view_nav_drawer_loyalty, NavDrawerSimpleRow.LOYALTY, Constants.ADS_VISIBLE, Constants.ADS_VISIBLE, false),
        LOGOUT(R.id.view_nav_drawer_logout, NavDrawerSimpleRow.LOGOUT, false, true),
        SUPPORT(R.id.view_nav_drawer_support, NavDrawerSimpleRow.SUPPORT, true, true);

        private final int mId;

        @Nullable
        private final NavDrawerSimpleRow mNavDrawerSimpleRow;

        private final boolean mVisibleIfDisconnected;
        private final boolean mVisibleIfNotAdmin;
        private final boolean mIsSelectable;

        NavDrawerRow(final int id, @Nullable NavDrawerSimpleRow navDrawerSimpleRow,
                     boolean visibleIfDisconnected, boolean visibleIfNotAdmin) {
            this(id, navDrawerSimpleRow, visibleIfDisconnected, visibleIfNotAdmin, true);
        }

        NavDrawerRow(final int id, @Nullable NavDrawerSimpleRow navDrawerSimpleRow,
                     boolean visibleIfDisconnected, boolean visibleIfNotAdmin, boolean isSelectable) {
            mId = id;
            mNavDrawerSimpleRow = navDrawerSimpleRow;
            mVisibleIfDisconnected = visibleIfDisconnected;
            mVisibleIfNotAdmin = visibleIfNotAdmin;
            mIsSelectable = isSelectable;
        }

        @NonNull
        /* package */ String getTag() {
            return mId + "" + mVisibleIfDisconnected + "" + mVisibleIfNotAdmin;
        }
    }

    /**
     * The simple nav drawer items.
     */
    private enum NavDrawerSimpleRow {
        FILES(R.id.view_nav_drawer_files_title,
                R.id.view_nav_drawer_files_icon,
                R.drawable.q_ic_drawer_home,
                R.drawable.q_ic_drawer_home_pressed),

        CLOUD(R.id.view_nav_drawer_cloud_title,
                R.id.view_nav_drawer_cloud_icon,
                R.drawable.q_ic_drawer_files,
                R.drawable.q_ic_drawer_files_pressed),

        NOTES(R.id.view_nav_drawer_note_title,
                R.id.view_nav_drawer_note_icon,
                R.drawable.q_ic_drawer_workspace,
                R.drawable.q_ic_drawer_workspace_pressed),

        COMMUNITY(R.id.view_nav_drawer_community_title,
                R.id.view_nav_drawer_community_icon,
                R.drawable.q_ic_drawer_community,
                R.drawable.q_ic_drawer_community_pressed),

        ADMIN(R.id.view_nav_drawer_admin_title,
                R.id.view_nav_drawer_admin_icon,
                R.drawable.q_ic_drawer_data,
                R.drawable.q_ic_drawer_data_pressed),

        SETTINGS(R.id.view_nav_drawer_settings_title,
                R.id.view_nav_drawer_settings_icon,
                R.drawable.ic_settings_grey,
                null),

        LOYALTY(R.id.view_nav_drawer_loyalty_title,
                R.id.view_nav_drawer_loyalty_icon,
                R.drawable.ic_loyalty,
                null),

        LOGOUT(R.id.view_nav_drawer_logout_title,
                R.id.view_nav_drawer_logout_icon,
                R.drawable.ic_log_out,
                null),

        SUPPORT(R.id.view_nav_drawer_support_title,
                R.id.view_nav_drawer_support_icon,
                R.drawable.ic_help_grey,
                null);

        private final int mTitleId;
        private final int mIconId;
        private final int mDrawableId;
        private final Integer mDrawablePressedId;

        NavDrawerSimpleRow(final int titleId, final int iconId, @DrawableRes int drawableId, @DrawableRes Integer drawablePressedId) {
            mTitleId = titleId;
            mIconId = iconId;
            mDrawableId = drawableId;
            mDrawablePressedId = drawablePressedId;
        }
    }

    /**
     * Interface definition for a callback to be invoked when a nav drawer view is clicked.
     */
    interface OnNavDrawerClickCallback {
        /**
         * Called when a view has been clicked.
         *
         * @param navDrawerRow The nav drawer view that was clicked.
         * @param v            The view that was clicked.
         */
        void onNavDrawerClicked(NavDrawerRow navDrawerRow, View v);
    }
}
