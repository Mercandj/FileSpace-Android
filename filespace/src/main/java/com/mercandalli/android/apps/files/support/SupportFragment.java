package com.mercandalli.android.apps.files.support;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mercandalli.android.apps.files.R;
import com.mercandalli.android.apps.files.common.fragment.BackFragment;
import com.mercandalli.android.apps.files.common.listener.SetToolbarCallback;

public class SupportFragment extends BackFragment {

    private static final String BUNDLE_ARG_TITLE = "SupportFragment.Args.BUNDLE_ARG_TITLE";
    private SetToolbarCallback mSetToolbarCallback;
    private String mTitle;

    public static SupportFragment newInstance(String title) {
        final SupportFragment fragment = new SupportFragment();
        final Bundle args = new Bundle();
        args.putString(BUNDLE_ARG_TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SetToolbarCallback) {
            mSetToolbarCallback = (SetToolbarCallback) context;
        } else {
            throw new IllegalArgumentException("Must be attached to a HomeActivity. Found: " + context);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mSetToolbarCallback = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Bundle args = getArguments();
        if (!args.containsKey(BUNDLE_ARG_TITLE)) {
            throw new IllegalStateException("Missing args. Please use newInstance()");
        }
        mTitle = args.getString(BUNDLE_ARG_TITLE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_support, container, false);

        final Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.fragment_support_toolbar);
        toolbar.setTitle(mTitle);
        mSetToolbarCallback.setToolbar(toolbar);


        return rootView;
    }

    @Override
    public boolean back() {
        return false;
    }

    @Override
    public void onFocus() {

    }
}
