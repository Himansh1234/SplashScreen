package com.example.vsvll.splashscreen;

import android.net.Uri;

public class Event_details {

    String name;
   String uri,place;


    public Event_details(String name, String uri) {
        this.name = name;
        this.uri = uri;
    }

    public String getName() {
        return name;
    }

    public String getUri() {
        return uri;
    }
}
