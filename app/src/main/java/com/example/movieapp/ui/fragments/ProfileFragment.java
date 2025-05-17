package com.example.movieapp.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.movieapp.R;
import com.example.movieapp.ui.activity.LoginActivity;
import com.example.movieapp.ui.viewmodel.ProfileViewModel;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileFragment extends Fragment {

    private ProfileViewModel profileViewModel;

    private TextView tvUserEmail;
    private EditText etCurrentPassword, etNewPassword;
    private Button btnChangePassword, btnSignOut;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        tvUserEmail = view.findViewById(R.id.tvUserEmail);
        etCurrentPassword = view.findViewById(R.id.etCurrentPassword);
        etNewPassword = view.findViewById(R.id.etNewPassword);
        btnChangePassword = view.findViewById(R.id.btnChangePassword);
        btnSignOut = view.findViewById(R.id.btnSignOut);

        if (profileViewModel.getCurrentUser() != null) {
            tvUserEmail.setText("Email: " + profileViewModel.getCurrentUser().getEmail());
        }

        btnChangePassword.setOnClickListener(v -> {
            String currentPass = etCurrentPassword.getText().toString().trim();
            String newPass = etNewPassword.getText().toString().trim();

            if (TextUtils.isEmpty(currentPass) || TextUtils.isEmpty(newPass)) {
                Toast.makeText(getContext(), "Please enter both passwords", Toast.LENGTH_SHORT).show();
                return;
            }

            profileViewModel.changePassword(currentPass, newPass);
        });

        btnSignOut.setOnClickListener(v -> {
            profileViewModel.signOut();

            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // clear backstack
            startActivity(intent);
        });

        profileViewModel.getStatusMessage().observe(getViewLifecycleOwner(), message ->
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show()
        );

        return view;
    }
}
