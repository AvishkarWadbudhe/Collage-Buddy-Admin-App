package com.example.collagebuddyadmin.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.collagebuddyadmin.Listeners.OnNoticeClickListener;
import com.example.collagebuddyadmin.Models.NoticeDataModel;
import com.example.collagebuddyadmin.R;

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



        public NoticeViewHolder(View itemView) {
            super(itemView);
            noticeImage = itemView.findViewById(R.id.noticePreviewImage);
            noticeTitle = itemView.findViewById(R.id.noticeTitleTextView);
            noticeDate = itemView.findViewById(R.id.noticeDateTextView);
            noticeTime = itemView.findViewById(R.id.noticeTimeTextView);
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

        if (notice.getImage() != null) {
            Glide.with(holder.itemView.getContext())
                    .load(notice.getImage())
                    .placeholder(R.drawable.placeholder_image)
                    .into(holder.noticeImage);
        } else {
            Glide.with(holder.itemView.getContext())
                    .load(R.drawable.placeholder_image)
                    .placeholder(R.drawable.placeholder_image)
                    .into(holder.noticeImage);
        }

        holder.itemView.setOnLongClickListener(v -> {
            onNoticeClickListener.onDeleteNotice(position);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return noticeList.size();
    }
}