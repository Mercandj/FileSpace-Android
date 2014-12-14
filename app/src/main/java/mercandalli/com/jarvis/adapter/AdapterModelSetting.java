package mercandalli.com.jarvis.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.List;

import mercandalli.com.jarvis.activity.Application;
import mercandalli.com.jarvis.Font;
import mercandalli.com.jarvis.R;
import mercandalli.com.jarvis.config.Const;
import mercandalli.com.jarvis.model.ModelSetting;

public class AdapterModelSetting extends RecyclerView.Adapter<AdapterModelSetting.ViewHolder> {
	private List<ModelSetting> itemsData;
	OnItemClickListener mItemClickListener;
	Application app;
	 
    public AdapterModelSetting(Application app, List<ModelSetting> itemsData) {
        this.itemsData = itemsData;
        this.app = app;
    }
    
    @Override
    public AdapterModelSetting.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    	if(viewType== Const.TAB_VIEW_TYPE_SECTION)
    		return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.tab_setting_section, parent, false), viewType);
    	return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.tab_setting, parent, false), viewType);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {        
    	ModelSetting model = itemsData.get(position);    	
    	switch(model.viewType) {
    	case Const.TAB_VIEW_TYPE_NORMAL:
            viewHolder.title.setText(""+model.title);
			if(model.toggleButtonListener==null)
				viewHolder.toggleButton.setVisibility(View.GONE);
			else {
				viewHolder.toggleButton.setVisibility(View.VISIBLE);
				viewHolder.toggleButton.setChecked(model.toggleButtonInitValue);
				viewHolder.toggleButton.setOnCheckedChangeListener(model.toggleButtonListener);
			}
    		break;
    	case Const.TAB_VIEW_TYPE_SECTION:
        	viewHolder.title.setText(""+model.title);
        	Font.applyFont(app, viewHolder.title, "fonts/MYRIADAB.TTF");
    		break;
    	}
    }
    
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {        
        public TextView title, value;
        public RelativeLayout item;
        public ToggleButton toggleButton;
         
        public ViewHolder(View itemLayoutView, int viewType) {
            super(itemLayoutView);            
            switch(viewType) {
        	case Const.TAB_VIEW_TYPE_NORMAL:
        		title = (TextView) itemLayoutView.findViewById(R.id.title);
        		item = (RelativeLayout) itemLayoutView.findViewById(R.id.item);	            
	            toggleButton = (ToggleButton) itemLayoutView.findViewById(R.id.toggleButton);
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
    
    public void addItem(ModelSetting name, int position) {
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