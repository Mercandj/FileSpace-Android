package mercandalli.com.filespace.ui.view.divider;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Jonathan on 31/10/2015.
 */
public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
    private final int mSpace;
    private final int mNbItemsPerRow;

    public SpacesItemDecoration(final int space) {
        this(space, 1);
    }

    public SpacesItemDecoration(final int space, final int nbItemsPerRow) {
        mSpace = space;
        mNbItemsPerRow = nbItemsPerRow;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.left = mSpace;
        outRect.right = mSpace;
        outRect.bottom = mSpace;

        // Add top margin only for the first item to avoid double space between items
        if (parent.getChildLayoutPosition(view) < mNbItemsPerRow) {
            outRect.top = mSpace;
        }
    }
}
