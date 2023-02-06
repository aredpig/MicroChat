package com.example.mealshared.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.mealshared.AVLTree;
import com.example.mealshared.Utils.BitmapUtils;
import com.example.mealshared.DatabaseHelper;
import com.example.mealshared.Models.Post;
import com.example.mealshared.R;
import com.example.mealshared.Models.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.mealshared.databinding.ActivityNavigatePageBinding;

import java.util.ArrayList;

public class NavigatePageActivity extends AppCompatActivity {
    final DatabaseHelper DB = DatabaseHelper.getInstance();
    private ActivityNavigatePageBinding binding;
    User user;
    AVLTree tree;
    private ArrayList<String> mainContent = new ArrayList<>();
    private ArrayList<String> addresses = new ArrayList<>();
    private ArrayList<String> timelines = new ArrayList<>();
    private ArrayList<String> mainTag = new ArrayList<>();
    private ArrayList<String> mainUsername = new ArrayList<>();
    private ArrayList<Bitmap> mainBitmap = new ArrayList<>();
    private ArrayList<Integer> uids = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String email = getIntent().getStringExtra("Email");
        email = getIntent().getStringExtra("Email");
        DB.getUser(email, TempUser -> {
            user = TempUser;

            binding = ActivityNavigatePageBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());

            BottomNavigationView navView = findViewById(R.id.nav_view);
            // Passing each menu ID as a set of Ids because each
            // menu should be considered as top level destinations.
            AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                    .build();
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_navigate_page);
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
            NavigationUI.setupWithNavController(binding.navView, navController);
        });

        DB.getTree(TempTree ->{
            tree = TempTree;
            DB.getPosts(TempList ->{
                for (Post post:TempList){
                    mainContent.add(post.getContext());
                    mainTag.add("#" + post.getTag());
                    timelines.add(post.getTimeline());
                    addresses.add(post.getAddress());
                    int Uid = post.getOwner();
                    uids.add(Uid);
                    User target = tree.find(Uid);
                    mainUsername.add("@" + target.getName());
                    if (target.getAvatar() == null || target.getAvatar().equals("")) {
                        mainBitmap.add(null);
                    } else {
                        mainBitmap.add(BitmapUtils.base64ToBitmap(target.getAvatar()));
                    }
                }
            });
        });

    }

    public ArrayList<String> getAddresses() {
        return addresses;
    }

    public ArrayList<String> getTimelines() {
        return timelines;
    }

    public User DataBuffer(){
        return user;
    }
    public ArrayList<Integer> getUids(){
        return uids;
    }
    public ArrayList<String> getMainContent(){
        return mainContent;
    }
    public ArrayList<String> getMainTag(){
        return mainTag;
    }

    public ArrayList<String> getMainUsername(){
        return mainUsername;
    }

    public ArrayList<Bitmap> getMainBitmap(){
        return mainBitmap;
    }

    public View.OnClickListener ToPostListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent PostPage = new Intent(NavigatePageActivity.this, PostTweetActivity.class);
            PostPage.putExtra("UID",String.valueOf(user.getUid()));
            startActivity(PostPage);
        }
    };
}