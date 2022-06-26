package com.opsc.collectebils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class CollectionDetailsActivity extends AppCompatActivity {

    TextView categoryName;
    TextView categoryGoal;
    TextView itemNumber;
    TextView itemPercentage;
    ProgressBar progressBar;

    String catName;
    String catKey;
    public int goalNumber;
    public int count;
    private FirebaseUser user;
    private String userId;
    private DatabaseReference ref;
    private DatabaseReference ref2;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection_details);

        categoryName = (TextView) findViewById(R.id.categoryName);
        categoryGoal = (TextView) findViewById(R.id.categoryGoal);
        itemNumber = (TextView) findViewById(R.id.itemNumber);
        itemPercentage =(TextView) findViewById(R.id.itemPercentage);
        progressBar= (ProgressBar) findViewById(R.id.progressBar);

        catName = getIntent().getExtras().getString("collectionName");
        catKey = getIntent().getExtras().getString("collectionKey");

        categoryName.setText(catName);
        GetCollectionDetails();
        categoryGoal.setText(goalNumber);
        GetItemDetails();
        itemNumber.setText(count);

        if(goalNumber != 0) {
            double percent = (count / goalNumber) * 100;
            itemPercentage.setText(percent + "%");
        }else{
            itemPercentage.setText("0%");
        }


        //progressBar.setMax(goalNumber);
        //progressBar.setProgress(count);

    }

    public void GetCollectionDetails() {
        // get categoryGoal
        user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        userId = user.getUid();
        ref=FirebaseDatabase.getInstance().getReference("Categories");

        ref.orderByChild("userID").equalTo(userId).addChildEventListener(new ChildEventListener()
        {//retrieve collection name and collection goal
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Category value = snapshot.getValue(Category.class);
                if (value.getCategoryName().equals(catName) && snapshot.getKey().equals(catKey)) {
                    goalNumber = value.getGoalNumber();
                }
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

    }
    public void GetItemDetails() {

        //get current user and create firebase instance
        ref2 = FirebaseDatabase.getInstance().getReference("Items");
        user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        userId = user.getUid();
        ref2.orderByChild("userID").equalTo(userId).addChildEventListener(new ChildEventListener()
        {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName)
            {
                //save firebase query result to object of Items class
                Items value2 = snapshot.getValue(Items.class);
                if (value2.getCategoryKey().equals(catKey) && value2.categoryName.equals(catName) ){

                    count= count+1;
                }
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