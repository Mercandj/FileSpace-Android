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
package mercandalli.com.filespace.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import mercandalli.com.filespace.R;
import mercandalli.com.filespace.config.Const;
import mercandalli.com.filespace.models.ModelHome;
import mercandalli.com.filespace.ui.activities.ApplicationActivity;
import mercandalli.com.filespace.utils.FontUtils;

public class AdapterModelHome extends RecyclerView.Adapter<AdapterModelHome.ViewHolder> {
	private List<ModelHome> itemsData;
	OnItemClickListener mItemClickListener;
	ApplicationActivity app;

    public AdapterModelHome(ApplicationActivity app, List<ModelHome> itemsData) {
        this.itemsData = itemsData;
        this.app = app;
    }

    @Override
    public AdapterModelHome.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == Const.TAB_VIEW_TYPE_SECTION)
    		return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.tab_information_section, parent, false), viewType);
        if(viewType == Const.TAB_VIEW_TYPE_TWO_BUTTONS)
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.tab_two_buttons, parent, false), viewType);
        if(viewType == Const.TAB_VIEW_TYPE_HOME_INFORMATION)
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.tab_home_information, parent, false), viewType);
        if(viewType == Const.TAB_VIEW_TYPE_HOME_INFORMATION_FORM)
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.tab_home_information_form, parent, false), viewType);
        if(viewType == Const.TAB_VIEW_TYPE_HOME_IMAGE)
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.tab_home_image, parent, false), viewType);
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.tab_information, parent, false), viewType);
    }
    
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        final ModelHome model = itemsData.get(position);
        switch(model.viewType) {
    	case Const.TAB_VIEW_TYPE_NORMAL:
    		viewHolder.title1.setText(""+model.getTitle1());
            viewHolder.title2.setText(""+model.getTitle2());
    		break;
    	case Const.TAB_VIEW_TYPE_SECTION:
    		viewHolder.title1.setText(""+model.getTitle1());
    		FontUtils.applyFont(app, viewHolder.title1, "fonts/Roboto-Medium.ttf");
    		break;
        case Const.TAB_VIEW_TYPE_TWO_BUTTONS:
            viewHolder.button1.setText(""+model.getTitle1());
            viewHolder.button2.setText(""+model.getTitle2());
            if(model.listener1 != null)
                viewHolder.button1.setOnClickListener(model.listener1);
            if(model.listener2 != null)
                viewHolder.button2.setOnClickListener(model.listener2);
            FontUtils.applyFont(app, viewHolder.button1, "fonts/Roboto-Medium.ttf");
            FontUtils.applyFont(app, viewHolder.button2, "fonts/Roboto-Medium.ttf");
            break;
        case Const.TAB_VIEW_TYPE_HOME_INFORMATION:
            viewHolder.title1.setText("" + model.getTitle1());
            viewHolder.title2.setText(model.getTitle2());
            FontUtils.applyFont(app, viewHolder.title1, "fonts/Roboto-Medium.ttf");
            FontUtils.applyFont(app, viewHolder.title2, "fonts/Roboto-Regular.ttf");
            FontUtils.applyFont(app, viewHolder.button1, "fonts/Roboto-Medium.ttf");
            FontUtils.applyFont(app, viewHolder.button2, "fonts/Roboto-Medium.ttf");
            if(model.listenerHome1 != null)
                viewHolder.button1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        model.listenerHome1.execute(model);
                    }
                });
            if(model.listenerHome2 != null)
                viewHolder.button2.setVisibility(View.VISIBLE);
                viewHolder.button2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        model.listenerHome2.execute(model);
                    }
                });
            break;
        case Const.TAB_VIEW_TYPE_HOME_INFORMATION_FORM:
            viewHolder.title1.setText("" + model.getTitle1());
            FontUtils.applyFont(app, viewHolder.title1, "fonts/Roboto-Medium.ttf");
            FontUtils.applyFont(app, viewHolder.button1, "fonts/Roboto-Medium.ttf");
            FontUtils.applyFont(app, viewHolder.button2, "fonts/Roboto-Medium.ttf");
            if(model.listenerHome1 != null)
                viewHolder.button1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        model.listenerHome1.execute(model);
                    }
                });

            if(model.modelForm.input1Text == null) viewHolder.input1.setVisibility(View.GONE);
            else viewHolder.input1Text.setText(model.modelForm.input1Text);
            if(model.modelForm.input1EditText != null) viewHolder.input1EditText.setText(model.modelForm.input1EditText);
            else viewHolder.input1EditText.setText("");

            if(model.modelForm.input2Text == null) viewHolder.input2.setVisibility(View.GONE);
            else viewHolder.input2Text.setText(model.modelForm.input2Text);
            if(model.modelForm.input2EditText != null) viewHolder.input1EditText.setText(model.modelForm.input2EditText);
            else viewHolder.input2EditText.setText("");

            if(model.modelForm.input3Text == null) viewHolder.input3.setVisibility(View.GONE);
            else viewHolder.input3Text.setText(model.modelForm.input3Text);
            if(model.modelForm.input3EditText != null) viewHolder.input1EditText.setText(model.modelForm.input3EditText);
            else viewHolder.input3EditText.setText("");

            if(model.modelForm != null)
                viewHolder.button2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        model.modelForm.input1EditText = viewHolder.input1EditText.getText().toString();
                        model.modelForm.input2EditText = viewHolder.input2EditText.getText().toString();
                        model.modelForm.input3EditText = viewHolder.input3EditText.getText().toString();
                        model.modelForm.send();
                        if(model.listenerHome1 != null)
                            model.listenerHome1.execute(model);
                    }
                });
            break;
        case Const.TAB_VIEW_TYPE_HOME_IMAGE:
            viewHolder.title1.setText("" + model.getTitle1());
            viewHolder.title2.setText(model.getTitle2());
            viewHolder.image.setImageBitmap(model.image);
            FontUtils.applyFont(app, viewHolder.title1, "fonts/Roboto-Medium.ttf");
            FontUtils.applyFont(app, viewHolder.title2, "fonts/Roboto-Regular.ttf");
            FontUtils.applyFont(app, viewHolder.button1, "fonts/Roboto-Medium.ttf");
            FontUtils.applyFont(app, viewHolder.button2, "fonts/Roboto-Medium.ttf");
            if(model.listenerHome1 != null) {
                viewHolder.button1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        model.listenerHome1.execute(model);
                    }
                });
            }
            else
                viewHolder.button1.setVisibility(View.GONE);
            if(model.listenerHome2 != null) {
                viewHolder.button2.setVisibility(View.VISIBLE);
                viewHolder.button2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        model.listenerHome2.execute(model);
                    }
                });
            }
            else
                viewHolder.button2.setVisibility(View.GONE);
            break;
    	}
    }
    
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {        
        public TextView title1, title2, input1Text, input2Text, input3Text;
        public EditText input1EditText, input2EditText, input3EditText;
        public Button button1, button2;
        public RelativeLayout item, input1, input2, input3;
        public ImageView image;
         
        public ViewHolder(View itemLayoutView, int viewType) {
            super(itemLayoutView);            
            switch(viewType) {
        	case Const.TAB_VIEW_TYPE_NORMAL:
        		item = (RelativeLayout) itemLayoutView.findViewById(R.id.item);
                title1 = (TextView) itemLayoutView.findViewById(R.id.title);
                title2 = (TextView) itemLayoutView.findViewById(R.id.value);
        		break;
        	case Const.TAB_VIEW_TYPE_SECTION:
                title1 = (TextView) itemLayoutView.findViewById(R.id.title);
        		break;
            case Const.TAB_VIEW_TYPE_TWO_BUTTONS:
                button1 = (Button) itemLayoutView.findViewById(R.id.button1);
                button2 = (Button) itemLayoutView.findViewById(R.id.button2);
                break;
            case Const.TAB_VIEW_TYPE_HOME_INFORMATION:
                title1 = (TextView) itemLayoutView.findViewById(R.id.title);
                title2 = (TextView) itemLayoutView.findViewById(R.id.content);
                button1 = (Button) itemLayoutView.findViewById(R.id.button1);
                button2 = (Button) itemLayoutView.findViewById(R.id.button2);
                break;
            case Const.TAB_VIEW_TYPE_HOME_INFORMATION_FORM:
                title1 = (TextView) itemLayoutView.findViewById(R.id.title);
                button1 = (Button) itemLayoutView.findViewById(R.id.button);
                button2 = (Button) itemLayoutView.findViewById(R.id.buttonSend);
                input1 = (RelativeLayout) itemLayoutView.findViewById(R.id.input1);
                input2 = (RelativeLayout) itemLayoutView.findViewById(R.id.input2);
                input3 = (RelativeLayout) itemLayoutView.findViewById(R.id.input3);
                input1Text = (TextView) itemLayoutView.findViewById(R.id.input1Text);
                input2Text = (TextView) itemLayoutView.findViewById(R.id.input2Text);
                input3Text = (TextView) itemLayoutView.findViewById(R.id.content);
                input1EditText = (EditText) itemLayoutView.findViewById(R.id.input1EditText);
                input2EditText = (EditText) itemLayoutView.findViewById(R.id.input2EditText);
                input3EditText = (EditText) itemLayoutView.findViewById(R.id.input3EditText);
                break;
            case Const.TAB_VIEW_TYPE_HOME_IMAGE:
                title1 = (TextView) itemLayoutView.findViewById(R.id.title);
                title2 = (TextView) itemLayoutView.findViewById(R.id.content);
                image = (ImageView) itemLayoutView.findViewById(R.id.image);
                button1 = (Button) itemLayoutView.findViewById(R.id.button);
                button2 = (Button) itemLayoutView.findViewById(R.id.buttonSend);
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
    
    public void addItem(ModelHome name, int position) {
    	itemsData.add(position, name);
        notifyItemInserted(position);
    }

    public void removeItem(int position) {
    	if(itemsData.size()<=0)
    		return;
    	itemsData.remove(position);
        notifyItemRemoved(position);
    }

    public void removeItem(ModelHome model) {
        if(model==null)
            return;
        for(int i=0; i<itemsData.size();i++)
            if (itemsData.get(i).equals(model)) {
                itemsData.remove(i);
                notifyItemRemoved(i);
            }
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

    public int size() {
        return itemsData.size();
    }

    public ModelHome get(int i) {
        if(i<this.size())
            return itemsData.get(i);
        return null;
    }
}
