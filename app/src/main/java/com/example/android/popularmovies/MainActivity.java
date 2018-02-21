package com.example.android.popularmovies;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.example.android.popularmovies.networkutils.NetworkUtils;
import com.example.android.popularmovies.jsonutils.JsonUtils;
import com.example.android.popularmovies.model.Movie;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<Movie> mMovieList;

    private NetworkUtils networkUtils;
    private JsonUtils jsonUtils;

    private RecyclerView moviePostersRecyclerView;
    private MoviePostersAdapter moviePostersAdapter;
    private ProgressBar mProgressBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get display size: 1 for small, 2 for medium, 3 for big screens
        int display_size = getDisplaySize();

        Spinner spinner = findViewById(R.id.movie_spinner);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.movies, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch(i) {
                    case 0:
                        new QueryMoviesTask().execute(networkUtils.getTopRatedUrl());
                        break;
                    case 1:
                        new QueryMoviesTask().execute(networkUtils.getPopularUrl());
                        break;
                    default:
                        new QueryMoviesTask().execute(networkUtils.getTopRatedUrl());
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                new QueryMoviesTask().execute(networkUtils.getTopRatedUrl());
            }

        });

        mProgressBar = findViewById(R.id.movies_progress_bar);
        moviePostersRecyclerView = findViewById(R.id.movie_recycler_view);

        networkUtils = new NetworkUtils();
        // initialize jsonUtils with display_size - we download bigger posters for bigger screens
        jsonUtils = new JsonUtils(display_size);

        mMovieList = new ArrayList<>();
        moviePostersAdapter = new MoviePostersAdapter(this, mMovieList);
        moviePostersRecyclerView.setAdapter(moviePostersAdapter);
        moviePostersRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        new QueryMoviesTask().execute(networkUtils.getTopRatedUrl() );

    }

    public class QueryMoviesTask extends AsyncTask<URL, Void, String> {

        @Override
        protected String doInBackground(URL... urls) {
            URL url = urls[0];
            String searchResult = null;
            try {
                searchResult = NetworkUtils.getResponseFromHttpUrl(getBaseContext(), url);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return searchResult;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String s) {
            if( s != null ) {
                mMovieList = jsonUtils.parseMovie(s);
            }
            mProgressBar.setVisibility(View.GONE);
            moviePostersAdapter.setItems(mMovieList);
            moviePostersAdapter.notifyDataSetChanged();
        }
    }

    // set screen size by density
    private int getDisplaySize() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int density = displayMetrics.densityDpi;
        if( density <= 320) {
            return 1;
        } else if( density <= 480) {
            return 2;
        } else {
            return 3;
        }
    }



}
