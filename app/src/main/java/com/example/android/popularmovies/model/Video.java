package com.example.android.popularmovies.model;

/**
 * Created by Juraj on 2/19/2018.
 */

public class Video {

    private String name;
    private String key;
    private String type;

    public Video(String name, String key, String type) {
        this.name = name;
        this.key = key;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getKey() {
        return key;
    }

    public String getType() {
        return type;
    }
}
