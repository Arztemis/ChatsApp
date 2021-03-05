package com.example.chatsapp.model;

public class MessageModel {
    private String sender, reciever, message, date, type;

    public MessageModel() {
    }

    public MessageModel(String sender, String reciever, String message, String date, String type) {
        this.sender = sender;
        this.reciever = reciever;
        this.message = message;
        this.date = date;
        this.type = type;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReciever() {
        return reciever;
    }

    public void setReciever(String reciever) {
        this.reciever = reciever;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
