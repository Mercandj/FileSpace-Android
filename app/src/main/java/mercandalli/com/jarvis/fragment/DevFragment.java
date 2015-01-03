package mercandalli.com.jarvis.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ToggleButton;

import mercandalli.com.jarvis.R;
import mercandalli.com.jarvis.activity.Application;

/**
 * Created by Jonathan on 03/01/2015.
 */
public class DevFragment extends Fragment {

    private Application app;
    private View rootView;

    public DevFragment(Application app) {
        this.app = app;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_dev, container, false);
        ToggleButton buttonLED = (ToggleButton) rootView.findViewById(R.id.toggleButtonLED);
        return rootView;
    }
}
