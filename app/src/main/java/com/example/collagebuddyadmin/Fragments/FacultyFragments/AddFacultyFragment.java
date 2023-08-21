package com.example.collagebuddyadmin.Fragments.FacultyFragments;

import static android.app.Activity.RESULT_OK;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.collagebuddyadmin.Activities.NoticeActivities.NoticeActivity;
import com.example.collagebuddyadmin.Models.FacultyDataModel;
import com.example.collagebuddyadmin.Models.NoticeDataModel;
import com.example.collagebuddyadmin.R;
import com.example.collagebuddyadmin.databinding.FragmentAddFacultyBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddFacultyFragment extends BottomSheetDialogFragment {


    public AddFacultyFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    FragmentAddFacultyBinding binding;
    private final int ReqCode = 1;
    private Bitmap bitmap;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private String downloadUrl = "";

    private ProgressDialog progressDialog;
    private Calendar calendar;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAddFacultyBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        calendar = Calendar.getInstance();
        progressDialog = new ProgressDialog(getContext());
        databaseReference = FirebaseDatabase.getInstance().getReference("FacultyDetails");
        storageReference = FirebaseStorage.getInstance().getReference();

        // Populate the spinner with designation options
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.designation_options,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerDesignation.setAdapter(adapter);

        binding.imageProfile.setOnClickListener(v -> {
            openGallery();
        });

        binding.btnCancel.setOnClickListener(v -> {dismiss();});


        // Initialize your views and set click listeners here
        binding.btnSave.setOnClickListener(v -> {
            if (validateFields()){
                    if(bitmap==null)
                    {
                        UploadData("");
                    }
                    else{
                        UploadDataWithImage();
                    }
                }
        });

        return view;
    }
    private void UploadData(String imageUrl) {
        progressDialog.setMessage("Uploading");
        progressDialog.show();
        DatabaseReference facultyReference = databaseReference;
        final String uniqueKey = facultyReference.push().getKey();



        FacultyDataModel facultyDataModel = new FacultyDataModel(
                binding.name.getText().toString(),binding.spinnerDesignation.getSelectedItem().toString(),
                imageUrl,binding.contact.getText().toString(),uniqueKey
        );

        facultyReference.child(uniqueKey).setValue(facultyDataModel)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        showToast("Faculty Detail Saved");
        dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        showToast("Oops! Something went wrong");
                    }
                });
    }
    private void UploadDataWithImage() {
        progressDialog.setMessage("Uploading");
        progressDialog.show();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] finalImg = byteArrayOutputStream.toByteArray();
        final StorageReference filePath = storageReference.child("FacultyPictures").child(System.currentTimeMillis() + ".jpg");

        final UploadTask uploadTask = filePath.putBytes(finalImg);
        uploadTask.addOnCompleteListener(requireActivity(), new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    downloadUrl = String.valueOf(uri);
                                  UploadData(downloadUrl);
                                }
                            });
                        }
                    });
                } else {
                    progressDialog.dismiss();
                    showToast("Oops! Something went wrong");
                }
            }
        });
    }



    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, ReqCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ReqCode && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            try {
                // Use the fragment's context to access getContentResolver
                 bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), uri);
                binding.textAddImage.setVisibility(View.GONE);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            binding.imageProfile.setImageBitmap(bitmap);
        }
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
    private boolean validateFields() {
        if (binding.name.getText().toString().isEmpty()) {
            binding.name.setError("Enter Name");
            binding.name.requestFocus();
            return false;
        }  else if (binding.spinnerDesignation.getSelectedItem().toString().equalsIgnoreCase("Select Designation")) {
            showToast("Select Designation");
            return false;
        } else if (binding.contact.getText().toString().isEmpty()) {
            binding.contact.setError("Enter Contact");
            binding.name.requestFocus();
            return false;
        } else {
            return true;
        }
    }
}