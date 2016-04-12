package com.mercandalli.android.apps.files.file.local.fab;

import android.support.annotation.IntRange;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;

import java.util.HashMap;
import java.util.Map;

/* package */ class FileLocalFabManagerImpl implements FileLocalFabManager {

    @Nullable
    private FabContainer mFabContainer;
    private final Map<Integer, FabController> mFabContainers = new HashMap<>();
    private int mViewPagerPosition;

    public FileLocalFabManagerImpl() {

    }

    @Override
    public void onCurrentViewPagerPageChange(final int viewPagerPosition) {
        mViewPagerPosition = viewPagerPosition;
        updateFabButtons();
    }

    @Override
    public void updateFabButtons() {
        if (mFabContainer == null) {
            return;
        }
        final FabController fabController = getFabController(mViewPagerPosition);
        if (fabController == null) {
            return;
        }

        final FabState[] fabStates = new FabState[2];
        fabStates[0] = new FabState();
        fabStates[1] = new FabState();
        fabStates[0].fabVisible = fabController.isFabVisible(0);
        fabStates[1].fabVisible = fabController.isFabVisible(1);
        fabStates[0].fabImageResource = fabController.getFabImageResource(0);
        fabStates[1].fabImageResource = fabController.getFabImageResource(1);
        mFabContainer.updateFabs(fabStates);
    }

    @Override
    public void setFabContainer(@Nullable final FabContainer fabContainer) {
        mFabContainer = fabContainer;
    }

    @Override
    public boolean addFabContainer(final int positionInViewPager, final FabController fabController) {
        return mFabContainers.put(positionInViewPager, fabController) == fabController;
    }

    @Override
    public boolean removeFabContainer(final int positionInViewPager) {
        return mFabContainers.remove(positionInViewPager) != null;
    }

    @Override
    public void onFabClick(@IntRange(from=0,to=1) final int fabId, final FloatingActionButton floatingActionButton) {
        if (mFabContainer == null) {
            return;
        }
        final FabController fabController = getFabController(mViewPagerPosition);
        if (fabController == null) {
            return;
        }
        fabController.onFabClick(fabId, floatingActionButton);
    }

    @Nullable
    private FabController getFabController(final int viewPagerPosition) {
        if (mFabContainers.containsKey(viewPagerPosition)) {
            return mFabContainers.get(viewPagerPosition);
        }
        return null;
    }
}