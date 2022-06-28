package com.opsc.collectebils;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
    // Establishes a Firebase connection variable
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initializes the Firebase connection variable
        mAuth = FirebaseAuth.getInstance();

        // Operation that checks if the user of the app is signed in or not
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        // If statement that keeps the logged in user logged in
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
        // Establishes a switch case to allow the function of 2 buttons
        switch (v.getId())
        {
            // Initializes a case the button that will link to the register page
            case R.id.redirectButton:
                startActivity(new Intent(this, RegisterActivity.class));
                break;

            // Initializes a case for the login button and its method
            case R.id.loginButton:
                userLogin();
                break;
        }
    }

    // The method that will create a user in Firebase
    private void userLogin()
    {
        String emailAddressEnter = emailAddress.getText().toString().trim();
        String passwordEnter = password.getText().toString().trim();

        // Line 90 - 112
        // A series of if statements that will validate the user's input
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

        // Logs in the user using the input specified
        mAuth.signInWithEmailAndPassword(emailAddressEnter, passwordEnter).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

            // Method that checks if the operation is successful
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {

                // If statement that stores user information to the Firebase realtime database
                // Only if previous operation is successful
                if(task.isSuccessful())
                {

                    startActivity(new Intent(LoginActivity.this, MyCollectionsActivity.class));
                    finish();
                }
                else
                {
                    LayoutInflater inflater = getLayoutInflater();
                    View customToastLayout = inflater.inflate(R.layout.list_item2, (ViewGroup) findViewById(R.id.root_layout));
                    TextView textView6 = customToastLayout.findViewById(R.id.name);
                    textView6.setText("Login failed.");

                    Toast mToast = new Toast(LoginActivity.this);
                    mToast.setDuration(Toast.LENGTH_SHORT);
                    mToast.setView(customToastLayout);
                    mToast.show();
                    //Toast.makeText(LoginActivity.this, "Login failed.", Toast.LENGTH_LONG).show();
                }

            }
        });
    }
}