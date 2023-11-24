package com.anniljing.udpprogect;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<MessageEntity> mEntities;

    public MessageAdapter(List<MessageEntity> entities) {
        mEntities = entities;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        if (MessageEntity.ROLE_HOST == viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_host, parent,false);
            viewHolder = new MyHostViewHolder(itemView);
        } else {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_client, parent,false);
           viewHolder = new MyClientViewHolder(itemView);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MessageEntity entity = mEntities.get(position);
        int type = entity.getRole();
        switch (type) {
            case MessageEntity.ROLE_HOST:
                MyHostViewHolder hostViewHolder = (MyHostViewHolder) holder;
                hostViewHolder.tvContent.setText(entity.getMessage());
                break;
            case MessageEntity.ROLE_CLIENT:
                MyClientViewHolder clientViewHolder = (MyClientViewHolder) holder;
                clientViewHolder.tvContent.setText(entity.getMessage());
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mEntities.get(position).getRole();
    }

    @Override
    public int getItemCount() {
        return mEntities.size();
    }

    protected class MyHostViewHolder extends RecyclerView.ViewHolder {
        private TextView tvContent;

        public MyHostViewHolder(@NonNull View itemView) {
            super(itemView);
            tvContent = itemView.findViewById(R.id.itemContentHost);
        }
    }

    protected class MyClientViewHolder extends RecyclerView.ViewHolder {
        private TextView tvContent;

        public MyClientViewHolder(@NonNull View itemView) {
            super(itemView);
            tvContent = itemView.findViewById(R.id.itemContentClient);
        }
    }
}
