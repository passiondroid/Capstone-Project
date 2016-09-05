package plus.health.app.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;
import plus.health.app.R;
import plus.health.app.interfaces.OnItemClickListener;
import plus.health.app.model.Medication;

public class HomeRVAdapter extends RecyclerView.Adapter<HomeRVAdapter.ViewHolder>{

    private Context context;
    private LayoutInflater inflater;
    private List<Medication> medicationList;
    private OnItemClickListener listener;

    public HomeRVAdapter(Context context, List<Medication> medicationList) {
        this.context = context;
        this.medicationList = medicationList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.activity_home_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.probTV.setText(medicationList.get(position).getProblem());
        DoctorRVAdapter doctorRVAdapter = new DoctorRVAdapter(context,medicationList.get(position).getDoctorList());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        holder.recyclerView.setLayoutManager(linearLayoutManager);
        holder.recyclerView.setAdapter(doctorRVAdapter);
    }

    @Override
    public int getItemCount() {
        return medicationList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView probTV,viewMoreTV;
        public RecyclerView recyclerView;

        public ViewHolder(View itemView) {
            super(itemView);
            probTV = (TextView) itemView.findViewById(R.id.probTV);
            viewMoreTV = (TextView) itemView.findViewById(R.id.viewMoreTV);
            recyclerView = (RecyclerView) itemView.findViewById(R.id.listDoctor);
            viewMoreTV.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(null != listener){
                listener.onItemClick(view,getLayoutPosition());
            }
        }
    }
}
    
    
