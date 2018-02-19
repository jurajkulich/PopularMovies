package com.example.android.popularmovies;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.android.popularmovies.jsonutils.JsonUtils;
import com.example.android.popularmovies.model.Movie;
import com.example.android.popularmovies.model.Review;
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

    private RecyclerView mMovieRatingsRecyclerView;
    private MovieReviewsAdapter mMovieReviewsAdapter;

    private Movie movie;
    private List<Review> mReviewList;
    private JsonUtils mJsonUtils;

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

        mReviewList = new ArrayList<>();
        mMovieRatingsRecyclerView = findViewById(R.id.movie_rating_recycler_view);
        mMovieReviewsAdapter = new MovieReviewsAdapter(this, mReviewList);
        mMovieRatingsRecyclerView.setAdapter(mMovieReviewsAdapter);
        mMovieRatingsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

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
        new QueryMovieReview().execute(NetworkUtils.getRatingsUrl(movie.getId()));
    }

    private void setUI(Movie movieDetail) {
        title.setText(movieDetail.getTitle());
        description.setText(movieDetail.getSynopsis());
        rating.setText(movieDetail.getRating());
        relaseDate.setText(movieDetail.getRelase_date());
        Picasso.with(getApplicationContext()).load(movieDetail.getImage()).into(poster);
        mRatingBar.setRating(Float.parseFloat(movieDetail.getRating()) / 2);
    }

    public class QueryMovieReview extends AsyncTask<URL, Void, String> {

        @Override
        protected String doInBackground(URL... urls) {
            URL url = urls[0];
            String searchResult = null;
            try {
                searchResult = NetworkUtils.getResponseFromHttpUrl(url);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return searchResult;
        }

        @Override
        protected void onPostExecute(String s) {
            if( s != null ) {
                mReviewList = mJsonUtils.parseReview(s);
            }
            mMovieReviewsAdapter.setItems(mReviewList);
            mMovieReviewsAdapter.notifyDataSetChanged();
            super.onPostExecute(s);
        }
    }
}
