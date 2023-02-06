package com.example.mealshared;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.mealshared.Activities.NavigatePageActivity;
import com.example.mealshared.Activities.PostTweetActivity;
import com.example.mealshared.Activities.SingleTweetActivity;

public class UITester extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uitester);
    }


    public void goMainPage(View view) {
//        Intent intent = new Intent(this, MainPageActivity.class);
//        startActivity(intent);
    }

    public void goNotafyPage(View view) {
//        Intent intent = new Intent(this, NotificationPageActivity.class);
//        startActivity(intent);
    }

    public void goSearchPage(View view) {
//        Intent intent = new Intent(this, SearchPageActivity.class);
//        startActivity(intent);
    }

    public void goPost(View view) {
        Intent intent = new Intent(this, PostTweetActivity.class);
        startActivity(intent);
    }

    public void goTweet(View view) {
        Intent intent = new Intent(this, SingleTweetActivity.class);
        startActivity(intent);
    }

    public void goNavi(View view) {
        Intent intent = new Intent(this, NavigatePageActivity.class);
        startActivity(intent);
    }
}