
package com.example.mealshared.Datastream;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Consumer;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.mealshared.AVLTree;
import com.example.mealshared.Activities.LoginActivity;
import com.example.mealshared.Activities.RegisterActivity;
import com.example.mealshared.DatabaseHelper;
import com.example.mealshared.Models.ChatItem;
import com.example.mealshared.Models.Post;
import com.example.mealshared.Models.User;
import com.example.mealshared.Parser;
import com.example.mealshared.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DataStreamActivity extends AppCompatActivity {
    private DatabaseHelper DB = DatabaseHelper.getInstance();
    private AVLTree tree;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_stream);
        loadDataAndExe();
    }

    private void loadDataAndExe(){
        BufferedReader bufferedReader;
        String place=null;
        try {

            bufferedReader = new BufferedReader(new InputStreamReader(getResources().openRawResource(R.raw.datastream), StandardCharsets.UTF_8));
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                String[] tokens = line.split(",");
                if(tokens[0].equals("Post") ){
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
                    Date date = new Date(System.currentTimeMillis());
                    String PostedTime =  formatter.format(date);
                    String Email = tokens[1];
                    String PureString = tokens[2];
                    Parser parser = new Parser(PureString);
                    String tags = parser.extractTag();
                    String content = parser.extractContent();
                    DB.getUser(Email, new Consumer<User>() {
                        @Override
                        public void accept(User user) {
                            int UID = user.getUid();
                            Post post = new Post(UID,PostedTime,content,tags,place);
                            DB.AddPost(post);
                            user.AddPost(post);
                            DB.UpdateUsersData(user);
                            DB.UpdateTreeNode(user);
                        }
                    });
                }else if(tokens[0].equals("Message")){
                    //First sender
                    String message = tokens[3];
                    String sender_email = tokens[1];
                    String reciever_email = tokens[2];
                    DB.getUser(sender_email, TempUser -> {
                        User sender = TempUser;
                        DB.getUser(reciever_email, new Consumer<User>() {
                            @Override
                            public void accept(User user) {
                                User receiver = user;
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
                        });


                    });

                }else if(tokens[0].equals("Like")){
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
                    Date date = new Date(System.currentTimeMillis());
                    String PostedTime =  formatter.format(date);
                    String Poster_Email = tokens[1];
                    String Subscriber_Email = tokens[2];
                    String PureString = tokens[3];
                    Parser parser = new Parser(PureString);
                    String tags = parser.extractTag();
                    String content = parser.extractContent();
                    DB.getUser(Poster_Email, new Consumer<User>() {
                        @Override
                        public void accept(User user) {
                            User sender = user;
                            int UID = sender.getUid();
                            Post post = new Post(UID,PostedTime,content,tags,place);
                            DB.getUser(Subscriber_Email, new Consumer<User>() {
                                @Override
                                public void accept(User user) {
                                    post.addLikes(user.getUid());
                                    sender.AddPost(post);
                                    DB.AddPost(post);
                                    DB.UpdateUsersData(sender);
                                    DB.UpdateTreeNode(sender);
                                }
                            });

                        }
                    });
                }
                delay_data_stream();
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            return ;
        }
    }

    private void delay_data_stream(){
        int i = 1000;
        System.out.println("Entered delay function.");
        while(i >= 0){
            i--;
        }
        return;
    }
}