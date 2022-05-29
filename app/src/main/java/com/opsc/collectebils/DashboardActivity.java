package com.opsc.collectebils;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class DashboardActivity extends AppCompatActivity {

    public  BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Initialize and assign variable
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Set Home selected
        bottomNavigationView.setSelectedItemId(R.id.dashboard);

        // Perform item selected listener
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.my_collections:
                        bottomNavigationView.getMenu().getItem(0).setChecked(true);
                        startActivity(new Intent(getApplicationContext(), MyCollectionsActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.explore:
                        bottomNavigationView.getMenu().getItem(1).setChecked(true);
                        startActivity(new Intent(getApplicationContext(), ExploreActivity.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                    case R.id.marketplace:
                        bottomNavigationView.getMenu().getItem(2).setChecked(true);
                        startActivity(new Intent(getApplicationContext(), MarketplaceActivity.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                    case R.id.dashboard:
                        bottomNavigationView.getMenu().getItem(3).setChecked(true);
                        startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                }
                return true;
            }
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        overridePendingTransition(0, 0);
        bottomNavigationView.getMenu().getItem(3).setChecked(true);
    }

}