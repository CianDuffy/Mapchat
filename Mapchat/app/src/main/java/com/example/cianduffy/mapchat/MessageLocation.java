package com.example.cianduffy.mapchat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by David on 03/01/2017.
 */

public class MessageLocation {
    private List<Message> messages;
    private double longitude;
    private double latitude;

    public MessageLocation() {}

    public MessageLocation(ArrayList<Message> messages, double latitude, double longitude) {
        this.messages = messages;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void addMessage(Message message) {
        messages.add(message);
    }

    public String toString() {
        StringBuffer locationDescription = new StringBuffer();
        locationDescription.append("Lat: " + this.latitude + " / Lon: " + this.longitude + "\n");
        for(Message msg : messages) {
            locationDescription.append((new Date(msg.getTimestamp())) + ": " + msg.getMessageText() + "\n");
        }
        return locationDescription.toString();
    }

    public double getLatitude() {
        return this.latitude;
    }
    public double getLongitude() {
        return this.longitude;
    }

    public List<Message> getMessages() {
        return messages;
    }
}
