package com.example.android.popularmovies;

/**
 * Movie record object. (Getters and setters omitted.)
 * Created by Jack on 11/5/2016.
 */

@SuppressWarnings("FieldCanBeLocal")
class MovieRecord {

    private String id;
    String originalTitle;
    String posterPath;
    String overview;
    String voteAverage;
    String releaseDate;

    MovieRecord(String id){
        this.id=id;

    }
}
