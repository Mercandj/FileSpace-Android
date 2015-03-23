package mercandalli.com.jarvis.adapter;

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
import mercandalli.com.jarvis.activity.Application;
import mercandalli.com.jarvis.listener.IModelFileListener;
import mercandalli.com.jarvis.model.ModelFile;
import mercandalli.com.jarvis.model.ModelFileTypeENUM;

public class AdapterModelFile extends RecyclerView.Adapter<AdapterModelFile.ViewHolder> {

	private Application app;
	private List<ModelFile> files;
    OnItemClickListener mItemClickListener;
    OnItemLongClickListener mItemLongClickListener;
	private IModelFileListener moreListener;
	
	public AdapterModelFile(Application app, List<ModelFile> files, IModelFileListener moreListener) {
		this.app = app;
		this.files = files;
		this.moreListener = moreListener;
	}

    @Override
    public AdapterModelFile.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.tab_file, parent, false), viewType);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        if(position<files.size()) {
            final ModelFile file = files.get(position);

            viewHolder.title.setText(file.getAdapterTitle());
            viewHolder.subtitle.setText(file.getAdapterSubtitle());

            if(file.directory)
                viewHolder.icon.setImageDrawable(app.getDrawable(R.drawable.directory));
            else if(file.type!=null) {
                if (file.type.equals(ModelFileTypeENUM.AUDIO.type))
                    viewHolder.icon.setImageDrawable(app.getDrawable(R.drawable.file_audio));
                else if (file.type.equals(ModelFileTypeENUM.PDF.type))
                    viewHolder.icon.setImageDrawable(app.getDrawable(R.drawable.file_pdf));
                else if (file.type.equals(ModelFileTypeENUM.APK.type))
                    viewHolder.icon.setImageDrawable(app.getDrawable(R.drawable.file_apk));
                else if (file.type.equals(ModelFileTypeENUM.JARVIS.type))
                    viewHolder.icon.setImageDrawable(app.getDrawable(R.drawable.file_jarvis));
                else
                    viewHolder.icon.setImageDrawable(app.getDrawable(R.drawable.file_default));
            }
            else
                viewHolder.icon.setImageDrawable(app.getDrawable(R.drawable.file_default));

            if(file.bitmap!=null)
                viewHolder.icon.setImageBitmap(file.bitmap);

            viewHolder.more.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(moreListener!=null)
                        moreListener.execute(file);
                }
            });


            if (file.type.equals(ModelFileTypeENUM.JARVIS.type)) {
                /*
                final Handler timerHandler = new Handler();

                Runnable timerRunnable = new Runnable() {
                    @Override
                    public void run() {
                        if(v)
                        viewHolder.title.notify();
                        timerHandler.postDelayed(this, 1000); // run every s
                    }
                };
                timerRunnable.run();
                */
            }
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
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
        return files.size();
    }


    public void remplaceList(ArrayList<ModelFile> list) {
        files.clear();
        files.addAll(0, list);
        notifyDataSetChanged();
    }

    public void addFirst(ArrayList<ModelFile> list) {
        files.addAll(0, list);
        notifyDataSetChanged();
    }

    public void addLast(ArrayList<ModelFile> list) {
        files.addAll(files.size(), list);
        notifyDataSetChanged();
    }

    public void addItem(ModelFile name, int position) {
        this.files.add(position, name);
        this.notifyItemInserted(position);
    }

    public void removeAll() {
        int size = files.size();
        if(size>0) {
            files = new ArrayList<>();
            this.notifyItemRangeInserted(0, size - 1);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(position<files.size())
            return files.get(position).viewType;
        return 0;
    }

    public interface OnItemClickListener {
        public void onItemClick(View view , int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public interface OnItemLongClickListener {
        public boolean onItemLongClick(View view , int position);
    }

    public void setOnItemLongClickListener(final OnItemLongClickListener mItemLongClickListener) {
        this.mItemLongClickListener = mItemLongClickListener;
    }
}
