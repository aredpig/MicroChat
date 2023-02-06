package com.example.mealshared.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.util.Consumer;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mealshared.AVLTree;
import com.example.mealshared.Utils.BitmapUtils;
import com.example.mealshared.DatabaseHelper;
import com.example.mealshared.Activities.LoginActivity;
import com.example.mealshared.R;
import com.example.mealshared.Models.User;

import java.util.ArrayList;
import java.util.List;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.RequestsViewHolder>{


    private Context context;
    private ArrayList<String> friendsRequests;
    private User cur_user;
    private AVLTree tree;
    private ArrayList<User> allUsers;
    private DatabaseHelper DB = DatabaseHelper.getInstance();

    public RequestAdapter(Context context, ArrayList<String> friendsRequests, User cur_user,ArrayList<User> allUsers) {
        this.context = context;
        this.friendsRequests = friendsRequests;
        this.cur_user = cur_user;
        this.allUsers = allUsers;
    }

    @NonNull
    @Override
    public RequestsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.request_item,parent,false);
        return new RequestsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestsViewHolder

                                             holder, int position) {
        String strangerName = friendsRequests.get(position);
        DB.getTree(tempTree ->{
            tree = tempTree;
            String realUsername = tree.find(Integer.parseInt(strangerName)).getName();
            String userAvatar = tree.find(Integer.parseInt(strangerName)).getAvatar();
            if (userAvatar == null || userAvatar == "") {
            } else {
                holder.userImage.setImageBitmap(BitmapUtils.base64ToBitmap(userAvatar));
            }
            holder.userName.setText("@" + realUsername);
        });



        //TODO: use Glide library to import customized image profile.

        holder.denyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DB.getUsers(new Consumer<List<User>>() {
                    @Override
                    public void accept(List<User> users) {
                        allUsers = (ArrayList<User>) users;
                        for(User u : allUsers){
                            if(Integer.valueOf(u.getUid()).toString().equals(strangerName)){
                                // TODO: Send notification to requester.
                                // Remove request from current user;
                                LoginActivity.user.getFriendRequest().remove(strangerName);
                                DB.UpdateUsersData(LoginActivity.user);
                            }
                        }
                    }
                });
            }
        });

        holder.acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DB.getUsers(new Consumer<List<User>>() {
                    @Override
                    public void accept(List<User> users) {
                        allUsers = (ArrayList<User>) users;
                        for(User u : allUsers){
                            if(Integer.valueOf(u.getUid()).toString().equals(strangerName)){
                                // TODO: Send notification to requester.
                                // Remove request from current user;
                                cur_user.getFriendRequest().remove(strangerName);
                                cur_user.getFriends().add(strangerName);
                                u.getFriends().add(Integer.valueOf(cur_user.getUid()).toString());
                                DB.UpdateUsersData(u);
                                DB.UpdateUsersData(cur_user);
                            }
                        }
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return friendsRequests.size();
    }

    class RequestsViewHolder extends RecyclerView.ViewHolder{
        public final ImageView userImage;
        public final TextView userName;
        public final Button denyBtn;
        public final Button   acceptBtn;

        public RequestsViewHolder(@NonNull View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.userimage_in_requestlist);
            userName = itemView.findViewById(R.id.username_in_requestlist);
            denyBtn = itemView.findViewById(R.id.deny_button);
            acceptBtn = itemView.findViewById(R.id.accept_button);

        }
    }
}
