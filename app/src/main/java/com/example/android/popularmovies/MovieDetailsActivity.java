package com.example.android.popularmovies;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.android.popularmovies.model.Movie;
import com.squareup.picasso.Picasso;

public class MovieDetailsActivity extends AppCompatActivity {

    private TextView title;
    private TextView description;
    private TextView rating;
    private TextView relaseDate;
    private ImageView poster;

    private RatingBar mRatingBar;

    private Movie movie;

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

        mRatingBar = findViewById(R.id.movie_rating_bar);
        mRatingBar.setMax(10);
        mRatingBar.setNumStars(5);


        Bundle bundle = getIntent().getExtras();


        if( bundle != null)
        {
            String title =  bundle.getString("title");
            String desc =  bundle.getString("description");
            String poster = bundle.getString("poster");
            String rating = bundle.getString("rating");
            String relase = bundle.getString("relase");
            movie = new Movie(title, poster, desc, rating, relase);
        }
        setUI(movie);
    }

    private void setUI(Movie movieDetail) {
        title.setText(movieDetail.getTitle());
        description.setText(movieDetail.getSynopsis());
        rating.setText(movieDetail.getRating());
        relaseDate.setText(movieDetail.getRelase_date());
        Picasso.with(getApplicationContext()).load(movieDetail.getImage()).into(poster);
        mRatingBar.setRating(Float.parseFloat(movieDetail.getRating())/2);
    }
}
