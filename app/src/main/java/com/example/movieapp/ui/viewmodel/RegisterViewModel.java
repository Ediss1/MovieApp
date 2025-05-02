package com.example.movieapp.ui.viewmodel;

import android.text.TextUtils;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;


public class RegisterViewModel extends ViewModel {

    private FirebaseAuth mAuth;
    private MutableLiveData<Boolean> registrationSuccess;
    private MutableLiveData<String> errorMessage;
    private MutableLiveData<Boolean> isLoading;

    public RegisterViewModel() {
        mAuth = FirebaseAuth.getInstance();
        registrationSuccess = new MutableLiveData<>();
        errorMessage = new MutableLiveData<>();
        isLoading = new MutableLiveData<>();
    }

    public LiveData<Boolean> getRegistrationSuccess() {
        return registrationSuccess;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void registerUser(String email, String password, String confirmPassword) {
        isLoading.setValue(true);

        // Validate input fields
        if (TextUtils.isEmpty(email)) {
            errorMessage.setValue("Enter email");
            isLoading.setValue(false);
            return;
        }

        if (TextUtils.isEmpty(password)) {
            errorMessage.setValue("Enter password");
            isLoading.setValue(false);
            return;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            errorMessage.setValue("Confirm your password");
            isLoading.setValue(false);
            return;
        }

        if (!password.equals(confirmPassword)) {
            errorMessage.setValue("Passwords do not match");
            isLoading.setValue(false);
            return;
        }

        // Create user with Firebase Auth
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    isLoading.setValue(false);
                    if (task.isSuccessful()) {
                        registrationSuccess.setValue(true);
                    } else {
                        errorMessage.setValue("Registration failed: " + task.getException().getMessage());
                    }
                });
    }
}
