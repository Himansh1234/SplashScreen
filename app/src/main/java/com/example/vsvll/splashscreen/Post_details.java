package com.example.vsvll.splashscreen;

public class Post_details {


    String id,name;
    String uri,place,time,username,city;


    public Post_details(String id ,String username,String name, String uri,String place,String city,String time) {
        this.name = name;
        this.username=username;
        this.uri = uri;
        this.place=place;
        this.time=time;
        this.id=id;
        this.city=city;
    }

    public String getName() {
        return name;
    }
    public String getId() {
        return id;
    }
    public String getUsername() {
        return username;
    }
    public String getUri() {
        return uri;
    }
    public String getPlace() {
        return place;
    }
    public String getTime() {
        return time;
    }
    public String getCity() {
        return city;
    }

}
