package com.example.android.popularmovies;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.data.MovieContract;
import com.example.android.popularmovies.data.MovieDbHelper;
import com.example.android.popularmovies.jsonutils.JsonUtils;
import com.example.android.popularmovies.model.Movie;
import com.example.android.popularmovies.model.Review;
import com.example.android.popularmovies.model.Video;
import com.example.android.popularmovies.networkutils.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MovieDetailsActivity extends AppCompatActivity {

    private TextView title;
    private TextView description;
    private TextView rating;
    private TextView relaseDate;
    private ImageView poster;

    private RatingBar mRatingBar;
    private ImageView favoriteStar;

    private ProgressBar mProgressBar;

    private RecyclerView mMovieRatingsRecyclerView;
    private MovieReviewsAdapter mMovieReviewsAdapter;

    private RecyclerView mMovieVideosRecyclerView;
    private MovieVideosAdapter mMovieVideosAdapter;

    private Movie movie;
    private List<Review> mReviewList;
    private List<Video> mVideosList;

    private JsonUtils mJsonUtils;

    private SQLiteDatabase mSQLiteDatabase;
    private MovieDbHelper mMovieDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        title = findViewById(R.id.movie_title);
        description = findViewById(R.id.movie_description);
        rating = findViewById(R.id.movie_rating);
        relaseDate = findViewById(R.id.movie_relase_date);
        poster = findViewById(R.id.movie_poster);

        mProgressBar = findViewById(R.id.details_progress_bar);
        favoriteStar = findViewById(R.id.favorite_image_view);

        mReviewList = new ArrayList<>();
        mMovieRatingsRecyclerView = findViewById(R.id.movie_rating_recycler_view);
        mMovieReviewsAdapter = new MovieReviewsAdapter(this, mReviewList);
        mMovieRatingsRecyclerView.setAdapter(mMovieReviewsAdapter);
        mMovieRatingsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mVideosList = new ArrayList<>();
        mMovieVideosRecyclerView = findViewById(R.id.movie_videos_recycler_view);
        mMovieVideosAdapter = new MovieVideosAdapter(this, mVideosList);
        mMovieVideosRecyclerView.setAdapter(mMovieVideosAdapter);
        mMovieVideosRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        mRatingBar = findViewById(R.id.movie_rating_bar);
        mRatingBar.setMax(10);
        mRatingBar.setNumStars(5);

        mJsonUtils = new JsonUtils();

        mMovieDbHelper = new MovieDbHelper(this);
        mSQLiteDatabase = mMovieDbHelper.getReadableDatabase();
        // mSQLiteDatabase.delete(MovieContract.MovieEntry.TABLE_NAME, null, null);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String id = bundle.getString("id");
            String title = bundle.getString("title");
            String desc = bundle.getString("description");
            String poster = bundle.getString("poster");
            String rating = bundle.getString("rating");
            String relase = bundle.getString("relase");
            movie = new Movie(id, title, poster, desc, rating, relase);
        }
        setUI(movie);


        if (checkIfDbContain()) {
            favoriteStar.setImageResource(R.drawable.ic_star_full);
        } else {
            favoriteStar.setImageResource(R.drawable.ic_star_border);
        }

        favoriteStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!checkIfDbContain()) {
                    addMovieToDb();
                    favoriteStar.setImageResource(R.drawable.ic_star_full);
                } else {
                    deleteMovieFromDb();
                    favoriteStar.setImageResource(R.drawable.ic_star_border);
                }
            }
        });

        new QueryMovieReview().execute(NetworkUtils.getRatingsUrl(movie.getId()), NetworkUtils.getVideosUrl(movie.getId()));
    }

    private void setUI(Movie movieDetail) {
        title.setText(movieDetail.getTitle());
        description.setText(movieDetail.getSynopsis());
        rating.setText(movieDetail.getRating());
        relaseDate.setText(movieDetail.getRelase_date());
        Picasso.with(getApplicationContext()).load(movieDetail.getImage()).into(poster);
        mRatingBar.setRating(Float.parseFloat(movieDetail.getRating()) / 2);
    }

    private void addMovieToDb() {
        mSQLiteDatabase = mMovieDbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MovieContract.MovieEntry.COLUMN_VIDEO_ID, movie.getId());
        contentValues.put(MovieContract.MovieEntry.COLUMN_VIDEO_TITLE, movie.getTitle());
        contentValues.put(MovieContract.MovieEntry.COLUMN_VIDEO_OVERVIEW, movie.getSynopsis());
        contentValues.put(MovieContract.MovieEntry.COLUMN_VIDEO_POSTER, movie.getImage());
        contentValues.put(MovieContract.MovieEntry.COLUMN_VIDEO_RATING, movie.getRating());
        contentValues.put(MovieContract.MovieEntry.COLUMN_VIDEO_RELASE_DATE, movie.getRelase_date());
        if (mSQLiteDatabase.insert(MovieContract.MovieEntry.TABLE_NAME, null, contentValues) == -1) {
            Toast.makeText(this, "Couldn't add to favorites", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteMovieFromDb() {
        mSQLiteDatabase = mMovieDbHelper.getWritableDatabase();
        if( mSQLiteDatabase.delete(MovieContract.MovieEntry.TABLE_NAME, MovieContract.MovieEntry.COLUMN_VIDEO_ID + " = ?",
                new String[] { movie.getId()}) != 0) {
            Toast.makeText(this, "Deleted from favorites", Toast.LENGTH_SHORT).show();
        }
    }


    private boolean checkIfDbContain() {
        Cursor cursor = mSQLiteDatabase.query(MovieContract.MovieEntry.TABLE_NAME, new String[]{MovieContract.MovieEntry.COLUMN_VIDEO_TITLE},
                MovieContract.MovieEntry.COLUMN_VIDEO_ID + " = ?", new String[]{movie.getId()}, null, null, null, null);
        if (cursor.moveToFirst()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onDestroy() {
        mSQLiteDatabase.close();
        super.onDestroy();
    }

    public class QueryMovieReview extends AsyncTask<URL, Void, String[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String[] doInBackground(URL... urls) {
            URL reviewsUrl = urls[0];
            URL videosUrl = urls[1];

            String reviewResult = null;
            String videoResults = null;
            try {
                reviewResult = NetworkUtils.getResponseFromHttpUrl(getBaseContext(),reviewsUrl);
                videoResults = NetworkUtils.getResponseFromHttpUrl(getBaseContext(),videosUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String arr[] = {reviewResult, videoResults};
            return arr;
        }

        @Override
        protected void onPostExecute(String[] s) {
            mProgressBar.setVisibility(View.GONE);
            if (s[0] != null) {
                mReviewList = mJsonUtils.parseReview(s[0]);
            }
            if (s[1] != null) {
                mVideosList = mJsonUtils.parseVideo(s[1]);
            }
            mMovieReviewsAdapter.setItems(mReviewList);
            mMovieReviewsAdapter.notifyDataSetChanged();
            mMovieVideosAdapter.setVideoList(mVideosList);
            mMovieVideosAdapter.notifyDataSetChanged();
            super.onPostExecute(s);
        }
    }


}
