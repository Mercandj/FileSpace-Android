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
package com.mercandalli.android.filespace.settings;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.List;

import com.mercandalli.android.filespace.R;
import com.mercandalli.android.filespace.main.Constants;
import com.mercandalli.android.filespace.main.ApplicationCallback;
import com.mercandalli.android.filespace.common.util.FontUtils;

public class AdapterModelSetting extends RecyclerView.Adapter<AdapterModelSetting.ViewHolder> {
    private List<ModelSetting> itemsData;
    OnItemClickListener mItemClickListener;
    Activity mActivity;
    ApplicationCallback mApplicationCallback;

    public AdapterModelSetting(Activity activity, ApplicationCallback applicationCallback, List<ModelSetting> itemsData) {
        this.itemsData = itemsData;
        this.mActivity = activity;
        this.mApplicationCallback = applicationCallback;
    }

    @Override
    public AdapterModelSetting.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == Constants.TAB_VIEW_TYPE_SECTION)
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.tab_setting_section, parent, false), viewType);
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.tab_setting, parent, false), viewType);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        ModelSetting model = itemsData.get(position);
        switch (model.viewType) {
            case Constants.TAB_VIEW_TYPE_NORMAL:
                viewHolder.title.setText("" + model.title);
                if (model.toggleButtonListener == null)
                    viewHolder.toggleButton.setVisibility(View.GONE);
                else {
                    viewHolder.toggleButton.setVisibility(View.VISIBLE);
                    viewHolder.toggleButton.setChecked(model.toggleButtonInitValue);
                    viewHolder.toggleButton.setOnCheckedChangeListener(model.toggleButtonListener);
                }
                if (model.subtitle != null) {
                    viewHolder.subtitle.setVisibility(View.VISIBLE);
                    viewHolder.subtitle.setText(model.subtitle);
                } else
                    viewHolder.subtitle.setVisibility(View.GONE);
                break;
            case Constants.TAB_VIEW_TYPE_SECTION:
                viewHolder.title.setText("" + model.title);
                FontUtils.applyFont(mActivity, viewHolder.title, "fonts/MYRIADAB.TTF");
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
            if (mItemClickListener != null)
                mItemClickListener.onItemClick(v, getPosition());
        }
    }

    @Override
    public int getItemCount() {
        return itemsData.size();
    }

    public void addItem(ModelSetting name, int position) {
        itemsData.add(position, name);
        notifyItemInserted(position);
    }

    public void removeItem(int position) {
        if (itemsData.size() <= 0)
            return;
        itemsData.remove(position);
        notifyItemRemoved(position);
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        if (position < itemsData.size())
            return itemsData.get(position).viewType;
        return 0;
    }
}