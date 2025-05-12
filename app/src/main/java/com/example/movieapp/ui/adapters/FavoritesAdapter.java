package com.example.movieapp.ui.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.movieapp.R;
import com.example.movieapp.data.model.MovieItem;

import java.util.List;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.FavoriteViewHolder> {

    private List<MovieItem> favoriteMovies;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int movieId);
    }

    public FavoritesAdapter(List<MovieItem> favoriteMovies, OnItemClickListener listener) {
        this.favoriteMovies = favoriteMovies;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favorite, parent, false);
        return new FavoriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {
        MovieItem movie = favoriteMovies.get(position);

        // Set movie poster
        if (movie.getPosterPath() != null) {
            Glide.with(holder.itemView.getContext())
                    .load("https://image.tmdb.org/t/p/w500" + movie.getPosterPath())
                    .placeholder(R.drawable.orange_button_background)
                    .error(R.drawable.oval_btn_background)
                    .into(holder.poster);
        } else {
            holder.poster.setImageResource(R.drawable.oval_btn_background);
        }

        // Set movie title
        if (movie.getTitle() != null) {
            holder.title.setText(movie.getTitle());
        } else {
            holder.title.setText("No title available");
        }

        // Set click listener
        holder.itemView.setOnClickListener(v -> listener.onItemClick(movie.getId()));
    }

    @Override
    public int getItemCount() {
        return favoriteMovies.size();
    }

    // Method to add movies to the list efficiently
    public void addMovie(MovieItem movie) {
        if (movie != null) {
            favoriteMovies.add(movie);
            notifyItemInserted(favoriteMovies.size() - 1);
            Log.d("FavoritesAdapter", "Added movie: " + movie.getTitle());
        }
    }


    static class FavoriteViewHolder extends RecyclerView.ViewHolder {
        ImageView poster;
        TextView title;

        public FavoriteViewHolder(@NonNull View itemView) {
            super(itemView);
            poster = itemView.findViewById(R.id.favoritePoster);
            title = itemView.findViewById(R.id.textView2); // Ensure this matches the XML ID
        }
    }
}