package com.example.androidexample;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.FriendViewHolder> {
    private List<Friend> friendsList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Friend friend);
    }

    public FriendAdapter(List<Friend> friendsList, OnItemClickListener listener) {
        this.friendsList = friendsList;
        this.listener = listener;
    }

    @Override
    public FriendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_item, parent, false);
        return new FriendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FriendViewHolder holder, int position) {
        Friend friend = friendsList.get(position);
        holder.bind(friend, listener);
    }

    @Override
    public int getItemCount() {
        return friendsList.size();
    }

    public static class FriendViewHolder extends RecyclerView.ViewHolder {
        public TextView usernameTextView;

        public FriendViewHolder(View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.friend_username);
        }

        public void bind(final Friend friend, final OnItemClickListener listener) {
            usernameTextView.setText(friend.getUsername());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(friend);
                }
            });
        }
    }
}