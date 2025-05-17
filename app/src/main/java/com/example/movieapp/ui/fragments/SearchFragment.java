package com.example.movieapp.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movieapp.R;
import com.example.movieapp.data.repository.FirebaseRepository;
import com.example.movieapp.data.repository.MovieRepository;
import com.example.movieapp.ui.adapters.MovieListAdapter;
import com.example.movieapp.ui.viewmodel.FavoritesViewModel;
import com.example.movieapp.ui.viewmodel.SearchViewModel;
import com.example.movieapp.ui.viewmodel.factory.ViewModelFactory;

public class SearchFragment extends Fragment {

    private SearchViewModel viewModel;
    private RecyclerView recyclerView;
    private ImageButton searchButton;
    private AutoCompleteTextView searchInput;
    private ArrayAdapter<String> historyAdapter;
    private ProgressBar progressBar;
    private TextView noResultsText;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        searchInput = view.findViewById(R.id.search_input);
        searchButton = view.findViewById(R.id.search_button);
        recyclerView = view.findViewById(R.id.searchResultsRecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        progressBar = view.findViewById(R.id.progressBarSearch);
        noResultsText = view.findViewById(R.id.noResultsText);

        ViewModelFactory factory = new ViewModelFactory(
                requireActivity().getApplication(),
                new MovieRepository(),
                new FirebaseRepository()
        );

        viewModel = new ViewModelProvider(this, factory).get(SearchViewModel.class);

        viewModel.getSearchHistory().observe(getViewLifecycleOwner(), history -> {
            historyAdapter = new ArrayAdapter<>(
                    requireContext(),
                    R.layout.autocomplete_item_layout,
                    R.id.autoCompleteItem,
                    history
            );
            searchInput.setAdapter(historyAdapter);
        });

        searchInput.setOnClickListener(v -> {
            if (historyAdapter != null && !historyAdapter.isEmpty()) {
                searchInput.showDropDown();
            }
        });

        searchInput.setOnItemClickListener((parent, view1, position, id) -> {
            String selectedTerm = parent.getItemAtPosition(position).toString();
            searchInput.setText(selectedTerm);
            viewModel.searchMovies(selectedTerm);
        });

        searchInput.setOnEditorActionListener((v, actionId, event) -> {
            String query = searchInput.getText().toString().trim();
            if (!query.isEmpty()) {
                viewModel.saveSearchTerm(query);
                viewModel.searchMovies(query);
            }
            return false;
        });

        searchButton.setOnClickListener(v -> {
            String query = searchInput.getText().toString().trim();
            if (!query.isEmpty()) {
                viewModel.saveSearchTerm(query);
                viewModel.searchMovies(query);
            }
        });

        viewModel.getIsEmptyResult().observe(getViewLifecycleOwner(), isEmpty -> {
            if (isEmpty) {
                Toast.makeText(requireContext(), "No movies found", Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });



        viewModel.getIsEmptyResult().observe(getViewLifecycleOwner(), isEmpty -> {
            if (isEmpty) {
                noResultsText.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                noResultsText.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        });


        viewModel.loadSearchHistory();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel.clearSearchResults();

        viewModel.getSearchResults().observe(getViewLifecycleOwner(), listMovie -> {
            if (listMovie != null) {
                recyclerView.setAdapter(new MovieListAdapter(listMovie, this::openMovieDetails));
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        searchInput.setText(""); // Clear input every time fragment becomes visible
    }

    private void openMovieDetails(int movieId) {
        Log.d("SearchFragment", "Clicked movie ID: " + movieId);
        NavDirections action = SearchFragmentDirections.actionSearchResultsFragmentToMovieDetailsFragment(movieId);
        NavHostFragment.findNavController(SearchFragment.this).navigate(action);
    }
}
