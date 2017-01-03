package com.example.cianduffy.mapchat;

/**
 * Created by David on 03/01/2017.
 */

public class MessageLocation {
    public Message[] messages;
    public double longitude;
    public double latitude;

    public MessageLocation(Message[] messages, double latitude, double longitude) {
        this.messages = messages;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
