package com.example.android.popularmovies;

import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.popularmovies.data.MovieContract;
import com.example.android.popularmovies.jsonutils.JsonUtils;
import com.example.android.popularmovies.model.Movie;
import com.example.android.popularmovies.networkutils.NetworkUtils;

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

    private int onSaveRecycler;

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
                switch (i) {
                    case 0:
                        new QueryMoviesTask().execute(networkUtils.getTopRatedUrl());
                        break;
                    case 1:
                        new QueryMoviesTask().execute(networkUtils.getPopularUrl());
                        break;
                    case 2:
                        new QueryMoviesTask().execute();
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

        networkUtils = new NetworkUtils(this);
        // initialize jsonUtils with display_size - we download bigger posters for bigger screens
        jsonUtils = new JsonUtils(display_size);

        mMovieList = new ArrayList<>();
        moviePostersAdapter = new MoviePostersAdapter(this, mMovieList);
        moviePostersRecyclerView.setAdapter(moviePostersAdapter);
        moviePostersRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        new QueryMoviesTask().execute(networkUtils.getTopRatedUrl());

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        onSaveRecycler = ((LinearLayoutManager) moviePostersRecyclerView.getLayoutManager()).findLastVisibleItemPosition();
        outState.putInt("KEY", onSaveRecycler);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        onSaveRecycler = savedInstanceState.getInt("KEY");
    }



    public class QueryMoviesTask extends AsyncTask<URL, Void, List<Movie>> {

        @Override
        protected List<Movie> doInBackground(URL... urls) {

            if (urls.length == 0) {
                Cursor cursor = getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI, null, null, null, null);
                return cursorToMovies(cursor);
            }
            URL url = urls[0];
            String searchResult = null;
            try {
                searchResult = NetworkUtils.getResponseFromHttpUrl(url);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if( searchResult != null) {
                return jsonUtils.parseMovie(searchResult);
            } else {
                return new ArrayList<>();
            }

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(List<Movie> movies) {
            super.onPostExecute(movies);
            mProgressBar.setVisibility(View.GONE);
            moviePostersAdapter.setItems(movies);
            moviePostersAdapter.notifyDataSetChanged();
            moviePostersRecyclerView.getLayoutManager().scrollToPosition(onSaveRecycler);
            // onSaveRecycler = 0;
        }
    }

    // set screen size by density
    private int getDisplaySize() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int density = displayMetrics.densityDpi;
        if (density <= 320) {
            return 1;
        } else if (density <= 480) {
            return 2;
        } else {
            return 3;
        }
    }

    private List<Movie> cursorToMovies(Cursor cursor) {
        List<Movie> cursorMovies = new ArrayList<>();
        try {
            while (cursor.moveToNext()) {
                int idIndex = cursor.getColumnIndexOrThrow("videoId");
                int titleIndex = cursor.getColumnIndexOrThrow("videoTitle");
                int descIndex = cursor.getColumnIndexOrThrow("videoOverview");
                int posterIndex = cursor.getColumnIndexOrThrow("videoPoster");
                int ratingIndex = cursor.getColumnIndexOrThrow("videoRating");
                int relaseIndex = cursor.getColumnIndexOrThrow("videoRelaseDate");
                Log.e("CursorToMovies", cursor.toString());
                cursorMovies.add(new Movie(cursor.getString(idIndex), cursor.getString(titleIndex),
                        cursor.getString(posterIndex), cursor.getString(descIndex), cursor.getString(ratingIndex)
                        , cursor.getString(relaseIndex)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
        return cursorMovies;
    }
}
