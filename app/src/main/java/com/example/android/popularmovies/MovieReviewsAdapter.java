package com.example.android.popularmovies;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.popularmovies.model.Review;

import java.util.List;

/**
 * Created by Juraj on 2/19/2018.
 */

public class MovieReviewsAdapter extends RecyclerView.Adapter<MovieReviewsAdapter.ViewHolder> {

    private List<Review> mReviews;

    public MovieReviewsAdapter(List<Review> list) {
        mReviews = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View ratingView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);
        ViewHolder viewHolder = new ViewHolder(ratingView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Review review = mReviews.get(position);
        TextView author = holder.author;
        author.setText(review.getAuthor());
        TextView content = holder.content;
        content.setText(review.getContent());
    }

    @Override
    public int getItemCount() {
        return mReviews.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView author;
        public TextView content;

        public ViewHolder(View itemView) {
            super(itemView);

            author = itemView.findViewById(R.id.rating_author);
            content = itemView.findViewById(R.id.rating_content);
        }
    }

    public void setItems(List<Review> reviews) {
        this.mReviews = reviews;
    }
}
