package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.model.Video;

import java.util.List;

/**
 * Created by Juraj on 2/19/2018.
 */

public class MovieVideosAdapter extends RecyclerView.Adapter<MovieVideosAdapter.ViewHolder> {

    List<Video> mVideoList;
    Context mContext;

    public MovieVideosAdapter(Context context, List<Video> videos) {
        mContext = context;
        mVideoList = videos;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView name;
        public TextView type;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.video_name);
            type = itemView.findViewById(R.id.video_type);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int pos = getAdapterPosition();
            Video video = mVideoList.get(pos);
            Uri link  = Uri.parse("https://www.youtube.com/watch?v=" + video.getKey());
            Intent intent = new Intent(Intent.ACTION_VIEW, link);
            if( intent.resolveActivity(mContext.getPackageManager()) != null) {
                mContext.startActivity(intent);
            }
        }
    }

    @Override
    public MovieVideosAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video, parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MovieVideosAdapter.ViewHolder holder, int position) {
        Video video = mVideoList.get(position);
        TextView name = holder.name;
        name.setText(video.getName());
        TextView type = holder.type;
        type.setText(video.getType());
    }

    @Override
    public int getItemCount() {
        return mVideoList.size();
    }

    public void setVideoList(List<Video> videoList) {
        mVideoList = videoList;
    }


}
