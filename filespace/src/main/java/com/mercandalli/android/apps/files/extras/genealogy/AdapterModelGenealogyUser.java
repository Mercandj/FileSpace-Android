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
package com.mercandalli.android.apps.files.extras.genealogy;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.mercandalli.android.apps.files.R;

import java.util.ArrayList;
import java.util.List;

public class AdapterModelGenealogyUser extends RecyclerView.Adapter<AdapterModelGenealogyUser.ViewHolder> {

    private final Activity mActivity;
    public List<ModelGenealogyPerson> users;
    OnItemClickListener mItemClickListener;
    OnItemLongClickListener mItemLongClickListener;
    private IModelGenealogyUserListener moreListener;
    private boolean isTree;

    public AdapterModelGenealogyUser(Activity activity, List<ModelGenealogyPerson> users, IModelGenealogyUserListener moreListener, boolean isTree) {
        this.mActivity = activity;
        this.users = users;
        this.moreListener = moreListener;
        this.isTree = isTree;
    }

    @Override
    public AdapterModelGenealogyUser.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (!isTree)
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.tab_file, parent, false), viewType);
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.tab_genealogy_small, parent, false), viewType);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        if (position < users.size()) {
            final ModelGenealogyPerson user = users.get(position);

            viewHolder.title.setText(user.getAdapterTitle());
            viewHolder.subtitle.setText(user.getAdapterSubtitle());
            viewHolder.more.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (moreListener != null)
                        moreListener.execute(user);
                }
            });

            if (!user.is_man)
                viewHolder.icon.setImageResource(R.drawable.file_video);
            else
                viewHolder.icon.setImageResource(R.drawable.file_default);

            if (user.selected)
                viewHolder.item.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.tab_selected));
            else
                viewHolder.item.setBackground(null);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements OnClickListener, View.OnLongClickListener {
        public TextView title, subtitle;
        public ImageView icon;
        public FrameLayout item;
        public ImageButton more;

        public ViewHolder(View itemLayoutView, int viewType) {
            super(itemLayoutView);
            item = (FrameLayout) itemLayoutView.findViewById(R.id.item);
            title = (TextView) itemLayoutView.findViewById(R.id.title);
            subtitle = (TextView) itemLayoutView.findViewById(R.id.subtitle);
            icon = (ImageView) itemLayoutView.findViewById(R.id.tab_icon);
            more = (ImageButton) itemLayoutView.findViewById(R.id.more);
            itemLayoutView.setOnClickListener(this);
            itemLayoutView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null)
                mItemClickListener.onItemClick(v, getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            if (mItemLongClickListener != null)
                return mItemLongClickListener.onItemLongClick(v, getAdapterPosition());
            return false;
        }
    }

    @Override
    public int getItemCount() {
        return users.size();
    }


    public void remplaceList(ArrayList<ModelGenealogyPerson> list) {
        users.clear();
        users.addAll(0, list);
        notifyDataSetChanged();
    }

    public void addFirst(ArrayList<ModelGenealogyPerson> list) {
        users.addAll(0, list);
        notifyDataSetChanged();
    }

    public void addLast(ArrayList<ModelGenealogyPerson> list) {
        users.addAll(users.size(), list);
        notifyDataSetChanged();
    }

    public void addItem(ModelGenealogyPerson name, int position) {
        this.users.add(position, name);
        this.notifyItemInserted(position);
    }

    public void removeAll() {
        int size = users.size();
        if (size > 0) {
            users = new ArrayList<>();
            this.notifyItemRangeInserted(0, size - 1);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position < users.size())
            return users.get(position).viewType;
        return 0;
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public interface OnItemLongClickListener {
        public boolean onItemLongClick(View view, int position);
    }

    public void setOnItemLongClickListener(final OnItemLongClickListener mItemLongClickListener) {
        this.mItemLongClickListener = mItemLongClickListener;
    }
}
