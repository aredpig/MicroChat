package com.example.mealshared.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Consumer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mealshared.Adapters.FriendAdapter;
import com.example.mealshared.DatabaseHelper;
import com.example.mealshared.R;
import com.example.mealshared.Models.User;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.util.ArrayList;
import java.util.List;

public class FriendsListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FriendAdapter friendAdapter;
    private ArrayList<String> friends;
    private ArrayList<User> allUsers = new ArrayList<>();
    private User cur_user;
    private Button searchBtn;
    private EditText searchFriendText;

    private DatabaseHelper DB = DatabaseHelper.getInstance();
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db = FirebaseFirestore.getInstance();
        db.setFirestoreSettings(settings);

        // Instantiate other views.
        searchBtn = findViewById(R.id.searchFriend_button);
        searchFriendText = findViewById(R.id.Search_friend);


        // Instantiate and configure recyclerview.
        recyclerView = findViewById(R.id.friends_list_recyclerv);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        // Assign value to friends list.
        friends = LoginActivity.user.getFriends();

        // Read all users from database.
        DB.getUsers(new Consumer<List<User>>() {
                @Override
                public void accept(List<User> users) {
                    allUsers = (ArrayList<User>) users;

                    // Assign adapter to recycler view.
                    friendAdapter = new FriendAdapter(FriendsListActivity.this,friends, LoginActivity.user,allUsers);
                    recyclerView.setAdapter(friendAdapter);
                }
        });



        // Set search button onClick listener.
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(searchFriendText.getText().equals("")){
                    Toast.makeText(FriendsListActivity.this,"Please enter correct UID!",Toast.LENGTH_SHORT).show();
                }else{
                    for(User u: allUsers){
                        if(Integer.valueOf(u.getUid()).toString().equals(searchFriendText.getText().toString())){
                            // Add friend.(First send request)
                            if(!u.getFriendRequest().contains(Integer.valueOf(LoginActivity.user.getUid()).toString()) && !(LoginActivity.user.getFriends().contains(searchFriendText.getText().toString()))){
                                u.getFriendRequest().add(Integer.valueOf(LoginActivity.user.getUid()).toString());
                                Toast.makeText(FriendsListActivity.this,"Successfully sent request!",Toast.LENGTH_SHORT).show();
                                DB.UpdateUsersData(u);
                                return;
                            }
                            Toast.makeText(FriendsListActivity.this,"You've sent the request! Please wait!",Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    Toast.makeText(FriendsListActivity.this,"No user matches with this UID! Please check!",Toast.LENGTH_SHORT).show();
                }
            }
        });

        addListenerOnDoc_friendlist(LoginActivity.user.getEmail());

    }

    /**
     *  This function is to act as a listener on a specific document on firestore, once change occurs
     *  on that document, the callback function will be invoked.
     * */
    public void addListenerOnDoc_friendlist (String email){
        DocumentReference docRef = db.collection(DatabaseHelper.COLLECTION_USERS_PATH).document(email);
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("addListenerOnDoc_friend", "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    cur_user= snapshot.toObject(User.class);
                    friends = cur_user.getFriends();

                    DB.getUsers(new Consumer<List<User>>() {
                        @Override
                        public void accept(List<User> users) {
                            allUsers = (ArrayList<User>) users;

                            // Assign adapter to recycler view.
                            friendAdapter = new FriendAdapter(FriendsListActivity.this,friends,cur_user,allUsers);
                            recyclerView.setAdapter(friendAdapter);
                        }
                    });
                    Log.d("addListenerOnDoc_friend", "Current data: " + snapshot.getData());
                } else {
                    Log.d("addListenerOnDoc_friend", "Current data: null");
                }
            }
        });
    }
}