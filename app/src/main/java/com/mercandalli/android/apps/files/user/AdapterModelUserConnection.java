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
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mercandalli.android.apps.files.R;
import com.mercandalli.android.apps.files.main.Constants;
import com.mercandalli.android.library.baselibrary.view.ViewUtils;

import java.util.ArrayList;
import java.util.List;

public class AdapterModelUserConnection extends RecyclerView.Adapter<AdapterModelUserConnection.ViewHolder> {

    private final List<UserConnectionModel> mItemsData;
    private OnItemClickListener mItemClickListener;

    public AdapterModelUserConnection(final List<UserConnectionModel> itemsData) {
        mItemsData = new ArrayList<>(itemsData);
    }

    @Override
    public AdapterModelUserConnection.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == Constants.TAB_VIEW_TYPE_SECTION) {
            return new ViewHolder(
                    LayoutInflater.from(parent.getContext()).inflate(R.layout.tab_information_section, parent, false),
                    viewType,
                    mItemClickListener);
        }
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.tab_user_connection, parent, false),
                viewType,
                mItemClickListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        final UserConnectionModel model = mItemsData.get(position);
        switch (model.viewType) {
            case Constants.TAB_VIEW_TYPE_NORMAL:
                viewHolder.title.setText(model.getAdapterTitle());
                viewHolder.value.setText(model.getAdapterSubtitle());
                break;
            case Constants.TAB_VIEW_TYPE_SECTION:
                viewHolder.title.setText(String.format("%s", model.title));
                ViewUtils.applyFont(viewHolder.title, "fonts/MYRIADAB.TTF");
                break;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView title, value;
        public RelativeLayout item;
        public ImageView icon;
        private OnItemClickListener mItemClickListener;

        public ViewHolder(
                final View itemLayoutView,
                final int viewType,
                final OnItemClickListener itemClickListener) {
            super(itemLayoutView);
            mItemClickListener = itemClickListener;
            switch (viewType) {
                case Constants.TAB_VIEW_TYPE_NORMAL:
                    item = (RelativeLayout) itemLayoutView.findViewById(R.id.item);
                    title = (TextView) itemLayoutView.findViewById(R.id.title);
                    value = (TextView) itemLayoutView.findViewById(R.id.value);
                    icon = (ImageView) itemLayoutView.findViewById(R.id.tab_icon);
                    break;
                case Constants.TAB_VIEW_TYPE_SECTION:
                    title = (TextView) itemLayoutView.findViewById(R.id.title);
                    break;
            }
            itemLayoutView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(v, getPosition());
            }
        }
    }

    @Override
    public int getItemCount() {
        return mItemsData.size();
    }

    public void addItem(UserConnectionModel name, int position) {
        mItemsData.add(position, name);
        notifyItemInserted(position);
    }

    public void removeItem(int position) {
        if (mItemsData.size() <= 0) {
            return;
        }
        mItemsData.remove(position);
        notifyItemRemoved(position);
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener itemClickListener) {
        mItemClickListener = itemClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        if (position < mItemsData.size()) {
            return mItemsData.get(position).viewType;
        }
        return 0;
    }
}
