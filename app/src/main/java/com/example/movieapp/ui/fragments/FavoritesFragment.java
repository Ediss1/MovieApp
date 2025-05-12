package com.example.movieapp.ui.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.movieapp.BuildConfig;
import com.example.movieapp.R;
import com.example.movieapp.data.model.MovieItem;
import com.example.movieapp.data.model.TMDbApiService;
import com.example.movieapp.ui.adapters.FavoritesAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;



public class FavoritesFragment extends Fragment {
    List<MovieItem> favoriteList = new ArrayList<>();
    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FavoritesAdapter adapter;
    private RecyclerView recyclerView;

    public FavoritesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        recyclerView = view.findViewById(R.id.favoritesRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize the adapter with an empty list
        adapter = new FavoritesAdapter(favoriteList, movieId -> {
            // Handle movie click to navigate to MovieDetailsFragment
            Bundle bundle = new Bundle();
            bundle.putInt("movie_id", movieId);
            MovieDetailsFragment fragment = new MovieDetailsFragment();
            fragment.setArguments(bundle);

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        recyclerView.setAdapter(adapter);

        // Load favorite IDs from Firestore and fetch movie details from TMDb API
        loadFavoriteMovies();

        return view;
    }

    private void loadFavoriteMovies() {

        Log.d("FavoritesFragment", "Current userId: " + userId);

        // Fetch favorite movie IDs from Firestore
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .collection("favorites")
                .get()
                .addOnSuccessListener(query -> {
                    // List to store movieIds temporarily
                    List<Long> movieIds = new ArrayList<>();
                    Log.d("FavoritesFragment", "Fetched movie IDs: " + movieIds);

                    for (DocumentSnapshot doc : query) {
                        Long idLong = doc.getLong("movieId");
                        if (idLong != null) {
                            movieIds.add(idLong);
                        }
                    }

                    // Now fetch movie details for each movieId
                    fetchMovieDetails(movieIds);
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error fetching favorites: ", e));
    }

    private void fetchMovieDetails(List<Long> movieIds) {
        // Retrofit setup
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.themoviedb.org/3/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        TMDbApiService apiService = retrofit.create(TMDbApiService.class);

        // Loop through each movieId and fetch movie details
        for (Long movieId : movieIds) {
            apiService.getMovieDetails(movieId.intValue(), BuildConfig.TMDB_API_KEY)
                    .enqueue(new Callback<MovieItem>() {
                        @Override
                        public void onResponse(Call<MovieItem> call, Response<MovieItem> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                Log.d("FavoritesFragment", "Fetched movie: " + response.body().getTitle());
                                // Add movie to the list
                                // favoriteList.add(response.body());
                                // Notify adapter to update the view
                                adapter.addMovie(response.body());
                            }
                        }

                        @Override
                        public void onFailure(Call<MovieItem> call, Throwable t) {
                            Log.e("TMDb", "API error: ", t);
                        }
                    });
        }
    }
}
