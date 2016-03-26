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

    public void setFileModel(final FileModel fileModel) {
        Preconditions.checkNotNull(fileModel);
        final File file = fileModel.getFile();
        Preconditions.checkNotNull(file);

        getDropboxIMGSize(file);

        if (FileTypeModelENUM.IMAGE.type.equals(fileModel.getType())) {
            mImageView.setBackgroundColor(Color.TRANSPARENT);
            Picasso.with(getContext())
                    .load(file)
                    .placeholder(R.drawable.placeholder_picture)
                    .into(mImageView);
        } else {
            throw new IllegalStateException(FileImageCardView.class.getName() + ": not an image.");
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mWidth = getMeasuredWidth();
    }

    /**
     * @param file
     * @return Height
     */
    private void getDropboxIMGSize(final File file) {
        BitmapFactory.Options options = new BitmapFactory.Options();
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
}
