package com.mercandalli.android.apps.files;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v4.view.LayoutInflaterFactory;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.mercandalli.android.apps.files.view.StaticProgressBar;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public final class LayoutInflaterWrapper {

    public static void wrap(final Activity activity) {
        final LayoutInflater layoutInflater = LayoutInflater.from(activity);

        if (activity instanceof AppCompatActivity) {
            final AppCompatDelegate delegate = ((AppCompatActivity) activity).getDelegate();
            if (delegate instanceof LayoutInflaterFactory) {
                LayoutInflaterCompat.setFactory(layoutInflater,
                        new LayoutInflaterWrapperCompat((LayoutInflaterFactory) delegate));
                return;
            }
        }

        final LayoutInflater.Factory factory = layoutInflater.getFactory();
        final LayoutInflater.Factory2 factory2 = layoutInflater.getFactory2();
        if (factory != null && factory2 != null) {
            layoutInflater.setFactory2(new LayoutInflaterWrapperFactory2());
        }
    }

    private static View createView(String name, Context context, AttributeSet attrs) {
        if ("ProgressBar".equals(name)) {
            return new StaticProgressBar(context, attrs);
        }
        return null;
    }

    private static class LayoutInflaterWrapperFactory2 implements LayoutInflater.Factory2 {

        @Override
        public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
            return createView(name, context, attrs);
        }

        @Override
        public View onCreateView(String name, Context context, AttributeSet attrs) {
            return createView(name, context, attrs);
        }
    }

    private static class LayoutInflaterWrapperCompat implements LayoutInflaterFactory {

        private final LayoutInflaterFactory mLayoutInflaterFactory;

        private LayoutInflaterWrapperCompat(LayoutInflaterFactory layoutInflaterFactory) {
            mLayoutInflaterFactory = layoutInflaterFactory;
        }

        @Override
        public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
            final View view = createView(name, context, attrs);
            if (view != null) {
                return view;
            }
            return mLayoutInflaterFactory.onCreateView(parent, name, context, attrs);
        }
    }
}