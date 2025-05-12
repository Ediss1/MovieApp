package com.example.movieapp.ui.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.example.movieapp.R;
import com.example.movieapp.data.model.ApiClient;
import com.example.movieapp.data.model.MovieItem;
import com.example.movieapp.data.model.TMDbApiService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.movieapp.BuildConfig;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MovieDetailsFragment extends Fragment {

    private RequestQueue mRequestQueue;
    private StringRequest mStringRequest;
    private ProgressBar progressBar;
    private TextView titleTxt, movieRate, movieTime, movieSummaryTxt, movieActorsTxt;
    private int idFilm;
    private ImageView pic2, backImg, addToFavorites;
    private RecyclerView.Adapter adapterActorList, adapterCategoryList;
    private RecyclerView recyclerViewActors, recyclerViewCategoryes;
    private ScrollView scrollView;
    private MovieItem item;
    private boolean isFavorite = false;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie_details, container, false);

        idFilm = getArguments() != null ? getArguments().getInt("movie_id", 0) : 0;

        titleTxt = view.findViewById(R.id.movieTitleTxt);
        progressBar = view.findViewById(R.id.progressBarDetail);
        scrollView = view.findViewById(R.id.scrollView2);
        pic2 = view.findViewById(R.id.picDetail);
        movieRate = view.findViewById(R.id.movieStar);
        movieTime = view.findViewById(R.id.movieTime);
        movieSummaryTxt = view.findViewById(R.id.movieSummary);
        movieActorsTxt = view.findViewById(R.id.movieActorInfo);
        backImg = view.findViewById(R.id.backImg);
        recyclerViewCategoryes = view.findViewById(R.id.genreView);
        recyclerViewActors = view.findViewById(R.id.imagesRecycler);
        addToFavorites = view.findViewById(R.id.favImg);
        recyclerViewActors.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewCategoryes.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        sendRequest(idFilm);
        checkIfFavorite();

        addToFavorites.setOnClickListener(v -> toggleFavoriteStatus());
        backImg.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        return view;
    }

    private void sendRequest(int movieId) {
        TMDbApiService apiService = ApiClient.getClient().create(TMDbApiService.class);

        Call<MovieItem> call = apiService.getMovieDetails(movieId, BuildConfig.TMDB_API_KEY);

        call.enqueue(new Callback<MovieItem>() {
            @Override
            public void onResponse(Call<MovieItem> call, Response<MovieItem> response) {
                if (response.isSuccessful() && response.body() != null) {
                    item = response.body();
                    // Update UI elements with 'item' data
                    titleTxt.setText(item.getTitle());
                    movieSummaryTxt.setText(item.getOverview());
                    movieRate.setText(String.valueOf(item.getVoteAverage()) + "/10");
                    movieTime.setText(item.getRuntime() + " min");
                    Glide.with(requireContext())
                            .load("https://image.tmdb.org/t/p/w500" + item.getPosterPath())
                            .into(pic2);
                    progressBar.setVisibility(View.GONE);
                } else {
                    Toast.makeText(getContext(), "Movie not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MovieItem> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addToFavorites() {
        if (item == null || auth.getCurrentUser() == null) return;

        String userId = auth.getCurrentUser().getUid();
        String movieId = String.valueOf(item.getId());

        DocumentReference movieRef = db.collection("users")
                .document(userId)
                .collection("favorites")
                .document(movieId);  // Document ID is the movie ID

        Map<String, Object> data = new HashMap<>();
        data.put("movieId", item.getId());

        movieRef.set(data)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Added to favorites", Toast.LENGTH_SHORT).show();
                    isFavorite = true;
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to add to favorites", Toast.LENGTH_SHORT).show();
                });
    }



    private void removeFromFavorites() {
        if (item == null || auth.getCurrentUser() == null) return;

        String userId = auth.getCurrentUser().getUid();
        String movieId = String.valueOf(item.getId());

        DocumentReference movieRef = db.collection("users")
                .document(userId)
                .collection("favorites")
                .document(movieId);

        movieRef.delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Removed from favorites", Toast.LENGTH_SHORT).show();
                    isFavorite = false;
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to remove from favorites", Toast.LENGTH_SHORT).show();
                });
    }


    private void checkIfFavorite() {
        if (auth.getCurrentUser() == null) return;

        String userId = auth.getCurrentUser().getUid();
        String movieId = String.valueOf(idFilm);

        DocumentReference movieRef = db.collection("users")
                .document(userId)
                .collection("favorites")
                .document(movieId);

        movieRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        isFavorite = true;
                        addToFavorites.setImageResource(R.drawable.fav); // filled icon
                    } else {
                        isFavorite = false;
                        addToFavorites.setImageResource(R.drawable.btn_2); // empty icon
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to check if favorite", Toast.LENGTH_SHORT).show();
                });
    }


    private void toggleFavoriteStatus() {
        if (item == null || auth.getCurrentUser() == null) return;

        if (isFavorite) {
            removeFromFavorites();
            checkIfFavorite();
        } else {
            addToFavorites();
            checkIfFavorite();
        }
    }



}