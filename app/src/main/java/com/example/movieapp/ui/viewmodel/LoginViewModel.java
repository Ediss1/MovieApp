package com.example.movieapp.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.movieapp.data.repository.FirebaseRepository;
import com.google.firebase.auth.FirebaseUser;

public class LoginViewModel extends ViewModel {

    private final FirebaseRepository firebaseRepository = new FirebaseRepository();

    public LiveData<FirebaseUser> getUserLiveData() {
        return firebaseRepository.getUserLiveData();
    }

    public LiveData<String> getErrorMessage() {
        return firebaseRepository.getErrorMessage();
    }

    public void login(String email, String password) {
        firebaseRepository.login(email, password);
    }

    public FirebaseUser getCurrentUser() {
        return firebaseRepository.getCurrentUser();
    }
}