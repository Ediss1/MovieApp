package com.example.movieapp.data.model;

public class Film {
    private int id;
    private String title;
    private String overview;
    private String poster_path;
    private String backdrop_path;
    private double vote_average;
    private String release_date;

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getOverview() { return overview; }
    public String getPosterPath() { return poster_path; }
    public String getBackdropPath() { return backdrop_path; }
    public double getVoteAverage() { return vote_average; }
    public String getReleaseDate() { return release_date; }

    // You can create helper to get full image URLs if needed:
    public String getFullPosterUrl() {
        return "https://image.tmdb.org/t/p/w500" + poster_path;
    }

    public String getFullBackdropUrl() {
        return "https://image.tmdb.org/t/p/w780" + backdrop_path;
    }
}
