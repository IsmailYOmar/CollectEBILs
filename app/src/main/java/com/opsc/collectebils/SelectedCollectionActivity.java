package com.opsc.collectebils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public class SelectedCollectionActivity extends AppCompatActivity {
    Dialog myDialog;
    Button addItem;
    Button addToWishlist;
    Button scanBarcode;
    TextView name;

    String catName;
    String catKey;

    private FirebaseUser user;
    private String userId;
    private DatabaseReference ref;


    // Uri indicates, where the image will be picked from
    private Uri filePath;

    // request code
    private final int PICK_IMAGE_REQUEST = 22;

    // instance for firebase storage and StorageReference
    FirebaseStorage storage;
    StorageReference storageReference;

    ListView my_collections_list;
    ArrayAdapter arrayAdapter;
    ArrayList<String> list = new ArrayList<>();

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

        catName = getIntent().getExtras().getString("collectionName");
        catKey = getIntent().getExtras().getString("collectionKey");
        name.setText(catName);

        addItem.setOnClickListener(view -> {
            myDialog.setContentView(R.layout.add_item_window);
            myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            myDialog.show();

            Button btnClose;
            Button btnAdd;
            Button btnUpload;
            Button btnCamera;

            btnAdd = (Button) myDialog.findViewById(R.id.btnAdd);
            btnCamera = (Button) myDialog.findViewById(R.id.btnCamera);
            btnUpload = (Button) myDialog.findViewById(R.id.btnUpload);
            btnClose = (Button) myDialog.findViewById(R.id.close_btn);

            EditText enterItemName;
            EditText enterItemDescription;
            EditText enterManufacturer;
            EditText enterProductionYear;
            EditText enterPurchasePrice;
            EditText enterPurchaseDate;

            enterItemName = (EditText) myDialog.findViewById(R.id.enterItemName);
            enterItemDescription = (EditText) myDialog.findViewById(R.id.enterItemDescription);
            enterManufacturer = (EditText) myDialog.findViewById(R.id.enterManufacturer);
            enterProductionYear = (EditText) myDialog.findViewById(R.id.enterProductionYear);
            enterPurchasePrice = (EditText) myDialog.findViewById(R.id.enterPurchasePrice);
            enterPurchaseDate = (EditText) myDialog.findViewById(R.id.enterPurchaseDate);




            btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    myDialog.dismiss();
                }
            });

            btnUpload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SelectImage();
                }
            });

            btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    uploadImage();
                    addItemData();

                    arrayAdapter = new ArrayAdapter(getApplicationContext(), R.layout.list_item,R.id.name, list);
                    my_collections_list.setAdapter(arrayAdapter);
                }
                private void addItemData() {
                    String userID = userId;
                    String categoryName = catName;
                    String categoryKey = getIntent().getExtras().getString("collectionKey");;
                    String itemName = enterItemName.getText().toString().trim();
                    String itemDescription = enterItemDescription.getText().toString().trim();
                    String manufacturer = enterManufacturer.getText().toString().trim();
                    String productionYear = enterProductionYear.getText().toString().trim();
                    String purchasePrice = enterPurchasePrice.getText().toString().trim();
                    String purchaseDate = enterPurchaseDate.getText().toString().trim();

                    Items item = new Items(userID, categoryName, categoryKey,itemName,itemDescription,manufacturer,productionYear,purchasePrice,purchaseDate);

                    ref.push().setValue(item)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()) {
                                        Toast.makeText(SelectedCollectionActivity.this, "New item added.", Toast.LENGTH_LONG).show();
                                        myDialog.dismiss();

                                    }
                                    else {
                                        Toast.makeText(SelectedCollectionActivity.this, "Operation failed.", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
                });


            btnCamera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try
                    {
                        Intent intent = new Intent();
                        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivity(intent);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
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

        my_collections_list = findViewById(R.id.my_collections_list);
        ref= FirebaseDatabase.getInstance().getReference("Items");

        user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        userId = user.getUid();

        arrayAdapter = new ArrayAdapter(getApplicationContext(), R.layout.list_item,R.id.name, list);
        my_collections_list.setAdapter(arrayAdapter);

        ref.orderByChild("userID").equalTo(userId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Items value= snapshot.getValue(Items.class);
                if(value.getCategoryKey().equals(catKey) && value.categoryName.equals(catName) && value.getUserID().equals(userId)){
                    list.add(value.getItemName());
                }
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
                Intent i = new Intent(SelectedCollectionActivity.this, ItemDetails.class);
                i.putExtra("itemName", list.get(position));
                i.putExtra("collectionName", catName);
                i.putExtra("collectionKey",catKey);
                startActivity(i);

            }


        });




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


    @Override
    public void onResume() {
        super.onResume();
        overridePendingTransition(0, 0);
        bottomNavigationView.getMenu().getItem(0).setChecked(true);
    }

    // Select Image method
    private void SelectImage()
    {
        // Defining Implicit Intent to mobile gallery
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(
                        intent,
                        "Select Image from here..."),
                PICK_IMAGE_REQUEST);
    }
    // Override onActivityResult method
    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent data)
    {

        super.onActivityResult(requestCode,
                resultCode,
                data);

        // checking request code and result code
        // if request code is PICK_IMAGE_REQUEST and
        // resultCode is RESULT_OK
        // then set image in the image view
        if (requestCode == PICK_IMAGE_REQUEST
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {


            // Get the Uri of data
            filePath = data.getData();
            try {

                // Setting image on image view using Bitmap
                Bitmap bitmap = MediaStore
                        .Images
                        .Media
                        .getBitmap(
                                getContentResolver(),
                                filePath);
                //imageView.setImageBitmap(bitmap);
            }

            catch (IOException e) {
                // Log the exception
                e.printStackTrace();
            }
        }
    }
    // UploadImage method
    private void uploadImage()
    {
        if (filePath != null) {

            // Code for showing progressDialog while uploading
            ProgressDialog progressDialog
                    = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            // Defining the child of storageReference
            StorageReference ref = storageReference.child("images" + UUID.randomUUID().toString());

            // adding listeners on upload
            // or failure of image
            ref.putFile(filePath)
                    .addOnSuccessListener(
                            new OnSuccessListener<UploadTask.TaskSnapshot>() {

                                @Override
                                public void onSuccess(
                                        UploadTask.TaskSnapshot taskSnapshot)
                                {

                                    // Image uploaded successfully
                                    // Dismiss dialog
                                    progressDialog.dismiss();
                                    Toast
                                            .makeText(SelectedCollectionActivity.this, "Image Uploaded!!", Toast.LENGTH_SHORT)
                                            .show();
                                }
                            })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {

                            // Error, Image not uploaded
                            progressDialog.dismiss();
                            Toast
                                    .makeText(SelectedCollectionActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT)
                                    .show();
                        }
                    })
                    .addOnProgressListener(
                            new OnProgressListener<UploadTask.TaskSnapshot>() {

                                // Progress Listener for loading
                                // percentage on the dialog box
                                @Override
                                public void onProgress(
                                        UploadTask.TaskSnapshot taskSnapshot)
                                {
                                    double progress
                                            = (100.0
                                            * taskSnapshot.getBytesTransferred()
                                            / taskSnapshot.getTotalByteCount());
                                    progressDialog.setMessage(
                                            "Uploaded "
                                                    + (int)progress + "%");
                                }
                            });
        }
    }
}