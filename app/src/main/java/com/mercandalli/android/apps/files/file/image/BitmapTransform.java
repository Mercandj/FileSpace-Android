package com.mercandalli.android.apps.files.file.image;

import android.graphics.Bitmap;

import com.squareup.picasso.Transformation;

/**
 * Transform the loaded image to avoid OutOfMemoryException
 */
public class BitmapTransform implements Transformation {

    private final int mMaxWidth;
    private final int mMaxHeight;

    public BitmapTransform(final int maxWidth, final int maxHeight) {
        mMaxWidth = maxWidth;
        mMaxHeight = maxHeight;
    }

    @Override
    public Bitmap transform(final Bitmap source) {
        final int targetWidth, targetHeight;
        final double aspectRatio;

        if (source.getWidth() > source.getHeight()) {
            targetWidth = mMaxWidth;
            aspectRatio = (double) source.getHeight() / (double) source.getWidth();
            targetHeight = (int) (targetWidth * aspectRatio);
        } else {
            targetHeight = mMaxHeight;
            aspectRatio = (double) source.getWidth() / (double) source.getHeight();
            targetWidth = (int) (targetHeight * aspectRatio);
        }

        final Bitmap result = Bitmap.createScaledBitmap(source, targetWidth, targetHeight, false);
        if (result != source) {
            source.recycle();
        }
        return result;
    }

    @Override
    public String key() {
        return mMaxWidth + "x" + mMaxHeight;
    }

}