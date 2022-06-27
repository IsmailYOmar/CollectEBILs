package com.opsc.collectebils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import android.Manifest;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
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


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class WishlistActivity extends AppCompatActivity {

    Dialog myDialog;
    Button addToWishlist;
    TextView name;
    String catName;
    String catKey;
    Uri imgUri;
    SearchView searchView;
    Button searchBtn2;
    boolean flag = false;


    private FirebaseUser user;
    private String userId;
    private DatabaseReference ref;
    public String fileName;
    // instance for firebase storage and StorageReference
    public StorageReference storageReference;
    ListView wishes;
    ArrayAdapter arrayAdapter;
    ArrayList<String> list = new ArrayList<>();
    Wishlist wishlist;
    public BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);

        name = findViewById(R.id.wishlistCollectionName);
        myDialog = new Dialog(this);
        addToWishlist = findViewById(R.id.addToWishlist);
        catName = getIntent().getExtras().getString("collectionName");
        catKey = getIntent().getExtras().getString("collectionKey");
        name.setText(catName+ " Wishlist");
        searchBtn2 = findViewById(R.id.searchBtn2);
        searchView = findViewById(R.id.search_bar3);

        searchBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (flag == true){
                    searchView.setVisibility(View.INVISIBLE);
                    flag = false;
                }
                else{
                    searchView.setVisibility(View.VISIBLE);
                    flag = true;
                }
            }
        });


        addToWishlist.setOnClickListener(view ->
        {
            myDialog.setContentView(R.layout.add_to_wishlist_window);
            myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            myDialog.show();

            Button btnClose, btnUploadWishlist, btnAddWishlist;
            EditText enterItemName, enterItemDescription;

            enterItemName = (EditText) myDialog.findViewById(R.id.enterWishlistItemName);
            enterItemDescription = (EditText) myDialog.findViewById(R.id.enterWishlistItemDescription);

            btnUploadWishlist = (Button) myDialog.findViewById(R.id.btnUploadWishlist);
            btnClose = (Button) myDialog.findViewById(R.id.closeBtn);

            btnClose.setOnClickListener(new View.OnClickListener()
            {

                @Override
                public void onClick(View view)
                {
                    myDialog.dismiss();
                }


            });

            btnUploadWishlist.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(WishlistActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 103);
                        SelectImage();
                    }else {
                        SelectImage();
                    }


                }
            });
            btnAddWishlist = (Button) myDialog.findViewById(R.id.btnAddWishlist);
            btnAddWishlist.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {

                    addWishlistData();

                    arrayAdapter = new ArrayAdapter(WishlistActivity.this, R.layout.list_item, R.id.name, list);
                    wishes.setAdapter(arrayAdapter);
                }

                private void addWishlistData() {
                        String userID = userId;
                        String categoryName = catName;
                        String categoryKey = getIntent().getExtras().getString("collectionKey");

                        String itemName = enterItemName.getText().toString().trim();
                        String itemDescription = enterItemDescription.getText().toString().trim();

                        String imageFileName = fileName;

                        if(itemName.isEmpty()) {
                            enterItemName.setError("All fields are required.");
                            enterItemName.requestFocus();
                            return;
                        }

                        if(itemName.length()>150) {
                            enterItemName.setError("The item name is too long.");
                            enterItemName.requestFocus();
                            return;
                        }

                        if(itemDescription.isEmpty()) {
                            enterItemDescription.setError("All fields are required.");
                            enterItemDescription.requestFocus();
                            return;
                        }

                        if(itemDescription.length()>350) {
                            enterItemDescription.setError("The description is too long.");
                            enterItemDescription.requestFocus();
                            return;
                        }

                        Wishlist wishlist = new Wishlist(userID,categoryName,categoryKey,itemName,itemDescription,imageFileName);


                        ref.push().setValue(wishlist)
                                .addOnCompleteListener(new OnCompleteListener<Void>()
                                {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task)
                                    {
                                        if (task.isSuccessful())
                                        {
                                            Toast.makeText(WishlistActivity.this, "New item added.", Toast.LENGTH_LONG).show();
                                            myDialog.dismiss();
                                        }

                                        else
                                        {
                                            Toast.makeText(WishlistActivity.this, "Operation failed.", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                    }
            });
        });

        wishes= findViewById(R.id.List);
        ref = FirebaseDatabase.getInstance().getReference("Wishlist");
        storageReference = FirebaseStorage.getInstance().getReference("Images");
        user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        userId = user.getUid();
        arrayAdapter = new ArrayAdapter(WishlistActivity.this, R.layout.list_item, R.id.name, list);
        wishes.setAdapter(arrayAdapter);

        ref.orderByChild("userID").equalTo(userId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName)
            {
                Items value = snapshot.getValue(Items.class);
                if (value.getCategoryKey().equals(catKey) && value.categoryName.equals(catName) && value.getUserID().equals(userId))
                {
                    list.add(value.getItemName());

                    Collections.sort(list, new Comparator<String>()
                    {
                        @Override
                        public int compare(String s, String t1)
                        {
                            return s.compareToIgnoreCase(t1);
                        }
                    });
                }
                arrayAdapter.notifyDataSetChanged();
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


        wishes.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {

                Intent i = new Intent(WishlistActivity.this, WishlistDetailsActivity.class);
                i.putExtra("itemName", list.get(position));
                i.putExtra("collectionName", catName);
                i.putExtra("collectionKey", catKey);
                startActivity(i);

            }


        });


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                WishlistActivity.this.arrayAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                WishlistActivity.this.arrayAdapter.getFilter().filter(newText);
                return false;
            }
        });


    // Initialize and assign variable
    bottomNavigationView = findViewById(R.id.bottomNavigation);

    // Set Home selected
        bottomNavigationView.setSelectedItemId(R.id.explore);

    // Perform item selected listener
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener()
    {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item)
        {
            switch (item.getItemId())
            {
                case R.id.myCollections:
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

    private void uploadImage()
    {
        if(imgUri != null)
        {
            fileName = System.currentTimeMillis()+"."+getFileExtension(imgUri);
            StorageReference fileRef = storageReference.child(fileName);
            fileRef.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
            {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                {
                    Task<Uri> downloadUri = taskSnapshot.getStorage().getDownloadUrl();
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
                    {
                        @Override
                        public void onSuccess(Uri uri)
                        {
                            Toast.makeText(WishlistActivity.this, "Image Uploaded.", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>()
            {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot)
                {

                }
            }).addOnFailureListener(new OnFailureListener()
            {
                @Override
                public void onFailure(@NonNull Exception e)
                {
                    Toast.makeText(WishlistActivity.this, "Operation failed.", Toast.LENGTH_LONG).show();
                }
            });
        }

    }
    private String getFileExtension(Uri imgUri)
    {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap typeMap = MimeTypeMap.getSingleton();
        return typeMap.getExtensionFromMimeType(contentResolver.getType(imgUri));
    }

    private void SelectImage()
    {
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

        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            imgUri = data.getData();
            uploadImage();
        }
    }
    @Override
    public void onResume()
    {
        super.onResume();
        //startActivity(new Intent(getApplicationContext(),MyCollectionsActivity.class));
        overridePendingTransition(0, 0);
        bottomNavigationView.getMenu().getItem(0).setChecked(true);
    }
}