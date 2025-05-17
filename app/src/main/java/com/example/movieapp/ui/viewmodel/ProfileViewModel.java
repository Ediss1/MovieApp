package com.example.movieapp.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.movieapp.data.repository.FirebaseRepository;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.AuthCredential;

public class ProfileViewModel extends ViewModel {

    private final FirebaseRepository firebaseRepository = new FirebaseRepository();

    public LiveData<String> getStatusMessage() {
        return firebaseRepository.getErrorMessage();
    }

    public FirebaseUser getCurrentUser() {
        return firebaseRepository.getCurrentUser();
    }

    public void changePassword(String currentPassword, String newPassword){
        firebaseRepository.changePassword(currentPassword, newPassword);
    }

    public void signOut(){
        firebaseRepository.signOut();
    }
}
