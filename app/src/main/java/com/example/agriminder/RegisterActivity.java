package com.example.agriminder;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class RegisterActivity extends AppCompatActivity {

    private EditText lastNameEditText, firstNameEditText, emailEditText, passwordEditText, confirmPasswordEditText;
    private Button registerButton;
    private ProgressBar progressBar;
    private TextView loginLink;

    private static final String API_URL = "https://devlab.helioho.st/api/register.php";
    private static final String API_KEY = "dc599a2d8310c9a6db280c393c88beff84a50b342da151f544237036e905187b";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // Initialize UI components
        lastNameEditText = findViewById(R.id.lastNameEditText);
        firstNameEditText = findViewById(R.id.firstNameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        registerButton = findViewById(R.id.registerButton);
        loginLink = findViewById(R.id.loginLink);
        progressBar = findViewById(R.id.progressBar);

        // Register button logic
        registerButton.setOnClickListener(v -> registerUser());

        // Redirect to LoginPage
        loginLink.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginProcess.class);
            startActivity(intent);
        });
    }

    private void registerUser() {
        String lastName = lastNameEditText.getText().toString().trim();
        String firstName = firstNameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        // Validate inputs
        if (lastName.isEmpty()) {
            lastNameEditText.setError("Last name is required");
            return;
        }
        if (firstName.isEmpty()) {
            firstNameEditText.setError("First name is required");
            return;
        }
        if (email.isEmpty()) {
            emailEditText.setError("Email is required");
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Enter a valid email");
            return;
        }
        if (password.isEmpty()) {
            passwordEditText.setError("Password is required");
            return;
        }
        if (confirmPassword.isEmpty()) {
            confirmPasswordEditText.setError("Please confirm your password");
            return;
        }
        if (!password.equals(confirmPassword)) {
            confirmPasswordEditText.setError("Passwords do not match");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        new Thread(() -> {
            HttpURLConnection connection = null;
            try {
                URL url = new URL(API_URL);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.setDoOutput(true);

                String postData = "api_key=" + API_KEY
                        + "&first_name=" + firstName
                        + "&last_name=" + lastName
                        + "&email=" + email
                        + "&password=" + password;

                OutputStream os = connection.getOutputStream();
                os.write(postData.getBytes());
                os.flush();
                os.close();

                int responseCode = connection.getResponseCode();
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        responseCode == HttpURLConnection.HTTP_OK
                                ? connection.getInputStream()
                                : connection.getErrorStream()
                ));

                StringBuilder response = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    if (responseCode == HttpURLConnection.HTTP_OK && response.toString().contains("success")) {
                        Toast.makeText(RegisterActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegisterActivity.this, LoginProcess.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(RegisterActivity.this, "Registration Failed: " + response, Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(RegisterActivity.this, "Error: Unable to connect", Toast.LENGTH_SHORT).show();
                });
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }).start();
    }
}
