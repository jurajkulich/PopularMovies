package com.example.android.popularmovies.model;

/**
 * Created by Juraj on 2/16/2018.
 */

public class Movie {

    private final String title;
    private final String image;
    private final String synopsis;
    private final String rating;
    private final String relase_date;

    public Movie(String title, String image, String synopsis, String rating, String relase_date) {
        this.title = title;
        this.image = image;
        this.synopsis = synopsis;
        this.rating = rating;
        this.relase_date = relase_date;
    }

    public String getTitle() {
        return title;
    }

    public String getImage() {
        return image;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public String getRating() {
        return rating;
    }

    public String getRelase_date() {
        return relase_date;
    }
}
