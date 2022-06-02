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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener
{

    private EditText emailAddress, password;
    private Button loginButton, redirectButton;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null)
        {
            // User is signed in
            Intent i = new Intent(LoginActivity.this, MyCollectionsActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        }
        else
        {
            // User is signed out
            //Log.d(, "onAuthStateChanged:signed_out");
        }

        loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(this);

        redirectButton = (Button) findViewById(R.id.redirectButton);
        redirectButton.setOnClickListener(this);

        emailAddress = (EditText) findViewById(R.id.emailAddress);
        password = (EditText) findViewById(R.id.password);

    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.redirectButton:
                startActivity(new Intent(this, RegisterActivity.class));
                break;

            case R.id.loginButton:
                userLogin();
                break;
        }
    }

    private void userLogin()
    {
        String emailAddressEnter = emailAddress.getText().toString().trim();
        String passwordEnter = password.getText().toString().trim();

        if(emailAddressEnter.isEmpty()) {
            emailAddress.setError("All fields are required.");
            emailAddress.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(emailAddressEnter).matches()) {
            emailAddress.setError("Enter valid email address.");
            emailAddress.requestFocus();
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

        mAuth.signInWithEmailAndPassword(emailAddressEnter, passwordEnter).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {

                if(task.isSuccessful())
                {

                    startActivity(new Intent(LoginActivity.this, MyCollectionsActivity.class));
                    finish();
                }
                else
                {
                    Toast.makeText(LoginActivity.this, "Login failed.", Toast.LENGTH_LONG).show();
                }

            }
        });
    }
}