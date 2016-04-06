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
package com.mercandalli.android.apps.files.settings;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.mercandalli.android.apps.files.R;
import com.mercandalli.android.apps.files.main.Constants;
import com.mercandalli.android.library.baselibrary.view.ViewUtils;

import java.util.ArrayList;
import java.util.List;

public class AdapterModelSetting extends RecyclerView.Adapter<AdapterModelSetting.ViewHolder> {

    private final List<ModelSetting> mItemsData;
    private OnItemClickListener mItemClickListener;

    public AdapterModelSetting(final List<ModelSetting> itemsData) {
        mItemsData = new ArrayList<>(itemsData);
    }

    @Override
    public AdapterModelSetting.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == Constants.TAB_VIEW_TYPE_SECTION) {
            return new ViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.tab_setting_section, parent, false), viewType);
        }
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tab_setting, parent, false), viewType);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        ModelSetting model = mItemsData.get(position);
        switch (model.viewType) {
            case Constants.TAB_VIEW_TYPE_NORMAL:
                viewHolder.title.setText(String.format("%s", model.title));
                if (model.toggleButtonListener == null) {
                    viewHolder.toggleButton.setVisibility(View.GONE);
                } else {
                    viewHolder.toggleButton.setVisibility(View.VISIBLE);
                    viewHolder.toggleButton.setChecked(model.toggleButtonInitValue);
                    viewHolder.toggleButton.setOnCheckedChangeListener(model.toggleButtonListener);
                }
                if (model.subtitle != null) {
                    viewHolder.subtitle.setVisibility(View.VISIBLE);
                    viewHolder.subtitle.setText(model.subtitle);
                } else {
                    viewHolder.subtitle.setVisibility(View.GONE);
                }
                break;
            case Constants.TAB_VIEW_TYPE_SECTION:
                viewHolder.title.setText(String.format("%s", model.title));
                ViewUtils.applyFont(viewHolder.title, "fonts/MYRIADAB.TTF");
                break;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView title, subtitle, value;
        public RelativeLayout item;
        public ToggleButton toggleButton;

        public ViewHolder(View itemLayoutView, int viewType) {
            super(itemLayoutView);
            switch (viewType) {
                case Constants.TAB_VIEW_TYPE_NORMAL:
                    title = (TextView) itemLayoutView.findViewById(R.id.title);
                    subtitle = (TextView) itemLayoutView.findViewById(R.id.subtitle);
                    item = (RelativeLayout) itemLayoutView.findViewById(R.id.item);
                    toggleButton = (ToggleButton) itemLayoutView.findViewById(R.id.toggleButton);
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

    public void addItem(ModelSetting name, int position) {
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

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        if (position < mItemsData.size()) {
            return mItemsData.get(position).viewType;
        }
        return 0;
    }
}