package com.opsc.collectebils;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText email_address, password;
    private Button login_button, redirect_button;
    /*private FirebaseAuth mAuth;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login_button = (Button) findViewById(R.id.login_button);
        login_button.setOnClickListener(this);

        redirect_button = (Button) findViewById(R.id.redirect_button);
        redirect_button.setOnClickListener(this);

        email_address = (EditText) findViewById(R.id.email_address);
        password = (EditText) findViewById(R.id.password);

        /*mAuth = FirebaseAuth.getInstance();*/
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.redirect_button:
                startActivity(new Intent(this, RegisterActivity.class));
                break;

            case R.id.login_button:
                /*userLogin();*/
                startActivity(new Intent(LoginActivity.this, MyCollectionsActivity.class));
                break;
        }
    }

    private void userLogin() {
        String emailAddress = email_address.getText().toString().trim();
        String passwordEnter = password.getText().toString().trim();

        if(emailAddress.isEmpty()) {
            email_address.setError("All fields are required.");
            email_address.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()) {
            email_address.setError("Enter valid email address.");
            email_address.requestFocus();
            return;
        }

        if(passwordEnter.isEmpty()) {
            password.setError("All fields are required.");
            password.requestFocus();
            return;
        }

        if(passwordEnter.length()<8) {
            password.setError("Password cannot be less than 8 characters.");
            password.requestFocus();
            return;
        }

        /*mAuth.signInWithEmailAndPassword(emailAddress, passwordEnter).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()) {

                    startActivity(new Intent(LoginActivity.this, MyCollectionsActivity.class));
                }
                else {
                    Toast.makeText(LoginActivity.this, "Login failed.", Toast.LENGTH_LONG).show();
                }

            }
        });*/
    }
}