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

public class UserlistAdapter extends RecyclerView.Adapter<UserlistAdapter.Holder>{
    private Context context;
    private ArrayList<String> list = null; // 아이템의 데이터 저장

    // 생성자에서 데이터 리스트 객체를 전달받음.
    UserlistAdapter(ArrayList<String> list) {
        this.list = list;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_userlist, parent,false);
        Holder holder = new Holder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        String item = list.get(position);
        holder.tvUsername.setText(item);
    }

    public class Holder extends RecyclerView.ViewHolder {
        TextView tvUsername;
        Holder(View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
        }
    }

    @Override
    public int getItemCount() {
        return list.size() ;
    }
}