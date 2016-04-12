package com.mercandalli.android.apps.files.file.local.fab;

import android.support.annotation.IntRange;
import android.support.design.widget.FloatingActionButton;

public interface FileLocalFabManager {

    void onCurrentViewPagerPageChange(final int viewPagerPosition);

    void updateFabButtons();

    void setFabContainer(final FabContainer fabContainer);

    boolean addFabContainer(final int positionInViewPager, final FabController fabController);

    boolean removeFabContainer(final int positionInViewPager);

    void onFabClick(@IntRange(from=0,to=1) int fabId, FloatingActionButton fab);

    interface FabContainer {
        void updateFabs(final FabState[] fabStates);
    }

    interface FabController {
        void onFabClick(@IntRange(from=0,to=1) final int fabId, final FloatingActionButton floatingActionButton);

        boolean isFabVisible(@IntRange(from=0,to=1) int fabId);

        int getFabImageResource(@IntRange(from=0,to=1) int fabId);
    }

    final class FabState {
        public boolean fabVisible;
        public int fabImageResource;
    }
}