package com.example.movieapp.ui.viewmodel;

import android.os.Handler;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.viewpager2.widget.ViewPager2;

import com.example.movieapp.R;
import com.example.movieapp.data.model.Movie;
import com.example.movieapp.data.model.SliderItems;
import com.example.movieapp.data.repository.MovieRepository;

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends ViewModel {

    private ViewPager2 viewPager2;
    private android.os.Handler slideHandler = new Handler();


    public LiveData<List<SliderItems>> getSliderItems() {
        MutableLiveData<List<SliderItems>> sliderItemsLiveData = new MutableLiveData<>();
        List<SliderItems> sliderItems = new ArrayList<>();
        sliderItems.add(new SliderItems(R.drawable.wide1));
        sliderItems.add(new SliderItems(R.drawable.wide2));
        sliderItems.add(new SliderItems(R.drawable.wide3));
        sliderItemsLiveData.setValue(sliderItems);
        return sliderItemsLiveData;
    }


}
