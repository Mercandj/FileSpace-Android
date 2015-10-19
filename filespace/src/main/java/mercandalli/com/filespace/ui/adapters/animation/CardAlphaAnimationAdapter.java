package mercandalli.com.filespace.ui.adapters.animation;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * {@link RecyclerView} adapter with animation.
 */
public class CardAlphaAnimationAdapter extends CardAnimationAdapter {
    private final float mFrom;
    private RecyclerView recyclerView;

    public CardAlphaAnimationAdapter(RecyclerView.Adapter adapter, int cardPerLine, float from) {
        super(adapter, cardPerLine);
        this.mFrom = from;
    }

    protected Animator[] getAnimators(View view) {
        view.setAlpha(0);
        return new Animator[]{ObjectAnimator.ofFloat(view, "alpha", this.mFrom, 1.0F)};
    }

    protected RecyclerView getRecyclerView() {
        return recyclerView;
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }
}