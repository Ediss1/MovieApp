package com.example.movieapp.data.model;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TMDbApiService {

    @GET("movie/{movie_id}")
    Call<MovieItem> getMovieDetails(
            @Path("movie_id") int movieId,
            @Query("api_key") String apiKey
    );

    @GET("movie/{movie_id}/credits")
    Call<CreditsResponse> getMovieCredits(
            @Path("movie_id") int movieId,
            @Query("api_key") String apiKey
    );

    @GET("movie/popular")
    Call<ListMovie> getPopularMovies(@Query("api_key") String apiKey, @Query("language") String lang);

    @GET("movie/top_rated")
    Call<ListMovie> getTopRatedMovies(@Query("api_key") String apiKey, @Query("language") String lang);

    @GET("movie/upcoming")
    Call<ListMovie> getUpcomingMovies(@Query("api_key") String apiKey, @Query("language") String lang);

    @GET("search/movie")
    Call<ListMovie> searchMovies(@Query("api_key") String apiKey, @Query("query") String query);


}
