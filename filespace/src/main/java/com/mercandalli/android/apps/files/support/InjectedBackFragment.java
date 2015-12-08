package com.mercandalli.android.apps.files.support;

import android.os.Bundle;

import com.mercandalli.android.apps.files.common.fragment.BackFragment;
import com.mercandalli.android.apps.files.main.App;
import com.mercandalli.android.apps.files.main.AppComponent;

public abstract class InjectedBackFragment extends BackFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inject(App.get(getActivity()).getAppComponent());
    }

    protected abstract void inject(AppComponent appComponent);
}
