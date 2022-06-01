package com.opsc.collectebils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MyCollectionsActivity extends AppCompatActivity {
    Dialog myDialog;
    Button addCategory;
    private FirebaseUser user;
    private String userId;
    private DatabaseReference ref;

    private String categoryId;
    public BottomNavigationView bottomNavigationView;

    ListView my_collections_list;
    ArrayAdapter arrayAdapter;
    ArrayList<String> list = new ArrayList<>();

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

            user = FirebaseAuth.getInstance().getCurrentUser();
            assert user != null;
            userId = user.getUid();
            ref = FirebaseDatabase.getInstance().getReference("Categories");



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

                    switch (view.getId()) {
                        case R.id.addCatBtn:
                            addCategoryData();
                            break;
                    }

                    arrayAdapter = new ArrayAdapter(getApplicationContext(), R.layout.list_item,R.id.name, list);
                    my_collections_list.setAdapter(arrayAdapter);

                }

                private void addCategoryData() {
                    String userID = userId;
                    String categoryName = catNameEditText.getText().toString().trim();
                    String goalNumber = goalEditText.getText().toString().trim();

                    Category cat = new Category(userID, categoryName, goalNumber);

                    ref.push().setValue(cat)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()) {
                                        Toast.makeText(MyCollectionsActivity.this, "New category added.", Toast.LENGTH_LONG).show();
                                        myDialog.dismiss();

                                    }
                                    else {
                                        Toast.makeText(MyCollectionsActivity.this, "Operation failed.", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
            });

        });

        my_collections_list = findViewById(R.id.my_collections_list);
        ref=FirebaseDatabase.getInstance().getReference("Categories");

        user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        userId = user.getUid();


        arrayAdapter = new ArrayAdapter(getApplicationContext(), R.layout.list_item,R.id.name, list);
        my_collections_list.setAdapter(arrayAdapter);

        ref.orderByChild("userID").equalTo(userId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String value= snapshot.getValue(Category.class).toString();
                list.add(value);
                Collections.sort(list, new Comparator<String>() {
                    @Override
                    public int compare(String s, String t1) {
                        return s.compareToIgnoreCase(t1);
                    }
                });
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



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


