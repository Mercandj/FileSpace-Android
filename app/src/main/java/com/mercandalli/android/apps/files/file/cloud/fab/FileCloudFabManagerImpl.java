package com.mercandalli.android.apps.files.file.cloud.fab;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;

import java.util.HashMap;
import java.util.Map;

/**
 * A {@link FileCloudFabManager} implementation.
 */
/* package */
class FileCloudFabManagerImpl extends FileCloudFabManager {

    @Nullable
    private FabContainer mFabContainer;
    private final Map<Integer, FabController> mFabContainers = new HashMap<>();
    private int mViewPagerPosition;

    /* package */ FileCloudFabManagerImpl() {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCurrentViewPagerPageChange(final int viewPagerPosition) {
        mViewPagerPosition = viewPagerPosition;
        updateFabButtons();
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFabContainer(@Nullable final FabContainer fabContainer, final int initPositionInViewPager) {
        mFabContainer = fabContainer;
        mViewPagerPosition = initPositionInViewPager;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean addFabController(final int positionInViewPager, final FabController fabController) {
        return mFabContainers.put(positionInViewPager, fabController) == fabController;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean removeFabController(final int positionInViewPager) {
        return mFabContainers.remove(positionInViewPager) != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onFabClick(
            final @IntRange(from = 0, to = 1) int fabPosition,
            final @NonNull FloatingActionButton floatingActionButton) {
        if (mFabContainer == null) {
            return;
        }
        final FabController fabController = getFabController(mViewPagerPosition);
        if (fabController == null) {
            return;
        }
        fabController.onFabClick(fabPosition, floatingActionButton);
    }

    @Nullable
    private FabController getFabController(final int viewPagerPosition) {
        if (mFabContainers.containsKey(viewPagerPosition)) {
            return mFabContainers.get(viewPagerPosition);
        }
        return null;
    }
}