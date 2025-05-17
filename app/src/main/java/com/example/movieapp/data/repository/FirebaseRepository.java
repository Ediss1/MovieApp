package com.example.movieapp.data.repository;

import android.text.TextUtils;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseRepository {
    private final FirebaseAuth auth;
    private final FirebaseFirestore db;

    public FirebaseRepository(){
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    private final MutableLiveData<FirebaseUser> userLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> registrationSuccess = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    public static interface FavoritesListener {
        void onFavoritesChanged(QuerySnapshot snapshot, FirebaseFirestoreException error);
    }


    public LiveData<FirebaseUser> getUserLiveData() {
        return userLiveData;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public MutableLiveData<Boolean> getRegistrationSuccess() {
        return registrationSuccess;
    }

    public MutableLiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void registerUser(String email, String password, String confirmPassword){
        isLoading.setValue(true);

        if (TextUtils.isEmpty(email)){
            errorMessage.setValue("Enter email");
            isLoading.setValue(false);
            return;
        }

        if (TextUtils.isEmpty(password)){
            errorMessage.setValue("Enter password");
            isLoading.setValue(false);
            return;
        }

        if (TextUtils.isEmpty(confirmPassword)){
            errorMessage.setValue("Confirm your password");
            isLoading.setValue(false);
            return;
        }

        if (!password.equals(confirmPassword)){
            errorMessage.setValue("Passwords do not match !");
            isLoading.setValue(false);
            return;
        }

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    isLoading.setValue(false);
                    if (task.isSuccessful()){
                        registrationSuccess.setValue(true);
                    } else {
                        errorMessage.setValue("Registration failed");
                    }
                });
    }

    public void login(String email, String password) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        userLiveData.setValue(auth.getCurrentUser());
                    } else {
                        errorMessage.setValue("Authentication failed: " +
                                (task.getException() != null ? task.getException().getMessage() : ""));
                    }
                });
    }

    public void signOut(){
        auth.signOut();
    }

    public void changePassword(String currentPassword, String newPassword) {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null && user.getEmail() != null) {
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);

            user.reauthenticate(credential).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    user.updatePassword(newPassword).addOnCompleteListener(updateTask -> {
                        if (updateTask.isSuccessful()) {
                            errorMessage.setValue("Password changed successfully");
                        } else {
                            errorMessage.setValue("Failed to change password");
                        }
                    });
                } else {
                    errorMessage.setValue("Re-authentication failed. Check current password.");
                }
            });
        } else {
            errorMessage.setValue("No authenticated user found");
        }
    }

    public void listenToFavorites(String userId, EventListener<QuerySnapshot> listener) {
        db.collection("users")
                .document(userId)
                .collection("favorites")
                .addSnapshotListener(listener);
    }

    public void loadSearchHistory(String userId, OnSuccessListener<List<String>> listener) {
        db.collection("users")
                .document(userId)
                .collection("search_history")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(5)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<String> history = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        String term = doc.getString("term");
                        if (term != null) {
                            history.add(term);
                        }
                    }
                    listener.onSuccess(history);
                });
    }

    public void saveSearchTerm(String userId, String term) {
        Map<String, Object> data = new HashMap<>();
        data.put("term", term);
        data.put("timestamp", new Date());

        db.collection("users")
                .document(userId)
                .collection("search_history")
                .document(term) // to avoid duplicates
                .set(data);
    }

    public FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }
}
