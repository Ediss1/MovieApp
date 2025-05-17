package com.example.movieapp.data.repository;

import com.example.movieapp.BuildConfig;
import com.example.movieapp.data.model.ApiClient;
import com.example.movieapp.data.model.CreditsResponse;
import com.example.movieapp.data.model.ListMovie;
import com.example.movieapp.data.model.MovieItem;
import com.example.movieapp.data.model.TMDbApiService;

import retrofit2.Callback;

public class MovieRepository {

    private final TMDbApiService apiService;
    private final String API_KEY = "7927ac1d504e1ffef0950f83ebc572b7";

    public MovieRepository() {
        apiService = ApiClient.getApiService();
    }

    public void getPopularMovies(Callback<ListMovie> callback) {
        apiService.getPopularMovies(API_KEY, "en-US").enqueue(callback);
    }

    public void getTopRatedMovies(Callback<ListMovie> callback) {
        apiService.getTopRatedMovies(API_KEY, "en-US").enqueue(callback);
    }

    public void getUpcomingMovies(Callback<ListMovie> callback) {
        apiService.getUpcomingMovies(API_KEY, "en-US").enqueue(callback);
    }

    public void searchMovies(String query, Callback<ListMovie> callback) {
        apiService.searchMovies(API_KEY, query).enqueue(callback);
    }

    public void fetchMovieDetails(int movieId, Callback<MovieItem> callback) {
        apiService.getMovieDetails(movieId, API_KEY).enqueue(callback);
    }

    public void fetchMovieCredits(int movieId, Callback<CreditsResponse> callback) {
        apiService.getMovieCredits(movieId, BuildConfig.TMDB_API_KEY).enqueue(callback);
    }

}
