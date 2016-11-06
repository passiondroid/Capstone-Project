package plus.health.app.fragment;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import plus.health.app.R;
import plus.health.app.activity.AddDoctorActivity;
import plus.health.app.adapter.DoctorAdapter;
import plus.health.app.adapter.ItemAdapter;
import plus.health.app.database.DataContract;
import plus.health.app.interfaces.PickerDialogListener;
import plus.health.app.model.Doctor;
import plus.health.app.model.Item;

import static android.R.attr.name;
import static android.app.Activity.RESULT_OK;
import static android.support.v7.recyclerview.R.styleable.RecyclerView;

/**
 * A placeholder fragment containing a simple view.
 */
public class AddMedicationsFragment extends Fragment implements View.OnClickListener, PickerDialogListener {

    private Button prescriptionBtn,saveBtn,reportsBtn,addDoctorBtn;
    static final int REQUEST_IMAGE_CAPTURE_PRESCRIPTION = 1;
    static final int REQUEST_IMAGE_CAPTURE_REPORTS = 2;
    static final int REQUEST_FILE_SELECTION_PRESCRIPTION = 3;
    static final int REQUEST_FILE_SELECTION_REPORTS = 4;
    static final int REQUEST_ADD_DOCTOR_INFO = 5;
    static final String PRESCRIPTION = "PRESC";
    static final String LAB_REPORTS = "LAB_REPORTS";
    String mCurrentPhotoPath;
    private Doctor doctor;
    private ItemAdapter prescAdapter,reportsAdapter;
    private DoctorAdapter doctorAdapter;
    private List<Item> prescList = new ArrayList<>();
    private List<Item> reportsList = new ArrayList<>();
    private List<Doctor> doctorsList = new ArrayList<>();
    private RecyclerView prescRecyclerView, reportsRecyclerView,doctorRV;
    private EditText problemET;

    public AddMedicationsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_medications, container, false);
        prescriptionBtn = (Button) view.findViewById(R.id.pBtn);
        saveBtn = (Button) view.findViewById(R.id.saveBtn);
        reportsBtn = (Button) view.findViewById(R.id.reportBtn);
        addDoctorBtn = (Button) view.findViewById(R.id.addDoctor);
        problemET = (EditText) view.findViewById(R.id.problemET);
        prescriptionBtn.setOnClickListener(this);
        saveBtn.setOnClickListener(this);
        reportsBtn.setOnClickListener(this);
        addDoctorBtn.setOnClickListener(this);
        prescRecyclerView = (RecyclerView) view.findViewById(R.id.listPrescription);
        doctorRV = (RecyclerView) view.findViewById(R.id.doctorRV);
        prescAdapter = new ItemAdapter(prescList,getActivity());
        doctorAdapter = new DoctorAdapter(getActivity(),doctorsList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        prescRecyclerView.setAdapter(prescAdapter);
        prescRecyclerView.setLayoutManager(layoutManager);

        reportsRecyclerView = (RecyclerView) view.findViewById(R.id.listReports);
        reportsAdapter = new ItemAdapter(reportsList,getActivity());
        reportsRecyclerView.setAdapter(reportsAdapter);
        doctorRV.setAdapter(doctorAdapter);
        LinearLayoutManager reportsLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        reportsRecyclerView.setLayoutManager(reportsLayoutManager);
        return view;
    }

    @Override
    public void onCameraSelected(String type) {
        dispatchTakePictureIntent(type);
    }

    @Override
    public void onFolderSelected(String type) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        if(type.equals(PRESCRIPTION))
            startActivityForResult(intent, REQUEST_FILE_SELECTION_PRESCRIPTION);
        else
            startActivityForResult(intent, REQUEST_FILE_SELECTION_REPORTS);
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.pBtn){
            PickerFragment myDialog = PickerFragment.newInstance();
            Bundle bundle = new Bundle();
            bundle.putString("TYPE",PRESCRIPTION);
            myDialog.setArguments(bundle);
            myDialog.setTargetFragment(this,300);
            FragmentManager fm = getActivity().getSupportFragmentManager();
            myDialog.show(fm, "pickerBtn");
        }else if(view.getId()==R.id.reportBtn){
            PickerFragment myDialog = PickerFragment.newInstance();
            Bundle bundle = new Bundle();
            bundle.putString("TYPE",LAB_REPORTS);
            myDialog.setArguments(bundle);
            myDialog.setTargetFragment(this,301);
            FragmentManager fm = getActivity().getSupportFragmentManager();
            myDialog.show(fm, "pickerBtn");
        }else if(view.getId()==R.id.saveBtn){
            //TODO: Save in db, Add validations
            Object[] objects = new Object[4];
            objects[0] = problemET.getText().toString();
            SaveInDbTask dbTask = new SaveInDbTask();
            dbTask.execute(objects);
            // uploadFile(Uri.parse(prescList.get(0).getData()));
        }else if(view.getId()==R.id.addDoctor){
            startActivityForResult(new Intent(getActivity(), AddDoctorActivity.class),REQUEST_ADD_DOCTOR_INFO);
        }
    }


    private void dispatchTakePictureIntent(String type) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getActivity(),
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                if(type.equals(PRESCRIPTION))
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE_PRESCRIPTION);
                else
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE_REPORTS);

            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE_PRESCRIPTION && resultCode == RESULT_OK) {
            Toast.makeText(getActivity(), "Picture taken", Toast.LENGTH_SHORT).show();
            Item item = new Item();
            item.setData(mCurrentPhotoPath);
            item.setType(Item.TYPE_IMAGE);
            prescList.add(item);
            prescAdapter.notifyDataSetChanged();
        }else if(requestCode == REQUEST_FILE_SELECTION_PRESCRIPTION && resultCode == RESULT_OK){
            ContentResolver contentResolver = getActivity().getContentResolver();
            Toast.makeText(getActivity(), "File Selected", Toast.LENGTH_SHORT).show();
            String path = data.getDataString();
            Uri uri = data.getData();
            String mimeType = contentResolver.getType(uri);
            Item item = new Item();
            item.setData(path);
            item.setType(Item.TYPE_DOC);
            if(mimeType != null) {
                if (mimeType.equals("image/jpeg") || mimeType.equals("image/jpg") || mimeType.equals("image/png"))
                    item.setType(Item.TYPE_IMAGE);
            }
            prescList.add(item);
            prescAdapter.notifyDataSetChanged();
        }else if (requestCode == REQUEST_IMAGE_CAPTURE_REPORTS && resultCode == RESULT_OK) {
            //TODO: Move the file to specific location
            Toast.makeText(getActivity(), "Picture taken", Toast.LENGTH_SHORT).show();
            Item item = new Item();
            item.setData(mCurrentPhotoPath);
            item.setType(Item.TYPE_IMAGE);
            reportsList.add(item);
            reportsAdapter.notifyDataSetChanged();
        }else if(requestCode == REQUEST_FILE_SELECTION_REPORTS && resultCode == RESULT_OK){
            ContentResolver contentResolver = getActivity().getContentResolver();
            Toast.makeText(getActivity(), "File Selected", Toast.LENGTH_SHORT).show();
            String path = data.getDataString();
            Uri uri = data.getData();
            String mimeType = contentResolver.getType(uri);
            Item item = new Item();
            item.setData(path);
            item.setType(Item.TYPE_DOC);
            if(mimeType != null) {
                if (mimeType.equals("image/jpeg") || mimeType.equals("image/jpg") || mimeType.equals("image/png"))
                    item.setType(Item.TYPE_IMAGE);
            }
            reportsList.add(item);
            reportsAdapter.notifyDataSetChanged();
        }else if(requestCode == REQUEST_ADD_DOCTOR_INFO && resultCode == RESULT_OK){
            doctor = data.getParcelableExtra("doctor");
            doctorsList.add(doctor);
            doctorAdapter.notifyDataSetChanged();
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        //mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private class SaveInDbTask extends AsyncTask<Object, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Object... objects) {
            //Add medication data
            //TODO:check validation
            try {
                Uri medicationUri = DataContract.Medications.CONTENT_URI;
                String problem = (String) objects[0];
                ContentValues medValues = new ContentValues();
                medValues.put(DataContract.Medications.PROBLEM, problem);
                Uri uri1 = getActivity().getContentResolver().insert(medicationUri, medValues);
                int id = Integer.parseInt(uri1.getLastPathSegment());


                Uri uri = DataContract.Prescriptions.CONTENT_URI;
                for (int i = 0; i < prescList.size(); i++) {
                    Item item = prescList.get(i);
                    ContentValues values = new ContentValues();
                    values.put(DataContract.Prescriptions.NAME, "Presc - " + i + 1);
                    values.put(DataContract.Prescriptions.PATH, item.getData());
                    values.put(DataContract.Prescriptions.TYPE, item.getType());
                    values.put(DataContract.Prescriptions.STATUS, Item.STATUS_LOCAL);
                    values.put(DataContract.Prescriptions.MEDICATION_ID, id);
                    getActivity().getContentResolver().insert(uri, values);
                }

                Uri reportUri = DataContract.Reports.CONTENT_URI;
                for (int i = 0; i < reportsList.size(); i++) {
                    Item item = reportsList.get(i);
                    ContentValues values = new ContentValues();
                    values.put(DataContract.Reports.NAME, "Report - " + i + 1);
                    values.put(DataContract.Reports.PATH, item.getData());
                    values.put(DataContract.Reports.TYPE, item.getType());
                    values.put(DataContract.Reports.STATUS, Item.STATUS_LOCAL);
                    values.put(DataContract.Reports.MEDICATION_ID, id);
                    getActivity().getContentResolver().insert(reportUri, values);
                }

                Uri doctorUri = DataContract.Doctors.CONTENT_URI;
                ContentValues values = new ContentValues();
                values.put(DataContract.Doctors.NAME, doctor.getName());
                values.put(DataContract.Doctors.EMAIL, doctor.getEmail());
                values.put(DataContract.Doctors.PHONE_NUMBER, doctor.getPhoneNumber());
                values.put(DataContract.Doctors.ADDRESS, doctor.getAddress());
                values.put(DataContract.Doctors.MEDICATION_ID, id);
                getActivity().getContentResolver().insert(doctorUri, values);
            }catch (Exception ex){
                Log.e("AddMedicationsFragment", "Exception",ex);
                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean saved) {
            if(!saved){
                Toast.makeText(getActivity(), "Error saving data in db", Toast.LENGTH_SHORT).show();
            }
            getActivity().finish();
        }

    }

}
