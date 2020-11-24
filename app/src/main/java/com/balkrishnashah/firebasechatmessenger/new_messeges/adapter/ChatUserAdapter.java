package com.balkrishnashah.firebasechatmessenger.new_messeges.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.balkrishnashah.firebasechatmessenger.Adapter.UserAdapter;
import com.balkrishnashah.firebasechatmessenger.R;
import com.balkrishnashah.firebasechatmessenger.message.MessageActivity;
import com.balkrishnashah.firebasechatmessenger.user_creation.User;
import com.bumptech.glide.Glide;

import java.util.List;

public class ChatUserAdapter extends RecyclerView.Adapter<ChatUserAdapter.ViewHolder>{
    private List<User> listOfUser;
    private Context mContext;

    public ChatUserAdapter(List<User> listOfUser, Context mContext) {
        this.listOfUser = listOfUser;
        this.mContext = mContext;
    }


    @NonNull
    @Override
    public ChatUserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatUserAdapter.ViewHolder holder, int position) {
         User ld = listOfUser.get(position);
        holder.username.setText(ld.getDisplayName());
        Log.d("ChatUser","username :"+ld.getDisplayName());
        holder.profile_image.setImageResource(R.drawable.user_default);
        Glide.with(mContext).asBitmap().load(ld.getProfileImageUrl()).into(holder.profile_image);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, MessageActivity.class);
                intent.putExtra("userid", ld.getUserId());
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return listOfUser.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView username;
        public ImageView profile_image;
        public ViewHolder(View itemView ) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            profile_image = itemView.findViewById(R.id.profile_image);
        }
    }
}
