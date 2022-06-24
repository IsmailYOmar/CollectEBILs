package com.opsc.collectebils;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DashboardActivity extends AppCompatActivity implements View.OnClickListener{

    private FirebaseUser user;
    private DatabaseReference ref;
    private String userId;
    private Button logoutButton;
    private Button collectionStatistics;
    public  BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        Spinner dropDown = findViewById(R.id.collectionLayout);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(
                this,
                R.array.Spinner_items,
                R.layout.dropdown_item
        );
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_layout);
        dropDown.setAdapter(adapter);
        logoutButton = (Button) findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(this);

        // get current user and create firebase instance
        user = FirebaseAuth.getInstance().getCurrentUser();
        ref = FirebaseDatabase.getInstance().getReference("Users");
        userId = user.getUid();

        final TextView full_name_view = (TextView) findViewById(R.id.full_name_view);
        //final TextView email_address_view = (TextView) findViewById(R.id.email_address_view);

        collectionStatistics = (Button) findViewById(R.id.collection_statistics);
        collectionStatistics.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                openCollectionStatisticsActivity();
            }
        });

        ref.child(userId).addListenerForSingleValueEvent(new ValueEventListener()
        {// get users Full Name from FireBase and Display in  TextView
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                User userProfile = snapshot.getValue(User.class);

                if(userProfile != null) {
                    String fullName = userProfile.fullName;
                    String emailAddress = userProfile.emailAddress;

                    full_name_view.setText(fullName);
                    //email_address_view.setText(emailAddress);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });

        // Initialize and assign variable
        bottomNavigationView = findViewById(R.id.bottomNavigation);

        // Set Home selected
        bottomNavigationView.setSelectedItemId(R.id.dashboard);

        // Perform item selected listener
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item)
            {
                switch (item.getItemId())
                {//nav menu styling and intents to change activity
                    case R.id.myCollections:
                        bottomNavigationView.getMenu().getItem(0).setChecked(true);
                        startActivity(new Intent(getApplicationContext(), MyCollectionsActivity.class));
                        overridePendingTransition(0, 0);
                        finish();
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
    public void openCollectionStatisticsActivity()
    { // Open activity to display users collection statistics
        Intent intent = new Intent(this, CollectionStatisticsActivity.class);
        startActivity(intent);
    }
    @Override
    public void onResume()
    {//override page animation on page resume
        super.onResume();
        overridePendingTransition(0, 0);
        bottomNavigationView.getMenu().getItem(3).setChecked(true);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.logoutButton:
                LogoutUser();
                break;
        }
    }

    private void LogoutUser()
    {//log out user from Firebase
        FirebaseAuth.getInstance().signOut();
        Intent i = new Intent(DashboardActivity.this, LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }
}