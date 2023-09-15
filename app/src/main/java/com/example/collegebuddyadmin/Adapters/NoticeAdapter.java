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
import com.example.collegebuddyadmin.Listeners.OnNoticeClickListener;
import com.example.collegebuddyadmin.Models.NoticeDataModel;
import com.example.collegebuddyadmin.R;

import java.util.List;

public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.NoticeViewHolder> {

    private final List<NoticeDataModel> noticeList;
    private final OnNoticeClickListener onNoticeClickListener;

    public NoticeAdapter(List<NoticeDataModel> noticeList, OnNoticeClickListener onNoticeClickListener) {
        this.noticeList = noticeList;
        this.onNoticeClickListener = onNoticeClickListener;
    }


    public static class NoticeViewHolder extends RecyclerView.ViewHolder {
        private final ImageView noticeImage;
        private final TextView noticeTitle;
        private final TextView noticeDate;
        private final TextView noticeTime;
        private final TextView notice;
        private final TextView edited;
        private final Button deleteBtn;
        private final Button editBtn;



        public NoticeViewHolder(View itemView) {
            super(itemView);
            noticeImage = itemView.findViewById(R.id.noticeImage);
            notice = itemView.findViewById(R.id.noticeDescription);
            noticeTitle = itemView.findViewById(R.id.noticeTitle);
            noticeDate = itemView.findViewById(R.id.noticeDate);
            noticeTime = itemView.findViewById(R.id.noticeTime);
            deleteBtn = itemView.findViewById(R.id.deleteButton);
            editBtn = itemView.findViewById(R.id.editButton);
            edited = itemView.findViewById(R.id.edited);
        }
    }

    @NonNull
    @Override
    public NoticeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notice_container, parent, false);
        return new NoticeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoticeViewHolder holder, int position) {
        NoticeDataModel notice = noticeList.get(position);
        holder.noticeTitle.setText(notice.getTitle());
        holder.noticeDate.setText(notice.getDate());
        holder.noticeTime.setText(notice.getTime());
        holder.notice.setText(notice.getNotice());

        if(notice.getEdited().equalsIgnoreCase("yes"))
        {
            holder.edited.setVisibility(View.VISIBLE);
        }
        else {
            holder.edited.setVisibility(View.GONE);
        }

        if (!notice.getImage().equalsIgnoreCase("none")) {
            Glide.with(holder.itemView.getContext())
                    .load(notice.getImage())
                    .placeholder(R.drawable.placeholder_image)
                    .into(holder.noticeImage);
            holder.noticeImage.setVisibility(View.VISIBLE); // Set visibility to VISIBLE if there's an image
        } else {
            holder.noticeImage.setVisibility(View.GONE); // Set visibility to GONE if there's no image
        }

        holder.deleteBtn.setOnClickListener(v -> {
            onNoticeClickListener.onDeleteNotice(position);
        });
        holder.editBtn.setOnClickListener(v -> {
            onNoticeClickListener.onEditFaculty(position);
        });
    }

    @Override
    public int getItemCount() {
        return noticeList.size();
    }
}