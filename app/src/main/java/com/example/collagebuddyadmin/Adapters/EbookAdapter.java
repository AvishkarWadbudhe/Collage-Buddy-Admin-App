package com.example.collagebuddyadmin.Adapters;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.collagebuddyadmin.Listeners.OnEbookClickListener;
import com.example.collagebuddyadmin.Listeners.OnNoticeClickListener;
import com.example.collagebuddyadmin.Models.EbookDataModel;
import com.example.collagebuddyadmin.R;


import java.util.List;

public class EbookAdapter extends RecyclerView.Adapter<EbookAdapter.EbookViewHolder> {

    private Context context;
    private List<EbookDataModel> ebookList;
    private final OnEbookClickListener onEbookClickListener;

    public EbookAdapter(List<EbookDataModel> ebookList,Context context, OnEbookClickListener onEbookClickListener) {
        this.ebookList = ebookList;
        this.context = context;
        this.onEbookClickListener = onEbookClickListener;
    }

    @NonNull
    @Override
    public EbookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.ebook_container, parent, false);
        return new EbookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EbookViewHolder holder, int position) {
        EbookDataModel ebook = ebookList.get(position);

        holder.ebookTitleTextView.setText(ebook.getEbookTitle());
        holder.dateTextView.setText(ebook.getDate());
        holder.TimeTextView.setText(ebook.getTime());

        if (ebook.getEbookThumbnail() != null) {
            Glide.with(holder.itemView.getContext())
                    .load(ebook.getEbookThumbnail())
                    .placeholder(R.drawable.placeholder_image)
                    .into(holder.thumbnailImageView);
        }
        else {
            Glide.with(holder.itemView.getContext())
                    .load(R.drawable.placeholder_image)
                    .placeholder(R.drawable.placeholder_image)
                    .into(holder.thumbnailImageView);
        }

        // Set an onClickListener to handle clicks on the items
        holder.itemView.setOnLongClickListener(v -> {
           onEbookClickListener.onDeleteEbook(position);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return ebookList.size();
    }

    static class EbookViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnailImageView;
        TextView ebookTitleTextView;
        TextView dateTextView;
        TextView TimeTextView;
        EbookViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnailImageView = itemView.findViewById(R.id.ebookThumbnail);
            ebookTitleTextView = itemView.findViewById(R.id.ebookTitleTextView);
            dateTextView = itemView.findViewById(R.id.DateTextView);
            TimeTextView = itemView.findViewById(R.id.TimeTextView);

        }
    }
}
