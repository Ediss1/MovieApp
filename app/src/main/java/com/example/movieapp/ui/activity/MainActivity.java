package com.example.movieapp.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.movieapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            // Not logged in, go back to login
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_container);

        if (navHostFragment == null) {
            throw new IllegalStateException("NavHostFragment not found");
        }

        NavController navController = navHostFragment.getNavController();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        NavigationUI.setupWithNavController(bottomNavigationView, navController);


    }
}
