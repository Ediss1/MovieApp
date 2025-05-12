package com.example.movieapp.data.model;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TMDbApiService {
    @GET("movie/popular")
    Call<ListFilm> getPopularMovies(@Query("api_key") String apiKey);

    @GET("movie/{movie_id}")
    Call<MovieItem> getMovieDetails(
            @Path("movie_id") int movieId,
            @Query("api_key") String apiKey
    );

    @GET("search/movie")
    Call<ListFilm> searchMovies(@Query("api_key") String apiKey, @Query("query") String query);
}
