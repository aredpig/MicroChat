package com.example.mealshared.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.os.Bundle;

import com.example.mealshared.AVLTree;
import com.example.mealshared.Adapters.FollowsAdapter;
import com.example.mealshared.Utils.BitmapUtils;
import com.example.mealshared.DatabaseHelper;
import com.example.mealshared.R;

import java.util.ArrayList;

public class FollowerListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private int curUid;

    private ArrayList<Integer> uids  = new ArrayList<>();
    private DatabaseHelper DB = DatabaseHelper.getInstance();
    private AVLTree tree;
    private ArrayList<String> username = new ArrayList<>();
    private ArrayList<Bitmap> bitmap = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follower_list);

        recyclerView = findViewById(R.id.follower_recycle);
        // get current user uid
        curUid = LoginActivity.user.getUid();
        //get the follower user information
        DB.getTree(tempTree ->{
            tree = tempTree;
            for (int follows: tree.find(curUid).getFollower()) {
                uids.add(follows);
            }
            for (Integer uid: uids) {
                String usernameTemp = tree.find(uid).getName();
                username.add("@" + usernameTemp);
                bitmap.add(BitmapUtils.base64ToBitmap(tree.find(uid).getAvatar()));
                FollowsAdapter followsAdapter = new FollowsAdapter(this, username, bitmap);
                recyclerView.setAdapter(followsAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
            }
        });
    }
}