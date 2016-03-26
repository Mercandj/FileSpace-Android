package com.mercandalli.android.apps.files.file.image;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mercandalli.android.apps.files.R;
import com.mercandalli.android.apps.files.file.FileModel;
import com.mercandalli.android.apps.files.file.FileTypeModelENUM;
import com.mercandalli.android.apps.files.precondition.Preconditions;
import com.squareup.picasso.Picasso;

import java.io.File;

public class FileImageCardView extends CardView {

    private ImageView mImageView;
    private int mWidth;

    public FileImageCardView(Context context) {
        super(context);
        init(context);
    }

    public FileImageCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FileImageCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
    }

    public void bindFileModel(final FileModel fileModel) {
        Preconditions.checkNotNull(fileModel);
        final File file = fileModel.getFile();
        Preconditions.checkNotNull(file);

        //syncWithImageSize(file);

        if (FileTypeModelENUM.IMAGE.type.equals(fileModel.getType())) {
            mImageView.setBackgroundColor(Color.TRANSPARENT);
            Picasso.with(getContext())
                    .load(file)
                    .transform(new BitmapTransform(600, 600))
                    .placeholder(R.drawable.placeholder_picture)
                    .into(mImageView);
        } else {
            throw new IllegalStateException(FileImageCardView.class.getName() + ": " +
                    file.getAbsolutePath() + " is not an image. FileType = " + fileModel.getType());
        }
    }

    private void init(final Context context) {
        final View rootView = inflate(context, R.layout.card_file_image, this);
        setCardBackgroundColor(Color.WHITE);
        initForeground(context);
        mImageView = (ImageView) rootView.findViewById(R.id.card_file_image_icon);
    }

    private void initForeground(Context context) {
        final TypedArray typedArray = context.obtainStyledAttributes(new int[]{R.attr.selectableItemBackground});
        final int backgroundResource = typedArray.getResourceId(0, 0);
        setForeground(ContextCompat.getDrawable(context, backgroundResource));
        typedArray.recycle();
    }

    /**
     * Sync the card height with the image.
     *
     * @param file The image file.
     */
    private void syncWithImageSize(final File file) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getAbsolutePath(), options);
        final int imageHeight = options.outHeight;
        final int imageWidth = options.outWidth;

        int height = (int) (mWidth * (1.0 * imageHeight / imageWidth));
        if (height == 0) {
            height = ViewGroup.LayoutParams.WRAP_CONTENT;
        }
        setLayoutParams(
                new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        height));
    }
}
