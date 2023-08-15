package com.example.collagebuddyadmin.Activities.NoticeActivities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.collagebuddyadmin.Adapters.NoticeAdapter;
import com.example.collagebuddyadmin.Listeners.OnNoticeClickListener;
import com.example.collagebuddyadmin.Models.NoticeDataModel;
import com.example.collagebuddyadmin.databinding.ActivityNoticeBinding;
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
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
        calendar = Calendar.getInstance();
        progressDialog = new ProgressDialog(this);
        databaseReference = FirebaseDatabase.getInstance().getReference("Notice");
        storageReference = FirebaseStorage.getInstance().getReference();
        noticeList = new ArrayList<>();
        noticeAdapter = new NoticeAdapter(noticeList, this);

        binding.recyclerViewNotices.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewNotices.setAdapter(noticeAdapter);
        fetchDataFromFirebase();


        binding.selectImageBtn.setOnClickListener(view -> openGallery());

        binding.uploadNoticeBtn.setOnClickListener(view -> {
            if (binding.noticeTitle.getText().toString().trim().isEmpty()) {
                binding.noticeTitle.setError("Title Empty");
                binding.noticeTitle.requestFocus();
            } else if (bitmap == null) {
                UploadDataWithImage("");
            } else {
                UploadImageAndTitle();
            }
        });
    }

    private void fetchDataFromFirebase() {
        // Attach a ValueEventListener to the database reference
        databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    noticeList.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        NoticeDataModel notice = dataSnapshot.getValue(NoticeDataModel.class);
                        noticeList.add(notice);
                    }
                if (noticeList.isEmpty()) {
                    binding.noNotice.setVisibility(View.VISIBLE);
                    binding.prevNotice.setVisibility(View.GONE);
                } else {
                    binding.noNotice.setVisibility(View.GONE);
                    binding.prevNotice.setVisibility(View.VISIBLE);
                }
                    // Notify the adapter of data change
                    noticeAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors if any
                showToast("Failed to fetch data");
            }
        });
    }

    private void UploadDataWithImage(String imageUrl) {
        progressDialog.setMessage("Uploading");
        progressDialog.show();
        DatabaseReference noticeReference = databaseReference;
        final String uniqueKey = noticeReference.push().getKey();

        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MM-yy");
        String date = currentDate.format(calendar.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        String time = currentTime.format(calendar.getTime());

        NoticeDataModel noticeDataModel = new NoticeDataModel(
                binding.noticeTitle.getText().toString(),
                imageUrl, date, time, uniqueKey
        );

        noticeReference.child(uniqueKey).setValue(noticeDataModel)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        showToast("Notice Uploaded");
                        clear();
                        // Update the RecyclerView with the new notice
                        noticeList.add(noticeDataModel);
                        fetchDataFromFirebase();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        showToast("Oops! Something went wrong");
                    }
                });
    }

    private void UploadImageAndTitle() {
        progressDialog.setMessage("Uploading");
        progressDialog.show();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] finalImg = byteArrayOutputStream.toByteArray();
        final StorageReference filePath = storageReference.child("Notice").child(System.currentTimeMillis() + ".jpg");

        final UploadTask uploadTask = filePath.putBytes(finalImg);
        uploadTask.addOnCompleteListener(NoticeActivity.this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
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
                                    UploadDataWithImage(downloadUrl);
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ReqCode && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            binding.prevImage.setImageBitmap(bitmap);
        }
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

    public void deleteNotice(int position) {
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

    private void clear() {
        binding.noticeTitle.setText(null);
        binding.prevImage.setImageBitmap(null);
    }
}
