package com.mercandalli.android.apps.files.support;

import android.os.Bundle;

import com.mercandalli.android.apps.files.common.fragment.BackFragment;
import com.mercandalli.android.apps.files.main.FileApp;
import com.mercandalli.android.apps.files.main.FileAppComponent;

public abstract class InjectedBackFragment extends BackFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inject(FileApp.get(getActivity()).getFileAppComponent());
    }

    protected abstract void inject(FileAppComponent fileAppComponent);
}
