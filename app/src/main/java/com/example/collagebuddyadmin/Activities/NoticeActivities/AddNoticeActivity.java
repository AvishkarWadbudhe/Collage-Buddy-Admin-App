package com.example.collagebuddyadmin.Activities.NoticeActivities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.example.collagebuddyadmin.Models.NoticeDataModel;
import com.example.collagebuddyadmin.R;
import com.example.collagebuddyadmin.databinding.ActivityAddNoticeBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddNoticeActivity extends AppCompatActivity {
    private ActivityAddNoticeBinding binding;
    private final int ReqCode = 1;
    private Bitmap bitmap;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private String downloadUrl = "";
   private String date;
    private String time;

    private ProgressDialog progressDialog;
    private Calendar calendar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddNoticeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        calendar = Calendar.getInstance();
        progressDialog = new ProgressDialog(this);
        databaseReference = FirebaseDatabase.getInstance().getReference("Notice");
        storageReference = FirebaseStorage.getInstance().getReference();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MM-yy");
      date = currentDate.format(calendar.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        time = currentTime.format(calendar.getTime());

        binding.imageFrameLayout.setOnClickListener(v -> openGallery());

        binding.textDateTime.setText(date+" "+time);

        binding.saveNotice.setOnClickListener(v -> {
            if (validateFields()){
                 if (bitmap == null) {
                    UploadDataWithImage("");
                } else {
                    UploadImageAndTitle();
                }
            }
        });
        binding.imageBackButton.setOnClickListener(v -> {
            onBackPressed();
        });


    }
    private boolean validateFields() {
        if (binding.inputNoticeTitle.getText().toString().isEmpty()) {
            binding.inputNoticeTitle.setError("Title Empty");
            binding.inputNoticeTitle.requestFocus();
            return false;
        } else if (binding.inputNoticeDescription.getText().toString().isEmpty()) {
            binding.inputNoticeDescription.setError("Enter Notice");
            binding.inputNoticeDescription.requestFocus();
            return false;
        } else {
            return true;
        }
    }
    private void UploadDataWithImage(String imageUrl) {
        progressDialog.setMessage("Uploading");
        progressDialog.show();
        DatabaseReference noticeReference = databaseReference;
        final String uniqueKey = noticeReference.push().getKey();



        NoticeDataModel noticeDataModel = new NoticeDataModel(
                binding.inputNoticeTitle.getText().toString(),binding.inputNoticeDescription.getText().toString(),
                imageUrl, date, time, uniqueKey
        );

        noticeReference.child(uniqueKey).setValue(noticeDataModel)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        showToast("Notice Uploaded");
                        onBackPressed();
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
        uploadTask.addOnCompleteListener(this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
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
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ReqCode && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            try {
                // Use the fragment's context to access getContentResolver
                bitmap = MediaStore.Images.Media.getBitmap(AddNoticeActivity.this.getContentResolver(), uri);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            binding.noticeImage.setImageBitmap(bitmap);
            binding.textAddImage.setVisibility(View.GONE);
        }
    }
    private void showToast(String message) {
        Toast.makeText(AddNoticeActivity.this, message, Toast.LENGTH_SHORT).show();
    }

}