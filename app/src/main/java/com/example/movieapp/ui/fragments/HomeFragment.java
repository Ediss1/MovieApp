package com.example.movieapp.ui.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.example.movieapp.R;
import com.example.movieapp.data.model.GenresItem;
import com.example.movieapp.data.model.ListFilm;
import com.example.movieapp.ui.adapters.CategoryListAdapter;
import com.example.movieapp.ui.adapters.FilmListAdapter;
import com.example.movieapp.ui.adapters.SliderAdapters;
import com.example.movieapp.ui.viewmodel.HomeViewModel;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    private ViewPager2 viewPager2;
    private Handler slideHandler = new Handler();
    private RecyclerView recyclerViewBestMovies, recyclerViewUpcoming, recyclerViewCategory;
    private ProgressBar loading1, loading2, loading3;
    private EditText searchEditText;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        viewPager2 = rootView.findViewById(R.id.viewpagerSlider);

        recyclerViewBestMovies = rootView.findViewById(R.id.view1);
        recyclerViewBestMovies.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        recyclerViewUpcoming = rootView.findViewById(R.id.view3);
        recyclerViewUpcoming.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        recyclerViewCategory = rootView.findViewById(R.id.view2);
        recyclerViewCategory.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        loading1 = rootView.findViewById(R.id.progressBar1);
        loading2 = rootView.findViewById(R.id.progressBar2);
        loading3 = rootView.findViewById(R.id.progressBar3);

        searchEditText = rootView.findViewById(R.id.searchEditText);

        observeData();

        return rootView;
    }

    private void observeData() {
        // Slider
        homeViewModel.getSliderItems().observe(getViewLifecycleOwner(), sliderItems -> {
            viewPager2.setAdapter(new SliderAdapters(sliderItems, viewPager2));
            viewPager2.setClipToPadding(false);
            viewPager2.setClipChildren(false);
            viewPager2.setOffscreenPageLimit(3);
            viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

            CompositePageTransformer compositeTransformer = new CompositePageTransformer();
            compositeTransformer.addTransformer(new MarginPageTransformer(40));
            compositeTransformer.addTransformer((page, position) -> {
                float r = 1 - Math.abs(position);
                page.setScaleY(0.85f + r * 0.15f);
            });

            viewPager2.setPageTransformer(compositeTransformer);
            viewPager2.setCurrentItem(1);

            viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);
                    slideHandler.removeCallbacks(sliderRunnable);
                    slideHandler.postDelayed(sliderRunnable, 3000);
                }
            });

            slideHandler.postDelayed(sliderRunnable, 3000);
        });

        // Best movies
        loading1.setVisibility(View.VISIBLE);
        homeViewModel.getBestMovies().observe(getViewLifecycleOwner(), listFilm -> {
            loading1.setVisibility(View.GONE);
            if (listFilm != null && listFilm.getResults() != null) {
                recyclerViewBestMovies.setAdapter(new FilmListAdapter(listFilm, this::openMovieDetails));
            } else {
                Toast.makeText(getContext(), "Failed to load best movies", Toast.LENGTH_SHORT).show();
            }
        });

        // Categories
        loading2.setVisibility(View.VISIBLE);
        homeViewModel.getCategories().observe(getViewLifecycleOwner(), genresItems -> {
            loading2.setVisibility(View.GONE);
            if (genresItems != null) {
                recyclerViewCategory.setAdapter(new CategoryListAdapter((ArrayList<GenresItem>) genresItems));
            } else {
                Toast.makeText(getContext(), "Failed to load categories", Toast.LENGTH_SHORT).show();
            }
        });

        // Upcoming movies
        loading3.setVisibility(View.VISIBLE);
        homeViewModel.getUpcomingMovies().observe(getViewLifecycleOwner(), listFilm -> {
            loading3.setVisibility(View.GONE);
            if (listFilm != null && listFilm.getResults() != null) {
                recyclerViewUpcoming.setAdapter(new FilmListAdapter(listFilm, this::openMovieDetails));
            } else {
                Toast.makeText(getContext(), "Failed to load upcoming movies", Toast.LENGTH_SHORT).show();
            }
        });

        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            String query = v.getText().toString().trim();
            if (!query.isEmpty()) {
                homeViewModel.searchMovies(query).observe(getViewLifecycleOwner(), result -> {
                    if (result != null && result.getResults() != null) {
                        recyclerViewBestMovies.setAdapter(new FilmListAdapter(result, this::openMovieDetails));
                    } else {
                        Toast.makeText(getContext(), "No results found", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            return true;
        });
    }

    private final Runnable sliderRunnable = () -> {
        if (viewPager2 != null) {
            viewPager2.setCurrentItem(viewPager2.getCurrentItem() + 1);
            slideHandler.postDelayed(this.sliderRunnable, 3000);
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        slideHandler.removeCallbacks(sliderRunnable);
    }

    @Override
    public void onResume() {
        super.onResume();
        slideHandler.postDelayed(sliderRunnable, 3000);
    }

    private void openMovieDetails(int movieId) {

        Log.d("HomeFragment", "Clicked movie ID: " + movieId);

        Bundle bundle = new Bundle();
        bundle.putInt("movie_id", movieId);

        MovieDetailsFragment fragment = new MovieDetailsFragment();
        fragment.setArguments(bundle);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment) // Make sure this ID matches your FrameLayout
                .addToBackStack(null)
                .commit();
    }


}
