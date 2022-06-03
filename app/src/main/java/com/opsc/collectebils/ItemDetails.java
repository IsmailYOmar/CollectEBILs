package com.opsc.collectebils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
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

public class ItemDetails extends AppCompatActivity
{

    TextView categoryName;
    TextView itemName;
    TextView itemDescription;
    TextView manufacturer;
    TextView productionYear;
    TextView purchasePrice;
    TextView purchaseDate;
    ImageView itemImage;
    private FirebaseUser user;
    private String userId;
    private DatabaseReference ref;
    public  StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
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

    public void GetItemDetails()
    {

        //get current user and create firebase instance
        ref = FirebaseDatabase.getInstance().getReference("Items");
        user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        userId = user.getUid();

        String catName = getIntent().getExtras().getString("collectionName");
        String catKey = getIntent().getExtras().getString("collectionKey");
        String itemName = getIntent().getExtras().getString("itemName");


        ref.orderByChild("userID").equalTo(userId).addChildEventListener(new ChildEventListener()
        {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName)
            {
                //save firebase query result to object of Items class
                Items value = snapshot.getValue(Items.class);
                if (value.getCategoryKey().equals(catKey) && value.categoryName.equals(catName) && value.getUserID().equals(userId) && value.getItemName().equals(itemName))
                {
                    //set text views == data in Items object
                    itemDescription.setText(value.getItemDescription());
                    manufacturer.setText(value.getManufacturer());
                    productionYear.setText(String.valueOf(value.getProductionYear()));
                    purchasePrice.setText("R " + value.getPurchasePrice() + "0");
                    purchaseDate.setText(value.getPurchaseDate());

                    //use image file name from Items object in Firebase storage reference query
                    storageReference =FirebaseStorage.getInstance().getReference().
                            child("Images/"+value.getImgFileName());
                    try
                    {
                        //save query result into temp file
                        File tempFile = File.createTempFile("temp","jpg");
                        storageReference.getFile(tempFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>()
                                {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot)
                            {
                                //set imageview to result of Firebase storage query
                                Bitmap bitmap = BitmapFactory.decodeFile(tempFile.getAbsolutePath());
                                itemImage.setImageBitmap(bitmap);
                            }
                        });
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    }

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