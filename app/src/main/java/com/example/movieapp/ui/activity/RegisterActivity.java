package com.example.movieapp.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.movieapp.R;
import com.example.movieapp.ui.viewmodel.RegisterViewModel;

public class RegisterActivity extends AppCompatActivity {

    private RegisterViewModel registerViewModel;
    private EditText passwordText, inputEmail, confPassword;
    private Button btnRegister;
    private ProgressBar progressBar;
    private TextView loginNow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize ViewModel
        registerViewModel = new RegisterViewModel();
        passwordText = findViewById(R.id.editTextPassword);
        confPassword = findViewById(R.id.editTextConfPassword);
        inputEmail = findViewById(R.id.editTextEmail);
        btnRegister = findViewById(R.id.btnRegister);
        progressBar = findViewById(R.id.progressBar);
        loginNow = findViewById(R.id.loginNow);

        // Observe LiveData
        registerViewModel.getIsLoading().observe(this, isLoading -> {
            if (isLoading != null && isLoading) {
                progressBar.setVisibility(View.VISIBLE);
            } else {
                progressBar.setVisibility(View.GONE);
            }
        });

        registerViewModel.getErrorMessage().observe(this, errorMessage -> {
            if (errorMessage != null) {
                Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        registerViewModel.getRegistrationSuccess().observe(this, registrationSuccess -> {
            if (registrationSuccess != null && registrationSuccess) {
                Toast.makeText(RegisterActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });

        loginNow.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        });

        btnRegister.setOnClickListener(view -> {
            String email = inputEmail.getText().toString().trim();
            String password = passwordText.getText().toString().trim();
            String confirmPassword = confPassword.getText().toString().trim();

            registerViewModel.registerUser(email, password, confirmPassword);
        });
    }
}
