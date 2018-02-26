package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.popularmovies.model.Movie;

import java.util.List;


/**
 * Created by Juraj on 2/17/2018.
 */

public class MoviePostersAdapter extends RecyclerView.Adapter<MoviePostersAdapter.ViewHolder> {

    private Context context;
    private List<Movie> movies;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView mImageView;

        public ViewHolder(View view) {
            super(view);
            mImageView = view.findViewById(R.id.item_movie_image);
            view.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            int pos = getAdapterPosition();
            Movie movie = movies.get(pos);
            Intent intent = new Intent(view.getContext(), MovieDetailsActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("id", movie.getId());
            bundle.putString("title", movie.getTitle());
            bundle.putString("description", movie.getSynopsis());
            bundle.putString("poster", movie.getImage());
            bundle.putString("rating", movie.getRating());
            bundle.putString("relase", movie.getRelase_date());
            intent.putExtras(bundle);
            view.getContext().startActivity(intent);
        }
    }

    public MoviePostersAdapter(Context context, List<Movie> movies) {
        this.context = context;
        this.movies = movies;
    }

    @Override
    public MoviePostersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View moviePoster = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie, parent, false);
        return new ViewHolder(moviePoster);
    }

    @Override
    public void onBindViewHolder(MoviePostersAdapter.ViewHolder holder, int position) {
        String url = movies.get(position).getImage();
        GlideApp.with(context).load(url).placeholder(R.drawable.poster_placeholder).into(holder.mImageView);
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public void setItems(List<Movie> movie) {
        this.movies = movie;
    }

}
