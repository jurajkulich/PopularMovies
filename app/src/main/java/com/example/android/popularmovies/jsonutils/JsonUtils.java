package com.example.android.popularmovies.jsonutils;

import com.example.android.popularmovies.model.Movie;
import com.example.android.popularmovies.model.Review;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Juraj on 2/16/2018.
 */

public class JsonUtils {

    private static final String RESULTS_PATH = "results";
    private static final String ID = "id";
    private static final String TITLE = "title";
    private static final String POSTER = "poster_path";
    private static final String OVERVIEW = "overview";
    private static final String RELASE_DATE = "release_date";
    private static final String VOTE_AVERAGE = "vote_average";

    private static final String AUTHOR = "author";
    private static final String CONTENT = "content";

    private static final String POSTER_URL = "http://image.tmdb.org/t/p/";

    private String posterSize;

    public JsonUtils() {}

    // append path to poster url by screen size
    public JsonUtils(int screenSize) {
        switch (screenSize) {
            case 1: {
                posterSize = "w185/";
                break;
            }
            case 2: {
                posterSize = "w500/";
                break;
            }
            case 3: {
                posterSize = "w780/";
            }
            default: {
                posterSize = "w500/";
                break;
            }
        }
    }

    public List<Movie> parseMovie(String json) {
        List<Movie> moviesDetails = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray movies = jsonObject.getJSONArray(RESULTS_PATH);
            for (int i = 0; i < movies.length(); i++) {
                JSONObject movie  = movies.getJSONObject(i);
                String id = "Not Available";
                if( movie.has(ID ))
                {
                    id = movie.optString(ID);
                }
                String title = "Not Available";
                if( movie.has(TITLE ))
                {
                    title = movie.optString(TITLE);
                }
                String overview = "Not Available";
                if( movie.has(OVERVIEW)) {
                    overview = movie.optString(OVERVIEW);
                }
                String poster = "Not Available";
                if( movie.has(POSTER)) {
                   poster = POSTER_URL + posterSize + movie.optString(POSTER);
                }
                String relase = "Not Available";
                if( movie.has(RELASE_DATE)) {
                    relase = movie.optString(RELASE_DATE);
                }
                String votes = "Not available";
                if( movie.has(VOTE_AVERAGE))
                votes = String.valueOf(movie.getDouble(VOTE_AVERAGE));
                moviesDetails.add(new Movie(id, title, poster, overview, votes, relase));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return moviesDetails;
    }

    public List<Review> parseReview(String json) {
        List<Review> reviewDetails = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray ratings = jsonObject.getJSONArray(RESULTS_PATH);
            for (int i = 0; i < ratings.length(); i++) {
                JSONObject rating  = ratings.getJSONObject(i);
                String author = "Not Available";
                if( rating.has(AUTHOR ))
                {
                    author = rating.optString(AUTHOR);
                }
                String content = "Not Available";
                if( rating.has(CONTENT)) {
                    content = rating.optString(CONTENT);
                }
                reviewDetails.add(new Review(author, content));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return reviewDetails;
    }
}
