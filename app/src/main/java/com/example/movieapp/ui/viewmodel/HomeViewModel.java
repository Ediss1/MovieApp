package com.example.movieapp.ui.viewmodel;

import android.app.Application;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.movieapp.R;
import com.example.movieapp.data.model.ListMovie;
import com.example.movieapp.data.model.MovieItem;
import com.example.movieapp.data.model.SliderItems;
import com.example.movieapp.data.repository.MovieRepository;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomeViewModel extends AndroidViewModel {

    private final MutableLiveData<List<SliderItems>> sliderItemsLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoadingBestMovies = new MutableLiveData<>();
    private final MutableLiveData<List<MovieItem>> bestMovies = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoadingTopRatedMovies = new MutableLiveData<>();
    private final MutableLiveData<List<MovieItem>> topRatedMovies = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoadingUpcomingMovies = new MutableLiveData<>();
    private final MutableLiveData<List<MovieItem>> upComingMovies = new MutableLiveData<>();

    private final MovieRepository movieRepository;

    public HomeViewModel(@NonNull Application application, MovieRepository movieRepository) {
        super(application);
        this.movieRepository = movieRepository;

        loadSliderItems();
        loadTopRatedMovies();
        loadBestMovies();
        loadUpComingMovies();
    }

    public LiveData<List<SliderItems>> getSliderItems() {
        return sliderItemsLiveData;
    }

    public LiveData<Boolean> getIsLoadingBestMovies() {
        return isLoadingBestMovies;
    }

    public LiveData<List<MovieItem>> getBestMovies() {
        return bestMovies;
    }

    public LiveData<Boolean> getIsLoadingTopRatedMovies() {
        return isLoadingTopRatedMovies;
    }

    public LiveData<List<MovieItem>> getTopRatedMovies() {
        return topRatedMovies;
    }

    public LiveData<Boolean> getIsLoadingUpcomingMovies() {
        return isLoadingUpcomingMovies;
    }

    public LiveData<List<MovieItem>> getUpcomingMovies() {
        return upComingMovies;
    }

    private void loadSliderItems() {
        List<SliderItems> sliderItems = new ArrayList<>();
        sliderItems.add(new SliderItems(R.drawable.wide1));
        sliderItems.add(new SliderItems(R.drawable.wide2));
        sliderItems.add(new SliderItems(R.drawable.wide3));
        sliderItemsLiveData.setValue(sliderItems);
    }

    private void loadBestMovies() {
        isLoadingBestMovies.setValue(true);
        movieRepository.getPopularMovies(new Callback<ListMovie>() {
            @Override
            public void onResponse(Call<ListMovie> call, Response<ListMovie> response) {
                isLoadingBestMovies.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    bestMovies.setValue(response.body().getResults());
                }
            }

            @Override
            public void onFailure(Call<ListMovie> call, Throwable t) {
                isLoadingBestMovies.setValue(false);
            }
        });
    }

    private void loadTopRatedMovies() {
        isLoadingTopRatedMovies.setValue(true);
        movieRepository.getTopRatedMovies(new Callback<ListMovie>() {
            @Override
            public void onResponse(Call<ListMovie> call, Response<ListMovie> response) {
                isLoadingTopRatedMovies.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    topRatedMovies.setValue(response.body().getResults());
                }
            }

            @Override
            public void onFailure(Call<ListMovie> call, Throwable t) {
                isLoadingTopRatedMovies.setValue(false);
            }
        });
    }

    private void loadUpComingMovies() {
        isLoadingUpcomingMovies.setValue(true);
        movieRepository.getUpcomingMovies(new Callback<ListMovie>() {
            @Override
            public void onResponse(Call<ListMovie> call, Response<ListMovie> response) {
                isLoadingUpcomingMovies.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    upComingMovies.setValue(response.body().getResults());
                }
            }

            @Override
            public void onFailure(Call<ListMovie> call, Throwable t) {
                isLoadingUpcomingMovies.setValue(false);
            }
        });
    }
}

