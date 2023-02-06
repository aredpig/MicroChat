package com.example.mealshared.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.mealshared.Activities.FollowerListActivity;
import com.example.mealshared.Activities.FollowingListActivity;
import com.example.mealshared.Activities.FriendsListActivity;
import com.example.mealshared.Activities.RequestListActivity;
import com.example.mealshared.R;
import com.example.mealshared.databinding.FragmentFriendsBinding;

public class FriendsFragment extends Fragment {

    private FragmentFriendsBinding binding;
    private Button friendBtn;
    private Button requestBtn;
    private Button followers;
    private Button followings;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentFriendsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        friendBtn = root.findViewById(R.id.button_friends);
        friendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent friendsList = new Intent(getContext(), FriendsListActivity.class);
                startActivity(friendsList);
            }
        });
        requestBtn = root.findViewById(R.id.button_requests);
        requestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent requestList = new Intent(getContext(), RequestListActivity.class);
                startActivity(requestList);
            }
        });
        followers = root.findViewById(R.id.button_follower);
        followers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent followerList = new Intent(getContext(), FollowerListActivity.class);
                startActivity(followerList);
            }
        });
        followings = root.findViewById(R.id.button_following);
        followings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent followingList = new Intent(getContext(), FollowingListActivity.class);
                startActivity(followingList);
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}