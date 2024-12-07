package com.example.agriminder;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.example.agriminder.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if the user is logged in
        SharedPreferences sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("user_login", false);

        // If not logged in, redirect to LoginProcess
        if (!isLoggedIn) {
            Intent intent = new Intent(MainActivity.this, LoginProcess.class);
            startActivity(intent);
            finish(); // Close MainActivity
            return;
        }

        // Initialize UI components
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Apply window insets for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Load the default fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
        }

        // Handle navigation item selection
        binding.navBar.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.menu_home) {
                replaceFragment(new HomeFragment());
            } else if (itemId == R.id.menu_reminders) {
                replaceFragment(new ReminderFragment());
            } else if (itemId == R.id.menu_calendar) {
                replaceFragment(new CalendarFragment());
            } else if (itemId == R.id.menu_batch) {
                replaceFragment(new BatchFragment());
            } else if (itemId == R.id.menu_logout) {
                logOutUser();
            }

            return true;
        });
    }

    // Method to replace fragment based on the selection
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

    // Method to log out user and redirect to LoginProcess activity
    private void logOutUser() {
        // Clear the login state in SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("user_login", false); // Set user_login to false
        editor.apply();

        // Redirect to LoginProcess activity
        Intent intent = new Intent(MainActivity.this, LoginProcess.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear back stack
        startActivity(intent);
        finish(); // Close MainActivity
    }
}
