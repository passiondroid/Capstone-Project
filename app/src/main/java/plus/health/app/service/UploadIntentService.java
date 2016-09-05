package plus.health.app.service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

import plus.health.app.database.DataContract;
import plus.health.app.model.Doctor;
import plus.health.app.model.Item;
import plus.health.app.model.Medication;
import plus.health.app.model.User;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * helper methods.
 */
public class UploadIntentService extends IntentService {
    private static final String ACTION_UPLOAD = "plus.health.app.action.upload";
    private static final String BUCKET_PATH = "gs://health-plus-db0c2.appspot.com/";
    private DatabaseReference mDatabase;
    //private static final String ACTION_BAZ = "plus.health.app.action.BAZ";

    //private static final String EXTRA_PARAM1 = "plus.health.app.extra.PARAM1";
    //private static final String EXTRA_PARAM2 = "plus.health.app.extra.PARAM2";

    public UploadIntentService() {
        super("UploadIntentService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startUpload(Context context) {
        Intent intent = new Intent(context, UploadIntentService.class);
        intent.setAction(ACTION_UPLOAD);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    /*public static void startActionBaz(Context context, String param1, String param2) {
        Intent intent = new Intent(context, UploadIntentService.class);
        intent.setAction(ACTION_BAZ);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }*/

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            Log.i("Upload Service","Data uploading started");
            final String action = intent.getAction();
            if (ACTION_UPLOAD.equals(action)) {
                mDatabase = FirebaseDatabase.getInstance().getReference();
                User user = getUser();
                Cursor cursor = getContentResolver().query(DataContract.Medications.CONTENT_URI,null,null,null,null);
                if(cursor.moveToFirst()){
                    do{
                        int i = cursor.getInt(cursor.getColumnIndex(DataContract.Medications.STATUS));
                        if(i==0){
                            int medId = cursor.getInt(cursor.getColumnIndex(DataContract.Medications.ID));
                            mDatabase.child("users").child(user.getId()).child("medications").child(medId+"")
                                    .child("problem").setValue(cursor.getString(cursor.getColumnIndex(DataContract.Medications.PROBLEM)));
                            List<Doctor> doctorList = getDoctors(medId);
                            for(Doctor doctor : doctorList){
                                mDatabase.child("users").child(user.getId()).child("medications").child(medId+"")
                                        .child("doctors").child(doctor.getId()+"")
                                        .child("name").setValue(doctor.getName());
                                mDatabase.child("users").child(user.getId()).child("medications").child(medId+"")
                                        .child("doctors").child(doctor.getId()+"")
                                        .child("email").setValue(doctor.getEmail());
                                mDatabase.child("users").child(user.getId()).child("medications").child(medId+"")
                                        .child("doctors").child(doctor.getId()+"")
                                        .child("address").setValue(doctor.getAddress());
                                mDatabase.child("users").child(user.getId()).child("medications").child(medId+"")
                                        .child("doctors").child(doctor.getId()+"")
                                        .child("phone_number").setValue(doctor.getPhoneNumber());
                            }
                            List<Item> prescList = getPrescriptions(medId);
                            for(Item item : prescList){
                                mDatabase.child("users").child(user.getId()).child("medications").child(medId+"")
                                        .child("prescriptions").child(item.getId()+"")
                                        .child("name").setValue(item.getName());
                                uploadFile(user,item,medId);
                                Uri uri = Uri.parse(item.getData());
                                mDatabase.child("users").child(user.getId()).child("medications").child(medId+"")
                                        .child("prescriptions").child(item.getId()+"")
                                        .child("path").setValue(BUCKET_PATH+user.getId()+"/"+user.getName()+"/"+uri.getLastPathSegment());
                                mDatabase.child("users").child(user.getId()).child("medications").child(medId+"")
                                        .child("prescriptions").child(item.getId()+"")
                                        .child("type").setValue(item.getType());

                            }
                            List<Item> reportList = getReports(medId);
                            for(Item item : reportList){
                                mDatabase.child("users").child(user.getId()).child("medications").child(medId+"")
                                        .child("reports").child(item.getId()+"")
                                        .child("name").setValue(item.getName());
                                uploadFile(user,item,medId);
                                Uri uri = Uri.parse(item.getData());
                                mDatabase.child("users").child(user.getId()).child("medications").child(medId+"")
                                        .child("reports").child(item.getId()+"")
                                        .child("path").setValue(BUCKET_PATH+user.getId()+"/"+user.getName()+"/"+uri.getLastPathSegment());
                                mDatabase.child("users").child(user.getId()).child("medications").child(medId+"")
                                        .child("reports").child(item.getId()+"")
                                        .child("type").setValue(item.getType());

                            }


                        }

                    }while (cursor.moveToNext());
                }
            }
        }
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public User getUser(){
        Cursor cursor = getContentResolver().query(DataContract.Users.CONTENT_URI,null,null,null,null);
        User user = new User();
        if(cursor.moveToFirst()){
            do{
                user.setName(cursor.getString(cursor.getColumnIndex(DataContract.Users.NAME)));
                user.setId(cursor.getString(cursor.getColumnIndex(DataContract.Users.USER_ID)));
            }while (cursor.moveToNext());
        }
        cursor.close();
        return user;
    }

    public List<Doctor> getDoctors(int medId){
        List<Doctor> doctorList = new ArrayList<>();
        Uri uri = Uri.withAppendedPath(DataContract.Doctors.CONTENT_URI,"medication/"+medId);
        Cursor cursor = getContentResolver().query(uri,null,null,null,null);
        if(cursor.moveToFirst()){
            do{
                Doctor doctor = new Doctor();
                doctor.setName(cursor.getString(cursor.getColumnIndex(DataContract.Doctors.NAME)));
                doctor.setEmail(cursor.getString(cursor.getColumnIndex(DataContract.Doctors.EMAIL)));
                doctor.setPhoneNumber(cursor.getString(cursor.getColumnIndex(DataContract.Doctors.PHONE_NUMBER)));
                doctor.setAddress(cursor.getString(cursor.getColumnIndex(DataContract.Doctors.ADDRESS)));
                doctor.setId(cursor.getInt(cursor.getColumnIndex(DataContract.Doctors.ID)));
                doctor.setMedicationId(cursor.getInt(cursor.getColumnIndex(DataContract.Doctors.MEDICATION_ID)));
                doctorList.add(doctor);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return doctorList;
    }

    public List<Item> getPrescriptions(int medId){
        List<Item> prescList = new ArrayList<>();
        Uri uri = Uri.withAppendedPath(DataContract.Prescriptions.CONTENT_URI,"medication/"+medId);
        Cursor cursor = getContentResolver().query(uri,null,null,null,null);
        if(cursor.moveToFirst()){
            do{
                Item item = new Item();
                item.setData(cursor.getString(cursor.getColumnIndex(DataContract.Prescriptions.PATH)));
                item.setName(cursor.getString(cursor.getColumnIndex(DataContract.Prescriptions.NAME)));
                item.setType(cursor.getInt(cursor.getColumnIndex(DataContract.Prescriptions.TYPE)));
                item.setMedicationId(cursor.getInt(cursor.getColumnIndex(DataContract.Prescriptions.MEDICATION_ID)));
                item.setId(cursor.getInt(cursor.getColumnIndex(DataContract.Prescriptions.ID)));
                prescList.add(item);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return prescList;
    }


    private List<Item> getReports(int medId) {
        List<Item> reportList = new ArrayList<>();
        Uri uri = Uri.withAppendedPath(DataContract.Reports.CONTENT_URI,"medication/"+medId);
        Cursor cursor = getContentResolver().query(uri,null,null,null,null);
        if(cursor.moveToFirst()){
            do{
                Item item = new Item();
                item.setData(cursor.getString(cursor.getColumnIndex(DataContract.Reports.PATH)));
                item.setName(cursor.getString(cursor.getColumnIndex(DataContract.Reports.NAME)));
                item.setType(cursor.getInt(cursor.getColumnIndex(DataContract.Reports.TYPE)));
                item.setMedicationId(cursor.getInt(cursor.getColumnIndex(DataContract.Reports.MEDICATION_ID)));
                item.setId(cursor.getInt(cursor.getColumnIndex(DataContract.Reports.ID)));
                reportList.add(item);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return reportList;
    }


    private void uploadFile( final User user, final Item item, final int medId) {
        Uri uri = Uri.parse(item.getData());
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://health-plus-db0c2.appspot.com");

        // Upload file and metadata to the path 'images/mountains.jpg'
        UploadTask uploadTask = storageRef.child(user.getId()+"/"+user.getName()+"/"+uri.getLastPathSegment()).putFile(uri);

        // Listen for state changes, errors, and completion of the upload.
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                //TODO: set this particular medication upload failed
                Log.e("UploadService", "Upload failed",exception);
                ContentValues values = new ContentValues();
                values.put(DataContract.Medications.STATUS,0);
                getContentResolver().update(DataContract.Medications.CONTENT_URI,values,DataContract.Medications.ID+"=?",new String[]{medId+""});
            }
        });
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Handle successful uploads on complete
                Uri downloadUrl = taskSnapshot.getMetadata().getDownloadUrl();
                //TODO: set this particular medication upload success
                ContentValues values = new ContentValues();
                values.put(DataContract.Medications.STATUS,1);
                getContentResolver().update(DataContract.Medications.CONTENT_URI,values,DataContract.Medications.ID+"=?",new String[]{medId+""});

            }
        });
    }
}
