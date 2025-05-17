package com.example.movieapp;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.example.movieapp.data.model.ListMovie;
import com.example.movieapp.data.model.MovieItem;
import com.example.movieapp.data.repository.FirebaseRepository;
import com.example.movieapp.data.repository.MovieRepository;
import com.example.movieapp.ui.viewmodel.SearchViewModel;
import com.google.firebase.auth.FirebaseUser;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Callback;
import retrofit2.Response;

@RunWith(MockitoJUnitRunner.class)
public class SearchViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    @Mock
    FirebaseRepository mockFirebaseRepository;

    @Mock
    MovieRepository mockMovieRepository;

    @Mock
    FirebaseUser mockUser;

    SearchViewModel viewModel;

    @Before
    public void setup() {
        when(mockFirebaseRepository.getCurrentUser()).thenReturn(mockUser);
        when(mockUser.getUid()).thenReturn("testUserId");

        viewModel = new SearchViewModel(mockFirebaseRepository, mockMovieRepository);
    }

    @Test
    public void testSearchMovies_SuccessfulResponse() {
        // Arrange
        String query = "batman";
        ListMovie fakeList = new ListMovie();
        List<MovieItem> results = new ArrayList<>();
        results.add(new MovieItem("Batman Begins", "Overview", "poster.jpg")); // Add appropriate constructor fields
        fakeList.setResults(results);

        doAnswer(invocation -> {
            Callback<ListMovie> callback = invocation.getArgument(1);
            callback.onResponse(null, Response.success(fakeList));
            return null;
        }).when(mockMovieRepository).searchMovies(eq(query), any());

        // Act
        viewModel.searchMovies(query);

        // Assert
        assertEquals(false, viewModel.getIsLoading().getValue());
        assertEquals(results, viewModel.getSearchResults().getValue());
        assertEquals(false, viewModel.getIsEmptyResult().getValue());
    }

    @Test
    public void testSearchMovies_EmptyResponse() {
        // Arrange
        String query = "random";
        ListMovie emptyList = new ListMovie();
        emptyList.setResults(Collections.emptyList());

        doAnswer(invocation -> {
            Callback<ListMovie> callback = invocation.getArgument(1);
            callback.onResponse(null, Response.success(emptyList));
            return null;
        }).when(mockMovieRepository).searchMovies(eq(query), any());

        // Act
        viewModel.searchMovies(query);

        // Assert
        assertEquals(true, viewModel.getIsEmptyResult().getValue());
        assertEquals(0, viewModel.getSearchResults().getValue().size());
    }

    @Test
    public void testSearchMovies_ApiFailure() {
        // Arrange
        String query = "something";

        doAnswer(invocation -> {
            Callback<ListMovie> callback = invocation.getArgument(1);
            callback.onFailure(null, new Throwable("Network Error"));
            return null;
        }).when(mockMovieRepository).searchMovies(eq(query), any());

        // Act
        viewModel.searchMovies(query);

        // Assert
        assertEquals(true, viewModel.getIsEmptyResult().getValue());
        assertEquals(0, viewModel.getSearchResults().getValue().size());
    }
}
