package com.example.collegebuddyadmin.Activities.SyllabusActivities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.example.collegebuddyadmin.R;
import com.example.collegebuddyadmin.databinding.ActivitySyllabusBinding;

public class SyllabusActivity extends AppCompatActivity {
private ActivitySyllabusBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySyllabusBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSpinners();

    }
    private void setSpinners(){
// Populate class spinner
        ArrayAdapter<CharSequence> classAdapter = ArrayAdapter.createFromResource(this, R.array.classes, android.R.layout.simple_spinner_item);
        classAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerClass.setAdapter(classAdapter);

// Set a listener for class spinner
        binding.spinnerClass.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // When a class is selected, update semester spinner options
                updateSemesterSpinner(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

// Initialize the semester spinner with an empty adapter (to be updated dynamically)
        ArrayAdapter<CharSequence> semesterAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        semesterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerSemester.setAdapter(semesterAdapter);

    }
    private void updateSemesterSpinner(int selectedClassPosition) {
        String[] semesterOptions;

        switch (selectedClassPosition) {
            case 1: // BCA
                semesterOptions = getResources().getStringArray(R.array.UG_Degree_semester);
                break;
            case 2: // MSC-CS
                semesterOptions = getResources().getStringArray(R.array.PG_Degree_semester);
                break;
            case 3: // PGDCA
            case 4: // DCA
                semesterOptions = getResources().getStringArray(R.array.Diploma_semester);
                break;
            default:
                semesterOptions = new String[0];
        }

        ArrayAdapter<CharSequence> semesterAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, semesterOptions);
        semesterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerSemester.setAdapter(semesterAdapter);
    }

}