package com.opsc.collectebils;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Date;
import java.util.jar.Attributes;

public class ItemDetails extends AppCompatActivity {

    TextView collectionName;
    TextView itemName;
    TextView manufacturer;
    TextView productionYear;
    TextView purchasePrice;
    TextView purchaseDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);

        collectionName = findViewById(R.id.itemCollectionName);
        itemName = findViewById(R.id.itemName);
        manufacturer = findViewById(R.id.itemManufacturer);
        productionYear = findViewById(R.id.itemProductionYear);
        purchasePrice = findViewById(R.id.itemPurchasePrice);
        purchaseDate = findViewById(R.id.itemPurchaseDate);

        itemName.setText(getIntent().getExtras().getString("itemName"));
        collectionName.setText(getIntent().getExtras().getString("collectionName"));



    }
}