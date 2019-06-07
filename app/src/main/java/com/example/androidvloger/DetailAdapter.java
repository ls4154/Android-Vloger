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

public class DetailAdapter extends RecyclerView.Adapter<DetailAdapter.Holder>{
    private Context context;
    private ArrayList<String[]> list = null; // 아이템의 데이터 저장
    final String IP_ADDR = "13.124.45.74";

    // 생성자에서 데이터 리스트 객체를 전달받음.
    DetailAdapter(ArrayList<String[]> list) {
        this.list = list;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent,false);
        Holder holder = new Holder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        String[] item = list.get(position);
        holder.tvUsername.setText(item[1]);
        holder.tvComment.setText(item[2]);
        holder.tvUsername.setTag(item[0]); // id만
    }

    public class Holder extends RecyclerView.ViewHolder {
        TextView tvUsername, tvComment;
        ConstraintLayout constraintLayout;
        Holder(View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvComment = itemView.findViewById(R.id.tvComment);
            constraintLayout = itemView.findViewById(R.id.constraintLayout);
        }
    }

    @Override
    public int getItemCount() {
        return list.size() ;
    }
}