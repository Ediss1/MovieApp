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
import com.example.movieapp.data.model.ListFilm;


public class FilmListAdapter extends RecyclerView.Adapter<FilmListAdapter.ViewHolder> {

    public interface OnMovieClickListener {
        void onMovieClick(int movieId);
    }

    private final ListFilm items;
    private final OnMovieClickListener listener;
    private Context context;

    public FilmListAdapter(ListFilm items, OnMovieClickListener listener) {
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
        holder.titleText.setText(items.getResults().get(position).getTitle());

        Glide.with(context)
                .load(items.getResults().get(position).getFullPosterUrl())
                .apply(new RequestOptions().transform(new CenterCrop(), new RoundedCorners(30)))
                .into(holder.pic);

        holder.itemView.setOnClickListener(view ->
                listener.onMovieClick(items.getResults().get(position).getId()));
    }

    @Override
    public int getItemCount() {
        return items.getResults().size();
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
