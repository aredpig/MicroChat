package com.example.mealshared.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.util.Consumer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mealshared.Adapters.MessageAdapter;
import com.example.mealshared.Models.ChatItem;
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

public class MessageActivity extends AppCompatActivity {

    private ArrayList<User> users_in_message = new ArrayList<>();
    private ImageView imageview;
    private TextView username;

    private EditText msg_text;
    private Button send_button;

    User sender;
    User receiver;

    MessageAdapter messageAdapter;
    private ArrayList<ChatItem> chatItems;

    private RecyclerView recyclerView;
    Intent intent;

    private FirebaseFirestore db;
    private DatabaseHelper DB = DatabaseHelper.getInstance();
    
    // Constant value
    private final int SENDER_TYPE = 0;
    private final int RECEIVER_TYPE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        // Initialize firestore instance. (Only for real-time message update)
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db = FirebaseFirestore.getInstance();
        db.setFirestoreSettings(settings);

        // Widgets
        username = findViewById(R.id.textView);

        send_button = findViewById(R.id.send_button);
        msg_text = findViewById(R.id.text_send);

        // RecyclerView.
        recyclerView = findViewById(R.id.messageRecycleView);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());

        recyclerView.setLayoutManager(linearLayoutManager);


        DB.getUsers(new Consumer<List<User>>() {
            @Override
            public void accept(List<User> users) {
                users_in_message = (ArrayList<User>) users;

                // Assign value to sender and reader
                for(User u : users_in_message){
                    if(Integer.valueOf(u.getUid()).toString().equals(getIntent().getStringExtra("sender_uid"))){
                        sender = u;
                    }else if(Integer.valueOf(u.getUid()).toString().equals(getIntent().getStringExtra("receiver_uid"))){
                        receiver = u;
                    }
                }
                // Add listener to sender and receiver doc on firestore.
                addListenerOnDoc(sender.getEmail(), SENDER_TYPE);
                addListenerOnDoc(receiver.getEmail(), RECEIVER_TYPE);

                username.setText("@" + receiver.getName());
                readMessages();
            }
        });

        send_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = msg_text.getText().toString();
                // If the message isn't empty, send it.
                if (!msg.equals("")) {
                    sendMessage(msg);
                } else {
                    Toast.makeText(MessageActivity.this, "You cannot send an empty message!", Toast.LENGTH_SHORT).show();
                }

                readMessages();

                msg_text.setText("");
            }
        });




    }

    private void sendMessage(String message) {
        // First, add this message into sender's chat log.
        ChatItem newMessageFromSender = new ChatItem(message, "2021.08.08", Integer.valueOf(sender.getUid()).toString(), Integer.valueOf(receiver.getUid()).toString());
        if(sender.getMessageList() == null ||sender.getMessageList().get(Integer.valueOf(receiver.getUid()).toString()) == null){
            ArrayList<ChatItem> chats = new ArrayList<>();
            chats.add(newMessageFromSender);
            sender.getMessageList().put(Integer.valueOf(receiver.getUid()).toString(),chats);
        }else{
            sender.getMessageList().get(Integer.valueOf(receiver.getUid()).toString()).add(newMessageFromSender);
        }


        // Then, add this message into receiver's chat log.
        if(receiver.getMessageList() == null||receiver.getMessageList().get(Integer.valueOf(sender.getUid()).toString()) == null){
            ArrayList<ChatItem> chats = new ArrayList<>();
            chats.add(newMessageFromSender);
            receiver.getMessageList().put(Integer.valueOf(sender.getUid()).toString(),chats);
        }else{
            receiver.getMessageList().get(Integer.valueOf(sender.getUid()).toString()).add(newMessageFromSender);
        }

        // Update data on firebase.
        DB.UpdateUsersData(sender);
        DB.UpdateUsersData(receiver);
    }

    private void readMessages(){

        chatItems = new ArrayList<>();

        DB.getUsers(new Consumer<List<User>>() {
            @Override
            public void accept(List<User> users) {
                users_in_message = (ArrayList<User>) users;
                for(User u: users_in_message){
                    if(u.getUid() == sender.getUid()){
                        System.out.println("Entered this if branch");
                        //Potential bugs: If there is no messages, then the return value will be null.
                        chatItems = sender.getMessageList().get(Integer.valueOf(receiver.getUid()).toString());
                        if(chatItems == null){
                            chatItems = new ArrayList<>();
                        }

                        messageAdapter = new MessageAdapter(sender,chatItems,MessageActivity.this);
                        recyclerView.setAdapter(messageAdapter);
                    }
                }

                // Set textView and imageView.
                username.setText("@" + receiver.getName());
            }
        });

    }

    /**
     *  This function is to act as a listener on a specific document on firestore, once change occurs
     *  on that document, the callback function will be invoked.
     * */
    public void addListenerOnDoc (String email, int type){
        DocumentReference docRef = db.collection(DatabaseHelper.COLLECTION_USERS_PATH).document(email);
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("addListenerOnDoc", "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    if(type == SENDER_TYPE){
                        sender = snapshot.toObject(User.class);
                    }else if(type == RECEIVER_TYPE){
                        receiver = snapshot.toObject(User.class);
                    }
                    readMessages();
                    Log.d("addListenerOnDoc", "Current data: " + snapshot.getData());
                } else {
                    Log.d("addListenerOnDoc", "Current data: null");
                }
            }
        });
    }

}