package com.example.movieapp.ui.viewmodel;

import android.app.Application;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.movieapp.R;
import com.example.movieapp.data.model.GenresItem;
import com.example.movieapp.data.model.ListFilm;
import com.example.movieapp.data.model.SliderItems;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends AndroidViewModel {
    private static final String BASE_URL = "https://api.themoviedb.org/3/";
    private static final String API_KEY = "7927ac1d504e1ffef0950f83ebc572b7";

    private final MutableLiveData<List<SliderItems>> sliderItemsLiveData = new MutableLiveData<>();
    private final MutableLiveData<ListFilm> bestMoviesLiveData = new MutableLiveData<>();
    private final MutableLiveData<ListFilm> upcomingMoviesLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<GenresItem>> categoryLiveData = new MutableLiveData<>();
    private final MutableLiveData<ListFilm> searchResultsLiveData = new MutableLiveData<>();


    private final RequestQueue requestQueue;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        requestQueue = Volley.newRequestQueue(application);
    }

    public LiveData<List<SliderItems>> getSliderItems() {
        List<SliderItems> sliderItems = new ArrayList<>();
        sliderItems.add(new SliderItems(R.drawable.wide1));
        sliderItems.add(new SliderItems(R.drawable.wide2));
        sliderItems.add(new SliderItems(R.drawable.wide3));
        sliderItemsLiveData.setValue(sliderItems);
        return sliderItemsLiveData;
    }

    public LiveData<ListFilm> getBestMovies() {
        String url = BASE_URL + "movie/popular?api_key=" + API_KEY + "&language=en-US&page=1";
        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            ListFilm items = new Gson().fromJson(response, ListFilm.class);
            bestMoviesLiveData.setValue(items);
        }, error -> {
            // Log or handle error
        });
        requestQueue.add(request);
        return bestMoviesLiveData;
    }


    public LiveData<ListFilm> getUpcomingMovies() {
        String url = BASE_URL + "movie/upcoming?api_key=" + API_KEY + "&language=en-US&page=1";
        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            ListFilm items = new Gson().fromJson(response, ListFilm.class);
            upcomingMoviesLiveData.setValue(items);
        }, error -> {
            // Log or handle error
        });
        requestQueue.add(request);
        return upcomingMoviesLiveData;
    }


    public LiveData<List<GenresItem>> getCategories() {
        StringRequest request = new StringRequest(Request.Method.GET, "https://moviesapi.ir/api/v1/genres", response -> {
            ArrayList<GenresItem> items = new Gson().fromJson(response, new TypeToken<ArrayList<GenresItem>>(){}.getType());
            categoryLiveData.setValue(items);
        }, error -> {
            // Log error
        });
        requestQueue.add(request);
        return categoryLiveData;
    }

    public LiveData<ListFilm> searchMovies(String query) {
        String url = BASE_URL + "search/movie?api_key=" + API_KEY + "&query=" + Uri.encode(query);
        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            ListFilm items = new Gson().fromJson(response, ListFilm.class);
            searchResultsLiveData.setValue(items);
        }, error -> {
            // Log or handle error
        });
        requestQueue.add(request);
        return searchResultsLiveData;
    }
}
