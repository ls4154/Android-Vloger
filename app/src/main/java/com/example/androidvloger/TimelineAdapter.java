package com.example.androidvloger;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
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
    private ArrayList<String[]> list = null; // 아이템의 데이터 저장
    final String IP_ADDR = "13.124.45.74";


    // 생성자에서 데이터 리스트 객체를 전달받음.
    TimelineAdapter(ArrayList<String[]> list) {
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
        String[] item = list.get(position);
        String path = "http://"+IP_ADDR+"/thumb"+item[0]+".jpg";
        holder.tvTitle.setText(item[1]);
        holder.tvUploader.setText(item[5]);
        holder.tvUploadTime.setText(item[4]);
        Picasso.get().load(path).into(holder.imgThumbnail);
        holder.constraintLayout.setTag(item[0]);
    }

    public class Holder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvUploader, tvUploadTime;
        ImageView imgThumbnail;
        ConstraintLayout constraintLayout;
        Holder(View itemView) {
            super(itemView);
            constraintLayout = itemView.findViewById(R.id.constraintLayout);
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
}