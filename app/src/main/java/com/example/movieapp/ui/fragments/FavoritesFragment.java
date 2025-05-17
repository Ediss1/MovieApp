package com.example.movieapp.ui.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.movieapp.R;
import com.example.movieapp.data.repository.FirebaseRepository;
import com.example.movieapp.data.repository.MovieRepository;
import com.example.movieapp.ui.adapters.FavoritesAdapter;
import com.example.movieapp.ui.viewmodel.FavoritesViewModel;
import com.example.movieapp.ui.viewmodel.factory.ViewModelFactory;


public class FavoritesFragment extends Fragment {

    private RecyclerView recyclerView;
    private FavoritesViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        recyclerView = view.findViewById(R.id.favoritesRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        ViewModelFactory factory = new ViewModelFactory(
                requireActivity().getApplication(),
                new MovieRepository(),
                new FirebaseRepository()
        );

        viewModel = new ViewModelProvider(this, factory).get(FavoritesViewModel.class);


        viewModel.getFavoriteMovies().observe(getViewLifecycleOwner(), movieItems -> {
            if (movieItems != null){
                recyclerView.setAdapter(new FavoritesAdapter(movieItems, this::openMovieDetails));
            } else {
                Toast.makeText(getContext(), "Failed to load favorite movies", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
    private void openMovieDetails(int movieId) {
        Log.d("Favorites fragment", "Clicked movie ID: " + movieId);
        NavDirections action = FavoritesFragmentDirections.actionFavoritesFragmentToMovieDetailsFragment(movieId);
        NavHostFragment.findNavController(FavoritesFragment.this).navigate(action);
    }
}


