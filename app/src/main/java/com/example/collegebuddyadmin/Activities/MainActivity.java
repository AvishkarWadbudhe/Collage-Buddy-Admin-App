package com.example.collegebuddyadmin.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.collegebuddyadmin.Activities.EbookActivities.EbookActivity;
import com.example.collegebuddyadmin.Activities.NoticeActivities.NoticeActivity;
import com.example.collegebuddyadmin.Activities.SyllabusActivities.SyllabusActivity;
import com.example.collegebuddyadmin.databinding.ActivityMainBinding;

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

        binding.cardViewSyllabus.setOnClickListener(v -> {
            Intent intent =new Intent(MainActivity.this, SyllabusActivity.class);
            startActivity(intent);
        });
    }
}