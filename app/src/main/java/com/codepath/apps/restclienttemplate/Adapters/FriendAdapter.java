package com.codepath.apps.restclienttemplate.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.apps.restclienttemplate.Activities.ProfileActivity.Friend;
import com.codepath.apps.restclienttemplate.R;
import com.github.scribejava.core.extractors.TokenExtractor;

import java.util.List;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder> {
    List<Friend> friends;
    Context context;

    public FriendAdapter(Context context, List<Friend> friends){
        this.friends = friends;
        this.context = context;
    }
    @NonNull
    @Override
    public FriendAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View nuevo = LayoutInflater.from(context).inflate(R.layout.friend, parent, false);
        return new ViewHolder(nuevo);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendAdapter.ViewHolder holder, int position) {
        holder.bind(friends.get(position));
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView name;
        TextView username;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.friend_name);
            username = itemView.findViewById(R.id.friend_username);
        }
        public void bind(Friend friend){
            this.name.setText("" + friend.getName());
            this.username.setText("" + friend.getUsername());
        }
    }
}
