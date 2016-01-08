package com.mercandalli.android.apps.files.common.fragment;

import android.os.Bundle;

import com.mercandalli.android.apps.files.main.FileApp;
import com.mercandalli.android.apps.files.main.FileAppComponent;

/**
 * A Dagger injected {@link FabFragment}.
 */
public abstract class InjectedFabFragment extends FabFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inject(FileApp.get(getActivity()).getFileAppComponent());
    }

    protected abstract void inject(FileAppComponent fileAppComponent);
}
