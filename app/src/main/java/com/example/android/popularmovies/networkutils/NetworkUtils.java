package com.example.android.popularmovies.networkutils;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.Nullable;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Juraj on 2/16/2018.
 */

public class NetworkUtils {

    private static final String KEY = "";
    private static final String SCHEME = "https";
    private static final String BASIC_URL = "api.themoviedb.org";
    private static final String PATH = "3";
    private static final String MOVIE_PATH = "movie";
    private static final String PARAM_KEY_URL = "api_key";
    private static final String TOP_RATED_URL = "top_rated";
    private static final String POPURAL_URL = "popular";
    private static final String REVIEWS = "reviews";
    private static final String VIDEOS = "videos";

    public URL getTopRatedUrl() {
        URL url = null;
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(SCHEME)
                .authority(BASIC_URL)
                .appendPath(PATH)
                .appendPath(MOVIE_PATH)
                .appendPath(TOP_RATED_URL)
                .appendQueryParameter(PARAM_KEY_URL, KEY);
        try {
            url = new URL(builder.build().toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public URL getPopularUrl() {
        URL url = null;
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(SCHEME)
                .authority(BASIC_URL)
                .appendPath(PATH)
                .appendPath(MOVIE_PATH)
                .appendPath(POPURAL_URL)
                .appendQueryParameter(PARAM_KEY_URL, KEY);
        try {
            url = new URL(builder.build().toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static URL getRatingsUrl(String id) {
        URL url = null;
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(SCHEME)
                .authority(BASIC_URL)
                .appendPath(PATH)
                .appendPath(MOVIE_PATH)
                .appendPath(id)
                .appendPath(REVIEWS)
                .appendQueryParameter(PARAM_KEY_URL, KEY);
        try {
            url = new URL(builder.build().toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static URL getVideosUrl(String id) {
        URL url = null;
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(SCHEME)
                .authority(BASIC_URL)
                .appendPath(PATH)
                .appendPath(MOVIE_PATH)
                .appendPath(id)
                .appendPath(VIDEOS)
                .appendQueryParameter(PARAM_KEY_URL, KEY);
        try {
            url = new URL(builder.build().toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        if( url != null) {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(url).build();
            Response response = client.newCall(request).execute();
            if( response.body() != null)
                return response.body().string();
        }
        return "";
    }
}
