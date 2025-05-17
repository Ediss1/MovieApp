package com.example.movieapp.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movieapp.R;

import java.util.List;

public class CategoryEachMovieAdapter extends RecyclerView.Adapter<CategoryEachMovieAdapter.ViewHolder> {
    List<String> items;
    Context context;

    public CategoryEachMovieAdapter(List<String> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public CategoryEachMovieAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_category, parent, false);
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryEachMovieAdapter.ViewHolder holder, int position) {
        holder.titleText.setText(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.TitleTxt);
        }
    }
}
