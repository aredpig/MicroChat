package com.example.mealshared.Models;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;

public class User implements Comparable<User> {
    private int uid;
    private String email;
    private String name;
    private String Avatar;
    private String password;
    private ArrayList<Post> posts;
    private ArrayList<Integer> following;
    private ArrayList<Integer> follower;
    // Added by Jiawei. This hashmap to store all the message of this user.
    private HashMap<String, ArrayList<ChatItem>> messageList;
    // End here
    // Added by Jiawei, this arraylist store all the friends of this user. Each friend will be represented by its uid
    private ArrayList<String> friends;
    private ArrayList<String> blocks;
    private ArrayList<String> friendRequest;
    // Constructors.
    User(){}

    public User(int uid, String email, String name, String Password){
        this.Avatar = "";
        this.uid = uid;
        this.email = email;
        this.name = name;
        this.password = String.valueOf(Password.hashCode());
        this.posts = new ArrayList<>();
        this.following = new ArrayList<>();
        this.follower = new ArrayList<>();
        this.messageList = new HashMap<>();
        this.friends = new ArrayList<>();
        this.blocks = new ArrayList<>();
        this.friendRequest = new ArrayList<>();
    }

    // Getters & Setters
    public void setAvatar(String avatar) {
        Avatar = avatar;
    }

    public ArrayList<Integer> getFollower() {
        return follower;
    }

    public void addFollower(int uid) {
        if (!this.follower.contains(uid)) {
            follower.add(uid);
        }
    }

    public void delFollower(int uid) {
        if (follower.contains(uid)) {
            int index = follower.indexOf(uid);
            follower.remove(index);
        }

    }

    public void addFollowing(int uid) {
        if (!this.following.contains(uid)) {
            following.add(uid);
        }
    }

    public void delFollowing(int uid) {
        if (following.contains(uid)) {
            int index = following.indexOf(uid);
            following.remove(index);
        }

    }

    public ArrayList<Integer> getFollowing() {
        return following;
    }

    public String getAvatar() {
        return Avatar;
    }

    public int getUid(){
        return this.uid;
    }

    public void setUid(int uid){this.uid = uid;}

    public void AddPost(Post post){
        this.posts.add(post);
    }

    public String getEmail() {
        return email;
    }

    public String getName(){
        return this.name;
    }

    public String getPassword() {return this.password; }

    public ArrayList<Post> getPosts(){
        return this.posts;
    }

    public void setPosts(ArrayList<Post> posts) {
        this.posts = posts;
    }

    // Added by jiawei

    public HashMap<String, ArrayList<ChatItem>> getMessageList() {
        return messageList;
    }

    public void setMessageList(HashMap<String, ArrayList<ChatItem>> messageList) {
        this.messageList = messageList;
    }

    public ArrayList<String> getFriends() {
        return friends;
    }

    public void setFriends(ArrayList<String> friends) {
        this.friends = friends;
    }

    public ArrayList<String> getBlocks() {
        return blocks;
    }

    public void setBlocks(ArrayList<String> blocks) {
        this.blocks = blocks;
    }

    public ArrayList<String> getFriendRequest() {
        return friendRequest;
    }

    public void setFriendRequest(ArrayList<String> friendRequest) {
        this.friendRequest = friendRequest;
    }

    // End here.


    @NonNull
    public String toString() {
        return "User [uid: "+uid+ ", email:" + email+" ,name: "+name+",  posts amount: "+posts.size()+", Password: "+password+ " has Avatar: "+ (this.getAvatar().length() > 0) +" ]";
    }

    @Override
    public int compareTo(User u) {
        return this.uid-u.uid;
    }
}

