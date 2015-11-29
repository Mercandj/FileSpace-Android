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
package com.mercandalli.android.filespace.admin;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mercandalli.android.filespace.R;
import com.mercandalli.android.filespace.common.util.FontUtils;
import com.mercandalli.android.filespace.main.Constants;

import java.util.ArrayList;
import java.util.List;

public class AdapterModelInformation extends RecyclerView.Adapter<AdapterModelInformation.ViewHolder> {

    private final Activity mActivity;
    private final List<ModelInformation> mModelInformations;
    private OnItemClickListener mItemClickListener;

    public AdapterModelInformation(final Activity activity, List<ModelInformation> modelInformations) {
        this.mModelInformations = new ArrayList<>();
        this.mModelInformations.addAll(modelInformations);
        this.mActivity = activity;
    }

    @Override
    public AdapterModelInformation.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == Constants.TAB_VIEW_TYPE_SECTION)
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.tab_information_section, parent, false), viewType);
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.tab_information, parent, false), viewType);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        ModelInformation model = mModelInformations.get(position);
        switch (model.viewType) {
            case Constants.TAB_VIEW_TYPE_NORMAL:
                viewHolder.title.setText(String.format("%s", model.title));
                viewHolder.value.setText(String.format("%s", model.value));
                break;
            case Constants.TAB_VIEW_TYPE_SECTION:
                viewHolder.title.setText(String.format("%s", model.title));
                FontUtils.applyFont(mActivity, viewHolder.title, "fonts/MYRIADAB.TTF");
                break;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView title, value;
        public RelativeLayout item;

        public ViewHolder(View itemLayoutView, int viewType) {
            super(itemLayoutView);
            switch (viewType) {
                case Constants.TAB_VIEW_TYPE_NORMAL:
                    item = (RelativeLayout) itemLayoutView.findViewById(R.id.item);
                    title = (TextView) itemLayoutView.findViewById(R.id.title);
                    value = (TextView) itemLayoutView.findViewById(R.id.value);
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
                mItemClickListener.onItemClick(v, getAdapterPosition());
        }
    }

    @Override
    public int getItemCount() {
        return mModelInformations.size();
    }

    public void addItem(ModelInformation name, int position) {
        mModelInformations.add(position, name);
        notifyItemInserted(position);
    }

    public void removeItem(int position) {
        if (mModelInformations.size() <= 0)
            return;
        mModelInformations.remove(position);
        notifyItemRemoved(position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        if (position < mModelInformations.size())
            return mModelInformations.get(position).viewType;
        return 0;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
}
