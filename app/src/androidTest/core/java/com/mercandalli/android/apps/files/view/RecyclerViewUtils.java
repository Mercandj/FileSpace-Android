package com.mercandalli.android.apps.files.view;

import android.support.annotation.IdRes;
import android.support.test.espresso.contrib.RecyclerViewActions;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;

/**
 * Static methods for dealing with {@link android.support.v7.widget.RecyclerView}.
 */
public final class RecyclerViewUtils {

    /**
     * Click a {@link android.support.v7.widget.RecyclerView} item.
     *
     * @param idRecycler The {@link android.support.v7.widget.RecyclerView} id.
     * @param position   The item position.
     */
    @SuppressWarnings("unused")
    public static void clickRecyclerAt(@IdRes final int idRecycler, final int position) {
        onView(withId(idRecycler)).perform(
                RecyclerViewActions.actionOnItemAtPosition(position, click()));
    }

    /**
     * Click a {@link android.support.v7.widget.RecyclerView} item.
     *
     * @param idRecycler The {@link android.support.v7.widget.RecyclerView} id.
     * @param idParent   The {@link android.support.v7.widget.RecyclerView} parent id.
     * @param position   The item position.
     */
    @SuppressWarnings("unused")
    public static void clickRecyclerAt(@IdRes final int idRecycler, @IdRes final int idParent, final int position) {
        onView(allOf(withId(idRecycler),
                isDescendantOfA(withId(idParent)))).perform(
                RecyclerViewActions.actionOnItemAtPosition(position, click()));
    }
}
