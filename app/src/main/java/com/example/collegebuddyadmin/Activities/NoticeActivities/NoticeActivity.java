package com.example.collegebuddyadmin.Activities.NoticeActivities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.collegebuddyadmin.Adapters.NoticeAdapter;
import com.example.collegebuddyadmin.Listeners.OnNoticeClickListener;
import com.example.collegebuddyadmin.Models.NoticeDataModel;
import com.example.collegebuddyadmin.databinding.ActivityNoticeBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class NoticeActivity extends AppCompatActivity implements OnNoticeClickListener {

    private ActivityNoticeBinding binding;
    private final int ReqCode = 1;
    private Bitmap bitmap;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private String downloadUrl = "";

    private ProgressDialog progressDialog;
    private Calendar calendar;
    private List<NoticeDataModel> noticeList;
    private NoticeAdapter noticeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNoticeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        databaseReference = FirebaseDatabase.getInstance().getReference("Notice");
        storageReference = FirebaseStorage.getInstance().getReference();
        noticeList = new ArrayList<>();
        noticeAdapter = new NoticeAdapter(noticeList, this);
        fetchDataFromFirebase();

        binding.noticeRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.noticeRecyclerView.setAdapter(noticeAdapter);

        binding.fabAddNotice.setOnClickListener(v -> {
            Intent intent = new Intent(NoticeActivity.this, AddNoticeActivity.class);
            startActivity(intent);
    fetchDataFromFirebase();
        });
binding.imageBackButton.setOnClickListener(v -> {
    onBackPressed();
});

    }
    private void fetchDataFromFirebase() {
        // Create a query to retrieve data in descending order of timestamps

        databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                noticeList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    NoticeDataModel noticeDataModel = snapshot.getValue(NoticeDataModel.class);
                    noticeList.add(noticeDataModel);


                }
                Collections.reverse(noticeList);
                noticeAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value

            }
        });



    }

    public void onDeleteNotice(int position) {
        // Show a confirmation dialog to the user
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Deletion");
        builder.setMessage("Do you want to delete this notice?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteNotice(position);
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    @Override
    public void onEditFaculty(int position) {
        if (position >= 0 && position < noticeList.size()) {
            NoticeDataModel noticeDataModel = noticeList.get(position);

            Bundle bundle = new Bundle();
            bundle.putSerializable("selectedNotice", noticeDataModel);

            Intent intent = new Intent(NoticeActivity.this, EditNoticeActivity.class);
            intent.putExtras(bundle); // Use putExtras() to add the bundle to the intent
            startActivity(intent); // Start the EditNoticeActivity
        } else {
            showToast("Invalid position");
        }
        fetchDataFromFirebase();
    }


    private void deleteNotice(int position) {
        NoticeDataModel notice = noticeList.get(position);
        String uniqueKey = notice.getKey();
        String imageUrl = notice.getImage(); // Assuming there's a method to get the image URL

        // Delete image from Firebase Storage
        if(!Objects.equals(imageUrl, ""))
        {
            StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);
            storageReference.delete()
                    .addOnSuccessListener(aVoid -> {
                        // Image deletion successful, now delete from the Firebase database
                    })
                    .addOnFailureListener(exception -> {
                        showToast("Image deletion failed: " + exception.getMessage());
                    });
        }

        databaseReference.child(uniqueKey).removeValue()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        showToast("Notice Deleted");
                        fetchDataFromFirebase();
                    } else {
                        showToast("Oops! Something went wrong");
                    }
                })
                .addOnFailureListener(e -> {
                    showToast("Error: " + e.getMessage());
                });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}
