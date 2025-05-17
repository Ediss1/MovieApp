package com.example.movieapp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.example.movieapp.data.model.MovieItem;
import com.example.movieapp.data.repository.FirebaseRepository;
import com.example.movieapp.data.repository.MovieRepository;
import com.example.movieapp.ui.viewmodel.FavoritesViewModel;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@RunWith(MockitoJUnitRunner.class)
public class FavoritesViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    @Mock
    FirebaseRepository mockFirebaseRepository;

    @Mock(strictness = Mock.Strictness.LENIENT)
    MovieRepository mockMovieRepository;

    @Mock(strictness = Mock.Strictness.LENIENT)
    FirebaseUser mockUser;

    @Mock
    QuerySnapshot mockSnapshot;

    @Mock
    QueryDocumentSnapshot mockDoc1;

    @Mock
    Call<MovieItem> mockCall;

    private FavoritesViewModel viewModel;

    @Before
    public void setUp() {
        lenient().when(mockFirebaseRepository.getCurrentUser()).thenReturn(mockUser);
        lenient().when(mockUser.getUid()).thenReturn("testUserId");

        // Mock listenToFavorites
        doAnswer(invocation -> {
            EventListener<QuerySnapshot> listener = invocation.getArgument(1);
            List<QueryDocumentSnapshot> docs = Collections.singletonList(mockDoc1);
            when(mockSnapshot.iterator()).thenReturn(docs.iterator());
            when(mockDoc1.getLong("movieId")).thenReturn(123L);
            listener.onEvent(mockSnapshot, null); // FIXED: correct method call
            return null;
        }).when(mockFirebaseRepository).listenToFavorites(anyString(), any());

        // Create ViewModel after mocking
        viewModel = new FavoritesViewModel(mockFirebaseRepository, mockMovieRepository);
    }

    @Test
    public void testFavoriteMoviesFetched() {
        MovieItem dummyMovie = new MovieItem();
        dummyMovie.setId(123);

        // Simulate API call
        doAnswer(invocation -> {
            Callback<MovieItem> callback = invocation.getArgument(1);
            callback.onResponse(mockCall, Response.success(dummyMovie));
            return null;
        }).when(mockMovieRepository).fetchMovieDetails(eq(123), any());

        viewModel.getFavoriteMovies().observeForever(movies -> {
            assertNotNull(movies);
            assertEquals(1, movies.size());
            assertEquals(123, movies.get(0).getId());
        });
    }
}
