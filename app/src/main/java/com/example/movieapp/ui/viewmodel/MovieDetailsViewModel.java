package com.example.movieapp.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.movieapp.data.model.Cast;
import com.example.movieapp.data.model.CreditsResponse;
import com.example.movieapp.data.model.MovieItem;
import com.example.movieapp.data.repository.MovieRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieDetailsViewModel extends ViewModel {
    private final MutableLiveData<MovieItem> movieItem = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isFavorite = new MutableLiveData<>();
    private final MovieRepository repository = new MovieRepository();
    private final MutableLiveData<List<Cast>> castList = new MutableLiveData<>();

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth auth = FirebaseAuth.getInstance();

    public LiveData<MovieItem> getMovieItem() {
        return movieItem;
    }

    public LiveData<Boolean> getIsFavorite() {
        return isFavorite;
    }

    public LiveData<List<Cast>> getCastList() {
        return castList;
    }

    public void fetchMovieDetails(int movieId) {
        repository.fetchMovieDetails(movieId, new Callback<MovieItem>() {
            @Override
            public void onResponse(Call<MovieItem> call, Response<MovieItem> response) {
                if (response.isSuccessful() && response.body() != null) {
                    movieItem.postValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<MovieItem> call, Throwable t) {
                movieItem.postValue(null);
            }
        });
    }

    public void fetchMovieCredits(int movieId) {
        repository.fetchMovieCredits(movieId, new Callback<CreditsResponse>() {
            @Override
            public void onResponse(Call<CreditsResponse> call, Response<CreditsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    castList.postValue(response.body().getCast());
                }
            }

            @Override
            public void onFailure(Call<CreditsResponse> call, Throwable t) {
                castList.postValue(null);
            }
        });
    }

    public void checkIfFavorite(int movieId) {
        if (auth.getCurrentUser() == null) return;

        String userId = auth.getCurrentUser().getUid();

        db.collection("users").document(userId)
                .collection("favorites").document(String.valueOf(movieId))
                .get()
                .addOnSuccessListener(doc -> isFavorite.setValue(doc.exists()))
                .addOnFailureListener(e -> isFavorite.setValue(false));
    }

    public void toggleFavoriteStatus(MovieItem item) {
        if (item == null || auth.getCurrentUser() == null) return;

        String userId = auth.getCurrentUser().getUid();
        String movieId = String.valueOf(item.getId());

        DocumentReference movieRef = db.collection("users")
                .document(userId)
                .collection("favorites")
                .document(movieId);

        if (Boolean.TRUE.equals(isFavorite.getValue())) {
            movieRef.delete()
                    .addOnSuccessListener(aVoid -> isFavorite.setValue(false));
        } else {
            Map<String, Object> data = new HashMap<>();
            data.put("movieId", item.getId());

            movieRef.set(data)
                    .addOnSuccessListener(aVoid -> isFavorite.setValue(true));
        }
    }
}
