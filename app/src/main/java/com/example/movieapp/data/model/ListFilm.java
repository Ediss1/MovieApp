package com.example.movieapp.data.model;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ListFilm {
    private int page;
    private List<Film> results;
    private int total_pages;
    private int total_results;

    public int getPage() { return page; }
    public List<Film> getResults() { return results; }
    public int getTotalPages() { return total_pages; }
    public int getTotalResults() { return total_results; }
}

