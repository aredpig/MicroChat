package com.example.mealshared.Fragments;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.mealshared.AVLTree;
import com.example.mealshared.Activities.NavigatePageActivity;
import com.example.mealshared.Activities.PostTweetActivity;
import com.example.mealshared.Adapters.TweetsAdapter;
import com.example.mealshared.Utils.BitmapUtils;
import com.example.mealshared.Utils.CameraUtils;
import com.example.mealshared.DatabaseHelper;
import com.example.mealshared.Models.Post;
import com.example.mealshared.Models.User;
import com.example.mealshared.R;
import com.example.mealshared.databinding.FragmentHomeBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.makeramen.roundedimageview.RoundedImageView;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;


public class HomeFragment extends Fragment {
    final DatabaseHelper DB = DatabaseHelper.getInstance();
    private FragmentHomeBinding binding;
    private Button postButton;
    private RxPermissions rxPermissions;
    private boolean hasPermissions;
    //Bottom pop
    private BottomSheetDialog bottomSheetDialog;
    //pop view
    private View bottomView;
    public static final int SELECT_PHOTO = 2;
    private AVLTree tree;
    private TextView post;
    private TextView follower;
    private TextView following;
    private TextView usernameMain;
    private RoundedImageView roundedImageView;
    private ImageView imageView;
    private User user;
    private RecyclerView recyclerView;

    private ArrayList<String> mainContent = new ArrayList<>();
    private ArrayList<String> addresses = new ArrayList<>();
    private ArrayList<String> timelines = new ArrayList<>();
    private ArrayList<String> mainTag = new ArrayList<>();
    private ArrayList<String> mainUsername = new ArrayList<>();
    private ArrayList<Bitmap> mainBitmap = new ArrayList<>();
    private ArrayList<Integer> uids = new ArrayList<>();

    private String base64Pic;
    private Bitmap orc_bitmap;
    private RequestOptions requestOptions = RequestOptions.centerCropTransform()
            .diskCacheStrategy(DiskCacheStrategy.NONE)//No diskCache
            .skipMemoryCache(true);//No memoryCache
//    private Bundle bundle;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        NavigatePageActivity Tempactivity = (NavigatePageActivity) getActivity();

        post = root.findViewById(R.id.num_post_main);
        follower = root.findViewById(R.id.num_follower_main);
        following = root.findViewById(R.id.num_following_main);
        usernameMain = root.findViewById(R.id.userName_main);
        hasPermissions = false;
        //clickable image

        checkVersion();
//        imageView = view.findViewById(R.id.test_avatar);
        roundedImageView = root.findViewById(R.id.single_tweet_user_pic);
        /*
        This part is for the recyclerview
        Start
         */
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
                    recyclerView = root.findViewById(R.id.main_page_tweets);
                    TweetsAdapter tweetsAdapter = new TweetsAdapter(getActivity(), mainContent, mainTag, mainUsername, timelines, addresses, uids, mainBitmap);
                    recyclerView.setAdapter(tweetsAdapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                }
            });
        });





        /*
        This part is for the recyclerview
        End
         */

        user = Tempactivity.DataBuffer();

        DB.getUser(user.getEmail(), TempUser -> {
            if (TempUser.getAvatar()!="") roundedImageView.setImageBitmap(BitmapUtils.base64ToBitmap(TempUser.getAvatar()));
            if (TempUser.getPosts() == null || TempUser.getPosts().isEmpty()) {
                post.setText("0");
            } else {
                post.setText(String.valueOf(TempUser.getPosts().size()));
            }

            if (TempUser.getFollower() == null || TempUser.getFollower().isEmpty()) {
                follower.setText("0");
            } else {
                follower.setText(String.valueOf(TempUser.getFollower().size()));
            }

            if (TempUser.getFollowing() == null || TempUser.getFollowing().isEmpty()) {
                following.setText("0");
            } else {
                following.setText(String.valueOf(TempUser.getFollowing().size()));
            }
        });

        roundedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeAvatar(view);
            }
        });

        usernameMain.setText(user.getName());

        postButton = (Button)root.findViewById(R.id.main_page_post);
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent postTweet = new Intent(getActivity(), PostTweetActivity.class);
                postTweet.putExtra("Email", user.getEmail());
                startActivity(postTweet);
            }
        });
        return root;
    }

    private void showMsg(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    private void checkVersion() {
        //Android6.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //
            rxPermissions = new RxPermissions(getActivity());
            //Permation
            rxPermissions.request(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .subscribe(granted -> {
                        if (granted) {
//                            showMsg("Permissions enabled");
                            hasPermissions = true;
                        } else {
                            showMsg("Permissions not enabled");
                            hasPermissions = false;
                        }
                    });
        } else {
            showMsg("No need to request dynamic permissions");
        }
    }


    public void changeAvatar(View view) {
        bottomSheetDialog = new BottomSheetDialog(getActivity());
        bottomView = getLayoutInflater().inflate(R.layout.dialog_bottom, null);
        bottomSheetDialog.setContentView(bottomView);
        bottomSheetDialog.getWindow().findViewById(R.id.design_bottom_sheet).setBackgroundColor(Color.TRANSPARENT);
        TextView tvOpenAlbum = bottomView.findViewById(R.id.tv_open_album);
        TextView tvCancel = bottomView.findViewById(R.id.tv_cancel);

        //Open Album
        tvOpenAlbum.setOnClickListener(v -> {
            openAlbum();
            showMsg("Open Album");
            bottomSheetDialog.cancel();

        });
        //Cancel
        tvCancel.setOnClickListener(v -> {
            bottomSheetDialog.cancel();
        });
        bottomSheetDialog.show();
    }
    private void displayImage(String imagePath) {
        if (!TextUtils.isEmpty(imagePath)) {
            //show image
            Glide.with(this).load(imagePath).apply(requestOptions).into(roundedImageView);

            //comprasing
            orc_bitmap = CameraUtils.compression(BitmapFactory.decodeFile(imagePath));
//            //Base64
            base64Pic = BitmapUtils.bitmapToBase64(orc_bitmap);
            user.setAvatar(base64Pic);
            DB.UpdateUsersData(user);
            DB.UpdateTreeNode(user);
//            write("./", "");

        } else {
            showMsg("Fail to load image");
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            //拍照后返回

            case SELECT_PHOTO:
                if (resultCode == getActivity().RESULT_OK) {
                    String imagePath = null;
                    //
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                        //4.4
                        imagePath = CameraUtils.getImageOnKitKatPath(data, getActivity());
                    } else {
                        imagePath = CameraUtils.getImageBeforeKitKatPath(data, getActivity());
                    }
                    //show image
                    displayImage(imagePath);
                }
                break;
            default:
                break;
        }
    }


    private void openAlbum() {
        if (!hasPermissions) {
            showMsg("No permission");
            checkVersion();
            return;
        }
        startActivityForResult(CameraUtils.getSelectPhotoIntent(), SELECT_PHOTO);
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}