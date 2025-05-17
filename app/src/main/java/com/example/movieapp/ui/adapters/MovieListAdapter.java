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
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.movieapp.R;
import com.example.movieapp.data.model.MovieItem;

import java.util.List;


public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.ViewHolder> {

    private final List<MovieItem> items;
    private final OnMovieClickListener listener;
    private Context context;

    public interface OnMovieClickListener {
        void onMovieClick(int movieId);
    }

    public MovieListAdapter(List<MovieItem> items, OnMovieClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View inflate = LayoutInflater.from(context).inflate(R.layout.viewholder_film, parent, false);
        return new ViewHolder(inflate);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.titleText.setText(items.get(position).getTitle());
        Glide.with(context)
                .load("https://image.tmdb.org/t/p/w500" + items.get(position).getPosterPath())
                .apply(new RequestOptions().transform(new CenterCrop(), new RoundedCorners(30)))
                .into(holder.pic);
        holder.itemView.setOnClickListener(view -> listener.onMovieClick(items.get(position).getId()));

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleText;
        ImageView pic;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.titleTxt);
            pic = itemView.findViewById(R.id.pic);
        }
    }



}
