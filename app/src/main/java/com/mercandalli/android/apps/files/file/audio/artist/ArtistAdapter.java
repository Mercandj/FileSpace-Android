package com.mercandalli.android.apps.files.file.audio.artist;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.mercandalli.android.library.mainlibrary.precondition.Preconditions;

import java.util.ArrayList;
import java.util.List;

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ViewHolder> {

    private final List<Artist> mArtists = new ArrayList<>();

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(new ArtistCard(parent.getContext()));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mArtistCard.setArtist(mArtists.get(position));
    }

    @Override
    public int getItemCount() {
        return mArtists.size();
    }

    public void addAll(final List<Artist> artists) {
        Preconditions.checkNotNull(artists);
        mArtists.clear();
        mArtists.addAll(artists);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private ArtistCard mArtistCard;

        public ViewHolder(ArtistCard artistCard) {
            super(artistCard);
            mArtistCard = artistCard;
        }
    }
}
