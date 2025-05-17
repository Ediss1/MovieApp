package com.example.movieapp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

import android.app.Application;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.example.movieapp.data.model.ListMovie;
import com.example.movieapp.data.model.MovieItem;
import com.example.movieapp.data.model.SliderItems;
import com.example.movieapp.data.repository.MovieRepository;
import com.example.movieapp.ui.viewmodel.HomeViewModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import retrofit2.Callback;
import retrofit2.Response;

@RunWith(MockitoJUnitRunner.class)
public class HomeViewModelTest {

    @Mock
    Application mockApplication;

    @Mock
    MovieRepository mockMovieRepository;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private HomeViewModel homeViewModel;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        homeViewModel = new HomeViewModel(mockApplication, mockMovieRepository);
    }

    @Test
    public void testLoadSliderItems_setsCorrectData() {
        List<SliderItems> value = homeViewModel.getSliderItems().getValue();
        assertNotNull(value);
        assertEquals(3, value.size());
    }

    @Test
    public void testLoadBestMovies_success() {
        List<MovieItem> fakeMovies = Arrays.asList(new MovieItem(), new MovieItem());

        // simulate a Retrofit call
        doAnswer(invocation -> {
            Callback<ListMovie> callback = invocation.getArgument(0);
            ListMovie listMovie = new ListMovie();
            listMovie.setResults(fakeMovies);

            Response<ListMovie> response = Response.success(listMovie);
            callback.onResponse(null, response);
            return null;
        }).when(mockMovieRepository).getPopularMovies(any());

        homeViewModel = new HomeViewModel(mockApplication, mockMovieRepository);

        assertEquals(false, homeViewModel.getIsLoadingBestMovies().getValue());
        assertEquals(fakeMovies, homeViewModel.getBestMovies().getValue());
    }

    @Test
    public void testLoadBestMovies_failure() {
        doAnswer(invocation -> {
            Callback<ListMovie> callback = invocation.getArgument(0);
            callback.onFailure(null, new Throwable("error"));
            return null;
        }).when(mockMovieRepository).getPopularMovies(any());

        homeViewModel = new HomeViewModel(mockApplication, mockMovieRepository);

        assertEquals(false, homeViewModel.getIsLoadingBestMovies().getValue());
        assertNull(homeViewModel.getBestMovies().getValue());
    }
}
