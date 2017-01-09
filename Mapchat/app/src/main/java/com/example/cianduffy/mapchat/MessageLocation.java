package com.example.cianduffy.mapchat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by David on 03/01/2017.
 */

public class MessageLocation {
    public List<Message> messages;
    public double longitude;
    public double latitude;

    public MessageLocation() {}

    public MessageLocation(ArrayList<Message> messages, double latitude, double longitude) {
        this.messages = messages;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void addMessage(Message message) {
        messages.add(message);
    }
}
