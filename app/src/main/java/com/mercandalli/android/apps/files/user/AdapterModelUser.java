/**
 * This file is part of FileSpace for Android, an app for managing your server (files, talks...).
 * <p/>
 * Copyright (c) 2014-2015 FileSpace for Android contributors (http://mercandalli.com)
 * <p/>
 * LICENSE:
 * <p/>
 * FileSpace for Android is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation, either version 2 of the License, or (at your option) any
 * later version.
 * <p/>
 * FileSpace for Android is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * @author Jonathan Mercandalli
 * @license http://www.gnu.org/licenses/gpl.html
 * @copyright 2014-2015 FileSpace for Android contributors (http://mercandalli.com)
 */
package com.mercandalli.android.apps.files.user;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.mercandalli.android.apps.files.R;
import com.mercandalli.android.apps.files.common.listener.IModelUserListener;
import com.mercandalli.android.apps.files.main.Config;

import java.util.ArrayList;
import java.util.List;

public class AdapterModelUser extends RecyclerView.Adapter<AdapterModelUser.ViewHolder> {

    private List<UserModel> mUsers;
    private OnItemClickListener mItemClickListener;
    private OnItemLongClickListener mItemLongClickListener;
    private IModelUserListener mMoreListener;

    public AdapterModelUser(List<UserModel> users, IModelUserListener moreListener) {
        mUsers = users;
        mMoreListener = moreListener;
    }

    @Override
    public AdapterModelUser.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.tab_user, parent, false), viewType);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        if (position < mUsers.size()) {
            final UserModel user = mUsers.get(position);

            viewHolder.title.setText(user.getAdapterTitle());
            viewHolder.subtitle.setText(user.getAdapterSubtitle());

            if (user.mPictureUrl != null) {
                Glide.with(viewHolder.title.getContext())
                        .load(new GlideUrl(user.mPictureUrl, new LazyHeaders.Builder()
                                .addHeader("Authorization", "Basic " + Config.getUserToken())
                                .build()))
                        .into(viewHolder.icon);
            }

            viewHolder.more.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mMoreListener != null) {
                        mMoreListener.execute(user);
                    }
                }
            });
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements OnClickListener, View.OnLongClickListener {
        public TextView title, subtitle;
        public ImageView icon;
        public RelativeLayout item, more;

        public ViewHolder(View itemLayoutView, int viewType) {
            super(itemLayoutView);
            item = (RelativeLayout) itemLayoutView.findViewById(R.id.item);
            title = (TextView) itemLayoutView.findViewById(R.id.title);
            subtitle = (TextView) itemLayoutView.findViewById(R.id.subtitle);
            icon = (ImageView) itemLayoutView.findViewById(R.id.tab_icon);
            more = (RelativeLayout) itemLayoutView.findViewById(R.id.more);
            itemLayoutView.setOnClickListener(this);
            itemLayoutView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(v, getPosition());
            }
        }

        @Override
        public boolean onLongClick(View v) {
            return mItemLongClickListener != null && mItemLongClickListener.onItemLongClick(v, getPosition());
        }
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }


    public void remplaceList(ArrayList<UserModel> list) {
        mUsers.clear();
        mUsers.addAll(0, list);
        notifyDataSetChanged();
    }

    public void addFirst(ArrayList<UserModel> list) {
        mUsers.addAll(0, list);
        notifyDataSetChanged();
    }

    public void addLast(ArrayList<UserModel> list) {
        mUsers.addAll(mUsers.size(), list);
        notifyDataSetChanged();
    }

    public void addItem(UserModel name, int position) {
        this.mUsers.add(position, name);
        this.notifyItemInserted(position);
    }

    public void removeAll() {
        int size = mUsers.size();
        if (size > 0) {
            mUsers = new ArrayList<>();
            this.notifyItemRangeInserted(0, size - 1);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public interface OnItemLongClickListener {
        boolean onItemLongClick(View view, int position);
    }

    public void setOnItemLongClickListener(final OnItemLongClickListener mItemLongClickListener) {
        this.mItemLongClickListener = mItemLongClickListener;
    }
}
