package com.example.cianduffy.mapchat;

/**
 * Created by David on 12/11/2016.
 */

public class Message {
    private String messageText;
    private long timestamp;

    public Message(){}

    public long getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessageText() {
        return messageText;
    }
    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }
}
