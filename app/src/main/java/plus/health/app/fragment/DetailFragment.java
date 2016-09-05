package plus.health.app.fragment;

import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import plus.health.app.R;
import plus.health.app.adapter.DoctorRVAdapter;
import plus.health.app.adapter.ItemAdapter;
import plus.health.app.database.DataContract;
import plus.health.app.model.Item;
import plus.health.app.model.Medication;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private RecyclerView listDoctors, listPrescriptions, listReports;
    private static final int PRECRIPTION_LOADER = 1;
    private static final int REPORT_LOADER = 2;
    private Medication medication;
    public DetailFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        listDoctors = (RecyclerView) view.findViewById(R.id.listDoctor);
        listReports = (RecyclerView)view.findViewById(R.id.listReports);
        listPrescriptions = (RecyclerView)view.findViewById(R.id.listPrescription);
        medication = getArguments().getParcelable("Medication");
        TextView probTV = (TextView)view.findViewById(R.id.probTV);
        probTV.setText(medication.getProblem());
        DoctorRVAdapter doctorRVAdapter = new DoctorRVAdapter(getActivity(),medication.getDoctorList());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        listDoctors.setLayoutManager(linearLayoutManager);
        listDoctors.setAdapter(doctorRVAdapter);
        getActivity().getSupportLoaderManager().initLoader(PRECRIPTION_LOADER, null, this);
        getActivity().getSupportLoaderManager().initLoader(REPORT_LOADER, null, this);

        return view;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case PRECRIPTION_LOADER:
                // Returns a new CursorLoader
                return new CursorLoader(getActivity(),   // Parent activity context
                        DataContract.Prescriptions.CONTENT_URI,        // Table to query
                        null,     // Projection to return
                        DataContract.Prescriptions.MEDICATION_ID+"=?",            // No selection clause
                        new String[]{medication.getId()+""},            // No selection arguments
                        null             // Default sort order
                );
            case REPORT_LOADER:
                // Returns a new CursorLoader
                return new CursorLoader(getActivity(),   // Parent activity context
                        DataContract.Reports.CONTENT_URI,        // Table to query
                        null,     // Projection to return
                        DataContract.Reports.MEDICATION_ID+"=?",            // No selection clause
                        new String[]{medication.getId()+""},            // No selection arguments
                        null             // Default sort order
                );
            default:
                // An invalid id was passed in
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if(loader.getId()==PRECRIPTION_LOADER) {
            List<Item> itemList = new ArrayList<>();
            if(cursor.moveToFirst()){
                do{
                    Item item = new Item();
                    item.setName(cursor.getString(cursor.getColumnIndex(DataContract.Prescriptions.NAME)));
                    item.setData(cursor.getString(cursor.getColumnIndex(DataContract.Prescriptions.PATH)));
                    item.setType(cursor.getInt(cursor.getColumnIndex(DataContract.Prescriptions.TYPE)));
                    item.setStatus(cursor.getInt(cursor.getColumnIndex(DataContract.Prescriptions.STATUS)));
                    itemList.add(item);
                }while (cursor.moveToNext());
                ItemAdapter prescAdapter = new ItemAdapter(itemList,getActivity());
                LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                listPrescriptions.setLayoutManager(layoutManager);
                listPrescriptions.setAdapter(prescAdapter);
            }
        }else if(loader.getId()==REPORT_LOADER) {
            List<Item> itemList = new ArrayList<>();
            if (cursor.moveToFirst()) {
                do {
                    Item item = new Item();
                    item.setName(cursor.getString(cursor.getColumnIndex(DataContract.Reports.NAME)));
                    item.setData(cursor.getString(cursor.getColumnIndex(DataContract.Reports.PATH)));
                    item.setType(cursor.getInt(cursor.getColumnIndex(DataContract.Reports.TYPE)));
                    item.setStatus(cursor.getInt(cursor.getColumnIndex(DataContract.Reports.STATUS)));
                    itemList.add(item);
                } while (cursor.moveToNext());
                ItemAdapter reportAdapter = new ItemAdapter(itemList, getActivity());
                LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                listReports.setLayoutManager(layoutManager);
                listReports.setAdapter(reportAdapter);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
