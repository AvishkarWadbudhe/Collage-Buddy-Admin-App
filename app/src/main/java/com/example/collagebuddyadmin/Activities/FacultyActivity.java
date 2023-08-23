package com.example.collagebuddyadmin.Activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.collagebuddyadmin.Adapters.FacultyAdapter;
import com.example.collagebuddyadmin.Fragments.FacultyFragments.AddFacultyFragment;
import com.example.collagebuddyadmin.Fragments.FacultyFragments.EditFacultyFragment;
import com.example.collagebuddyadmin.Listeners.OnFacultyClickListener;
import com.example.collagebuddyadmin.Models.FacultyDataModel;
import com.example.collagebuddyadmin.databinding.ActivityFacultyBinding;
import com.example.collagebuddyadmin.databinding.FacultyContainerBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FacultyActivity extends AppCompatActivity implements OnFacultyClickListener {

    ActivityFacultyBinding binding;
    FacultyContainerBinding facultyContainerBinding;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    List<FacultyDataModel> facultyList;
    FacultyAdapter facultyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFacultyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        databaseReference =  FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
        facultyList =new ArrayList<>();
        facultyAdapter = new FacultyAdapter(facultyList,this);
        fetchDataFromFirebase();


        binding.fabAddFaculty.setOnClickListener(v -> {
            showAddFacultyDialog();
        });
        setRecycleView();
    }
    private void fetchDataFromFirebase() {
        // Attach a ValueEventListener to the database reference
        DatabaseReference facultyReference = databaseReference.child("FacultyDetails");

        // Attach a ValueEventListener to the reference
        facultyReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        facultyList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                 FacultyDataModel faculty = snapshot.getValue(FacultyDataModel.class);
                      facultyList.add(faculty);


                }
                Collections.reverse(facultyList);
                facultyAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value

            }
        });
    }
    private void setRecycleView()
    {
        binding.recyclerViewFaculty.setAdapter(facultyAdapter);
        binding.recyclerViewFaculty.setLayoutManager(new LinearLayoutManager(this));
    }

    private void showAddFacultyDialog() {
        AddFacultyFragment bottomSheetDialog = new AddFacultyFragment();
        bottomSheetDialog.show(getSupportFragmentManager(), bottomSheetDialog.getTag());
    }
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void onDeleteFacutly(int position) {
        // Show a confirmation dialog to the user
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Deletion");
        builder.setMessage("Do you want to delete this ?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteFaculty( position);
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    @Override
    public void onEditFaculty(int position) {
        if (position >= 0 && position < facultyList.size()) {
            FacultyDataModel selectedFaculty = facultyList.get(position);

            // Create a bundle and show the bottom sheet dialog
            Bundle bundle = new Bundle();
            bundle.putSerializable("selectedFaculty", selectedFaculty);

            EditFacultyFragment bottomSheetDialog = new EditFacultyFragment();
            bottomSheetDialog.setArguments(bundle);
            bottomSheetDialog.show(getSupportFragmentManager(), bottomSheetDialog.getTag());
        } else {
            showToast("Invalid position");
        }
    }

    private void deleteFaculty(int position) {
        FacultyDataModel facultyDataModel = facultyList.get(position);
        String uniqueKey = facultyDataModel.getUniqueKey();
        String imageUrl = facultyDataModel.getImageUrl();


        // Delete image from Firebase Storage if the image URL is not empty
        if (!imageUrl.equalsIgnoreCase("none")) {
            StorageReference imageStorageReference = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);
            imageStorageReference.delete()
                    .addOnSuccessListener(aVoid -> {

                    })
                    .addOnFailureListener(exception -> {
                        showToast("Image deletion failed: " + exception.getMessage());
                    });
        }
        databaseReference.child("FacultyDetails").child(uniqueKey).removeValue()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        showToast("Data Deleted");
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