package com.opsc.collectebils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class SelectedCollectionActivity extends AppCompatActivity
{
    Dialog myDialog;
    Button addItem;
    Button goToWishlist;
    Button searchBtn;
    Button scanBarcode;
    TextView name;
    String catName;
    String catKey;
    Uri imgUri;
    String currentPhotoPath;
    SearchView searchView;
    boolean flag = false;


    private FirebaseUser user;
    private String userId;
    private DatabaseReference ref;
    public String imageUrl ;
    public String fileName;
    // instance for firebase storage and StorageReference
    public StorageReference storageReference;
    ListView collectionsList;
    ArrayAdapter arrayAdapter;
    ArrayList<String> list = new ArrayList<>();
    ArrayList<String> listOfKey = new ArrayList<>();
    ArrayList<Integer> sortingMethodReturns = new ArrayList<Integer>();
    int j=0;
    Items items;
    public BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_colllection);

        searchView = findViewById(R.id.search_bar1);
        searchBtn = findViewById(R.id.searchBtn2);

        name = findViewById(R.id.selectedCollectionName);
        myDialog = new Dialog(this);
        addItem = findViewById(R.id.addItem);
        goToWishlist = findViewById(R.id.goToWishlist);
        scanBarcode = findViewById(R.id.barcodeScanner);
        catName = getIntent().getExtras().getString("collectionName");
        catKey = getIntent().getExtras().getString("collectionKey");
        name.setText(catName);

        searchBtn.setOnClickListener(new View.OnClickListener() {
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

        addItem.setOnClickListener(view ->
        {
            myDialog.setContentView(R.layout.add_item_window);
            myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            myDialog.show();

            Button btnClose, btnAdd, btnUpload, btnCamera;

            btnAdd = (Button) myDialog.findViewById(R.id.btnAdd);
            btnCamera = (Button) myDialog.findViewById(R.id.btnCamera);
            btnUpload = (Button) myDialog.findViewById(R.id.btnUpload);
            btnClose = (Button) myDialog.findViewById(R.id.closeBtn);

            EditText enterItemName, enterItemDescription, enterManufacturer, enterProductionYear,
                    enterPurchasePrice, enterPurchaseDate;

            enterItemName = (EditText) myDialog.findViewById(R.id.enterItemName);
            enterItemDescription = (EditText) myDialog.findViewById(R.id.enterItemDescription);
            enterManufacturer = (EditText) myDialog.findViewById(R.id.enterManufacturer);
            enterProductionYear = (EditText) myDialog.findViewById(R.id.enterProductionYear);
            enterPurchasePrice = (EditText) myDialog.findViewById(R.id.enterPurchasePrice);
            enterPurchaseDate = (EditText) myDialog.findViewById(R.id.enterPurchaseDate);

            enterPurchaseDate.addTextChangedListener(new TextWatcher() {
                private String current = "";
                private String ddmmyyyy = "DDMMYYYY";
                private Calendar cal = Calendar.getInstance();

                @Override
                public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                    if (!s.toString().equals(current)) {
                        String clean = s.toString().replaceAll("[^\\d.]|\\.", "");
                        String cleanC = current.replaceAll("[^\\d.]|\\.", "");

                        int cl = clean.length();
                        int sel = cl;
                        for (i = 2; i <= cl && i < 6; i += 2) {
                            sel++;
                        }
                        if (clean.equals(cleanC)) sel--;

                        if (clean.length() < 8){
                            clean = clean + ddmmyyyy.substring(clean.length());
                        }else{
                            int day  = Integer.parseInt(clean.substring(0,2));
                            int mon  = Integer.parseInt(clean.substring(2,4));
                            int year = Integer.parseInt(clean.substring(4,8));

                            mon = mon < 1 ? 1 : mon > 12 ? 12 : mon;
                            cal.set(Calendar.MONTH, mon-1);
                            year = (year<1900)?1900:(year>2100)?2100:year;
                            cal.set(Calendar.YEAR, year);

                            day = (day > cal.getActualMaximum(Calendar.DATE))? cal.getActualMaximum(Calendar.DATE):day;
                            clean = String.format("%02d%02d%02d",day, mon, year);
                        }

                        clean = String.format("%s/%s/%s", clean.substring(0, 2),
                                clean.substring(2, 4),
                                clean.substring(4, 8));

                        sel = sel < 0 ? 0 : sel;
                        current = clean;
                        enterPurchaseDate.setText(current);
                        enterPurchaseDate.setSelection(sel < current.length() ? sel : current.length());
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            btnClose.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {

                    myDialog.dismiss();
                }
            });

            btnUpload.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(SelectedCollectionActivity.this,new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE}, 103);
                        SelectImage();
                    }else {
                        SelectImage();
                    }
                }
            });
            btnCamera.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(SelectedCollectionActivity.this,new String[] {Manifest.permission.CAMERA}, 101);
                        TakeImage();
                    }else {
                        TakeImage();
                    }
                }
            });


            btnAdd.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {

                    addItemData();

                    arrayAdapter = new ArrayAdapter(SelectedCollectionActivity.this, R.layout.list_item, R.id.name, list);
                    collectionsList.setAdapter(arrayAdapter);

                }

                private void addItemData() {
                    String userID = userId;
                    String categoryName = catName;
                    String categoryKey = getIntent().getExtras().getString("collectionKey");

                    String itemName = enterItemName.getText().toString().trim();
                    String itemDescription = enterItemDescription.getText().toString().trim();
                    String manufacturer = enterManufacturer.getText().toString().trim();
                    int productionYear;
                    double purchasePrice;
                    String purchaseDate = enterPurchaseDate.getText().toString().trim();

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

                    if(manufacturer.isEmpty()) {
                        enterManufacturer.setError("All fields are required.");
                        enterManufacturer.requestFocus();
                        return;
                    }

                    if(manufacturer.length()>100) {
                        enterManufacturer.setError("The manufacturer name is too long.");
                        enterManufacturer.requestFocus();
                        return;
                    }

                    try {
                        productionYear = Integer.parseInt(enterProductionYear.getText().toString().trim());
                    }
                    catch (NumberFormatException e) {
                        enterProductionYear.setError("All fields are required.");
                        enterProductionYear.requestFocus();
                        return;
                    }

                    if(productionYear == 0) {
                        enterProductionYear.setError("The year cannot be 0.");
                        enterProductionYear.requestFocus();
                        return;
                    }

                    if(productionYear < 0) {
                        enterProductionYear.setError("The year cannot be less than 0.");
                        enterProductionYear.requestFocus();
                        return;
                    }

                    if (productionYear > 2022) {
                        enterProductionYear.setError("The year is too far ahead.");
                        enterProductionYear.requestFocus();
                        return;
                    }

                    try {
                        purchasePrice = Double.parseDouble(enterPurchasePrice.getText().toString().trim());
                    }
                    catch (NumberFormatException e) {
                        enterPurchasePrice.setError("All fields are required.");
                        enterPurchasePrice.requestFocus();
                        return;
                    }

                    if (purchasePrice < 0) {
                        enterPurchasePrice.setError("The price cannot be less than 0.");
                        enterPurchasePrice.requestFocus();
                        return;
                    }

                    if (purchaseDate.isEmpty()) {
                        enterPurchaseDate.setError("All fields are required.");
                        enterPurchaseDate.requestFocus();
                        return;
                    }

                    Items item = new Items(userID, categoryName, categoryKey, itemName, itemDescription, manufacturer, productionYear, purchasePrice, purchaseDate,imageFileName );

                    ref.push().setValue(item)
                            .addOnCompleteListener(new OnCompleteListener<Void>()
                            {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    if (task.isSuccessful())
                                    {
                                        LayoutInflater inflater = getLayoutInflater();
                                        View customToastLayout = inflater.inflate(R.layout.list_item2, (ViewGroup) findViewById(R.id.root_layout));
                                        TextView textView6 = customToastLayout.findViewById(R.id.name);
                                        textView6.setText("New item added.");

                                        Toast mToast = new Toast(SelectedCollectionActivity.this);
                                        mToast.setDuration(Toast.LENGTH_LONG);
                                        mToast.setView(customToastLayout);
                                        mToast.show();
                                        //Toast.makeText(SelectedCollectionActivity.this, "New item added.", Toast.LENGTH_LONG).show();
                                        myDialog.dismiss();
                                        checkGoal(list);
                                    }

                                    else
                                    {
                                        LayoutInflater inflater = getLayoutInflater();
                                        View customToastLayout = inflater.inflate(R.layout.list_item2, (ViewGroup) findViewById(R.id.root_layout));
                                        TextView textView6 = customToastLayout.findViewById(R.id.name);
                                        textView6.setText("Operation failed.");

                                        Toast mToast = new Toast(SelectedCollectionActivity.this);
                                        mToast.setDuration(Toast.LENGTH_LONG);
                                        mToast.setView(customToastLayout);
                                        mToast.show();
                                        //Toast.makeText(SelectedCollectionActivity.this, "Operation failed.", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                }
            });
        });

        goToWishlist.setOnClickListener(view ->
        {
            Intent i = new Intent(SelectedCollectionActivity.this, WishlistActivity.class);
            i.putExtra("collectionName", catName);
            i.putExtra("collectionKey", catKey);
            startActivity(i);

        });

        collectionsList= findViewById(R.id.collectionsList);
        ref = FirebaseDatabase.getInstance().getReference("Items");
        storageReference = FirebaseStorage.getInstance().getReference("Images");
        user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        userId = user.getUid();
        arrayAdapter = new ArrayAdapter(getBaseContext(), R.layout.list_item, R.id.name, list);
        collectionsList.setAdapter(arrayAdapter);

        ref.orderByChild("userID").equalTo(userId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName)
            {
                Items value = snapshot.getValue(Items.class);
                if (value.getCategoryKey().equals(catKey) && value.categoryName.equals(catName) && value.getUserID().equals(userId))
                {
                    list.add(value.getItemName());
                    listOfKey.add(snapshot.getKey());

                    Collections.sort(list, new Comparator<String>()
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
                    Collections.sort(listOfKey, new Comparator<String>()
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


        collectionsList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {

                Intent i = new Intent(SelectedCollectionActivity.this, ItemDetails.class);
                i.putExtra("itemName", list.get(position));
                i.putExtra("collectionName", catName);
                i.putExtra("collectionKey", catKey);
                i.putExtra("itemKey", listOfKey.get(position));
                startActivity(i);

            }


        });
        collectionsList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {

                myDialog.setContentView(R.layout.update_delete_window);
                myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                myDialog.show();

                String name = list.get(position);
                String key = catKey;
                String collectionName = catName;
                String itemKey = listOfKey.get(position);

                Button btnUpdate,btnDetele;

                btnDetele= (Button) myDialog.findViewById(R.id.deteleBtn);
                btnUpdate = (Button) myDialog.findViewById(R.id.updateBtn);

                btnDetele.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ref = FirebaseDatabase.getInstance().getReference();;
                        user = FirebaseAuth.getInstance().getCurrentUser();
                        assert user != null;
                        userId = user.getUid();

                        ref.child("Items").child(itemKey).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                snapshot.getRef().removeValue();
                                myDialog.dismiss();

                                list.remove(position);
                                listOfKey.remove(position);
                                arrayAdapter.notifyDataSetChanged();

                                LayoutInflater inflater = getLayoutInflater();
                                View customToastLayout = inflater.inflate(R.layout.list_item2, (ViewGroup) findViewById(R.id.root_layout));
                                TextView textView6 = customToastLayout.findViewById(R.id.name);
                                textView6.setText("Item deleted.");

                                Toast mToast = new Toast(SelectedCollectionActivity.this);
                                mToast.setDuration(Toast.LENGTH_LONG);
                                mToast.setView(customToastLayout);
                                mToast.show();
                                //Toast.makeText(getApplicationContext(), "Deleted", Toast.LENGTH_SHORT).show();
                                myDialog.dismiss();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                                LayoutInflater inflater = getLayoutInflater();
                                View customToastLayout = inflater.inflate(R.layout.list_item2, (ViewGroup) findViewById(R.id.root_layout));
                                TextView textView6 = customToastLayout.findViewById(R.id.name);
                                textView6.setText("Operation failed.");

                                Toast mToast = new Toast(SelectedCollectionActivity.this);
                                mToast.setDuration(Toast.LENGTH_LONG);
                                mToast.setView(customToastLayout);
                                mToast.show();
                                //Toast.makeText(MyCollectionsActivity.this, "Operation failed.", Toast.LENGTH_LONG).show();
                                myDialog.dismiss();
                            }
                        });

                    }
                });
                return true;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                SelectedCollectionActivity.this.arrayAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                SelectedCollectionActivity.this.arrayAdapter.getFilter().filter(newText);
                return false;
            }
        });


        scanBarcode.setOnClickListener(view ->
        {
            myDialog.setContentView(R.layout.scan_barcode_window);
            myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            myDialog.show();
            Button btnClose;
            btnClose = (Button) myDialog.findViewById(R.id.closeBtn);
            btnClose.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    myDialog.dismiss();
                }
            });
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

    private void checkGoal(ArrayList<String> list) {
        int goal = Integer.parseInt(getIntent().getExtras().getString("collectionGoal"));
        if(goal == list.size()){

            int secondsDelayed = 1;

            new Handler().postDelayed(new Runnable()
            {
                public void run()
                {
                    myDialog.setContentView(R.layout.collection_goal_reached);
                    myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    myDialog.show();
                    new Handler().postDelayed(new Runnable()
                    {
                        public void run()
                        {
                            myDialog.dismiss();
                        }
                    }, secondsDelayed * 4500);
                }
            }, secondsDelayed * 1000);
        }
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
                            LayoutInflater inflater = getLayoutInflater();
                            View customToastLayout = inflater.inflate(R.layout.list_item2, (ViewGroup) findViewById(R.id.root_layout));
                            TextView textView6 = customToastLayout.findViewById(R.id.name);
                            textView6.setText("Image Uploaded.");

                            Toast mToast = new Toast(SelectedCollectionActivity.this);
                            mToast.setDuration(Toast.LENGTH_LONG);
                            mToast.setView(customToastLayout);
                            mToast.show();
                            //Toast.makeText(SelectedCollectionActivity.this, "Image Uploaded.", Toast.LENGTH_LONG).show();
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
                    LayoutInflater inflater = getLayoutInflater();
                    View customToastLayout = inflater.inflate(R.layout.list_item2, (ViewGroup) findViewById(R.id.root_layout));
                    TextView textView6 = customToastLayout.findViewById(R.id.name);
                    textView6.setText("Operation failed.");

                    Toast mToast = new Toast(SelectedCollectionActivity.this);
                    mToast.setDuration(Toast.LENGTH_LONG);
                    mToast.setView(customToastLayout);
                    mToast.show();
                    //Toast.makeText(SelectedCollectionActivity.this, "Operation failed.", Toast.LENGTH_LONG).show();
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
    private void TakeImage() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.opsc.collectebils.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, 111);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 2 && resultCode == RESULT_OK && data != null) {
            imgUri = data.getData();
            uploadImage();
        }
        if(requestCode == 111 && resultCode == RESULT_OK ) {
            File f = new File(currentPhotoPath);
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            imgUri = Uri.fromFile(f);
            mediaScanIntent.setData(imgUri);
            this.sendBroadcast(mediaScanIntent);

            uploadImage(f.getName(),imgUri);

        }
    }
    private void uploadImage(String name,Uri imgUri)
    {
        if(imgUri != null)
        {
            fileName = name;
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
                            LayoutInflater inflater = getLayoutInflater();
                            View customToastLayout = inflater.inflate(R.layout.list_item2, (ViewGroup) findViewById(R.id.root_layout));
                            TextView textView6 = customToastLayout.findViewById(R.id.name);
                            textView6.setText("Image Uploaded.");

                            Toast mToast = new Toast(SelectedCollectionActivity.this);
                            mToast.setDuration(Toast.LENGTH_LONG);
                            mToast.setView(customToastLayout);
                            mToast.show();
                            //Toast.makeText(SelectedCollectionActivity.this, "Image Uploaded.", Toast.LENGTH_LONG).show();
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
                    LayoutInflater inflater = getLayoutInflater();
                    View customToastLayout = inflater.inflate(R.layout.list_item2, (ViewGroup) findViewById(R.id.root_layout));
                    TextView textView6 = customToastLayout.findViewById(R.id.name);
                    textView6.setText("Operation failed.");

                    Toast mToast = new Toast(SelectedCollectionActivity.this);
                    mToast.setDuration(Toast.LENGTH_LONG);
                    mToast.setView(customToastLayout);
                    mToast.show();
                    //Toast.makeText(SelectedCollectionActivity.this, "Operation failed.", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
//        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
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
