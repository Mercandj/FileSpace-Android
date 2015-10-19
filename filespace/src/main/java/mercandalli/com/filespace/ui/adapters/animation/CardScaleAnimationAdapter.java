package mercandalli.com.filespace.ui.adapters.animation;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * {@link RecyclerView} adapter with animation.
 */
public class CardScaleAnimationAdapter extends CardAnimationAdapter {
    private final float mFrom;
    private RecyclerView recyclerView;

    public CardScaleAnimationAdapter(RecyclerView.Adapter adapter, int cardPerLine) {
        this(adapter, cardPerLine, 0.0F);
    }

    public CardScaleAnimationAdapter(RecyclerView.Adapter adapter, int cardPerLine, float from) {
        super(adapter, cardPerLine);
        this.mFrom = from;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = super.mAdapter.onCreateViewHolder(parent, viewType);
        viewHolder.itemView.setScaleX(0);
        viewHolder.itemView.setScaleY(0);
        return viewHolder;
    }

    protected Animator[] getAnimators(View view) {
        view.setScaleX(0);
        view.setScaleY(0);
        return new Animator[]{ObjectAnimator.ofFloat(view, "scaleX", this.mFrom, 1.0F), ObjectAnimator.ofFloat(view, "scaleY", this.mFrom, 1.0F)};
    }

    protected RecyclerView getRecyclerView() {
        return recyclerView;
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }
}