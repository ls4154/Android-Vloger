package com.example.androidvloger;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.timeline_item, parent,false);
        Holder holder = new Holder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        ThumbItem item = list.get(position);
        holder.title.setText(item.title);
        holder.uploader.setText(item.uploader);
        holder.uploadTime.setText(item.uploadTime);
        Picasso.get().load(item.path).into(holder.thumbnail);
    }

    public class Holder extends RecyclerView.ViewHolder {
        TextView title, uploader, uploadTime;
        ImageView thumbnail;
        Holder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            uploader = itemView.findViewById(R.id.uploader);
            uploadTime = itemView.findViewById(R.id.uploadTime);
            thumbnail = itemView.findViewById(R.id.thumbnail);
        }
    }

    @Override
    public int getItemCount() {
        return list.size() ;
    }

    public class ThumbItem {
        String path;
        String uploader;
        String title;
        String uploadTime;
    }
}