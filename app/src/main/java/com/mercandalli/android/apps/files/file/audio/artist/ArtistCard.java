package com.mercandalli.android.apps.files.file.audio.artist;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.mercandalli.android.apps.files.R;
import com.mercandalli.android.library.base.generic.GenericRecyclerAdapter;

import java.util.List;

public class ArtistCard extends CardView implements
        GenericRecyclerAdapter.GenericView<Artist>,
        View.OnClickListener {

    @Nullable
    private Artist mArtist;

    public ArtistCard(Context context) {
        super(context);
        init(context);
    }

    public ArtistCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ArtistCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @NonNull
    @Override
    public GenericRecyclerAdapter.GenericView<Artist> newInstance(@NonNull final Context context) {
        return new ArtistCard(context);
    }

    @Override
    public void setModel(@NonNull final Artist artist, @NonNull final List<Artist> list) {
        mArtist = artist;
    }

    @Override
    public void onClick(View v) {

    }

    /**
     * Initialize this {@link ArtistCard}.
     *
     * @param context the {@link Context} passed in the constructor.
     */
    private void init(Context context) {
        inflate(context, R.layout.file_audio_artist_card, this);
        setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        setUseCompatPadding(true);
        setCardBackgroundColor(Color.WHITE);
        setOnClickListener(this);
    }
}
