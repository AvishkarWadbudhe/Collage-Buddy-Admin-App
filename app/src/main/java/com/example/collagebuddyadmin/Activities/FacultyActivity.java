package com.example.collagebuddyadmin.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.collagebuddyadmin.Fragments.AddFacultyFragment;
import com.example.collagebuddyadmin.R;
import com.example.collagebuddyadmin.databinding.ActivityFacultyBinding;
import com.example.collagebuddyadmin.databinding.FacultyContainerBinding;

public class FacultyActivity extends AppCompatActivity {

    ActivityFacultyBinding binding;
    FacultyContainerBinding facultyContainerBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFacultyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.fabAddFaculty.setOnClickListener(v -> {
            showAddFacultyDialog();
        });
    }

    private void showAddFacultyDialog() {
        AddFacultyFragment bottomSheetDialog = new AddFacultyFragment();
        bottomSheetDialog.show(getSupportFragmentManager(), bottomSheetDialog.getTag());
    }
}