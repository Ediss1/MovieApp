package com.example.movieapp.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.movieapp.R;
import com.example.movieapp.ui.viewmodel.LoginViewModel;


public class LoginActivity extends AppCompatActivity {

    LoginViewModel loginViewModel;

    EditText passwordText, inputEmail;
    Button btnLogin;
    ProgressBar progressBar;
    TextView registerNow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        if (loginViewModel.getCurrentUser() != null) {
            navigateToMainActivity();
        }

        passwordText = findViewById(R.id.editTextPassword);
        inputEmail = findViewById(R.id.editTextEmail);
        btnLogin = findViewById(R.id.btnLogin);
        progressBar = findViewById(R.id.progressBar);
        registerNow = findViewById(R.id.registerNow);

        btnLogin.setOnClickListener(view -> {
            String email = inputEmail.getText().toString().trim();
            String password = passwordText.getText().toString().trim();

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Enter all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            progressBar.setVisibility(View.VISIBLE);
            loginViewModel.login(email, password);
        });

        loginViewModel.getUserLiveData().observe(this, firebaseUser -> {
            progressBar.setVisibility(View.GONE);
            if (firebaseUser != null) {
                Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
                navigateToMainActivity();
            }
        });

        registerNow.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
            finish();
        });

        loginViewModel.getErrorMessage().observe(this, error -> {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
        });
    }

    private void navigateToMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

}