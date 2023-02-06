package com.example.mealshared.Models;

public class ChatItem {
    private String messageContent;
    private String timeStamp;
    private String sender;
    private String receiver;

    public ChatItem(){

    }

    public ChatItem(String messageContent, String timeStamp, String sender, String receiver) {
        this.messageContent = messageContent;
        this.timeStamp = timeStamp;
        this.sender = sender;
        this.receiver = receiver;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }
}
