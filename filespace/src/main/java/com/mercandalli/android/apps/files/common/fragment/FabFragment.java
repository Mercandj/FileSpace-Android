package com.mercandalli.android.apps.files.common.fragment;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;

/**
 * Manage the floating button.
 */
public abstract class FabFragment extends BackFragment {

    protected RefreshFabCallback mRefreshFabCallback;

    public FabFragment() {
        super();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof RefreshFabCallback) {
            mRefreshFabCallback = (RefreshFabCallback) context;
        } else {
            throw new IllegalArgumentException("Must be attached to a HomeActivity. Found: " + context);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mRefreshFabCallback = null;
    }

    public abstract void onFabClick(int fab_id, FloatingActionButton fab);

    public abstract boolean isFabVisible(int fab_id);

    public abstract int getFabImageResource(int fab_id);

    public void refreshFab() {
        if (mRefreshFabCallback != null)
            mRefreshFabCallback.onRefreshFab();
    }

    public interface RefreshFabCallback {
        void onRefreshFab();
    }
}
