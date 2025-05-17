package com.example.movieapp.ui.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.movieapp.data.model.MovieItem;
import com.example.movieapp.data.repository.FirebaseRepository;
import com.example.movieapp.data.repository.MovieRepository;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoritesViewModel extends ViewModel {

    private final FirebaseRepository firebaseRepository;
    private final MovieRepository movieRepository;

    private final MutableLiveData<List<MovieItem>> favoriteMovies = new MutableLiveData<>();
    private final String userId;

    public FavoritesViewModel(FirebaseRepository firebaseRepository, MovieRepository movieRepository) {
        this.firebaseRepository = firebaseRepository;
        this.movieRepository = movieRepository;
        this.userId = firebaseRepository.getCurrentUser().getUid();

        firebaseRepository.listenToFavorites(userId, (querySnapshot, error) -> {
            if (error != null || querySnapshot == null){
                Log.e("FavoritesViewModel", "Firebase error", error);
                return;
            }

            List<Long> movieIds = new ArrayList<>();
            for (DocumentSnapshot doc : querySnapshot){
                Long idLong = doc.getLong("movieId");
                if (idLong != null){
                    movieIds.add(idLong);
                }
            }
            fetchMovieDetails(movieIds);
        });
    }

    public LiveData<List<MovieItem>> getFavoriteMovies() {
        return favoriteMovies;
    }

    private void fetchMovieDetails(List<Long> movieIds) {
        List<MovieItem> fetchedMovies = new ArrayList<>();

        for (Long movieId : movieIds) {
            movieRepository.fetchMovieDetails(movieId.intValue(), new Callback<MovieItem>() {
                        @Override
                        public void onResponse(Call<MovieItem> call, Response<MovieItem> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                fetchedMovies.add(response.body());
                                favoriteMovies.postValue(new ArrayList<>(fetchedMovies));
                            }
                        }

                        @Override
                        public void onFailure(Call<MovieItem> call, Throwable t) {
                            Log.e("FavoritesViewModel", "API error: ", t);
                        }
                    });
        }

        if (movieIds.isEmpty()) {
            favoriteMovies.setValue(new ArrayList<>()); // Clear list if none
        }
    }
}
