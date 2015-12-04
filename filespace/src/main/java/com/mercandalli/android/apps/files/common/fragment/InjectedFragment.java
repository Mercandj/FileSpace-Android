package com.mercandalli.android.apps.files.common.fragment;

import android.os.Bundle;

import com.mercandalli.android.apps.files.main.App;
import com.mercandalli.android.apps.files.main.AppComponent;

/**
 * A Dagger injected {@link FabFragment}.
 */
public abstract class InjectedFragment extends FabFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inject(App.get(getActivity()).getAppComponent());
    }

    protected abstract void inject(AppComponent appComponent);
}
