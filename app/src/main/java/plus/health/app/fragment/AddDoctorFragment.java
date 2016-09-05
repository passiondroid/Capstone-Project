package plus.health.app.fragment;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import plus.health.app.R;
import plus.health.app.database.DataContract;
import plus.health.app.model.Doctor;

/**
 * A placeholder fragment containing a simple view.
 */
public class AddDoctorFragment extends Fragment implements View.OnClickListener {

    private Button saveBtn;
    private EditText nameET, phoneET, emailET, addressET;

    public AddDoctorFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_doctor, container, false);
        nameET = (EditText) view.findViewById(R.id.nameET);
        phoneET = (EditText) view.findViewById(R.id.phoneET);
        emailET = (EditText) view.findViewById(R.id.emailET);
        addressET = (EditText) view.findViewById(R.id.addressET);
        saveBtn = (Button) view.findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        //TODO: write validations
        String name = nameET.getText().toString();
        String phone = phoneET.getText().toString();
        String email = emailET.getText().toString();
        String address = addressET.getText().toString();
        Doctor doctor = new Doctor();
        doctor.setName(name);
        doctor.setPhoneNumber(phone);
        doctor.setEmail(email);
        doctor.setAddress(address);
        Intent intent = new Intent();
        intent.putExtra("doctor",doctor);
        getActivity().setResult(Activity.RESULT_OK,intent);
        getActivity().finish();
    }
}
