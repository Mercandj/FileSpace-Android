package mercandalli.com.jarvis.ui.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import mercandalli.com.jarvis.R;
import mercandalli.com.jarvis.ui.activity.Application;

/**
 * Created by Jonathan on 03/01/2015.
 */
public class ProfileFragment extends Fragment {

    private Application app;
    private View rootView;
    private ProgressBar circularProgressBar;

    public ProfileFragment(Application app) {
        this.app = app;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        this.circularProgressBar = (ProgressBar) this.rootView.findViewById(R.id.circularProgressBar);
        this.circularProgressBar.setVisibility(View.VISIBLE);

        if(this.app.isInternetConnection()) {

        }

        Bitmap icon_profile_online = app.getConfig().getUserProfiePicture();
        if(icon_profile_online!=null)
            ((ImageView) rootView.findViewById(R.id.icon)).setImageBitmap(icon_profile_online);

        return rootView;
    }

    @Override
    public boolean back() {
        return false;
    }
}
