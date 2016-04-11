package com.mercandalli.android.apps.files.fab;

import android.support.design.widget.FloatingActionButton;

public interface FabContainer {

    void onFabClick(int fabId, FloatingActionButton fab);

    boolean isFabVisible(int fabId);

    int getFabImageResource(int fabId);

    void refreshFab();

    interface RefreshFabCallback {
        void onRefreshFab();

        void hideFab(int fab_id);
        void showFab(int fab_id);
    }
}
