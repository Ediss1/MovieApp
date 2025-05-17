package com.example.movieapp.ui.adapters;

import android.content.Context;
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
    private Context context;

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
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favorite, parent, false);
        return new FavoriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {
        holder.titleText.setText(favoriteMovies.get(position).getTitle());
        Glide.with(holder.itemView.getContext())
                .load("https://image.tmdb.org/t/p/w500" + favoriteMovies.get(position).getPosterPath())
                .placeholder(R.drawable.button_background)
                .error(R.drawable.oval_btn_background)
                .into(holder.pic);

        // Set movie title
        if (favoriteMovies.get(position).getTitle() != null) {
            holder.titleText.setText(favoriteMovies.get(position).getTitle());
        } else {
            holder.titleText.setText("No title available");
        }

        // Set click listener
        holder.itemView.setOnClickListener(v -> listener.onItemClick(favoriteMovies.get(position).getId()));
    }

    @Override
    public int getItemCount() {
        return favoriteMovies.size();
    }

    static class FavoriteViewHolder extends RecyclerView.ViewHolder {
        TextView titleText;
        ImageView pic;

        public FavoriteViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.textView2); // Ensure this matches the XML ID
            pic = itemView.findViewById(R.id.favoritePoster);
        }
    }

    public void setMovies(List<MovieItem> movies) {
        this.favoriteMovies = movies;
        notifyDataSetChanged();
    }
}