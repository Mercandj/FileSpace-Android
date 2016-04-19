package com.mercandalli.android.apps.files.file.cloud.fab;

import android.support.annotation.DrawableRes;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;

/**
 * Manage the {@link FloatingActionButton}s visibility, images, clicks...
 */
public interface FileCloudFabManager {

    /**
     * The number of {@link FloatingActionButton} maximum.
     */
    @SuppressWarnings("UnnecessaryInterfaceModifier")
    static final int NUMBER_MAX_OF_FAB = 2;

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
     * @param fabContainer            The {@link FabContainer}.
     * @param initPositionInViewPager The initial {@link android.support.v4.view.ViewPager} position.
     */
    void setFabContainer(final FabContainer fabContainer, final int initPositionInViewPager);

    /**
     * Add a {@link FabController}.
     *
     * @param positionInViewPager The position in the {@link android.support.v4.view.ViewPager}.
     * @param fabController       The {@link FabController} to add.
     * @return <code>true</code> if the {@link FabController} is added.
     */
    boolean addFabController(final int positionInViewPager, final FabController fabController);

    /**
     * Remove a {@link FabController}.
     *
     * @param positionInViewPager The position in the {@link android.support.v4.view.ViewPager}.
     * @return <code>true</code> if the {@link FabController} is removed.
     */
    boolean removeFabController(final int positionInViewPager);

    /**
     * The {@link FabContainer} call this method when a {@link FloatingActionButton} is clicked to
     * notify the current {@link FabController}.
     *
     * @param fabPosition The {@link FloatingActionButton} position.
     * @param fab         THe {@link android.view.View} clicked.
     */
    void onFabClick(
            final @IntRange(from = 0, to = NUMBER_MAX_OF_FAB - 1) int fabPosition,
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
         *                             {@link FileCloudFabManager#NUMBER_MAX_OF_FAB}.
         * @param floatingActionButton The {@link FloatingActionButton} {@link  android.view.View}.
         */
        void onFabClick(
                final @IntRange(from = 0, to = NUMBER_MAX_OF_FAB - 1) int fabPosition,
                final @NonNull FloatingActionButton floatingActionButton);

        /**
         * Is the {@link FloatingActionButton} of this controller visible.
         *
         * @param fabPosition The {@link FloatingActionButton} position.
         * @return <code>true</code> if visible, <code>false</code> otherwise.
         */
        boolean isFabVisible(
                final @IntRange(from = 0, to = NUMBER_MAX_OF_FAB - 1) int fabPosition);

        /**
         * Get the {@link FloatingActionButton} image resource of this controller.
         *
         * @param fabPosition The {@link FloatingActionButton} position.
         * @return The resource.
         */
        @DrawableRes
        int getFabImageResource(
                final @IntRange(from = 0, to = NUMBER_MAX_OF_FAB - 1) int fabPosition);
    }

    /**
     * All the {@link FloatingActionButton} data.
     */
    final class FabState {

        /**
         * Is the {@link FloatingActionButton} visible.
         */
        public boolean fabVisible;

        /**
         * The image resource. Example <code>R.drawable.arrow_up</code>.
         */
        @DrawableRes
        public int fabImageResource;
    }
}