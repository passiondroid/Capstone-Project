package plus.health.app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import plus.health.app.R;
import plus.health.app.model.Item;
import plus.health.app.model.Report;

/**
 * Created by akhan on 3/3/2016.
 */
public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ViewHolder> {

    private List<Report> items;
    private Context mContext;
    // Define listener member variable
    private static OnAdapterItemClickListener listener;
    // Define the listener interface
    public interface OnAdapterItemClickListener {
        void onItemClick(View itemView, int position);
    }

    public ReportAdapter(List<Report> items, Context context) {
        this.items = items;
        this.mContext = context;
    }

    @Override
    public ReportAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.layout_presc_item, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ReportAdapter.ViewHolder viewHolder, int position) {
        TextView textView = viewHolder.textView;
        textView.setText(items.get(position).getData());
        if(items.get(position).getType() == Item.TYPE_IMAGE){
            Glide.with(mContext)
                    .load(items.get(position).getData())
                    .into(viewHolder.imageView);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // Define the method that allows the parent activity or fragment to define the listener
    public void setOnItemClickListener(OnAdapterItemClickListener listener) {
        this.listener = listener;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public ImageView imageView,cancelView;
        public TextView textView;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(final View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.file);
            cancelView = (ImageView) itemView.findViewById(R.id.cancel);
            textView = (TextView) itemView.findViewById(R.id.title);
            // Setup the click listener
            cancelView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    items.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());
                    notifyItemRangeChanged(getAdapterPosition(), items.size());
                    // Triggers click upwards to the adapter on click
                    if (listener != null)
                        listener.onItemClick(itemView, getLayoutPosition());
                }
            });
        }
    }


}