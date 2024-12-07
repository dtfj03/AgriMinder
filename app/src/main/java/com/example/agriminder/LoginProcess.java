package com.example.agriminder;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginProcess extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button loginButton, registerButton;
    private ProgressBar progressBar;

    private static final String API_URL = "https://devlab.helioho.st/api/validate.php"; // Replace with your login API endpoint
    private static final String API_KEY = "dc599a2d8310c9a6db280c393c88beff84a50b342da151f544237036e905187b"; // Replace with your API key

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize UI components
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerLink);
        progressBar = findViewById(R.id.progressBar);

        // Login button logic
        loginButton.setOnClickListener(v -> loginUser());

        // Register button logic - navigate to RegisterPage
        registerButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginProcess.this, RegisterActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void loginUser() {
        // Get user inputs
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Validate inputs
        if (!validateInputs(email, password)) {
            return;
        }

        // Show progress bar while logging in
        progressBar.setVisibility(View.VISIBLE);

        // Create a new thread for the network request
        new Thread(() -> {
            HttpURLConnection connection = null;
            try {
                // Create URL object
                URL url = new URL(API_URL);

                // Create connection
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.setConnectTimeout(15000);
                connection.setReadTimeout(15000);
                connection.setDoOutput(true);

                // Create the POST parameters
                String postData = "api_key=" + API_KEY
                        + "&email=" + email
                        + "&password=" + password;

                // Send request
                OutputStream os = connection.getOutputStream();
                os.write(postData.getBytes());
                os.flush();
                os.close();

                // Get the response
                int responseCode = connection.getResponseCode();
                BufferedReader in;
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                } else {
                    in = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                }

                StringBuilder response = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // Handle the API response on the main thread
                handleResponse(response.toString(), responseCode);

            } catch (Exception e) {
                e.printStackTrace();
                // Handle the error on the main thread
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(LoginProcess.this, "Login Failed. Please check your connection.", Toast.LENGTH_SHORT).show();
                });
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }).start();
    }

    private boolean validateInputs(String email, String password) {
        if (email == null || email.isEmpty()) {
            emailEditText.setError("Email is required");
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Enter a valid email");
            return false;
        }
        if (password == null || password.isEmpty()) {
            passwordEditText.setError("Password is required");
            return false;
        }
        return true;
    }

    private void handleResponse(String response, int responseCode) {
        runOnUiThread(() -> {
            progressBar.setVisibility(View.GONE);
            try {
                JSONObject jsonResponse = new JSONObject(response);
                boolean success = jsonResponse.optBoolean("success", false);
                String message = jsonResponse.optString("message", "Invalid email or password");

                if (responseCode == HttpURLConnection.HTTP_OK && success) {
                    SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("isLoggedIn", true);
                    editor.apply();

                    Toast.makeText(LoginProcess.this, "Login Successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginProcess.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginProcess.this, message, Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(LoginProcess.this, "Invalid server response", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
