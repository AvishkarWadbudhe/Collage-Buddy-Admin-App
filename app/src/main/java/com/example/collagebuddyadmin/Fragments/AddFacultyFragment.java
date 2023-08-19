package com.example.collagebuddyadmin.Fragments;

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

import androidx.annotation.Nullable;

import com.example.collagebuddyadmin.R;
import com.example.collagebuddyadmin.databinding.FragmentAddFacultyBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
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

        // Populate the spinner with designation options
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.designation_options,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerDesignation.setAdapter(adapter);

        binding.btnCancel.setOnClickListener(v -> {dismiss();});


        // Initialize your views and set click listeners here
        binding.btnSave.setOnClickListener(v -> {
            if (validateFields()){
                    if(bitmap==null)
                    {

                    }
                    else{

                    }
                }
        });

        return view;
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
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), uri);
                binding.imageProfile.setImageBitmap(bitmap);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
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