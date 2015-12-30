package com.example.user.tweetweet;

/**
 * Created by user on 12/29/2015.
 */
public class ModelTweet {
    private  String text;
    private  String name;
    private String screen_name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScreen_name() {
        return screen_name;
    }

    public void setScreen_name(String screen_name) {
        this.screen_name = screen_name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }



}
