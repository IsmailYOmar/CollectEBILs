package com.opsc.collectebils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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

public class CollectionStatisticsActivity extends AppCompatActivity {

    private FirebaseUser user;
    private String userId;
    private DatabaseReference ref;
    ListView my_statistics_list;
    ArrayAdapter arrAd;
    ArrayList<String> arrList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection_statistics);

        user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        userId = user.getUid();
        ref=FirebaseDatabase.getInstance().getReference("Categories");
        my_statistics_list = (ListView) findViewById(R.id.my_statistics_list);

        arrAd = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, arrList);
        my_statistics_list.setAdapter(arrAd);

        ref.orderByChild("userID").equalTo(userId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String val2 = snapshot.getValue(Category.class).getGoalNumber();
                arrList.add(val2);
                Collections.sort(arrList, new Comparator<String>() {
                    @Override
                    public int compare(String s, String t1) {
                        return s.compareToIgnoreCase(t1);
                    }
                });
                arrAd.notifyDataSetChanged();
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

        }
    }

