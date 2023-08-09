package com.example.collagebuddyadmin.Activities.EbookActivities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.collagebuddyadmin.Adapters.NoticeAdapter;
import com.example.collagebuddyadmin.Models.NoticeDataModel;
import com.example.collagebuddyadmin.R;
import com.example.collagebuddyadmin.databinding.ActivityEbookBinding;
import com.example.collagebuddyadmin.databinding.ActivityNoticeBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

public class EbookActivity extends AppCompatActivity {

   private ActivityEbookBinding binding;

    private final int ReqCode = 1;
    private Uri pdfData;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    private ProgressDialog progressDialog;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEbookBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        calendar = Calendar.getInstance();
        progressDialog = new ProgressDialog(this);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
// Spinner setup
           setClasses();

           binding.selectbookBtn.setOnClickListener(view -> {
               openGallery();
           });
    }
    private void setClasses()
    {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.notice_types_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.selectClasses.setAdapter(adapter);
    }
    private void openGallery() {
        Intent intent = new Intent();
        String[] mimeTypes = {"application/pdf", "application/msword", "application/vnd.ms-powerpoint"};
        intent.setType("*/*");  // This will allow any file type
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);  // Set allowed MIME types
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Document"), ReqCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ReqCode && resultCode == RESULT_OK) {
         pdfData =data.getData();
            Bitmap thumbnail = generatePdfThumbnail(pdfData);
            if (thumbnail != null) {
                binding.prevImage.setImageBitmap(thumbnail);
            } else {
                showToast("Failed to generate PDF thumbnail");
            }
        }
    }
    private Bitmap generatePdfThumbnail(Uri pdfUri) {
        try {
            ParcelFileDescriptor parcelFileDescriptor = getContentResolver().openFileDescriptor(pdfUri, "r");
            if (parcelFileDescriptor != null) {
                PdfRenderer pdfRenderer = new PdfRenderer(parcelFileDescriptor);
                PdfRenderer.Page page = pdfRenderer.openPage(0);

                Bitmap bitmap = Bitmap.createBitmap(page.getWidth(), page.getHeight(), Bitmap.Config.ARGB_8888);
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

                page.close();
                pdfRenderer.close();
                parcelFileDescriptor.close();

                return bitmap;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    private void clear() {
        binding.bookTitle.setText(null);
        binding.prevImage.setImageBitmap(null);
    }
}




