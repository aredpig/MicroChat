package com.example.mealshared.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mealshared.Models.ChatItem;
import com.example.mealshared.R;
import com.example.mealshared.Models.User;
import com.example.mealshared.Utils.BitmapUtils;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private User cur_user;
    private ArrayList<ChatItem> messageList;
    Context context;


    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    public MessageAdapter(User cur_user, ArrayList<ChatItem> messageList, Context context) {
        this.cur_user = cur_user;
        this.messageList = messageList;
        this.context = context;
    }

    @NonNull
    @Override
    public MessageAdapter.MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == MSG_TYPE_LEFT){
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_left,parent,false);
            return new MessageViewHolder(view);
        }else{
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_right,parent,false);
            return new MessageViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.MessageViewHolder holder, int position) {

        ChatItem chat = messageList.get(position);

        holder.messageItemView.setText(chat.getMessageContent());



    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(Integer.valueOf(messageList.get(position).getSender()) == (cur_user.getUid())){
            return MSG_TYPE_RIGHT;
        }else{
            return MSG_TYPE_LEFT;
        }
    }

    class MessageViewHolder extends RecyclerView.ViewHolder{
        public final ImageView messageImageView;
        public final TextView messageItemView;


        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageItemView =   itemView.findViewById(R.id.show_message);
            messageImageView = itemView.findViewById(R.id.profile_image);
        }
    }
}



