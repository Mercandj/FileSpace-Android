package com.mercandalli.android.apps.files.file.audio.album;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mercandalli.android.apps.files.R;
import com.mercandalli.android.library.base.view.GenericRecyclerAdapter;

public class AlbumCard extends CardView implements
        GenericRecyclerAdapter.GenericView<Album>,
        View.OnClickListener {

    @Nullable
    private Album mAlbum;

    public AlbumCard(Context context) {
        super(context);
        init(context);
    }

    public AlbumCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AlbumCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @NonNull
    @Override
    public GenericRecyclerAdapter.GenericView<Album> newInstance(@NonNull final Context context) {
        return new AlbumCard(context);
    }

    @Override
    public void setModel(@NonNull final Album album) {
        mAlbum = album;
        ((TextView) findViewById(R.id.view_album_card_title)).setText("Name: " + album.getName());
        ((TextView) findViewById(R.id.view_album_card_subtitle)).setText("Size: " + album.getFilePaths().size());
    }

    @Override
    public void onClick(View v) {

    }

    /**
     * Initialize this {@link AlbumCard}.
     *
     * @param context the {@link Context} passed in the constructor.
     */
    private void init(Context context) {
        inflate(context, R.layout.view_album_card_view, this);
        setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        setUseCompatPadding(true);
        setCardBackgroundColor(Color.WHITE);
        setOnClickListener(this);
    }
}
