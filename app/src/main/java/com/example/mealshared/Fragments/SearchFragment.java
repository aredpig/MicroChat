package com.example.mealshared.Fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mealshared.AVLTree;
import com.example.mealshared.Adapters.TweetsAdapter;
import com.example.mealshared.Utils.BitmapUtils;
import com.example.mealshared.DatabaseHelper;
import com.example.mealshared.Models.Post;
import com.example.mealshared.Models.User;
import com.example.mealshared.R;
import com.example.mealshared.databinding.FragmentSearchBinding;

import java.util.ArrayList;
import java.util.Locale;

public class SearchFragment extends Fragment {

    private FragmentSearchBinding binding;
    private DatabaseHelper DB = DatabaseHelper.getInstance();
    private EditText searchContent;
    private Button searchButton;
    private RecyclerView recyclerView;
    private AVLTree tree;
    private ArrayList<String> mainContent = new ArrayList<>();
    private ArrayList<String> addresses = new ArrayList<>();
    private ArrayList<String> timelines = new ArrayList<>();
    private ArrayList<String> mainTag = new ArrayList<>();
    private ArrayList<String> mainUsername = new ArrayList<>();
    private ArrayList<Bitmap> mainBitmap = new ArrayList<>();
    private ArrayList<Integer> uids = new ArrayList<>();


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentSearchBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        searchContent = root.findViewById(R.id.search_content);
        searchButton = root.findViewById(R.id.button_search);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String information = searchContent.getText().toString();

                information = information.replace(" ", "");
                information = information.toUpperCase(Locale.ROOT);
                if (information.contains("@") && information.contains("#")) {
                    Toast.makeText(getActivity(),"Please only use '@' to search user, and '#' to search tag! ",Toast.LENGTH_SHORT).show();
                } else if (information.contains("#")) {
                    String tag = information.replace("#", "").toUpperCase(Locale.ROOT);
                    DB.getTree(tempTree ->{
                        tree = tempTree;
                        DB.getPosts(postList ->{
                            boolean found = false;
                            for (Post post:postList){
                                if (post.getTag().toUpperCase(Locale.ROOT).contains(tag)) {
                                    found = true;
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
                                    recyclerView = root.findViewById(R.id.search_results);
                                    TweetsAdapter tweetsAdapter = new TweetsAdapter(getActivity(), mainContent, mainTag, mainUsername, timelines, addresses, uids, mainBitmap);
                                    recyclerView.setAdapter(tweetsAdapter);
                                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                                }
                            }
                            if (!found) {
                                Toast.makeText(getActivity(),"No related tags",Toast.LENGTH_SHORT).show();
                            }
                        });
                    });
                } else if (information.contains("@")){
                    String aUser = information.replace("@", "").toUpperCase(Locale.ROOT);
                    DB.getTree(TempTree ->{
                        tree = TempTree;
                        DB.getPosts(TempList ->{
                            boolean found = false;
                            for (Post post:TempList){
                                //check every UID, if contains same user name
                                int Uid = post.getOwner();
                                User target = tree.find(Uid);

                                if (target.getName().toUpperCase(Locale.ROOT).equals(aUser)) {
                                    found = true;
                                    uids.add(Uid);
                                    mainContent.add(post.getContext());
                                    mainTag.add("#" + post.getTag());
                                    timelines.add(post.getTimeline());
                                    addresses.add(post.getAddress());
                                    mainUsername.add("@" + target.getName());
                                    if (target.getAvatar() == null || target.getAvatar().equals("")) {
                                        mainBitmap.add(null);
                                    } else {
                                        mainBitmap.add(BitmapUtils.base64ToBitmap(target.getAvatar()));
                                    }
                                    recyclerView = root.findViewById(R.id.search_results);
                                    TweetsAdapter tweetsAdapter = new TweetsAdapter(getActivity(), mainContent, mainTag, mainUsername, timelines, addresses, uids, mainBitmap);
                                    recyclerView.setAdapter(tweetsAdapter);
                                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                                }
                            }
                            if (!found) {
                                Toast.makeText(getActivity(),"No related users",Toast.LENGTH_SHORT).show();
                            }
                        });
                    });
                } else {
                    Toast.makeText(getActivity(),"Please only use '@' to search user, and '#' to search tag! ",Toast.LENGTH_SHORT).show();
                }
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