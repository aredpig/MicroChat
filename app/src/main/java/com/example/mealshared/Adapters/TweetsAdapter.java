package com.example.mealshared.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mealshared.R;
import com.example.mealshared.Activities.SingleTweetActivity;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.MyviewHolder> {
    private ArrayList<String> content, tag, username, timeline, address;
    private ArrayList<Integer> uid;
    private ArrayList<Bitmap> bitmap;
    Context context;

    public TweetsAdapter(Context context, ArrayList<String> content, ArrayList<String> tag, ArrayList<String> username, ArrayList<Bitmap> bitmap) {

    }

    public TweetsAdapter( Context context, ArrayList<String> content, ArrayList<String> tag, ArrayList<String> username, ArrayList<String> timeline, ArrayList<String> address, ArrayList<Integer> uid, ArrayList<Bitmap> bitmap) {
        this.content = content;
        this.tag = tag;
        this.username = username;
        this.timeline = timeline;
        this.address = address;
        this.uid = uid;
        this.bitmap = bitmap;
        this.context = context;
    }

    @NonNull
    @Override
    public MyviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.tweet_item, parent, false);

        return new MyviewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyviewHolder holder, int position) {
        holder.myContent.setText(content.get(position));
        holder.myTag.setText(tag.get(position));
        holder.myUsername.setText(username.get(position));

        if (bitmap.get(position) == null) {
            holder.roundedImageView.setImageResource(R.drawable.cat);
        } else {
            holder.roundedImageView.setImageBitmap(bitmap.get(position));
        }
        holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, SingleTweetActivity.class);
                intent.putExtra("tag", tag.get(position));
                intent.putExtra("name", username.get(position));
                intent.putExtra("address", address.get(position));
                intent.putExtra("uid", uid.get(position));
                intent.putExtra("time", timeline.get(position));
//                intent.putExtra("bitmap", bitmap.get(position));
                intent.putExtra("content", content.get(position));
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return username.size();
    }

    public class MyviewHolder extends RecyclerView.ViewHolder {
        TextView myContent, myTag, myUsername;
        RoundedImageView roundedImageView;
        ConstraintLayout constraintLayout;
        public MyviewHolder(@NonNull View itemView) {
            super(itemView);
            myContent = itemView.findViewById(R.id.every_content);
            myTag = itemView.findViewById(R.id.every_tag);
            myUsername = itemView.findViewById(R.id.every_name);
            roundedImageView = itemView.findViewById(R.id.every_pic);
            constraintLayout = itemView.findViewById(R.id.every_tweet);


        }
    }
}
