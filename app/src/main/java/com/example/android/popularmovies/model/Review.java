package com.example.android.popularmovies.model;

/**
 * Created by Juraj on 2/19/2018.
 */

public class Review {

    String author;
    String content;

    public Review(String author, String content) {
        this.author = author;
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }
}
