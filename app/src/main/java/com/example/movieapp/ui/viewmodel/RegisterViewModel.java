package com.example.movieapp.ui.viewmodel;

import android.text.TextUtils;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.movieapp.data.repository.FirebaseRepository;
import com.google.firebase.auth.FirebaseAuth;


public class RegisterViewModel extends ViewModel {

    private final FirebaseRepository firebaseRepository = new FirebaseRepository();

    public LiveData<Boolean> getRegistrationSuccess() {
        return firebaseRepository.getRegistrationSuccess();
    }

    public LiveData<String> getErrorMessage() {
        return firebaseRepository.getErrorMessage();
    }

    public void registerUser(String email, String password, String confitmPassword){
        firebaseRepository.registerUser(email, password, confitmPassword);
    }

    public LiveData<Boolean> getIsLoading() {
        return firebaseRepository.getIsLoading();
    }


}
