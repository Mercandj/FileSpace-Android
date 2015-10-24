package mercandalli.com.filespace.ui.fragments;

import android.os.Bundle;

import mercandalli.com.filespace.config.MyApp;
import mercandalli.com.filespace.config.MyAppComponent;

/**
 * Created by Jonathan on 24/10/2015.
 */
public abstract class InjectedFragment extends FabFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inject(MyApp.get(getActivity()).getAppComponent());
    }

    protected abstract void inject(MyAppComponent myAppComponent);
}
