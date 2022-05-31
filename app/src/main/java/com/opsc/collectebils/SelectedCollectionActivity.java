package com.opsc.collectebils;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.List;

public class SelectedCollectionActivity extends AppCompatActivity {
    Dialog myDialog;
    Button addItem;
    Button addToWishlist;
    Button scanBarcode;
    TextView name;

    public BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_colllection);

        name = findViewById(R.id.selected_collection_name);
        myDialog = new Dialog(this);
        addItem = findViewById(R.id.add_Item);
        addToWishlist= findViewById(R.id.add_to_wishlist);
        scanBarcode= findViewById(R.id.barcode_scanner);

        name.setText(getIntent().getExtras().getString("collectionName"));

        addItem.setOnClickListener(view -> {
            myDialog.setContentView(R.layout.add_item_window);
            myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            myDialog.show();
            Button btnClose;
            btnClose = (Button) myDialog.findViewById(R.id.close_btn);
            btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    myDialog.dismiss();
                }
            });
        });

        addToWishlist.setOnClickListener(view -> {
            myDialog.setContentView(R.layout.add_to_wishlist_window);
            myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            myDialog.show();
            Button btnClose;
            btnClose = (Button) myDialog.findViewById(R.id.close_btn);
            btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    myDialog.dismiss();
                }
            });
        });

        /*ArrayAdapter arrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1,list);
        my_collections_list.setAdapter(arrayAdapter);

        my_collections_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            private Object ActionFiguresActivity;

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    // startActivity(new Intent(SelectedCollectionActivity.this, (Class<?>) s.class));


                }
            });
        */

        scanBarcode.setOnClickListener(view -> {
            myDialog.setContentView(R.layout.scan_barcode_window);
            myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            myDialog.show();
            Button btnClose;
            btnClose = (Button) myDialog.findViewById(R.id.close_btn);
            btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    myDialog.dismiss();
                }
            });
        });


        // Initialize and assign variable
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Set Home selected
        bottomNavigationView.setSelectedItemId(R.id.explore);

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
                        startActivity(new Intent(getApplicationContext(), SelectedCollectionActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.marketplace:
                        bottomNavigationView.getMenu().getItem(2).setChecked(true);
                        startActivity(new Intent(getApplicationContext(), MarketplaceActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.dashboard:
                        bottomNavigationView.getMenu().getItem(3).setChecked(true);
                        startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
                        overridePendingTransition(0, 0);
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
        bottomNavigationView.getMenu().getItem(0).setChecked(true);
    }
}