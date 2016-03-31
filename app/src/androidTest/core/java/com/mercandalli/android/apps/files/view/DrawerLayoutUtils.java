package com.mercandalli.android.apps.files.view;

import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.v4.view.GravityCompat;
import android.view.View;

import org.hamcrest.Matcher;

import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;

public class DrawerLayoutUtils {

    public static ViewAction actionOpenDrawer() {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isAssignableFrom(android.support.v4.widget.DrawerLayout.class);
            }

            @Override
            public String getDescription() {
                return "open drawer";
            }

            @Override
            public void perform(UiController uiController, View view) {
                ((android.support.v4.widget.DrawerLayout) view).openDrawer(GravityCompat.START);
            }
        };
    }

    public static ViewAction actionCloseDrawer() {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isAssignableFrom(android.support.v4.widget.DrawerLayout.class);
            }

            @Override
            public String getDescription() {
                return "close drawer";
            }

            @Override
            public void perform(UiController uiController, View view) {
                ((android.support.v4.widget.DrawerLayout) view).closeDrawer(GravityCompat.START);
            }
        };
    }
}
