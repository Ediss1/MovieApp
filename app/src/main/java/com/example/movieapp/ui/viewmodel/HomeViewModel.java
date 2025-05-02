package com.example.movieapp.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.movieapp.data.model.Movie;
import com.example.movieapp.data.repository.MovieRepository;

import java.util.List;

public class HomeViewModel extends ViewModel {
    private MutableLiveData<List<Movie>> movieList;
    private MovieRepository repository;



    public HomeViewModel() {
        //repository = new MovieRepository();
        //movieList = repository.getPopularMovies();
    }

//    public LiveData<List<Movie>> getMovies() {
//        return movieList;
//    }
}
