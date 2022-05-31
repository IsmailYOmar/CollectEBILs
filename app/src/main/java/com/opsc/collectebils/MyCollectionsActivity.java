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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.List;

public class MyCollectionsActivity extends AppCompatActivity {
    Dialog myDialog;
    Button addCategory;
    ListView my_collections_list;
    public BottomNavigationView bottomNavigationView;
    ArrayAdapter arrayAdapter;
    List<String> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_collections);

        myDialog = new Dialog(this);
        addCategory = findViewById(R.id.add_Category);

        addCategory.setOnClickListener(view -> {

            myDialog.setContentView(R.layout.create_a_category_window);
            myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            myDialog.show();

            EditText catNameEditText;
            EditText goalEditText;
            Button btnClose;
            Button addCatBtn;

            btnClose = (Button) myDialog.findViewById(R.id.close_btn);
            addCatBtn = (Button) myDialog.findViewById(R.id.addCatBtn);
            catNameEditText = (EditText) myDialog.findViewById(R.id.catNameEditText);
            goalEditText = (EditText) myDialog.findViewById(R.id.goalEditText);
            btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    myDialog.dismiss();
                }
            });

            addCatBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String catName = catNameEditText.getText().toString();
                    String goal = goalEditText.getText().toString();
                    list.add(catName);

                    arrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, list);
                    my_collections_list.setAdapter(arrayAdapter);

                    Toast.makeText(getBaseContext(), catName + " category added", Toast.LENGTH_SHORT).show();
                    myDialog.dismiss();
                }
            });

        });

        my_collections_list = findViewById(R.id.my_collections_list);


        list.add("Action figures");
        list.add("Comics");

        arrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, list);
        my_collections_list.setAdapter(arrayAdapter);

        my_collections_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

           @Override
           public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               //AdapterView.OnItemClickListener.super.onItemClick(adapterView, view, i, l);

               Intent i = new Intent(MyCollectionsActivity.this, SelectedCollectionActivity.class);
               i.putExtra("collectionName", list.get(position));
               startActivity(i);
           }
       });


        // Initialize and assign variable
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Set Home selected
        bottomNavigationView.setSelectedItemId(R.id.my_collections);

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
                return false;
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


