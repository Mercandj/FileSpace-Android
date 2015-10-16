package mercandalli.com.filespace.ui.fragments;

import android.support.design.widget.FloatingActionButton;

import mercandalli.com.filespace.listeners.IListener;

/**
 * Manage the floating button.
 */
public abstract class FabFragment extends BackFragment {
    protected IListener refreshFab;

    public FabFragment() {
        super();
    }

    public void setRefreshFab(IListener refreshFab) {
        this.refreshFab = refreshFab;
    }

    public abstract void onFabClick(int fab_id, FloatingActionButton fab);

    public abstract boolean isFabVisible(int fab_id);

    public abstract int getFabImageResource(int fab_id);

    public void refreshFab() {
        if (refreshFab != null)
            refreshFab.execute();
    }
}
