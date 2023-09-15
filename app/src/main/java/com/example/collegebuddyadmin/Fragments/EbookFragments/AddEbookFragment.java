package com.example.collegebuddyadmin.Fragments.EbookFragments;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.os.ParcelFileDescriptor;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.collegebuddyadmin.Models.EbookDataModel;
import com.example.collegebuddyadmin.R;
import com.example.collegebuddyadmin.databinding.FragmentAddEbookBinding;
import com.example.collegebuddyadmin.databinding.FragmentAddFacultyBinding;
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
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddEbookFragment extends BottomSheetDialogFragment {

    public AddEbookFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

   private FragmentAddEbookBinding binding;
    private final int ReqCode = 1;
    private Uri pdfData;

    private DatabaseReference databaseReference;
    private String eBookName;
    private StorageReference storageReference;

    private ProgressDialog progressDialog;

    private Calendar calendar;
    private Bitmap thumbnail;
    private String thumbnailUrl = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAddEbookBinding.inflate(inflater, container, false);


        View view = binding.getRoot();
        calendar = Calendar.getInstance();
        progressDialog = new ProgressDialog(getContext());
        databaseReference = FirebaseDatabase.getInstance().getReference("Ebooks");
        storageReference = FirebaseStorage.getInstance().getReference();

        binding.selectPdfCardview.setOnClickListener(v -> {
            openGallery();
        });
        binding.btnSave.setOnClickListener(v -> {
            Upload_Ebook_Thumbnail();
        });

        return view;
    }
    private void Upload_Ebook_Thumbnail() {
        progressDialog.setTitle("Please wait...");
        progressDialog.setMessage("Uploading E-Book...");
        progressDialog.show();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] finalImg = byteArrayOutputStream.toByteArray();
        final StorageReference filePath = storageReference.child("Ebook_Thumbnails").child(System.currentTimeMillis() + ".jpg");

        final UploadTask uploadTask = filePath.putBytes(finalImg);
        uploadTask.addOnCompleteListener(getActivity(), new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    thumbnailUrl = String.valueOf(uri);
                                    uploadPdf();
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


    private void uploadPdf() {

        StorageReference reference = storageReference.child("Ebooks/" + binding.eBookTitle.getText().toString() + ".pdf");

        reference.putFile(pdfData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Get the download URL of the uploaded file
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();

                uriTask.addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> uriTask) {
                        if (uriTask.isSuccessful()) {
                            Uri uri = uriTask.getResult();
                            // Upload the data with the obtained download URL
                            uploadData(uri.toString());
                        } else {
                            showToast("Failed to retrieve download URL");
                        }
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showToast("Oops! Something went wrong");
                progressDialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showToast("Oops! Something went wrong");
                progressDialog.dismiss();
            }
        });
    }

    private void uploadData(String pdfUrl) {
        DatabaseReference eBookReference = databaseReference;
        final String uniqueKey = eBookReference.push().getKey();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MM-yy");
        String date = currentDate.format(calendar.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        String time = currentTime.format(calendar.getTime());


        EbookDataModel ebookDataModel = new EbookDataModel(
                binding.eBookTitle.getText().toString(), thumbnailUrl, date, pdfUrl, time, uniqueKey
        );
        eBookReference.child(uniqueKey).setValue(ebookDataModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
                showToast("E-Book Uploaded successfully");
                dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();

                showToast("Failed to upload E-Book");
            }
        });

    }

    private void openGallery() {
        Intent intent = new Intent();
        String[] mimeTypes = {"application/pdf", "application/msword", "application/vnd.ms-powerpoint"};
        intent.setType("*/*");  // This will allow any file type
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);  // Set allowed MIME types
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Document"), ReqCode);
    }

    @SuppressLint("Range")
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ReqCode && resultCode == RESULT_OK) {
            pdfData = data.getData();
            thumbnail = generatePdfThumbnail(pdfData);
            if (thumbnail != null) {
                binding.pdfThumbnail.setImageBitmap(thumbnail);
                binding.textSelectPdf.setVisibility(View.GONE);
            } else {
                showToast("Failed to generate PDF thumbnail");
                binding.textSelectPdf.setVisibility(View.VISIBLE);

            }
            if (pdfData.toString().startsWith("content://")) {
                Cursor cursor = null;
                try {
                    cursor = getActivity().getContentResolver().query(pdfData, null, null, null, null);
                    if (cursor != null && cursor.moveToFirst()) {
                        eBookName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

            } else if (pdfData.toString().startsWith("file://")) {
                eBookName = new File(pdfData.toString()).getName();
            }
            binding.pdfName.setText(eBookName.toString());
        }
    }

    private Bitmap generatePdfThumbnail(Uri pdfUri) {
        try {
            // Replace requireActivity() with requireContext()
            ParcelFileDescriptor parcelFileDescriptor = requireContext().getContentResolver().openFileDescriptor(pdfUri, "r");

            if (parcelFileDescriptor != null) {
                PdfRenderer pdfRenderer = new PdfRenderer(parcelFileDescriptor);
                PdfRenderer.Page page = pdfRenderer.openPage(0);

                Bitmap bitmap = Bitmap.createBitmap(page.getWidth(), page.getHeight(), Bitmap.Config.ARGB_8888);
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_PRINT);

                page.close();
                pdfRenderer.close();
                parcelFileDescriptor.close();

                if (bitmap.equals("")) {
                    showToast("hehehhhe");
                }

                return bitmap;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}