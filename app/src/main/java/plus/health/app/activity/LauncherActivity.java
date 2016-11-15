package plus.health.app.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import plus.health.app.R;
import plus.health.app.database.DataContract;
import plus.health.app.model.Doctor;
import plus.health.app.model.Item;
import plus.health.app.model.Medication;
import plus.health.app.model.Prescription;
import plus.health.app.model.Report;
import plus.health.app.model.User;

public class LauncherActivity extends AppCompatActivity implements View.OnClickListener,GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "LauncherActivity";
    private SignInButton signInButton;
    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(LauncherActivity.this,HomeActivity.class));
            this.finish();
        } else {
            // not signed in
            // Configure Google Sign In
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getResources().getString(R.string.web_client_id))
                    .requestEmail()
                    .build();

            signInButton =(SignInButton)findViewById(R.id.sign_in_button);
            signInButton.setSize(SignInButton.SIZE_WIDE);
            signInButton.setScopes(gso.getScopeArray());
            signInButton.setOnClickListener(this);

            // Build a GoogleApiClient with access to the Google Sign-In API and the
            // options specified by gso.
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this, this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();

            mAuthListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (user != null) {
                        // User is signed in
                        Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    } else {
                        // User is signed out
                        Log.d(TAG, "onAuthStateChanged:signed_out");
                    }
                }
            };
        }


    }


    @Override
    public void onClick(View view) {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                // ...
                System.out.println("result.toString() = " + result.toString());
                System.out.println(result.getStatus());
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mAuthListener != null) {
            mAuth.addAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                        ContentValues values = new ContentValues();
                        values.put(DataContract.Users.NAME, acct.getDisplayName());
                        values.put(DataContract.Users.EMAIL, acct.getEmail());
                        values.put(DataContract.Users.PHOTO_URL, acct.getPhotoUrl().toString());
                        values.put(DataContract.Users.USER_ID, acct.getId());
                        Uri uri = Uri.withAppendedPath(DataContract.Users.CONTENT_URI,acct.getId());
                        getContentResolver().insert(uri, values);

                        //TODO: Before writing user data check if the user is present or not otherwise all data will be lost
                        WriteNewUserTask writeNewUserTask = new WriteNewUserTask();
                        writeNewUserTask.execute(getUser(acct));

                        startActivity(new Intent(LauncherActivity.this,HomeActivity.class));
                        LauncherActivity.this.finish();
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(LauncherActivity.this, getResources().getString(R.string.authentication_failed),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(LauncherActivity.this, getResources().getString(R.string.sign_in_failed), Toast.LENGTH_SHORT).show();
    }

    private class WriteNewUserTask extends AsyncTask<Object, Void, Void> {

        @Override
        protected Void doInBackground(Object... params) {
            User user = (User)params[0];
            if(mDatabase.child("users").child(user.getId())==null) {
                mDatabase.child("users").child(user.getId()).setValue(user);
            }else{
                //User already exists. Restore the data of the user.
                mDatabase.child("users").child(user.getId()).child("medications").addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                // Get user value
                                //User user = dataSnapshot.getValue(User.class);
                                List<Medication> medicationList = new ArrayList<>();
                                for (DataSnapshot medicationSpshot : dataSnapshot.getChildren()) {
                                    Medication medication = new Medication();
                                    medication.setId(Integer.parseInt(medicationSpshot.getKey()));
                                    // for (DataSnapshot medicationValueSpshot : dataSnapshot.getChildren()) {
                                    HashMap map = (HashMap) medicationSpshot.getValue();
                                    medication.setProblem(map.get("problem").toString());
                                    List<Item> prescList = new ArrayList<>();
                                    List<Item> reportList = new ArrayList<>();
                                    List<Doctor> doctors = new ArrayList<>();
                                    if(map.get("prescriptions") instanceof List){
                                        List<Object> prescObjList = (List) map.get("prescriptions");
                                        for (int i = 0; i < prescObjList.size(); i++) {
                                            Object presc = prescObjList.get(i);
                                            if (null != presc) {
                                                Item item = new Item();
                                                HashMap mapPresc = (HashMap) presc;
                                                item.setName(mapPresc.get("name").toString());
                                                item.setType(Integer.parseInt(mapPresc.get("type").toString()));
                                                item.setData(mapPresc.get("path").toString());
                                                item.setMedicationId(medication.getId());
                                                item.setId(i);
                                                prescList.add(item);
                                            }
                                        }
                                    }else if(map.get("prescriptions") instanceof HashMap){
                                        Item item = new Item();
                                        HashMap mapPresc = (HashMap) map.get("prescriptions");
                                        Iterator it = mapPresc.entrySet().iterator();
                                        while (it.hasNext()) {
                                            Map.Entry pair = (Map.Entry)it.next();
                                            HashMap mapFinal = (HashMap) pair.getValue();
                                            item.setName(mapFinal.get("name").toString());
                                            item.setType(Integer.parseInt(mapFinal.get("type").toString()));
                                            item.setData(mapFinal.get("path").toString());
                                            item.setMedicationId(medication.getId());
                                            item.setId(Integer.parseInt(pair.getKey().toString()));
                                            prescList.add(item);
                                            it.remove(); // avoids a ConcurrentModificationException
                                        }

                                    }

                                    if(map.get("reports") instanceof List){
                                        List<Object> prescObjList = (List) map.get("reports");
                                        for (int i = 0; i < prescObjList.size(); i++) {
                                            Object presc = prescObjList.get(i);
                                            if (null != presc) {
                                                Item item = new Item();
                                                HashMap mapPresc = (HashMap) presc;
                                                item.setName(mapPresc.get("name").toString());
                                                item.setType(Integer.parseInt(mapPresc.get("type").toString()));
                                                item.setData(mapPresc.get("path").toString());
                                                item.setMedicationId(medication.getId());
                                                item.setId(i);
                                                reportList.add(item);
                                            }
                                        }
                                    }else if(map.get("reports") instanceof HashMap){
                                        Item item = new Item();
                                        HashMap mapPresc = (HashMap) map.get("reports");
                                        Iterator it = mapPresc.entrySet().iterator();
                                        while (it.hasNext()) {
                                            Map.Entry pair = (Map.Entry)it.next();
                                            HashMap mapFinal = (HashMap) pair.getValue();
                                            item.setName(mapFinal.get("name").toString());
                                            item.setType(Integer.parseInt(mapFinal.get("type").toString()));
                                            item.setData(mapFinal.get("path").toString());
                                            item.setMedicationId(medication.getId());
                                            item.setId(Integer.parseInt(pair.getKey().toString()));
                                            reportList.add(item);
                                            it.remove(); // avoids a ConcurrentModificationException
                                        }

                                    }

                                    if(map.get("doctor") instanceof List){
                                        List<Object> doctorObjList = (List) map.get("doctor");
                                        for (int i = 0; i < doctorObjList.size(); i++) {
                                            Object presc = doctorObjList.get(i);
                                            if (null != presc) {
                                                Doctor item = new Doctor();
                                                HashMap mapPresc = (HashMap) presc;
                                                item.setName(mapPresc.get("name").toString());
                                                item.setEmail(mapPresc.get("email").toString());
                                                item.setAddress(mapPresc.get("address").toString());
                                                item.setPhoneNumber(mapPresc.get("phoneNumber").toString());
                                                item.setMedicationId(medication.getId());
                                                item.setId(i);
                                                doctors.add(item);
                                            }
                                        }
                                    }else if(map.get("doctor") instanceof HashMap){
                                        Doctor item = new Doctor();
                                        HashMap mapPresc = (HashMap) map.get("doctor");
                                        Iterator it = mapPresc.entrySet().iterator();
                                        while (it.hasNext()) {
                                            Map.Entry pair = (Map.Entry)it.next();
                                            HashMap mapFinal = (HashMap) pair.getValue();
                                            item.setName(mapFinal.get("name").toString());
                                            item.setEmail(mapFinal.get("email").toString());
                                            item.setAddress(mapFinal.get("address").toString());
                                            item.setPhoneNumber(mapFinal.get("phoneNumber").toString());
                                            item.setMedicationId(medication.getId());
                                            item.setId(Integer.parseInt(pair.getKey().toString()));
                                            doctors.add(item);
                                            it.remove(); // avoids a ConcurrentModificationException
                                        }

                                    }

                                    medication.setReportList(reportList);
                                    medication.setPrescriptionList(prescList);
                                    medication.setDoctorList(doctors);

                                    // }

                                    medicationList.add(medication);

                                }
                                System.out.println("Medications Downloaded :::: " + medicationList);
                                SaveInDbTask task = new SaveInDbTask();
                                task.execute(medicationList);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                            }
                        });
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.i(TAG,"User added");
        }

    }

    public User getUser(GoogleSignInAccount acct){
        User user = new User();
        user.setName(acct.getDisplayName());
        user.setEmail(acct.getEmail());
        user.setId(acct.getId());
        user.setPhotoURL(acct.getPhotoUrl().toString());
        return user;
    }

    private class SaveInDbTask extends AsyncTask<List<Medication>, Void, Boolean> {

        @Override
        protected Boolean doInBackground(List<Medication>... objects) {
            //Add medication data
            //TODO:check validation
            Uri medicationUri = DataContract.Medications.CONTENT_URI;
            List<Medication> medicationList = (List<Medication>) objects[0];
            for (Medication medication: medicationList) {
                try {
                    ContentValues medValues = new ContentValues();
                    medValues.put(DataContract.Medications.PROBLEM, medication.getProblem());
                    Uri uri1 = getContentResolver().insert(medicationUri, medValues);
                    int id = Integer.parseInt(uri1.getLastPathSegment());


                    Uri uri = DataContract.Prescriptions.CONTENT_URI;
                    for (int i = 0; i < medication.getPrescriptionList().size(); i++) {
                        Item item = medication.getPrescriptionList().get(i);
                        ContentValues values = new ContentValues();
                        values.put(DataContract.Prescriptions.NAME, "Presc - " + i + 1);
                        values.put(DataContract.Prescriptions.PATH, item.getData());
                        values.put(DataContract.Prescriptions.TYPE, item.getType());
                        values.put(DataContract.Prescriptions.STATUS, Item.STATUS_LOCAL);
                        values.put(DataContract.Prescriptions.MEDICATION_ID, id);
                        getContentResolver().insert(uri, values);
                    }

                    Uri reportUri = DataContract.Reports.CONTENT_URI;
                    for (int i = 0; i < medication.getReportList().size(); i++) {
                        Item item = medication.getReportList().get(i);
                        ContentValues values = new ContentValues();
                        values.put(DataContract.Reports.NAME, "Report - " + i + 1);
                        values.put(DataContract.Reports.PATH, item.getData());
                        values.put(DataContract.Reports.TYPE, item.getType());
                        values.put(DataContract.Reports.STATUS, Item.STATUS_LOCAL);
                        values.put(DataContract.Reports.MEDICATION_ID, id);
                        getContentResolver().insert(reportUri, values);
                    }

                    Uri doctorUri = DataContract.Doctors.CONTENT_URI;
                    for (int i = 0; i < medication.getDoctorList().size(); i++) {
                        ContentValues values = new ContentValues();
                        values.put(DataContract.Doctors.NAME, medication.getDoctorList().get(i).getName());
                        values.put(DataContract.Doctors.EMAIL, medication.getDoctorList().get(i).getEmail());
                        values.put(DataContract.Doctors.PHONE_NUMBER, medication.getDoctorList().get(i).getPhoneNumber());
                        values.put(DataContract.Doctors.ADDRESS, medication.getDoctorList().get(i).getAddress());
                        values.put(DataContract.Doctors.MEDICATION_ID, id);
                        getContentResolver().insert(doctorUri, values);
                    }
                }catch (Exception ex){
                    Log.e("AddMedicationsFragment", "Exception",ex);
                    return false;
                }
            }
            return true;
        }


        @Override
        protected void onPostExecute(Boolean saved) {
            if(!saved){
                Toast.makeText(LauncherActivity.this, getResources().getString(R.string.error_retreiving_data), Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(LauncherActivity.this, getResources().getString(R.string.data_stored_successfully), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LauncherActivity.this,HomeActivity.class));
            }
        }

    }


}
