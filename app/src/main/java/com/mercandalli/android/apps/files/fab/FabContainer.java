package com.mercandalli.android.apps.files.fab;

import android.support.design.widget.FloatingActionButton;

public interface FabContainer {

    void onFabClick(int fab_id, FloatingActionButton fab);

    boolean isFabVisible(int fab_id);

    int getFabImageResource(int fab_id);

    void refreshFab();

    interface RefreshFabCallback {
        void onRefreshFab();

        void hideFab(int fab_id);
        void showFab(int fab_id);
    }
}
