package mercandalli.com.filespace.ui.fragment;

import android.os.Bundle;

import mercandalli.com.filespace.config.App;
import mercandalli.com.filespace.config.AppComponent;

/**
 * Created by Jonathan on 24/10/2015.
 */
public abstract class InjectedFragment extends FabFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inject(App.get(getActivity()).getAppComponent());
    }

    protected abstract void inject(AppComponent appComponent);
}
