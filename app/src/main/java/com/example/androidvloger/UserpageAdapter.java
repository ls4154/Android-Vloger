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

public class UserpageAdapter extends RecyclerView.Adapter<UserpageAdapter.Holder>{
    private Context context;
    private ArrayList<Pair<String, String>> list = null; // 아이템의 데이터 저장

    // 생성자에서 데이터 리스트 객체를 전달받음.
    UserpageAdapter(ArrayList<Pair<String, String>> list) {
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
        Pair<String, String> item = list.get(position);
        holder.tvTitle.setText(item.first);
        Picasso.get().load(item.second).into(holder.imgThumbnail);
    }

    public class Holder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        ImageView imgThumbnail;
        Holder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            imgThumbnail = itemView.findViewById(R.id.imgThumbnail);
        }
    }

    @Override
    public int getItemCount() {
        return list.size() ;
    }
}