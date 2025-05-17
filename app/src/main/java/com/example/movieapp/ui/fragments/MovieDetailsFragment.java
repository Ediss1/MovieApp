package com.example.movieapp.ui.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.movieapp.R;
import com.example.movieapp.data.model.MovieItem;
import com.example.movieapp.ui.adapters.CategoryEachMovieAdapter;
import com.example.movieapp.ui.viewmodel.MovieDetailsViewModel;


import java.util.ArrayList;
import java.util.List;


public class MovieDetailsFragment extends Fragment {

    private int idFilm;
    private ProgressBar progressBar;
    private TextView titleTxt, movieRate, movieTime, movieSummaryTxt, movieActorsTxt;
    private ImageView pic2, backImg, addToFavorites;
    private RecyclerView genresRecyclerView;
    private CategoryEachMovieAdapter genresAdapter;
    private List<MovieItem.Genre> genres;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie_details, container, false);

        MovieDetailsViewModel viewModel = new ViewModelProvider(this).get(MovieDetailsViewModel.class);

        titleTxt = view.findViewById(R.id.movieTitleTxt);
        movieSummaryTxt = view.findViewById(R.id.movieSummary);
        movieActorsTxt = view.findViewById(R.id.movieActorInfo);
        movieRate = view.findViewById(R.id.movieStar);
        movieTime = view.findViewById(R.id.movieTime);
        pic2 = view.findViewById(R.id.picDetail);
        progressBar = view.findViewById(R.id.progressBarDetail);

        genresRecyclerView = view.findViewById(R.id.genresRecyclerView);
        genresRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        addToFavorites = view.findViewById(R.id.favImg);
        backImg = view.findViewById(R.id.backImg);


        addToFavorites.setOnClickListener(v -> {
            viewModel.toggleFavoriteStatus(viewModel.getMovieItem().getValue());
        });


        backImg.setOnClickListener(v -> {
            requireActivity().onBackPressed();
        });

        viewModel.getMovieItem().observe(getViewLifecycleOwner(), item -> {
            if (item != null) {
                titleTxt.setText(item.getTitle());
                movieSummaryTxt.setText(item.getOverview());
                movieRate.setText(item.getVoteAverage() + "/10");
                movieTime.setText(item.getRuntime() + " min");
                Glide.with(requireContext())
                        .load("https://image.tmdb.org/t/p/w500" + item.getPosterPath())
                        .into(pic2);
                progressBar.setVisibility(View.GONE);
            } else {
                Toast.makeText(getContext(), "Failed to load movie details", Toast.LENGTH_SHORT).show();
            }
            genres = item.getGenres();
            if (genres != null && !genres.isEmpty()) {
                List<String> genreNames = new ArrayList<>();
                for (MovieItem.Genre genre : genres) {
                    genreNames.add(genre.getName());
                }

                genresAdapter = new CategoryEachMovieAdapter(genreNames);
                genresRecyclerView.setAdapter(genresAdapter);
            }
        });

        viewModel.getIsFavorite().observe(getViewLifecycleOwner(), favorite -> {
            addToFavorites.setImageResource(favorite ? R.drawable.fav : R.drawable.btn_2);
        });

        if (getArguments() != null) {
            MovieDetailsFragmentArgs args = MovieDetailsFragmentArgs.fromBundle(getArguments());
            idFilm = args.getMovieId();
        }

        viewModel.getCastList().observe(getViewLifecycleOwner(), castList -> {
            if (castList != null) {
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < Math.min(castList.size(), 5); i++) {
                    if (i != 4) {
                        builder.append(castList.get(i).getName()).append(", ");
                    } else {
                        builder.append(castList.get(i).getName()).append("\n");
                    }
                }
                movieActorsTxt.setText(builder.toString());
            } else {
                movieActorsTxt.setText("No actors found.");
            }
        });

        viewModel.fetchMovieDetails(idFilm);
        viewModel.fetchMovieCredits(idFilm);
        viewModel.checkIfFavorite(idFilm);

        return view;
    }

}