package com.example.mealshared.Models;

import android.location.Address;
import android.text.format.Time;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class Post {
    private int owner;
    private String timeline;
    private String context;
    private String tag;
    private ArrayList<Integer> likers;
    private String address;

    Post(){}

    public Post(int owner, String timeline, String context, String tag, String address){
        this.owner = owner;
        this.context = context;
        this.timeline = timeline;
        this.tag = tag;
        this.likers = new ArrayList<>();
        this.address = address;
    }

    public String getTag() {
        return tag;
    }

    public String getAddress() {
        return address;
    }

    public String getTimeline() {
        return timeline;
    }

    public ArrayList<Integer> getLikers() {
        return likers;
    }

    public int AllLikes() {
        return likers.size();
    }

    public int getOwner() {
        return this.owner;
    }


    public void addLikes(int uid) {
        if (!likers.contains(uid)) {
            likers.add(uid);
        }
    }

    public void delLikes(int uid) {
        if (likers.contains(uid)) {
            int index = likers.indexOf(uid);
            likers.remove(index);
        }
    }

    public String getContext() {
        return context;
    }

    @NonNull
    public String toString() {
        return "Post [tag: "+tag+" time: "+timeline+", post context: "+context+", owns by:"+ owner +"from"+ address +" ]";
    }
}
