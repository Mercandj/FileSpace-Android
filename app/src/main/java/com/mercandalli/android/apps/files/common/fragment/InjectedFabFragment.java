package com.mercandalli.android.apps.files.common.fragment;

import android.os.Bundle;

import com.mercandalli.android.apps.files.main.FileApp;
import com.mercandalli.android.apps.files.main.FileAppComponent;

/**
 * A Dagger injected {@link BackFragment}.
 */
public abstract class InjectedFabFragment extends BackFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inject(FileApp.get().getFileAppComponent());
    }

    protected abstract void inject(FileAppComponent fileAppComponent);
}
