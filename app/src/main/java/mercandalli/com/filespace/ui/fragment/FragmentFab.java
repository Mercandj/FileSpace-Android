package mercandalli.com.filespace.ui.fragment;

import android.graphics.drawable.Drawable;
import android.support.design.widget.FloatingActionButton;

import mercandalli.com.filespace.listener.IListener;

/**
 * Created by Jonathan on 17/09/2015.
 */
public abstract class FragmentFab extends Fragment {
    protected IListener refreshFab;
    public FragmentFab() {
        super();
    }
    public void setRefreshFab(IListener refreshFab) {
        this.refreshFab = refreshFab;
    }
    public abstract void onFabClick(int fab_id, FloatingActionButton fab);
    public abstract boolean isFabVisible(int fab_id);
    public abstract Drawable getFabDrawable(int fab_id);
    public void refreshFab() {
        if(refreshFab != null)
            refreshFab.execute();
    }
}
