package com.example.chatsapp.model;

public class ChatListModel {
    private String chatListID, dateTime, lastMessage, member;

    public ChatListModel() {

    }

    public ChatListModel(String chatListID, String dateTime, String lastMessage, String member) {
        this.chatListID = chatListID;
        this.dateTime = dateTime;
        this.lastMessage = lastMessage;
        this.member = member;
    }

    public String getChatListID() {
        return chatListID;
    }

    public void setChatListID(String chatListID) {
        this.chatListID = chatListID;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getMember() {
        return member;
    }

    public void setMember(String member) {
        this.member = member;
    }
}
