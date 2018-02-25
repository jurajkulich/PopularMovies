package com.example.android.popularmovies;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.data.MovieContract;
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

public class MovieDetailsActivity extends AppCompatActivity {

    private TextView title;
    private TextView description;
    private TextView rating;
    private TextView relaseDate;
    private ImageView poster;

    private RatingBar mRatingBar;
    private Menu favoriteStar;

    private ProgressBar mProgressBar;

    private RecyclerView mMovieRatingsRecyclerView;
    private MovieReviewsAdapter mMovieReviewsAdapter;

    private RecyclerView mMovieVideosRecyclerView;
    private MovieVideosAdapter mMovieVideosAdapter;

    private Movie movie;
    private List<Review> mReviewList;
    private List<Video> mVideosList;

    private JsonUtils mJsonUtils;

    private float offset;
    private static final String OFFSET_KEY = "OFFSET_KEY";
    private int onSaveRecyclerReview;
    private static final String RECYCLERVIEW_KEY = "RECYCLERVIEW_KEY";

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

        mReviewList = new ArrayList<>();
        mMovieRatingsRecyclerView = findViewById(R.id.movie_rating_recycler_view);
        mMovieRatingsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mMovieReviewsAdapter = new MovieReviewsAdapter(mReviewList);
        mMovieRatingsRecyclerView.setAdapter(mMovieReviewsAdapter);

        mVideosList = new ArrayList<>();
        mMovieVideosRecyclerView = findViewById(R.id.movie_videos_recycler_view);
        mMovieVideosAdapter = new MovieVideosAdapter(this, mVideosList);
        mMovieVideosRecyclerView.setAdapter(mMovieVideosAdapter);
        mMovieVideosRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        mRatingBar = findViewById(R.id.movie_rating_bar);
        mRatingBar.setMax(10);
        mRatingBar.setNumStars(5);

        mJsonUtils = new JsonUtils();

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
        new QueryMovieReview().execute(NetworkUtils.getRatingsUrl(movie.getId()), NetworkUtils.getVideosUrl(movie.getId()));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        onSaveRecyclerReview = ((LinearLayoutManager) mMovieRatingsRecyclerView.getLayoutManager()).findFirstVisibleItemPosition();
        View firstItemView = mMovieRatingsRecyclerView.getLayoutManager().findViewByPosition(onSaveRecyclerReview);
        if (firstItemView != null) {
            offset = firstItemView.getTop();
        } else {
            offset = 0;
        }

        outState.putFloat(OFFSET_KEY, offset);
        outState.putInt(RECYCLERVIEW_KEY, onSaveRecyclerReview);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        onSaveRecyclerReview = savedInstanceState.getInt(RECYCLERVIEW_KEY);
        offset = savedInstanceState.getFloat(OFFSET_KEY);
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

        ContentValues contentValues = new ContentValues();
        contentValues.put(MovieContract.MovieEntry.COLUMN_VIDEO_ID, movie.getId());
        contentValues.put(MovieContract.MovieEntry.COLUMN_VIDEO_TITLE, movie.getTitle());
        contentValues.put(MovieContract.MovieEntry.COLUMN_VIDEO_OVERVIEW, movie.getSynopsis());
        contentValues.put(MovieContract.MovieEntry.COLUMN_VIDEO_POSTER, movie.getImage());
        contentValues.put(MovieContract.MovieEntry.COLUMN_VIDEO_RATING, movie.getRating());
        contentValues.put(MovieContract.MovieEntry.COLUMN_VIDEO_RELASE_DATE, movie.getRelase_date());

        getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, contentValues);
        Toast.makeText(this, "Added to favorites", Toast.LENGTH_SHORT).show();

    }

    private void deleteMovieFromDb() {
        if (getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI.buildUpon().appendPath(movie.getId()).build(), MovieContract.MovieEntry.COLUMN_VIDEO_ID + " = ?",
                new String[]{movie.getId()}) != 0) {
            Toast.makeText(this, "Deleted from favorites", Toast.LENGTH_SHORT).show();
        }
    }


    private boolean checkIfDbContain() {
        Cursor cursor = getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI, null, MovieContract.MovieEntry.COLUMN_VIDEO_ID + " = ?", new String[]{movie.getId()}, null, null);
        if (cursor.moveToFirst()) {
            cursor.close();
            return true;
        } else {
            cursor.close();
            return false;
        }
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
                reviewResult = NetworkUtils.getResponseFromHttpUrl(reviewsUrl);
                videoResults = NetworkUtils.getResponseFromHttpUrl(videosUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String arr[] = {reviewResult, videoResults};
            return arr;
        }

        @Override
        protected void onPostExecute(String[] s) {
            super.onPostExecute(s);
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
            ((LinearLayoutManager) mMovieRatingsRecyclerView.getLayoutManager()).scrollToPositionWithOffset(onSaveRecyclerReview, (int) offset);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.movie_detail_toolbar_menu, menu);
        favoriteStar = menu;
        MenuItem menuItem = menu.findItem(R.id.movie_detail_menu_favorite);
        if (checkIfDbContain()) {
            menuItem.setIcon(R.drawable.ic_star_full);
        } else {
            menuItem.setIcon(R.drawable.ic_star_border);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.movie_detail_menu_favorite: {
                if (!checkIfDbContain()) {
                    addMovieToDb();
                    favoriteStar.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_star_full));
                } else {
                    deleteMovieFromDb();
                    favoriteStar.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_star_border));
                }
                break;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
        return true;
    }
}
