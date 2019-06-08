package com.example.androidvloger;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.Holder>{
    private Context context;
    private ArrayList<NotificationItem> list = null; // 아이템의 데이터 저장
    final String IP_ADDR = "13.124.45.74";


    // 생성자에서 데이터 리스트 객체를 전달받음.
    NotificationAdapter(ArrayList<NotificationItem> list) {
        this.list = list;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent,false);
        Holder holder = new Holder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        NotificationItem item = list.get(position);
        holder.tvUsername.setText(item.who);
        String content = "";
        if(item.type==0) content = "댓글 : "+item.title;
        else if(item.type==1) content = "영상 : "+item.title;
        else if(item.type==2) content = "좋아요를 눌렀습니다";
        else if(item.type==3) content = "팔로우했습니다";
        holder.tvContent.setText(content);
        String path = "http://"+IP_ADDR+"/thumb"+item.vidId+".jpg";
        Picasso.get().load(path).into(holder.imgThumbnail);
        holder.constraintLayout.setTag(item.vidId);
    }

    public class Holder extends RecyclerView.ViewHolder {
        ImageView imgThumbnail;
        TextView tvUsername, tvContent;
        ConstraintLayout constraintLayout;
        Holder(View itemView) {
            super(itemView);
            imgThumbnail = itemView.findViewById(R.id.imgThumbnail);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvContent = itemView.findViewById(R.id.tvContent);
            constraintLayout = itemView.findViewById(R.id.constraintLayout);
        }
    }

    @Override
    public int getItemCount() {
        return list.size() ;
    }
}