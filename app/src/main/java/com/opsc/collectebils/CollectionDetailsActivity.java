package com.opsc.collectebils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
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

    ArrayList<String> list = new ArrayList<>();

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

        user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        userId = user.getUid();

        categoryName.setText(catName);
        GetCollectionDetails(userId);
        GetItemDetails(userId);

    }

    public void GetCollectionDetails(String userId) {
        // get categoryGoal
        ref=FirebaseDatabase.getInstance().getReference("Categories");

        ref.orderByChild("userID").equalTo(userId).addChildEventListener(new ChildEventListener()
        {//retrieve collection name and collection goal
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Category value = snapshot.getValue(Category.class);
                if (value.getCategoryName().equals(catName) && snapshot.getKey().equals(catKey)) {
                    categoryGoal.setText(String.valueOf(value.getGoalNumber()));
                    progressBar.setMax(value.getGoalNumber());
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

    public void GetItemDetails(String userId) {

        ref2 = FirebaseDatabase.getInstance().getReference("Items");


        ref2.orderByChild("userID").equalTo(userId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName)
            {
                Items value2 = snapshot.getValue(Items.class);
                if (value2.getCategoryKey().equals(catKey) && value2.categoryName.equals(catName) && value2.getUserID().equals(userId))
                {
                    list.add(value2.getItemName());
                }

                itemNumber.setText(String.valueOf(list.size()));
                progressBar.setProgress(list.size());

                if(goalNumber != 0) {
                    double percent = (list.size() / goalNumber) * 100;
                    itemPercentage.setText(String.valueOf(percent) + "%");
                }else{
                    itemPercentage.setText("0%");
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

}