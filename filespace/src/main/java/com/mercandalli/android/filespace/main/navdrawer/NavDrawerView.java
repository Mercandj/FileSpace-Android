package com.mercandalli.android.filespace.main.navdrawer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.mercandalli.android.filespace.R;

public class NavDrawerView extends FrameLayout implements View.OnClickListener {

    /**
     * Is the user connected.
     */
    private boolean mIsConnected;

    /**
     * Is the user admin.
     */
    private boolean mIsAdmin;

    public NavDrawerView(Context context) {
        super(context);
        initView(context, null);
    }

    public NavDrawerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public NavDrawerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    @Override
    public void onClick(View v) {
        final int viewId = v.getId();
        switch (viewId) {
            case R.id.view_nav_drawer_header:
                // TODO - header clicked
                break;
            case R.id.view_nav_drawer_home:
                // TODO - home clicked
                break;
            case R.id.view_nav_drawer_files:
                // TODO - files clicked
                break;
        }
    }

    /* package */ void setConnected(boolean connected) {
        mIsConnected = connected;
    }

    /* package */ void setIsAdmin(boolean isAdmin) {
        mIsAdmin = isAdmin;
    }

    protected void initView(@NonNull Context context, @Nullable AttributeSet attrs) {
        inflate(context, R.layout.view_nav_drawer, this);

        findViews();

        // TODO - initView connected or not or admin
    }

    private void findViews() {
        findViewById(R.id.view_nav_drawer_header).setOnClickListener(this);
        findViewById(R.id.view_nav_drawer_home).setOnClickListener(this);
        findViewById(R.id.view_nav_drawer_files).setOnClickListener(this);
    }
}
