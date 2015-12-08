package com.mercandalli.android.apps.files.support;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class SupportCommentAdapter extends RecyclerView.Adapter<SupportCommentAdapter.ViewHolder> {

    private final List<SupportComment> mSupportComments;
    private final OnSupportCommentClickListener mOnSupportCommentClickListener;
    private final OnSupportCommentLongClickListener mOnSupportCommentLongClickListener;

    public SupportCommentAdapter(
            OnSupportCommentClickListener onSupportCommentClickListener,
            OnSupportCommentLongClickListener onSupportCommentLongClickListener) {

        mSupportComments = new ArrayList<>();
        mOnSupportCommentClickListener = onSupportCommentClickListener;
        mOnSupportCommentLongClickListener = onSupportCommentLongClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(
                new SupportCommentCardView(parent.getContext()),
                mOnSupportCommentClickListener,
                mOnSupportCommentLongClickListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mSupportCommentCardView.setSupportComment(mSupportComments.get(position));
    }

    @Override
    public int getItemCount() {
        return mSupportComments.size();
    }

    public void setSupportComments(List<SupportComment> supportComments) {
        mSupportComments.clear();
        mSupportComments.addAll(supportComments);
        notifyDataSetChanged();
    }

    public interface OnSupportCommentClickListener {

        void onSupportCommentClick(View view, int position);
    }

    public interface OnSupportCommentLongClickListener {

        boolean onSupportCommentLongClick(View view, int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        public final SupportCommentCardView mSupportCommentCardView;
        private final OnSupportCommentClickListener mOnSupportCommentClickListener;
        private final OnSupportCommentLongClickListener mOnSupportCommentLongClickListener;

        public ViewHolder(
                SupportCommentCardView supportCommentCardView,
                OnSupportCommentClickListener onSupportCommentClickListener,
                OnSupportCommentLongClickListener onSupportCommentLongClickListener) {

            super(supportCommentCardView);
            mSupportCommentCardView = supportCommentCardView;
            mOnSupportCommentClickListener = onSupportCommentClickListener;
            mOnSupportCommentLongClickListener = onSupportCommentLongClickListener;
            mSupportCommentCardView.setOnClickListener(this);
            mSupportCommentCardView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mOnSupportCommentClickListener != null) {
                mOnSupportCommentClickListener.onSupportCommentClick(v, getAdapterPosition());
            }
        }

        @Override
        public boolean onLongClick(View v) {
            return mOnSupportCommentLongClickListener != null && mOnSupportCommentLongClickListener.onSupportCommentLongClick(v, getAdapterPosition());
        }
    }
}
