package com.example.mealshared.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.util.Consumer;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mealshared.AVLTree;
import com.example.mealshared.Utils.BitmapUtils;
import com.example.mealshared.DatabaseHelper;
import com.example.mealshared.Activities.MessageActivity;
import com.example.mealshared.R;
import com.example.mealshared.Models.User;

import java.util.ArrayList;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.FriendsViewHolder>{


    private Context context;
    private ArrayList<String> friends;
    private User cur_user;
    private AVLTree tree;
    private ArrayList<User> allUsers;
    private DatabaseHelper DB = DatabaseHelper.getInstance();

    public FriendAdapter(Context context, ArrayList<String> friends, User cur_user,ArrayList<User> allUsers) {
        this.context = context;
        this.friends = friends;
        this.cur_user = cur_user;
        this.allUsers = allUsers;
    }

    @NonNull
    @Override
    public FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.friend_item,parent,false);
        return new FriendsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendsViewHolder holder, int position) {
        String friendname = friends.get(position);
        DB.getTree(tempTree ->{
            tree = tempTree;
            String realUsername = tree.find(Integer.parseInt(friendname)).getName();
            String userAvatar = tree.find(Integer.parseInt(friendname)).getAvatar();
            if (userAvatar == null || userAvatar == "") {
            } else {
                holder.userImage.setImageBitmap(BitmapUtils.base64ToBitmap(userAvatar));
            }
            holder.userName.setText("@" + realUsername);
        });
//        holder.userName.setText(friendname);
        if(cur_user.getBlocks().contains(friendname)){
            holder.blockBtn.setText("UNBLOCK");
        }else{
            holder.blockBtn.setText("BLOCK");
        }

        //TODO: use Glide library to import customized image profile.

        holder.blockBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // At the beginning, reload cur_user from database.
                DB.getUser(cur_user.getEmail(), new Consumer<User>() {
                    @Override
                    public void accept(User user) {
                        cur_user = user;
                        // Selected user is not blocked.
                        if(! cur_user.getBlocks().contains(friendname)){
                            cur_user.getBlocks().add(friendname);
                            DB.UpdateUsersData(cur_user);
                            Toast.makeText(context,"You have blocked "+friendname,Toast.LENGTH_SHORT).show();
                            holder.blockBtn.setText("UNBLOCK");
                        }else{
                            cur_user.getBlocks().remove(friendname);
                            DB.UpdateUsersData(cur_user);
                            Toast.makeText(context,"You have unblocked "+friendname,Toast.LENGTH_SHORT).show();
                            holder.blockBtn.setText("BLOCK");
                        }
                    }
                });

            }
        });

        holder.chatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User clickedUser = null;
                for(User u: allUsers){
                    if(Integer.valueOf(u.getUid()).toString().equals(friendname)){
                        clickedUser =u;
                    }
                }

                if(!clickedUser.getBlocks().contains(Integer.valueOf(cur_user.getUid()).toString())){
                    Intent msgPage = new Intent(context, MessageActivity.class);
                    msgPage.putExtra("sender_uid",Integer.valueOf(cur_user.getUid()).toString());
                    // TODO: Fix the format of string and int.
                    msgPage.putExtra("receiver_uid",friendname);
                    context.startActivity(msgPage);
                }else{
                    Toast.makeText(context,"You have been blocked! Cannot send message!",Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    class FriendsViewHolder extends RecyclerView.ViewHolder{
        public final ImageView userImage;
        public final TextView userName;
        public final Button blockBtn;
        public final Button   chatBtn;

        public FriendsViewHolder(@NonNull View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.userimage_in_friendslist);
            userName = itemView.findViewById(R.id.username_in_friendslist);
            blockBtn = itemView.findViewById(R.id.block_button);
            chatBtn = itemView.findViewById(R.id.chat_button);

        }
    }
}
