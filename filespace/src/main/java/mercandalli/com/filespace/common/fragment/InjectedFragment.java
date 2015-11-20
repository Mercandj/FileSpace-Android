package mercandalli.com.filespace.common.fragment;

import android.os.Bundle;

import mercandalli.com.filespace.main.App;
import mercandalli.com.filespace.main.AppComponent;

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
