/**
 * This file is part of Jarvis for Android, an app for managing your server (files, talks...).
 *
 * Copyright (c) 2014-2015 Jarvis for Android contributors (http://mercandalli.com)
 *
 * LICENSE:
 *
 * Jarvis for Android is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation, either version 2 of the License, or (at your option) any
 * later version.
 *
 * Jarvis for Android is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * @author Jonathan Mercandalli
 * @license http://www.gnu.org/licenses/gpl.html
 * @copyright 2014-2015 Jarvis for Android contributors (http://mercandalli.com)
 */
package mercandalli.com.filespace.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import mercandalli.com.filespace.R;
import mercandalli.com.filespace.config.Const;
import mercandalli.com.filespace.model.ModelUserConnection;
import mercandalli.com.filespace.ui.activity.Application;
import mercandalli.com.filespace.util.FontUtils;

public class AdapterModelUserConnection extends RecyclerView.Adapter<AdapterModelUserConnection.ViewHolder> {
	private List<ModelUserConnection> itemsData;
	OnItemClickListener mItemClickListener;
	Application app;

    public AdapterModelUserConnection(Application app, List<ModelUserConnection> itemsData) {
        this.itemsData = itemsData;
        this.app = app;
    }

    @Override
    public AdapterModelUserConnection.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType== Const.TAB_VIEW_TYPE_SECTION)
    		return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.tab_information_section, parent, false), viewType);
    	return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.tab_user_connection, parent, false), viewType);
    }
    
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        ModelUserConnection model = itemsData.get(position);
        switch(model.viewType) {
    	case Const.TAB_VIEW_TYPE_NORMAL:
    		viewHolder.title.setText("#"+model.id_user+"  "+model.title);
            viewHolder.value.setText(""+model.value);
    		break;
    	case Const.TAB_VIEW_TYPE_SECTION:
    		viewHolder.title.setText(""+model.title);
    		FontUtils.applyFont(app, viewHolder.title, "fonts/MYRIADAB.TTF");
    		break;
    	}
    }
    
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {        
        public TextView title, value;
        public RelativeLayout item;
        public ImageView icon;
         
        public ViewHolder(View itemLayoutView, int viewType) {
            super(itemLayoutView);            
            switch(viewType) {
        	case Const.TAB_VIEW_TYPE_NORMAL:
        		item = (RelativeLayout) itemLayoutView.findViewById(R.id.item);
                title = (TextView) itemLayoutView.findViewById(R.id.title);
                value = (TextView) itemLayoutView.findViewById(R.id.value);
                icon = (ImageView) itemLayoutView.findViewById(R.id.icon);
        		break;
        	case Const.TAB_VIEW_TYPE_SECTION:
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
    
    public void addItem(ModelUserConnection name, int position) {
    	itemsData.add(position, name);
        notifyItemInserted(position);
    }
     
    public void removeItem(int position) {
    	if(itemsData.size()<=0)
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
    	if(position<itemsData.size())
    		return itemsData.get(position).viewType;
    	return 0;
    }
}
