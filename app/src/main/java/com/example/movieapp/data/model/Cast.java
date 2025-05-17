package com.example.movieapp.data.model;

import com.google.gson.annotations.SerializedName;

public class Cast {

    @SerializedName("name")
    private String name;

    @SerializedName("profile_path")
    private String profilePath;

    public String getName() {
        return name;
    }

    public String getProfilePath() {
        return profilePath;
    }
}