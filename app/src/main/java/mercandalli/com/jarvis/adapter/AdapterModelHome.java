package mercandalli.com.jarvis.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import mercandalli.com.jarvis.Font;
import mercandalli.com.jarvis.R;
import mercandalli.com.jarvis.activity.Application;
import mercandalli.com.jarvis.config.Const;
import mercandalli.com.jarvis.model.ModelHome;

public class AdapterModelHome extends RecyclerView.Adapter<AdapterModelHome.ViewHolder> {
	private List<ModelHome> itemsData;
	OnItemClickListener mItemClickListener;
	Application app;

    public AdapterModelHome(Application app, List<ModelHome> itemsData) {
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
        if(viewType == Const.TAB_VIEW_TYPE_HOME_INFORMATION_SHORT)
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.tab_home_information_short, parent, false), viewType);
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.tab_information, parent, false), viewType);
    }
    
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        final ModelHome model = itemsData.get(position);
        switch(model.viewType) {
    	case Const.TAB_VIEW_TYPE_NORMAL:
    		viewHolder.title1.setText(""+model.title1);
            viewHolder.title2.setText(""+model.title2);
    		break;
    	case Const.TAB_VIEW_TYPE_SECTION:
    		viewHolder.title1.setText(""+model.title1);
    		Font.applyFont(app, viewHolder.title1, "fonts/Roboto-Medium.ttf");
    		break;
        case Const.TAB_VIEW_TYPE_TWO_BUTTONS:
            viewHolder.button1.setText(""+model.title1);
            viewHolder.button2.setText(""+model.title2);
            if(model.listener1 != null)
                viewHolder.button1.setOnClickListener(model.listener1);
            if(model.listener2 != null)
                viewHolder.button2.setOnClickListener(model.listener2);
            Font.applyFont(app, viewHolder.button1, "fonts/Roboto-Medium.ttf");
            Font.applyFont(app, viewHolder.button2, "fonts/Roboto-Medium.ttf");
            break;
        case Const.TAB_VIEW_TYPE_HOME_INFORMATION:
        case Const.TAB_VIEW_TYPE_HOME_INFORMATION_SHORT:
            viewHolder.title1.setText("" + model.title1);
            viewHolder.title2.setText(model.title2);
            Font.applyFont(app, viewHolder.title1, "fonts/Roboto-Medium.ttf");
            Font.applyFont(app, viewHolder.title2, "fonts/Roboto-Regular.ttf");
            Font.applyFont(app, viewHolder.button1, "fonts/Roboto-Medium.ttf");
            if(model.listenerHome1 != null)
                viewHolder.button1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        model.listenerHome1.execute(model);
                    }
                });
            break;
    	}
    }
    
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {        
        public TextView title1, title2;
        public Button button1, button2;
        public RelativeLayout item;
         
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
            case Const.TAB_VIEW_TYPE_HOME_INFORMATION_SHORT:
                title1 = (TextView) itemLayoutView.findViewById(R.id.title);
                title2 = (TextView) itemLayoutView.findViewById(R.id.content);
                button1 = (Button) itemLayoutView.findViewById(R.id.button);
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
