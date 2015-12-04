package com.mercandalli.android.apps.files.common.view.divider;

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
        final int position = parent.getChildLayoutPosition(view);

        outRect.bottom = mSpace;

        // Add top margin only for the first item to avoid double space between items
        if (position < mNbItemsPerRow) {
            outRect.top = mSpace;
        }

        if (mNbItemsPerRow < 2) {
            outRect.left = mSpace;
            outRect.right = mSpace;
        }
        else {
            final int n = position % mNbItemsPerRow;
            if (n == 0) {
                outRect.left = mSpace;
                outRect.right = mSpace / 2;
            }
            else if (n == mNbItemsPerRow - 1) {
                outRect.left = mSpace / 2;
                outRect.right = mSpace;
            }
            else {
                outRect.left = mSpace / 2;
                outRect.right = mSpace / 2;
            }
        }
    }
}
