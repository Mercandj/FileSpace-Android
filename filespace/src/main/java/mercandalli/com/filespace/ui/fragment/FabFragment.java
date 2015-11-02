package mercandalli.com.filespace.ui.fragment;

import android.support.design.widget.FloatingActionButton;

import mercandalli.com.filespace.listener.IListener;

/**
 * Manage the floating button.
 */
public abstract class FabFragment extends BackFragment {
    protected RefreshFabCallback refreshFab;

    public FabFragment() {
        super();
    }

    public void setRefreshFab(RefreshFabCallback refreshFab) {
        this.refreshFab = refreshFab;
    }

    public abstract void onFabClick(int fab_id, FloatingActionButton fab);

    public abstract boolean isFabVisible(int fab_id);

    public abstract int getFabImageResource(int fab_id);

    public void refreshFab() {
        if (refreshFab != null)
            refreshFab.onRefreshFab();
    }

    public interface RefreshFabCallback{
        void onRefreshFab();
    }
}
