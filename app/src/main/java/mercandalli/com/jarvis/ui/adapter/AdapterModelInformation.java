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
package mercandalli.com.jarvis.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import mercandalli.com.jarvis.ui.activity.Application;
import mercandalli.com.jarvis.util.FontUtils;
import mercandalli.com.jarvis.R;
import mercandalli.com.jarvis.config.Const;
import mercandalli.com.jarvis.model.ModelInformation;

public class AdapterModelInformation extends RecyclerView.Adapter<AdapterModelInformation.ViewHolder> {
	private List<ModelInformation> itemsData;
	OnItemClickListener mItemClickListener;
	Application app;
	 
    public AdapterModelInformation(Application app, List<ModelInformation> itemsData) {
        this.itemsData = itemsData;
        this.app = app;
    }
    
    @Override
    public AdapterModelInformation.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType== Const.TAB_VIEW_TYPE_SECTION)
    		return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.tab_information_section, parent, false), viewType);
    	return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.tab_information, parent, false), viewType);
    }
    
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
    	ModelInformation model = itemsData.get(position);
        switch(model.viewType) {
    	case Const.TAB_VIEW_TYPE_NORMAL:
    		viewHolder.title.setText(""+model.title);
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
         
        public ViewHolder(View itemLayoutView, int viewType) {
            super(itemLayoutView);            
            switch(viewType) {
        	case Const.TAB_VIEW_TYPE_NORMAL:
        		item = (RelativeLayout) itemLayoutView.findViewById(R.id.item);
                title = (TextView) itemLayoutView.findViewById(R.id.title);
                value = (TextView) itemLayoutView.findViewById(R.id.value);
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
    
    public void addItem(ModelInformation name, int position) {
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
        public void onItemClick(View view , int position);
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
