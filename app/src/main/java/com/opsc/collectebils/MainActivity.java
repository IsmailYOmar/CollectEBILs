package com.opsc.collectebils;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*ListView my_collections_list = findViewById(R.id.my_collections_list);

        List<String> list = new ArrayList<>();

        list.add("Action figures");
        list.add("Art");
        list.add("Board games");
        list.add("Books");
        list.add("Coins");
        list.add("Comic Books");
        list.add("Currency");
        list.add("Lego");

        ArrayAdapter arrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1,list);
        my_collections_list.setAdapter(arrayAdapter);
*/

        //Splash Screen duration
        int secondsDelayed = 1;
        new Handler().postDelayed(new Runnable() {
            public void run() {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
            }
        }, secondsDelayed * 1500);

    }
}
