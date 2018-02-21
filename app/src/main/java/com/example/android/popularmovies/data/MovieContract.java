package com.example.android.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Juraj on 2/20/2018.
 */

public class MovieContract {

    public static final String AUTHORITY = "com.example.android.popularmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String PATH_VIDEOS = "videos";

    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_VIDEOS).build();

        public static final String TABLE_NAME = "videos";

        public static final String COLUMN_VIDEO_TITLE = "videoTitle";
        public static final String COLUMN_VIDEO_ID = "videoId";
        public static final String COLUMN_VIDEO_OVERVIEW = "videoOverview";
        public static final String COLUMN_VIDEO_POSTER = "videoPoster";
        public static final String COLUMN_VIDEO_RATING = "videoRating";
        public static final String COLUMN_VIDEO_RELASE_DATE = "videoRelaseDate";
    }
}
