package plus.health.app.fragment;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import plus.health.app.R;
import plus.health.app.database.DataContract;
import plus.health.app.interfaces.PickerDialogListener;
import plus.health.app.model.Doctor;
import plus.health.app.model.Item;

import static android.app.Activity.RESULT_OK;
import static plus.health.app.fragment.AddMedicationsFragment.PRESCRIPTION;

/**
 * A placeholder fragment containing a simple view.
 */
public class AddDoctorFragment extends Fragment implements View.OnClickListener, PickerDialogListener {

    private Button saveBtn;
    private EditText nameET, phoneET, emailET, addressET;
    private ImageView imageView;
    static final int REQUEST_FILE_SELECTION= 4;
    static final int REQUEST_IMAGE_CAPTURE = 2;
    String mCurrentPhotoPath;

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
        imageView = (ImageView) view.findViewById(R.id.image);
        imageView.setOnClickListener(this);
        saveBtn = (Button) view.findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.saveBtn) {
            if(validate()){
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
                intent.putExtra("doctor", doctor);
                getActivity().setResult(Activity.RESULT_OK, intent);
                getActivity().finish();
            }
        }else if(view.getId() == R.id.image){
            PickerFragment myDialog = PickerFragment.newInstance();
            Bundle bundle = new Bundle();
            myDialog.setArguments(bundle);
            myDialog.setTargetFragment(this,302);
            FragmentManager fm = getActivity().getSupportFragmentManager();
            myDialog.show(fm, "pickerBtn");
        }
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
        startActivityForResult(intent, REQUEST_FILE_SELECTION);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath,bmOptions);
            bitmap = Bitmap.createBitmap(bitmap);
            imageView.setImageBitmap(bitmap);
        } else if (requestCode == REQUEST_FILE_SELECTION && resultCode == RESULT_OK) {
            ContentResolver contentResolver = getActivity().getContentResolver();
            Toast.makeText(getActivity(), getResources().getString(R.string.file_selected), Toast.LENGTH_SHORT).show();
            String path = data.getDataString();
            Uri uri = data.getData();
            String mimeType = contentResolver.getType(uri);
            if (mimeType != null) {
                if (mimeType.equals("image/jpeg") || mimeType.equals("image/jpg") || mimeType.equals("image/png")) {
                    Glide.with(getActivity())
                            .load(path)
                            .into(imageView);
                }
                else
                    Toast.makeText(getActivity(),getResources().getString(R.string.file_not_supported),Toast.LENGTH_SHORT).show();
            }

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
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);

            }
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

    private boolean validate(){
        if(nameET.getText().toString().trim().equals("")){
            nameET.setError(getResources().getString(R.string.error_name));
            return false;
        }else if(phoneET.getText().toString().trim().equals("")){
            phoneET.setError(getResources().getString(R.string.error_phone_number));
            return false;
        }else if(emailET.getText().toString().trim().equals("")){
            emailET.setError(getResources().getString(R.string.error_email));
            return false;
        }else if(addressET.getText().toString().trim().equals("")){
            addressET.setError(getResources().getString(R.string.error_address));
            return false;
        }
        return true;
    }
}
