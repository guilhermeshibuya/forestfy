package com.example.reconhecimentoflorestal.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.os.Bundle;

import com.example.reconhecimentoflorestal.ui.home.HomeFragment;
import com.example.reconhecimentoflorestal.R;
import com.example.reconhecimentoflorestal.ui.results.ResultsFragment;
import com.example.reconhecimentoflorestal.utils.RuntimeLocaleChanger;
import com.example.reconhecimentoflorestal.ui.settings.SettingsFragment;
import com.example.reconhecimentoflorestal.SharedViewModel;
import com.example.reconhecimentoflorestal.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity  {
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new HomeFragment());

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                replaceFragment(new HomeFragment());
            } else if (id == R.id.nav_results) {
                replaceFragment(new ResultsFragment());
            } else if (id == R.id.nav_settings) {
               replaceFragment(new SettingsFragment());
            }

            return true;
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(RuntimeLocaleChanger.wrapContext(newBase));
    }

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayout, fragment)
                .commit();
    }

    public void switchToResultsFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayout, new ResultsFragment())
                .commit();
    }
}