package com.example.mealshared.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mealshared.R;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

public class FollowsAdapter extends RecyclerView.Adapter<FollowsAdapter.MyviewHolder>{
//    User user;
    ArrayList<String> username = new ArrayList<>();
    private ArrayList<Bitmap> bitmap = new ArrayList<>();
    private Context context;

    public FollowsAdapter(Context context, ArrayList<String> username, ArrayList<Bitmap> bitmap) {

        this.username = username;
        this.bitmap = bitmap;
        this.context = context;
    }

    @NonNull
    @Override
    public FollowsAdapter.MyviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.follow_item, parent, false);
        return new FollowsAdapter.MyviewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FollowsAdapter.MyviewHolder holder, int position) {
        holder.followUsername.setText(username.get(position));
        if (bitmap.get(position) == null) {
            holder.roundedImageView.setImageResource(R.drawable.cat);
        } else {
            holder.roundedImageView.setImageBitmap(bitmap.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return username.size();
    }

    public class MyviewHolder extends RecyclerView.ViewHolder {
        TextView followUsername;
        RoundedImageView roundedImageView;
        ConstraintLayout constraintLayout;
        public MyviewHolder(@NonNull View itemView) {
            super(itemView);
            followUsername = itemView.findViewById(R.id.follow_text);
            roundedImageView = itemView.findViewById(R.id.follower_pic);
            constraintLayout = itemView.findViewById(R.id.every_follow_item);
        }
    }
}
