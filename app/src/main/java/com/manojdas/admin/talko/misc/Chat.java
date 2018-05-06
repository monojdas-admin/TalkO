package com.manojdas.admin.talko.misc;

/**
 * Created by Manoj Das on 23-Mar-18.
 */

public class Chat {

    private String id;
    private String mid;
    private String content;
    private String sender;
    private String receiver;
    private String dateTime;

    public Chat() {
    }

    public Chat(String content, String sender, String receiver) {
        this.content = content;
        this.sender = sender;
        this.receiver = receiver;
    }

    public Chat(String mid,String content, String sender, String receiver) {
        this.mid = mid;
        this.content = content;
        this.sender = sender;
        this.receiver = receiver;
    }

    public Chat(String mid,String content, String sender, String receiver, String dateTime) {
        this.mid = mid;
        this.content = content;
        this.sender = sender;
        this.receiver = receiver;
        this.dateTime = dateTime;
    }

    public Chat(String id,String mid, String content, String sender, String receiver, String dateTime) {
        this.id = id;
        this.mid = mid;
        this.content = content;
        this.sender = sender;
        this.receiver = receiver;
        this.dateTime = dateTime;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getMid() {
        return mid;
    }
    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
}
