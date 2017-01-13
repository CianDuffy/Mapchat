package com.example.cianduffy.mapchat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
        locationDescription.append("\nLatitude: " + this.latitude + "\nLongitude: " + this.longitude + "\n\n");
        for(Message msg : messages) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd,yyyy HH:mm", Locale.ENGLISH);
            Date resultDate = new Date(msg.getTimestamp());
            String dateString = simpleDateFormat.format(resultDate);
            locationDescription.append(dateString + ": " + msg.getMessageText() + "\n");
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
