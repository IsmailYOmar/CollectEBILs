package com.opsc.collectebils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.net.URI;
import java.util.Date;
import java.util.jar.Attributes;

public class ItemDetails extends AppCompatActivity {

    TextView categoryName;
    TextView itemName;
    TextView itemDescription;
    TextView manufacturer;
    TextView productionYear;
    TextView purchasePrice;
    TextView purchaseDate;
    ImageView itemImage;

    Items items;

    private FirebaseUser user;
    private String userId;
    private DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);

        categoryName = findViewById(R.id.itemCategoryName);
        itemName = findViewById(R.id.itemName);
        itemDescription = findViewById(R.id.itemDescription);
        manufacturer = findViewById(R.id.itemManufacturer);
        productionYear = findViewById(R.id.itemProductionYear);
        purchasePrice = findViewById(R.id.itemPurchasePrice);
        purchaseDate = findViewById(R.id.itemPurchaseDate);
        itemImage =findViewById(R.id.itemImage);



        itemName.setText(getIntent().getExtras().getString("itemName"));
        categoryName.setText(getIntent().getExtras().getString("collectionName"));
        GetItemDetails();

    }

    public void GetItemDetails() {

        ref= FirebaseDatabase.getInstance().getReference("Items");

        user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        userId = user.getUid();

        String catName = getIntent().getExtras().getString("collectionName");
        String catKey = getIntent().getExtras().getString("collectionKey");
        String itemName = getIntent().getExtras().getString("itemName");


        ref.orderByChild("userID").equalTo(userId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Items value= snapshot.getValue(Items.class);
                if(value.getCategoryKey().equals(catKey) && value.categoryName.equals(catName) && value.getUserID().equals(userId) && value.getItemName().equals(itemName) ){
                    itemDescription.setText(value.getItemDescription());;
                    manufacturer.setText(value.getManufacturer());
                    productionYear.setText(value.getProductionYear());
                    purchasePrice.setText("R "+value.getPurchasePrice());
                    purchaseDate.setText(value.getPurchaseDate());
                    if(value.getImgUri() != null){
                        itemImage.setImageURI(Uri.parse(value.getImgUri()));
                    }

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