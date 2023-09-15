package com.example.collegebuddyadmin.Activities.NoticeActivities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.collegebuddyadmin.Models.NoticeDataModel;
import com.example.collegebuddyadmin.R;
import com.example.collegebuddyadmin.databinding.ActivityEditNoticeBinding;
import com.google.android.gms.tasks.OnCompleteListener;
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

public class EditNoticeActivity extends AppCompatActivity {

    private ActivityEditNoticeBinding binding;
    private final int ReqCode = 1;
    private Bitmap bitmap;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private String date;
    private String time;

    private ProgressDialog progressDialog;
    private Calendar calendar;
 private    NoticeDataModel noticeDataModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditNoticeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        calendar = Calendar.getInstance();
        progressDialog = new ProgressDialog(this);
        databaseReference = FirebaseDatabase.getInstance().getReference("Notice");
        storageReference = FirebaseStorage.getInstance().getReference();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MM-yy");
        date = currentDate.format(calendar.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        time = currentTime.format(calendar.getTime());

        // Retrieve the bundle from the intent
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            // Retrieve the serialized data from the bundle
          noticeDataModel = (NoticeDataModel) bundle.getSerializable("selectedNotice");

            binding.inputNoticeTitle.setText(noticeDataModel.getTitle());
            binding.inputNoticeDescription.setText(noticeDataModel.getNotice());

            if(noticeDataModel.getImage().equalsIgnoreCase("none")){
                binding.textAddImage.setVisibility(View.VISIBLE);
            }
            Glide.with(this)
                    .asBitmap()
                    .load(noticeDataModel.getImage())
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            binding.noticeImage.setImageBitmap(resource);
                            binding.textAddImage.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {

                        }
                    });
        }


        binding.imageFrameLayout.setOnClickListener(v -> openGallery());

        binding.textDateTime.setText(date+" "+time);

        binding.saveNotice.setOnClickListener(v -> {
            if (validateFields()){
                UpdateNoticeImage(noticeDataModel.getKey(),noticeDataModel.getImage());
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
    private void updateNoticeData(String noticeId,String imgUrl) {
        progressDialog.setMessage("Updating");
        progressDialog.show();

        DatabaseReference noticeRef = databaseReference.child(noticeId);

        // Proceed to update faculty data
        NoticeDataModel updatednoitce = new NoticeDataModel(
                binding.inputNoticeTitle.getText().toString(),binding.inputNoticeDescription.getText().toString(),
                imgUrl, date, time, noticeId,"yes"
        );

        noticeRef.setValue(updatednoitce)
                .addOnSuccessListener(unused -> {
                    progressDialog.dismiss();
                    showToast("Notice Updated");
                  onBackPressed();
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    showToast("Oops! Something went wrong");
                });
    }
    private void deleteNotice(String oldImgUrl) {
        // Delete image from Firebase Storage

        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(oldImgUrl);
        storageReference.delete()
                .addOnSuccessListener(aVoid -> {
                })
                .addOnFailureListener(exception -> {
                    showToast("Image deletion failed: " + exception.getMessage());
                });
    }
    private void UpdateNoticeImage(String facultyId,String oldImgUrl) {
        progressDialog.setMessage("Uploading");
        progressDialog.show();

        if (bitmap == null) {
            showToast("No image selected. Using default image URL.");
            updateNoticeData(facultyId, oldImgUrl);
            progressDialog.dismiss();
            return;
        }


        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] finalImg = byteArrayOutputStream.toByteArray();
        final StorageReference filePath = storageReference.child("FacultyPictures").child(System.currentTimeMillis() + ".jpg");

        final UploadTask uploadTask = filePath.putBytes(finalImg);
        uploadTask.addOnCompleteListener(EditNoticeActivity.this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                   String imageUrl = String.valueOf(uri);
                                    if(!oldImgUrl.equalsIgnoreCase("none"))
                                    {  deleteNotice(oldImgUrl);}
                                    updateNoticeData(facultyId,imageUrl);
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
                bitmap = MediaStore.Images.Media.getBitmap(EditNoticeActivity.this.getContentResolver(), uri);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            binding.noticeImage.setImageBitmap(bitmap);
            binding.textAddImage.setVisibility(View.GONE);
        }
    }
    private void showToast(String message) {
        Toast.makeText(EditNoticeActivity.this, message, Toast.LENGTH_SHORT).show();
    }

}