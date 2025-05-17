package com.example.movieapp.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.movieapp.data.model.ListMovie;
import com.example.movieapp.data.model.MovieItem;
import com.example.movieapp.data.repository.FirebaseRepository;
import com.example.movieapp.data.repository.MovieRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchViewModel extends ViewModel {
    private final FirebaseRepository firebaseRepository;
    private final MovieRepository movieRepository;

    private final MutableLiveData<List<MovieItem>> searchResults = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isEmptyResult = new MutableLiveData<>(false);
    private final MutableLiveData<List<String>> searchHistory = new MutableLiveData<>(new ArrayList<>());
    private final String userId;

    public SearchViewModel(FirebaseRepository firebaseRepository, MovieRepository movieRepository) {
        this.firebaseRepository = firebaseRepository;
        this.movieRepository = movieRepository;
        this.userId = firebaseRepository.getCurrentUser() != null
                ? firebaseRepository.getCurrentUser().getUid()
                : null;
    }

    public void searchMovies(String query) {
        isLoading.setValue(true);
        movieRepository.searchMovies(query, new Callback<ListMovie>() {
            @Override
            public void onResponse(Call<ListMovie> call, Response<ListMovie> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    List<MovieItem> results = response.body().getResults();
                    searchResults.setValue(results);
                    isEmptyResult.setValue(results == null || results.isEmpty());
                } else {
                    searchResults.setValue(Collections.emptyList());
                    isEmptyResult.setValue(true);
                }
            }

            @Override
            public void onFailure(Call<ListMovie> call, Throwable t) {
                searchResults.setValue(Collections.emptyList());
                isEmptyResult.postValue(true);
            }
        });
    }

    public void loadSearchHistory() {
        if (userId == null) return;

        firebaseRepository.loadSearchHistory(userId, history -> searchHistory.setValue(history));
    }

    public void saveSearchTerm(String term) {
        if (userId == null) return;

        firebaseRepository.saveSearchTerm(userId, term);
    }

    public void clearSearchResults() {
        searchResults.setValue(Collections.emptyList());
    }

    public LiveData<Boolean> getIsEmptyResult() {
        return isEmptyResult;
    }

    public LiveData<List<MovieItem>> getSearchResults() {
        return searchResults;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<List<String>> getSearchHistory() {
        return searchHistory;
    }

}



