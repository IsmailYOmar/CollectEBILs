package com.opsc.collectebils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

public class CollectionStatisticsActivity extends AppCompatActivity
{
    Items items;
    SearchView searchView4;
    Button searchBtn4;
    boolean flag = false;
    private FirebaseUser user;
    private String userId;
    private DatabaseReference ref;
    ListView statisticsList;
    ArrayAdapter arrAd;
    ArrayList<String> arrList = new ArrayList<>();
    ArrayList<String> arrListKey = new ArrayList<>();
    ArrayList<Integer> sortingMethodReturns = new ArrayList<Integer>();
    int j = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection_statistics);

        // get current user and create firebase instance
        user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        userId = user.getUid();
        ref=FirebaseDatabase.getInstance().getReference("Categories");
        statisticsList = (ListView) findViewById(R.id.statisticsList);
        searchBtn4 = findViewById(R.id.searchBtn4);
        searchView4 = findViewById(R.id.search_bar4);

        searchBtn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (flag == true){
                    searchView4.setVisibility(View.INVISIBLE);
                    flag = false;
                }
                else{
                    searchView4.setVisibility(View.VISIBLE);
                    flag = true;
                }
            }
        });


        arrAd = new ArrayAdapter(CollectionStatisticsActivity.this, R.layout.list_item,R.id.name, arrList);
        statisticsList.setAdapter(arrAd);

        ref.orderByChild("userID").equalTo(userId).addChildEventListener(new ChildEventListener()
        {//retrieve collection name and collection goal
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName)
            {
                String value= snapshot.getValue(Category.class).getCategoryName();
                arrList.add(value);
                arrListKey.add(snapshot.getKey());

                Collections.sort(arrList, new Comparator<String>()
                {
                    @Override
                    public int compare(String lhs, String rhs)
                    {
                        int returning = lhs.compareTo(rhs);
                        sortingMethodReturns.add(returning);
                        return returning;
                    }

                });
                // now sort the list B according to the changes made with the order of
                // items in listA
                Collections.sort(arrListKey, new Comparator<String>()
                {
                    @Override
                    public int compare(String lhs, String rhs)
                    {
                        // comparator method will sort the second list also according to
                        // the changes made with list a
                        int returning = sortingMethodReturns.get(j);
                        j++;
                        return returning;
                    }

                });

                arrAd.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName)
            {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot)
            {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName)
            {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });

        statisticsList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                //AdapterView.OnItemClickListener.super.onItemClick(adapterView, view, i, l);

                Intent i = new Intent(CollectionStatisticsActivity.this, CollectionDetailsActivity.class);
                i.putExtra("collectionName", arrList.get(position));
                i.putExtra("collectionKey",arrListKey.get(position));
                startActivity(i);
            }

        });

        searchView4.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                CollectionStatisticsActivity.this.arrAd.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                CollectionStatisticsActivity.this.arrAd.getFilter().filter(newText);
                return false;
            }
        });
        }
    }

