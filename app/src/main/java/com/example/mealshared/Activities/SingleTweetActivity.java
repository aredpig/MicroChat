package com.example.mealshared.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.mealshared.AVLTree;
import com.example.mealshared.Utils.BitmapUtils;
import com.example.mealshared.DatabaseHelper;
import com.example.mealshared.Models.Post;
import com.example.mealshared.R;
import com.example.mealshared.Models.User;
import com.makeramen.roundedimageview.RoundedImageView;
import com.sackcentury.shinebuttonlib.ShineButton;

public class SingleTweetActivity extends AppCompatActivity {
    private RoundedImageView roundedImageView;
    private TextView singleTweetLocation, singleTweetTime, singleTweetUsername, SingleTweetTag, contentTweet, likeCount;
    AVLTree tree;
    private int curUid = LoginActivity.user.getUid();
    private ToggleButton toggleButton;
    private DatabaseHelper DB = DatabaseHelper.getInstance();
    private String content, tag, username, timeline, address;
    private Integer uid;
    private Bitmap bitmap;
    private Post curPost;
    private ShineButton shineButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_tweet);
        roundedImageView = findViewById(R.id.single_tweet_user_pic);
        singleTweetLocation = findViewById(R.id.single_tweet_location);
        singleTweetTime = findViewById(R.id.single_tweet_time);
        singleTweetUsername = findViewById(R.id.single_tweet_user_name);
        SingleTweetTag = findViewById(R.id.single_tweet_tag);
        contentTweet = findViewById(R.id.tweet_content);
        toggleButton = findViewById(R.id.toggleButton2);
        shineButton = findViewById(R.id.tweet_heart_button);
        likeCount = findViewById(R.id.likes_count);
        // get data from intent
        getData();
        // set data on view
        setData();


    }


    private void getData() {
        if(getIntent().hasExtra("tag")
                && getIntent().hasExtra("name")
                && getIntent().hasExtra("address")
                && getIntent().hasExtra("uid")
                && getIntent().hasExtra("time")
                && getIntent().hasExtra("content")) {

            tag = getIntent().getStringExtra("tag");
            username = getIntent().getStringExtra("name");
            address = getIntent().getStringExtra("address");
            uid = getIntent().getIntExtra("uid", 1);
            timeline = getIntent().getStringExtra("time");
            content = getIntent().getStringExtra("content");


        } else {
            Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
        }


    }

    private void setData() {
        if (curUid == uid) {
            toggleButton.setVisibility(View.GONE);
        }

        DB.getTree(tempTree ->{
            tree = tempTree;
            // Update avatar
            String userAvatar = tree.find(uid).getAvatar();
            bitmap = BitmapUtils.base64ToBitmap(userAvatar);
            if (userAvatar == null || userAvatar == "") {
                roundedImageView.setImageResource(R.drawable.cat);
            } else {
                roundedImageView.setImageBitmap(bitmap);
            }

            String messages = username + "'s UID is " + uid;
            roundedImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(SingleTweetActivity.this, messages, Toast.LENGTH_SHORT).show();
                }
            });

            // check if current user's following contains twitter host
            if (tree.find(curUid).getFollowing().contains(uid)) {
                toggleButton.setChecked(true);
            } else {
                toggleButton.setChecked(false);
            }
            //add or delete following
            toggleButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (toggleButton.isChecked()) {
                        User hostUser = tree.find(uid);
                        hostUser.addFollower(curUid);
                        User curUser = tree.find(curUid);
                        curUser.addFollowing(uid);
                        DB.UpdateUsersData(hostUser);
                        DB.UpdateTreeNode(hostUser);
                        DB.UpdateUsersData(curUser);
                        DB.UpdateTreeNode(curUser);
                    } else {
                        User hostUser = tree.find(uid);
                        hostUser.delFollower(curUid);
                        User curUser = tree.find(curUid);
                        curUser.delFollowing(uid);
                        DB.UpdateUsersData(hostUser);
                        DB.UpdateTreeNode(hostUser);
                        DB.UpdateUsersData(curUser);
                        DB.UpdateTreeNode(curUser);
                    }
                }
            });
            // check user like or not
            DB.getPosts(postList ->{
                for (Post post:postList) {
                    // find current post
                    if (post.getTimeline().equals(timeline)) {
                        curPost = post;

                        break;
                    }
                }
                if (curPost.getLikers() == null || curPost.getLikers().size() == 0) {
                    likeCount.setText("0");
                } else {
                    likeCount.setText(String.valueOf(curPost.getLikers().size()));
                }

                if (curPost.getLikers() == null) {
                    shineButton.setChecked(false);
                }
                else if (curPost.getLikers().contains(curUid)) {
                    shineButton.setChecked(true);
                } else {
                    shineButton.setChecked(false);
                }
                shineButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (shineButton.isChecked()) {
                            int curCount = Integer.parseInt(likeCount.getText().toString()) + 1;
                            likeCount.setText(String.valueOf(curCount));
                            curPost.addLikes(curUid);
                            DB.UpdatePostData(curPost);
                        } else {
                            int curCount = Integer.parseInt(likeCount.getText().toString()) - 1;
                            likeCount.setText(String.valueOf(curCount));
                            curPost.delLikes(curUid);
                            DB.UpdatePostData(curPost);
                        }
                    }
                });

            });

        });

        if (address == null) {
            singleTweetLocation.setText("Unknown Location");
        } else {
            singleTweetLocation.setText(address);
        }

        singleTweetTime.setText(timeline);
        singleTweetUsername.setText(username);
        SingleTweetTag.setText(tag);
        contentTweet.setText(content);

    }
}