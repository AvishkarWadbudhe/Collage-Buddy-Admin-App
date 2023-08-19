package com.example.collagebuddyadmin.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.collagebuddyadmin.Activities.EbookActivities.EbookActivity;
import com.example.collagebuddyadmin.Activities.NoticeActivities.NoticeActivity;
import com.example.collagebuddyadmin.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

 private    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();

    }
    private void setListeners()
    {
        binding.cardViewNotice.setOnClickListener(view -> {
            Intent intent =new Intent(MainActivity.this, NoticeActivity.class);
            startActivity(intent);
        });
        binding.cardViewNotes.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, EbookActivity.class);
            startActivity(intent);
        });
        binding.cardViewFacultyDetail.setOnClickListener(v -> {
            Intent intent =new Intent(MainActivity.this, FacultyActivity.class);
            startActivity(intent);
        });
    }
}