package com.mercandalli.android.apps.files.file.local.fab;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;

/**
 * Manage the {@link FloatingActionButton}s visibility, images, clicks...
 */
public interface FileLocalFabManager {

    int NUMBER_MAX_OF_FAB = 2;

    /**
     * The {@link android.support.v4.view.ViewPager.OnPageChangeListener#onPageSelected(int)} call
     * this method to notify a scroll.
     *
     * @param viewPagerPosition The new {@link android.support.v4.view.ViewPager} current page position.
     */
    void onCurrentViewPagerPageChange(final int viewPagerPosition);

    /**
     * Update the {@link FloatingActionButton}s in the current page.
     */
    void updateFabButtons();

    /**
     * Set the {@link FabContainer}. Build to be the {@link android.support.v4.view.ViewPager}
     * container.
     *
     * @param fabContainer The {@link FabContainer}.
     */
    void setFabContainer(final FabContainer fabContainer);

    boolean addFabContainer(final int positionInViewPager, final FabController fabController);

    boolean removeFabContainer(final int positionInViewPager);

    void onFabClick(
            final @IntRange(from = 0, to = NUMBER_MAX_OF_FAB - 1) int fabId,
            final @NonNull FloatingActionButton fab);

    /**
     * The {@link FloatingActionButton} container. If you follow the google guidelines, the
     * {@link FloatingActionButton} container and the {@link android.support.v4.view.ViewPager}
     * container are the same.
     */
    interface FabContainer {

        /**
         * Update the current {@link FabController} with the {@link FloatingActionButton} properties
         * include in the {@link FabState} object.
         *
         * @param fabStates The new {@link FabState}. The id the the array is the {@link FloatingActionButton}
         *                  position
         */
        void updateFabs(final FabState[] fabStates);
    }

    /**
     * Control the {@link FloatingActionButton}. It is often a {@link android.support.v7.widget.RecyclerView}
     * container.
     */
    interface FabController {

        /**
         * Notify the current {@link FabController} that a {@link FloatingActionButton} has been clicked.
         *
         * @param fabPosition          The {@link FloatingActionButton} position, between 0 and
         *                             {@link FileLocalFabManager#NUMBER_MAX_OF_FAB}.
         * @param floatingActionButton The {@link FloatingActionButton} {@link  android.view.View}.
         */
        void onFabClick(
                final @IntRange(from = 0, to = NUMBER_MAX_OF_FAB - 1) int fabPosition,
                final @NonNull FloatingActionButton floatingActionButton);

        boolean isFabVisible(
                final @IntRange(from = 0, to = NUMBER_MAX_OF_FAB - 1) int fabPosition);

        int getFabImageResource(
                final @IntRange(from = 0, to = NUMBER_MAX_OF_FAB - 1) int fabPosition);
    }

    final class FabState {
        public boolean fabVisible;
        public int fabImageResource;
    }
}