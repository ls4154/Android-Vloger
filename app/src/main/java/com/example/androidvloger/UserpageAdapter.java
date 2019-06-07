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

public class UserpageAdapter extends RecyclerView.Adapter<UserpageAdapter.Holder>{
    private Context context;
    private ArrayList<String[]> list = null; // 아이템의 데이터 저장
    final String IP_ADDR = "13.124.45.74";

    // 생성자에서 데이터 리스트 객체를 전달받음.
    UserpageAdapter(ArrayList<String[]> list) {
        this.list = list;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_userpage, parent,false);
        Holder holder = new Holder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        String[] item = list.get(position);
        holder.tvTitle.setText(item[1]);
        String path = "http://"+IP_ADDR+"/thumb"+item[0]+".jpg";
        Picasso.get().load(path).into(holder.imgThumbnail);
        holder.constraintLayout.setTag(item);
    }

    public class Holder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        ImageView imgThumbnail;
        ConstraintLayout constraintLayout;
        Holder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            imgThumbnail = itemView.findViewById(R.id.imgThumbnail);
            constraintLayout = itemView.findViewById(R.id.constraintLayout);
        }
    }

    @Override
    public int getItemCount() {
        return list.size() ;
    }
}