package com.example.mealshared.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Consumer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.example.mealshared.Adapters.RequestAdapter;
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

public class RequestListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RequestAdapter requestAdapter;

    private ArrayList<String> requests;
    private ArrayList<User> allUsers = new ArrayList<>();
    private User cur_user;

    private DatabaseHelper DB = DatabaseHelper.getInstance();
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_list);

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db = FirebaseFirestore.getInstance();
        db.setFirestoreSettings(settings);

        // Instantiate and configure recyclerview.
        recyclerView = findViewById(R.id.recyclerViewinRequestList);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        // Assign value to friends list.
        requests = LoginActivity.user.getFriendRequest();

        // Read all users from database.
        DB.getUsers(new Consumer<List<User>>() {
            @Override
            public void accept(List<User> users) {
                allUsers = (ArrayList<User>) users;

                // Assign adapter to recycler view.
                requestAdapter = new RequestAdapter(RequestListActivity.this,requests, LoginActivity.user,allUsers);
                recyclerView.setAdapter(requestAdapter);
            }
        });

        // Set data listener.
        addListenerOnDoc_requestlist(LoginActivity.user.getEmail());

    }


    /**
     *  This function is to act as a listener on a specific document on firestore, once change occurs
     *  on that document, the callback function will be invoked.
     * */
    public void addListenerOnDoc_requestlist (String email){
        DocumentReference docRef = db.collection(DatabaseHelper.COLLECTION_USERS_PATH).document(email);
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("addListenerOnDoc_req", "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    cur_user= snapshot.toObject(User.class);
                    requests = cur_user.getFriendRequest();

                    DB.getUsers(new Consumer<List<User>>() {
                        @Override
                        public void accept(List<User> users) {
                            allUsers = (ArrayList<User>) users;

                            // Assign adapter to recycler view.
                            requestAdapter = new RequestAdapter(RequestListActivity.this,requests,cur_user,allUsers);
                            recyclerView.setAdapter(requestAdapter);
                        }
                    });

                    Log.d("addListenerOnDoc_req", "Current data: " + snapshot.getData());
                } else {
                    Log.d("addListenerOnDoc_req", "Current data: null");
                }
            }
        });
    }
}