package com.example.androidvloger;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class TimelineAdapter extends RecyclerView.Adapter<TimelineAdapter.Holder>{
    private Context context;
    private ArrayList<ThumbItem> list = null; // 아이템의 데이터 저장

    // 생성자에서 데이터 리스트 객체를 전달받음.
    TimelineAdapter(ArrayList<ThumbItem> list) {
        this.list = list;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_timeline, parent,false);
        Holder holder = new Holder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        ThumbItem item = list.get(position);
        holder.tvTitle.setText(item.title);
        holder.tvUploader.setText(item.uploader);
        holder.tvUploadTime.setText(item.uploadTime);
        Picasso.get().load(item.imgPath).into(holder.imgThumbnail);
    }

    public class Holder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvUploader, tvUploadTime;
        ImageView imgThumbnail;
        Holder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvUploader = itemView.findViewById(R.id.tvUploader);
            tvUploadTime = itemView.findViewById(R.id.tvUploadtime);
            imgThumbnail = itemView.findViewById(R.id.imgThumbnail);
        }
    }

    @Override
    public int getItemCount() {
        return list.size() ;
    }

    public class ThumbItem {
        String imgPath;
        String uploader;
        String title;
        String uploadTime;
    }
}