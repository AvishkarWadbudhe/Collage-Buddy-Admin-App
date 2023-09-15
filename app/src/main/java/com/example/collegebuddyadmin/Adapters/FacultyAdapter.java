package com.example.collegebuddyadmin.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.collegebuddyadmin.Listeners.OnFacultyClickListener;
import com.example.collegebuddyadmin.Models.FacultyDataModel;
import com.example.collegebuddyadmin.R;

import java.util.List;

public class FacultyAdapter extends RecyclerView.Adapter<FacultyAdapter.ViewHolder> {

    private final List<FacultyDataModel> facultyList;
    OnFacultyClickListener onFacultyClickListener;

    public FacultyAdapter(List<FacultyDataModel> facultyList,OnFacultyClickListener onFacultyClickListener) {
        this.facultyList = facultyList;
        this.onFacultyClickListener =onFacultyClickListener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textName;
        private final TextView textDesignation;
        private ImageView imageView;
        private TextView contactTextView;
        private Button deleteBtn;
        private Button editBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.teacherName);
            textDesignation = itemView.findViewById(R.id.designation);
            imageView = itemView.findViewById(R.id.profileImage);
            contactTextView = itemView.findViewById(R.id.contactNumber);
            deleteBtn = itemView.findViewById(R.id.deleteButton);
            editBtn = itemView.findViewById(R.id.editButton);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.faculty_container, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FacultyDataModel faculty = facultyList.get(position);

        holder.textName.setText(faculty.getName());
        holder.textDesignation.setText(faculty.getDesignation());
        // Load and set image using imageUrl with a library like Picasso or Glide
        // holder.imageView.setImageUrl(faculty.getImageUrl());
        holder.contactTextView.setText(faculty.getContact());


        if (!faculty.getImageUrl().equalsIgnoreCase("none")) {
            Glide.with(holder.itemView.getContext())
                    .load(faculty.getImageUrl())
                    .placeholder(R.drawable.placeholder_image)
                    .into(holder.imageView);
        } else {
            Glide.with(holder.itemView.getContext())
                    .load(R.drawable.placeholder_image)
                    .placeholder(R.drawable.placeholder_image)
                    .into(holder.imageView);
        }
        holder.deleteBtn.setOnClickListener(v ->
                onFacultyClickListener.onDeleteFacutly(position));
        holder.editBtn.setOnClickListener(v -> {
            onFacultyClickListener.onEditFaculty(position);
        });
    }

    @Override
    public int getItemCount() {
        return facultyList.size();
    }
}
