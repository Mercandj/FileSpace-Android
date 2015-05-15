package mercandalli.com.jarvis.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import mercandalli.com.jarvis.R;
import mercandalli.com.jarvis.ui.activity.Application;
import mercandalli.com.jarvis.listener.IModelUserListener;
import mercandalli.com.jarvis.model.ModelConversationMessage;

public class AdapterModelConnversationMessage extends RecyclerView.Adapter<AdapterModelConnversationMessage.ViewHolder> {

	private Application app;
	private List<ModelConversationMessage> users;
    OnItemClickListener mItemClickListener;
    OnItemLongClickListener mItemLongClickListener;
	private IModelUserListener moreListener;

	public AdapterModelConnversationMessage(Application app, List<ModelConversationMessage> users, IModelUserListener moreListener) {
		this.app = app;
		this.users = users;
		this.moreListener = moreListener;
	}

    @Override
    public AdapterModelConnversationMessage.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.tab_user, parent, false), viewType);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        if(position<users.size()) {
            final ModelConversationMessage user = users.get(position);

            viewHolder.title.setText(user.getAdapterTitle());
            viewHolder.subtitle.setText(user.getAdapterSubtitle());
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements OnClickListener, View.OnLongClickListener {
        public TextView title, subtitle;
        public ImageView icon, more;
        public RelativeLayout item;

        public ViewHolder(View itemLayoutView, int viewType) {
            super(itemLayoutView);
            item = (RelativeLayout) itemLayoutView.findViewById(R.id.item);
            title = (TextView) itemLayoutView.findViewById(R.id.title);
            subtitle = (TextView) itemLayoutView.findViewById(R.id.subtitle);
            icon = (ImageView) itemLayoutView.findViewById(R.id.icon);
            more = (ImageView) itemLayoutView.findViewById(R.id.more);
            itemLayoutView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null)
                mItemClickListener.onItemClick(v, getPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            if (mItemLongClickListener != null)
                return mItemLongClickListener.onItemLongClick(v, getPosition());
            return false;
        }
    }

    @Override
    public int getItemCount() {
        return users.size();
    }


    public void remplaceList(ArrayList<ModelConversationMessage> list) {
        users.clear();
        users.addAll(0, list);
        notifyDataSetChanged();
    }

    public void addFirst(ArrayList<ModelConversationMessage> list) {
        users.addAll(0, list);
        notifyDataSetChanged();
    }

    public void addLast(ArrayList<ModelConversationMessage> list) {
        users.addAll(users.size(), list);
        notifyDataSetChanged();
    }

    public void addItem(ModelConversationMessage name, int position) {
        this.users.add(position, name);
        this.notifyItemInserted(position);
    }

    public void removeAll() {
        int size = users.size();
        if(size>0) {
            users = new ArrayList<>();
            this.notifyItemRangeInserted(0, size - 1);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(position<users.size())
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
