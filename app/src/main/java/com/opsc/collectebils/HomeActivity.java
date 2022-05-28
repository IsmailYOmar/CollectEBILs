package com.opsc.collectebils;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class HomeActivity extends AppCompatActivity {
    Dialog myDialog;
    Button addCategory;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        myDialog = new Dialog(this);
        addCategory = findViewById(R.id.add_Category);

        addCategory.setOnClickListener(view -> {
            myDialog.setContentView(R.layout.create_a_category_window);
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


    }
}