package com.opsc.collectebils;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class SelectedCollectionActivity extends AppCompatActivity {
    Dialog myDialog;
    Button addItem;
    Button addToWishlist;
    Button scanBarcode;
    TextView name;

    String catName;
    String catKey;

    Uri imgUri;

    private FirebaseUser user;
    private String userId;
    private DatabaseReference ref;


    // instance for firebase storage and StorageReference
    public StorageReference storageReference;

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
        addToWishlist = findViewById(R.id.add_to_wishlist);
        scanBarcode = findViewById(R.id.barcode_scanner);

        catName = getIntent().getExtras().getString("collectionName");
        catKey = getIntent().getExtras().getString("collectionKey");
        name.setText(catName);

        addItem.setOnClickListener(view -> {
            myDialog.setContentView(R.layout.add_item_window);
            myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            myDialog.show();

            Button btnClose, btnAdd, btnUpload, btnCamera;

            btnAdd = (Button) myDialog.findViewById(R.id.btnAdd);
            btnCamera = (Button) myDialog.findViewById(R.id.btnCamera);
            btnUpload = (Button) myDialog.findViewById(R.id.btnUpload);
            btnClose = (Button) myDialog.findViewById(R.id.close_btn);

            EditText enterItemName, enterItemDescription, enterManufacturer, enterProductionYear,
                    enterPurchasePrice, enterPurchaseDate;

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
            btnCamera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Intent intent = new Intent();
                        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        startActivity(intent);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    addItemData();

                    arrayAdapter = new ArrayAdapter(getApplicationContext(), R.layout.list_item, R.id.name, list);
                    my_collections_list.setAdapter(arrayAdapter);
                }

                private void addItemData() {
                    String userID = userId;
                    String categoryName = catName;
                    String categoryKey = getIntent().getExtras().getString("collectionKey");

                    String itemName = enterItemName.getText().toString().trim();
                    String itemDescription = enterItemDescription.getText().toString().trim();
                    String manufacturer = enterManufacturer.getText().toString().trim();
                    String productionYear = enterProductionYear.getText().toString().trim();
                    String purchasePrice = enterPurchasePrice.getText().toString().trim();
                    String purchaseDate = enterPurchaseDate.getText().toString().trim();


                    uploadImage();

                    Items item = new Items(userID, categoryName, categoryKey, itemName, itemDescription, manufacturer, productionYear, purchasePrice, purchaseDate, imgUri.toString());

                    ref.push().setValue(item)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(SelectedCollectionActivity.this, "New item added.", Toast.LENGTH_LONG).show();
                                        myDialog.dismiss();

                                    } else {
                                        Toast.makeText(SelectedCollectionActivity.this, "Operation failed.", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
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
        ref = FirebaseDatabase.getInstance().getReference("Items");
        storageReference = FirebaseStorage.getInstance().getReference();


        user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        userId = user.getUid();

        arrayAdapter = new ArrayAdapter(getApplicationContext(), R.layout.list_item, R.id.name, list);
        my_collections_list.setAdapter(arrayAdapter);

        ref.orderByChild("userID").equalTo(userId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Items value = snapshot.getValue(Items.class);
                if (value.getCategoryKey().equals(catKey) && value.categoryName.equals(catName) && value.getUserID().equals(userId)) {
                    list.add(value.getItemName());

                    Collections.sort(list, new Comparator<String>() {
                        @Override
                        public int compare(String s, String t1) {
                            return s.compareToIgnoreCase(t1);
                        }
                    });
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
                i.putExtra("collectionKey", catKey);
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

    private void uploadImage() {
        if(imgUri != null){
            StorageReference fileRef = storageReference.child(System.currentTimeMillis()+"."+getFileExtension(imgUri));
            fileRef.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Toast.makeText(SelectedCollectionActivity.this, "Uploaded.", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(SelectedCollectionActivity.this, "Operation failed.", Toast.LENGTH_LONG).show();
                }
            });
        }

    }
    private String getFileExtension(Uri imgUri){

        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap typeMap = MimeTypeMap.getSingleton();
        return typeMap.getExtensionFromMimeType(contentResolver.getType(imgUri));
    }

    private void SelectImage() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        galleryIntent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        galleryIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(galleryIntent,2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 2 && resultCode == RESULT_OK && data != null);

        imgUri=data.getData();
    }


    @Override
    public void onResume() {
        super.onResume();
        //startActivity(new Intent(getApplicationContext(),MyCollectionsActivity.class));
        overridePendingTransition(0, 0);
        bottomNavigationView.getMenu().getItem(0).setChecked(true);
    }

}
