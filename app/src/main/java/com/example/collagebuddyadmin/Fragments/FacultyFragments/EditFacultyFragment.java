package com.example.collagebuddyadmin.Fragments.FacultyFragments;

import static android.app.Activity.RESULT_OK;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.collagebuddyadmin.Models.FacultyDataModel;
import com.example.collagebuddyadmin.R;
import com.example.collagebuddyadmin.databinding.FragmentAddFacultyBinding;
import com.example.collagebuddyadmin.databinding.FragmentEditFacultyBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class EditFacultyFragment extends BottomSheetDialogFragment {



    public EditFacultyFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private FragmentEditFacultyBinding binding;

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

        binding = FragmentEditFacultyBinding.inflate(inflater, container, false);
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

        Bundle bundle = getArguments();
        FacultyDataModel selectedFaculty = (FacultyDataModel) bundle.getSerializable("selectedFaculty");
        if (bundle != null) {
            // Use the selectedFaculty data to pre-fill edit fields
            if (selectedFaculty != null) {
                binding.name.setText(selectedFaculty.getName());
                binding.spinnerDesignation.setSelection(getDesignationPosition(selectedFaculty.getDesignation()));
                binding.contact.setText(selectedFaculty.getContact());

                if(selectedFaculty.getImageUrl().equalsIgnoreCase("")){
                    binding.textAddImage.setVisibility(View.VISIBLE);
                }
                Glide.with(this)
                        .asBitmap()
                        .load(selectedFaculty.getImageUrl())
                        .into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                binding.imageProfile.setImageBitmap(resource);
                                binding.textAddImage.setVisibility(View.INVISIBLE);
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {

                            }
                        });
            }
        }



        binding.imageProfile.setOnClickListener(v -> {
            openGallery();
        });

        binding.btnCancel.setOnClickListener(v -> {dismiss();});


        // Initialize your views and set click listeners here
        binding.btnSave.setOnClickListener(v -> {
            if (validateFields()){

                updateFacultyData(selectedFaculty.getUniqueKey(),selectedFaculty.getImageUrl()
                        ,bitmap);
            }
        });

      return  view;
    }
    private void updateFacultyData(String facultyId, String imageUrl, Bitmap newImageBitmap) {
        progressDialog.setMessage("Updating");
        progressDialog.show();

        DatabaseReference facultyRef = databaseReference.child(facultyId);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        newImageBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] finalImg = byteArrayOutputStream.toByteArray();

        final StorageReference filePath = storageReference.child("FacultyPictures").child(System.currentTimeMillis() + ".jpg");
        final UploadTask uploadTask = filePath.putBytes(finalImg);

        uploadTask.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                uploadTask.addOnSuccessListener(taskSnapshot -> {
                    filePath.getDownloadUrl().addOnSuccessListener(uri -> {
                        String updatedImageUrl = String.valueOf(uri);

                        // Get the current faculty data to access the old imageUrl
                        facultyRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    FacultyDataModel currentFaculty = snapshot.getValue(FacultyDataModel.class);

                                    // Delete the old image from storage
                                    if (currentFaculty != null && !currentFaculty.getImageUrl().isEmpty()) {
                                        StorageReference oldImageRef = FirebaseStorage.getInstance().getReferenceFromUrl(currentFaculty.getImageUrl());
                                        oldImageRef.delete()
                                                .addOnSuccessListener(aVoid -> {
                                                    // Proceed to update faculty data
                                                    FacultyDataModel updatedFacultyData = new FacultyDataModel(
                                                            binding.name.getText().toString(),
                                                            binding.spinnerDesignation.getSelectedItem().toString(),
                                                            updatedImageUrl,
                                                            binding.contact.getText().toString(),
                                                            facultyId
                                                    );

                                                    facultyRef.setValue(updatedFacultyData)
                                                            .addOnSuccessListener(unused -> {
                                                                progressDialog.dismiss();
                                                                showToast("Faculty Detail Updated");
                                                                dismiss();
                                                            })
                                                            .addOnFailureListener(e -> {
                                                                progressDialog.dismiss();
                                                                showToast("Oops! Something went wrong");
                                                            });
                                                })
                                                .addOnFailureListener(e -> {
                                                    progressDialog.dismiss();
                                                    showToast("Failed to delete old image");
                                                });
                                    } else {
                                        progressDialog.dismiss();
                                        showToast("Failed to retrieve old image URL");
                                    }
                                } else {
                                    progressDialog.dismiss();
                                    showToast("Faculty data not found");
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                progressDialog.dismiss();
                                showToast("Failed to retrieve faculty data");
                            }
                        });
                    });
                });
            } else {
                progressDialog.dismiss();
                showToast("Oops! Something went wrong");
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
    private int getDesignationPosition(String designation) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(), R.array.designation_options, android.R.layout.simple_spinner_item
        );

        int count = adapter.getCount();
        for (int i = 0; i < count; i++) {
            if (adapter.getItem(i).equals(designation)) {
                return i;
            }
        }

        return 0; // Default position if not found
    }

}