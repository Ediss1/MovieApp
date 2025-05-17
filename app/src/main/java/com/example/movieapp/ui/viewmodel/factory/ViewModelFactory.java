package com.example.movieapp.ui.viewmodel.factory;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.movieapp.data.repository.FirebaseRepository;
import com.example.movieapp.data.repository.MovieRepository;
import com.example.movieapp.ui.viewmodel.FavoritesViewModel;
import com.example.movieapp.ui.viewmodel.HomeViewModel;
import com.example.movieapp.ui.viewmodel.SearchViewModel;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private final Application application;
    private final MovieRepository movieRepository;
    private final FirebaseRepository firebaseRepository;

    public ViewModelFactory(Application application,
                            MovieRepository movieRepository,
                            FirebaseRepository firebaseRepository) {
        this.application = application;
        this.movieRepository = movieRepository;
        this.firebaseRepository = firebaseRepository;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(HomeViewModel.class)) {
            return (T) new HomeViewModel(application, movieRepository);
        } else if (modelClass.isAssignableFrom(FavoritesViewModel.class)) {
            return (T) new FavoritesViewModel(firebaseRepository, movieRepository);
        } else if (modelClass.isAssignableFrom(SearchViewModel.class)) {
            return (T) new SearchViewModel(firebaseRepository, movieRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}
