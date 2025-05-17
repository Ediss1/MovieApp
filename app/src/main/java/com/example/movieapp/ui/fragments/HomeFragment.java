package com.example.movieapp.ui.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.example.movieapp.R;
import com.example.movieapp.data.model.SliderItems;
import com.example.movieapp.data.repository.FirebaseRepository;
import com.example.movieapp.data.repository.MovieRepository;
import com.example.movieapp.ui.adapters.MovieListAdapter;
import com.example.movieapp.ui.adapters.SliderAdapters;
import com.example.movieapp.ui.viewmodel.HomeViewModel;
import com.example.movieapp.ui.viewmodel.factory.ViewModelFactory;

import java.util.List;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    private ViewPager2 viewPager2;
    private Handler slideHandler = new Handler();
    private RecyclerView recyclerViewBestMovies, recyclerViewUpcoming, recyclerViewTopRatedMovies;
    private ProgressBar loading1, loading2, loading3;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        ViewModelFactory factory = new ViewModelFactory(
                requireActivity().getApplication(),
                new MovieRepository(),
                new FirebaseRepository()
        );

        homeViewModel = new ViewModelProvider(this, factory).get(HomeViewModel.class);


        viewPager2 = rootView.findViewById(R.id.viewpagerSlider);

        recyclerViewBestMovies = rootView.findViewById(R.id.view1);
        recyclerViewBestMovies.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        recyclerViewUpcoming = rootView.findViewById(R.id.view3);
        recyclerViewUpcoming.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        recyclerViewTopRatedMovies = rootView.findViewById(R.id.view2);
        recyclerViewTopRatedMovies.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        loading1 = rootView.findViewById(R.id.progressBar1);
        loading2 = rootView.findViewById(R.id.progressBar2);
        loading3 = rootView.findViewById(R.id.progressBar3);

        observeData();

        return rootView;
    }

    private void observeData() {
        // Observe slider items
        homeViewModel.getSliderItems().observe(getViewLifecycleOwner(), this::setupSlider);

        // Best movies
        homeViewModel.getIsLoadingBestMovies().observe(getViewLifecycleOwner(), isLoading -> {
            loading1.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        homeViewModel.getBestMovies().observe(getViewLifecycleOwner(), listFilm -> {
            if (listFilm != null) {
                recyclerViewBestMovies.setAdapter(new MovieListAdapter(listFilm, this::openMovieDetails));
            } else {
                Toast.makeText(getContext(), "Failed to load best movies", Toast.LENGTH_SHORT).show();
            }
        });

        //Top Rated movies
        homeViewModel.getIsLoadingTopRatedMovies().observe(getViewLifecycleOwner(), isLoading -> {
            loading2.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        homeViewModel.getTopRatedMovies().observe(getViewLifecycleOwner(), listFilm -> {
            if (listFilm != null) {
                recyclerViewTopRatedMovies.setAdapter(new MovieListAdapter(listFilm, this::openMovieDetails));
            } else {
                Toast.makeText(getContext(), "Failed to load best movies", Toast.LENGTH_SHORT).show();
            }
        });

        // Upcoming movies
        homeViewModel.getIsLoadingUpcomingMovies().observe(getViewLifecycleOwner(), isLoading -> {
            loading3.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        homeViewModel.getUpcomingMovies().observe(getViewLifecycleOwner(), listFilm -> {
            if (listFilm != null) {
                recyclerViewUpcoming.setAdapter(new MovieListAdapter(listFilm, this::openMovieDetails));
            } else {
                Toast.makeText(getContext(), "Failed to load upcoming movies", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupSlider(List<SliderItems> sliderItems) {
        viewPager2.setAdapter(new SliderAdapters(sliderItems, viewPager2));
        viewPager2.setClipToPadding(false);
        viewPager2.setClipChildren(false);
        viewPager2.setOffscreenPageLimit(3);
        viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        CompositePageTransformer transformer = new CompositePageTransformer();
        transformer.addTransformer(new MarginPageTransformer(40));
        transformer.addTransformer((page, position) -> {
            float r = 1 - Math.abs(position);
            page.setScaleY(0.85f + r * 0.15f);
        });
        viewPager2.setPageTransformer(transformer);

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
    }


    private final Runnable sliderRunnable = new Runnable() {
        @Override
        public void run() {
            if (viewPager2 != null && isAdded()) {
                viewPager2.setCurrentItem(viewPager2.getCurrentItem() + 1);
                slideHandler.postDelayed(this, 3000);
            }
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
        NavDirections action = HomeFragmentDirections.actionHomeFragmentToMovieDetailsFragment(movieId);
        NavHostFragment.findNavController(HomeFragment.this).navigate(action);
    }


}
