package com.mercandalli.android.filespace.common.fragment;

import android.os.Bundle;

import com.mercandalli.android.filespace.main.App;
import com.mercandalli.android.filespace.main.AppComponent;

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
