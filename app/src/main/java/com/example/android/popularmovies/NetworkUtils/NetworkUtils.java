package com.example.android.popularmovies.NetworkUtils;

import android.net.Uri;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Juraj on 2/16/2018.
 */

public class NetworkUtils {

    // You can get API KEY on https://www.themoviedb.org/
    private static final String KEY = "";
    private static final String SCHEME = "https";
    private static final String BASIC_URL = "api.themoviedb.org";
    private static final String PATH = "3";
    private static final String MOVIE_PATH = "movie";
    private static final String PARAM_KEY_URL = "api_key";
    private static final String TOP_RATED_URL = "top_rated";
    private static final String POPURAL_URL = "popular";

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
