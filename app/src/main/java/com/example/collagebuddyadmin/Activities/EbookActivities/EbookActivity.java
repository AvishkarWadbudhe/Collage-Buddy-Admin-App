package com.example.collagebuddyadmin.Activities.EbookActivities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Toast;

import com.example.collagebuddyadmin.Adapters.EbookAdapter;
import com.example.collagebuddyadmin.Fragments.EbookFragments.AddEbookFragment;
import com.example.collagebuddyadmin.Fragments.FacultyFragments.AddFacultyFragment;
import com.example.collagebuddyadmin.Listeners.OnEbookClickListener;
import com.example.collagebuddyadmin.Models.EbookDataModel;
import com.example.collagebuddyadmin.databinding.ActivityEbookBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class EbookActivity extends AppCompatActivity implements OnEbookClickListener {

    private ActivityEbookBinding binding;

    private final int ReqCode = 1;
    private Uri pdfData;

    private DatabaseReference databaseReference;
    private String eBookName;
    private StorageReference storageReference;

    private ProgressDialog progressDialog;
    private List<EbookDataModel> eBookList = new ArrayList<>();
    private Calendar calendar;
    private Bitmap thumbnail;
    private String thumbnailUrl = "";
    private EbookAdapter ebookAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEbookBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        calendar = Calendar.getInstance();
        progressDialog = new ProgressDialog(this);
        databaseReference = FirebaseDatabase.getInstance().getReference("Ebooks");
        storageReference = FirebaseStorage.getInstance().getReference();
        fetchDataFromFirebase();
        ebookAdapter = new EbookAdapter( eBookList, this,this);

        binding.addEbookBtn.setOnClickListener(v -> {
            showAddFacultyDialog();
        });

        binding.recyclerViewEbook.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewEbook.setAdapter(ebookAdapter);

    }
    private void showAddFacultyDialog() {
        AddEbookFragment bottomSheetDialog = new AddEbookFragment();
        bottomSheetDialog.show(getSupportFragmentManager(), bottomSheetDialog.getTag());
    }

    private void fetchDataFromFirebase() {
        // Attach a ValueEventListener to the database reference
        databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                eBookList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    EbookDataModel ebookDataModel = dataSnapshot.getValue(EbookDataModel.class);
                    eBookList.add(ebookDataModel);
                }
                Collections.reverse(eBookList);

                // Notify the adapter of data change
                ebookAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors if any
                showToast("Failed to fetch data");
            }
        });
    }


    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void onDeleteEbook(int position) {
        // Show a confirmation dialog to the user
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Deletion");
        builder.setMessage("Do you want to delete this notice?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteEbook(position);
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
    private void deleteEbook(int position) {
        EbookDataModel ebookDataModel = eBookList.get(position);
        String uniqueKey = ebookDataModel.getKey();
        String imageUrl = ebookDataModel.getEbookThumbnail();
        String pdfUrl = ebookDataModel.getPdfUrl();

        // Delete image from Firebase Storage if the image URL is not empty
        if (!imageUrl.isEmpty()) {
            StorageReference imageStorageReference = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);
            imageStorageReference.delete()
                    .addOnSuccessListener(aVoid -> {

                    })
                    .addOnFailureListener(exception -> {
                        showToast("Image deletion failed: " + exception.getMessage());
                    });
        }

            if (!pdfUrl.isEmpty()) {
                StorageReference pdfStorageReference = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl);
                pdfStorageReference.delete()
                        .addOnSuccessListener(aVoid -> {
                        })
                        .addOnFailureListener(exception -> {
                            showToast("PDF deletion failed: " + exception.getMessage());
                        });
            }
        databaseReference.child(uniqueKey).removeValue()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        showToast("E-Book Deleted");
                        fetchDataFromFirebase();
                    } else {
                        showToast("Oops! Something went wrong");
                    }
                })
                .addOnFailureListener(e -> {
                    showToast("Error: " + e.getMessage());
                });
    }
}




