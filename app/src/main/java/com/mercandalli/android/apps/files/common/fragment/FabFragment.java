package com.mercandalli.android.apps.files.common.fragment;

import android.content.Context;

import com.mercandalli.android.apps.files.fab.FabController;

/**
 * Manage the floating button.
 */
public abstract class FabFragment extends BackFragment implements FabController {

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

    @Override
    public void refreshFab() {
        if (mRefreshFabCallback != null) {
            mRefreshFabCallback.onRefreshFab();
        }
    }
}
